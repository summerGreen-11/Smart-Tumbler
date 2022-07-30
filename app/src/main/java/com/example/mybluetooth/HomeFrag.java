package com.example.mybluetooth;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFrag extends Fragment {
    private View view;

    //DB 연동
    private SensorDBHelper dbHelper;
    private TextView testPrint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.homefrag,container,false);

        //테스트 데이터 출력
        testPrint = (TextView) view.findViewById(R.id.dataPrintTest);

        dbHelper = new SensorDBHelper(getActivity().getApplicationContext());

        try {
            getDBdata();
        }
        catch(Exception e){
            testPrint.setText("DB NULL");
        }

        return view;
    }

    private void getDBdata() {
        try {
            Cursor cursor = dbHelper.readRecord();

            while (cursor.moveToNext()) {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry._ID));
                int temp = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_TEMP));
                int weight = cursor.getInt(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_WEIGHT));
                String colordt = cursor.getString(cursor.getColumnIndexOrThrow(SensorContract.SensorEntry.COLUMN_COLOR));

                String result = " ID : " + Integer.toString(itemId) + "\n 온도: " +
                        Integer.toString(temp) + "도, 무게: " +
                        Integer.toString(weight) + "g, 음료: " + colordt;
                testPrint.setText(result);
            }
                cursor.close();
        }
        catch (Exception e){
            testPrint.setText("DATA EMPTY");
        }
    }
}
