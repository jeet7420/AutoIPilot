package com.whizhomespilot;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ListView mDrawerList;
    private CharSequence mTitle;
    private Handler mHandler;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("TEST3");
        setContentView(R.layout.activity_main);

        System.out.println("Inside Main Activity");
        Window window = MainActivity.this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.colorTheme));
        myDb = new DatabaseHelper(this);
        if(!StaticValues.loginUsed){
            StaticValues.USERNAME=SaveSharedPreference.getUserName(MainActivity.this);
            StaticValues.controllerMap=myDb.readControllerData(StaticValues.USERNAME);
            StaticValues.deviceMap=myDb.readDeviceData(StaticValues.USERNAME);
            //StaticValues.schedularMap=myDb.readSchedularData(StaticValues.USERNAME);
            StaticValues.statusMap=myDb.readStatusData(StaticValues.USERNAME);
            StaticValues.securityMap=myDb.readSecurityData(StaticValues.USERNAME);
            StaticValues.topicMap=myDb.readTopicData(StaticValues.USERNAME);
        }
        myDb.printControllerData(StaticValues.USERNAME);
        myDb.printDeviceData(StaticValues.USERNAME);
        myDb.printStatusData(StaticValues.USERNAME);
        //myDb.printSchedularData(StaticValues.USERNAME);
        drawerItems.clear();
        StaticValues.controllerList.clear();
        StaticValues.controllerList.add(StaticValues.ADDNEWCONTROLLER);
        mHandler = new Handler();
        mTitle = mDrawerTitle = getTitle();
        mControllerTitles = getResources().getStringArray(R.array.controller_array);
        drawerStaticItems= Arrays.asList(mControllerTitles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        if(StaticValues.controllerMap.size()!=0) {
            Iterator iteratorControllerMap = StaticValues.controllerMap.entrySet().iterator();
            while (iteratorControllerMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorControllerMap.next();
                StaticValues.controllerList.add(entry.getValue().toString());
            }
        }
        System.out.println("Controller ArrayList : " + StaticValues.controllerList);
        /*StaticValues.controllerList.add("Master Bedroom");
        StaticValues.controllerList.add("Child Bedroom");
        StaticValues.controllerList.add("Hall Room");*/
        drawerItems.addAll(StaticValues.controllerList);
        drawerItems.addAll(drawerStaticItems);

        System.out.println("Final Drawer : " + drawerItems);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        //getSupportActionBar().setLogo(R.drawable.smb);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.sidemenubutton);
        //getSupportActionBar().setTitle(R.string.main_activity_title);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTheme)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView title=(TextView)findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText("AUTOI");
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(0,0,110,0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
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
                StaticValues.controllerName="NO CONTROLLER";
                getSelectedFragment();
            }
            else{
                if("".equals(StaticValues.controllerName) || "Logout".equals(StaticValues.controllerName)){
                    navItemIndex = 1;
                    StaticValues.controllerName=drawerItems.get(navItemIndex);
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
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
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
            navItemIndex=position+1;
            System.out.println("position : " + position);
            StaticValues.controllerName=drawerItems.get(navItemIndex-1);
            StaticValues.isUserNew=false;
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
        if(StaticValues.isUserNew){
            BlankActivity blankActivity=new BlankActivity();
            return blankActivity;
        }
        else if(StaticValues.controllerName.equals(StaticValues.ADDNEWCONTROLLER)){
            AddControllerActivity addControllerActivity = new AddControllerActivity();
            return addControllerActivity;
        }
        else if(StaticValues.controllerName.equals(StaticValues.EDITCONTROLLER)){
            EditControllerActivity editControllerActivity = new EditControllerActivity();
            return editControllerActivity;
        }
        else if(StaticValues.controllerName.equals("User Profile")){
            UserProfileActivity userProfileActivity = new UserProfileActivity();
            return userProfileActivity;
        }
        else if(StaticValues.controllerName.equals("Schedular")){
            SchedularGridActivity schedularActivity = new SchedularGridActivity();
            return schedularActivity;
        }
        else if(StaticValues.controllerName.equals("Metrics")){
            MetricsDetailsActivity metricsDetailsActivity = new MetricsDetailsActivity();
            return metricsDetailsActivity;
        }
        else if(StaticValues.controllerName.equals("Logout")){
            DummyActivity dummyActivity = new DummyActivity();
            //BlankActivity blankActivity=new BlankActivity();
            SaveSharedPreference.clearUserName(MainActivity.this);
            myDb.purgeControllerData(StaticValues.USERNAME);
            myDb.purgeDeviceData(StaticValues.USERNAME);
            myDb.purgeSchedularData(StaticValues.USERNAME);
            myDb.purgeStatusData(StaticValues.USERNAME);
            myDb.purgeUserProfileData(StaticValues.USERNAME);
            return dummyActivity;
        }
        else if(StaticValues.controllerName.equals("About Us")){
            AboutUsActivity aboutUsActivity = new AboutUsActivity();
            return aboutUsActivity;
        }
        else if(StaticValues.controllerName.equals("Contact Us")){
            ContactUsActivity contactUsActivity = new ContactUsActivity();
            return contactUsActivity;
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
}
