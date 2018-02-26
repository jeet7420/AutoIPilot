package com.whizhomespilot;

/**
 * Created by smarhas on 1/13/2018.
 */
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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
import java.util.Iterator;

public class MetricsDisplayActivity extends AppCompatActivity {
    private CombinedChart mChart;
    protected String[] mMonths = new String[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display_metrics);
    Window window = MetricsDisplayActivity.this.getWindow();

    // clear FLAG_TRANSLUCENT_STATUS flag:
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    // finally change the color
    window.setStatusBarColor(ContextCompat.getColor(MetricsDisplayActivity.this,R.color.colorBlack));
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow);
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlack)));
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setCustomView(R.layout.actionbar);
    TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
    title.setText("WHIZ HOMES");
    title.setPadding(0,0,70,0);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    try {
        mChart = (CombinedChart) findViewById(R.id.chart1);
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
    }
    catch(Exception e) {
        e.printStackTrace();
    }
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
}
