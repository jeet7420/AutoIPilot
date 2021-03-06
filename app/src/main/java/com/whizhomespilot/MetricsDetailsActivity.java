package com.whizhomespilot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by smarhas on 1/13/2018.
 */

public class MetricsDetailsActivity extends Fragment {
    Context context;
    List<String> deviceList=new ArrayList<String>();
    ArrayAdapter<String> roomsAdapter, devicesAdapter;
    HashMap<String, String> postDataParams;
    ImageButton btnSubmit;
    String selectedDevice, selectedYear;
    String controllerId, deviceId;
    String controllerType;
    JSONObject jsonResponse, jsonObject;
    String response="success";
    HashMap<String, String> deviceMapForSelectedController=new HashMap<String, String>();
    private CombinedChart mChart;
    protected String[] mMonths = new String[12];
    private String[] mYear;
    private String[] mControllerNames;
    ImageView chartImage;
    ListPopupWindow listPopupForRooms;
    ListPopupWindow listPopupForDevices;
    String selectedControllerId, selectedControllerName, selectedDeviceId, selectedDeviceName;
    HashMap<String,String> getDeviceMapForSelectedController=new HashMap<String,String>();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_select_details_for_metrics, container, false);
        context=this.getActivity();
        //mYear = getResources().getStringArray(R.array.year_array);
        btnSubmit=(ImageButton)view.findViewById(R.id.btnSubmit);
        mChart = (CombinedChart)view.findViewById(R.id.chart1);
        mChart.setVisibility(View.INVISIBLE);
        mChart.setEnabled(false);
        mChart.setTouchEnabled(false);
        View C = view.findViewById(R.id.chart1);
        ViewGroup parent = (ViewGroup) C.getParent();
        int index = parent.indexOfChild(C);
        parent.removeView(C);
        final ImageButton btnRooms=(ImageButton)view.findViewById(R.id.btnRooms);
        final ImageButton btnDevices=(ImageButton)view.findViewById(R.id.btnDevices);

        listPopupForRooms = new ListPopupWindow(this.getActivity());
        listPopupForDevices = new ListPopupWindow(this.getActivity());

        listPopupForRooms.setAnchorView(btnRooms);
        listPopupForDevices.setAnchorView(btnDevices);

        roomsAdapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.simple_spinner_dropdown_item, StaticValues.controllerList);
        listPopupForRooms.setAdapter(roomsAdapter);

        btnRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPopupForRooms.show();
            }
        });

        btnDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPopupForDevices.show();
            }
        });

        listPopupForRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StaticValues.deviceList.clear();
                selectedControllerName=StaticValues.controllerList.get(i);
                if("Bedroom".equals(selectedControllerName)) {
                    btnRooms.setImageResource(R.drawable.bedroomlov);
                }
                if("Hall".equals(selectedControllerName)) {
                    btnRooms.setImageResource(R.drawable.halllov);
                }
                if("Bathroom".equals(selectedControllerName)) {
                    btnRooms.setImageResource(R.drawable.bathroomlov);
                }
                if("Kitchen".equals(selectedControllerName)) {
                    btnRooms.setImageResource(R.drawable.kitchenlov);
                }
                listPopupForRooms.dismiss();
            }
        });

        listPopupForRooms.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                StaticValues.deviceList.clear();
                selectedControllerId=StaticValues.getControllerId(selectedControllerName);
                getDeviceMapForSelectedController=StaticValues.getDeviceMapForSelectedController(selectedControllerId);
                StaticValues.deviceList.addAll(getDeviceMapForSelectedController.values());
                devicesAdapter = new ArrayAdapter<String>(
                        context, android.R.layout.simple_spinner_dropdown_item, StaticValues.deviceList);
                listPopupForDevices.setAdapter(devicesAdapter);
            }
        });

        listPopupForDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDeviceName=StaticValues.deviceList.get(i);
                if("Fan".equals(selectedDeviceName)) {
                    btnDevices.setImageResource(R.drawable.fanlov);
                }
                if("Light".equals(selectedDeviceName)) {
                    btnDevices.setImageResource(R.drawable.lightlov);
                }
                if("Geysor".equals(selectedDeviceName)) {
                    btnDevices.setImageResource(R.drawable.geysorlov);
                }
                if("3-Pin".equals(selectedDeviceName)) {
                    btnDevices.setImageResource(R.drawable.pinlov);
                }
                selectedDeviceId=StaticValues.getDeviceId(selectedDeviceName, getDeviceMapForSelectedController);
                listPopupForDevices.dismiss();
            }
        });

        /*btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartImage.setEnabled(false);
                mChart.setEnabled(true);
                new MyAsyncTask().execute();
            }
        });*/
        // Inflate the layout for this fragment
        return view;
    }

    private LineData generateLineData() {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries = getLineEntriesData(entries);

        LineDataSet set = new LineDataSet(entries, "Line");
        //set.setColor(Color.rgb(240, 238, 70));
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private ArrayList<Entry> getLineEntriesData(ArrayList<Entry> entries){
        JSONObject tempData=StaticValues.metricsData;
        Iterator<?> keys = tempData.keys();
        int count=0;
        String floatData = null;
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            //   System.out.print(key);
            //  System.out.print(json.get(key));
            //  labels.add(key);
            try {
                floatData =tempData.get(key).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            entries.add(new Entry(count,Float.valueOf(floatData)));

            count++;
        }


        return entries;
    }

    private BarData generateBarData() {
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        entries = getBarEnteries(entries);

        BarDataSet set1 = new BarDataSet(entries, "Bar");
        //set1.setColor(Color.rgb(60, 220, 78));
        set1.setColors(ColorTemplate.COLORFUL_COLORS);
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        return d;
    }

    private ArrayList<BarEntry> getBarEnteries(ArrayList<BarEntry> entries){
        JSONObject tempData=StaticValues.metricsData;
        Iterator<?> keys = tempData.keys();
        int count=0;
        String floatData = null;
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            //   System.out.print(key);
            //  System.out.print(json.get(key));
            //labels.add(key);
            try {
                floatData =tempData.get(key).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            entries.add(new BarEntry(count,Float.valueOf(floatData)));

            count++;
        }

        return  entries;
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
                postDataParams = new HashMap<String, String>();
                postDataParams.put("controllerId", controllerId);
                postDataParams.put("deviceId", deviceId);
                postDataParams.put("year", selectedYear);
                jsonResponse = httpurlConnection.getMetrics(StaticValues.getMetricsURL, postDataParams);
                StaticValues.metricsData=jsonResponse;
                try{
                    //jsonObject=(JSONObject)jsonResponse.get("response");
                    //response=jsonObject.get("status").toString();
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

            try {
                mChart.getDescription().setText("This is testing Description");
                mChart.setBackgroundColor(Color.WHITE);
                mChart.setDrawGridBackground(true);
                mChart.setDrawBarShadow(true);
                mChart.setHighlightFullBarEnabled(true);

                // draw bars behind lines
                mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                });

                Legend l = mChart.getLegend();
                l.setWordWrapEnabled(true);
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

                YAxis rightAxis = mChart.getAxisRight();
                rightAxis.setDrawGridLines(false);
                rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setDrawGridLines(false);
                leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                xAxis.setAxisMinimum(0f);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {

                        JSONObject tempData = StaticValues.metricsData;
                        Iterator<?> keys = tempData.keys();
                        int count = 0;
                        String floatData = null;
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            //   System.out.print(key);
                            //  System.out.print(json.get(key));
                            mMonths[count] = key;
                            // labels.add(key);


                            count++;
                        }
                        return mMonths[(int) value % mMonths.length];
                    }
                });

                CombinedData data = new CombinedData();

                data.setData(generateLineData());
                data.setData(generateBarData());

                xAxis.setAxisMaximum(data.getXMax() + 0.25f);
                mChart.setData(data);
                mChart.invalidate();
                mChart.setVisibility(View.VISIBLE);
                mChart.setTouchEnabled(false);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            //Intent startMetricsDisplayActivity = new Intent(MetricsDetailsActivity.this.getActivity(), MetricsDisplayActivity.class);
            //MetricsDetailsActivity.this.getActivity().startActivity(startMetricsDisplayActivity);
            //if (pDialog.isShowing())
            //  pDialog.dismiss();
        }
    }
}
