package com.whizhomespilot;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<String> drawerItems=new ArrayList<String>();
    public static List<String> drawerStaticItems=new ArrayList<String>();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String[] mControllerTitles;
    public static int navItemIndex = 0;
    private CharSequence mDrawerTitle;
    public String selectedDrawerItem;
    private ListView mDrawerList;
    private Context context;
    HashMap<String, String> postDataParams;
    private ProgressDialog pDialog;
    JSONObject jsonResponse;
    private PopupWindow popupWindow;
    String newPassword, confirmPassword, username, email, response;
    EditText etNewPassword, etConfirmPassword;
    Button savePopupDetails;
    private LayoutInflater layoutInflator;
    private CharSequence mTitle;
    private Handler mHandler;
    ImageButton editProfile;
    TextView name;
    int pos=0;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.colorTheme));

        context=this;
        myDb = new DatabaseHelper(context);

        if(!StaticValues.loginUsed){
            StaticValues.USERNAME=SaveSharedPreference.getUserName(MainActivity.this);
            StaticValues.userProfileMap=myDb.readUserProfileData(StaticValues.USERNAME);
            StaticValues.controllerMap=myDb.readControllerData(StaticValues.USERNAME);
            StaticValues.deviceMap=myDb.readDeviceData(StaticValues.USERNAME);
            StaticValues.schedules=myDb.readSchedularData(StaticValues.USERNAME);
            StaticValues.statusMap=myDb.readStatusData(StaticValues.USERNAME);
            StaticValues.securityMap=myDb.readSecurityData(StaticValues.USERNAME);
            StaticValues.topicMap=myDb.readTopicData(StaticValues.USERNAME);
        }

        myDb.printUserProfileData(StaticValues.USERNAME);
        myDb.printControllerData(StaticValues.USERNAME);
        myDb.printDeviceData(StaticValues.USERNAME);
        myDb.printSchedularData(StaticValues.USERNAME);
        myDb.printStatusData(StaticValues.USERNAME);
        myDb.printSecurityData(StaticValues.USERNAME);
        myDb.printTopicData(StaticValues.USERNAME);

        drawerItems.clear();
        StaticValues.controllerList.clear();
        mHandler = new Handler();
        mTitle = mDrawerTitle = getTitle();
        mControllerTitles = getResources().getStringArray(R.array.controller_array);
        drawerStaticItems= Arrays.asList(mControllerTitles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        LayoutInflater myinflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup)myinflater.inflate(R.layout.drawer_header, mDrawerList, false);
        mDrawerList.addHeaderView(myHeader, null, false);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);

        RoundedBitmapDrawable rounded =   RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        rounded.setCornerRadius(bitmap.getWidth());

        ImageView drawerProfile = (ImageView) mDrawerList.findViewById(R.id.drawer_profile_image);
        drawerProfile.setImageDrawable(rounded);

        editProfile=(ImageButton)mDrawerList.findViewById(R.id.editProfile);

        name=(TextView)mDrawerList.findViewById(R.id.name);

        StaticValues.userProfileMap=myDb.readUserProfileData(StaticValues.USERNAME);

        username=StaticValues.userProfileMap.get(StaticValues.UserNameKey);
        email=StaticValues.userProfileMap.get(StaticValues.UserEmailIdKey);

        System.out.println("NAME : " + username);

        name.setText(username.substring(0, username.indexOf(' ')));

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StaticValues.controllerList.size()>0){
                    StaticValues.controllerName=StaticValues.controllerList.get(0);
                    StaticValues.fragmentName=StaticValues.CONTROLLER;
                }
                else{
                    StaticValues.controllerName="";
                    StaticValues.fragmentName="";
                }
                getControllerFragment();
                mDrawerLayout.closeDrawer(mDrawerList);
                initiatePopupWindow(view);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if(StaticValues.controllerMap.size()!=0) {
            Iterator iteratorControllerMap = StaticValues.controllerMap.entrySet().iterator();
            while (iteratorControllerMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorControllerMap.next();
                StaticValues.controllerList.add(entry.getValue().toString());
            }
        }
        System.out.println("Controller ArrayList : " + StaticValues.controllerList);

        drawerItems.add(StaticValues.ADDNEWCONTROLLER);
        drawerItems.addAll(StaticValues.controllerList);
        drawerItems.addAll(drawerStaticItems);

        System.out.println("Final Drawer : " + drawerItems);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.sidemenubutton);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTheme)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText("AUTOI");
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(0,0,110,0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.sidemenubutton);
                //getSupportActionBar().setTitle(mTitle);
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow);
                //getSupportActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            if(StaticValues.isUserNew){
                navItemIndex = -1;
                StaticValues.controllerName="";
                getSelectedFragment();
            }
            else{
                if("".equals(StaticValues.controllerName)){
                    navItemIndex = 2;
                    StaticValues.controllerName=drawerItems.get(navItemIndex-1);
                }
                getSelectedFragment();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Uri uri = Uri.parse("http://autoiinnovations.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pos=position-1;
            navItemIndex=pos+1;
            System.out.println("position : " + pos);
            selectedDrawerItem=drawerItems.get(navItemIndex-1);
            if(selectedDrawerItem.equals(StaticValues.ADDNEWCONTROLLER) ||
               selectedDrawerItem.equals(StaticValues.EDITCONTROLLER) ||
               selectedDrawerItem.equals(StaticValues.USERPROFILE) ||
               selectedDrawerItem.equals(StaticValues.SCHEDULAR) ||
               selectedDrawerItem.equals(StaticValues.METRICS) ||
               selectedDrawerItem.equals(StaticValues.ABOUTUS) ||
               selectedDrawerItem.equals(StaticValues.CONTACTUS) ||
               selectedDrawerItem.equals(StaticValues.LOGOUT)){
                StaticValues.fragmentName=drawerItems.get(navItemIndex-1);
                StaticValues.controllerName="";
            }
            else{
                StaticValues.isUserNew=false;
                StaticValues.fragmentName=StaticValues.CONTROLLER;
                StaticValues.controllerName=drawerItems.get(navItemIndex-1);
            }
            System.out.println("Item Clicked at position : " + navItemIndex + " controller : " + StaticValues.controllerName);
            getSelectedFragment();
        }
    }

    public void getSelectedFragment(){
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                android.support.v4.app.Fragment fragment = getControllerFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_frame, fragment).commit();
                //fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(navItemIndex-1, true);
        //setTitle(mControllerTitles[navItemIndex-1]);
        setTitle(R.string.app_name);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    private android.support.v4.app.Fragment getControllerFragment() {
        if(StaticValues.flowContext.equals(StaticValues.ADDNEWCONTROLLER) ||
                StaticValues.flowContext.equals(StaticValues.EDITCONTROLLER)){
            mDrawerList.setItemChecked(drawerItems.indexOf(StaticValues.controllerName), true);
            StaticValues.flowContext="";
        }
        if(StaticValues.flowContext.equals(StaticValues.SCHEDULAR)){
            mDrawerList.setItemChecked(drawerItems.indexOf(StaticValues.SCHEDULAR), true);
            StaticValues.flowContext="";
        }
        if(StaticValues.isUserNew && "".equals(StaticValues.fragmentName)){
            BlankActivity blankActivity=new BlankActivity();
            return blankActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.ADDNEWCONTROLLER)){
            AddControllerActivity addControllerActivity = new AddControllerActivity();
            return addControllerActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.EDITCONTROLLER)){
            EditControllerActivity editControllerActivity = new EditControllerActivity();
            return editControllerActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.USERPROFILE)){
            UserProfileActivity userProfileActivity = new UserProfileActivity();
            return userProfileActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.SCHEDULAR)){
            SchedularGridActivity schedularActivity = new SchedularGridActivity();
            return schedularActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.METRICS)){
            MetricsDetailsActivity metricsDetailsActivity = new MetricsDetailsActivity();
            return metricsDetailsActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.ABOUTUS)){
            AboutUsActivity aboutUsActivity = new AboutUsActivity();
            return aboutUsActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.CONTACTUS)){
            ContactUsActivity contactUsActivity = new ContactUsActivity();
            return contactUsActivity;
        }
        else if(StaticValues.fragmentName.equals(StaticValues.LOGOUT)){
            DummyActivity dummyActivity = new DummyActivity();
            SaveSharedPreference.clearUserName(MainActivity.this);
            myDb.purgeUserProfileData(StaticValues.USERNAME);
            myDb.purgeControllerData(StaticValues.USERNAME);
            myDb.purgeDeviceData(StaticValues.USERNAME);
            myDb.purgeSchedularData(StaticValues.USERNAME);
            myDb.purgeStatusData(StaticValues.USERNAME);
            myDb.purgeSecurityData(StaticValues.USERNAME);
            myDb.purgeTopicData(StaticValues.USERNAME);
            StaticValues.fragmentName="";
            StaticValues.controllerName="";
            StaticValues.flowContext="";
            return dummyActivity;
        }
        else {
            ControllerActivity controllerActivity = new ControllerActivity();
            return controllerActivity;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initiatePopupWindow(final View v) {
        try {
            layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = layoutInflator.inflate(R.layout.change_password,
                    (ViewGroup) v.findViewById(R.id.popup_change_password));
            popupWindow = new PopupWindow(layout, 1000, 700, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 150);
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
                        System.out.println("123");
                        Toast.makeText(getApplicationContext(), "Please enter all values", Toast.LENGTH_SHORT).show();
                    }
                    else if(!(newPassword.equals(confirmPassword))){
                        System.out.println("890");
                        Toast.makeText(getApplicationContext(), "Password and confirm password should be same", Toast.LENGTH_SHORT).show();
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
