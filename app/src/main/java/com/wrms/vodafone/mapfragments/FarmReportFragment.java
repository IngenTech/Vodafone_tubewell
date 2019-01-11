package com.wrms.vodafone.mapfragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.wrms.vodafone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Admin on 03-08-2017.
 */
public class FarmReportFragment extends Fragment {
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
     * @return A new instance of fragment OptimalMandiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmReportFragment newInstance(String param1, String param2) {
        FarmReportFragment fragment = new FarmReportFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, param1);
        args.putString(LONGITUDE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FarmReportFragment() {
        // Required empty public constructor
    }

    private static View view;
    private GoogleMap mMap;
    private double latitude, longitude;
    ArrayList<OptimalMandiData> mandiArray = new ArrayList<>();
    ArrayList<Crop> cropArray = new ArrayList<>();
    ArrayList<UserBean> userArray = new ArrayList<>();
    ArrayList<StateBean> stateArray = new ArrayList<>();
    ArrayList<GroupBean> groupArray = new ArrayList<>();
    ArrayList<String> varietyArray = new ArrayList<>();
    Spinner farmerGroupSpinner;
    Spinner userSpinner;
    Spinner stateSpinner;
    Spinner cropSpinner;
    EditText dateFrom, dateTo;
    Button getFarmsBTN;

    SharedPreferences prefs;
    String crop_id = "", state_id = "", group_id = "", user_id = "";
    private static String DATE_FORMAT_STRING = "dd-MM-yyyy";
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = Double.valueOf(getArguments().getString(LATITUDE));
            longitude = Double.valueOf(getArguments().getString(LONGITUDE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (LinearLayout) inflater.inflate(R.layout.farm_report_activity, container, false);

        setUpMapIfNeeded(); // For setting up the MapFragment

        cropSpinner = (Spinner) view.findViewById(R.id.crop_spinner);
        farmerGroupSpinner = (Spinner) view.findViewById(R.id.group_spinner);
        userSpinner = (Spinner) view.findViewById(R.id.user_spinner);
        stateSpinner = (Spinner) view.findViewById(R.id.state_spinner);
        dateFrom = (EditText) view.findViewById(R.id.date_from);
        dateTo = (EditText) view.findViewById(R.id.date_to);
        getFarmsBTN = (Button) view.findViewById(R.id.get_farmReport_BTN);

        if (prefs == null) {
            prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
        }

        String userID = prefs.getString(AppConstant.PREFRENCE_KEY_USER_ID, "");
        if (userID != null) {

            getAllSpinnerData(userID);

        } else {
            Toast.makeText(getActivity(), "User id not found", Toast.LENGTH_SHORT).show();
        }

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                final View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
                adb.setView(view1);
                final Dialog dialog;
                adb.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view1.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = DATE_FORMAT.format(date);
                        dateFrom.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                final View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
                adb.setView(view1);
                final Dialog dialog;
                adb.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view1.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = DATE_FORMAT.format(date);
                        dateTo.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        getFarmsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from_date = dateFrom.getText().toString().trim();
                String to_date = dateTo.getText().toString().trim();
                if (from_date == null || from_date.length() < 5) {

                    Toast.makeText(getActivity(), "Please choose from date.", Toast.LENGTH_SHORT).show();
                } else if (to_date == null || to_date.length() < 5) {

                    Toast.makeText(getActivity(), "Please choose to date", Toast.LENGTH_LONG).show();
                } else {

                    String jsonParameterString = createJsonParameterForFarmReport();
                    String createdString = AppManager.getInstance().removeSpaceForUrl(jsonParameterString);

                    Log.v("kdmlsmdls", createdString);

                    new getFarmReport(createdString).execute();
                }


            }
        });

        return view;
    }

    private void getAllSpinnerData(String userID) {

        Log.v("IDDDDDD", "" + userID);

        StringRequest stringCropRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/Farm/Filter/Data/" + userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley Crop Response : " + response);
                        try {
                            response = response.trim();
                         //   response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");

                            if (!response.equalsIgnoreCase("No Farms")) {

                                Log.v("sasas", response.toString() + "");
                                ArrayList<String> cropSpinnerArray = new ArrayList<>();
                                ArrayList<String> groupSpinnerArray = new ArrayList<>();
                                ArrayList<String> userSpinnerArray = new ArrayList<>();
                                ArrayList<String> stateSpinnerArray = new ArrayList<>();
                                JSONArray jsonArray = new JSONArray(response);

                                if (jsonArray.length() > 0) {
                                    cropArray.add(null);
                                    cropSpinnerArray.add("All");
                                    groupSpinnerArray.add("All");
                                    userSpinnerArray.add("All");
                                    stateSpinnerArray.add("All");

                                    JSONArray js = jsonArray.getJSONArray(0);
                                    for (int j = 0; j < js.length(); j++) {
                                        JSONObject jsonObject = js.getJSONObject(j);
                                        String id = jsonObject.isNull("Str1") ? "" : jsonObject.getString("Str1");
                                        String name = jsonObject.isNull("Str2") ? "" : jsonObject.getString("Str2");
                                        userArray.add(new UserBean(id, name));
                                        userSpinnerArray.add(name);
                                    }

                                    JSONArray js1 = jsonArray.getJSONArray(1);
                                    for (int j = 0; j < js1.length(); j++) {
                                        JSONObject jsonObject = js1.getJSONObject(j);
                                        String id = jsonObject.isNull("Str1") ? "" : jsonObject.getString("Str1");
                                        String name = jsonObject.isNull("Str2") ? "" : jsonObject.getString("Str2");
                                        stateArray.add(new StateBean(id, name));
                                        stateSpinnerArray.add(name);
                                    }

                                    JSONArray js2 = jsonArray.getJSONArray(2);
                                    for (int j = 0; j < js2.length(); j++) {
                                        JSONObject jsonObject = js2.getJSONObject(j);
                                        String id = jsonObject.isNull("Str1") ? "" : jsonObject.getString("Str1");
                                        String name = jsonObject.isNull("Str2") ? "" : jsonObject.getString("Str2");
                                        cropArray.add(new Crop(id, name));
                                        cropSpinnerArray.add(name);
                                    }

                                    JSONArray js3 = jsonArray.getJSONArray(3);
                                    for (int j = 0; j < js3.length(); j++) {
                                        JSONObject jsonObject = js3.getJSONObject(j);
                                        String id = jsonObject.isNull("Str1") ? "" : jsonObject.getString("Str1");
                                        String name = jsonObject.isNull("Str2") ? "" : jsonObject.getString("Str2");
                                        groupArray.add(new GroupBean(id, name));
                                        groupSpinnerArray.add(name);
                                    }


                                }

                                ArrayAdapter<String> groupSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, groupSpinnerArray);
//                            cropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                                farmerGroupSpinner.setAdapter(groupSpinnerAdapter);
                                farmerGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (i > 0) {
                                            GroupBean crop = groupArray.get(i - 1);
                                            group_id = crop.getGroupID();
                                        } else {
                                            group_id = "";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });


                                ArrayAdapter<String> cropSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cropSpinnerArray);
//                            cropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                                cropSpinner.setAdapter(cropSpinnerAdapter);
                                cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (i > 0) {
                                            Crop crop = cropArray.get(i - 1);
                                            crop_id = crop.getCropId();
                                        } else {
                                            crop_id = "";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                ArrayAdapter<String> userSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userSpinnerArray);
//                            cropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                                userSpinner.setAdapter(userSpinnerAdapter);
                                userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (i > 0) {
                                            UserBean crop = userArray.get(i - 1);
                                            user_id = crop.getUserID();
                                        } else {
                                            user_id = "";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });


                                ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, stateSpinnerArray);
//                            cropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                                stateSpinner.setAdapter(stateSpinnerAdapter);
                                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (i > 0) {
                                            StateBean crop = stateArray.get(i - 1);
                                            state_id = crop.getStateId();
                                        } else {
                                            state_id = "";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error : " + error);
            }
        });
        AppController.getInstance().addToRequestQueue(stringCropRequest);
    }


    private class getFarmReport extends AsyncTask<Void, Void, String> {

        String result = null;
        String createdString;

        public getFarmReport(String createdString) {
            this.createdString = createdString;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Processing . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            String sendPath = AppManager.getInstance().getFarmReport;


            String jsonParameterString = createJsonParameterForFarmReport();
            createdString = AppManager.getInstance().removeSpaceForUrl(jsonParameterString);

            Log.v("vishal", "--" + createdString);
            Log.v("tripathi", "---" + jsonParameterString);

            response = AppManager.getInstance().httpRequestPutMethod(sendPath, createdString);


            System.out.println("Save Response :---" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();
            progressDialog.dismiss();
            try {
                response = response.trim();
               // response = response.substring(1, response.length() - 1);
                response = response.replace("\\", "");
                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.replace("\"[", "[");
                response = response.replace("]\"", "]");
                System.out.println("Farm report Response : " + response);

                JSONArray locationArray = new JSONArray(response);
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                for (int i = 0; i < locationArray.length(); i++) {
                    JSONObject locationObject = locationArray.getJSONObject(i);
                    OptimalMandiData data = new OptimalMandiData();
                    data.setPrice(locationObject.getString("Price"));
                    data.setLocation(locationObject.getString("Location"));
                    data.setLatitude(locationObject.getDouble("Latitude"));
                    data.setLongitude(locationObject.getDouble("Longitude"));
                    data.setDistance(locationObject.getDouble("Distance"));
                    data.setUnit(locationObject.getString("Unit"));
                    data.setOptimal(locationObject.getString("Optimal"));
                    data.setDate(locationObject.getString("Date"));

                    if (mMap != null) {
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.home);

                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(data.getLatitude(), data.getLongitude()))
                                .title("Location : " + data.getLocation())
                                .snippet("Distance : " + data.getDistance())
                                .icon(icon);
                        Marker mMarker = mMap.addMarker(markerOptions);
                        if (mMarker != null) {
                            bc.include(mMarker.getPosition());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(), data.getLongitude()), 5));
                            System.out.println("Marker has been added with " + data.getLatitude() + " , " + data.getLongitude() + " Location : " + data.getLocation());
                        }
                        data.setMarker(mMarker);
                    }
                    mandiArray.add(data);
                }
                if (mandiArray.size() > 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
                    //  initializeTable(mandiArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Response Formatting Error", Toast.LENGTH_LONG).show();
            }


        }
    }


    private String createJsonParameterForFarmReport() {
        String parameterString = "";


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FGroups", "");
            jsonObject.put("Users", "100465");
            jsonObject.put("Crops", "0");
            jsonObject.put("States", "0");
            jsonObject.put("dtRegFrom", "");
            jsonObject.put("dtRegTo", "");
            jsonObject.put("Plots", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        parameterString = jsonObject.toString();

        return parameterString;
    }


    /*****
     * Sets up the map if it is possible to do so
     *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mandiMap)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();
                }
            });
            // Check if we were successful in obtaining the map.
          //  if (mMap != null)

        }
    }

    private void setUpMap() {
        // For showing a move to my loction button
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 12.0f));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mandiMap)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap  = googleMap;
                    setUpMap();
                }
            }); // getMap is deprecated
            // Check if we were successful in obtaining the map.
          //  if (mMap != null)

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class OptimalMandiData {

        String location;
        double latitude;
        double longitude;
        double distance;
        Marker marker;
        String price;
        String unit;
        String optimal;
        String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOptimal() {
            return optimal;
        }

        public void setOptimal(String optimal) {
            this.optimal = optimal;
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

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
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

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }

    private class Crop {
        private String cropId;
        private String cropName;

        public Crop(String cropId, String cropName) {
            this.cropId = cropId;
            this.cropName = cropName;
        }

        public String getCropId() {
            return cropId;
        }

        public void setCropId(String cropId) {
            this.cropId = cropId;
        }

        public String getCropName() {
            return cropName;
        }

        public void setCropName(String cropName) {
            this.cropName = cropName;
        }
    }

    private class GroupBean {
        private String groupID;
        private String groupName;

        public GroupBean(String cropId, String cropName) {
            this.groupID = cropId;
            this.groupName = cropName;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }
    }

    private class StateBean {
        private String stateId;
        private String stateName;

        public StateBean(String id, String name) {
            this.stateId = id;
            this.stateName = name;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }
    }

    private class UserBean {
        private String userID;
        private String userName;

        public UserBean(String id, String name) {
            this.userID = id;
            this.userName = name;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }


}