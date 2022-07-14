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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.LineChart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ChartFrag extends Fragment {
    private View view;

    private Handler mHandler;
    private Activity myActivity;

    //Chart
    private LineChart lineChart;
    private List<Float> temp_values = new ArrayList<>();

    private Thread thread;

    private TextView bufferText;

    public final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //DB 연동
    private SensorDBHelper dbHelper;
    private List<Integer> temp_datas = new ArrayList<>(); //온도 데이터
    private List<Integer> weight_datas = new ArrayList<>(); //무게 데이터
    private List<String> color_datas = new ArrayList<>(); //색상 데이터

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chartfrag,container,false);

        myActivity = getActivity();

        //DB 처리
        dbHelper = new SensorDBHelper(myActivity.getApplicationContext());
        getDBdata();

        bufferText = (TextView) view.findViewById(R.id.buffertext);

        temp_values.add(0f);

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

    private void getDBdata() {
        Cursor cursor = dbHelper.readRecord();

        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry._ID));
            int temp = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_TEMP));
            int weight = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_WEIGHT));
            String colordt = cursor.getString(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_COLOR));

            String result = colordt;
            bufferText.setText(result);
            //ArrayList에 값 저장
            temp_datas.add(temp);
            weight_datas.add(weight);
            color_datas.add(colordt);
        }

        cursor.close();
    }

    private BroadcastReceiver mMessageReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            //bufferText.setText(message);
            temp_values.add(Float.parseFloat(message));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null)
            thread.interrupt();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private void addEntry() {
        LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.addEntry(new Entry(set.getEntryCount(),temp_values.get(temp_values.size()-1)), 0);
            //data.addEntry(new Entry(set.getEntryCount(),temp_datas.get(temp_datas.size()-1)), 0);
            data.notifyDataChanged();
            XAxis xAxis = lineChart.getXAxis(); // x 축 설정
            xAxis.setValueFormatter(new TimeAxisValueFormat());

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
                    myActivity.runOnUiThread(runnable);
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