package com.wrms.vodafone.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.MultiselectionAdapter;
import com.wrms.vodafone.bean.CropBean;
import com.wrms.vodafone.bean.FarmerBean;
import com.wrms.vodafone.bean.MultiBean;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.live_cotton.LiveCottonActivity;
import com.wrms.vodafone.utils.AppConstant;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 12-10-2017.
 */
public class AfterLoginActivity extends AppCompatActivity {


    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static View view;
    private GoogleMap mMap;


    private String cityArr[];
    String villageID = null;
    String vill_id = null;
    String lat = null;
    String lon = null;
    String villageName = null;
    ArrayList<LatLng> points = null;

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


    Double lat1 = 0.0, lon1 = 0.0;

    LinearLayout dashboardBTN, liveCottonBTN, mandiButton, advisoryBTN;
    String moist_txt = null;
    String strt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_login_activity);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //    Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }


        checkPermissionStorage();

        strt = getIntent().getStringExtra("data");

        Log.v("jksjks",""+strt);
        if (strt!=null && strt.equalsIgnoreCase("advi")) {
            Intent intent = new Intent(AfterLoginActivity.this, NavigationDrawerActivity.class);
            intent.putExtra("data", "advi");
            startActivity(intent);
        }

        dashboardBTN = (LinearLayout) findViewById(R.id.dashboard);
        liveCottonBTN = (LinearLayout) findViewById(R.id.live_cotton);
        mandiButton = (LinearLayout) findViewById(R.id.mandi);
        advisoryBTN = (LinearLayout) findViewById(R.id.advisory);

        points = new ArrayList<LatLng>();


        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();
                }
            });

        }


        setUpMapIfNeeded(); // For setting up the MapFragment

        SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);

        String lat1 = prefs.getString("lat", null);
        String lon1 = prefs.getString("lon", null);
        String villageId1 = prefs.getString("villageId", null);
        String villageName1 = prefs.getString("villageName", null);
        String role1 = prefs.getString(AppConstant.PREFRENCE_KEY_ROLE, null);


        farmerID = prefs.getString("farmerID", null);
        moist_txt = getIntent().getStringExtra("check_moist");


        Log.v("llllll", lat1 + "----" + lon1);
        if (role1 != null && role1.equalsIgnoreCase("client")) {


            lat = lat1;
            lon = lon1;


            if (farmerID != null) {

                getLatLongList();
            } else {
                if (mMap != null) {
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.72376, 71.797875), 10.0f));

                }

            }

        } else {


            lat = lat1;
            lon = lon1;
            villageID = villageId1;
            villageName = villageName1;


            if (farmerID != null) {
               // String[] parts = villageID.split("-");
                vill_id = villageID;
                getLatLongList();
            } else {
                if (mMap != null) {
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.72376, 71.797875), 10.0f));

                }
            }


        }

        //  getLatLongList(villageID);

        dashboardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                in.putExtra("data", "dash");
                startActivity(in);
            }
        });

        mandiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
              //  in.putExtra("data", "mand");
                in.putExtra("data", "tube");
                startActivity(in);
            }
        });

        advisoryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                in.putExtra("data", "advi");
                startActivity(in);
            }
        });

        liveCottonBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), LiveCottonActivity.class);
                in.putExtra("data", "dash");
                startActivity(in);
            }
        });




    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void exitMethod() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AfterLoginActivity.this);
        builder.setTitle("EXIT").
                setMessage("Do You want to exit?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                }).
                setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        exitMethod();
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
        if (id == R.id.action_settings) {

            strt = null;
            villageMethod();
            return true;
        } else if (id == R.id.action_error) {
            File logFile = new File(Environment.getExternalStorageDirectory(), "ggrc.txt");
            if (logFile.exists()) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"vishal.tripathi@iembsys.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logFile));
                // the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GGRC Error log");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "GGRC app");

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(this, "No email application is available to share error log file", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "GGRC ErrorLog file does not exist ", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
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

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(AfterLoginActivity.this, android.R.layout.simple_spinner_item, districtList); //selected item will look like a spinner set from XML
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
        multiAdapter = new MultiselectionAdapter(AfterLoginActivity.this, multiArray);
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
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        response = response.replace("\\", "");

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

                        ArrayAdapter<String> farmerAdapter = new ArrayAdapter<String>(AfterLoginActivity.this, android.R.layout.simple_spinner_item, farmArr);
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

                                    lat = lat_farmer;
                                    lon = lon_farmer;

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

    private void getLatLongList() {

        final ProgressDialog dialoug1 = ProgressDialog.show(this, "",
                "Fetching Data Please wait...", true);

        Log.v("afterLogin URL", "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Data/" + farmerID + "/" + "Farm" + "/" + lat + "/" + lon);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Data/" + farmerID + "/" + "Farm" + "/" + lat + "/" + lon,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialoug1.cancel();
                        System.out.println(" Response : " + response);
                        try {


                            response = response.trim();
                           // response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");


                            Log.v("response", response + "");


                            JSONObject jb = new JSONObject(response);
                            Log.v("response", jb.toString() + "");
                            JSONArray locationArray = jb.getJSONArray("DT7");
                            points = new ArrayList<LatLng>();
                            for (int i = 0; i < locationArray.length(); i++) {
                                JSONObject locationObject = locationArray.getJSONObject(i);
                                String point = locationObject.getString("Contour");
                                List<String> l_List = Arrays.asList(point.split(","));


                                if (l_List.size()>1) {

                                    lat1 = Double.valueOf(l_List.get(0));
                                    lon1 = Double.valueOf(l_List.get(l_List.size() - 1));
                                    points.add(new LatLng(lat1, lon1));

                                    for (int j = 1; j < l_List.size() - 1; j++) {
                                        String currentString = l_List.get(j);
                                        if (currentString != null) {
                                            String[] separated = currentString.split("-");
                                            String la = separated[0];
                                            String lo = separated[1];

                                            points.add(new LatLng(Double.valueOf(lo), Double.valueOf(la)));
                                        }
                                    }
                                }
                            }

                            if (lat1 != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat1, lon1), 16.0f));
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                Log.v("latlon1", lat + "---" + lon);

                            } else {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lat), Double.valueOf(lon)), 13.0f));
                                Log.v("latlon2", lat1 + "---" + lon1);
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            }
                            if (mMap != null) {
                                mMap.clear();
                                setUpMap();
                            }


                            if (farmerID != null) {
                                String[] parts = farmerID.split("-");
                                String farmer_id1 = parts[0];
                                if (parts.length > 1) {
                                    String lat_farmer1 = parts[1];
                                    String lon_farmer1 = parts[2];
                                }
                                if (moist_txt == null) {
                                    yesNoMoisture(farmer_id1);
                                }
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error : " + error);
                dialoug1.dismiss();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.6807, 75.9928), 10.0f));
                //noInternetMethod();
            }
        });
        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    /*****
     * Sets up the map if it is possible to do so
     *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        }
    }

    private void setUpMap() {
        // For showing a move to my loction button
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the
        if (points.size() > 4) {
            drawCircle();

            Log.v("asklcjlks", "sdkasmkld");
        }


    }


    public void drawCircle() {
        // Clear the map to remove the previous circle
        mMap.clear();
        // Generate the points
        mMap.addPolygon(new PolygonOptions().addAll(points).strokeWidth(4).strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
        // Create and return the polygon

    }


    public void loadVillagesData(String ID) {
        final ProgressDialog dialoug = ProgressDialog.show(AfterLoginActivity.this, "",
                "Fetching Villages. Please wait...", true);
        Log.v("knsknklanl", "http://myfarminfo.com/yfirest.svc/JalnaVillages");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley village Response : " + response);

                        response = response.trim();
                    //    response = response.substring(1, response.length() - 1);
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

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(AfterLoginActivity.this, android.R.layout.simple_spinner_item, cityArr);
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
                                SharedPreferences.Editor editor  = prefs.edit();
                                editor.putString("villageId",villageID);
                                editor.putString("villageName",villageName);
                                editor.apply();

                               /* if (villageID != null) {

                                    String[] parts = villageID.split("-");
                                    vill_id = parts[0];
                                    lat = parts[1];
                                    lon = parts[2];

                                    SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("lat", lat);
                                    editor.putString("lon", lon);
                                    editor.putString("villageId", villageID);
                                    editor.putString("villageName", villageName);
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


    private void yesNoMoisture(String far) {

        final ProgressDialog dialoug1 = ProgressDialog.show(this, "", "Loading Data Please wait...", true);

        Log.v("dkljal", "http://myfarminfo.com/YFIRest.svc/Irrigation/Check/" + far);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/YFIRest.svc/Irrigation/Check/" + far,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialoug1.cancel();
                        System.out.println(" Response : " + response);
                        try {

                            if (response != null) {
                                response = response.trim();
                             //   response = response.substring(1, response.length() - 1);
                                response = response.replace("\\", "");
                                response = response.replace("\\", "");
                                response = response.replace("\"{", "{");
                                response = response.replace("}\"", "}");
                                response = response.replace("\"[", "[");
                                response = response.replace("]\"", "]");

                                if (response.equalsIgnoreCase("Yes")) {


                                    String ss = getResources().getString(R.string.moisture_txt);
                                    noMoisturePopup(ss);
                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error : " + error);
                dialoug1.dismiss();

            }
        });
        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void noMoisturePopup(String str) {


        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        // Include dialog.xml file
        dialog.setContentView(R.layout.no_device_popup);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout doneBTN = (RelativeLayout) dialog.findViewById(R.id.done_nodevice);
        RelativeLayout cancelBTN = (RelativeLayout) dialog.findViewById(R.id.cancel_nodevice);
        TextView deviceText = (TextView) dialog.findViewById(R.id.device_text);
        deviceText.setText(str);

        doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

                Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                in.putExtra("irrigation", "irri");
                startActivity(in);

            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


    public static final int ACCESS_STORAGE = 112;


    private boolean checkPermissionStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE);
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


            case ACCESS_STORAGE:

        }
    }

}
