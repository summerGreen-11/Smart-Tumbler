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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

    //DB 연동
    private SensorDBHelper dbHelper;

    //Chart Button
    private Button DayBtn;
    private Button WeekBtn;
    private Button MonthBtn;
    //Drink Button
    private Button WaBtn;
    private Button CoBtn;
    private Button LaBtn;
    private Button MiBtn;

    LineChart chart;

    //그래프를 그리기 위한 리스트
    List<Entry> entries1 = new ArrayList<>();
    List<Entry> entries2 = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<String>(); // X 축 이름 값

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chartfrag,container,false);

        //DB 처리
        dbHelper = new SensorDBHelper(getActivity().getApplicationContext());

        //차트
        chart = (LineChart) view.findViewById(R.id.linechart);

        //일별 차트 버튼
        DayBtn = (Button) view.findViewById(R.id.btn_chart_day);
        DayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date(\"now\"))", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String date = cursor.getString(1).substring(14,19);
                    String drinks = cursor.getString(2);
                    String temp = Integer.toString(cursor.getInt(3));
                    String intakes = Integer.toString(cursor.getInt(4));

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(temp)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(intakes)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

                setChartData(); //차트 데이터셋 구성
            }
        });

        //주간 차트 버튼
        WeekBtn = (Button) view.findViewById(R.id.btn_chart_week);
        WeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //월간 차트 버튼
        MonthBtn = (Button) view.findViewById(R.id.btn_chart_month);
        MonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m\", dateTime) = strftime(\"%Y/%m\", date('now'))", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String date = cursor.getString(1).substring(11,16);
                    String drinks = cursor.getString(2);
                    String temp = Integer.toString(cursor.getInt(3));
                    String intakes = Integer.toString(cursor.getInt(4));

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(temp)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(intakes)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

                setChartData(); //차트 데이터셋 구성
            }
        });

        //음료 버튼 제어
        WaBtn = view.findViewById(R.id.btn_chart_water);
        WaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = \"2022/09/26\" AND drinkDT == \"water\"", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String date = cursor.getString(1).substring(11,16);
                    String drinks = cursor.getString(2);
                    String temp = Integer.toString(cursor.getInt(3));
                    String intakes = Integer.toString(cursor.getInt(4));

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(temp)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(intakes)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

                setChartData(); //차트 데이터셋 구성
            }
        });

        CoBtn = view.findViewById(R.id.btn_chart_coffee);
        CoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date('now')) AND drinkDT == \"americano\"", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String date = cursor.getString(1).substring(11,16);
                    String drinks = cursor.getString(2);
                    String temp = Integer.toString(cursor.getInt(3));
                    String intakes = Integer.toString(cursor.getInt(4));

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(temp)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(intakes)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

                setChartData(); //차트 데이터셋 구성
            }
        });

        LaBtn = view.findViewById(R.id.btn_chart_latte);
        LaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date('now')) AND drinkDT == \"latte\"", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String date = cursor.getString(1).substring(11,16);
                    String drinks = cursor.getString(2);
                    String temp = Integer.toString(cursor.getInt(3));
                    String intakes = Integer.toString(cursor.getInt(4));

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(temp)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(intakes)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

                setChartData(); //차트 데이터셋 구성
            }
        });

        MiBtn = view.findViewById(R.id.btn_chart_milk);
        MiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date('now')) AND drinkDT == \"milk\"", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String date = cursor.getString(1).substring(11,16);
                    String drinks = cursor.getString(2);
                    String temp = Integer.toString(cursor.getInt(3));
                    String intakes = Integer.toString(cursor.getInt(4));

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(temp)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(intakes)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

                setChartData(); //차트 데이터셋 구성
            }
        });

        return view;
    }

    public void setChartData(){
        //온도 데이터
        LineDataSet lineDataSet = new LineDataSet(entries1, "temp");
        lineDataSet.setLineWidth(3);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextSize(8f);

        //섭취량 데이터
        LineDataSet lineDataSet2 = new LineDataSet(entries2, "intakes");
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setCircleRadius(4);
        lineDataSet2.setCircleColor(Color.parseColor("#000000"));
        lineDataSet2.setColor(Color.parseColor("#000000"));
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setDrawValues(true);
        lineDataSet2.setValueTextSize(8f);

        //데이터셋에 데이터 추가
        LineData lineData = new LineData(lineDataSet, lineDataSet2);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);
        //추가
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);
        yLAxis.setAxisLineWidth(1f);
        yLAxis.setAxisLineColor(Color.BLACK);

        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setAxisLineColor(R.color.chart_temp);
        yRAxis.setAxisLineWidth(1f);
        yRAxis.setAxisMinimum(0f);
        yRAxis.setAxisMaximum(100f);
        yRAxis.setGranularity(10f);
        yRAxis.setDrawLabels(true);
        yRAxis.setDrawAxisLine(true);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        chart.setVisibleXRangeMaximum(6);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDescription(description);
        chart.invalidate();
    }

}