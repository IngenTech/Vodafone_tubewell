package com.wrms.vodafone.mapfragments;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.wrms.vodafone.utils.AppConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LatLonCellID extends Service {
    public static double lat = 0.0, lon = 0.0, speed = 0.0;
    public static String imeino, datetimestamp = null;
    LocationManager _locationManager;
    LocationListener _connector;
    public static double currentLat = 0.0;
    public static double currentLon = 0.0;

    TelephonyManager tm;
    GsmCellLocation gsm_cell_location;

    @Override
    public void onCreate() {
        try {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imeino = tm.getDeviceId();
            gsm_cell_location = (GsmCellLocation) tm.getCellLocation();

            //CellID = gsm_cell_location.getCid();
            turnGPSOn();
            _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

         /*   Criteria locationCritera = new Criteria();
            locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
            locationCritera.setAltitudeRequired(false);
            locationCritera.setBearingRequired(false);
            locationCritera.setCostAllowed(true);
            locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

            String providerName = _locationManager.getBestProvider(locationCritera, true);
            if (providerName!=null) {
                Location location = _locationManager.getLastKnownLocation(providerName);

                if (location!=null){

                    Log.i("--- Latitude1",""+location.getLatitude());
                    Log.i("--- Latitude11",""+location.getLongitude());

                    LatLonCellID.lat = location.getLatitude();
                    LatLonCellID.lon = location.getLongitude();
                }

                LatLonCellID.lat = location.getLatitude();
                LatLonCellID.lon = location.getLongitude();
                Log.i("--- Latitude22",""+location.getLatitude());
                Log.i("--- Latitude22",""+location.getLongitude());
            }*/



            _connector = new MyLocationListener();
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _connector);
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _connector);

            final Timer timer = new Timer();

            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(LatLonCellID.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(LatLonCellID.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _connector);
              //      _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _connector);
                    if (lat != 0.0 && lon != 0.0) {
                        if (AppConstant.isWrite) {
                            if (AppConstant.routeArray != null) {
                                AppConstant.routeArray.add(new LatLng(lat, lon));
                            }
                        }

                    } else {
                        //System.out.println("Trying to get GPS Signal");
                    }
                    LatLonCellID.currentLat = LatLonCellID.lat;
                    LatLonCellID.currentLon = LatLonCellID.lon;
                  //  lat = 0.0;
                 //
                    //   lon = 0.0;
                }

            }, 10000, 5000);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("Exception" + e);
        }
        //stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void turnGPSOn() {
        try {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (!provider.contains("gps")) { //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}

class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        //Toast.makeText(get, text, duration)
        LatLonCellID.lat = loc.getLatitude();
        LatLonCellID.lon = loc.getLongitude();
        LatLonCellID.speed = loc.getSpeed();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LatLonCellID.datetimestamp = sdf.format(new Date(loc.getTime()));

        System.out.println("Lat_Lon_CellID.No Found "+loc);
        if (loc != null) {
            System.out.println("Lat_Lon_CellID.lat "+loc);
            //System.out.println("Lat_Lon_CellID.lon "+LatLonCellID.lon);
        }
        System.out.println("Get the GPS Signal"+"-"+ LatLonCellID.lat+"-"+ LatLonCellID.lon);

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}

