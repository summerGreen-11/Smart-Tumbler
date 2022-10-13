package com.example.mybluetooth;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import static android.text.TextUtils.split;

import com.github.mikephil.charting.data.Entry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private HomeFrag homeFrag;
    private ChartFrag chartFrag;
    private AlarmFrag alarmFrag;
    private SetFrag setFrag;

    //DB 연동
    private SensorDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DB 처리
        dbHelper = new SensorDBHelper(this.getApplicationContext());

        //DB 섭취량 계산
        //CalIntakes();

        bottomNavigationView = findViewById(R.id.bottomNavi);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_chart:
                        setFrag(1);
                        break;
                    case R.id.action_alarm:
                        setFrag(2);
                        break;
                    case R.id.action_settings:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        homeFrag = new HomeFrag();
        chartFrag = new ChartFrag();
        alarmFrag = new AlarmFrag();
        setFrag = new SetFrag();

        setFrag(0); //show homeFrag

    }

    //replace fragments
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.main_frame,homeFrag);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame,chartFrag);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame,alarmFrag);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame,setFrag);
                ft.commit();
                break;
        }
    }

//    //Calculate Intakes
//    private void CalIntakes(){
//        SQLiteDatabase sql = dbHelper.getReadableDatabase();
//        Cursor cursor = sql.rawQuery("SELECT * FROM SensorData " +
//                "WHERE strftime(\"%Y/%m/%d\", dateTime) = \"2022/08/29\"", null);
//        int itk = 0;
//        while (cursor.moveToNext()) {
//
//            int wht = cursor.getInt(3);
//            itk = cursor.getInt(4);
//            Log.i("100", "data: " + Integer.toString(wht));
//        }
//        cursor.close();
//        sql.close();
//    }
}


