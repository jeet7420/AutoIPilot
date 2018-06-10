package com.whizhomespilot;

/**
 * Created by smarhas on 12/23/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomList extends ArrayAdapter<String> {

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflator;
    private RelativeLayout layout;
    private final Activity context;
    private final String[] deviceName;
    private final String[] deviceIdArray=new String[2];
    private final Integer[] imageId;
    private final Integer[] buttonId;
    private boolean isFanOn=true;
    private boolean isLightOn=true;
    private boolean isSchedulerDeviceOn=true;
    private String deviceId, name, controllerId, signal,
            response, controllerName, topic, securtiyToken;
    HashMap<String, String> postDataParams;
    JSONObject jsonResponse;
    private ProgressDialog pDialog;
    public String deviceActionMode="U";
    public String schedularDeviceStatus="1";
    public String startDate="";
    public String startTime="";
    public String endDate="";
    public String endTime="";
    public String description="TEST SCHEDULAR";
    Schedule schedule;
    EditText etTimer;
    DatabaseHelper myDb;
    SimpleDateFormat sdf;
    Button btnSaveTimer;
    int status=0;
    public CustomList(Activity context,
                      String[] deviceName, Integer[] imageId, Integer[] buttonId) {
        super(context, R.layout.list_single, deviceName);
        this.context = context;
        this.deviceName = deviceName;
        this.imageId = imageId;
        this.buttonId = buttonId;
        myDb = new DatabaseHelper(context);
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        System.out.println("Position : " + position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        final ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.ib);
        txtTitle.setText(deviceName[position]);
        imageView.setImageResource(imageId[position]);
        imageButton.setImageResource(buttonId[position]);
        deviceIdArray[position]=StaticValues.getDeviceId(deviceName[position], StaticValues.deviceMapForSelectedController);
        if(position==0){
            if("0".equals(StaticValues.statusMap.get(deviceIdArray[position])))
                isFanOn=false;
            else
                isFanOn=true;
        }
        else{
            if("0".equals(StaticValues.statusMap.get(deviceIdArray[position])))
                isLightOn=false;
            else
                isLightOn=true;
        }

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setSelected(true);
                schedule=new Schedule();
                TextView txtTitle = (TextView) view.findViewById(R.id.txt);
                System.out.println("LONG CLICK ITEM -> " + txtTitle.getText().toString());
                controllerId=StaticValues.controllerId;
                controllerName=StaticValues.controllerName;
                deviceId=StaticValues.getDeviceId(txtTitle.getText().toString(), StaticValues.deviceMapForSelectedController);
                schedule.setControllerName(controllerName);
                schedule.setDeviceName(txtTitle.getText().toString());
                initiatePopupWindow(view);
                return false;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Button Clicked : " + position);
                name=deviceName[position];
                Iterator iteratorDeviceMapForSelectedController = StaticValues.deviceMapForSelectedController.entrySet().iterator();
                while (iteratorDeviceMapForSelectedController.hasNext()) {
                    Map.Entry entry = (Map.Entry)iteratorDeviceMapForSelectedController.next();
                    if(name.equals(entry.getValue())){
                        deviceId=entry.getKey().toString();
                    }
                }
                controllerId=StaticValues.controllerId;
                topic=StaticValues.topicMap.get(controllerId);
                securtiyToken=StaticValues.securityMap.get(controllerId);
                System.out.println("check controller id : " + controllerId);
                System.out.println("check topic : " + topic);
                System.out.println("check security token : " + securtiyToken);
                if(position==0){
                    if(isFanOn){
                        isFanOn=false;
                        signal="0";
                        imageButton.setImageResource(R.drawable.deviceoff1);
                    }
                    else{
                        isFanOn=true;
                        signal="1";
                        imageButton.setImageResource(R.drawable.deviceon1);
                    }
                }
                if(position==1){
                    if(isLightOn){
                        signal="0";
                        isLightOn=false;
                        imageButton.setImageResource(R.drawable.deviceoff1);
                    }
                    else{
                        signal="1";
                        isLightOn=true;
                        imageButton.setImageResource(R.drawable.deviceon1);
                    }
                }
                TestMQTT testMQTT=new TestMQTT();
                testMQTT.doDemo(topic, signal, deviceId, securtiyToken);

                new MyAsyncTask().execute();
            }
        });
        return rowView;
    }

    private void initiatePopupWindow(final View v) {
        try {
            layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = layoutInflator.inflate(R.layout.timer,
                        (ViewGroup) v.findViewById(R.id.popup_timer));

            popupWindow = new PopupWindow(layout, 1000, 750, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 170);
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
            final ImageButton toggleButton=(ImageButton) layout.findViewById(R.id.toggleButton);
            btnSaveTimer=(Button) layout.findViewById(R.id.saveTimer);
            btnSaveTimer.setEnabled(false);
            closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    v.setSelected(false);
                    popupWindow.dismiss();
                 }
            });
            textView.setText("SCHEDULE DETAILS");
            etTimer=(EditText)layout.findViewById(R.id.editTime);
            etTimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                  Calendar mcurrentTime = Calendar.getInstance();
                  int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                  int minute = mcurrentTime.get(Calendar.MINUTE);
                  TimePickerDialog mTimePicker;
                  mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                       @Override
                       public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                           etTimer.setText(selectedHour + ":" + selectedMinute);
                           startTime=etTimer.getText().toString();
                           endTime=etTimer.getText().toString();
                           if(startTime!=null){
                               schedule.setTime(startTime);
                               btnSaveTimer.setEnabled(true);
                           }
                           }
                       }, hour, minute, true);//Yes 24 hour time
                        //mTimePicker.setTitle("SET TIME");
                  mTimePicker.show();
                  }
            });
            btnSaveTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schedule.setAction(schedularDeviceStatus);
                    deviceActionMode="E";
                    description="ENABLE SCHEDULAR";
                    StaticValues.schedules.add(schedule);
                    System.out.println(StaticValues.schedules);
                    new MyAsyncTask().execute();
                    v.setSelected(false);
                    popupWindow.dismiss();
                }
            });

            if (toggleButton != null) {
                toggleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSchedulerDeviceOn){
                            schedularDeviceStatus="0";
                            isSchedulerDeviceOn=false;
                            toggleButton.setImageResource(R.drawable.deviceoff1);
                        }
                        else {
                            schedularDeviceStatus="1";
                            isSchedulerDeviceOn=true;
                            toggleButton.setImageResource(R.drawable.deviceon1);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //pDialog = new ProgressDialog(context);
            //pDialog.setMessage("Please wait...");
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            if(deviceActionMode.equals("U")){
                try{
                    HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                    postDataParams=new HashMap<String, String>();
                    postDataParams.put("deviceID",deviceId);
                    postDataParams.put("controllerID",controllerId);
                    postDataParams.put("userID",StaticValues.USERNAME);
                    postDataParams.put("status",signal);
                    postDataParams.put("source",StaticValues.SOURCEMANUAL);
                    jsonResponse = httpurlConnection.invokeService(StaticValues.deviceActionURL, postDataParams);
                    try{
                        response=jsonResponse.get("response").toString();
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
            }
            if(deviceActionMode.equals("E")){
                try{
                    HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                    postDataParams=new HashMap<String, String>();
                    postDataParams.put("deviceID",deviceId);
                    postDataParams.put("controllerID",controllerId);
                    postDataParams.put("userID",StaticValues.USERNAME);
                    postDataParams.put("status",schedularDeviceStatus);
                    postDataParams.put("startDate",startDate);
                    postDataParams.put("startTime",startTime);
                    postDataParams.put("endDate",endDate);
                    postDataParams.put("endTime",endTime);
                    postDataParams.put("description",description);
                    postDataParams.put("mode",deviceActionMode);
                    jsonResponse = httpurlConnection.invokeService(StaticValues.scheduleDeviceURL, postDataParams);
                    try{
                        response=jsonResponse.get("status").toString();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        return StaticValues.scheduleDeviceServiceResponseDown;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.scheduleDeviceServiceDown;
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
            if(deviceActionMode.equals("U")){
                myDb.updateStatusData(deviceId, signal);
                StaticValues.statusMap=myDb.readStatusData(StaticValues.USERNAME);
            }
            if(deviceActionMode.equals("E")){
                myDb.insertScheduleData(controllerId, deviceId, schedularDeviceStatus, startTime, "OPEN");
            }
            //if (pDialog.isShowing())
                //pDialog.dismiss();
        }
    }
}