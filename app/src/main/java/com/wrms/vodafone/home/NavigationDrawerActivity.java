package com.wrms.vodafone.home;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.wrms.vodafone.R;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.live_cotton.LiveCottonActivity;
import com.wrms.vodafone.mapfragments.AddFarmOnMap;
import com.wrms.vodafone.mapfragments.AdvisoryFragment;
import com.wrms.vodafone.mapfragments.BestMandiFragment;
import com.wrms.vodafone.mapfragments.CropFeasibilityFragment;
import com.wrms.vodafone.mapfragments.DashboardFragment;
import com.wrms.vodafone.mapfragments.ExistingFarms;
import com.wrms.vodafone.mapfragments.FarmAdvisoryFragment;
import com.wrms.vodafone.mapfragments.FarmMapFagment;
import com.wrms.vodafone.mapfragments.FarmReportFragment;
import com.wrms.vodafone.mapfragments.FarmerListFragment;
import com.wrms.vodafone.mapfragments.IrrigationFragment;
import com.wrms.vodafone.mapfragments.LatLonCellID;
import com.wrms.vodafone.mapfragments.LocateYoutFarmFragment;
import com.wrms.vodafone.mapfragments.MandiDetailFragment;
import com.wrms.vodafone.mapfragments.MandiFragment;
import com.wrms.vodafone.mapfragments.NutritionFragment;
import com.wrms.vodafone.mapfragments.OptimalMandiFragment;
import com.wrms.vodafone.mapfragments.VillageMapFagment;
import com.wrms.vodafone.mapfragments.VodafoneFragment;
import com.wrms.vodafone.mapfragments.WaterManagemnetFragment;
import com.wrms.vodafone.tubewell.DrawerLocker;
import com.wrms.vodafone.utils.AlarmReceiver;
import com.wrms.vodafone.utils.AppConstant;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerActivity extends AppCompatActivity implements
        DrawerLocker,
        NavigationView.OnNavigationItemSelectedListener,
        OptimalMandiFragment.OnFragmentInteractionListener,
        MandiFragment.OnFragmentInteractionListener,
        BestMandiFragment.OnFragmentInteractionListener,
        CropFeasibilityFragment.OnFragmentInteractionListener,
        NutritionFragment.OnFragmentInteractionListener,
        MandiDetailFragment.OnFragmentInteractionListener,
        FarmAdvisoryFragment.OnFragmentInteractionListener,
        FarmReportFragment.OnFragmentInteractionListener,
        LocateYoutFarmFragment.OnFragmentInteractionListener,
        AdvisoryFragment.OnFragmentInteractionListener,
        FarmerListFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        ExistingFarms.OnFragmentInteractionListener,
        WaterManagemnetFragment.OnFragmentInteractionListener,
        FarmMapFagment.OnFragmentInteractionListener,
        VillageMapFagment.OnFragmentInteractionListener{

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    int callingActivity;
    String selectedFarmName;
    String allDrawLatLngPoint;
    String area;
    String latitude, longitude, stateId;
    DBAdapter db;
    SharedPreferences prefs;

    Spinner districtSpinner;
    Spinner villageSpinner;
    private String cityArr[];
    String villageID = null;
    String vill_id = null;
    String lat = null;
    String lon = null;
    String villageName = null;
    String role = null;
    private PendingIntent pendingIntent;

    int MULTIPLE_PERMISSIONS = 7;
    String[] permissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS
    };

    String redirectData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        checkPermissions();

        Intent alarmIntent = new Intent(NavigationDrawerActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(NavigationDrawerActivity.this, 0, alarmIntent, 0);
        start();

        SharedPreferences prefs1 = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
        String la = prefs1.getString("lat",null);

        if (la!=null){

        }else {
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putString("lat", "19.6807");
            editor1.putString("lon", "75.9928");
            editor1.apply();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        TextView farmInfo = (TextView) findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Tubewell");
        farmInfo.setTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        db = new DBAdapter(NavigationDrawerActivity.this);
        db.open();

        latitude = ""+ LatLonCellID.lat;
        longitude = ""+LatLonCellID.lon;

        if (latitude==null || latitude.length()<5){
            latitude = "26.4148";
        }
        if (longitude==null || longitude.length()<5){
            longitude = "80.2321";
        }

     /*   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        redirectData = getIntent().getStringExtra("data");
        String redirect= getIntent().getStringExtra("irrigation");
        if (redirect!=null && redirect.equalsIgnoreCase("irri")){
            redirectData = redirect;
        }

        callingActivity = getIntent().getIntExtra("calling-activity", 0);
        selectedFarmName = getIntent().getStringExtra("FarmName");
        allDrawLatLngPoint = getIntent().getStringExtra("AllLatLngPount");
        area = getIntent().getStringExtra("Area");
        if(area==null){
            area = "0";
        }

        switch (callingActivity) {
            case AppConstant.HomeActivity:
                if (selectedFarmName != null) {
                    System.out.println("Selected Farm Name " + selectedFarmName);
                    Cursor cursor = db.getStateFromSelectedFarm(selectedFarmName);
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        latitude = cursor.getString(cursor.getColumnIndex(DBAdapter.CENTRE_LAT));
                        longitude = cursor.getString(cursor.getColumnIndex(DBAdapter.CENTRE_LON));
                        stateId = cursor.getString(cursor.getColumnIndex(DBAdapter.STATE_ID));
                        allDrawLatLngPoint = cursor.getString(cursor.getColumnIndex(DBAdapter.CONTOUR));
                        area = cursor.getString(cursor.getColumnIndex(DBAdapter.AREA));
                        System.out.println("Navigation drawer activity Area : "+area);
                    }
                    cursor.close();
                }
                break;
            case AppConstant.AddFarmMap: // this will call when you choose farm from the list
                //26.434334196791397,80.34493837505579-26.435601124064195,80.34630931913853-26.434692660565318,80.3474086895585-26.43347706155533,80.3462949022650
                if (allDrawLatLngPoint != null) {
                    String[] landPoints = allDrawLatLngPoint.split("-");
                    System.out.println("allDrawLatLngPoint : " + allDrawLatLngPoint);
                    int midPoint = landPoints.length / 2;
                    String[] latlng = landPoints[midPoint].split(",");
                    latitude = latlng[0];
                    longitude = latlng[1];
                    if (AppConstant.stateID != null) {
                        stateId = AppConstant.stateID;
                    }

                }
                break;
        }

        View headerView = (View) LayoutInflater.from(this).inflate(R.layout.nav_header_navigation_drawer, null);
        if(headerView!=null) {
            TextView accountinfo = (TextView) headerView.findViewById(R.id.user_profile);
            ImageView user_profile_pic = (ImageView) headerView.findViewById(R.id.user_profile_pic);
            if (accountinfo != null) {
                if(AppConstant.isLogin) {
                    accountinfo.setText(AppConstant.visible_Name);
                    user_profile_pic.setVisibility(View.VISIBLE);
                }else{
                    accountinfo.setText("Cotton Doctor");
                    user_profile_pic.setVisibility(View.INVISIBLE);
                }
            }
        }

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(headerView);
        navigationView.setNavigationItemSelectedListener(this);

        String role = AppConstant.role;
        Log.v("roleeeeeelllll",role+"");

        if (role!=null && role.equalsIgnoreCase("Admin")){
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.add_user_nav);
            target.setVisible(true);
        }else {
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.add_user_nav);
            target.setVisible(false);
        }

        if (role!=null && role.equalsIgnoreCase("client")){
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.formerlist_nav);
            target.setVisible(false);

        }else {
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.formerlist_nav);
            target.setVisible(true);

        }
        String  fromDraw =getIntent().getStringExtra("add");

        if (selectedFarmName!=null || fromDraw!=null) {

            Fragment fragment = LocateYoutFarmFragment.newInstance(String.valueOf(callingActivity), selectedFarmName, allDrawLatLngPoint, area);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }else {
           /* latitude = String.valueOf(LatLonCellID.currentLat);
            longitude = String.valueOf(LatLonCellID.currentLon);

            Fragment fragment = MandiFragment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();*/

            if (redirectData!=null && redirectData.equalsIgnoreCase("mand")){
                if (prefs == null) {
                    prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                }
                AppConstant.mobile_no = prefs.getString(AppConstant.PREFRENCE_KEY_MOBILE, "8285686540");
                AppConstant.role = prefs.getString(AppConstant.PREFRENCE_KEY_ROLE, "Admin");

                // latitude longitude selected from dashboard Village

                String l1 = prefs.getString("lat",null);
                String l2 = prefs.getString("lon",null);
                if (l1!=null){

                    latitude = l1;
                }
                if (l2!=null){

                    longitude = l2;
                }
                Fragment fragment = MandiFragment.newInstance(latitude, longitude);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            }else if (redirectData!=null && redirectData.equalsIgnoreCase("advi")){

                Fragment fragment = AdvisoryFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            }else if (redirectData!=null && redirectData.equalsIgnoreCase("irri")){

                Fragment fragment = IrrigationFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            }else if (redirectData!=null && redirectData.equalsIgnoreCase("allfarm")){

                Fragment fragment = FarmMapFagment.newInstance(latitude, longitude);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            }else if (redirectData!=null && redirectData.equalsIgnoreCase("tube")){

                Fragment fragment = VodafoneFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            }
            else {

                Fragment fragment = new DashboardFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            }
        }
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 42000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);


        // Toast.makeText(this, "Update Location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment frg = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (frg instanceof VodafoneFragment) {    // do something with f
                //  HomeActivity.this.finish();
               /* Intent in = new Intent(getApplicationContext(), AfterLoginActivity.class);

                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("check_moist","moist");
                startActivity(in);
                finish();*/

                exitMethod();

            } else {

                /*Fragment fragment = VillageMapFagment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();*/

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    Fragment fragment = VodafoneFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                }
            }

        }
    }

    private void exitMethod(){

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NavigationDrawerActivity.this);
        builder.setTitle("EXIT").
                setMessage("Do You want to exit?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                }).
                setNegativeButton("NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

          //  villageMethod();
            if (prefs == null) {
                prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
            }
            boolean isLogin = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
            SharedPreferences.Editor editor = prefs.edit();
            AppConstant.isLogin = false;
            editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, "");
            editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, "");
            editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
            editor.clear();
            editor.apply();

            Intent in = new Intent(getApplicationContext(),HomeActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();

            return true;
        }else if (id == R.id.action_error){
            File logFile = new File(Environment.getExternalStorageDirectory(), "tubewell.txt");
            if (logFile.exists()) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"vishal.tripathi@weather-risk.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logFile));
                // the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tubewell Error log");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "=Tubewell app");

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(this, "No email application is available to share error log file", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Tubewell ErrorLog file does not exist ", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        System.out.println("Farm Latitude " + latitude);
        System.out.println("Farm Longitude " + longitude);

        if (prefs == null) {
            prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
        }
        AppConstant.mobile_no = prefs.getString(AppConstant.PREFRENCE_KEY_MOBILE, "8285686540");
        AppConstant.role = prefs.getString(AppConstant.PREFRENCE_KEY_ROLE, "Admin");

        // latitude longitude selected from dashboard Village

        String l1 = prefs.getString("lat",null);
        String l2 = prefs.getString("lon",null);
        if (l1!=null){

            latitude = l1;
        }
        if (l2!=null){

            longitude = l2;
        }

        if (latitude==null || latitude.length()<5){
            latitude = "26.4148";
        }
        if (longitude==null || longitude.length()<5){
            longitude = "80.2321";
        }


        if (id == R.id.mandi_prices) {
            Fragment fragment = MandiFragment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        }if (id == R.id.voda_nav) {
            Fragment fragment = VodafoneFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        } else if (id == R.id.dash_nav) {
            Fragment fragment = new DashboardFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }
        else if (id == R.id.irrigation_nav) {
            Fragment fragment = IrrigationFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }
        else if (id == R.id.water_nav) {
            Fragment fragment = WaterManagemnetFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }
        else if (id == R.id.optimal_mandi) {
            Fragment fragment = OptimalMandiFragment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        }else if (id == R.id.best_mandi) {
            Fragment fragment = BestMandiFragment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        }else if (id == R.id.ur_farms_nav) {
            Fragment fragment = FarmMapFagment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        }else if (id == R.id.advisory_nav) {
            Fragment fragment = AdvisoryFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }else if (id == R.id.tag_farm_nav) {

            Intent intent = new Intent(getApplicationContext(), AddFarmOnMap.class);
            intent.putExtra("calling-activity", AppConstant.HomeActivity);
            intent.putExtra("lat", String.valueOf(LatLonCellID.currentLat));
            intent.putExtra("log", String.valueOf(LatLonCellID.currentLon));
            //intent.putExtra("hashMapValue", hashMap);
            startActivity(intent);


        }else if (id == R.id.exist_farm_nav) {
            Fragment fragment = ExistingFarms.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        }
        else if (id == R.id.add_user_nav) {
            addNewUser();
        }else if (id == R.id.formerlist_nav) {
            Fragment fragment = FarmerListFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }
        else if (id == R.id.live_cotton_nav) {
            Intent in = new Intent(getApplicationContext(),LiveCottonActivity.class);
            startActivity(in);
        }
        else if (id == R.id.cop_fiesibility) {
            Fragment fragment = CropFeasibilityFragment.newInstance(latitude, longitude, stateId);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        } else if (id == R.id.vulnerability) {

           /* Fragment fragment =new DiseaseAdviceFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
*/
        } else if (id == R.id.forecast_advisory) {

         /*   Fragment fragment =new ForecastFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
*/
        } else if (id == R.id.nutrition) {
            Fragment fragment = NutritionFragment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }else if (id == R.id.farm_report) {
            Fragment fragment = FarmReportFragment.newInstance(latitude, longitude);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        }else if (id == R.id.logout) {
            accountAlert();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void accountAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(AppConstant.visible_Name);
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (prefs == null) {
                    prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                }
                boolean isLogin = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
                SharedPreferences.Editor editor = prefs.edit();
                AppConstant.isLogin = false;
                editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, "");
                editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, "");
                editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
                editor.commit();


                Intent in = new Intent(getApplicationContext(),HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                finish();
            }
        });
        builder.show();
    }

   /* private void getState( String latitude, String longitude) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/YFIRest.svc/StateID/"+ latitude + "/" + longitude,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("State Volley Response : " + response);
                        try {
                            response = response.trim();
                            response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            JSONArray locationArray = new JSONArray(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(NavigationDrawerActivity.this, "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NavigationDrawerActivity.this, "Could not connect to the server", Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }*/

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }


    /*public void villageMethod(){

        final Dialog dialog = new Dialog(this);

        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.5f;

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        // Include dialog.xml file
        dialog.setContentView(R.layout.village_select_popup);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        districtSpinner = (Spinner)dialog.findViewById(R.id.popup_district);
        villageSpinner = (Spinner)dialog.findViewById(R.id.popup_village);
        Button okBTN = (Button)dialog.findViewById(R.id.ok_btn);
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (villageID!=null){
                    dialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(),"please select village.",Toast.LENGTH_SHORT).show();
                }

            }
        });

        ArrayList<String> districtList = new ArrayList<>();
        final ArrayList<String> districtID = new ArrayList<>();


        districtList.add("-Select-");
        districtList.add("Bhavnagar");
        districtList.add("Botad");
        districtList.add("Chhota Udaipur");
        //  districtList.add("Jagityal");
        //  districtList.add("Jalna");
        districtList.add("Jamnagar");
        districtList.add("Junagadh");
        districtList.add("Rajkot");
        districtList.add("Sabarkantha");
        districtList.add("Surendranagar");
        districtList.add("Vadodara");




        districtID.add("0");
        districtID.add("15841");
        districtID.add("16311");
        districtID.add("16441");
        // districtID.add("16321");
        // districtID.add("16032");
        districtID.add("15844");
        districtID.add("15845");
        districtID.add("15854");
        districtID.add("15855");
        districtID.add("15857");
        districtID.add("15859");


        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(NavigationDrawerActivity.this, android.R.layout.simple_spinner_item, districtList); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(varietyArrayAdapter);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    //    districtSpinner.setSelection(position);

                    String ID = districtID.get(position);
                    villageID = null;
                    vill_id = null;
                    lat = null;
                    lon = null;

                    loadVillagesData(ID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
*/
    ProgressDialog dialog;

    public void loadVillagesData(String ID) {
        dialog = ProgressDialog.show(NavigationDrawerActivity.this, "",
                "Fetching Villages. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley State Response : " + response);

                        response = response.trim();
                     //   response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");



                        DataBean bean = new DataBean();
                        bean = getEventTypeList( response);
                        ArrayList<VillageBean> cityList = new ArrayList<VillageBean>();
                        cityList = bean.getCityList();
                        cityArr = new String[cityList.size()];
                        for (int i = 0; i < cityList.size(); i++) {
                            cityArr[i] = cityList.get(i).getVilageName();
                        }

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(NavigationDrawerActivity.this, android.R.layout.simple_spinner_item, cityArr);
                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        villageSpinner.setAdapter(eventTypeAdapter);

                        final DataBean finalBean = bean;
                        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                villageID = finalBean.getCityList().get(position).getVillageID();
                                villageName = finalBean.getCityList().get(position).getVilageName();

                                Log.v("ksjkls",villageID);

                                vill_id = villageID;

                                SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor  = prefs.edit();
                                editor.putString("villageId",villageID);
                                editor.putString("villageName",villageName);
                                editor.apply();


                               /* if (villageID!=null){

                                    String[] parts = villageID.split("-");
                                    vill_id = parts[0];
                                    lat = parts[1];
                                    lon = parts[2];

                                    SharedPreferences prefs =getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor  = prefs.edit();
                                    editor.putString("lat",lat);
                                    editor.putString("lon",lon);
                                    editor.putString("villageId",villageID);
                                    editor.putString("villageName",villageName);
                                    editor.apply();
                                }*/
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public DataBean getEventTypeList(String response) {

        DataBean dataBean = new DataBean();
        ArrayList<VillageBean> eventTypeList = new ArrayList<VillageBean>();
        if (response != null){
            try{

                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() > 0) {

                }

                for (int i = 0; i < jsonArray.length(); i++) {

                    VillageBean typeBean = new VillageBean();
                    typeBean.setVilageName(jsonArray.getJSONObject(i).getString("Village_Final"));
                    typeBean.setVillageID(jsonArray.getJSONObject(i).getString("Id"));
                    eventTypeList.add(typeBean);

                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            dataBean.setCityList(eventTypeList);



        }

        return dataBean;
    }

    public void addNewUser() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_new_user, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText userNameET = (EditText) promptsView.findViewById(R.id.username_add);
        final EditText passwordET = (EditText) promptsView.findViewById(R.id.password_add);
        final EditText emailIdET = (EditText) promptsView.findViewById(R.id.email_id_add);
        final EditText phoneET = (EditText) promptsView.findViewById(R.id.phone_add);
        final Spinner roleSpinner = (Spinner) promptsView.findViewById(R.id.role_add_spinner);
        final Button submitBTN = (Button) promptsView.findViewById(R.id.submit_add);

        final ArrayList<String> roleArray = new ArrayList<String>();
        roleArray.add("select role");
        roleArray.add("Admin");
        roleArray.add("Moderater");
        roleArray.add("Expert");
        roleArray.add("Client");



        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roleArray);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roleSpinner.setAdapter(eventTypeAdapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position>0){
                    role = roleArray.get(position);
                }else {
                    role = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // dialog.dismiss();
                String userName = userNameET.getText().toString().trim();
                String pass = passwordET.getText().toString().trim();
                String email = emailIdET.getText().toString().trim();
                String phone = phoneET.getText().toString().trim();

                submitAdd(userName,pass,email,phone,dialog);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }

        });
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.show();



    }

    public void submitAdd(String uName,String pass,String email,String phone,DialogInterface dialog){

        if (uName==null || uName.length()<1){
            Toast.makeText(getApplicationContext(),"Please enter user name",Toast.LENGTH_SHORT).show();
        }else if (pass==null || pass.length()<1){
            Toast.makeText(getApplicationContext(),"Please enter valid password",Toast.LENGTH_SHORT).show();
        }else if (email==null || email.length()<1){
            Toast.makeText(getApplicationContext(),"Please enter valid Email id",Toast.LENGTH_SHORT).show();
        }else if (phone==null || phone.length()<8){
            Toast.makeText(getApplicationContext(),"Please enter valid phone number",Toast.LENGTH_SHORT).show();
        }else if (role==null || role.length()<1){
            Toast.makeText(getApplicationContext(),"Please select role",Toast.LENGTH_SHORT).show();
        }else {
            dialog.dismiss();
            createNewUser(uName,pass,email,phone,role);

        }

    }

    private void createNewUser(String name,String pass,String email,String phone,String role) {

        final ProgressDialog dialog = ProgressDialog.show(this, "", "Added new user. Please wait...", true);
        String userName = AppConstant.visible_Name;

        String url = "http://pdjalna.myfarminfo.com/PDService.svc/Register/" + email + "/" + name + "/" + pass + "/" + role+ "/" + phone;
        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        response = response.trim();
                    //    response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("response : " + response);
                        Toast.makeText(getApplicationContext(),"Added new user successfully",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                System.out.println("Volley Error : " + error);

            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
        }
    }
}
