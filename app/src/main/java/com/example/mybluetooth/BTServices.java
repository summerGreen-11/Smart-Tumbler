package com.example.mybluetooth;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import android.app.FragmentManager;
import android.app.Activity;
import android.app.FragmentTransaction;

import static android.text.TextUtils.split;

public class BTServices extends Service {
    public BTServices() {
    }
    private BluetoothAdapter mBluetoothAdapter;
    //    public static final String B_DEVICE = "green";
    public static final String B_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    // 00000000-0000-1000-8000-00805f9b34fb

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private ConnectBtThread mConnectThread;
    private static ConnectedBtThread mConnectedThread;

    private static Handler mHandler = null;
    public static int mState = STATE_NONE;

    //DB 연동
    private SensorDBHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //mHandler = getApplication().getHandler();
        return mBinder;
    }
    public void toast(String mess){
        Toast.makeText(this,mess,Toast.LENGTH_SHORT).show();
    }
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BTServices getService() {
            return BTServices.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String deviceg = intent.getStringExtra("bluetooth_device");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        connectToDevice(deviceg);
        mHandler = new Handler();

        dbHelper = new SensorDBHelper(getApplicationContext());

        return START_STICKY;
    }

    private synchronized void connectToDevice(String macAddress){
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        if (mState == STATE_CONNECTING){
            if (mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectBtThread(device);
        toast("connecting");
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private void setState(int state){
        BTServices.mState = state;
        //mState = state;
        if (mHandler != null){
            // mHandler.obtainMessage();
        }
    }
    public synchronized void stop(){
        setState(STATE_NONE);
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }

        stopSelf();
    }

    public void sendData(String message){
        if (mConnectedThread!= null){
            mConnectedThread.write(message.getBytes());
            toast("sent data");
        }else {
            Toast.makeText(BTServices.this,"Failed to send data",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean stopService(Intent name) {
        setState(STATE_NONE);

        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mBluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

//    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
//        // Cancel the thread that completed the connection
//        if (mConnectThread != null) {
//            mConnectThread.cancel();
//            mConnectThread = null;
//        }
//
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//
//        mConnectedThread = new ConnectedBtThread(mmSocket);
//        mConnectedThread.start();
//
//        setState(STATE_CONNECTED);
//
//    }

    private class ConnectBtThread extends Thread{
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectBtThread(BluetoothDevice device){
            mDevice = device;
            BluetoothSocket socket = null;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(B_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = socket;

        }

        @Override
        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
                Log.d("service","connect thread run method (connected)");
                SharedPreferences pre = getSharedPreferences("BT_NAME",0);
                pre.edit().putString("bluetooth_connected",mDevice.getName()).apply();

            } catch (IOException e) {

                try {
                    mSocket.close();
                    Log.d("service","connect thread run method (close function)");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            //connected(mSocket,mDevice);
            mConnectedThread = new ConnectedBtThread(mSocket);
            mConnectedThread.start();
        }

        public void cancel(){

            try {
                mSocket.close();
                Log.d("service","connect thread cancel method");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedBtThread extends Thread{
        private final BluetoothSocket cSocket;
        private final InputStream inS;
        private final OutputStream outS;

        private byte[] buffer;

        public ConnectedBtThread(BluetoothSocket socket){
            cSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inS = tmpIn;
            outS = tmpOut;
        }

        @Override
        public void run() {
            buffer = new byte[1024];
            int mByte;
            while (true){
                try {
                    if (inS==null){
                        mState = STATE_NONE;
                        break;
                    } else {
                        SystemClock.sleep(5000);
                        mByte = inS.available(); // how many bytes are ready to be read?
                        mByte = inS.read(buffer, 0, mByte);
                        //mByte= inS.read(buffer);
                        String readMessage = new String(buffer, 0, mByte);
                        sendMessage(readMessage);

                        try {
                            String[] array = readMessage.split(",");
                            int temp = Integer.parseInt(array[0]);
                            int weight = Integer.parseInt((array[1]));
                            String colordt = array[2];
                            int btnstate = Integer.parseInt(array[3]);
                            Log.i("51", "data: " + Integer.toString(temp) + Integer.toString(weight) + colordt);
                            dbHelper.insertRecord(temp, weight, colordt,btnstate);
                        } catch (NumberFormatException e) {
                        // NumberFormatException 이 발생한 경우 처리 방법
                            String[] array = readMessage.split(",");
                            int temp = 0;
                            int weight = 0;
                            String colordt = "water";
                            int btnstate = 0;
                            dbHelper.insertRecord(temp, weight, colordt, btnstate);
                            Log.i("50", "data: " + Integer.toString(temp) + Integer.toString(weight) + colordt);
                    } catch (Exception e) {
                        // Exception 이 발생한 경우 처리 방법
                    }
                        //mHandler.obtainMessage(BluetoothSetting.MESSAGE_READ, mByte, -1, readMessage).sendToTarget();
                        Log.i("7", "message:" + readMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            Log.d("service","connected thread run method");
        }

        public void write(byte[] buff){
            try {
                outS.write(buff);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cancel(){
            try {
                cSocket.close();
                Log.d("service","connected thread cancel method");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String mess){
        Log.d("messageService", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        intent.putExtra("message", mess);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        this.stop();
        super.onDestroy();
    }
}