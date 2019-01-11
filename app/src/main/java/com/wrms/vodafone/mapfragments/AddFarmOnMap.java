package com.wrms.vodafone.mapfragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.wrms.vodafone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.LocationData;
import com.wrms.vodafone.home.NavigationDrawerActivity;
import com.wrms.vodafone.utils.AppConstant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AddFarmOnMap extends FragmentActivity {
    Boolean mDrawPolygon = false;
    PolygonOptions rectOptions;
    LocationData locationData;
    Button drawPolygon;
    Button mapViewType;
    Button nextBtn;
    Button draw_by_walk;
    int flag = 0;
    ArrayList<LatLng> arrayPoints = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> points = null;
    Double areadOfPolygon = null;
    String allPoints = "";
    int flagForDraw = 0;
    HashMap<String, String> hashMap;
    ImageView gpsStatus;
    Spinner stateSpinner;
    DBAdapter db;
    TextView alertTitle;
    FloatingActionButton homeReturn;

    Button distanceBTN;
    LinearLayout clearLay;
    Button drawOnMapBTN;
    Button clearMapBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farm_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        drawOnMapBTN = (Button)findViewById(R.id.draw_poly);
        clearLay = (LinearLayout)findViewById(R.id.clear_lay);
        clearMapBTN = (Button)findViewById(R.id.clear_map);

        drawOnMapBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayPoints.size()>2) {
                    rectOptions.fillColor(Color.GRAY);
                    rectOptions.strokeWidth(5);
                    mMap.addPolygon(rectOptions);
                    // allPoints = allPoints.substring(1, allPoints.length() - 1);
                    Log.d("allPoints", allPoints);
                    areadOfPolygon = SphericalUtil.computeArea(arrayPoints);
                }else {
                    Toast.makeText(getApplicationContext(),"Please click atleast 3 points.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearMapBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawPolygon = true;
                arrayPoints.clear();
                mMap.clear();
                allPoints = null;
                rectOptions = new PolygonOptions(); // create a new object and remove previous garbage
                areadOfPolygon = null;
                clearLay.setVisibility(View.GONE);
            }
        });

        AppConstant.isWrite = false;

        AppConstant.routeArray.clear();
        AppConstant.maxDistance = 25.0;

        setUpMapIfNeeded();
        db = new DBAdapter(AddFarmOnMap.this);
        db.open();

        homeReturn = (FloatingActionButton) findViewById(R.id.home_return);

        distanceBTN = (Button) findViewById(R.id.distance);
        distanceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                distancePopup();
            }
        });

        homeReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mapViewType = (Button) findViewById(R.id.mapView);
        nextBtn = (Button) findViewById(R.id.next);
        drawPolygon = (Button) findViewById(R.id.draw_button);
        gpsStatus = (ImageView) findViewById(R.id.gpsStatus);
        alertTitle = (TextView) findViewById(R.id.alertTitle);

        draw_by_walk = (Button) findViewById(R.id.draw_by_walk);
        stateSpinner = (Spinner) findViewById(R.id.stateList);

        // drawPolygon.setOnTouchListener(this);
        rectOptions = new PolygonOptions();
        arrayPoints = new ArrayList<LatLng>();


        ArrayList<String> stateList = new ArrayList<>();
        final Cursor stateCursor = db.getAllStates();
        stateList.add("Select State");
        if (stateCursor.getCount() > 0) {
            stateCursor.moveToFirst();
            do {
                stateList.add(stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_NAME)));
            } while (stateCursor.moveToNext());
        }
        ArrayAdapter<String> stateListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, stateList);
        stateSpinner.setAdapter(stateListAdapter);
        db.close();

        int a = stateListAdapter.getPosition("Maharashtra");
        if (a >= 0) {
            stateSpinner.setSelection(a);
        }

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    stateCursor.moveToPosition(position - 1);
                    AppConstant.stateID = stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_ID));
                    AppConstant.state = stateCursor.getString(stateCursor.getColumnIndex(DBAdapter.STATE_NAME));
                }/*else{
                    AppConstant.stateID = "-1";
                    AppConstant.state = "";
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (allPoints != null && allPoints.length() > 2) {
                    if (allPoints.substring(0, 1).equals("-")|| allPoints.substring(0, 1).equals("null")||allPoints.substring(0, 1).equals(null)) {
                        allPoints = allPoints.substring(1, allPoints.length() - 1); // this will remove dess(-) from string at starting point
                    }
                }
                if (areadOfPolygon == null || areadOfPolygon == 0.0) {

                    Toast.makeText(AddFarmOnMap.this, "Please Draw Area", Toast.LENGTH_LONG).show();

                } else {

                    mDrawPolygon = false;//when user came back this page then you have to click draw after this you can drow anything
                    int selectedStatePosition = stateSpinner.getSelectedItemPosition();
                    if (selectedStatePosition > 0) {
                        AddFarmOnMap.this.finish();
                        Intent intent = new Intent(AddFarmOnMap.this, NavigationDrawerActivity.class);
                        intent.putExtra("calling-activity", AppConstant.AddFarmMap);
                        intent.putExtra("AllLatLngPount", allPoints);
                        Double acreageArea = (0.000247105) * areadOfPolygon;
                        intent.putExtra("Area", acreageArea.toString());
                        System.out.println("AllPoints" + allPoints);
                        System.out.println("areainmeter" + areadOfPolygon);
                        System.out.println("areainacre" + acreageArea);
                        intent.putExtra("hashMapValue", hashMap);
                        intent.putExtra("add", "add");
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddFarmOnMap.this, "Please Select State", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        nextBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Button Pressed
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //finger was lifted
                }
                return false;
            }
        });

        if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
            drawPolygon.setEnabled(false);
        } else {
            drawPolygon.setEnabled(true);
        }

        drawPolygon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!AppConstant.isWrite) {
                    mDrawPolygon = !mDrawPolygon;
                    if (mDrawPolygon) {
                        mDrawPolygon = true;
                        arrayPoints.clear();
                        mMap.clear();
                        rectOptions = new PolygonOptions(); // create a new object and remove previous garbage
                        areadOfPolygon = null;
                        drawPolygon.setBackgroundColor(Color.RED);
                    } else {
                        drawPolygon.setBackgroundResource(R.drawable.line);
                    }
                } else {
                    Toast.makeText(AddFarmOnMap.this, "Points are being captured by other option", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mapViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }

            }

        });


        Intent intent = getIntent();

        String latitude = intent.getStringExtra("lat");
        String longitude = intent.getStringExtra("log");
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("hashMapValue");

        double lat = Double.parseDouble(latitude);
        double log = Double.parseDouble(longitude);

        // System.out.println("" + hashMap.get("353"));


        draw_by_walk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!mDrawPolygon) {
//                    AppConstant.isWrite = !(AppConstant.isWrite);
                    if (!AppConstant.isWrite) {
                        AppConstant.isWrite = true;
                        alertTitle.setVisibility(View.VISIBLE);
                        AppConstant.isRequestedWrite = true;
                        draw_by_walk.setBackgroundColor(Color.RED);
                        AppConstant.routeArray.clear();
                        arrayPoints.clear();
                        mMap.clear();
                        rectOptions = new PolygonOptions(); // creat a new object and remove previous garbage
                        areadOfPolygon = null;
                        startTimer();
                    } else {

                        if (AppConstant.routeArray != null && AppConstant.routeArray.size() > 2) {

                            LatLng firstPoint = AppConstant.routeArray.get(0);
                            LatLng lastPoint = AppConstant.routeArray.get((AppConstant.routeArray.size() - 1));
                            double distance = distFrom(firstPoint.latitude, firstPoint.longitude, lastPoint.latitude, lastPoint.longitude);
                            System.out.println("DistanceLast: " + distance);
                            //if distance between first and last point is more then 15 meter then ask user to go to the starting point
                            if (distance > 15) {
                                incompleteFarmAlert();
                            } else {
                                AppConstant.isWrite = false;
                                alertTitle.setVisibility(View.GONE);
                                currentLine = mMap.addPolyline(new PolylineOptions()
                                        .add(firstPoint, lastPoint).width(7).color(Color.GREEN).geodesic(true));
                                allPoints = "";
                                for (LatLng point : AppConstant.routeArray) {
                                    allPoints = allPoints + "-" + point.latitude + "," + point.longitude;
                                }
                                areadOfPolygon = SphericalUtil.computeArea(AppConstant.routeArray);
                                draw_by_walk.setBackgroundResource(R.drawable.line);
                                stopTimertask();
                            }
                        } else {
                            allPoints = "";
                            draw_by_walk.setBackgroundResource(R.drawable.line);
                            stopTimertask();
                        }

                    }
                } else {
                    Toast.makeText(AddFarmOnMap.this, "Points are being captured by other option", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 100, 5000); //
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
                mHandler.post(runnable);
            }
        };
    }

    private void recreateAlertDialoug() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFarmOnMap.this);
        builder.setTitle("Points Missed");
        builder.setMessage("We have missed some points\nPlease Recreate the farm")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AppConstant.isWrite = true;
                        AppConstant.isRequestedWrite = true;
                        draw_by_walk.setBackgroundColor(Color.RED);
                        AppConstant.routeArray.clear();
                        arrayPoints.clear();
                        mMap.clear();
                        rectOptions = new PolygonOptions(); // creat a new object and remove previous garbage
                        areadOfPolygon = null;
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void incompleteFarmAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFarmOnMap.this);
        builder.setTitle("Farm is Incomplete");


        builder.setMessage("Please go to the starting point\nOr\n Discard the Farm")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                        AppConstant.isWrite = false;
                        alertTitle.setVisibility(View.GONE);
                        AppConstant.isRequestedWrite = false;
                        draw_by_walk.setBackgroundResource(R.drawable.line);
                        AppConstant.routeArray.clear();
                        arrayPoints.clear();
                        mMap.clear();
                        rectOptions = new PolygonOptions(); // creat a new object and remove previous garbage
                        areadOfPolygon = null;
                        stopTimertask();
                    }
                })
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (AppConstant.routeArray != null && AppConstant.routeArray.size() > 2) {

                            LatLng firstPoint = AppConstant.routeArray.get(0);
                            LatLng lastPoint = AppConstant.routeArray.get((AppConstant.routeArray.size() - 1));
                            double distance = distFrom(firstPoint.latitude, firstPoint.longitude, lastPoint.latitude, lastPoint.longitude);
                            System.out.println("Distance : " + distance);
                            //if distance between first and last poin is more then 15 meter then ask user to go to the starting point

                            AppConstant.isWrite = false;
                            alertTitle.setVisibility(View.GONE);
                            currentLine = mMap.addPolyline(new PolylineOptions()
                                    .add(firstPoint, lastPoint)
                                    .width(7).color(Color.GREEN).geodesic(true));
                            allPoints = "";
                            for (LatLng point : AppConstant.routeArray) {
                                allPoints = allPoints + "-" + point.latitude + "," + point.longitude;
                            }
                            areadOfPolygon = SphericalUtil.computeArea(AppConstant.routeArray);
                            draw_by_walk.setBackgroundResource(R.drawable.line);
                            stopTimertask();

                        } else {
                            allPoints = "";
                            draw_by_walk.setBackgroundResource(R.drawable.line);
                            stopTimertask();
                        }

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    Timer timer;
    TimerTask timerTask;
    final Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            try {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    NOGPSDialog(AddFarmOnMap.this);
                }

                if (LatLonCellID.lat == 0.0 || LatLonCellID.lon == 0.0) {
                    gpsStatus.setImageResource(R.drawable.gps_not);
                    alertTitle.setText("NO GPS");
                    alertTitle.setBackgroundColor(getResources().getColor(R.color.red_alret));
                } else {
                    gpsStatus.setImageResource(R.drawable.gps_got);
                    alertTitle.setText("GET GPS");
                    alertTitle.setBackgroundColor(getResources().getColor(R.color.green_alert));
                }

                if (AppConstant.isWrite) {

                    System.out.println("LatLonCellID.lat " + LatLonCellID.lat + " , LatLonCellID.currentLon " + LatLonCellID.lon);
                    if (LatLonCellID.lat == 0.0 || LatLonCellID.lon == 0.0) {
                        if (AppConstant.isRequestedWrite) {
                            stopAlert = true;
                            AppConstant.isRequestedWrite = false;
                            audioPlayer();
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddFarmOnMap.this);
                            builder.setTitle("No GPS Stop Alert");
                            builder.setMessage("Not able to create route\nPlease wait for GPS to start creating route.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            stopAlert = true;
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    } else {
                        if (currentLat == 0.0 || currentLng == 0.0) {

                            currentLat = LatLonCellID.lat;
                            currentLng = LatLonCellID.lon;

                            previousLat = currentLat;
                            previousLng = currentLng;

                        } else {

                            if (stopAlert) {
                                stopAlert = false;
                                AppConstant.isRequestedWrite = true;
                                double distance = distFrom(currentLat, currentLng, LatLonCellID.lat, LatLonCellID.lon);

                                System.out.println("Distanceeeee:" + distance);

                                if (distance > 25) {
                                    recreateAlertDialoug();
                                    return;
                                }
                            }

                            if (AppConstant.routeArray.size() > 0) {

                                LatLng ll = AppConstant.routeArray.get(AppConstant.routeArray.size() - 1);

                                previousLat = ll.latitude;
                                previousLng = ll.longitude;

                                currentLat = ll.latitude;
                                currentLng = ll.longitude;
                            }


                            double distt = distFrom(previousLat, previousLng, LatLonCellID.lat, LatLonCellID.lon);

                            Log.v("totalDistance", "" + distt);

                            if (distt < AppConstant.maxDistance) {


                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 17));
                                currentLine = mMap.addPolyline(new PolylineOptions().add(new LatLng(currentLat, currentLng), new LatLng(LatLonCellID.lat, LatLonCellID.lon)).width(7).color(Color.GREEN).geodesic(true));

                                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                if (AppConstant.routeArray.size() == 1) {
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                    mMap.addMarker(new MarkerOptions().position(AppConstant.routeArray.get(AppConstant.routeArray.size() - 1)).icon(icon).title("count"));
                                }

                                mMap.addMarker(new MarkerOptions().position(new LatLng(LatLonCellID.lat, LatLonCellID.lon)).icon(icon).title("count"));
                            } else {

                            }
                            currentLat = LatLonCellID.lat;
                            currentLng = LatLonCellID.lon;
                            previousLat = currentLat;
                            previousLng = currentLng;
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    };
    private boolean stopAlert = false;


    public static final int EARTH_RADIUS = 6371000;
    //    public static final Handler handler = new Handler();
    public static final int HANDLER_INTERVAL = 5000; // 5 sec
    public static Runnable runable;
    /*public static ArrayList<LatLng> routeArray = new ArrayList<>();
    public static boolean isWrite = false;
    public static boolean isRequestedWrite = false;*/
    double currentLat = 0.0;
    double currentLng = 0.0;

    Polyline currentLine;

    double previousLat = 0.0;
    double previousLng = 0.0;


    private ArrayList<LatLng> filterRoute(ArrayList<LatLng> unfilteredList) {

        ArrayList<LatLng> filteredRoute = new ArrayList<>();

        for (int i = 0; i < unfilteredList.size() - 2; i++) {

            if (i == 0) {
                filteredRoute.add(unfilteredList.get(i));
            }

            boolean isRight = false;

            //Get (x,y) coordinates on plane

            double ax = EARTH_RADIUS * Math.sin(Math.toRadians(unfilteredList.get(i).latitude)) * Math.cos(Math.toRadians(unfilteredList.get(i).longitude));
            //System.out.println("x "+ax);
            double ay = EARTH_RADIUS * Math.sin(Math.toRadians(unfilteredList.get(i).latitude)) * Math.sin(Math.toRadians(unfilteredList.get(i).longitude));
            //System.out.println("y "+ay);
            double bx = EARTH_RADIUS * Math.sin(Math.toRadians(unfilteredList.get(i + 1).latitude)) * Math.cos(Math.toRadians(unfilteredList.get(i + 1).longitude));
            //System.out.println("x "+bx);
            double by = EARTH_RADIUS * Math.sin(Math.toRadians(unfilteredList.get(i + 1).latitude)) * Math.sin(Math.toRadians(unfilteredList.get(i + 1).longitude));
            //System.out.println("y "+by);
            double cx = EARTH_RADIUS * Math.sin(Math.toRadians(unfilteredList.get(i + 2).latitude)) * Math.cos(Math.toRadians(unfilteredList.get(i + 2).longitude));
            //System.out.println("x "+cx);
            double cy = EARTH_RADIUS * Math.sin(Math.toRadians(unfilteredList.get(i + 2).latitude)) * Math.sin(Math.toRadians(unfilteredList.get(i + 2).longitude));
            //System.out.println("y "+cy);
            isRight = (((bx - ax) * (cy - ay) - (by - ay) * (cx - ax)) > 0);
            //System.out.println("is Right  "+isRight);

//            get edges of the triangle

            double A = distFrom(unfilteredList.get(i).latitude, unfilteredList.get(i).longitude, unfilteredList.get(i + 1).latitude, unfilteredList.get(i + 1).longitude);
            //System.out.println("A "+(int)A);
            double B = distFrom(unfilteredList.get(i + 1).latitude, unfilteredList.get(i + 1).longitude, unfilteredList.get(i + 2).latitude, unfilteredList.get(i + 2).longitude);
            //System.out.println("B "+(int)B);
            double C = distFrom(unfilteredList.get(i + 2).latitude, unfilteredList.get(i + 2).longitude, unfilteredList.get(i).latitude, unfilteredList.get(i).longitude);

//          Calculate angle between the edges
            double cosTheata = (-(C * C - A * A - B * B) / (2 * A * B));

//            Convert angle in to degrees
            int angle = (int) Math.toDegrees(Math.acos(cosTheata));
            System.out.println("(" + i + ")  Math.toDegrees(Math.acos(cosTheata))   " + angle);

//          check angle if it is more then 170( 10 degrees in inverse) degree then consider it as valid point
            if (angle < 170) {
                filteredRoute.add(unfilteredList.get(i + 1));
            }


        }
        return filteredRoute;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (double) (earthRadius * c);

        return dist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }

    public void NOGPSDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("GPS OFF :\nPlease Enable GPS")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
//                        AddFarmOnMap.this.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void audioPlayer() {
        try {
            AssetFileDescriptor afd = getAssets().openFd("beep.mp3");
            //set up MediaPlayer
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */


    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();
                }
            });
            // Check if we were successful in obtaining the map.


        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        Intent intent = getIntent();

        String latitude = intent.getStringExtra("lat");
        String longitude = intent.getStringExtra("log");

        double lat = Double.parseDouble(latitude);
        double log = Double.parseDouble(longitude);

        // System.out.println("" + hashMap.get("353"));

        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, log)).title("Hello Maps");
//// adding marker
        mMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, log)).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                //  Take some action here
//
//                                              rectOptions.fillColor(Color.GRAY);
//                                              rectOptions.strokeWidth(5);
//                                              mMap.addPolygon(rectOptions);
////                                                int x =  arrayPoints.size();
//                                           //  Double d =  CalculateArea.calculateAreaOfGPSPolygonOnEarthInSquareMeters(arrayPoints);
//                                              //this is preparing string in the special format for sending to server in the farm information class
//                                              allPoints = allPoints.substring(1,allPoints.length()-1);
//                                              Log.d("allPoints", allPoints);
//                                              areadOfPolygon = SphericalUtil.computeArea(arrayPoints);
//                                             Toast.makeText(AddFarmOnMap.this, "Area in Squaremiter" + areadOfPolygon, Toast.LENGTH_LONG).show();

                return true;
            }

        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(LatLng point) {

                if (mDrawPolygon) { //if the value is true then draw polygon. value will be true from drawPolygon click event

                    // TODO Auto-generated method stub
                    //  arrayPoints.add(new LatLng(point.latitude, point.longitude));
                    arrayPoints.add(point);
                    Log.d("arg0----------", point.latitude + "***************" + point.longitude);
                    //STORE ALL POINT HERE THAT FURTHER SEND FOR SERVER REQUEST WITH FARM INFORMATIOS

                    allPoints = allPoints + "-" + point.latitude + "," + point.longitude;
                    Log.d("allpoints-------", allPoints);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(point);
                    markerOptions.title("Position");
//                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot_marker);
                    BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    markerOptions.icon(icon);
                    Double lat = point.latitude;
                    Double log = point.longitude;
                    //below lat lng point wil use in createdStringforFieldInprovemnt() method fo farminformation class
                    AppConstant.latitude = String.valueOf(point.latitude);
                    AppConstant.longitude = String.valueOf(point.longitude);
                    rectOptions.add(point);
                    mMap.addMarker(markerOptions);

                    if (flagForDraw > 2) {

                        clearLay.setVisibility(View.VISIBLE);
                        /*rectOptions.fillColor(Color.GRAY);
                        rectOptions.strokeWidth(5);
                        mMap.addPolygon(rectOptions);
                        // allPoints = allPoints.substring(1, allPoints.length() - 1);
                        Log.d("allPoints", allPoints);
                        areadOfPolygon = SphericalUtil.computeArea(arrayPoints);*/
                    }else {
                        clearLay.setVisibility(View.GONE);
                    }
                    flagForDraw++;

                }

            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // TODO Auto-generated method stub

                mDrawPolygon = false;

            }
        });


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
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location location = getLocation();


                AppConstant.latitude = ""+location.getLatitude();
                AppConstant.longitude = ""+location.getLongitude();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(19).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                /*MarkerOptions marker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current Location");
                mMap.addMarker(marker);*/
                return true;


            }
        });


    }
    //not applicable yet below code
    private Location getLocation() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        return locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

    }

    public String decmilFormat(Double point) {

        DecimalFormat df = new DecimalFormat("#.######");
        String d = df.format(point);
        System.out.println("latitude deccimle point" + d);
        return d;

    }


    public void distancePopup() {

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        // Include dialog.xml file
        dialog.setContentView(R.layout.distance_popup);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button btn50 = (Button) dialog.findViewById(R.id.distance_10);
        Button btn100 = (Button) dialog.findViewById(R.id.distance_20);
        Button btn150 = (Button) dialog.findViewById(R.id.distance_50);
        Button btn200 = (Button) dialog.findViewById(R.id.distance_100);
        Button nolimit = (Button) dialog.findViewById(R.id.nolimit);
        btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                AppConstant.maxDistance = 10.0;
            }
        });

        btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                AppConstant.maxDistance = 20.0;
            }
        });

        btn150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                AppConstant.maxDistance = 50.0;
            }
        });

        btn200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                AppConstant.maxDistance = 100.0;
            }
        });

        nolimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                AppConstant.maxDistance = 100000.0;
            }
        });


        dialog.show();
    }



}





