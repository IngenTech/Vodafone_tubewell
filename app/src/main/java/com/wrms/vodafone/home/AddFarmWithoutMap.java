package com.wrms.vodafone.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.SphericalUtil;
import com.wrms.vodafone.R;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.LocationData;
import com.wrms.vodafone.mapfragments.LatLonCellID;
import com.wrms.vodafone.utils.AppConstant;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WRMS on 29-img_12-2015.
 */
public class AddFarmWithoutMap extends AppCompatActivity {

    Boolean mDrawPolygon = false;
    PolygonOptions rectOptions;
    LocationData locationData;
    Button nextBtn;
    Button draw_by_walk;
    ListView listView;
    int flag = 0;
    ArrayList<LatLng> arrayPoints = null;
    ArrayList<LatLng> points = null;
    Double areadOfPolygon = null;
    String allPoints = "";
    int flagForDraw = 0;
    HashMap<String, String> hashMap;
    ImageView gpsStatus;
    DBAdapter db;
    List<String> your_array_list;
    ArrayAdapter<String> arrayAdapter;
    Spinner stateSpinner;
    String stateId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBAdapter(this);
        setContentView(R.layout.activity_farm_without_map);

        nextBtn = (Button) findViewById(R.id.next);
        gpsStatus = (ImageView)findViewById(R.id.gpsStatus);
        draw_by_walk = (Button) findViewById(R.id.draw_by_walk);
        listView = (ListView)findViewById(R.id.latlnglistview);
        stateSpinner = (Spinner) findViewById(R.id.stateList);

        // drawPolygon.setOnTouchListener(this);
        rectOptions = new PolygonOptions();
        arrayPoints = new ArrayList<LatLng>();

        db.open();
        ArrayList<String> stateList = new ArrayList<>();
        final Cursor stateCursor = db.getAllStates();
        stateList.add("Select State");
        if(stateCursor.getCount()>0){
            stateCursor.moveToFirst();
            do{
                stateList.add(stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_NAME)));
            }while(stateCursor.moveToNext());
        }
        ArrayAdapter<String> stateListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stateList);
        stateSpinner.setAdapter(stateListAdapter);
        db.close();
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    stateCursor.moveToPosition(position-1);
                    stateId = stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_ID));
                }else{
                    stateId = "-1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(allPoints!=null && allPoints.length()>2) {
                    allPoints = allPoints.substring(1, allPoints.length() - 1); // this will remove dess(-) from string at starting point
                }
                if(stateId.equals("-1")){
                    Toast.makeText(AddFarmWithoutMap.this, "Please Select State", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    AppConstant.stateID = stateId;
                }
                if (areadOfPolygon == null) {

                    Toast.makeText(AddFarmWithoutMap.this, "Please Draw Area", Toast.LENGTH_LONG).show();

                } else {

                    mDrawPolygon = false;//when user came back this page then you have to click draw after this you can drow anything
//                    Intent intent = new Intent(AddFarmOnMap.this, FarmInformation.class);
                    Intent intent = new Intent(AddFarmWithoutMap.this, NavigationDrawerActivity.class);
                    intent.putExtra("calling-activity", AppConstant.AddFarmMap);

                    intent.putExtra("AllLatLngPount", allPoints);
                    System.out.println("fsdfsfsfasdfasf" + allPoints);
                    intent.putExtra("hashMapValue", hashMap);
                    startActivity(intent);
                }

            }
        });
        Intent intent = getIntent();

        String latitude = intent.getStringExtra("lat");
        String longitude = intent.getStringExtra("log");
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("hashMapValue");

        double lat = Double.parseDouble(latitude);
        double log = Double.parseDouble(longitude);

//        System.out.println("" + hashMap.get("353"));


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            NOGPSDialog(this);
        }


        your_array_list = new ArrayList<String>();
//        your_array_list.add("Test String");
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list );

        listView.setAdapter(arrayAdapter);
/*
        runable = new Runnable() {

            @Override
            public void run() {
                try {

                    if(LatLonCellID.currentLat == 0.0 || LatLonCellID.currentLon == 0.0){
                        gpsStatus.setImageResource(R.drawable.gps_not);
                    }else{
                        gpsStatus.setImageResource(R.drawable.gps_got);
                    }

                    if (AppConstant.isWrite) {

                        System.out.println("LatLonCellID.currentLat " + LatLonCellID.currentLat + " , LatLonCellID.currentLon " + LatLonCellID.currentLon);
                        if (LatLonCellID.currentLat == 0.0 || LatLonCellID.currentLon == 0.0) {
                            //Toast.makeText(CreateRouteActivity.this, "NO GPS ! Not able to create route.", Toast.LENGTH_LONG).show();
                            if (AppConstant.isWrite) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddFarmWithoutMap.this);
                                builder.setTitle("NO GPS");
                                builder.setMessage("Not able to create route\nPlease try after some time.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                draw_by_walk.setBackgroundResource(R.drawable.line);
                                                AppConstant.isWrite = false;
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } else {
                            if (currentLat == 0.0 || currentLng == 0.0) {
                                currentLat = LatLonCellID.currentLat;
                                currentLng = LatLonCellID.currentLon;
                            } else {
                                //ToDo Create listview adapter with currentLat and current Lon of this activity and update the adapter
                                your_array_list.add(currentLat+" , "+currentLng);
                                arrayAdapter.notifyDataSetChanged();
                                currentLat = LatLonCellID.currentLat;
                                currentLng = LatLonCellID.currentLon;
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    //also call the same runnable
                    handler.postDelayed(this, HANDLER_INTERVAL);
                }
            }
        };

        handler.postDelayed(runable, HANDLER_INTERVAL);*/

        draw_by_walk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AppConstant.isWrite = !(AppConstant.isWrite);
                if (AppConstant.isWrite) {
                    AppConstant.isRequestedWrite = true;
                    draw_by_walk.setBackgroundColor(Color.RED);
                    AppConstant.routeArray.clear();
                    startTimer();
                } else {
                    if(AppConstant.routeArray!=null && AppConstant.routeArray.size()>2){
                        allPoints = "";
                        for(LatLng point : AppConstant.routeArray) {
                            allPoints = allPoints + "-" +point.latitude + "," + point.longitude;
                        }
                        areadOfPolygon = SphericalUtil.computeArea(AppConstant.routeArray);
                        AppConstant.isRequestedWrite = false;
                    }else{
                        allPoints = "";
                    }
                    draw_by_walk.setBackgroundColor(Color.LTGRAY);
                    draw_by_walk.setBackgroundResource(R.drawable.line);
                    stopTimertask();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(AddFarmWithoutMap.this,HomeActivity.class);
        startActivity(intent);
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 5000); //
    }

    public void stopTimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        System.out.println("timer is running");
                        try {
                            if(LatLonCellID.currentLat == 0.0 || LatLonCellID.currentLon == 0.0){
                                gpsStatus.setImageResource(R.drawable.gps_not);
                            }else{
                                gpsStatus.setImageResource(R.drawable.gps_got);
                            }

                            if (AppConstant.isWrite) {

                                System.out.println("LatLonCellID.currentLat " + LatLonCellID.currentLat + " , LatLonCellID.currentLon " + LatLonCellID.currentLon);
                                if (LatLonCellID.currentLat == 0.0 || LatLonCellID.currentLon == 0.0) {
                                    //Toast.makeText(CreateRouteActivity.this, "NO GPS ! Not able to create route.", Toast.LENGTH_LONG).show();
                                    if (AppConstant.isWrite) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AddFarmWithoutMap.this);
                                        builder.setTitle("NO GPS");
                                        builder.setMessage("Not able to create route\nPlease try after some time.")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        draw_by_walk.performClick();
                                                        draw_by_walk.setBackgroundResource(R.drawable.line);
                                                        AppConstant.isWrite = false;
                                                    }
                                                });

                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }

                                } else {
                                    if (currentLat == 0.0 || currentLng == 0.0) {
                                        currentLat = LatLonCellID.currentLat;
                                        currentLng = LatLonCellID.currentLon;
                                    } else {
                                        //ToDo Create listview adapter with currentLat and current Lon of this activity and update the adapter
                                        your_array_list.add(currentLat+" , "+currentLng);
                                        arrayAdapter.notifyDataSetChanged();
                                        currentLat = LatLonCellID.currentLat;
                                        currentLng = LatLonCellID.currentLon;
                                    }
                                }
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }
                });
            }
        };
    }


    Timer timer;
    TimerTask timerTask;
    final Handler mHandler = new Handler();

    /*public static final int EARTH_RADIUS = 6371 * 1000;
    public static final Handler handler = new Handler();
    public static final int HANDLER_INTERVAL = 5000; // 5 sec
    public static Runnable runable;*/

    double currentLat = 0.0;
    double currentLng = 0.0;

    Polyline currentLine;


    public void NOGPSDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("GPS OFF :\nPlease Enable GPS &\nlaunch again")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddFarmWithoutMap.this.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
