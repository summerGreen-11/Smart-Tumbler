package com.example.mybluetooth;

//MPAndroidChart import
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.LineChart;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ChartFrag extends Fragment {
    private View view;

    private LineChart lineChart;
    ArrayList<Entry> entry_chart = new ArrayList<>();

    private Handler mHandler;
    private List<Float> temp_values = new ArrayList<>();

    private TextView bufferText;

    public final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chartfrag,container,false);

        lineChart = (LineChart) view.findViewById(R.id.linechart);//layout의 id
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.LTGRAY);

        bufferText = (TextView) view.findViewById(R.id.buffertext);

        //temp_values.add(0f);

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    temp_values.add(Float.parseFloat(readMessage));
                    bufferText.setText(readMessage);
                }
            }
        };

        LineData chartData = new LineData();
        if(temp_values.size() > 0){
            for (int i = 0; i < temp_values.size(); i++) {
                entry_chart.add(new Entry(i, temp_values.get(i)));
            }
        }
        else{
            for (int i = 0; i < 10; i++) {
                float val = (float) (Math.random() * 10);
                entry_chart.add(new Entry(i, val));
            }
        }

        LineDataSet lineDataSet = new LineDataSet(entry_chart, "temp");
        chartData.addDataSet(lineDataSet);

        // lineDataSet.setColor(Color.BLACK);
        //lineDataSet.setCircleColor(Color.BLACK);

        lineDataSet.setFillAlpha(65);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setValueTextColor(Color.WHITE);

        lineChart.setData(chartData);

        lineChart.invalidate();

        return view;
    }
}