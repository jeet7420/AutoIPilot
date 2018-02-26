package com.whizhomespilot;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by smarhas on 2/25/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AutoI.db";
    public static final String CONTROLLERS_TABLE = "AIC_CONTROLLERS_L";
    public static final String DEVICES_TABLE = "AIC_DEVICES_L";
    public static final String SCHEDULES_TABLE = "AIC_SCHEDULES_L";
    public static final String CONTROLLERS_TABLE_COL1 = "CONTROLLER_ID";
    public static final String CONTROLLERS_TABLE_COL2 = "CONTROLLER_NAME";
    public static final String DEVICES_COL1 = "DEVICE_ID";
    public static final String DEVICES_COL2 = "CONTROLLER_ID";
    public static final String DEVICES_COL3 = "DEVICE_NAME";
    public static final String SCHEDULES_COL1 = "SCHEDULE_ID";
    public static final String SCHEDULES_COL2 = "CONTROLLER_ID";
    public static final String SCHEDULES_COL3 = "DEVICE_ID";
    public static final String SCHEDULES_COL4 = "ACTION";
    public static final String SCHEDULES_COL5 = "TIME";
    public static final String SCHEDULES_COL6 = "STATUS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + CONTROLLERS_TABLE + " (" + CONTROLLERS_TABLE_COL1 + " TEXT," + CONTROLLERS_TABLE_COL2 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DEVICES_TABLE + " (" + DEVICES_COL1 + " TEXT," + DEVICES_COL2 + " TEXT," + DEVICES_COL3 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SCHEDULES_TABLE + " (" + SCHEDULES_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + SCHEDULES_COL2 + " TEXT,"
                + SCHEDULES_COL3 + " TEXT," + SCHEDULES_COL4 + " TEXT," + SCHEDULES_COL5 + " TEXT," + SCHEDULES_COL6 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean insertControllerData(String controllerId, String controllerName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTROLLERS_TABLE_COL1, controllerId);
        contentValues.put(CONTROLLERS_TABLE_COL2, controllerName);
        long result = db.insert(CONTROLLERS_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDeviceData(String deviceId, String controllerId, String deviceName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEVICES_COL1, deviceId);
        contentValues.put(DEVICES_COL2, controllerId);
        contentValues.put(DEVICES_COL3, deviceName);
        long result = db.insert(DEVICES_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertScheduleData(String controllerId, String deviceId, String action, String time, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCHEDULES_COL2, controllerId);
        contentValues.put(SCHEDULES_COL3, deviceId);
        contentValues.put(SCHEDULES_COL4, action);
        contentValues.put(SCHEDULES_COL5, time);
        contentValues.put(SCHEDULES_COL6, status);
        long result = db.insert(SCHEDULES_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateControllerData(String controllerId, String controllerName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTROLLERS_TABLE_COL2, controllerName);
        long result = db.update(CONTROLLERS_TABLE, contentValues, CONTROLLERS_TABLE_COL1 + "=" + controllerId, null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateDeviceData(String deviceId, String controllerId, String deviceName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEVICES_COL3, deviceName);
        long result = db.update(CONTROLLERS_TABLE, contentValues, CONTROLLERS_TABLE_COL1 + "=" + deviceId
                + " and " + CONTROLLERS_TABLE_COL2 + "=" + controllerId, null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateScheduleData(String controllerId, String deviceId, String action, String time, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCHEDULES_COL4, action);
        contentValues.put(SCHEDULES_COL5, time);
        contentValues.put(SCHEDULES_COL6, status);
        long result = db.update(SCHEDULES_TABLE, contentValues, SCHEDULES_COL2 + "=" + controllerId
                + " and " + SCHEDULES_COL3 + "=" + deviceId, null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean deleteScheduleData(String controllerId, String deviceId, String action, String time, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(SCHEDULES_TABLE, SCHEDULES_COL2 + "=" + controllerId
                + " and " + SCHEDULES_COL3 + "=" + deviceId, null);
        if(result == -1)
            return false;
        else
            return true;
    }
}
