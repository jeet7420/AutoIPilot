package com.whizhomespilot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by smarhas on 3/7/2018.
 */

public class DummyActivity extends Fragment {
    String newControllerNumber, newControllerName, newPassKey, response;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_blank, container, false);
        Intent loginIntent=new Intent(DummyActivity.this.getActivity(),LoginActivity.class);
        DummyActivity.this.startActivity(loginIntent);
        // Inflate the layout for this fragment
        return view;
    }
}
