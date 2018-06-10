package com.whizhomespilot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by smarhas on 12/24/2017.
 */

public class AddControllerActivity extends Fragment {

    String newControllerNumber, newControllerName, newPassKey, newFirstDeviceName, newSecondDeviceName;
    AutoCompleteTextView actvControllerName, actvFirstDeviceName, actvSecondDeviceName;
    private String[] mControllerNames, mDeviceNames;
    EditText etControllerNumber, etPassKey;
    HashMap<String, String> postDataParams;
    JSONObject jsonResponse, jsonObject;
    private ProgressDialog pDialog;
    ArrayAdapter<String> adapter;
    DatabaseHelper myDb;
    String response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view=inflater.inflate(R.layout.activity_addcontroller, container, false);

         myDb = new DatabaseHelper(this.getActivity());

         etControllerNumber = (EditText) view.findViewById(R.id.etControllerNumber);
         etPassKey = (EditText) view.findViewById(R.id.etPassKey);

         actvControllerName = (AutoCompleteTextView) view.findViewById(R.id.actvControllerName);
         actvFirstDeviceName = (AutoCompleteTextView) view.findViewById(R.id.actvFirstDeviceName);
         actvSecondDeviceName = (AutoCompleteTextView) view.findViewById(R.id.actvSecondDeviceName);

         mControllerNames = getResources().getStringArray(R.array.controller_names);
         
         adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, mControllerNames);
         actvControllerName.setAdapter(adapter);
         actvControllerName.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 actvControllerName.showDropDown();
             }
         });
         
         mDeviceNames = getResources().getStringArray(R.array.device_names);
         
         adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, mDeviceNames);
         actvFirstDeviceName.setAdapter(adapter);
         actvFirstDeviceName.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 actvFirstDeviceName.showDropDown();
             }
         });
         actvSecondDeviceName.setAdapter(adapter);
         actvSecondDeviceName.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 actvSecondDeviceName.showDropDown();
             }
         });
         
         ImageButton btnAddController = (ImageButton) view.findViewById(R.id.btnAddController);
         btnAddController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newControllerNumber = etControllerNumber.getText().toString();
                newPassKey = etPassKey.getText().toString();
                newControllerName = actvControllerName.getText().toString();
                newFirstDeviceName = actvFirstDeviceName.getText().toString();
                newSecondDeviceName = actvSecondDeviceName.getText().toString();
                if("".equals(newControllerNumber) || "".equals(newPassKey) || "".equals(newControllerName)
                    || "".equals(newFirstDeviceName) || "".equals(newSecondDeviceName)){
                    Toast.makeText(getActivity(), "Please provide values for all fields", Toast.LENGTH_SHORT).show();
                }
                else
                    new AddControllerActivity.MyAsyncTask().execute();
            }
        });
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
            pDialog = new ProgressDialog(AddControllerActivity.this.getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                postDataParams = new HashMap<String, String>();
                postDataParams.put("controllerId", newControllerNumber);
                postDataParams.put("passKey", newPassKey);
                postDataParams.put("userName", StaticValues.USERNAME);
                postDataParams.put("controllerName", newControllerName);
                postDataParams.put("device1", newFirstDeviceName);
                postDataParams.put("device2", newSecondDeviceName);
                jsonResponse = httpurlConnection.invokeService(StaticValues.addControllerURL, postDataParams);
                StaticValues.serverResult=jsonToHashMap(jsonResponse);
                try{
                    jsonObject=(JSONObject)jsonResponse.get("response");
                    response=jsonObject.get("status").toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.addControllerServiceResponseDown;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return StaticValues.addControllerServiceDown;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(AddControllerActivity.this.getActivity(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if (result.equals("1")) {
                System.out.println("New Controller Name -> " + newControllerName);
                System.out.println("New Controller Number -> " + newControllerNumber);
                System.out.println("New First Device Name -> " + newFirstDeviceName);
                System.out.println("New Second Device Name -> " + newSecondDeviceName);
                System.out.println("Server Result :" + StaticValues.serverResult);
                StaticValues.controllerList.add(newControllerName);
                StaticValues.controllerMap.put(newControllerNumber, newControllerName);
                myDb.insertControllerData(newControllerNumber, newControllerName);
                System.out.println("Updated Controller HashMap : " + StaticValues.controllerMap);
                StaticValues.deviceMap.put(newControllerNumber, StaticValues.serverResult.get("devices"));
                Iterator iterator=StaticValues.serverResult.get("devices").entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry entry=(Map.Entry)iterator.next();
                    myDb.insertDeviceData(entry.getKey().toString(), newControllerNumber, entry.getValue().toString());
                }

                StaticValues.securityMap.put(newControllerNumber, StaticValues.serverResult.get("broadcastDetails").get("security"));
                StaticValues.topicMap.put(newControllerNumber, StaticValues.serverResult.get("broadcastDetails").get("topic"));

                Iterator iteratorSecurityMap=StaticValues.securityMap.entrySet().iterator();
                while(iteratorSecurityMap.hasNext()){
                    Map.Entry entry= (Map.Entry) iteratorSecurityMap.next();
                    myDb.insertSecurityData(entry.getKey().toString(), entry.getValue().toString());
                }

                Iterator iteratorTopicMap=StaticValues.topicMap.entrySet().iterator();
                while(iteratorTopicMap.hasNext()){
                    Map.Entry entry= (Map.Entry) iteratorTopicMap.next();
                    myDb.insertTopicData(entry.getKey().toString(), entry.getValue().toString());
                }

                StaticValues.statusMap=StaticValues.serverResult.get("deviceStatus");
                Iterator iteratorStatusMap=StaticValues.statusMap.entrySet().iterator();
                while(iteratorStatusMap.hasNext()){
                    Map.Entry entry= (Map.Entry) iteratorStatusMap.next();
                    myDb.insertStatusData(entry.getKey().toString(), entry.getValue().toString());
                }

                myDb.printControllerData(StaticValues.USERNAME);
                myDb.printDeviceData(StaticValues.USERNAME);
                myDb.printSchedularData(StaticValues.USERNAME);
                StaticValues.isUserNew = false;
                StaticValues.controllerName=newControllerName;
                StaticValues.fragmentName=StaticValues.CONTROLLER;
                StaticValues.flowContext=StaticValues.ADDNEWCONTROLLER;
                StaticValues.printControllerMap();
                StaticValues.printDeviceMap();
                StaticValues.printUpdateControllerMap();
                StaticValues.printUpdateDeviceMap();
            } else{
                Toast.makeText(AddControllerActivity.this.getActivity(), "Controller could not be added. Please try again !", Toast.LENGTH_LONG).show();   
            }
            Intent reloadMainActivity = new Intent(AddControllerActivity.this.getActivity(),MainActivity.class);
            AddControllerActivity.this.getActivity().startActivity(reloadMainActivity);
            //if (pDialog.isShowing())
            //  pDialog.dismiss();
        }
    }
}
