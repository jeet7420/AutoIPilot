package com.whizhomespilot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
    public static String controllerId, controllerName, deviceId, deviceName,
            action, time, status, schedule_id;
    public static HashMap<String,String> controllerMap;
    public static HashMap<String,String> deviceMapForController;
    public static HashMap<String,String> schedularMapForSchedule;
    public static HashMap<String,HashMap<String,String>> deviceMap;
    public static LinkedHashMap<Integer,HashMap<String,String>> schedulerMap;
    public static int numberOfSchedules=0;

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
        long result = db.update(CONTROLLERS_TABLE, contentValues, CONTROLLERS_TABLE_COL1 + "=" +  "'" + controllerId + "'", null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateDeviceData(String deviceId, String controllerId, String deviceName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEVICES_COL3, deviceName);
        long result = db.update(DEVICES_TABLE, contentValues, DEVICES_COL1 + "=" + "'" + deviceId
                + "'" + " and " + DEVICES_COL2 + "=" + "'" + controllerId + "'", null);
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
        long result = db.update(SCHEDULES_TABLE, contentValues, SCHEDULES_COL2 + "=" + "'" + controllerId + "'"
                + " and " + SCHEDULES_COL3 + "=" + "'" + deviceId + "'", null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean deleteScheduleData(String controllerId, String deviceId){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(SCHEDULES_TABLE, SCHEDULES_COL2 + "=" + "'" + controllerId + "'"
                + " and " + SCHEDULES_COL3 + "=" + "'" + deviceId + "'", null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public HashMap<String,String> readControllerData(String username){
        controllerMap=new HashMap<String,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {CONTROLLERS_TABLE_COL1, CONTROLLERS_TABLE_COL2};
        Cursor c = db.query(CONTROLLERS_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                controllerId = c.getString(0);
                controllerName = c.getString(1);
                controllerMap.put(controllerId, controllerName);
            }while(c.moveToNext());
        }
        return controllerMap;
    }

    public HashMap<String,HashMap<String,String>> readDeviceData(String username){
        deviceMap=new HashMap<String,HashMap<String,String>>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {DEVICES_COL1, DEVICES_COL2, DEVICES_COL3};
        Cursor c = db.query(DEVICES_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                deviceId = c.getString(0);
                controllerId = c.getString(1);
                deviceName = c.getString(2);
                if(deviceMap.containsKey(controllerId)){
                    deviceMapForController=deviceMap.get(controllerId);
                    deviceMapForController.put(deviceId, deviceName);
                }
                else{
                    deviceMapForController=new HashMap<String, String>();
                    deviceMapForController.put(deviceId, deviceName);
                    deviceMap.put(controllerId, deviceMapForController);
                }
            }while(c.moveToNext());
        }
        return deviceMap;
    }

    public LinkedHashMap<Integer,HashMap<String,String>> readSchedularData(String username){
        schedulerMap=new LinkedHashMap<Integer,HashMap<String,String>>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {SCHEDULES_COL1, SCHEDULES_COL2, SCHEDULES_COL3, SCHEDULES_COL4, SCHEDULES_COL5, SCHEDULES_COL6};
        Cursor c = db.query(SCHEDULES_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                schedularMapForSchedule=new HashMap<String, String>();
                schedule_id = c.getString(0);
                controllerId = c.getString(1);
                deviceId = c.getString(2);
                action = c.getString(3);
                time = c.getString(4);
                status = c.getString(5);
                controllerName=StaticValues.controllerMap.get(controllerId);
                deviceName=StaticValues.deviceMap.get(controllerId).get(deviceId);
                schedularMapForSchedule.put("controllerName", controllerName);
                schedularMapForSchedule.put("deviceName", deviceName);
                schedularMapForSchedule.put("time", time);
                schedularMapForSchedule.put("status", action);
                schedulerMap.put(++numberOfSchedules, schedularMapForSchedule);
            }while(c.moveToNext());
        }
        return schedulerMap;
    }

    public void purgeControllerData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + CONTROLLERS_TABLE);
    }

    public void purgeDeviceData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + DEVICES_TABLE);
    }

    public void purgeSchedularData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + SCHEDULES_TABLE);
    }

    public void printControllerData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {CONTROLLERS_TABLE_COL1, CONTROLLERS_TABLE_COL2};
        Cursor c = db.query(CONTROLLERS_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("CONTROLLER DATA");
            do{
                controllerId = c.getString(0);
                controllerName = c.getString(1);
                System.out.println("Controller Id -> " + controllerId);
                System.out.println("Controller Name -> " + controllerName);
            }while(c.moveToNext());
        }
        else
            System.out.println("Controller Table Empty");
    }

    public void printDeviceData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {DEVICES_COL1, DEVICES_COL2, DEVICES_COL3};
        Cursor c = db.query(DEVICES_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("DEVICE DATA");
            do{
                deviceId = c.getString(0);
                controllerId = c.getString(1);
                deviceName = c.getString(2);
                System.out.println("Device Id -> " + deviceId);
                System.out.println("Controller Id -> " + controllerId);
                System.out.println("Device Name -> " + deviceName);
            }while(c.moveToNext());
        }
        else
            System.out.println("Device Table Empty");
    }

    public void printSchedularData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {SCHEDULES_COL1, SCHEDULES_COL2, SCHEDULES_COL3, SCHEDULES_COL4, SCHEDULES_COL5, SCHEDULES_COL6};
        Cursor c = db.query(SCHEDULES_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("SCHEDULAR DATA");
            do{
                schedularMapForSchedule=new HashMap<String, String>();
                schedule_id = c.getString(0);
                controllerId = c.getString(1);
                deviceId = c.getString(2);
                action = c.getString(3);
                time = c.getString(4);
                status = c.getString(5);
                System.out.println("Schedule Id -> " + schedule_id);
                System.out.println("Controller Id -> " + controllerId);
                System.out.println("Device Id -> " + deviceId);
                System.out.println("Action -> " + action);
                System.out.println("Time -> " + time);
                System.out.println("Status -> " + status);
            }while(c.moveToNext());
        }
        else
            System.out.println("Schedular Table Empty");
    }
}
