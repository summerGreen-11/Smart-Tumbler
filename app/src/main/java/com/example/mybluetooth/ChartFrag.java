package com.example.mybluetooth;

//MPAndroidChart import
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.LineChart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ChartFrag extends Fragment {
    private View view;

    //MPAndroidChart
    private LineDataSet setComp1;
    //DB 연동
    private SensorDBHelper dbHelper;

    //UI
    private TextView bufferText;
    private Button DayBtn;
    private Button WeekBtn;
    private Button MonthBtn;

    //그래프를 그리기 위한 리스트들 (Y값, X값)
    ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
    ArrayList<String> xVals = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chartfrag,container,false);

        bufferText = (TextView) view.findViewById(R.id.buffertext);

        //DB 처리
        dbHelper = new SensorDBHelper(getActivity().getApplicationContext());

        //일별 차트 버튼
        DayBtn = (Button) view.findViewById(R.id.btn_chart_day);
        DayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //valsComp1는 데이터를 그래프에 추가하기 위한 리스트
                valsComp1.clear();
                xVals.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT strftime(\"%d\", today) AS date FROM SensorData GROUP BY date", null);
                String luxvalue = "DB" + "\r\n";

                while (cursor.moveToNext()) {

                    valsComp1.add(new Entry(Float.parseFloat(cursor.getString(1)), Integer.parseInt(cursor.getString(0))));
                    xVals.add(cursor.getString(0));
                    luxvalue += cursor.getString(0) + " " + cursor.getString(1) + "\r\n";
                }
                cursor.close();
                sql.close();

                setComp1 = new LineDataSet(valsComp1, "조도 값");

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();       //최종적으로 그래프에 추가할 dataSets 설정
                dataSets.add(setComp1);                                     //위에서 적용한 그래프 모양 + 데이터를 dataSets에 넣음

                LineData data = new LineData(dataSets);
                LineChart chart = (LineChart) view.findViewById(R.id.linechart);
                chart.setData(data);
                chart.getAxisRight().setDrawLabels(false);
                chart.invalidate();
            }
        });

        return view;
    }

}