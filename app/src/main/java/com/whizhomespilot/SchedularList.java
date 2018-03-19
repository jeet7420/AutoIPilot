package com.whizhomespilot;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by smarhas on 2/24/2018.
 */

public class SchedularList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] scheduleDetails;
    private final Integer[] updateImageId;
    private final Integer[] deleteImageId;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflator;
    JSONObject jsonResponse;
    private String deviceId, name, controllerId, signal,
            response, controllerName, scheduleDetail, str1, deviceName;
    private String[] splitString;
    HashMap<String, String> postDataParams;
    HashMap<String, String> schedularDetails;
    public String deviceActionMode="U";
    public String schedularDeviceStatus="0";
    public String startDate="";
    public String startTime="";
    public String endDate="";
    public String endTime="";
    public String description="TEST SCHEDULAR";
    EditText etTimer;
    DatabaseHelper myDb;
    public int pos;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        myDb = new DatabaseHelper(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_schedular, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        final ImageButton imageButton1 = (ImageButton) rowView.findViewById(R.id.ib1);
        final ImageButton imageButton2 = (ImageButton) rowView.findViewById(R.id.ib2);
        txtTitle.setText(scheduleDetails[position]);
        imageButton1.setImageResource(updateImageId[position]);
        imageButton2.setImageResource(deleteImageId[position]);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos=position;
                System.out.println("SCHEDULAR CLICKED AT : " + position);
                schedularDetails=new HashMap<String, String>();
                scheduleDetail=scheduleDetails[position];
                str1=scheduleDetail.substring(0, scheduleDetail.indexOf("  "));
                splitString=str1.split(":");
                controllerName=splitString[0].trim();
                deviceName=splitString[1].trim();
                controllerId=StaticValues.getControllerId(controllerName);
                deviceId=StaticValues.getDeviceId(deviceName, StaticValues.deviceMap.get(controllerId));
                schedularDetails.put("controllerName", controllerName);
                schedularDetails.put("deviceName", deviceName);
                initiatePopupWindow(view);
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleDetail=scheduleDetails[position];
                str1=scheduleDetail.substring(0, scheduleDetail.indexOf("  "));
                splitString=str1.split(":");
                controllerName=splitString[0].trim();
                deviceName=splitString[1].trim();
                controllerId=StaticValues.getControllerId(controllerName);
                deviceId=StaticValues.getDeviceId(deviceName, StaticValues.deviceMap.get(controllerId));
                deviceActionMode="D";
                description="DISABLE SCHEDULAR";
                startTime="-1";
                endTime="-1";
                startDate="-1";
                endDate="-1";
                schedularDeviceStatus="-1";
                new MyAsyncTask().execute();
                StaticValues.removeFromPosition(position);
                //StaticValues.schedularMap.remove(position+1);
                StaticValues.numberOfSchedules=StaticValues.numberOfSchedules-1;
                StaticValues.controllerName="Schedular";
                Intent reloadMainActivity = new Intent(context,MainActivity.class);
                context.startActivity(reloadMainActivity);
            }
        });

        return rowView;


    }

    public SchedularList(Activity context,
                         String[] scheduleDetails, Integer[] updateImageId, Integer[] deleteImageId) {
        super(context, R.layout.list_schedular, scheduleDetails);
        this.context = context;
        this.scheduleDetails = scheduleDetails;
        this.updateImageId = updateImageId;
        this.deleteImageId = deleteImageId;
    }

    private void initiatePopupWindow(final View v) {
        try {
            layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = layoutInflator.inflate(R.layout.timer,
                    (ViewGroup) v.findViewById(R.id.popup_timer));
            popupWindow = new PopupWindow(layout, 1000, 700, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            Toolbar toolbar = (Toolbar) layout.findViewById(R.id.mytoolbar);
            TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
            ImageButton closePopup = (ImageButton) toolbar.findViewById(R.id.close_popup);
            Switch toggleButton=(Switch) layout.findViewById(R.id.toggleButton);
            System.out.println("SCHEDULAR PRINT POSITION : " + pos);
            if("0".equals(StaticValues.schedularMap.get(pos+1).get("status"))){
                toggleButton.setChecked(false);
                schedularDeviceStatus="0";
            }
            if("1".equals(StaticValues.schedularMap.get(pos+1).get("status"))){
                toggleButton.setChecked(true);
                schedularDeviceStatus="1";

            }
            schedularDetails.put("status", schedularDeviceStatus);
            Button btnSaveTimer=(Button) layout.findViewById(R.id.saveTimer);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateobj = new Date();
            startDate=df.format(dateobj);
            endDate=df.format(dateobj);
            closePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    v.setSelected(false);
                    popupWindow.dismiss();
                    StaticValues.controllerName="Schedular";
                    Intent reloadMainActivity = new Intent(context,MainActivity.class);
                    context.startActivity(reloadMainActivity);
                }
            });
            textView.setText("TIME SCHEDULER DETAILS");
            etTimer=(EditText)layout.findViewById(R.id.editTime);
            startTime=StaticValues.schedularMap.get(pos+1).get("time");
            etTimer.setText(startTime);
            schedularDetails.put("time", startTime);
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
                            schedularDetails.put("time", startTime);
                        }
                    }, hour, minute, true);
                    mTimePicker.show();
                }
            });
            btnSaveTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deviceActionMode="U";
                    description="ENABLE SCHEDULAR";
                    StaticValues.schedularMap.put(pos+1,schedularDetails);
                    new SchedularList.MyAsyncTask().execute();
                    v.setSelected(false);
                    popupWindow.dismiss();
                    StaticValues.controllerName="Schedular";
                    Intent reloadMainActivity = new Intent(context,MainActivity.class);
                    context.startActivity(reloadMainActivity);
                }
            });
            if (toggleButton != null) {
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked) {
                            schedularDeviceStatus="1";
                        } else {
                            schedularDeviceStatus="0";
                        }
                        schedularDetails.put("status", schedularDeviceStatus);
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
        }
        @Override
        protected String doInBackground(Void... arg0) {

            if(deviceActionMode.equals("U") || deviceActionMode.equals("D")){
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
                        return StaticValues.deviceActionResponseIssue;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.deviceActionServiceDown;
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Toast.makeText(CustomList.this.getActivity(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if(deviceActionMode.equals("U")){
                myDb.updateScheduleData(controllerId, deviceId, schedularDeviceStatus, startTime, "OPEN");
                myDb.printControllerData(StaticValues.USERNAME);
                myDb.printDeviceData(StaticValues.USERNAME);
                myDb.printSchedularData(StaticValues.USERNAME);
            }
            if(deviceActionMode.equals("D")){
                myDb.deleteScheduleData(controllerId, deviceId);
                myDb.printControllerData(StaticValues.USERNAME);
                myDb.printDeviceData(StaticValues.USERNAME);
                myDb.printSchedularData(StaticValues.USERNAME);
            }
            //if (pDialog.isShowing())
            //  pDialog.dismiss();
        }
    }
}
