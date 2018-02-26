package com.whizhomespilot;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by smarhas on 2/24/2018.
 */

public class SchedularList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] scheduleDetail;
    private final Integer[] updateImageId;
    private final Integer[] deleteImageId;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_schedular, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        final ImageButton imageButton1 = (ImageButton) rowView.findViewById(R.id.ib1);
        final ImageButton imageButton2 = (ImageButton) rowView.findViewById(R.id.ib2);
        txtTitle.setText(scheduleDetail[position]);
        imageButton1.setImageResource(updateImageId[position]);
        imageButton2.setImageResource(deleteImageId[position]);
        return rowView;
    }

    public SchedularList(Activity context,
                         String[] scheduleDetail, Integer[] updateImageId, Integer[] deleteImageId) {
        super(context, R.layout.list_schedular, scheduleDetail);
        this.context = context;
        this.scheduleDetail = scheduleDetail;
        this.updateImageId = updateImageId;
        this.deleteImageId = deleteImageId;
    }
}
