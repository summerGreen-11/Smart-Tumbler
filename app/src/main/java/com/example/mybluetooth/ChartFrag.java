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

    //MPAndroidChart
    private LineDataSet setComp1;
    //DB 연동
    private SensorDBHelper dbHelper;

    //UI
    private TextView bufferText;
    private Button DayBtn;
    private Button WeekBtn;
    private Button MonthBtn;

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
        LineChart chart = (LineChart) view.findViewById(R.id.linechart);

        //일별 차트 버튼
        DayBtn = (Button) view.findViewById(R.id.btn_chart_day);
        DayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = \"2022/08/29\"", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String str = cursor.getString(1);
                    String dt = Integer.toString(cursor.getInt(2));
                    String wht = Integer.toString(cursor.getInt(3));
                    String date = str.substring(11,16);
                    Log.i("100", "data: " + str);

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(dt)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(wht)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

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

                //무게 데이터
                LineDataSet lineDataSet2 = new LineDataSet(entries2, "weight");
                lineDataSet2.setLineWidth(3);
                lineDataSet2.setCircleRadius(4);
                lineDataSet2.setCircleColor(Color.parseColor("#000000"));
                lineDataSet2.setColor(Color.parseColor("#000000"));
                lineDataSet2.setDrawCircleHole(true);
                lineDataSet2.setDrawCircles(true);
                lineDataSet2.setDrawHorizontalHighlightIndicator(false);
                lineDataSet2.setDrawHighlightIndicators(false);
                lineDataSet2.setDrawValues(false);

                //데이터셋에 데이터 추가
                LineData lineData = new LineData(lineDataSet, lineDataSet2);
                chart.setData(lineData);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.BLACK);
                xAxis.enableGridDashedLine(8, 24, 0);
                //추가
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
                //xAxis.setLabelCount(10, true);

                YAxis yLAxis = chart.getAxisLeft();
                yLAxis.setTextColor(Color.BLACK);

                YAxis yRAxis = chart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(false);
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
        });

        //주간 차트 버튼
        WeekBtn = (Button) view.findViewById(R.id.btn_chart_week);
        WeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //배열 초기화
                entries1.clear();
                entries2.clear();

                SQLiteDatabase sql = dbHelper.getReadableDatabase();
                Cursor cursor = sql.rawQuery("SELECT strftime(\"%Y/%w\",dateTime)as date_time, tempDT, weightDT, colorDT  " +
                        "FROM SensorData where time BETWEEN datetime(\"now\", \"-90days\") AND datetime(\"now\", \"localtime\") GROUP BY date_time", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String str = cursor.getString(1);
                    String dt = Integer.toString(cursor.getInt(2));
                    String wht = Integer.toString(cursor.getInt(3));
                    String date = str.substring(11,16);

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(dt)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(wht)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

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

                //무게 데이터
                LineDataSet lineDataSet2 = new LineDataSet(entries2, "weight");
                lineDataSet2.setLineWidth(3);
                lineDataSet2.setCircleRadius(4);
                lineDataSet2.setCircleColor(Color.parseColor("#000000"));
                lineDataSet2.setColor(Color.parseColor("#000000"));
                lineDataSet2.setDrawCircleHole(true);
                lineDataSet2.setDrawCircles(true);
                lineDataSet2.setDrawHorizontalHighlightIndicator(false);
                lineDataSet2.setDrawHighlightIndicators(false);
                lineDataSet2.setDrawValues(false);

                //데이터셋에 데이터 추가
                LineData lineData = new LineData(lineDataSet, lineDataSet2);
                chart.setData(lineData);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.BLACK);
                xAxis.enableGridDashedLine(8, 24, 0);
                //추가
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
                //xAxis.setLabelCount(10, true);

                YAxis yLAxis = chart.getAxisLeft();
                yLAxis.setTextColor(Color.BLACK);

                YAxis yRAxis = chart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(false);
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
                Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m\", dateTime) = \"2022/08\"", null);
                int CheckNumberData = 0;

                while (cursor.moveToNext()) {
                    String id =Integer.toString(cursor.getInt(0));
                    String str = cursor.getString(1);
                    String dt = Integer.toString(cursor.getInt(2));
                    String wht = Integer.toString(cursor.getInt(3));
                    String date = str.substring(11,16);

                    entries1.add(new Entry(CheckNumberData, Float.parseFloat(dt)));
                    entries2.add(new Entry(CheckNumberData, Float.parseFloat(wht)));
                    xVals.add(date);
                    CheckNumberData++;
                }
                cursor.close();
                sql.close();

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

                //무게 데이터
                LineDataSet lineDataSet2 = new LineDataSet(entries2, "weight");
                lineDataSet2.setLineWidth(3);
                lineDataSet2.setCircleRadius(4);
                lineDataSet2.setCircleColor(Color.parseColor("#000000"));
                lineDataSet2.setColor(Color.parseColor("#000000"));
                lineDataSet2.setDrawCircleHole(true);
                lineDataSet2.setDrawCircles(true);
                lineDataSet2.setDrawHorizontalHighlightIndicator(false);
                lineDataSet2.setDrawHighlightIndicators(false);
                lineDataSet2.setDrawValues(false);

                //데이터셋에 데이터 추가
                LineData lineData = new LineData(lineDataSet, lineDataSet2);
                chart.setData(lineData);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.BLACK);
                xAxis.enableGridDashedLine(8, 24, 0);
                //추가
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
                //xAxis.setLabelCount(10, true);

                YAxis yLAxis = chart.getAxisLeft();
                yLAxis.setTextColor(Color.BLACK);

                YAxis yRAxis = chart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(false);
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
        });


        return view;
    }

}