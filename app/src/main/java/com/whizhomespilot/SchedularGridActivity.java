package com.whizhomespilot;

import android.support.v4.app.Fragment;

/**
 * Created by smarhas on 5/20/2018.
 */

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SchedularGridActivity extends Fragment {
    Schedule schedule;
    GridView grid;
    String[] schedulesArray;
    Integer[] imagesArray;
    HashMap<String, String> schedulesMapLocal;
    int numberOfSchedules=0;
    String action="";
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_schedular, container, false);
        numberOfSchedules=StaticValues.schedules.size();
        System.out.println("NUMBER OF SCHEDULES : " + numberOfSchedules);
        System.out.println(StaticValues.schedules);
        if(numberOfSchedules>0){
            schedulesArray=new String[numberOfSchedules];
            imagesArray=new Integer[numberOfSchedules];
            position=1;
            Iterator iterator=StaticValues.schedules.iterator();

            while(iterator.hasNext()){
                System.out.println("SCHEDULE POSITION : " + position);
                //Map.Entry entry= (Map.Entry) iterator.next();
                //schedulesMapLocal=new HashMap<String, String>();
                schedule=new Schedule();
                schedule=(Schedule)iterator.next();
                //position=(int) entry.getKey();
                if("1".equals(schedule.getAction()))
                    action="On";
                if("0".equals(schedule.getAction()))
                    action="Off";
                schedulesArray[position-1]="Switch " + schedule.getControllerName() + " " + schedule.getDeviceName()
                        + " " + action + " at " + schedule.getTime();
                imagesArray[position-1]=R.drawable.cancel;
                position++;
            }
            CustomGrid adapter = new CustomGrid(SchedularGridActivity.this.getActivity(), schedulesArray, imagesArray);
            grid=(GridView)view.findViewById(R.id.grid);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(SchedularGridActivity.this, "You Clicked at " +schedule[+ position], Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}
