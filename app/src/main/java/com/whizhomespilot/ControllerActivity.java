package com.whizhomespilot;

/**
 * Created by smarhas on 12/23/2017.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
            R.drawable.light,
            R.drawable.light
    };

    Integer[] deviceStatusImage = {
            R.drawable.deviceoff,
            R.drawable.deviceoff
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
        deviceMapForSelectedController=StaticValues.getDeviceMapForSelectedController(controllerId);
        StaticValues.deviceMapForSelectedController=deviceMapForSelectedController;
        numberOfDevices=deviceMapForSelectedController.size();
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
        View view=inflater.inflate(R.layout.activity_controller, container, false);
        TextView tvHeader=(TextView)view.findViewById(R.id.header);
        System.out.println("CONTROLLER : " + StaticValues.controllerName);
        tvHeader.setText(StaticValues.controllerName);
        tvHeader.setEnabled(true);
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
        addControllerBtn.setImageResource(R.drawable.addcontroller);
        addControllerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAction=addAction;
                initiatePopupWindow(view);
            }
        });
        ImageButton editControllerBtn=(ImageButton)view.findViewById(R.id.editControllerButton);
        editControllerBtn.setImageResource(R.drawable.update);
        editControllerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAction=editAction;
                initiatePopupWindow(view);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void initiatePopupWindow(View v) {
        try {
            if(popupAction.equals(addAction)) {
                layout = (RelativeLayout) v.findViewById(R.id.popup_element_add);
                //We need to get the instance of the LayoutInflater, use the context of this activity
                layoutInflator = (LayoutInflater) ControllerActivity.this.getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //Inflate the view from a predefined XML layout
                final View layout = layoutInflator.inflate(R.layout.activity_addcontroller,
                        (ViewGroup) v.findViewById(R.id.popup_element_add));

                popupWindow = new PopupWindow(layout, 1000, 1000, true);
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                popupWindow.setFocusable(true);
            /*layout.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent){
                    popupWindow.dismiss();
                    return true;
                }
            });*/
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.mytoolbar);
                TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
                ImageButton closePopup = (ImageButton) toolbar.findViewById(R.id.close_popup);
                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                textView.setText("NEW CONTROLLER DETAILS");
                Button btnAddController = (Button) layout.findViewById(R.id.btnAddController);
                btnAddController.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText etControllerNumber = (EditText) layout.findViewById(R.id.etControllerNumber);
                        newControllerNumber = etControllerNumber.getText().toString();
                        EditText etControllerName = (EditText) layout.findViewById(R.id.etControllerName);
                        newControllerName = etControllerName.getText().toString();
                        EditText etPassKey = (EditText) layout.findViewById(R.id.etPassKey);
                        newPassKey = etPassKey.getText().toString();
                        new MyAsyncTask().execute();
                    }
                });
            }

            if(popupAction.equals(editAction)) {
                layout = (RelativeLayout) v.findViewById(R.id.popup_element_edit);
                //We need to get the instance of the LayoutInflater, use the context of this activity
                layoutInflator = (LayoutInflater) ControllerActivity.this.getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //Inflate the view from a predefined XML layout
                final View layout = layoutInflator.inflate(R.layout.activity_editcontroller,
                        (ViewGroup) v.findViewById(R.id.popup_element_edit));

                popupWindow = new PopupWindow(layout, 1000, 1000, true);
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                popupWindow.setFocusable(true);
            /*layout.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent){
                    popupWindow.dismiss();
                    return true;
                }
            });*/
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.mytoolbar);
                TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
                ImageButton closePopup = (ImageButton) toolbar.findViewById(R.id.close_popup);
                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                textView.setText("EDIT CONTROLLER DETAILS");
                etControllerName = (EditText) layout.findViewById(R.id.etControllerName);
                etFirstDevice = (EditText) layout.findViewById(R.id.etFirstDevice);
                etSecondDevice = (EditText) layout.findViewById(R.id.etSecondDevice);
                etControllerName.setText(controllerName);
                etFirstDevice.setText(deviceName[0]);
                etSecondDevice.setText(deviceName[1]);
                Button btnAddController = (Button) layout.findViewById(R.id.btnSave);
                btnAddController.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editControllerName = etControllerName.getText().toString();
                        editFirstDevice = etFirstDevice.getText().toString();
                        editSecondDevice = etSecondDevice.getText().toString();
                        StaticValues.updateControllerMap.put(controllerId, editControllerName);
                        StaticValues.updateDeviceMap.put(deviceId[0], editFirstDevice);
                        StaticValues.updateDeviceMap.put(deviceId[1], editSecondDevice);
                        new MyAsyncTask().execute();
                    }
                });
            }
            //TextView mResultText = (TextView) layout.findViewById(R.id.server_status_text);
            //Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
            //cancelButton.setOnClickListener(cancel_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    StaticValues.isUserNew = false;
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
                    StaticValues.deviceMap.put(controllerId, StaticValues.updateDeviceMap);
                    Iterator iterator=StaticValues.updateDeviceMap.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry entry=(Map.Entry)iterator.next();
                        myDb.insertDeviceData(entry.getKey().toString(), controllerId, entry.getValue().toString());
                    }
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
