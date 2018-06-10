package com.whizhomespilot;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by smarhas on 6/2/2018.
 */

public class UserProfileActivity extends Fragment {
    String newPassword, confirmPassword, email, response;
    EditText etNewPassword, etConfirmPassword;
    TextView userName, userEmailId;
    ImageButton changePassword;
    Button savePopupDetails;
    DatabaseHelper myDb;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflator;
    JSONObject jsonResponse;
    private Context context;
    HashMap<String, String> postDataParams;
    private ProgressDialog pDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_profile, container, false);
        context=this.getActivity();
        myDb=new DatabaseHelper(context);
        StaticValues.userProfileMap=myDb.readUserProfileData(StaticValues.USERNAME);
        userName=(TextView)view.findViewById(R.id.userName);
        userEmailId=(TextView)view.findViewById(R.id.userEmailId);
        //changePassword=(ImageButton)view.findViewById(R.id.changePassword);

        /*changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopupWindow(view);
            }
        });*/

        userName.setText(StaticValues.userProfileMap.get(StaticValues.UserNameKey));
        userEmailId.setText(StaticValues.userProfileMap.get(StaticValues.UserEmailIdKey));

        email=userEmailId.getText().toString();

        return view;
    }

    private void initiatePopupWindow(final View v) {
        try {
            layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = layoutInflator.inflate(R.layout.change_password,
                    (ViewGroup) v.findViewById(R.id.popup_change_password));
            popupWindow = new PopupWindow(layout, 1000, 700, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 200);
            popupWindow.setFocusable(true);
            Toolbar toolbar = (Toolbar) layout.findViewById(R.id.mytoolbar);
            TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
            ImageButton closePopup = (ImageButton) toolbar.findViewById(R.id.close_popup);
            textView.setText("CHANGE PASSWORD");
            textView.setTextColor(Color.BLACK);
            closePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    v.setSelected(false);
                    popupWindow.dismiss();
                }
            });
            etNewPassword=(EditText)layout.findViewById(R.id.etNewPassword);
            etConfirmPassword=(EditText)layout.findViewById(R.id.etConfirmPassword);
            savePopupDetails=(Button) layout.findViewById(R.id.saveTimer);
            savePopupDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newPassword=etNewPassword.getText().toString();
                    confirmPassword=etConfirmPassword.getText().toString();
                    if("".equals(newPassword) || "".equals(confirmPassword)){
                        Toast.makeText(getActivity(), "Please enter all values", Toast.LENGTH_SHORT).toString();
                    }
                    else if(!(newPassword.equals(confirmPassword))){
                        Toast.makeText(getActivity(), "Password and confirm password should be same", Toast.LENGTH_SHORT).toString();
                    }
                    else
                        new MyAsyncTask().execute();
                }
            });
        }
         catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(Void... arg0) {
            try {
                HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                postDataParams = new HashMap<String, String>();
                postDataParams.put("email", email);
                postDataParams.put("password", newPassword);
                jsonResponse = httpurlConnection.invokeService(StaticValues.changePasswordURL, postDataParams);
                try {
                    response = jsonResponse.get("response").toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return StaticValues.changePasswordServiceDown;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return StaticValues.changePasswordResponseIssue;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
            if(result.equals("-1")){
                Toast.makeText(context, "INTERNAL ERROR. PLEASE TRY AGAIN", Toast.LENGTH_LONG).show();
            }
            if(result.equals("1")){
                Toast.makeText(context, "PASSWORD CHANGED SUCCESSFULLY", Toast.LENGTH_LONG).show();
            }
            if (pDialog.isShowing())
                pDialog.dismiss();
            popupWindow.dismiss();
        }
    }
}
