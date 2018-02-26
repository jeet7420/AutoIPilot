package com.whizhomespilot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by smarhas on 1/6/2018.
 */

public class BlankActivity extends Fragment {
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflator;
    private RelativeLayout layout;
    HashMap<String, String> postDataParams;
    JSONObject jsonResponse, jsonObject;
    private ProgressDialog pDialog;
    String newControllerNumber, newControllerName, newPassKey, response;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_blank, container, false);
        ImageButton imageButton=(ImageButton)view.findViewById(R.id.addControllerButton);
        imageButton.setImageResource(R.drawable.addcontroller);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopupWindow(view);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void initiatePopupWindow(View v) {
        try {
            layout=(RelativeLayout)v.findViewById(R.id.popup_element_add);
            //We need to get the instance of the LayoutInflater, use the context of this activity
            layoutInflator = (LayoutInflater) BlankActivity.this.getActivity()
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
            Toolbar toolbar=(Toolbar)layout.findViewById(R.id.mytoolbar);
            TextView textView=(TextView)toolbar.findViewById(R.id.tv_toolbar);
            ImageButton closePopup=(ImageButton)toolbar.findViewById(R.id.close_popup);
            closePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });
            textView.setText("CONTROLLER DETAILS");
            Button btnAddController=(Button)layout.findViewById(R.id.btnAddController);
            btnAddController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText etControllerNumber=(EditText)layout.findViewById(R.id.etControllerNumber);
                    newControllerNumber=etControllerNumber.getText().toString();
                    EditText etControllerName=(EditText)layout.findViewById(R.id.etControllerName);
                    newControllerName=etControllerName.getText().toString();
                    EditText etPassKey=(EditText)layout.findViewById(R.id.etPassKey);
                    newPassKey=etPassKey.getText().toString();
                    new MyAsyncTask().execute();
                }
            });
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
                postDataParams=new HashMap<String, String>();
                postDataParams.put("controllerId",newControllerNumber);
                postDataParams.put("passKey",newPassKey);
                postDataParams.put("userName",StaticValues.USERNAME);
                postDataParams.put("controllerName",newControllerName);
                jsonResponse = httpurlConnection.invokeService(StaticValues.addControllerURL, postDataParams);
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
            Toast.makeText(BlankActivity.this.getActivity(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if(result.equals("1")){
                System.out.println("New Controller Name -> " + newControllerName);
                System.out.println("New Controller Number -> " + newControllerNumber);
                System.out.println("Server Result :" + StaticValues.serverResult);
                //StaticValues.controllerList.add(newControllerName);
                StaticValues.controllerMap.put(newControllerNumber, newControllerName);
                System.out.println("Updated Controller HashMap : " + StaticValues.controllerMap);
                StaticValues.deviceMap.put(newControllerNumber, StaticValues.serverResult.get("devices"));
                StaticValues.isUserNew=false;
            }
            else
                Toast.makeText(BlankActivity.this.getActivity(), "Controller could not be added. Please try again !", Toast.LENGTH_LONG).show();
            popupWindow.dismiss();
            Intent reloadMainActivity = new Intent(BlankActivity.this.getActivity(),MainActivity.class);
            BlankActivity.this.getActivity().startActivity(reloadMainActivity);
            //if (pDialog.isShowing())
            //  pDialog.dismiss();
        }
    }
}
