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
 * Created by smarhas on 1/6/2018.
 */

public class BlankActivity extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_blank, container, false);
        ImageButton imageButton=(ImageButton)view.findViewById(R.id.addControllerButton);
        imageButton.setImageResource(R.drawable.add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticValues.isUserNew=false;
                StaticValues.controllerName=StaticValues.ADDNEWCONTROLLER;
                Intent reloadMainActivity = new Intent(BlankActivity.this.getActivity(),MainActivity.class);
                BlankActivity.this.getActivity().startActivity(reloadMainActivity);
            }
        });
        return view;
    }
}
