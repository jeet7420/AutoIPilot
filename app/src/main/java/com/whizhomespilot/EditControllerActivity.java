package com.whizhomespilot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by smarhas on 5/10/2018.
 */

public class EditControllerActivity extends Fragment {
    String newControllerName, newFirstDeviceName, newSecondDeviceName;
    AutoCompleteTextView actvControllerName, actvFirstDeviceName, actvSecondDeviceName;
    HashMap<String, HashMap<String,String>> postDataParams;
    private String[] mControllerNames, mDeviceNames;
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
        View view=inflater.inflate(R.layout.activity_editcontroller, container, false);

        myDb = new DatabaseHelper(this.getActivity());

        actvControllerName = (AutoCompleteTextView) view.findViewById(R.id.actvControllerName);
        actvFirstDeviceName = (AutoCompleteTextView) view.findViewById(R.id.actvFirstDeviceName);
        actvSecondDeviceName = (AutoCompleteTextView) view.findViewById(R.id.actvSecondDeviceName);

        actvControllerName.setText(StaticValues.controllerName);
        actvFirstDeviceName.setText(StaticValues.firstDevice);
        actvSecondDeviceName.setText(StaticValues.secondDevice);

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

        ImageButton btnSave = (ImageButton) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newControllerName = actvControllerName.getText().toString();
                newFirstDeviceName = actvFirstDeviceName.getText().toString();
                newSecondDeviceName = actvSecondDeviceName.getText().toString();
                if("".equals(newControllerName) || "".equals(newFirstDeviceName) || "".equals(newSecondDeviceName)){
                    Toast.makeText(getActivity(), "Please provide values for all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    StaticValues.controllerName=newControllerName;
                    StaticValues.updateControllerMap.put(StaticValues.controllerId, newControllerName);
                    StaticValues.updateDeviceMap.put(StaticValues.firstDeviceId, newFirstDeviceName);
                    StaticValues.updateDeviceMap.put(StaticValues.secondDeviceId, newSecondDeviceName);
                    new EditControllerActivity.MyAsyncTask().execute();
                }
            }
        });
        return view;
    }

    public static HashMap<String, HashMap<String,String>> jsonToHashMap(JSONObject object) throws JSONException {
        HashMap<String, HashMap<String,String>> map = new HashMap<String, HashMap<String,String>>();
        HashMap<String, String> innerMap=null;
        if(object!=null){
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
        }
        return map;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(EditControllerActivity.this.getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                postDataParams = new HashMap<String, HashMap<String,String>>();
                postDataParams.put("controllers", StaticValues.updateControllerMap);
                postDataParams.put("devices", StaticValues.updateDeviceMap);
                jsonResponse = httpurlConnection.invokeServiceForMap(StaticValues.editControllerURL, postDataParams);
                StaticValues.serverResult=jsonToHashMap(jsonResponse);
                try{
                    if(jsonResponse!=null){
                        jsonObject=(JSONObject)jsonResponse.get("response");
                        if(jsonObject!=null)
                            response=jsonObject.get("status").toString();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.editControllerServiceResponseDown;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return StaticValues.editControllerServiceDown;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(EditControllerActivity.this.getActivity(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if (result.equals("1")) {
                System.out.println("New Controller Name -> " + newControllerName);
                System.out.println("New First Device Name -> " + newFirstDeviceName);
                System.out.println("New Second Device Name -> " + newSecondDeviceName);
                System.out.println("Server Result :" + StaticValues.serverResult);
                StaticValues.controllerMap.put(StaticValues.controllerId, newControllerName);
                myDb.updateControllerData(StaticValues.controllerId, newControllerName);
                StaticValues.deviceMap.put(StaticValues.controllerId, StaticValues.updateDeviceMap);
                Iterator iterator=StaticValues.updateDeviceMap.entrySet().iterator();
                StaticValues.controllerName=newControllerName;
                StaticValues.fragmentName=StaticValues.CONTROLLER;
                StaticValues.flowContext=StaticValues.EDITCONTROLLER;
                while(iterator.hasNext()){
                    Map.Entry entry=(Map.Entry)iterator.next();
                    myDb.updateDeviceData(entry.getKey().toString(), StaticValues.controllerId, entry.getValue().toString());
                }
            } else
                Toast.makeText(EditControllerActivity.this.getActivity(), "Controller could not be updated. Please try again !", Toast.LENGTH_LONG).show();
            Intent reloadMainActivity = new Intent(EditControllerActivity.this.getActivity(),MainActivity.class);
            EditControllerActivity.this.getActivity().startActivity(reloadMainActivity);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
