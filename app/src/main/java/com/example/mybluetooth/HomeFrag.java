package com.example.mybluetooth;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;

import org.w3c.dom.Text;

public class HomeFrag extends Fragment {
    private View view;

    //DB 연동
    private SensorDBHelper dbHelper;
    private TextView testPrint;

    private TextView currentTemp;
    private ImageView waterImg;
    private TextView waterPC;
    
    //요약 리포트 텍스트
    private TextView reportF;
    private TextView reportS;
    private TextView reportT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homefrag,container,false);

        //현재 온도
        currentTemp = (TextView) view.findViewById(R.id.current_temp);
        //섭취량 이미지
        waterImg = (ImageView) view.findViewById(R.id.water_homf_img);
        //현재 섭취량
        waterPC = (TextView) view.findViewById(R.id.waterPC);

        //DB 처리
        dbHelper = new SensorDBHelper(getActivity().getApplicationContext());

        //DB 데이터 저장
        String id="";
        String date="";
        String drinks="";
        String temp="";
        String intakes="";

        SQLiteDatabase sql = dbHelper.getReadableDatabase();
        Cursor cursor1 = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date('now')) " +
                "ORDER BY dtID LIMIT 1", null);

        while (cursor1.moveToNext()) {
            temp = Integer.toString(cursor1.getInt(3));
            intakes = Integer.toString(cursor1.getInt(4));
        }
        cursor1.close();
        sql.close();

        currentTemp.setText(temp);
        waterPC.setText(intakes);

        //일일섭취량 계산
        int Dintakes=0;
        Cursor cursor2 = sql.rawQuery("SELECT SUM(intakesDT) AS Dintake FROM SensorData " +
                "WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date('now'))", null);

        while (cursor2.moveToNext()) {
            Dintakes = cursor2.getInt(0);
        }
        cursor2.close();
        sql.close();

        //섭취량 이미지 변경
        if(Dintakes==0) waterImg.setImageResource(R.drawable.water0);
        else if(Dintakes<=1125) waterImg.setImageResource(R.drawable.bluecc);
        else if(Dintakes<1500) waterImg.setImageResource(R.drawable.water75);
        else waterImg.setImageResource(R.drawable.water100);
        
        //요약리포트
        reportF = (TextView) view.findViewById(R.id.report_less);
        reportS = (TextView) view.findViewById(R.id.report_const);
        reportT = (TextView) view.findViewById(R.id.report_more);
        if(Dintakes>=2500){ //초과
            reportT.setTextColor(getResources().getColor(R.color.colorPrimary));
            reportS.setTextColor(getResources().getColor(R.color.report_disable));
            reportF.setTextColor(getResources().getColor(R.color.report_disable));
        }
        else if(Dintakes>=1500){ //유지
            reportS.setTextColor(getResources().getColor(R.color.colorPrimary));
            reportT.setTextColor(getResources().getColor(R.color.report_disable));
            reportF.setTextColor(getResources().getColor(R.color.report_disable));
        }
        else{ //부족
            reportF.setTextColor(getResources().getColor(R.color.colorPrimary));
            reportT.setTextColor(getResources().getColor(R.color.report_disable));
            reportS.setTextColor(getResources().getColor(R.color.report_disable));
        }


        return view;
    }
}
