package com.whizhomespilot;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by smarhas on 12/16/2017.
 */

public class StaticValues {
    public static final String loginURL="http://www.autoitechnologies.com/rest/login/authenticateUserWH";
    public static final String registerURL="http://www.autoitechnologies.com/rest/register/registerMemberWH";
    public static final String deviceActionURL="http://www.autoitechnologies.com/rest/action/deviceWH";
    public static final String addControllerURL="http://www.autoitechnologies.com/rest/controller/verifyController";
    public static final String editControllerURL="http://www.autoitechnologies.com/rest/controller/updateController";
    public static final String getMetricsURL="http://www.autoitechnologies.com/rest/action/getMetrics";
    public static final String googleSignInURL="http://www.autoitechnologies.com/rest/register/googleSingInWH";
    public static final String scheduleDeviceURL="http://www.autoitechnologies.com/rest/controller/scheduleDevice";
    public static final String changePasswordURL="http://www.autoitechnologies.com/rest/register/changePassword";
    public static final String sendTemporaryPassword="http://www.autoitechnologies.com/rest/register/sendTemporaryPassword";
    public static ArrayList<String> controllerList=new ArrayList<String>();
    public static ArrayList<String> deviceList=new ArrayList<String>();

    public static final String loginServiceDown="LOGIN SERVICE DOWN";
    public static final String registerServiceDown="REGISTER SERVICE DOWN";
    public static final String deviceActionServiceDown="DEVICE ACTION SERVICE DOWN";
    public static final String changePasswordServiceDown="CHANGE PASSWORD SERVICE DOWN";
    public static final String addControllerServiceDown="ADD CONTROLLER SERVICE DOWN";
    public static final String editControllerServiceDown="EDIT CONTROLLER SERVICE DOWN";
    public static final String scheduleDeviceServiceDown="SCHEDULE DEVICE SERVICE DOWN";

    public static final String loginServiceResponseIssue="LOGIN SERVICE RESPONSE ISSUE";
    public static final String registerServiceResponseIssue="REGISTER SERVICE RESPONSE ISSUE";
    public static final String deviceActionResponseIssue="DEVICE ACTION RESPONSE ISSUE";
    public static final String changePasswordResponseIssue="CHANGE PASSWORD RESPONSE ISSUE";
    public static final String addControllerServiceResponseDown="ADD CONTROLLER SERVICE RESPONSE ISSUE";
    public static final String editControllerServiceResponseDown="EDIT CONTROLLER SERVICE RESPONSE ISSUE";
    public static final String scheduleDeviceServiceResponseDown="SCHEDULE DEVICE SERVICE RESPONSE ISSUE";

    public static final String MAINACTIVITY="Main Activity";
    public static final String BLANKACTIVITY="Blank Activity";
    public static final String ADDNEWCONTROLLER="Add iSwitch";
    public static final String EDITCONTROLLER="Edit Controller";
    public static final String USERPROFILE="User Profile";
    public static final String SCHEDULAR="Schedules";
    public static final String METRICS="Metrics";
    public static final String ABOUTUS="About Us";
    public static final String CONTACTUS="Contact Us";
    public static final String LOGOUT="Logout";

    public static final String CONTROLLER="Controller";

    public static final String MQTT_BROKER_URL="tcp://45.113.138.18:1883";

    public static String controller, firstDevice, secondDevice, firstDeviceId, secondDeviceId;

    public static String USERNAME="";
    public static final String SOURCEMANUAL="USER";

    public static boolean isUserNew=false;
    public static boolean loginUsed=false;

    public static String controllerName="";
    public static String fragmentName="";
    public static String flowContext="";

    public static HashMap<String, HashMap<String,String>> serverResult;

    public static HashMap<String, String> userProfileMap=new HashMap<String, String>();
    public static HashMap<String, String> controllerMap=new HashMap<String, String>();
    public static HashMap<String, HashMap<String,String>> deviceMap=new HashMap<String, HashMap<String,String>>();
    public static HashMap<String, String> deviceMapForSelectedController=new HashMap<String, String>();
    public static HashMap<String, String> updateControllerMap=new HashMap<String, String>();
    public static HashMap<String, String> updateDeviceMap=new HashMap<String, String>();
    public static HashMap<String, String> statusMap=new HashMap<String, String>();
    public static HashMap<String, String> securityMap=new HashMap<String, String>();
    public static HashMap<String, String> topicMap=new HashMap<String, String>();
    public static LinkedHashSet<Schedule> schedules=new LinkedHashSet<Schedule>();
    public static JSONObject metricsData;

    public static final String UserNameKey="userName";
    public static final String UserEmailIdKey="email";

    public static String deviceId;
    public static String controllerId;

    public static String getControllerId(String controllerName){
        Iterator iteratorControllerMap = StaticValues.controllerMap.entrySet().iterator();
        while (iteratorControllerMap.hasNext()) {
            Map.Entry entry = (Map.Entry)iteratorControllerMap.next();
            if(entry.getValue().toString().equals(controllerName)){
                controllerId=entry.getKey().toString();
            }
        }
        return controllerId;
    }

    public static String getDeviceId(String deviceName, HashMap deviceMapForSelectedController){
        Iterator iteratorDeviceMapForSelectedController = deviceMapForSelectedController.entrySet().iterator();
        while (iteratorDeviceMapForSelectedController.hasNext()) {
            Map.Entry entry = (Map.Entry)iteratorDeviceMapForSelectedController.next();
            if(deviceName.equals(entry.getValue())){
                deviceId=entry.getKey().toString();
            }
        }
        return deviceId;
    }

    public static HashMap getDeviceMapForSelectedController(String controllerId){
        Iterator iteratorDeviceMap = StaticValues.deviceMap.entrySet().iterator();
        while (iteratorDeviceMap.hasNext()) {
            Map.Entry entry = (Map.Entry)iteratorDeviceMap.next();
            if(entry.getKey().equals(controllerId)){
                deviceMapForSelectedController=(HashMap<String, String>)entry.getValue();
            }
        }
        return deviceMapForSelectedController;
    }

    public static void printControllerMap(){
        System.out.println(StaticValues.controllerMap);
    }

    public static void printDeviceMap(){
        System.out.println(StaticValues.deviceMap);
    }

    public static void printUpdateControllerMap(){
        System.out.println(StaticValues.updateControllerMap);
    }

    public static void printUpdateDeviceMap(){
        System.out.println(StaticValues.updateDeviceMap);
    }

    public static Schedule getScheduleAtPosition(int position){
        int count=0;
        for(Schedule s : schedules){
            if(count==position)
                return s;
            count++;
        }
        return null;
    }
}
