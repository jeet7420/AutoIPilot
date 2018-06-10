package com.whizhomespilot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by smarhas on 12/16/2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    String name, firstname, lastname, email;
    HashMap<String, String> postDataParams;
    private static final int REQUEST_CODE = 10;
    EditText etUsername, etPassword;
    private ProgressDialog pDialog;
    private String controllerId;
    HashMap<String, String> innerMap;
    private ImageButton loginButton;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;
    String username, password, response;
    JSONObject jsonResponse, jsonObject;
    public static String mode="C";
    ImageButton btnLogin, btnForgotPassword;
    Context mContext;
    DatabaseHelper myDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        Window window = LoginActivity.this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.colorTeal));

        myDb = new DatabaseHelper(this);

        myDb.purgeControllerData(StaticValues.USERNAME);
        myDb.purgeDeviceData(StaticValues.USERNAME);
        myDb.purgeSchedularData(StaticValues.USERNAME);

        etUsername=(EditText)findViewById(R.id.etUsername);
        etPassword=(EditText)findViewById(R.id.etPassword);
        btnLogin=(ImageButton)findViewById(R.id.btnLogin);
        btnForgotPassword=(ImageButton)findViewById(R.id.btnForgotPassword);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=etUsername.getText().toString();
                password=etPassword.getText().toString();
                if("".equals(username) || "".equals(password)){
                    Toast.makeText(getApplicationContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
                }
                else{
                    mode="C";
                    StaticValues.USERNAME=username;
                    new MyAsyncTask().execute();
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=etUsername.getText().toString();
                if("".equals(username)){
                    Toast.makeText(getApplicationContext(), "Enter username then click on forgot password", Toast.LENGTH_SHORT).show();
                }
                else{
                    mode="FORGOT PASSWORD";
                    new MyAsyncTask().execute();
                }
            }
        });

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();



        loginButton = (ImageButton) findViewById(R.id.googlesignin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            try{
                name = account.getDisplayName();
                String str[] = name.split(" ");
                firstname = str[0];
                lastname = str[1];
                email = account.getEmail();
                StaticValues.USERNAME=email;
                Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
                mode="G";
                new MyAsyncTask().execute();
            }
            catch (Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
        }
    }

    public void registerAction(View view) {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        LoginActivity.this.startActivity(registerIntent);
    }

    public static HashMap<String, HashMap<String,String>> jsonToHashMap(JSONObject object) throws JSONException {
        HashMap<String, HashMap<String,String>> map = new HashMap<String, HashMap<String,String>>();
        HashMap<String, String> innerMap=null;
        Iterator<String> keysItrOuter = object.keys();
        while(keysItrOuter.hasNext()) {
            String keyOuter = keysItrOuter.next();
            Object valueOuter = object.get(keyOuter);
            JSONObject innerJsonObject = (JSONObject)valueOuter;
            Iterator<String> keysItrInner = innerJsonObject.keys();
            innerMap = new HashMap<String, String>();
            while(keysItrInner.hasNext()){
                String keyInner = keysItrInner.next();
                Object valueInner = innerJsonObject.get(keyInner);
                String valueInnerAsString = valueInner.toString();
                innerMap.put(keyInner, valueInnerAsString);
            }
            map.put(keyOuter, innerMap);
        }
        return map;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try{
                StaticValues.serverResult=new HashMap<String, HashMap<String,String>>();
                HTTPURLConnection httpurlConnection = new HTTPURLConnection();
                if(mode.equals("FORGOT PASSWORD")){
                    postDataParams=new HashMap<String, String>();
                    postDataParams.put("email",username);
                    jsonResponse = httpurlConnection.invokeService(StaticValues.sendTemporaryPassword, postDataParams);
                    StaticValues.serverResult=jsonToHashMap(jsonResponse);
                }
                if(mode.equals("C")){
                    postDataParams=new HashMap<String, String>();
                    postDataParams.put("email",username);
                    postDataParams.put("password",password);
                    jsonResponse = httpurlConnection.invokeService(StaticValues.loginURL, postDataParams);
                    StaticValues.serverResult=jsonToHashMap(jsonResponse);
                    //StaticValues.deviceStatus=StaticValues.serverResult.get("Devices");
                    System.out.println("CHECK 1 : " + StaticValues.serverResult.get("homeId"));
                    System.out.println("CHECK 2 : " + StaticValues.serverResult.get("controllers"));
                    System.out.println("CHECK 3 : " + StaticValues.serverResult.get("security"));
                    System.out.println("CHECK 4 : " + StaticValues.serverResult.get("topic"));
                    System.out.println("CHECK 5 : " + StaticValues.serverResult.get("deviceStatus"));
                    System.out.println("CHECK 6 : " + StaticValues.serverResult.get("profile"));
                }
                if(mode.equals("G")){
                    postDataParams=new HashMap<String, String>();
                    postDataParams.put("email",email);
                    postDataParams.put("name",name);
                    postDataParams.put("mode",mode);
                    jsonResponse = httpurlConnection.invokeService(StaticValues.googleSignInURL, postDataParams);
                    StaticValues.serverResult=jsonToHashMap(jsonResponse);
                    //StaticValues.deviceStatus=StaticValues.serverResult.get("Devices");
                    System.out.println("CHECK 1 : " + StaticValues.serverResult.get("homeId"));
                    System.out.println("CHECK 2 : " + StaticValues.serverResult.get("controllers"));
                    System.out.println("CHECK 3 : " + StaticValues.serverResult.get("security"));
                    System.out.println("CHECK 4 : " + StaticValues.serverResult.get("topic"));
                    System.out.println("CHECK 5 : " + StaticValues.serverResult.get("status"));
                    System.out.println("CHECK 6 : " + StaticValues.serverResult.get("profile"));
                }
                try{
                    jsonObject=(JSONObject)jsonResponse.get("homeId");
                    response=jsonObject.get("homeId").toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                    return StaticValues.loginServiceResponseIssue;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return StaticValues.loginServiceDown;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if(result!=null){
                if(result.equals("-1")){
                    Toast.makeText(getApplicationContext(), "ISSUE WITH LOGIN SERVICE", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("-999")){
                    Toast.makeText(getApplicationContext(), "INVALID CREDENTIALS", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("-777")){
                    Toast.makeText(getApplicationContext(), "INVALID EMAIL ID", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("200")){
                    Toast.makeText(getApplicationContext(), "NEW PASSWORD SENT TO EMAIL ID", Toast.LENGTH_LONG).show();
                }
                else{
                    if((StaticValues.serverResult.get("profile"))!=null){
                        StaticValues.userProfileMap=StaticValues.serverResult.get("profile");
                        Iterator iteratorUserProfileMap=StaticValues.userProfileMap.entrySet().iterator();
                        while(iteratorUserProfileMap.hasNext()){
                            Map.Entry entry= (Map.Entry) iteratorUserProfileMap.next();
                            myDb.insertUserProfileData(entry.getKey().toString(), entry.getValue().toString());
                        }
                        myDb.printUserProfileData(StaticValues.USERNAME);
                    }
                    StaticValues.controllerMap=StaticValues.serverResult.get("controllers");
                    if(StaticValues.controllerMap.size()==0){
                        StaticValues.isUserNew=true;
                    }
                    else{
                        Iterator iterator=StaticValues.controllerMap.entrySet().iterator();
                        while(iterator.hasNext()){
                            Map.Entry entry= (Map.Entry) iterator.next();
                            myDb.insertControllerData(entry.getKey().toString(), entry.getValue().toString());
                        }
                        Iterator iteratorControllerMap = StaticValues.controllerMap.entrySet().iterator();
                        while (iteratorControllerMap.hasNext()) {
                            Map.Entry entry = (Map.Entry)iteratorControllerMap.next();
                            StaticValues.deviceMap.put(entry.getKey().toString(), StaticValues.serverResult.get(entry.getKey().toString()));
                        }
                        Iterator iteratorDeviceMap = StaticValues.deviceMap.entrySet().iterator();
                        while (iteratorDeviceMap.hasNext()) {
                            Map.Entry entry = (Map.Entry)iteratorDeviceMap.next();
                            controllerId=entry.getKey().toString();
                            innerMap=new HashMap<String, String>();
                            innerMap=(HashMap<String, String>)entry.getValue();
                            Iterator iteratorInnerDeviceMap = innerMap.entrySet().iterator();
                            while(iteratorInnerDeviceMap.hasNext()){
                                Map.Entry entry1=(Map.Entry)iteratorInnerDeviceMap.next();
                                myDb.insertDeviceData(entry1.getKey().toString(), controllerId, entry1.getValue().toString());
                            }
                        }

                        StaticValues.securityMap=StaticValues.serverResult.get("security");
                        Iterator iteratorSecurityMap=StaticValues.securityMap.entrySet().iterator();
                        while(iteratorSecurityMap.hasNext()){
                            Map.Entry entry= (Map.Entry) iteratorSecurityMap.next();
                            myDb.insertSecurityData(entry.getKey().toString(), entry.getValue().toString());
                        }

                        StaticValues.topicMap=StaticValues.serverResult.get("topic");
                        Iterator iteratorTopicMap=StaticValues.topicMap.entrySet().iterator();
                        while(iteratorTopicMap.hasNext()){
                            Map.Entry entry= (Map.Entry) iteratorTopicMap.next();
                            myDb.insertTopicData(entry.getKey().toString(), entry.getValue().toString());
                        }

                        StaticValues.statusMap=StaticValues.serverResult.get("status");
                        Iterator iteratorStatusMap=StaticValues.statusMap.entrySet().iterator();
                        while(iteratorStatusMap.hasNext()){
                            Map.Entry entry= (Map.Entry) iteratorStatusMap.next();
                            myDb.insertStatusData(entry.getKey().toString(), entry.getValue().toString());
                        }

                        StaticValues.loginUsed=true;
                        myDb.printControllerData(StaticValues.USERNAME);
                        myDb.printDeviceData(StaticValues.USERNAME);
                        myDb.printSecurityData(StaticValues.USERNAME);
                        myDb.printTopicData(StaticValues.USERNAME);
                        myDb.printStatusData(StaticValues.USERNAME);
                    }
                    Toast.makeText(getApplicationContext(), "LOGIN SUCCESSFULL", Toast.LENGTH_LONG).show();
                    SaveSharedPreference.setUserName(mContext, StaticValues.USERNAME);
                    Intent loginIntent=new Intent(LoginActivity.this,MainActivity.class);
                    LoginActivity.this.startActivity(loginIntent);
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