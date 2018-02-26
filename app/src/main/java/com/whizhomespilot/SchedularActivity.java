package com.whizhomespilot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by smarhas on 2/24/2018.
 */

public class SchedularActivity extends Fragment {
    int numberOfSchedules=0;
    String[] scheduleDetail;
    Integer[] updateImageId;
    Integer[] deleteImageId;
    HashMap<String, String> scheduleDetails;
    ListView list;
    int position;
    String action="";
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        numberOfSchedules=StaticValues.numberOfSchedules;
        View view=inflater.inflate(R.layout.activity_schedular, container, false);
        if(numberOfSchedules>0){
            scheduleDetail=new String[numberOfSchedules];
            updateImageId=new Integer[numberOfSchedules];
            deleteImageId=new Integer[numberOfSchedules];

            Iterator iterator=StaticValues.schedularMap.entrySet().iterator();

            while(iterator.hasNext()){
                Map.Entry entry= (Map.Entry) iterator.next();
                scheduleDetails=new HashMap<String, String>();
                scheduleDetails=(HashMap<String, String>) entry.getValue();
                position=(int) entry.getKey();
                if("1".equals(scheduleDetails.get("status")))
                    action="ON";
                if("0".equals(scheduleDetails.get("status")))
                    action="OFF";
                scheduleDetail[position-1]="Bedroom" + " : " + scheduleDetails.get("deviceName")
                        + "       " + action + "       " + scheduleDetails.get("time");
                updateImageId[position-1]=R.drawable.deviceoff;
                deleteImageId[position-1]=R.drawable.deviceoff;
            }
            SchedularList adapter = new
                    SchedularList(SchedularActivity.this.getActivity(), scheduleDetail, updateImageId, deleteImageId);
            list=(ListView)view.findViewById(R.id.list);
            list.setAdapter(adapter);
        }
        return view;
    }
}
