package com.wrms.vodafone.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
import com.wrms.vodafone.adapter.MultiselectionAdapter;
import com.wrms.vodafone.bean.CropBean;
import com.wrms.vodafone.bean.FarmerBean;
import com.wrms.vodafone.bean.MultiBean;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.AllFarmDetail;
import com.wrms.vodafone.entities.CropQueryData;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.FarmInformationData;
import com.wrms.vodafone.entities.LocationData;
import com.wrms.vodafone.entities.Register;
import com.wrms.vodafone.entities.SignInData;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.mapfragments.AddFarmOnMap;
import com.wrms.vodafone.mapfragments.LatLonCellID;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.utils.ConnectionDetector;
import com.wrms.vodafone.utils.LocationPollReceiver;
import com.wrms.vodafone.utils.PlaceDetailsJSONParser;
import com.wrms.vodafone.utils.PlaceJSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    Context context = this;
    public static final String LOCATION = "location";
    LinearLayout visibleLoginDetail;
    LinearLayout Invisible;
    LinearLayout visibleOrText;
    TextView loginUserName;
    private Toolbar toolbar;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
    Button tell_me_more;
    Button sign_Up;
    LocationData locationData;
    int selected = 0;
    DBAdapter db;
    EditText username;
    EditText password;
    Button signIn;
    private Register data;
    private EditText result;
    Spinner chooseYourFarmSpiner;
    FarmInformationData farmInformationData;
    public static boolean status = true;
    HashMap<String, String> pickCropIdOrValue;
    double latitude;
    double longitude;
    public static String syncFor = AppConstant.STATE_ID;
    public String syncMsg = "Syncronizing " + AppConstant.STATE_ID;
    int syncCount = 1;
    HashMap<String, String> hashMap;
    int callingMethod;
    String storeCurrentStateId = "noValue";
    SharedPreferences prefs;
    Intent service;
    private Menu menu;
    TextView loginStatus;

    Button mandiInfo;


    private String cityArr[];
    String villageID = null;
    String vill_id = null;

    String villageName = null;

    Spinner villageSpinner, districtSpinner, farmerSpinner;
    EditText cropSpinner;
    ArrayList<MultiBean> multiArray;
    ArrayList<CropBean> cropList;
    ArrayList<String> idSelectedList;
    MultiselectionAdapter multiAdapter;
    ListView listView1;
    String resultstr;
    ArrayList<FarmerBean> farmerList;
    String farmerID = null;
    String farmerName = null;
    String farmer_id = null;
    String lat_farmer = null;
    String lon_farmer = null;


    String vil_id, vil_lati, vil_longi, vil_name, userID;

    String device_id = null;
    String deviceToken = null;

    SharedPreferences prefs1 = null;

    String major_res = null, minor_res = null, build_res = null;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

      //  checkPermissions();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            NOGPSDialog(this);
        }

    /*    if (!checkPermissionSMS()) {
            return;
        }
*/


        Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        //  in.putExtra("data", "mand");
        in.putExtra("data", "tube");
        startActivity(in);
        finish();


       /* ConnectionDetector con = new ConnectionDetector(this);
        if (con.isConnectingToInternet()) {
            getUpdateAPK();
        }

        hashMap = new HashMap<>();
        db = new DBAdapter(this);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        TextView farmInfo = (TextView) findViewById(R.id.logo);
        tell_me_more = (Button) findViewById(R.id.tell_me_more);
        sign_Up = (Button) findViewById(R.id.sign_up);
        visibleLoginDetail = (LinearLayout) findViewById(R.id.visibleLoginDetails);
        Invisible = (LinearLayout) findViewById(R.id.llInvisible);
        visibleOrText = (LinearLayout) findViewById(R.id.showORtext);
        loginUserName = (TextView) findViewById(R.id.loginUserName);
        chooseYourFarmSpiner = (Spinner) findViewById(R.id.chooseFarmSpinner);
        loginStatus = (TextView) findViewById(R.id.loginStatus);

        mandiInfo = (Button) findViewById(R.id.mandi_info_btn);
        mandiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(in);
            }
        });


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Tubewell");
        setSupportActionBar(toolbar);                    // Setting toolbar as the ActionBar with setSupportActionBar() call
        //  getSupportActionBar().setDisplayShowTitleEnabled(false); //Make the default lable invisible
        db.open();
        Cursor cursor = db.getAllStates();
        Cursor allCrop = db.getAllCropVariety();
        if ((!(cursor.getCount() > 0)) && (!(allCrop.getCount() > 0))) {
            loadData();
        }

        Cursor getAllUser = db.getAllCredentials();
        if (getAllUser.getCount() > 0) {
            getAllUser.moveToFirst();
            do {
                System.out.println("User Name : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.USER_NAME)));
                System.out.println("Visible Name : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.VISIBLE_NAME)));
                System.out.println("Password : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.PASSWORD)));
                System.out.println("Email : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.EMAIL_ADDRESS)));
                System.out.println("Created Date Time : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.CREATED_DATE_TIME)));
                System.out.println("User id : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.USER_ID)));
                System.out.println("Sending Status : " + getAllUser.getString(getAllUser.getColumnIndex(DBAdapter.SENDING_STATUS)));

            } while (getAllUser.moveToNext());
        }
        db.close();

        //////////////////////////////////////////////////////////////////////////////
        PollReceiver.scheduleAlarms(this);

        if (prefs == null) {
            prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
        }

        AppConstant.mobile_no = prefs.getString(AppConstant.PREFRENCE_KEY_MOBILE, "8285686540");
        AppConstant.role = prefs.getString(AppConstant.PREFRENCE_KEY_ROLE, "Admin");

        boolean isLogin = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
        AppConstant.isLogin = isLogin;
        updateMenuTitles(isLogin);
        if (isLogin) {

            AppConstant.user_id = prefs.getString(AppConstant.PREFRENCE_KEY_USER_ID, "");
            System.out.println("Got User id Home : " + AppConstant.user_id);
            AppConstant.visible_Name = prefs.getString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, "");
        }

        *//*service = new Intent(this, LatLonCellID.class);
        startService(service);*//*

        LocationPollReceiver.scheduleAlarms(HomeActivity.this);


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            NOGPSDialog(this);
        }


        //////////////////////////////////////////////////////////////////////////////

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.sign_in);
        data = new Register();

        if (AppConstant.isLogin) {
            showLoginView();

            Log.d("LoginStatus:", "" + AppConstant.isLogin);
        }
        Log.d("LoginStatus:", "" + AppConstant.isLogin);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("signin AppConstant.APP_MODE == AppConstant.OFFLINE : " + (AppConstant.APP_MODE == AppConstant.OFFLINE));

                goFarSignIn();

                *//*if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                    goFarSignIn();
                } else if (AppManager.isOnline(HomeActivity.this)) {
                    goFarSignIn();
                } else {
                    Toast.makeText(HomeActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
                }*//*
            }
        });

        getAllFarmName(); //This will get all farm name from the database and set the farm list in spinnerView
        this.chooseYourFarmSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!chooseYourFarmSpiner.getSelectedItem().toString().equals("Select")) {
                    db.open();
                    Cursor c = db.getStateFromSelectedFarm(chooseYourFarmSpiner.getSelectedItem().toString());
                    if (c.moveToFirst()) {
                        do {
                            AppConstant.stateID = c.getString(c.getColumnIndex(DBAdapter.STATE_ID));
                            String contour = c.getString(c.getColumnIndex(DBAdapter.CONTOUR));
                            getAtLeastOneLatLngPoint(contour);
                        }
                        while (c.moveToNext());
                    }
                    db.close();
                    callingMethod = AppConstant.selectForm;
                    if (storeCurrentStateId.contains(AppConstant.stateID)) {
                        openActivity();
                    } else {
                        syncCount = 2;
                        syncFor = AppConstant.CROP_INITIAL;
                        syncMsg = "Syncronizing " + AppConstant.CROP_INITIAL;
                        new appDataSetting().execute();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //display user name here that signing
        sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomeActivity.this, SignUpActivity.class);
                startActivity(i);

            }
        });

        selected = 0;
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.farm_location);
        atvPlaces.setThreshold(1);

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4) {
                    placesTask = new PlacesTask();
                    placesTask.execute(s.toString());
                    if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                        if (selected == 1) {
                            try {
                                atvPlaces.dismissDropDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                atvPlaces.showDropDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                    try {
                        if (selected == 1) {
                            try {
                                atvPlaces.dismissDropDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                atvPlaces.showDropDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//
        atvPlaces.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                    if (selected == 1) {
                        try {
                            atvPlaces.dismissDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            atvPlaces.showDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });

        if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
            tell_me_more.setText("CREATE FARM");
            atvPlaces.setVisibility(View.GONE);

        } else {
            tell_me_more.setText("TELL ME MORE");
            atvPlaces.setVisibility(View.VISIBLE);
        }

        tell_me_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                    if (AppConstant.isLogin) {
                        Intent intent = new Intent(HomeActivity.this, AddFarmWithoutMap.class);
                        intent.putExtra("calling-activity", AppConstant.HomeActivity);
                        intent.putExtra("lat", String.valueOf(LatLonCellID.currentLat));
                        intent.putExtra("log", String.valueOf(LatLonCellID.currentLon));
                        intent.putExtra("hashMapValue", hashMap);
                        startActivity(intent);
                    }
                } else {*//*
                    *//*callingMethod = AppConstant.tellMeMore; //this will observe the method is calling from here when you redirect next activity
                    locationData = new LocationData();
                    openActivity();*//*
                if (locationData != null && locationData.getLatitude() != null) {
                    Log.d("HomeActivity", "" + locationData);
                    String lat = locationData.getLatitude();
                    String lon = locationData.getLongitude();
                    callingMethod = AppConstant.tellMeMore; //this will observe the method is calling from here when you redirect next activity
                    syncCount = 1;
                    syncFor = AppConstant.STATE_ID;
                    syncMsg = "Syncronizing " + AppConstant.STATE_ID;
                    new appDataSetting().execute();
                    selected = 0;

                } else if (LatLonCellID.lat != 0.0 && LatLonCellID.lon != 0.0) {
                    callingMethod = AppConstant.tellMeMore; //this will observe the method is calling from here when you redirect next activity
                    locationData = new LocationData();
                    locationData.setLatitude(String.valueOf(LatLonCellID.lat));
                    locationData.setLongitude(String.valueOf(LatLonCellID.lon));
                    openActivity();
                } else {
                    Toast.makeText(HomeActivity.this, "Could not get current location\nPlease wait for a while", Toast.LENGTH_LONG).show();
                }
//                }
            }
        });
        // Setting an item click listener for the AutoCompleteTextView dropdown list
        atvPlaces.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {
                if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                    selected = 1;
                    try {
                        atvPlaces.dismissDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    locationData = new LocationData();

                    SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                    HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                    atvPlaces.setText(hm.get("description"));

                    // Creating a DownloadTask to download Places details of the selected place
                    placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                    // Getting url to the Google Places details api
                    String url = getPlaceDetailsUrl(hm.get("reference"));


                    // Start downloading Google Place Details
                    // This causes to execute doInBackground() of DownloadTask class
                    placeDetailsDownloadTask.execute(url);
                    if (selected == 1) {
                        try {
                            atvPlaces.dismissDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            atvPlaces.showDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


       *//* if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
            if (LatLonCellID.currentLat == 0.0 || LatLonCellID.currentLon == 0.0) {
                NOLocation(this);
            } else {
                if (AppConstant.isLogin) {
                    Intent intent = new Intent(HomeActivity.this, AddFarmWithoutMap.class);
                    intent.putExtra("calling-activity", AppConstant.HomeActivity);
                    intent.putExtra("lat", String.valueOf(LatLonCellID.currentLat));
                    intent.putExtra("log", String.valueOf(LatLonCellID.currentLon));
                    intent.putExtra("hashMapValue", hashMap);
                    startActivity(intent);
                }
            }
        }*//*


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        deviceToken = preferences.getString("registration_id", null);


        Log.v("registration_id", deviceToken + "vis");*/

    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * A method to download json data from url
     */

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console

            String key = "key=" + getResources().getString(R.string.browser_key);  //previous key
            //  String key = "key="+getResources().getString(R.string.browser_key);
            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // place searched by country
            String country = "components=country:in";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + country + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                System.out.println("URL  : " + url);
                // Fetching the data from we service
                data = downloadUrl(url);
                System.out.println("DATA : " + data); //complete address of locations

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.trim().length() > 0) {
                // Creating ParserTask
                parserTask = new ParserTask(PLACES);

                // Starting Parsing the JSON string returned by Web Service
                parserTask.execute(result);
                if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                    if (selected == 1) {
                        atvPlaces.dismissDropDown();
                    } else {
                        atvPlaces.showDropDown();
                    }
                }
            } else {
                Toast.makeText(HomeActivity.this, "Could not connect to the location API", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void submit() {
        if (isValidEntry()) {

            if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                db.open();
                Cursor cursor = db.isAuthenticated(data.getMailId(), data.getPassword());
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String userId = cursor.getString(cursor.getColumnIndex(DBAdapter.USER_ID));
                    String visibleName = cursor.getString(cursor.getColumnIndex(DBAdapter.VISIBLE_NAME));
                    if (prefs == null) {
                        prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, userId);
                    editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, visibleName);
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, true);
                    editor.commit();
                    AppConstant.isLogin = true;
                    AppConstant.user_id = userId;
                    AppConstant.visible_Name = visibleName;
                    /*Intent intent = new Intent(HomeActivity.this, AddFarmOnMap.class);
                    intent.putExtra("calling-activity", AppConstant.HomeActivity);
                    intent.putExtra("lat", String.valueOf(LatLonCellID.currentLat));
                    intent.putExtra("log", String.valueOf(LatLonCellID.currentLon));
                    intent.putExtra("hashMapValue", hashMap);
                    startActivity(intent);*/


                    Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                    startActivity(in);

                    updateMenuTitles(AppConstant.isLogin);
                } else {
                    Toast.makeText(HomeActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
                }
                db.close();
            } else {
                new LoginAsyncTask(data).execute();
            }
        } else {
            Toast.makeText(HomeActivity.this, "Invalid Entry", Toast.LENGTH_LONG).show();
        }
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
                updateMenuTitles(AppConstant.isLogin);
                showLogoutView();
            }
        });
        builder.show();
    }


    public boolean isValidEntry() {

        return data.getMailId().toString().length() > 0 && data.getPassword().toString().length() > 0;
    }

    private String getPlaceDetailsUrl(String ref) {


        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=" + getResources().getString(R.string.browser_key);

        // reference of place
        String reference = "reference=" + ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

        return url;
    }

    public void goFarSignIn() {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts_layout, null);
        prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText inputUid = (EditText) promptsView.findViewById(R.id.UserID);
        final EditText inputPWD = (EditText) promptsView.findViewById(R.id.UserPassword);
        final CheckBox checkBox = (CheckBox) promptsView.findViewById(R.id.checkBox);

        try {

            String user_name = prefs.getString(AppConstant.PREFRENCE_KEY_EMAIL, "");
            System.out.println("user name" + user_name);
            String password = prefs.getString(AppConstant.PREFRENCE_KEY_PASS, "");
            System.out.println("password" + password);
            Boolean bool = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISSAVED, false);
            System.out.println("BoolValue" + bool);
            checkBox.setChecked(bool);
            inputUid.setText(user_name.toString());
            inputPWD.setText(password.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                data.setMailId(inputUid.getText().toString());
                data.setPassword(inputPWD.getText().toString());
                SharedPreferences.Editor editor = prefs.edit();
                if (checkBox.isChecked()) {
                    System.out.println("isCHecked");
                    editor.putString(AppConstant.PREFRENCE_KEY_EMAIL, inputUid.getText().toString());
                    editor.putString(AppConstant.PREFRENCE_KEY_PASS, inputPWD.getText().toString());
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISSAVED, true);
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, true);
                    editor.commit();

                } else {
                    editor.putString(AppConstant.PREFRENCE_KEY_EMAIL, "");
                    editor.putString(AppConstant.PREFRENCE_KEY_PASS, "");
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISSAVED, false);
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, true);
                    editor.commit();

                }

                // Submit data for login here
                submit();
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


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("HomeActicity-------", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jobject = null;
            try {

                jobject = new JSONObject(result);
                JSONObject jsonObject = jobject.getJSONObject("result");
                JSONArray jsonArray = jsonObject.getJSONArray("address_components");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    JSONArray types = jObject.getJSONArray("types");
                    for (int k = 0; k < types.length(); k++) {
                        if (types.optString(k).contains("administrative_area_level_1")) {
                            AppConstant.state = jObject.getString("long_name");
                            System.out.println("State Name---" + AppConstant.state);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    placesParserTask.execute(result);
                    if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                        if (selected == 1) {
                            atvPlaces.dismissDropDown();
                        } else {
                            atvPlaces.showDropDown();
                        }
                    }
                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
                    if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                        if (selected == 1) {
                            atvPlaces.dismissDropDown();
                        } else {
                            atvPlaces.showDropDown();
                        }
                    }
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */

    ProgressDialog pDialog;

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected void onPreExecute() {
            if (parserType == PLACES_DETAILS) {
                pDialog = ProgressDialog.show(HomeActivity.this, "",
                        "Please wait.....", true);
            }
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                        //we will pick here state

                        //     JSONObject  job = new JSONObject(jsonData[0]);


                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }


        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch (parserType) {
                case PLACES:
                    if (result != null) {
                        String[] from = new String[]{"description"};
                        int[] to = new int[]{android.R.id.text1};

                        // Creating a SimpleAdapter for the AutoCompleteTextView
                        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                        // Setting the adapter
                        atvPlaces.setAdapter(adapter);
                        if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                            if (selected == 1) {
                                atvPlaces.dismissDropDown();
                            } else {
                                atvPlaces.showDropDown();
                            }
                        }
                    }
                    break;
                case PLACES_DETAILS:
                    if (pDialog != null) {
                        pDialog.cancel();
                    }
                    if (result != null) {

                        HashMap<String, String> hm = result.get(0);


                        // Getting latitude from the parsed data
                        latitude = Double.parseDouble(hm.get("lat"));
                        // Getting longitude from the parsed data
                        longitude = Double.parseDouble(hm.get("lng"));


//                        List<Address> addresses = null;
//                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
//                        try {
//                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                            if (addresses.size() > 0)
//                                System.out.println("aaaaaaaaaaaaa"+addresses.get(0).getAdminArea());
//                            AppConstant.state = addresses.get(0).getAdminArea();
//
//                            locationData.setModifiedDate(String.valueOf((new Date()).getTime()));
//                            System.out.println("Place Detail Latitude : " + latitude + " longitude : " + longitude);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        locationData.setLocationName(atvPlaces.getText().toString());
                        locationData.setLatitude(String.valueOf(latitude));
                        locationData.setLongitude(String.valueOf(longitude));
                        locationData.setModifiedDate(String.valueOf((new Date()).getTime()));

                        Log.d("HomeActivity", "" + locationData);
                        String lat = locationData.getLatitude();
                        String log = locationData.getLongitude();
                        callingMethod = AppConstant.tellMeMore; //this will observe the method is calling from here when you redirect next activity
                        syncCount = 1;
                        syncFor = AppConstant.STATE_ID;
                        syncMsg = "Syncronizing " + AppConstant.STATE_ID;
                        new appDataSetting().execute();

                        selected = 0;

                        System.out.println("Place Detail Latitude : " + latitude + " longitude : " + longitude);

                    }

                    break; //End of second case
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (prefs == null) {
            prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
        }
        boolean isLogin = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
        System.out.println("isLogin : " + isLogin);
        this.menu = menu;
        updateMenuTitles(isLogin);
        return true;
    }

    private void updateMenuTitles(boolean isLogin) {
        if (menu != null) {
            MenuItem menuItem = menu.findItem(R.id.action_login);
            if (isLogin) {
                menuItem.setIcon(R.drawable.ic_action_user);
            } else {
                menuItem.setIcon(R.drawable.action_login);
            }

            MenuItem menuMode = menu.findItem(R.id.action_offline);
            if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                menuMode.setTitle("ONLINE");
                loginStatus.setText("OFFLINE");
                loginStatus.setTextColor(Color.RED);
            } else {
                menuMode.setTitle("OFFLINE");
                loginStatus.setText("ONLINE");
                loginStatus.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_login) {
            if (prefs == null) {
                prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
            }
            boolean isLogin = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
            if (isLogin) {//if Logout action is performed
                accountAlert();
            } else {//if Login action is performed
                goFarSignIn();
//                item.setIcon(R.drawable.ic_action_user);
            }
        }
        if (id == R.id.action_sync) {
            loadData();
        }

        if (id == R.id.action_contact) {

            Intent intent = new Intent(HomeActivity.this, Contact.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_services) {
            Intent intent = new Intent(HomeActivity.this, Service.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_offline) {
            if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                AppConstant.APP_MODE = AppConstant.ONLINE;
                item.setTitle("OFFLINE");
                loginStatus.setText("ONLINE");
                loginStatus.setTextColor(Color.GREEN);
            } else {
                AppConstant.APP_MODE = AppConstant.OFFLINE;
                item.setTitle("ONLINE");
                loginStatus.setText("OFFLINE");
                loginStatus.setTextColor(Color.RED);
            }

            if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                tell_me_more.setText("CREATE FARM");
                atvPlaces.setVisibility(View.GONE);

            } else {
                tell_me_more.setText("TELL ME MORE");
                atvPlaces.setVisibility(View.VISIBLE);
            }
        }

        if (id == R.id.action_signup) {
            if (AppConstant.isLogin) {
                Toast.makeText(HomeActivity.this, "You are already logged In", Toast.LENGTH_LONG).show();
            } else {

                Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void NOGPSDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("GPS OFF :\nPlease Enable GPS\nlaunch again")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void NOLocation(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Not getting current location\nPlease try After few minutes")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
//                        HomeActivity.this.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private class LoginAsyncTask extends AsyncTask<Void, Void, String> {
        Register data;
        String result = "";

        public LoginAsyncTask(Register data) {
            this.data = data;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Login Progress . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // cancel AsyncTask
                    cancel(false);
                }
            });
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String sendRequest = null;
            String response = null;
            try {

                sendRequest = AppManager.getInstance().login + AppManager.getInstance().removeSpaceForUrl(data.getMailId()) + "/" + data.getPassword() + "/" + deviceToken;
                Log.d("sync login data", sendRequest);

                sendRequest = createdJsonStringForLogin(data.getMailId(), data.getPassword(), deviceToken);
                sendRequest = AppManager.getInstance().removeSpaceForUrl(sendRequest);
                response = AppManager.getInstance().httpRequestPutMethodLogin(AppManager.getInstance().login, sendRequest);
                return response;


            } catch (Exception ex) {
                ex.printStackTrace();

                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPreExecute();
            try {
                if (result.contains(AppConstant.SERVER_CONNECTION_ERROR)) {
                    db.open();
                    Cursor isAuthenticated = db.isAuthenticated(data.getMailId(), data.getPassword());
                    if (isAuthenticated.getCount() > 0) {
                        isAuthenticated.moveToFirst();
                        if (prefs == null) {
                            prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = prefs.edit();

                        AppConstant.user_id = isAuthenticated.getString(isAuthenticated.getColumnIndex(DBAdapter.USER_ID));
                        AppConstant.visible_Name = isAuthenticated.getString(isAuthenticated.getColumnIndex(DBAdapter.VISIBLE_NAME));
                        String sendingStatus = isAuthenticated.getString(isAuthenticated.getColumnIndex(DBAdapter.SENDING_STATUS));

                        data.setUser_id(AppConstant.user_id);
                        data.setVisibleName(AppConstant.visible_Name);
                        data.save(db, sendingStatus);

                        editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, AppConstant.user_id);
                        editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, AppConstant.visible_Name);
                        editor.commit();

                        AppConstant.isLogin = true;
                        updateMenuTitles(AppConstant.isLogin);
                        showLoginView();
                    } else {
                        Toast.makeText(getBaseContext(), AppConstant.SERVER_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
                    }
                    db.close();
                } else if (result.contains("[]")) {

                    Toast.makeText(getBaseContext(), "User name or Password Incorrect", Toast.LENGTH_LONG).show();

                } else {
                    //  SharedPreferences prefs = getSharedPreferences("user_detail", MODE_PRIVATE);
                    // SharedPreferences.Editor edit = prefs.edit();
                    // varietyResponse = varietyResponse.substring(1, varietyResponse.length() - 1);

                    Log.v("LoginWDeviceTokenResult", result);

                    JSONObject jb = new JSONObject(result);
                    JSONArray jArray = jb.getJSONArray("LoginWDeviceTokenResult");

                    // JSONArray jArray = new JSONArray(result);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        if (prefs == null) {
                            prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = prefs.edit();

                        AppConstant.user_id = jObject.getString("UserID");
                        AppConstant.visible_Name = jObject.getString("VisibleName");

                        userID = jObject.getString("UserID");
                        data.setUser_id(userID);
                        data.setVisibleName(jObject.getString("VisibleName"));
                        data.setCreatedDateTime(jObject.getString("UserSince"));
                        AppConstant.mobile_no = jObject.getString("PhNo");
                        AppConstant.role = jObject.getString("Role");


                        vil_id = jObject.getString("VillageID");
                        vil_lati = jObject.getString("Latitude");
                        vil_longi = jObject.getString("Longitude");
                        vil_name = jObject.getString("Village");
                        if (jObject.has("Device_ID")) {
                            device_id = jObject.getString("Device_ID");
                        } else {
                            device_id = null;
                        }
                        lat_farmer = vil_lati;
                        lon_farmer = vil_longi;

                        db.open();
                        data.save(db, DBAdapter.SENT);
                        db.close();

                        editor.putString(AppConstant.PREFRENCE_KEY_MOBILE, AppConstant.mobile_no);
                        editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, AppConstant.user_id);
                        editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, AppConstant.visible_Name);
                        editor.putString(AppConstant.PREFRENCE_KEY_ROLE, AppConstant.role);
                        editor.putString("Device_Id", device_id);

                        editor.commit();

                        AppConstant.isLogin = true;
                        updateMenuTitles(AppConstant.isLogin);


                    }

                    if (AppConstant.role != null && AppConstant.role.equalsIgnoreCase("Admin")) {

                        if (vil_id == null || vil_id.equalsIgnoreCase("null")) {
                            //     villageMethod();

                            villageID = "56";
                            villageName = "pirkalyan";

                            SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = prefs.edit();

                            editor1.putString("villageId", villageID);
                            editor1.putString("villageName", villageName);


                            if (lat_farmer == null || lat_farmer.equalsIgnoreCase("null")) {
                                editor1.putString("lat", "19.6807");
                                editor1.putString("lon", "75.9928");
                            } else {

                                editor1.putString("lat", lat_farmer);
                                editor1.putString("lon", lon_farmer);
                            }
                            editor1.putString("farmerID", userID);

                            editor1.apply();


                        } else {
                            //  villageID  = vil_id+"-"+vil_lati+"-"+vil_longi;
                            villageID = vil_id;
                            villageName = vil_name;

                            SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = prefs.edit();

                            editor1.putString("villageId", villageID);
                            editor1.putString("villageName", villageName);

                            if (lat_farmer == null || lat_farmer.equalsIgnoreCase("null")) {
                                editor1.putString("lat", "19.6807");
                                editor1.putString("lon", "75.9928");

                                Log.v("llklk1", "" + lat_farmer);
                            } else {

                                editor1.putString("lat", lat_farmer);
                                editor1.putString("lon", lon_farmer);

                                Log.v("llklk", "" + lat_farmer);
                            }
                            editor1.putString("farmerID", userID);

                            editor1.apply();
                        }

                      //  villageMethod();


                        villageID = "56";
                        villageName = "pirkalyan";

                        Log.v("villageNameee","nullwalaloop");

                        SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = prefs.edit();

                        editor1.putString("villageId", villageID);
                        editor1.putString("villageName", villageName);


                        if (lat_farmer == null || lat_farmer.equalsIgnoreCase("null")) {
                            editor1.putString("lat", "19.6807");
                            editor1.putString("lon", "75.9928");
                        } else {

                            editor1.putString("lat", lat_farmer);
                            editor1.putString("lon", lon_farmer);
                        }
                        editor1.putString("farmerID", userID);

                        editor1.apply();
                        showLoginView();


                    } else if (vil_id == null || vil_id.equalsIgnoreCase("null")) {
                        //     villageMethod();

                        villageID = "56";
                        villageName = "pirkalyan";

                        Log.v("villageNameee","nullwalaloop");

                        SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = prefs.edit();

                        editor1.putString("villageId", villageID);
                        editor1.putString("villageName", villageName);


                        if (lat_farmer == null || lat_farmer.equalsIgnoreCase("null")) {
                            editor1.putString("lat", "19.6807");
                            editor1.putString("lon", "75.9928");
                        } else {

                            editor1.putString("lat", lat_farmer);
                            editor1.putString("lon", lon_farmer);
                        }
                        editor1.putString("farmerID", userID);

                        editor1.apply();
                        showLoginView();

                    } else {
                        //  villageID  = vil_id+"-"+vil_lati+"-"+vil_longi;

                        Log.v("villageNameee","Notnullwalaloop"+","+vil_id+",");

                        villageID = vil_id;
                        villageName = vil_name;

                        SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = prefs.edit();

                        editor1.putString("villageId", villageID);
                        editor1.putString("villageName", villageName);

                        if (lat_farmer == null || lat_farmer.equalsIgnoreCase("null")) {
                            editor1.putString("lat", "19.6807");
                            editor1.putString("lon", "75.9928");

                            Log.v("llklk1", "" + lat_farmer);
                        } else {

                            editor1.putString("lat", lat_farmer);
                            editor1.putString("lon", lon_farmer);

                            Log.v("llklk", "" + lat_farmer);
                        }
                        editor1.putString("farmerID", userID);

                        editor1.apply();
                        showLoginView();
                    }

                    new getFarmDetailAsyncTask().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(getBaseContext(), "No Response From Server Try Again!", Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();

        }
    }

    private boolean isLocalLogin(DBAdapter db, Register register) {
        boolean isRegisterUser = false;

        return isRegisterUser;
    }

    //////////////////////////////////////////////////////////////////////////
    private class getFarmDetailAsyncTask extends AsyncTask<Void, Void, String> {
        SignInData data;
        String result = "";

        public getFarmDetailAsyncTask() {
            this.data = data;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Get Farm Detail Progress . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                                @Override
                                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                                    return false;
                                                }
                                            }
            );
        }

        @Override
        protected String doInBackground(Void... params) {
            String sendRequest = null;
            try {
                sendRequest = AppManager.getInstance().getFarmList + AppConstant.user_id;
                Log.d("get farm url", sendRequest);
                String response = AppManager.getInstance().httpRequestGetMethod(sendRequest);
                System.out.println("farm detial :" + response);
                return response;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null; //show network problm
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();
            try {

                if (response != null) {
                    if (response.contains("No Farms")) {
                        System.out.println("Farm not available");
                        //  Toast.makeText(getBaseContext(), "Farm not available", Toast.LENGTH_LONG).show();

                    } else {
                        AllFarmDetail addFarmDetail;

                        db.open();
                        //  db.deleteAllFarmDetailTable();
                        System.out.println("farm detail response " + response);

                        JSONArray jArray = new JSONArray(AppManager.getInstance().placeSpaceIntoString(response));
                        System.out.println("farm detail response " + jArray.length());
                        if (jArray.length() > 0) {
                            int deleteCount = db.db.delete(DBAdapter.DATABASE_TABLE_ALL_FARM_DETAIL, DBAdapter.SENDING_STATUS + " = '" + DBAdapter.SENT + "'", null);
                            int deleteCount1 = db.db.delete(DBAdapter.TABLE_QUERY_CROP, DBAdapter.SENDING_STATUS + " = '" + DBAdapter.SENT + "'", null);
                            System.out.println("deleteCount : " + deleteCount + " deleteCount1 : " + deleteCount1);
                        }
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject jsonObject = jArray.getJSONObject(i);
                            addFarmDetail = new AllFarmDetail(jsonObject);
                            addFarmDetail.setUserId(AppConstant.user_id);
                            String farmId = addFarmDetail.getFarmId();
                            String farmName = addFarmDetail.getFarmName();
                            String concern = addFarmDetail.getConcern();
                            Long l = db.insertAllFarmDetail(addFarmDetail, DBAdapter.SENT);
                            if (jsonObject.has("CropInfo")) {
                                JSONArray corpInfoArray = jsonObject.getJSONArray("CropInfo");

                                for (int j = 0; j < corpInfoArray.length(); j++) {
                                    JSONObject cropJsonObject = corpInfoArray.getJSONObject(j);
                                    CropQueryData data = new CropQueryData();
                                    data.setFarmId(farmId);
                                    data.setFarmName(farmName);
                                    data.setYourCencern(concern);
                                    data.setCropID(cropJsonObject.isNull("CropID") ? "" : cropJsonObject.getString("CropID"));
                                    data.setCrop(cropJsonObject.isNull("CropName") ? "" : cropJsonObject.getString("CropName"));
                                    String variety = cropJsonObject.isNull("Variety") ? "" : cropJsonObject.getString("Variety");
                                    data.setVariety(variety.replaceAll("%20", " "));
                                    data.setBasalDoseN(cropJsonObject.isNull("N") ? "0" : cropJsonObject.getString("N"));
                                    data.setBasalDoseP(cropJsonObject.isNull("P") ? "0" : cropJsonObject.getString("P"));
                                    data.setBasalDoseK(cropJsonObject.isNull("K") ? "0" : cropJsonObject.getString("K"));
                                    data.setSowPeriodForm(cropJsonObject.isNull("SowDate") ? "" : cropJsonObject.getString("SowDate"));
                                    data.setOtherNutrition(cropJsonObject.isNull("OtherNutrient") ? "" : cropJsonObject.getString("OtherNutrient"));
                                    data.setBesalDoseApply(cropJsonObject.isNull("BasalDoseApply") ? "" : cropJsonObject.getString("BasalDoseApply"));
                                    long inserted = data.insert(db, DBAdapter.SENT);
                                    System.out.println("database return value=" + l);
                                }
                            }

                            // showLoginView();
                        }
                        db.close();
                        getAllFarmName(); //This will get all farm deatail from the database and set the farm list in spinnerView
                    }
                } else {
                    Toast.makeText(getBaseContext(), "could not connect to server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                System.out.println("catch block Pls Try again");
            }

            progressDialog.dismiss();

        }
    }

    public void getAllFarmName() {


        System.out.println("getAllFarmCalled");
        ArrayList<String> str = new ArrayList<String>();
        str.add("Select");

        db.open();
        Cursor c = db.getallFarmName(AppConstant.user_id);
        if (c.moveToFirst()) {
            do {
                str.add(c.getString(0).toString());
            } while (c.moveToNext());
        }
        db.close();
        ArrayAdapter<String> chooseYourFarmSpiner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        chooseYourFarmSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.chooseYourFarmSpiner.setAdapter(chooseYourFarmSpiner);
    }

    public void showLoginView() {
       /* loginUserName.setText("User Name : " + AppConstant.visible_Name);
        Invisible.setVisibility(View.GONE);
        visibleLoginDetail.setVisibility(View.VISIBLE);
        visibleOrText.setVisibility(View.VISIBLE);*/


        loginUserName.setText("");
        Invisible.setVisibility(View.VISIBLE);
        visibleLoginDetail.setVisibility(View.INVISIBLE);
        visibleOrText.setVisibility(View.INVISIBLE);

        String strt = getIntent().getStringExtra("data");

        Log.v("jksjks", "" + strt);
        if (strt != null && strt.equalsIgnoreCase("advi")) {
            Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
            in.putExtra("data", "advi");
            startActivity(in);
            finish();
        } else {
            Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
            //  in.putExtra("data", "mand");
            in.putExtra("data", "tube");
            startActivity(in);
            finish();
        }


    }

    public void showLogoutView() {
        loginUserName.setText("");
        Invisible.setVisibility(View.VISIBLE);
        visibleLoginDetail.setVisibility(View.INVISIBLE);
        visibleOrText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppConstant.isLogin = false;
                        HomeActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    class appDataSetting extends AsyncTask<Void, Void, String> {
        boolean firstRound = true;

        ProgressDialog dialoug;

        public appDataSetting() {
        }

        @Override
        protected void onPreExecute() {
            dialoug = ProgressDialog.show(HomeActivity.this, "" + syncMsg, " Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = syncForApplicationDataSetting(syncFor);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                String msgDisplayInDailogBox = null;
                dialoug.dismiss();
                if (!result.contains("Success")) {
                    if (syncCount == 1) {

                        msgDisplayInDailogBox = "State id sync Not Succesfully!";

                    }
                    if (syncCount == 2) {

                        msgDisplayInDailogBox = "Crop initial sync Not Succesfully!";

                    }
                    if (syncCount == 3) {

                        msgDisplayInDailogBox = "Crop all initial sync Not Succesfully!";

                    }
                    syncMsg = "Syncronizing for " + HomeActivity.syncFor;
                    alertDialogBox(msgDisplayInDailogBox, syncCount);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (storeCurrentStateId != null && storeCurrentStateId.contains(AppConstant.stateID)) {

                openActivity();
            } else {

                if (syncCount < AppConstant.syncArray.length) {

                    HomeActivity.syncFor = AppConstant.syncArray[syncCount];
                    System.out.println("value in syncFor " + HomeActivity.syncFor);
                    syncMsg = "Syncronizing for " + HomeActivity.syncFor;
                    syncCount++;
                    new appDataSetting().execute();

                } else {
                    openActivity();
                }

            }
        }
    }//+


    public String syncForApplicationDataSetting(String value) {
        String returnResult = null;
        System.out.println("value:" + value);
        if (value.contains(AppConstant.STATE_ID)) {
            returnResult = syncForStateId();
            return returnResult;

        }
        /*if (value.contains(AppConstant.CROP_INITIAL)) {
            db.open();
            long l = db.deleteAllCropVarietyTableRecord();
            db.close();
            System.out.print("value of long" + l);
            hashMap.clear();
            returnResult = syncForCropInitial();
            System.out.println("crop_initial_called");

        }
        if (value.contains(AppConstant.CROP_ALL_INITIAL)) {
            returnResult = syncForCropAllInitial();
            System.out.println("crop_all_initial called");

        }*/
        return returnResult;
    }

    public String syncForStateId() {
        String message = "NoValue";
        String sendRequest = null;
        sendRequest = AppManager.getInstance().getStateId + AppConstant.state + "/" + latitude + "/" + longitude;
        System.out.println("State Request URL : " + sendRequest);
        String response = AppManager.getInstance().httpRequestGetMethod(AppManager.getInstance().removeSpaceForUrl(sendRequest));
        System.out.println("state id is - --" + response);
        System.out.println("state id is - fcasdfasfasdfasf--");

        if (response == "") {
            return message;
        } else {
            AppConstant.stateID = response;
            message = "Success";
            return message;
        }
    }//+

    public String syncForCropInitial() {
        String message;
        String sendRequest = null;
        sendRequest = AppManager.getInstance().cropsInitial + AppConstant.stateID;
        Log.d("sync app data", sendRequest);
        String response = AppManager.getInstance().httpRequestGetMethod(AppManager.getInstance().removeSpaceForUrl(sendRequest));
        try {
            JSONArray jArray = new JSONArray(response);
            if (jArray.length() == 0) {
                Toast.makeText(HomeActivity.this, "No response Pls Try again", Toast.LENGTH_LONG).show();
            } else {
                db.open();
                ContentValues values = new ContentValues();

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String cropId = jObject.getString("ID");
                    String cropName = jObject.getString("Name");
                    hashMap.put(cropName, cropId);
                    hashMap.put(cropId, cropName);
                    values.put(DBAdapter.CROP_ID, cropId);
                    values.put(DBAdapter.CROP, cropName);
                    db.db.insert(DBAdapter.TABLE_CROP, null, values);
                    System.out.println(cropId + " " + cropName);
                }
                message = "Success";
                db.close();
                return message;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "JSON Format Exception";

        }
        return "Web Service Error";
    }//+

    public String syncForCropAllInitial() {
        String sendRequest = null;
        String message = null;

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        sendRequest = AppManager.getInstance().cropsAllInitial + AppConstant.stateID;//AppConstant.stateID
        Log.d("sync app data", sendRequest);
        String response = AppManager.getInstance().httpRequestGetMethod(AppManager.getInstance().removeSpaceForUrl(sendRequest));

        try {
            JSONArray jArray = new JSONArray(response);
            if (jArray.length() == 0) {

                Toast.makeText(HomeActivity.this, "No response Pls Try again", Toast.LENGTH_LONG).show();
            }

            db.open();
            {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String crop = jObject.getString("Name");
                    System.out.println(crop);
                    Log.d("FarmInformationClass", "" + crop);
                    String variety = jObject.getString("Variety");
                    Log.d("FarmInformationClass", "" + variety);
                    long l = db.insertCropVariety(crop, variety);
                    Log.d("Farminfo-----", "return value from database" + l);
                }
                db.close();

                message = "Success";
                return message;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "JSON Format Exception";
        }

    }

    public void openActivity() {

        storeCurrentStateId = AppConstant.stateID;

        switch (callingMethod) {

            case AppConstant.selectForm: //if you select the farm

//                Intent i = new Intent(HomeActivity.this, FarmInformation.class);
                Intent i = new Intent(HomeActivity.this, NavigationDrawerActivity.class);
                i.putExtra("calling-activity", AppConstant.HomeActivity);
                i.putExtra("FarmName", chooseYourFarmSpiner.getSelectedItem().toString());
                i.putExtra("hashMapValue", hashMap);
                startActivity(i);
                break;
            case AppConstant.tellMeMore: // if you choose your new farm
                Intent intent = new Intent(HomeActivity.this, AddFarmOnMap.class);
                intent.putExtra("calling-activity", AppConstant.HomeActivity);
                intent.putExtra("lat", locationData.getLatitude().toString());
                intent.putExtra("log", locationData.getLongitude().toString());
                intent.putExtra("hashMapValue", hashMap);
                startActivity(intent);
                break;
        }

    }

    public void alertDialogBox(String msg, int syncCount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new appDataSetting().execute();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void getAtLeastOneLatLngPoint(String contour) {

        String newContour = "";

        int pos = contour.indexOf("-");
        if (pos != -1) {
            newContour = contour.substring(0, pos);
        }
        int posOfComma = newContour.indexOf(",");
        AppConstant.latitude = newContour.substring(0, posOfComma);
        System.out.println("dgasdgsdfgsg" + AppConstant.latitude);
        String str = newContour.substring(newContour.indexOf(","));
        AppConstant.longitude = str.replace(",", "");
        System.out.println("dgasdgsdfgsg" + AppConstant.longitude);

    }

    /*private void isNeedToEdit(String userId){
        Cursor cursor = db.getC
    }*/

    ProgressDialog dialoug1;

    public void loadData() {
        dialoug1 = ProgressDialog.show(HomeActivity.this, "",
                "Loading state. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/All/States",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug1.cancel();
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
                        db.open();
                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                        SqliteDB.beginTransaction();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                db.db.execSQL("delete from " + DBAdapter.TABLE_STATE);
                            }

                            String query = "INSERT INTO " + DBAdapter.TABLE_STATE + "(" + DBAdapter.STATE_ID + "," + DBAdapter.STATE_NAME + ") VALUES (?,?)";
                            SQLiteStatement stmt = SqliteDB.compileStatement(query);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject territoryElementArray = jsonArray.getJSONObject(i);

                                String stateId = String.valueOf((int) territoryElementArray.getDouble("StateID"));

                                stmt.bindString(1, stateId);
                                stmt.bindString(2, territoryElementArray.getString("StateName"));
                                stmt.execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }
                        SqliteDB.setTransactionSuccessful();
                        SqliteDB.endTransaction();
                        db.getClass();
                        getCropVariety();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialoug1.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getCropVariety() {
        dialoug = ProgressDialog.show(HomeActivity.this, "",
                "Loading crop list . Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/All/Crops",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
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
                        db.open();
                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                        SqliteDB.beginTransaction();
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                db.db.execSQL("delete from " + DBAdapter.TABLE_CROP_VARIETY);
                            }

                            String query = "INSERT INTO " + DBAdapter.TABLE_CROP_VARIETY + "(" + DBAdapter.STATE_ID + "," + DBAdapter.CROP_ID + "," + DBAdapter.CROP + "," + DBAdapter.VARIETY + ") VALUES (?,?,?,?)";
                            SQLiteStatement stmt = SqliteDB.compileStatement(query);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONArray territoryElementArray = jsonArray.getJSONArray(i);

                                stmt.bindString(1, territoryElementArray.get(3).toString());
                                stmt.bindString(2, territoryElementArray.get(0).toString());
                                stmt.bindString(3, territoryElementArray.get(1).toString());
                                stmt.bindString(4, territoryElementArray.get(2).toString());
                                stmt.execute();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }
                        SqliteDB.setTransactionSuccessful();
                        SqliteDB.endTransaction();

                        loadVillageData();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialoug.cancel();
                Toast.makeText(HomeActivity.this, "Could not connect to the server", Toast.LENGTH_LONG).show();
                System.out.println("Volley Error : " + error);
            }
        });
        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    ProgressDialog dialoug;




    public void loadVillageData() {
        dialoug = ProgressDialog.show(HomeActivity.this, "",
                "Loading Villages. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley State Response : " + response);

                        response = response.trim();
                        // response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        db.open();
                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                        SqliteDB.beginTransaction();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                db.db.execSQL("delete from " + DBAdapter.TABLE_VILLAGE);
                            }

                            String query = "INSERT INTO " + DBAdapter.TABLE_VILLAGE + "(" + DBAdapter.VILLAGE_ID + "," + DBAdapter.VILLAGE_NAME + ") VALUES (?,?)";
                            SQLiteStatement stmt = SqliteDB.compileStatement(query);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject territoryElementArray = jsonArray.getJSONObject(i);


                                stmt.bindString(1, territoryElementArray.getString("Id"));
                                stmt.bindString(2, territoryElementArray.getString("Village_Final"));
                                stmt.execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }
                        SqliteDB.setTransactionSuccessful();
                        SqliteDB.endTransaction();
                        db.getClass();

                        db.close();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialoug.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static final int EXCESS_FINE_LOCATION = 102;
    public static final int EXCESS_COURSE_LOCATION = 103;
    public static final int ACCESS_NETWORK_LOCATION = 105;
    public static final int ACCESS_PHONE_STATE = 104;
    public static final int ACCESS_STORAGE = 106;

    private boolean checkPermissionLocation1() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, EXCESS_COURSE_LOCATION);
                return false;
            }

        }
        return true;
    }

    private boolean checkPermissionLocation2() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, EXCESS_FINE_LOCATION);
                return false;
            }

        }
        return true;
    }

    private boolean checkPermissionStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE);
                return false;
            }

        }
        return true;
    }


    private boolean checkPermissionPhone() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, ACCESS_NETWORK_LOCATION);
                return false;
            }

        }
        return true;
    }

    private boolean checkPermissionPhoneSTATE() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, ACCESS_PHONE_STATE);
                return false;
            }

        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {


            case EXCESS_COURSE_LOCATION:
            case EXCESS_FINE_LOCATION:
            case ACCESS_NETWORK_LOCATION:
            case ACCESS_PHONE_STATE:
            case ACCESS_STORAGE:
                activityRestart();
                break;
        }
    }

    public void activityRestart() {
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }


    public void villageMethod() {

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

        districtSpinner = (Spinner) dialog.findViewById(R.id.popup_district);
        villageSpinner = (Spinner) dialog.findViewById(R.id.popup_village);

        cropSpinner = (EditText) dialog.findViewById(R.id.popup_village_crop);

        cropSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiArray.size() > 0) {

                    selectCropPopup();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select village", Toast.LENGTH_SHORT).show();
                }
            }
        });

        farmerSpinner = (Spinner) dialog.findViewById(R.id.popup_farmer);
        Button okBTN = (Button) dialog.findViewById(R.id.popup_submit);
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (farmer_id != null) {
                    dialog.dismiss();
                    showLoginView();
                } else {
                    Toast.makeText(getApplicationContext(), "please select village.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayList<String> districtList = new ArrayList<>();
        final ArrayList<String> districtID = new ArrayList<>();


        districtList.add("-Select-");
        districtList.add("Jalna");


        districtID.add("0");
        districtID.add("16032");

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, districtList); //selected item will look like a spinner set from XML
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
                    lat_farmer = null;
                    lon_farmer = null;

                    loadVillagesData(ID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void loadVillagesData(String ID) {
        final ProgressDialog dialoug = ProgressDialog.show(HomeActivity.this, "",
                "Fetching Villages. Please wait...", true);
        Log.v("knsknklanl", "ttp://myfarminfo.com/yfirest.svc/JalnaVillages");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley village Response : " + response);

                        response = response.trim();
                        //  response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");


                        DataBean bean = new DataBean();
                        bean = getEventTypeList(response);
                        ArrayList<VillageBean> cityList = new ArrayList<VillageBean>();
                        cityList = bean.getCityList();
                        cityArr = new String[cityList.size()];
                        for (int i = 0; i < cityList.size(); i++) {
                            cityArr[i] = cityList.get(i).getVilageName();
                        }

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, cityArr);
                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        villageSpinner.setAdapter(eventTypeAdapter);

                        final DataBean finalBean = bean;
                        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                villageID = finalBean.getCityList().get(position).getVillageID();
                                villageName = finalBean.getCityList().get(position).getVilageName();

                                Log.v("ksjkls", villageID);

                                vill_id = villageID;
                                SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("villageId", villageID);
                                editor.putString("villageName", villageName);
                                editor.apply();

                               /* if (villageID!=null){

                                    String[] parts = villageID.split("-");
                                    vill_id = parts[0];
                                    lat_farmer = parts[1];
                                    lon_farmer = parts[2];

                                    SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor  = prefs.edit();
                                    editor.putString("lat",lat_farmer);
                                    editor.putString("lon",lon_farmer);
                                    editor.putString("villageId",villageID);
                                    editor.putString("villageName",villageName);
                                    editor.apply();

                                }*/
                                getCropList(villageID);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialoug.cancel();
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
        if (response != null) {
            try {

                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() > 0) {

                }

                for (int i = 0; i < jsonArray.length(); i++) {

                    VillageBean typeBean = new VillageBean();
                    typeBean.setVilageName(jsonArray.getJSONObject(i).getString("Village_Final"));
                    typeBean.setVillageID(jsonArray.getJSONObject(i).getString("Id"));
                    eventTypeList.add(typeBean);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataBean.setCityList(eventTypeList);


        }

        return dataBean;
    }

    public void selectCropPopup() {

        idSelectedList = new ArrayList<String>();

        //final Dialog dialog = new Dialog(OtherUserProfile.this,android.R.style.Theme_Translucent_NoTitleBar);
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        // Include dialog.xml file
        dialog.setContentView(R.layout.select_crop_popup);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout createBTN = (RelativeLayout) dialog.findViewById(R.id.done_addpopup);
        listView1 = (ListView) dialog.findViewById(R.id.list_addgroup);
        multiAdapter = new MultiselectionAdapter(HomeActivity.this, multiArray);
        listView1.setAdapter(multiAdapter);


        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showResult();
                dialog.cancel();


                if (resultstr != null) {
                    cropSpinner.setText(resultstr);
                }
                if (idSelectedList.size() > 0) {
                    String ids = "";
                    for (int i = 0; i < idSelectedList.size() - 1; i++) {
                        ids = idSelectedList.get(i) + "," + ids;
                    }
                    ids = ids + idSelectedList.get(idSelectedList.size() - 1);
                    getFarmerList(ids);
                }

            }
        });

        dialog.show();
    }

    public void showResult() {

        String totalAmount = null;
        resultstr = "";
        for (MultiBean p : multiAdapter.getBox()) {
            if (p.box) {
                resultstr += p.crop_name + ",";
                totalAmount += p.crop_id;
                idSelectedList.add(p.crop_id);
            }
        }
        //  Toast.makeText(this, result+"\n"+"Total Amount:="+totalAmount, Toast.LENGTH_LONG).show();
    }

    public void getFarmerList(String crop_id) {

        farmer_id = null;
        lat_farmer = null;
        lon_farmer = null;

        final ProgressDialog cropDialog = ProgressDialog.show(this, "",
                "Fetching Farmers. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Farms/" + vill_id + "/" + crop_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cropDialog.cancel();
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

                        farmerList = new ArrayList<FarmerBean>();


                        try {

                            JSONObject jb = new JSONObject(response);
                            JSONArray jsonArray = jb.getJSONArray("DT");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                FarmerBean typeBean = new FarmerBean();
                                typeBean.setName(jsonArray.getJSONObject(i).getString("Name"));
                                typeBean.setId(jsonArray.getJSONObject(i).getString("ID"));
                                farmerList.add(typeBean);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String[] farmArr = new String[farmerList.size()];
                        for (int i = 0; i < farmerList.size(); i++) {
                            farmArr[i] = farmerList.get(i).getName();
                        }

                        ArrayAdapter<String> farmerAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item, farmArr);
                        farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        farmerSpinner.setAdapter(farmerAdapter);

                        farmerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                farmerID = farmerList.get(position).getId();
                                farmerName = farmerList.get(position).getName();

                                Log.v("farmerName", farmerName + "");

                                if (farmerID != null) {

                                    String[] parts = farmerID.split("-");
                                    farmer_id = parts[0];
                                    lat_farmer = parts[1];
                                    lon_farmer = parts[2];

                                    SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("lat", lat_farmer);
                                    editor.putString("lon", lon_farmer);
                                    editor.putString("farmerID", farmerID);
                                    editor.apply();
                                }


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cropDialog.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getCropList(String ID) {
        final ProgressDialog cropDialog = ProgressDialog.show(this, "",
                "Fetching Crops. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Crops/" + ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cropDialog.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley State Response : " + response);

                        response = response.trim();
                        //  response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");

                        cropList = new ArrayList<CropBean>();

                        multiArray = new ArrayList<MultiBean>();


                        cropSpinner.setText(null);

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                CropBean typeBean = new CropBean();

                                typeBean.setName(jsonArray.getJSONObject(i).getString("Name"));
                                typeBean.setId(jsonArray.getJSONObject(i).getString("ID"));

                                MultiBean bean = new MultiBean(typeBean.getName(), false, typeBean.getId());
                                multiArray.add(bean);
                                cropList.add(typeBean);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                       /* String[] cropArr = new String[cropList.size()];
                        for (int i = 0; i < cropList.size(); i++) {
                            cropArr[i] = cropList.get(i).getName();
                        }
*/


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cropDialog.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public String createdJsonStringForLogin(String mail, String pass, String devi) {
        String json = "";


        // farmInformationData.getUserID() + "/" + farmInformationData.getFarmId() + "/" + farmInformationData.getFarmName() + "/" + farmInformationData.getAllLatLngPoint() + "/" + farmInformationData.getCropID() + "/" +farmInformationData.getState() + "/" + farmInformationData.getVariety() + "/" +farmInformationData.getYourCencern() + "/" + farmInformationData.getBasalDoseN() + "/" + farmInformationData.getBasalDoseP() + "/" +farmInformationData.getBasalDoseK() + "/" + farmInformationData.getBesalDoseApply() + "/" +farmInformationData.getOtherNutrition() + "/" +farmInformationData.getSowPeriodForm() + "/" + farmInformationData.getSowPeriodTo();
        //firest.svc/saveFarmInfo
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserNameOrMail", mail);
            jsonObject.put("Password", pass);
            jsonObject.put("DeviceID", devi);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //      jsonObject.put("guarderiasIdGuarderias",jsonObject2);
        json = jsonObject.toString();


        return json;

    }


    private void getUpdateAPK() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/App/LatestVersion/jalna",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        /*response = response.trim();
                        response = response.substring(1, response.length() - 1);*/
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {

                                String id = jsonArray.getJSONObject(0).getString("ID");
                                major_res = jsonArray.getJSONObject(0).getString("Major");
                                minor_res = jsonArray.getJSONObject(0).getString("Minor");
                                build_res = jsonArray.getJSONObject(0).getString("Build");

                                if (prefs1 == null) {
                                    prefs1 = getSharedPreferences("version", MODE_PRIVATE);
                                }
                                String saved_major = prefs1.getString("major", null);
                                String saved_minor = prefs1.getString("minor", null);
                                String saved_build = prefs1.getString("build", null);
                                if (saved_major == null) {
                                    saved_major = "1";
                                }
                                if (saved_minor == null) {
                                    saved_minor = "0";
                                }
                                if (saved_build == null) {
                                    saved_build = "0";
                                }

                                if (saved_major == null || !major_res.equalsIgnoreCase(saved_major)) {
                                    updateMethod("major");
                                } else if (saved_minor == null || !minor_res.equalsIgnoreCase(saved_minor)) {
                                    updateMethod("minor");
                                } else if (saved_build == null || !build_res.equalsIgnoreCase(saved_build)) {
                                    updateMethod("build");
                                }


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Could not connect to the server", Toast.LENGTH_LONG).show();
                System.out.println("Volley Error : " + error);
            }
        });
        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void updateMethod(final String ss) {


        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);

        if (ss.equalsIgnoreCase("major")) {
            builder.setTitle("Major Changes Please UPDATE");

        } else if (ss.equalsIgnoreCase("minor")) {

            builder.setTitle("Minor Changes Please UPDATE");

        } else if (ss.equalsIgnoreCase("build")) {

            builder.setTitle("Please UPDATE");

        }
        builder.setMessage("New version available on Play Store").setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if (prefs1 == null) {
                    prefs1 = getSharedPreferences("version", MODE_PRIVATE);
                }
                SharedPreferences.Editor editor = prefs1.edit();
                editor.putString("major", major_res);
                editor.putString("minor", minor_res);
                editor.putString("build", build_res);
                editor.commit();

                dialogInterface.dismiss();
                //new DownloadAPK().execute(url);

                final String appPackageName = "com.wrms.vishal.jalnaa"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (ss.equalsIgnoreCase("major")) {

                    Toast.makeText(getApplicationContext(), "Update necessary becuase of major changes in app", Toast.LENGTH_SHORT);

                } else if (ss.equalsIgnoreCase("minor")) {

                    Toast.makeText(getApplicationContext(), "Update necessary becuase of Minor changes in app", Toast.LENGTH_SHORT);


                } else if (ss.equalsIgnoreCase("build")) {

                    dialogInterface.dismiss();

                }
            }
        }).setCancelable(false);
        builder.show();
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
