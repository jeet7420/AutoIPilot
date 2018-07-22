package com.whizhomespilot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by smarhas on 12/16/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    HashMap<String, String> postDataParams;
    private ProgressDialog pDialog;
    EditText etName, etPassword, etEmail, etPhone;
    String name, password, email, phone, homeId, response;
    JSONObject jsonResponse;
    ImageButton btnRegister;
    public static final String mode="C";
    DatabaseHelper myDb;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = RegisterActivity.this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(RegisterActivity.this,R.color.colorTeal));

        myDb = new DatabaseHelper(this);

        etName=(EditText)findViewById(R.id.etName);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etPhone=(EditText)findViewById(R.id.etPhone);

        btnRegister=(ImageButton)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=etName.getText().toString();
                password=etPassword.getText().toString();
                email=etEmail.getText().toString();
                phone=etPhone.getText().toString();

                if("".equals(name) || "".equals(password) || "".equals(email) || "".equals(phone)){
                    Toast.makeText(getApplicationContext(), "Please provide values for all fields", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.contains("@"))){
                    Toast.makeText(getApplicationContext(), "Invalid Email Id", Toast.LENGTH_SHORT).show();
                }
                else if(!(phone.length()==10)){
                        Toast.makeText(getApplicationContext(), "Phone number should be of 10 digits", Toast.LENGTH_SHORT).show();
                }
                else
                    new RegisterActivity.MyAsyncTask().execute();
            }
        });
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                postDataParams=new HashMap<String, String>();
                postDataParams.put("email",email);
                postDataParams.put("name",name);
                postDataParams.put("phone",phone);
                postDataParams.put("password",password);
                postDataParams.put("mode",mode);
                jsonResponse = httpurlConnection.invokeService(StaticValues.registerURL, postDataParams);
                try{
                    if(jsonResponse!=null){
                        response=jsonResponse.get("key").toString();
                        name=jsonResponse.get("userName").toString();
                        email=jsonResponse.get("email").toString();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.registerServiceResponseIssue;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return StaticValues.registerServiceDown;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if(result!=null){
                if(result.equals("-1")){
                    Toast.makeText(getApplicationContext(), "ISSUE WITH REGISTER SERVICE", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("-999")){
                    Toast.makeText(getApplicationContext(), "USER ALREADY REGISTERED", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("1")){
                    StaticValues.userProfileMap.put(StaticValues.UserNameKey,name);
                    StaticValues.userProfileMap.put(StaticValues.UserEmailIdKey,email);
                    if(StaticValues.userProfileMap!=null){
                        Iterator iteratorUserProfileMap=StaticValues.userProfileMap.entrySet().iterator();
                        while(iteratorUserProfileMap.hasNext()){
                            Map.Entry entry= (Map.Entry) iteratorUserProfileMap.next();
                            myDb.insertUserProfileData(entry.getKey().toString(), entry.getValue().toString());
                        }
                    }
                    myDb.printUserProfileData(StaticValues.USERNAME);
                    StaticValues.isUserNew=true;
                    StaticValues.fragmentName="";
                    Intent registerIntent=new Intent(RegisterActivity.this,MainActivity.class);
                    RegisterActivity.this.startActivity(registerIntent);
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "RESPONSE FROM SEVER IS NULL", Toast.LENGTH_LONG).show();
            }

            System.out.println(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
