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

    //Chart
    private LineChart lineChart;
    private Thread thread;

    //DB 연동
    private SensorDBHelper dbHelper;

    //UI
    private TextView bufferText;
    private Button DayBtn;
    private Button WeekBtn;
    private Button MonthBtn;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chartfrag,container,false);

        bufferText = (TextView) view.findViewById(R.id.buffertext);

        //DB 처리
        dbHelper = new SensorDBHelper(getActivity().getApplicationContext());

        lineChart = (LineChart) view.findViewById(R.id.linechart);//layout의 id
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.animateXY(5000, 5000);
        lineChart.invalidate();

        LineData data = new LineData();
        lineChart.setData(data);

        feedMultiple();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null)
            thread.interrupt();
    }

    private void addEntry() {
        LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            SQLiteDatabase sql = dbHelper.getReadableDatabase();
            Cursor cursor = sql.rawQuery("SELECT * FROM SensorData WHERE strftime(\"%Y/%m/%d\", dateTime) = strftime(\"%Y/%m/%d\", date('now'))", null);
            int CheckNumberData = 0;

            while (cursor.moveToNext()) {
                String id =Integer.toString(cursor.getInt(0));
                String dt = Integer.toString(cursor.getInt(2));
                //bufferText.setText(dt);

                data.addEntry(new Entry(set.getEntryCount(),Float.parseFloat(dt)), 0);
                CheckNumberData++;
            }
            cursor.close();
            sql.close();

            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            //data.addEntry(new Entry(set.getEntryCount(),temp_values.get(temp_values.size()-1)), 0);
            //data.addEntry(new Entry(set.getEntryCount(),temp_datas.get(temp_datas.size()-1)), 0);

            data.notifyDataChanged();
//            XAxis xAxis = lineChart.getXAxis(); // x 축 설정
//            xAxis.setValueFormatter(new TimeAxisValueFormat());

            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(5);
            lineChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setFillAlpha(90);
        set.setFillColor(Color.parseColor("#673AB7"));
        set.setColor(Color.parseColor("#673AB7"));
        set.setCircleColor(Color.parseColor("#673AB7"));
        set.setValueTextColor(Color.WHITE);
        set.setDrawValues(false);
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setDrawCircles(true);
        set.setValueTextSize(9f);
        set.setDrawFilled(false);
        set.setForm(Legend.LegendForm.NONE);

        XAxis x = lineChart.getXAxis();
        //x.setTextColor(Color.WHITE);
        x.setLabelCount(5);

        YAxis y = lineChart.getAxisLeft();
        //y.setTextColor(Color.WHITE);
        y.setAxisMinimum(0f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setHighLightColor(Color.rgb(244, 117, 117));

        return set;
    }

    private void feedMultiple() {
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    getActivity().runOnUiThread(runnable);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

}