package com.example.mybluetooth;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeAxisValueFormat extends IndexAxisValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long temp = ((long) value) * 1000;

        // Show time in local version
        Date timeMilliseconds = new Date(temp);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("mm:ss");
        return dateTimeFormat.format(timeMilliseconds);
    }
}
