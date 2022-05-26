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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ChartFrag extends Fragment {
    private View view;

    private LineChart lineChart;
    ArrayList<Entry> entry_chart = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chartfrag,container,false);

        Bundle bundle = getArguments();
        String[] temparray = bundle.getStringArray("array");

        lineChart = (LineChart) view.findViewById(R.id.linechart);//layout의 id
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.LTGRAY);

        LineData chartData = new LineData();

        for (int i = 0; i < temparray.length; i++) {

            float val = Float.parseFloat(temparray[i]);
            entry_chart.add(new Entry(i, val));
        }

        LineDataSet lineDataSet = new LineDataSet(entry_chart, "graph1");
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