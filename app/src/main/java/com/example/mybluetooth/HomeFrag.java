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

import org.w3c.dom.Text;

public class HomeFrag extends Fragment {
    private View view;

    //DB 연동
    private SensorDBHelper dbHelper;
    private TextView testPrint;

    private TextView currentTemp;
    private ImageView waterImg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homefrag,container,false);

        //현재 온도
        currentTemp = (TextView) view.findViewById(R.id.current_temp);
        //섭취량 이미지
        waterImg = (ImageView) view.findViewById(R.id.water_homf_img);


        //테스트 데이터 출력 전 세팅
        testPrint = (TextView) view.findViewById(R.id.dataPrintTest);
        dbHelper = new SensorDBHelper(getActivity().getApplicationContext());
        //데이터 출력
        try {
            Cursor cursor = dbHelper.readRecord();

            while (cursor.moveToNext()) {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry._ID));
                int temp = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_TEMP));
                int intakes = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_INTAKES));
                String drinks = cursor.getString(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_DRINK));

                String result = " ID : " + Integer.toString(itemId) + "\n 온도: " +
                        Integer.toString(temp) + "도, 무게: " +
                        Integer.toString(intakes) + "g, 음료: " + drinks;
                testPrint.setText(result);

                //현재 온도 출력
                currentTemp.setText(temp);
            }
            cursor.close();
        }
        catch (Exception e){
            testPrint.setText("DATA EMPTY");
        }




        return view;
    }
}
