package com.example.mybluetooth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class SensorDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database";
    public static final int DATABASE_VERSION = 1;

    public SensorDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SensorContract.SensorEntry.SQL_CREATE_TABLE); // 테이블 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 단순히 데이터를 삭제하고 다시 시작하는 정책이 적용될 경우
        sqLiteDatabase.execSQL(SensorContract.SensorEntry.SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    void insertRecord(int temp, int weight, String colordt) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(SensorContract.SensorEntry.COLUMN_TEMP, temp);
        values.put(SensorContract.SensorEntry.COLUMN_WEIGHT, weight);
        values.put(SensorContract.SensorEntry.COLUMN_COLOR, colordt);

        db.insert(SensorContract.SensorEntry.TABLE_NAME, null, values);
    }

    public Cursor readRecord() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                SensorContract.SensorEntry.DATE_TIME,
                SensorContract.SensorEntry.COLUMN_TEMP,
                SensorContract.SensorEntry.COLUMN_WEIGHT,
                SensorContract.SensorEntry.COLUMN_COLOR
        };

        String sortOrder = BaseColumns._ID;

        Cursor cursor = db.query(
                SensorContract.SensorEntry.TABLE_NAME,   // The table to query
                projection,   // The array of columns to return (pass null to get all)
                null,   // where 문에 필요한 column
                null,   // where 문에 필요한 value
                null,   // group by를 적용할 column
                null,   // having 절
                sortOrder   // 정렬 방식
        );

        return cursor;
    }
}
