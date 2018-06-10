package com.whizhomespilot;

/**
 * Created by smarhas on 5/20/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

public class CustomGrid extends BaseAdapter{
    private Context mContext;
    private final String[] schedules;
    private final Integer[] Imageid;
    private String deviceId, name, controllerId, signal,
            response, controllerName, scheduleDetail, str1, deviceName;
    public String deviceActionMode="D";
    public String schedularDeviceStatus="0";
    public String startDate="";
    public String startTime="";
    public String endDate="";
    public String endTime="";
    public String description="TEST SCHEDULAR";
    HashMap<String, String> postDataParams;
    DatabaseHelper myDb;
    JSONObject jsonResponse;
    Schedule schedule;
    int count=0;

    public CustomGrid(Context c,String[] schedules,Integer[] Imageid ) {
        mContext = c;
        this.schedules = schedules;
        this.Imageid = Imageid;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return schedules.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myDb = new DatabaseHelper(mContext);
        if (convertView == null) {

            grid = inflater.inflate(R.layout.grid_single, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageButton removeSchedule=(ImageButton)grid.findViewById(R.id.removeSchedule);
            textView.setText(schedules[position]);
            removeSchedule.setImageResource(Imageid[position]);
            removeSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Schedule schedule=StaticValues.getScheduleAtPosition(position);
                    if(schedule!=null){
                        controllerName=schedule.getControllerName();
                        deviceName=schedule.getDeviceName();
                        controllerId=StaticValues.getControllerId(controllerName);
                        deviceId=StaticValues.getDeviceId(deviceName, StaticValues.deviceMap.get(controllerId));
                        deviceActionMode="D";
                        description="DISABLE SCHEDULAR";
                        startTime="-1";
                        endTime="-1";
                        startDate="-1";
                        endDate="-1";
                        schedularDeviceStatus="-1";
                        System.out.println("AAAA : " + controllerName);
                        StaticValues.schedules.remove(schedule);
                        StaticValues.fragmentName=StaticValues.SCHEDULAR;
                        StaticValues.controllerName="";
                        StaticValues.flowContext=StaticValues.SCHEDULAR;
                        myDb.deleteScheduleData(controllerId, deviceId);
                        new CustomGrid.MyAsyncTask().execute();
                        Intent reloadMainActivity = new Intent(mContext,MainActivity.class);
                        mContext.startActivity(reloadMainActivity);
                    }
                }
            });
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
    private class MyAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... arg0) {

            if(deviceActionMode.equals("D")){
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
            System.out.println(result);
            myDb.printControllerData(StaticValues.USERNAME);
            myDb.printDeviceData(StaticValues.USERNAME);
            myDb.printSchedularData(StaticValues.USERNAME);
        }
    }
}
