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
    HashMap<String, String> deviceMapForSelectedController=new HashMap<String, String>();
    String controllerName, controllerId;
    String userId, response;
    int numberOfDevices;
    int position=0;
    ListView list;
    DatabaseHelper myDb;
    String[] deviceName;
    String[] deviceId;
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
        System.out.println("CONTROLLER : " + StaticValues.controllerName);
        CustomList adapter = new
                CustomList(ControllerActivity.this.getActivity(), deviceName, imageId, deviceStatusImage);
        list=(ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);
        ImageButton addControllerBtn=(ImageButton)view.findViewById(R.id.addControllerButton);
        addControllerBtn.setImageResource(R.drawable.add);
        addControllerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticValues.isUserNew=false;
                StaticValues.controllerName=StaticValues.ADDNEWCONTROLLER;
                Intent reloadMainActivity = new Intent(ControllerActivity.this.getActivity(),MainActivity.class);
                ControllerActivity.this.getActivity().startActivity(reloadMainActivity);
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
        return view;
    }
}
