package com.whizhomespilot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by smarhas on 2/25/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AutoI.db";

    public static final String USER_PROFILE_TABLE = "AIC_USER_PROFILE_L";
    public static final String CONTROLLERS_TABLE = "AIC_CONTROLLERS_L";
    public static final String DEVICES_TABLE = "AIC_DEVICES_L";
    public static final String SCHEDULES_TABLE = "AIC_SCHEDULES_L";
    public static final String STATUS_TABLE = "AIC_STATUS_L";
    public static final String SECURITY_TABLE = "AIC_SECURITY_L";
    public static final String TOPIC_TABLE = "AIC_TOPIC_L";

    public static final String USER_PROFILE_TABLE_COL1 = "ID";
    public static final String USER_PROFILE_TABLE_COL2 = "KEY";
    public static final String USER_PROFILE_TABLE_COL3 = "VALUE";

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

    public static final String STATUS_TABLE_COL1 = "DEVICE_ID";
    public static final String STATUS_TABLE_COL2 = "STATUS";

    public static final String SECURITY_TABLE_COL1 = "CONTROLLER_ID";
    public static final String SECURITY_TABLE_COL2 = "SECURTY_TOKEN";

    public static final String TOPIC_TABLE_COL1 = "CONTROLLER_ID";
    public static final String TOPIC_TABLE_COL2 = "TOPIC";

    public static String controllerId, controllerName, deviceId, deviceName,
            action, time, schedule_id, status, securityToken, topic, userProfileKey, userProfileValue;

    public static HashMap<String,String> userProfileMap;
    public static HashMap<String,String> controllerMap;
    public static HashMap<String,String> statusMap;
    public static HashMap<String,String> securityMap;
    public static HashMap<String,String> topicMap;
    public static HashMap<String,String> deviceMapForController;
    public static HashMap<String,String> schedularMapForSchedule;
    public static HashMap<String,HashMap<String,String>> deviceMap;
    public static LinkedHashSet<Schedule> schedulerSet;

    public static Schedule schedule;

    public static int numberOfSchedules=0;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_PROFILE_TABLE + " (" + USER_PROFILE_TABLE_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_PROFILE_TABLE_COL2 + " TEXT," + USER_PROFILE_TABLE_COL3 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + CONTROLLERS_TABLE + " (" + CONTROLLERS_TABLE_COL1 + " TEXT," + CONTROLLERS_TABLE_COL2 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DEVICES_TABLE + " (" + DEVICES_COL1 + " TEXT," + DEVICES_COL2 + " TEXT," + DEVICES_COL3 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SCHEDULES_TABLE + " (" + SCHEDULES_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + SCHEDULES_COL2 + " TEXT,"
                + SCHEDULES_COL3 + " TEXT," + SCHEDULES_COL4 + " TEXT," + SCHEDULES_COL5 + " TEXT," + SCHEDULES_COL6 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + STATUS_TABLE + " (" + STATUS_TABLE_COL1 + " TEXT," + STATUS_TABLE_COL2 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SECURITY_TABLE + " (" + SECURITY_TABLE_COL1 + " TEXT," + SECURITY_TABLE_COL2 + " SECURITY_TABLE_COL1)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TOPIC_TABLE + " (" + TOPIC_TABLE_COL1 + " TEXT," + TOPIC_TABLE_COL2 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean insertUserProfileData(String key, String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_PROFILE_TABLE_COL2, key);
        contentValues.put(USER_PROFILE_TABLE_COL3, value);
        long result = db.insert(USER_PROFILE_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
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

    public boolean insertStatusData(String deviceId, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS_TABLE_COL1, deviceId);
        contentValues.put(STATUS_TABLE_COL2, status);
        long result = db.insert(STATUS_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertSecurityData(String controllerId, String securityToken){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SECURITY_TABLE_COL1, controllerId);
        contentValues.put(SECURITY_TABLE_COL2, securityToken);
        long result = db.insert(SECURITY_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertTopicData(String controllerId, String topic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOPIC_TABLE_COL1, controllerId);
        contentValues.put(TOPIC_TABLE_COL2, topic);
        long result = db.insert(TOPIC_TABLE, null, contentValues);
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

    public boolean updateStatusData(String deviceId, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS_TABLE_COL2, status);
        long result = db.update(STATUS_TABLE, contentValues, STATUS_TABLE_COL1 + "=" +  "'" + deviceId + "'", null);
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

    public HashMap<String,String> readUserProfileData(String username){
        userProfileMap=new HashMap<String,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {USER_PROFILE_TABLE_COL2, USER_PROFILE_TABLE_COL3};
        Cursor c = db.query(USER_PROFILE_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                userProfileKey = c.getString(0);
                userProfileValue = c.getString(1);
                userProfileMap.put(userProfileKey, userProfileValue);
            }while(c.moveToNext());
        }
        return userProfileMap;
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

    public LinkedHashSet<Schedule> readSchedularData(String username){
        schedulerSet=new LinkedHashSet<Schedule>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {SCHEDULES_COL1, SCHEDULES_COL2, SCHEDULES_COL3, SCHEDULES_COL4, SCHEDULES_COL5, SCHEDULES_COL6};
        Cursor c = db.query(SCHEDULES_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                schedule=new Schedule();

                schedule_id = c.getString(0);
                controllerId = c.getString(1);
                deviceId = c.getString(2);
                action = c.getString(3);
                time = c.getString(4);
                status = c.getString(5);

                controllerName=StaticValues.controllerMap.get(controllerId);
                deviceName=StaticValues.deviceMap.get(controllerId).get(deviceId);

                schedule.setAction(action);
                schedule.setControllerName(controllerName);
                schedule.setDeviceName(deviceName);
                schedule.setTime(time);

                schedulerSet.add(schedule);
            }while(c.moveToNext());
        }
        return schedulerSet;
    }

    public HashMap<String,String> readStatusData(String username){
        statusMap=new HashMap<String,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {STATUS_TABLE_COL1, STATUS_TABLE_COL2};
        Cursor c = db.query(STATUS_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                deviceId = c.getString(0);
                status = c.getString(1);
                statusMap.put(deviceId, status);
            }while(c.moveToNext());
        }
        return statusMap;
    }

    public HashMap<String,String> readSecurityData(String username){
        securityMap=new HashMap<String,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {SECURITY_TABLE_COL1, SECURITY_TABLE_COL2};
        Cursor c = db.query(SECURITY_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                controllerId = c.getString(0);
                securityToken = c.getString(1);
                securityMap.put(controllerId, securityToken);
            }while(c.moveToNext());
        }
        return securityMap;
    }

    public HashMap<String,String> readTopicData(String username){
        topicMap=new HashMap<String,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {TOPIC_TABLE_COL1, TOPIC_TABLE_COL2};
        Cursor c = db.query(TOPIC_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                controllerId = c.getString(0);
                topic = c.getString(1);
                topicMap.put(controllerId, topic);
            }while(c.moveToNext());
        }
        return topicMap;
    }

    public void purgeUserProfileData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + USER_PROFILE_TABLE);
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

    public void purgeStatusData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + STATUS_TABLE);
    }

    public void purgeSecurityData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + SECURITY_TABLE);
    }

    public void purgeTopicData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TOPIC_TABLE);
    }

    public void printUserProfileData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {USER_PROFILE_TABLE_COL1, USER_PROFILE_TABLE_COL2, USER_PROFILE_TABLE_COL3};
        Cursor c = db.query(USER_PROFILE_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("USER PROFILE DATA");
            do{
                userProfileKey = c.getString(1);
                userProfileValue = c.getString(2);
                System.out.println(userProfileKey + " -> " + userProfileValue);
            }while(c.moveToNext());
        }
        else
            System.out.println("User Profile Table Empty");
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

    public void printStatusData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {STATUS_TABLE_COL1, STATUS_TABLE_COL2};
        Cursor c = db.query(STATUS_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("STATUS DATA");
            do{
                deviceId = c.getString(0);
                status = c.getString(1);
                System.out.println("device Id -> " + deviceId);
                System.out.println("status -> " + status);
            }while(c.moveToNext());
        }
        else
            System.out.println("Status Table Empty");
    }

    public void printSecurityData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {SECURITY_TABLE_COL1, SECURITY_TABLE_COL2};
        Cursor c = db.query(SECURITY_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("SECURITY DATA");
            do{
                controllerId = c.getString(0);
                securityToken = c.getString(1);
                System.out.println("controller Id -> " + controllerId);
                System.out.println("security token -> " + securityToken);
            }while(c.moveToNext());
        }
        else
            System.out.println("Security Table Empty");
    }

    public void printTopicData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {TOPIC_TABLE_COL1, TOPIC_TABLE_COL2};
        Cursor c = db.query(TOPIC_TABLE, coloumns, null, null, null, null, null);
        if(c.getCount()>0){
            c.moveToFirst();
            System.out.println("TOPIC DATA");
            do{
                controllerId = c.getString(0);
                topic = c.getString(1);
                System.out.println("controller Id -> " + controllerId);
                System.out.println("topic -> " + topic);
            }while(c.moveToNext());
        }
        else
            System.out.println("Topic Table Empty");
    }
}
