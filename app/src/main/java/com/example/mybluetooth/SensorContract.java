package com.example.mybluetooth;

import android.provider.BaseColumns;

public final class SensorContract {
    private SensorContract() {
    }

    public static class SensorEntry implements BaseColumns {
        public static final String TABLE_NAME = "SensorData";
        public static final String COLUMN_ID = "dtID";
        public static final String DATE_TIME = "dateTime";
        public static final String COLUMN_DRINK = "drinkDT";
        public static final String COLUMN_TEMP = "tempDT";
        public static final String COLUMN_INTAKES = "intakesDT";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        DATE_TIME + " TEXT DEFAULT (datetime('now','localtime')),"+
                        COLUMN_DRINK+ " TEXT," +
                        COLUMN_TEMP + " INTEGER," +
                        COLUMN_INTAKES + " INTEGER)";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
