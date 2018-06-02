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
    public static final String authURL="http://www.autoitechnologies.com/rest/login/authenticateToken";
    public static final String registerURL="http://www.autoitechnologies.com/rest/register/registerMemberWH";
    public static final String deviceActionURL="http://www.autoitechnologies.com/rest/action/deviceWH";
    public static final String addControllerURL="http://www.autoitechnologies.com/rest/controller/verifyController";
    public static final String editControllerURL="http://www.autoitechnologies.com/rest/controller/updateController";
    public static final String getMetricsURL="http://www.autoitechnologies.com/rest/action/getMetrics";
    public static final String googleSignInURL="http://www.autoitechnologies.com/rest/register/googleSingInWH";
    public static final String scheduleDeviceURL="http://www.autoitechnologies.com/rest/controller/scheduleDevice";
    public static final String registerLocalURL="http://localhost:8032/SmHome/rest/register/registerMemberWH";
    public static Context globalRoomContext;
    public static Intent globalRoomIntent;
    public static boolean connectionStatus=false;
    public static String controllerName="";
    public static String fragmentName="";
    public static ArrayList<String> controllerList=new ArrayList<String>();
    public static ArrayList<String> deviceList=new ArrayList<String>();
    public static final String loginURLLocal="http://192.168.0.107:8057/SmHome/rest/login/authenticateUserWH";
    //public static final String authURL="http://192.168.0.27:8899/SmHome/rest/login/authenticateToken";
    //public static final String registerURL="http://192.168.0.27:8899/SmHome/rest/register/user";
    public static final String deviceActionLocalURL="http://192.168.0.107:8057/SmHome/rest/action/deviceWH";
    public static final String addControllerLocalURL="http://192.168.0.103:8032/SmHome/rest/controller/verifyController";
    public static final String editControllerLocalURL="http://192.168.0.108:8032/SmHome/rest/controller/updateController";
    public static final String scheduleDeviceLocalURL="http://localhost:8035/SmHome/rest/controller/scheduleDevice";

    public static final String loginServiceDown="LOGIN SERVICE DOWN";
    public static final String authServiceDown="AUTHORIZE TOKEN SERVICE DOWN";
    public static final String registerServiceDown="REGISTER SERVICE DOWN";
    public static final String deviceActionServiceDown="DEVICE ACTION SERVICE DOWN";

    public static final String loginServiceResponseIssue="LOGIN SERVICE RESPONSE ISSUE";
    public static final String authServiceResponseIssue="AUTHORIZE TOKEN SERVICE RESPONSE ISSUE";
    public static final String registerServiceResponseIssue="REGISTER SERVICE RESPONSE ISSUE";
    public static final String deviceActionResponseIssue="DEVICE ACTION RESPONSE ISSUE";

    public static final String ADDNEWCONTROLLER="Add New Controller";
    public static final String EDITCONTROLLER="Edit Controller";

    public static String controller, firstDevice, secondDevice, firstDeviceId, secondDeviceId;

    public static String USERNAME="";
    public static final String SOURCEMANUAL="USER";
    public static boolean isUserNew=false;

    public static boolean ISLIGHTON=false;
    public static int FANSTATUS=0;
    public static boolean ISWINDOWOPEN=false;
    public static int ACSTATUS=0;

    public static final String LIGHTOF="0";
    public static final String LIGHTON="1";

    public static final String FANOF="0";
    public static final String FAN1="1";
    public static final String FAN2="2";
    public static final String FAN3="3";
    public static final String FAN4="4";
    public static final String FAN5="5";

    public static final String ACOF="0";
    public static final String AC1="1";
    public static final String AC2="2";
    public static final String AC3="3";
    public static final String AC4="4";
    public static final String AC5="5";

    public static final String WINDOWCLOSE="0";
    public static final String WINDOWOPEN="1";

    public static final String LIGHTOFMESSAGE="0";
    public static final String LIGHTONMESSAGE="1";

    public static final String FANOFMESSAGE="0";
    public static final String FAN1MESSAGE="1";
    public static final String FAN2MESSAGE="2";
    public static final String FAN3MESSAGE="3";
    public static final String FAN4MESSAGE="4";
    public static final String FAN5MESSAGE="5";

    public static final String ACOFMESSAGE="0";
    public static final String AC1MESSAGE="1";
    public static final String AC2MESSAGE="2";
    public static final String AC3MESSAGE="3";
    public static final String AC4MESSAGE="4";
    public static final String AC5MESSAGE="5";

    public static final String WINDOWCLOSEMESSAGE="0";
    public static final String WINDOWOPENMESAGE="1";

    public static final String TYPEISFAN="Fan";
    public static final String TYPEISLIGHT1="Light 1";
    public static final String TYPEISLIGHT2="Light 2";
    public static final String TYPEISLIGHT3="Light1";
    public static final String TYPEISLIGHT4="Light2";
    public static final String TYPEISWINDOW="Window";
    public static final String TYPEISAC="AC";

    public static HashMap<String, HashMap<String,String>> serverResult;
    public static HashMap<String,String> deviceStatus;
    public static TreeMap<Integer, String> deviceIdMapFull=new TreeMap<Integer, String>();

    public static HashMap<String, String> homeMap=new HashMap<String, String>();
    public static HashMap<String, String> controllerMap=new HashMap<String, String>();
    public static HashMap<String, HashMap<String,String>> deviceMap=new HashMap<String, HashMap<String,String>>();
    public static HashMap<String, String> deviceMapForSelectedController=new HashMap<String, String>();
    public static HashMap<String, String> updateControllerMap=new HashMap<String, String>();
    public static HashMap<String, String> updateDeviceMap=new HashMap<String, String>();
    public static HashMap<String, String> statusMap=new HashMap<String, String>();
    public static HashMap<String, String> securityMap=new HashMap<String, String>();
    public static HashMap<String, String> topicMap=new HashMap<String, String>();
    //public static TreeMap<Integer, Schedule> schedularMap=new TreeMap<Integer, Schedule>();
    public static LinkedHashSet<Schedule> schedules=new LinkedHashSet<Schedule>();
    public static int numberOfSchedules=0;
    public static boolean loginUsed=false;
    public static JSONObject metricsData;

    public static String roomId="R0";
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

    /*public static void removeFromPosition(int position){
        Iterator schedularIterator=StaticValues.schedularMap.entrySet().iterator();
        int count=0;
        int key=-1;
        while(schedularIterator.hasNext()){
            Map.Entry entry=(Map.Entry)schedularIterator.next();
            if(count==position){
                key= (int) entry.getKey();
            }
            count++;
        }
        if(key>0)
            StaticValues.schedularMap.remove(key);
    }*/
}
