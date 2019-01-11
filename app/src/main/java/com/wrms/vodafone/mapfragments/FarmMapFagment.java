package com.wrms.vodafone.mapfragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.wrms.vodafone.R;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.home.NavigationDrawerActivity;
import com.wrms.vodafone.utils.AppConstant;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Admin on 20-09-2017.
 */
public class FarmMapFagment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MandiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmMapFagment newInstance(String param1, String param2) {
        FarmMapFagment fragment = new FarmMapFagment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, param1);
        args.putString(LONGITUDE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FarmMapFagment() {
        // Required empty public constructor
    }

    private static View view;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */

    private GoogleMap mMap;
    private double latitude, longitude;
    ArrayList<FarmData> mandiArray = new ArrayList<>();


    DBAdapter db;
    Spinner farmSpinner;
    ArrayList<String> allFarmsArray;
    ArrayList<String> allFarmsContour;

    ArrayList<LatLng> points;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = Double.valueOf(getArguments().getString(LATITUDE));
            longitude = Double.valueOf(getArguments().getString(LONGITUDE));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = inflater.inflate(R.layout.farm_map_fragment, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map

        db = new DBAdapter(getActivity());
        points = new ArrayList<LatLng>();

        farmSpinner = (Spinner) view.findViewById(R.id.farm_spinner);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Farm Map");
        farmInfo.setTextColor(Color.WHITE);


      /*  Button mandiData = (Button)view.findViewById(R.id.mandiData);
        mandiData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        // For setting up the MapFragment

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        setUpMapIfNeeded();

        return view;
    }



    /*****
     * Sets up the map if it is possible to do so
     *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.farmMap)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();

                    db.open();
                    Cursor c1 = db.getallContour(AppConstant.user_id);

                    allFarmsArray = new ArrayList<String>();
                    allFarmsContour = new ArrayList<String>();
                    allFarmsArray.add("All");

                    if (c1.moveToFirst()) {
                        do {
                            String ss = c1.getString(c1.getColumnIndex(DBAdapter.FARM_NAME));
                            String c_lat = c1.getString(c1.getColumnIndex(DBAdapter.CENTRE_LAT));
                            String c_lon = c1.getString(c1.getColumnIndex(DBAdapter.CENTRE_LON));
                            String contour = c1.getString(c1.getColumnIndex(DBAdapter.CONTOUR));
                            allFarmsContour.add(contour);
                            allFarmsArray.add(ss);

                            Log.v("contourrrr", "" + contour);

                            FarmData data = new FarmData();

                            data.setFarmerName(ss);

                            if (c_lat != null) {
                                data.setLatitude(Double.parseDouble(c_lat));
                                data.setLongitude(Double.parseDouble(c_lon));
                            }

                            if (mMap != null) {
                                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.home);

                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(data.getLatitude(), data.getLongitude()))
                                        .title("" + data.getFarmerName())
                                        .icon(icon);


                                Marker mMarker = mMap.addMarker(markerOptions);
                                if (mMarker != null) {

                                    Log.v("markerAddd", "Addedddd");
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(), data.getLongitude()), 5));
                                }
                                data.setMarker(mMarker);
                            }
                            mandiArray.add(data);

                            Log.v("contour", "-0" + ss);
                        } while (c1.moveToNext());
                    }
                    db.close();

                    if (mandiArray.size() < 1) {
                        if (mMap != null) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                        }
                    }


                    ArrayAdapter<String> chooseYourFarmSpiner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, allFarmsArray);
                    chooseYourFarmSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    farmSpinner.setAdapter(chooseYourFarmSpiner);

                    farmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i > 0) {
                                String conto = allFarmsContour.get(i - 1);
                                if (conto != null) {
                                    if (mMap != null) {
                                        mMap.clear();

                                    }
                                    points = new ArrayList<LatLng>();
                                    List<String> l_List = Arrays.asList(conto.split("-"));
                                    Double lat1 = null;
                                    Double lon1 =null;

                                   /* Double lat1 = Double.valueOf(l_List.get(0));
                                    Double lon1 = Double.valueOf(l_List.get(l_List.size() - 1));
                                    points.add(new LatLng(lat1, lon1));*/

                                    for (int j = 0; j < l_List.size(); j++) {
                                        String currentString = l_List.get(j);
                                        if (currentString != null) {
                                            String[] separated = currentString.split(",");
                                            if (separated.length>1) {
                                                String la = separated[0];
                                                String lo = separated[1];

                                                lat1=Double.parseDouble(la);
                                                lon1=Double.parseDouble(lo);

                                                points.add(new LatLng(Double.valueOf(la), Double.valueOf(lo)));

                                                Log.v("points",la+","+lo);
                                            }
                                        }
                                    }

                                    if (lat1 != null) {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat1, lon1), 19.0f));
                                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                                        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
                                        SharedPreferences.Editor ed = prefs.edit();
                                        ed.putString("lat",lat1+"");
                                        ed.putString("lon",lon1+"");
                                        ed.apply();

                                    } else {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)), 13.0f));
                                        Log.v("latlon2", lat1 + "---" + lon1);
                                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                                        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
                                        SharedPreferences.Editor ed = prefs.edit();
                                        ed.putString("lat",latitude+"");
                                        ed.putString("lon",longitude+"");
                                        ed.apply();
                                    }
                                    if (mMap != null) {
                                        mMap.clear();
                                        setUpMap();
                                    }
                                }
                            } else {

                                if (mMap != null) {
                                    mMap.clear();

                                }

                                db.open();
                                Cursor c1 = db.getallContour(AppConstant.user_id);

                                allFarmsArray = new ArrayList<String>();
                                allFarmsContour = new ArrayList<String>();
                                allFarmsArray.add("All");

                                if (c1.moveToFirst()) {
                                    do {
                                        String ss = c1.getString(c1.getColumnIndex(DBAdapter.FARM_NAME));
                                        String c_lat = c1.getString(c1.getColumnIndex(DBAdapter.CENTRE_LAT));
                                        String c_lon = c1.getString(c1.getColumnIndex(DBAdapter.CENTRE_LON));
                                        String contour = c1.getString(c1.getColumnIndex(DBAdapter.CONTOUR));
                                        allFarmsContour.add(contour);
                                        allFarmsArray.add(ss);

                                        FarmData data = new FarmData();

                                        data.setFarmerName(ss);

                                        if (c_lat != null) {
                                            data.setLatitude(Double.parseDouble(c_lat));
                                            data.setLongitude(Double.parseDouble(c_lon));
                                        }

                                        if (mMap != null) {
                                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.home);

                                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(data.getLatitude(), data.getLongitude()))
                                                    .title("" + data.getFarmerName())
                                                    .icon(icon);


                                            Marker mMarker = mMap.addMarker(markerOptions);
                                            if (mMarker != null) {

                                                Log.v("markerAddd", "Addedddd");
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(), data.getLongitude()), 5));
                                            }
                                            data.setMarker(mMarker);
                                        }
                                        mandiArray.add(data);

                                        Log.v("contour", "-0" + ss);
                                    } while (c1.moveToNext());
                                }
                                db.close();

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    // Setting a custom info window adapter for the google map
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        // Use default InfoWindow frame
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        // Defines the contents of the InfoWindow
                        @Override
                        public View getInfoContents(Marker arg0) {
                            View v = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
                            // Getting reference to the TextView to set latitude
                            TextView tvLat = (TextView) v.findViewById(R.id.title);

                            // Getting reference to the TextView to set longitude
                            TextView tvLng = (TextView) v.findViewById(R.id.distance);
                            System.out.println("Title : " + arg0.getTitle());
                            if (arg0.getTitle() != null && arg0.getTitle().length() > 0) {
                                // Getting the position from the marker

                                final String title = arg0.getTitle();

                                db.open();
                                Cursor c = db.getStateFromSelectedFarm(title);
                                if (c.moveToFirst()) {
                                    do {
                                        AppConstant.stateID = c.getString(c.getColumnIndex(DBAdapter.STATE_ID));
                          /*  String contour = c.getString(c.getColumnIndex(DBAdapter.CONTOUR));
                            getAtLeastOneLatLngPoint(contour);*/
                                    }
                                    while (c.moveToNext());
                                }
                                db.close();

                                final String distance = arg0.getSnippet();
                                tvLat.setText(title);
                                tvLng.setText(distance);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(title).
                                        setMessage(distance).
                                        setPositiveButton("Farm Data", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                dialogInterface.cancel();
                                                Intent intent = new Intent(getActivity(), NavigationDrawerActivity.class);
                                                intent.putExtra("calling-activity", AppConstant.HomeActivity);
                                                intent.putExtra("FarmName", title);

                                                startActivity(intent);
                                                getActivity().finish();


                                            }
                                        }).
                                        setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                builder.show();

                            } else {
                                // Setting the latitude
                                tvLat.setText(String.valueOf(arg0.getPosition().latitude));
                                // Setting the longitude
                                tvLng.setText(String.valueOf(arg0.getPosition().longitude));
                            }
                            return v;
                        }
                    });


                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

            }
        }
    }

    private void setUpMap() {
        // For showing a move to my loction button


        // For dropping a marker at a point on the Map
       /* mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));*/
        if (points.size()>2) {
            drawCircle();
        }


    }
    public void drawCircle() {
        // Clear the map to remove the previous circle
        mMap.clear();
        // Generate the points
        mMap.addPolygon(new PolygonOptions().addAll(points).strokeWidth(5).strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
        // Create and return the polygon
        for (int i=0;i<points.size();i++){
            Log.v("DrawCircle","drwaakmdaskfmlsmn"+points.get(i));
        }



    }

/*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
*/

    @Override
    public void onResume() {
        super.onResume();

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Farm Map");
        farmInfo.setTextColor(Color.WHITE);



    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();

/*        try {
            Fragment fragment = (getFragmentManager()
                    .findFragmentById(R.id.mandiMap));
            if(fragment!=null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class FarmData {

        String farmerName;
        double latitude;
        double longitude;

        Marker marker;


        public String getFarmerName() {
            return farmerName;
        }

        public void setFarmerName(String farmerName) {
            this.farmerName = farmerName;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }


        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }

}
