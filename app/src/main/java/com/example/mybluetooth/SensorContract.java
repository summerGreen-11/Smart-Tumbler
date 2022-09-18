package com.example.mybluetooth;

import android.provider.BaseColumns;

public final class SensorContract {
    private SensorContract() {
    }

    public static class SensorEntry implements BaseColumns {
        public static final String TABLE_NAME = "SensorData";
        //public static final String COLUMN_ID = "dtID";
        public static final String COLUMN_TEMP = "tempDT";
        public static final String COLUMN_WEIGHT = "weightDT";
        public static final String COLUMN_COLOR = "colorDT";
        public static final String DATE_TIME = "dateTime";
        public static final String BTN_STATE = "btnState";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        DATE_TIME + " TEXT DEFAULT (datetime('now','localtime')),"+
                        COLUMN_TEMP + " INTEGER," +
                        COLUMN_WEIGHT + " INTEGER," +
                        BTN_STATE + " INTEGER," +
                        COLUMN_COLOR + " TEXT)";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
