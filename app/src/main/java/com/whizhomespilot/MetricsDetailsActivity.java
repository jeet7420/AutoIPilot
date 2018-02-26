package com.whizhomespilot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
    ArrayAdapter<String> adapter;
    HashMap<String, String> postDataParams;
    Spinner spinnerController, spinnerDevice, spinnerYear;
    Button btnSubmit;
    String selectedController=" ";
    String selectedDevice, selectedYear;
    String controllerId, deviceId;
    JSONObject jsonResponse, jsonObject;
    String response="success";
    HashMap<String, String> deviceMapForSelectedController=new HashMap<String, String>();
    private CombinedChart mChart;
    protected String[] mMonths = new String[12];
    private String[] mYear;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_select_details_for_metrics, container, false);
        context=this.getActivity();
        spinnerController=(Spinner)view.findViewById(R.id.spinnerController);
        spinnerDevice=(Spinner)view.findViewById(R.id.spinnerDevice);
        spinnerYear=(Spinner)view.findViewById(R.id.spinnerYear);
        spinnerController.setPrompt("SELECT CONTROLLER");
        spinnerDevice.setPrompt("SELECT DEVICE");
        spinnerYear.setPrompt("SELECT YEAR");
        mYear = getResources().getStringArray(R.array.year_array);
        adapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.select_dialog_singlechoice, mYear);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
        btnSubmit=(Button)view.findViewById(R.id.btnSubmit);
        mChart = (CombinedChart)view. findViewById(R.id.chart1);
        mChart.setVisibility(View.INVISIBLE);
        mChart.setTouchEnabled(false);
        adapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.select_dialog_singlechoice, StaticValues.controllerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerController.setAdapter(adapter);

        spinnerController.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deviceList.clear();
                selectedController=spinnerController.getSelectedItem().toString();
                controllerId=StaticValues.getControllerId(selectedController);
                deviceMapForSelectedController=StaticValues.getDeviceMapForSelectedController(controllerId);
                Iterator iteratorDeviceMapForSelectedController = deviceMapForSelectedController.entrySet().iterator();
                while (iteratorDeviceMapForSelectedController.hasNext()) {
                    Map.Entry entry = (Map.Entry)iteratorDeviceMapForSelectedController.next();
                    deviceList.add(entry.getValue().toString());
                }
                //deviceList=new ArrayList<String>(StaticValues.deviceMap.get(selectedController).values());

                adapter = new ArrayAdapter<String>(
                        context, android.R.layout.select_dialog_singlechoice, deviceList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDevice.setAdapter(adapter);
                System.out.println("SELECTED CONTROLLER : " + selectedController);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDevice=spinnerDevice.getSelectedItem().toString();
                deviceId=StaticValues.getDeviceId(selectedDevice, deviceMapForSelectedController);
                System.out.println("SELECTED DEVICE : " + selectedDevice);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear=spinnerYear.getSelectedItem().toString();
                System.out.println("SELECTED YEAR : " + selectedYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTask().execute();
            }
        });
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
