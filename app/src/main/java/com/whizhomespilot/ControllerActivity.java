package com.whizhomespilot;

/**
 * Created by smarhas on 12/23/2017.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControllerActivity extends Fragment {
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflator;
    private RelativeLayout layout;
    HashMap<String, String> postDataParamsForAddAction;
    HashMap<String, HashMap<String,String>> postDataParamsForEditAction;
    JSONObject jsonResponse, jsonObject;
    private ProgressDialog pDialog;
    ListView list;
    String controllerName, controllerId;
    String newControllerNumber, newControllerName, newPassKey;
    String editControllerName, editFirstDevice, editSecondDevice;
    String userId, status, source, signal, response;
    int numberOfDevices;
    int position=0;
    HashMap<String, String> deviceMapForSelectedController=new HashMap<String, String>();
    String popupAction;
    DatabaseHelper myDb;
    public final String addAction="ADD";
    public final String editAction="EDIT";
    String[] deviceName;
    String[] deviceId;
    EditText etControllerName, etFirstDevice, etSecondDevice;
    Integer[] imageId = {
            R.drawable.fan,
            R.drawable.light1
    };

    Integer[] deviceStatusImage = {
            R.drawable.deviceon1,
            R.drawable.deviceoff1
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Inside Controller Activity");
        myDb = new DatabaseHelper(this.getActivity());
        userId=StaticValues.USERNAME;
        controllerName=StaticValues.controllerName;
        controllerId=StaticValues.getControllerId(controllerName);
        StaticValues.controllerId=controllerId;
        System.out.println("CONTROLLER ID : " + controllerId);
        System.out.println("CONTROLLER NAME : " + controllerName);
        StaticValues.printControllerMap();
        StaticValues.printDeviceMap();
        StaticValues.printUpdateControllerMap();
        StaticValues.printUpdateDeviceMap();
        deviceMapForSelectedController=StaticValues.getDeviceMapForSelectedController(controllerId);
        StaticValues.deviceMapForSelectedController=deviceMapForSelectedController;
        numberOfDevices=deviceMapForSelectedController.size();
        System.out.println("NUMBER OF DEVICES : " + numberOfDevices);
        deviceName=new String[numberOfDevices];
        deviceId=new String[numberOfDevices];
        Iterator iteratorDeviceMapForSelectedController = deviceMapForSelectedController.entrySet().iterator();
        while (iteratorDeviceMapForSelectedController.hasNext()) {
            Map.Entry entry = (Map.Entry)iteratorDeviceMapForSelectedController.next();
            if(position<numberOfDevices){
                deviceName[position]=entry.getValue().toString();
                deviceId[position]=entry.getKey().toString();
                position++;
            }
        }

        for(int i=0; i<numberOfDevices; i++){
            if("Fan".equals(deviceName[i])){
                imageId[i]=R.drawable.fan;
            }
            else if("Light".equals(deviceName[i])){
                imageId[i]=R.drawable.light1;
            }
            else if("Geysor".equals(deviceName[i])){
                imageId[i]=R.drawable.geysor;
            }
            else if("3-Pin".equals(deviceName[i])){
                imageId[i]=R.drawable.pin;
            }
            else{
                imageId[i]=R.drawable.fan;
            }
        }

        for(int i=0; i<numberOfDevices; i++){
            if("0".equals(StaticValues.statusMap.get(deviceId[i]))){
                deviceStatusImage[i]=R.drawable.deviceoff1;
            }
            else{
                deviceStatusImage[i]=R.drawable.deviceon1;
            }
        }

        View view=inflater.inflate(R.layout.activity_controller, container, false);
        ImageButton roomWall=(ImageButton)view.findViewById(R.id.imagebutton1);
        if("Bedroom".equals(StaticValues.controllerName)){
            roomWall.setBackground(getResources().getDrawable(R.drawable.bedroomwall));
        }

        else if("Hall".equals(StaticValues.controllerName)){
            roomWall.setBackground(getResources().getDrawable(R.drawable.bedroomwall));
        }

        else if("Kitchen".equals(StaticValues.controllerName)){
            roomWall.setBackground(getResources().getDrawable(R.drawable.bedroomwall));
        }

        else if("Bathroom".equals(StaticValues.controllerName)){
            roomWall.setBackground(getResources().getDrawable(R.drawable.bedroomwall));
        }

        else{
            roomWall.setBackground(getResources().getDrawable(R.drawable.bedroomwall));
        }

        StaticValues.firstDevice=deviceName[0];
        StaticValues.secondDevice=deviceName[1];
        StaticValues.firstDeviceId=deviceId[0];
        StaticValues.secondDeviceId=deviceId[1];

        roomWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticValues.controller=StaticValues.controllerName;
                StaticValues.controllerName=StaticValues.EDITCONTROLLER;
                Intent reloadMainActivity = new Intent(ControllerActivity.this.getActivity(),MainActivity.class);
                ControllerActivity.this.getActivity().startActivity(reloadMainActivity);
            }
        });
        //TextView tvHeader=(TextView)view.findViewById(R.id.header);
        System.out.println("CONTROLLER : " + StaticValues.controllerName);
        //tvHeader.setText(StaticValues.controllerName);
        //tvHeader.setEnabled(true);
        CustomList adapter = new
                CustomList(ControllerActivity.this.getActivity(), deviceName, imageId, deviceStatusImage);
        //list=(ListView)getActivity().findViewById(R.id.list);
        list=(ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);
        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                System.out.println("You Clicked at " +deviceName[+ position]);
            }
        });*/
        ImageButton addControllerBtn=(ImageButton)view.findViewById(R.id.addControllerButton);
        addControllerBtn.setImageResource(R.drawable.add);
        addControllerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAction=addAction;
                //initiatePopupWindow(view);
            }
        });
        /*ImageButton editControllerBtn=(ImageButton)view.findViewById(R.id.editControllerButton);
        editControllerBtn.setImageResource(R.drawable.update);
        editControllerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAction=editAction;
                initiatePopupWindow(view);
            }
        });*/
        // Inflate the layout for this fragment
        return view;
    }

    public static HashMap<String, HashMap<String,String>> jsonToHashMap(JSONObject object) throws JSONException {
        HashMap<String, HashMap<String,String>> map = new HashMap<String, HashMap<String,String>>();
        HashMap<String, String> innerMap=null;
        Iterator<String> keysItrOuter = object.keys();
        while(keysItrOuter.hasNext()) {
            String keyOuter = keysItrOuter.next();
            Object valueOuter = object.get(keyOuter);
            JSONObject innerJsonObject = (JSONObject)valueOuter;
            Iterator<String> keysItrInner = innerJsonObject.keys();
            innerMap = new HashMap<String, String>();
            while(keysItrInner.hasNext()){
                String keyInner = keysItrInner.next();
                Object valueInner = innerJsonObject.get(keyInner);
                String valueInnerAsString = valueInner.toString();
                innerMap.put(keyInner, valueInnerAsString);
            }
            map.put(keyOuter, innerMap);
        }
        return map;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //pDialog = new ProgressDialog(RoomActivity.this);
            //pDialog.setMessage("Please wait...");
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                if(popupAction.equals(addAction)) {
                    postDataParamsForAddAction = new HashMap<String, String>();
                    postDataParamsForAddAction.put("controllerId", newControllerNumber);
                    postDataParamsForAddAction.put("passKey", newPassKey);
                    postDataParamsForAddAction.put("userName", StaticValues.USERNAME);
                    postDataParamsForAddAction.put("controllerName", newControllerName);
                    jsonResponse = httpurlConnection.invokeService(StaticValues.addControllerURL, postDataParamsForAddAction);
                }
                if(popupAction.equals(editAction)) {
                    postDataParamsForEditAction = new HashMap<String, HashMap<String,String>>();
                    postDataParamsForEditAction.put("controllers", StaticValues.updateControllerMap);
                    postDataParamsForEditAction.put("devices", StaticValues.updateDeviceMap);
                    jsonResponse = httpurlConnection.invokeServiceForMap(StaticValues.editControllerURL, postDataParamsForEditAction);
                }
                StaticValues.serverResult=jsonToHashMap(jsonResponse);
                try{
                    jsonObject=(JSONObject)jsonResponse.get("response");
                    response=jsonObject.get("status").toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.deviceActionResponseIssue;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return StaticValues.deviceActionServiceDown;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(ControllerActivity.this.getActivity(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if(popupAction.equals(addAction)) {
                if (result.equals("1")) {
                    System.out.println("New Controller Name -> " + newControllerName);
                    System.out.println("New Controller Number -> " + newControllerNumber);
                    System.out.println("Server Result :" + StaticValues.serverResult);
                    //StaticValues.controllerList.add(newControllerName);
                    StaticValues.controllerMap.put(newControllerNumber, newControllerName);
                    myDb.insertControllerData(newControllerNumber, newControllerName);
                    System.out.println("Updated Controller HashMap : " + StaticValues.controllerMap);
                    StaticValues.deviceMap.put(newControllerNumber, StaticValues.serverResult.get("devices"));
                    Iterator iterator=StaticValues.serverResult.get("devices").entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry entry=(Map.Entry)iterator.next();
                        myDb.insertDeviceData(entry.getKey().toString(), newControllerNumber, entry.getValue().toString());
                    }
                    myDb.printControllerData(StaticValues.USERNAME);
                    myDb.printDeviceData(StaticValues.USERNAME);
                    myDb.printSchedularData(StaticValues.USERNAME);
                    StaticValues.isUserNew = false;
                    StaticValues.printControllerMap();
                    StaticValues.printDeviceMap();
                    StaticValues.printUpdateControllerMap();
                    StaticValues.printUpdateDeviceMap();
                } else
                    Toast.makeText(ControllerActivity.this.getActivity(), "Controller could not be added. Please try again !", Toast.LENGTH_LONG).show();
                popupWindow.dismiss();
            }
            if(popupAction.equals(editAction)) {
                if (result.equals("1")) {
                    System.out.println("New Controller Name -> " + editControllerName);
                    System.out.println("New First Device -> " + editFirstDevice);
                    System.out.println("New First Device -> " + editSecondDevice);
                    System.out.println("Server Result :" + StaticValues.serverResult);
                    StaticValues.controllerMap.put(controllerId, editControllerName);
                    myDb.updateControllerData(controllerId, editControllerName);
                    System.out.println("CHECK1 : " + controllerId);
                    StaticValues.deviceMap.put(controllerId, StaticValues.updateDeviceMap);
                    System.out.println("SHEKHAR");
                    StaticValues.printDeviceMap();
                    Iterator iterator=StaticValues.updateDeviceMap.entrySet().iterator();
                    while(iterator.hasNext()){
                        System.out.println("CHECK1 : " + controllerId);
                        Map.Entry entry=(Map.Entry)iterator.next();
                        myDb.updateDeviceData(entry.getKey().toString(), controllerId, entry.getValue().toString());
                    }
                    myDb.printControllerData(StaticValues.USERNAME);
                    myDb.printDeviceData(StaticValues.USERNAME);
                    myDb.printSchedularData(StaticValues.USERNAME);
                    StaticValues.printControllerMap();
                    StaticValues.printDeviceMap();
                    StaticValues.printUpdateControllerMap();
                    StaticValues.printUpdateDeviceMap();
                    controllerId = null;
                } else
                    Toast.makeText(ControllerActivity.this.getActivity(), "Controller could not be updated. Please try again !", Toast.LENGTH_LONG).show();
                popupWindow.dismiss();
            }
            Intent reloadMainActivity = new Intent(ControllerActivity.this.getActivity(),MainActivity.class);
            ControllerActivity.this.getActivity().startActivity(reloadMainActivity);
            //if (pDialog.isShowing())
            //  pDialog.dismiss();
        }
    }
}
