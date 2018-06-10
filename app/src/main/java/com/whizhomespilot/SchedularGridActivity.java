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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SchedularGridActivity extends Fragment {
    Schedule schedule;
    GridView grid;
    String[] schedulesArray;
    Integer[] imagesArray;
    int numberOfSchedules=0;
    TextView tvNoSchedules;
    String action="";
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_schedular, container, false);
        tvNoSchedules=(TextView)view.findViewById(R.id.tvNoSchedules);
        numberOfSchedules=StaticValues.schedules.size();
        System.out.println("NUMBER OF SCHEDULES : " + numberOfSchedules);
        System.out.println(StaticValues.schedules);
        if(numberOfSchedules>0){
            tvNoSchedules.setVisibility(View.GONE);
            schedulesArray=new String[numberOfSchedules];
            imagesArray=new Integer[numberOfSchedules];
            position=1;
            Iterator iterator=StaticValues.schedules.iterator();

            while(iterator.hasNext()){
                System.out.println("SCHEDULE POSITION : " + position);
                schedule=new Schedule();
                schedule=(Schedule)iterator.next();
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
        }
        return view;
    }
}
