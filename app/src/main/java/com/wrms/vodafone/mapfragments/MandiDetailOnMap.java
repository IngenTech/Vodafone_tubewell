package com.wrms.vodafone.mapfragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wrms.vodafone.R;
import com.wrms.vodafone.entities.MandiDetail;
import com.wrms.vodafone.utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

public class MandiDetailOnMap extends FragmentActivity {
    double latitude;
    double longitude;
    double lat;
    double lng;
    MarkerOptions marker1;
    Button normal;
    Button satellite;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<MandiDetail> mandiDetail = new ArrayList<MandiDetail>();

    HashMap<String, MandiDetails> markarMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandi_detail_on_map);

        normal = (Button)findViewById(R.id.normal);
        satellite = (Button)findViewById(R.id.sattelite);

        setUpMapIfNeeded();

        Intent intent = getIntent();
        mandiDetail = intent.getParcelableArrayListExtra(AppConstant.MANDI_DETAIL);
        String mandi = mandiDetail.get(0).getLatitude().toString();
        System.out.println("latitude of mandi detail" + mandi);

        lat = Double.parseDouble(AppConstant.latitude);
        lng = Double.parseDouble(AppConstant.longitude);
        System.out.println("AppConstant.latitude" + lat);
        System.out.println("AppConstant.longitude" + lng);


        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });
        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
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
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap  = googleMap;

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);

                    marker1 = new MarkerOptions().position(new LatLng(lat, lng)).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.home_markar));
//// adding marker
                    mMap.addMarker(marker1);

                    for(int i=0;i<mandiDetail.size();i++)
                    {

                        latitude = Double.parseDouble( mandiDetail.get(i).getLatitude().toString());
                        longitude = Double.parseDouble(mandiDetail.get(i).getLongitude().toString());
                        String location = mandiDetail.get(i).getLocation().toString();
                        String price = mandiDetail.get(i).getPrice().toString();
                        String unit = mandiDetail.get(i).getUnit().toString();
                        String distance =  mandiDetail.get(i).getDistance().toString();

                        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).snippet("" + i);

                        markarMap.put(String.valueOf(i), new MandiDetails(location, price, unit, distance));


                        mMap.addMarker(marker);
                    }


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(

                            new LatLng(lat, lng)).zoom(8).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

                        @Override
                        public boolean onMarkerClick(Marker marker){
                            if(!marker.getId().contains("0")) {
                                marker.showInfoWindow();
                                bounceMarker(marker);
                            }

                            return true;

                        }

                    });
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        // Use default InfoWindow frame
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                            LatLng latLng = marker.getPosition();
                            String markerMapKey = marker.getSnippet();
                            MandiDetails details = markarMap.get(markerMapKey);
                            try {
                                TextView location = (TextView) v.findViewById(R.id.textViewLocation);
                                TextView price = (TextView) v.findViewById(R.id.textViewPrice);
                                TextView unit = (TextView) v.findViewById(R.id.textViewUnit);
                                TextView distance = (TextView) v.findViewById(R.id.textViewDistance);
                                // location.setText("Location:"+mandiDetail.get(0).getLocation().toString());
                                location.setText("Location: " + details.getLocation().toString());
                                price.setText("Price: " + details.getPrice().toString() + "Rs.");
                                unit.setText("Unit: " + details.getUnit().toString());
                                distance.setText("Distance: " + details.getDistance().toString() + "km");
//                price.setText("Price:"+ mandiDetail.get(0).getPrice().toString());
//                unit.setText("Unit:"+mandiDetail.get(0).getUnit().toString());
//                distance.setText("Distance" + mandiDetail.get(0).getDistance().toString() + "km");
                            }catch (Exception e) {
                                e.printStackTrace();
                            }

                            return v;

                        }
                    });

                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
               // setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Your Location"));
    }

    public  class MandiDetails
    {
        String location;
        String price;
        String unit;
        String distance;

        public MandiDetails(String location, String price, String unit, String distance) {
            this.location = location;
            this.price = price;
            this.unit = unit;
            this.distance = distance;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }
    //////
    private void bounceMarker(final Marker marker){
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 1500;
        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

    }
    //////////
}
