package com.wrms.vodafone.mapfragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Admin on 20-09-2017.
 */
public class VillageMapFagment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<String> idSelectedList;

    private OnFragmentInteractionListener mListener;

    String result;


    public VillageMapFagment() {
        // Required empty public constructor
    }

    private static View view;
    private GoogleMap mMap;

    private String cityArr[];
    ArrayList<LatLng> points = null;

    Double lat1 = 0.0, lon1 = 0.0;
    LinearLayout hideShowLay;

    Spinner districtSpinner, villageSpinner, farmerSpinner;
    Button submitBtn;

    EditText cropSpinner;

    private String villArr[];
    String villageID = null;
    String vill_id = null;
    String lat = null;
    String lon = null;
    String villageName = null;

    Button nextBtn;

    String cropID = null;
    String cropName = null;

    String farmerID = null;
    String farmerName = null;
    String farmer_id = null;
    String lat_farmer = null;
    String lon_farmer = null;
    String nextResponse = null;

    ArrayList<CropBean> cropList;

    ArrayList<MultiBean> multiArray;
    ArrayList<FarmerBean> farmerList;

    MultiselectionAdapter multiAdapter;
    ListView listView;

    public static VillageMapFagment newInstance() {
        VillageMapFagment fragment = new VillageMapFagment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.village_map_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        hideShowLay = (LinearLayout) view.findViewById(R.id.aaaa);

        multiArray = new ArrayList<MultiBean>();

        points = new ArrayList<LatLng>();

        districtSpinner = (Spinner) view.findViewById(R.id.dashboard_district);
        villageSpinner = (Spinner) view.findViewById(R.id.dashboard_village);

        cropSpinner = (EditText) view.findViewById(R.id.village_crop);

        cropSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiArray.size() > 0) {

                    selectCropPopup();
                } else {
                    Toast.makeText(getActivity(), "Please select village", Toast.LENGTH_SHORT).show();
                }
            }
        });

        farmerSpinner = (Spinner) view.findViewById(R.id.dashboard_farmer);

        submitBtn = (Button) view.findViewById(R.id.dashboard_submit);
        nextBtn = (Button) view.findViewById(R.id.next_btn);
        nextBtn.setVisibility(View.GONE);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new DashboardFragment(nextResponse);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack("das").commit();
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (farmer_id == null) {
                    Toast.makeText(getActivity(), "please select Farmer", Toast.LENGTH_SHORT).show();
                } else {
                    messageData();
                }
            }
        });
        setUpMapIfNeeded();

        if (mMap != null) {
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.6807, 75.9928), 10.0f));

        }


        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

        String lat1 = prefs.getString("lat", null);
        String lon1 = prefs.getString("lon", null);
        String villageId1 = prefs.getString("villageId", null);
        String villageName1 = prefs.getString("villageName", null);
        String farmID = prefs.getString("farmerID", null);
        String role1 = prefs.getString(AppConstant.PREFRENCE_KEY_ROLE, null);


        Log.v("llllll", lat1 + "----" + lon1);
        if (role1 != null && role1.equalsIgnoreCase("client")) {

            hideShowLay.setVisibility(View.GONE);

            farmerID = farmID;
            lat = lat1;
            lon = lon1;

            lat_farmer = lat;
            lon_farmer = lon;

            if (farmerID != null) {
                messageData();
            } else {
                if (mMap != null) {
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.6807, 75.9928), 10.0f));

                }

            }

        } else {
            hideShowLay.setVisibility(View.VISIBLE);

            lat = lat1;
            lon = lon1;

            farmerID = farmID;

            lat_farmer = lat;
            lon_farmer = lon;


            if (farmerID != null) {

                messageData();
            } else {
                if (mMap != null) {
                    mMap.clear();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.6807, 75.9928), 10.0f));

                }
            }

        }


        ArrayList<String> districtList = new ArrayList<>();
        final ArrayList<String> districtID = new ArrayList<>();


        districtList.add("-Select-");
        districtList.add("Jalna");


        districtID.add("0");
        districtID.add("16032");


        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, districtList); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(varietyArrayAdapter);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    //    districtSpinner.setSelection(position);

                    farmer_id = null;
                    lat_farmer = null;
                    lon_farmer = null;

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

        return view;
    }

    private void noInternetMethod() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("No Internet").
                setMessage("No internet connections please retry.").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

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

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.villMap)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

            }
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
        if (points.size()>4) {
            drawCircle();
        }


    }
    public void drawCircle() {
        // Clear the map to remove the previous circle
        mMap.clear();
        // Generate the points
        mMap.addPolygon(new PolygonOptions().addAll(points).strokeWidth(4).strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
        // Create and return the polygon

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mMap != null) {
            setUpMap();
        }

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.villMap)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap  = googleMap;
                }
            }); // getMap is deprecated
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }



    ProgressDialog dialoug1;

    public void messageData() {
        dialoug1 = ProgressDialog.show(getActivity(), "",
                "Fetching Data Please wait...", true);

        Log.v("dkkdkd","http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Data/"+farmerID+"/"+"Farm"+"/"+lat_farmer+"/"+lon_farmer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Data/"+farmerID+"/"+"Farm"+"/"+lat_farmer+"/"+lon_farmer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        dialoug1.dismiss();
                        System.out.println(" Response : " + response);
                        try {

                            nextBtn.setVisibility(View.VISIBLE);


                            response = response.trim();
                         //   response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");

                            nextResponse = response;
                            Log.v("response",response+"");



                            JSONObject jb = new JSONObject(response);
                            Log.v("response",jb.toString()+"");
                            JSONArray locationArray = jb.getJSONArray("DT7");
                            points = new ArrayList<LatLng>();
                            for (int i = 0; i < locationArray.length(); i++) {
                                JSONObject locationObject = locationArray.getJSONObject(i);
                                String point  = locationObject.getString("Contour");
                                List<String> l_List = Arrays.asList(point.split(","));


                                lat1 = Double.valueOf(l_List.get(0));
                                lon1 = Double.valueOf(l_List.get(l_List.size()-1));
                                points.add(new LatLng(lat1, lon1));

                                for (int j = 1;j<l_List.size()-1;j++){
                                    String currentString = l_List.get(j);
                                    if (currentString!=null) {
                                        String[] separated = currentString.split("-");
                                        String la = separated[0];
                                        String lo = separated[1];

                                        points.add(new LatLng(Double.valueOf(lo), Double.valueOf(la)));
                                    }
                                }
                            }

                            if (lat1!=null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat1, lon1), 16.0f));
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                Log.v("latlon1",lat+"---"+lon);

                            }else {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lat), Double.valueOf(lon)), 13.0f));
                                Log.v("latlon2",lat1+"---"+lon1);
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            }
                            if (mMap != null) {
                                mMap.clear();
                                setUpMap();
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
                dialoug1.dismiss();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.6807, 75.9928), 10.0f));
                noInternetMethod();

            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Village Map");
        farmInfo.setTextColor(Color.WHITE);

    }



    ProgressDialog dialoug;

    public void loadVillagesData(String ID) {
        dialoug = ProgressDialog.show(getActivity(), "",
                "Fetching Villages. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
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


                        DataBean bean = new DataBean();
                        bean = getEventTypeList( response);
                        ArrayList<VillageBean> cityList = new ArrayList<VillageBean>();
                        cityList = bean.getCityList();
                        villArr = new String[cityList.size()];
                        for (int i = 0; i < cityList.size(); i++) {
                            villArr[i] = cityList.get(i).getVilageName();
                        }

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, villArr);
                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        villageSpinner.setAdapter(eventTypeAdapter);

                        final DataBean finalBean = bean;
                        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                villageID = finalBean.getCityList().get(position).getVillageID();
                                villageName = finalBean.getCityList().get(position).getVilageName();

                                farmer_id = null;
                                lat_farmer = null;
                                lon_farmer = null;

                                Log.v("ksjkls",villageID);

                                if (villageID!=null){

                                    SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

                                    lat = prefs.getString("lat", null);
                                    lon = prefs.getString("lon", null);
                                    vill_id = villageID;
                                    SharedPreferences.Editor editor  = prefs.edit();
                                    editor.putString("villageId",villageID);
                                    editor.putString("villageName",villageName);
                                    editor.apply();
                                }


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
                noInternetMethod();
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



    public void getCropList(String ID) {
        final ProgressDialog cropDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Crops. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Crops/"+ID,
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

                        cropList = new ArrayList<CropBean>();

                        multiArray = new ArrayList<MultiBean>();


                        try{

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                CropBean typeBean = new CropBean();

                                typeBean.setName(jsonArray.getJSONObject(i).getString("Name"));
                                typeBean.setId(jsonArray.getJSONObject(i).getString("ID"));

                                MultiBean bean = new MultiBean(typeBean.getName(),false,typeBean.getId());
                                multiArray.add(bean);
                                cropList.add(typeBean);

                            }

                        }catch (JSONException e){
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


    public void selectCropPopup() {

        idSelectedList = new ArrayList<String>();

        //final Dialog dialog = new Dialog(OtherUserProfile.this,android.R.style.Theme_Translucent_NoTitleBar);
        final Dialog dialog = new Dialog(getActivity());
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
        listView = (ListView) dialog.findViewById(R.id.list_addgroup);
        multiAdapter = new MultiselectionAdapter(getActivity(), multiArray);
        listView.setAdapter(multiAdapter);


        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showResult();
                dialog.cancel();


                if (result!=null) {
                    cropSpinner.setText(result);
                }
                if (idSelectedList.size()>0){
                    String ids="";
                    for (int i=0;i<idSelectedList.size()-1;i++){
                        ids = idSelectedList.get(i)+","+ids;
                    }
                    ids = ids+idSelectedList.get(idSelectedList.size()-1);
                    getFarmerList(ids);
                }

            }
        });

        dialog.show();
    }

    public void showResult() {

        String totalAmount = null;
        result = "";
        for (MultiBean p : multiAdapter.getBox()) {
            if (p.box) {
                result +=  p.crop_name+",";
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

        final ProgressDialog cropDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Farmers. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Farms/"+vill_id+"/"+crop_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cropDialog.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley State Response : " + response);

                        response = response.trim();
                   //     response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");

                        farmerList = new ArrayList<FarmerBean>();


                        try{

                            JSONObject jb = new JSONObject(response);
                            JSONArray jsonArray = jb.getJSONArray("DT");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                FarmerBean typeBean = new FarmerBean();
                                typeBean.setName(jsonArray.getJSONObject(i).getString("Name"));
                                typeBean.setId(jsonArray.getJSONObject(i).getString("ID"));
                                farmerList.add(typeBean);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                        String[] farmArr = new String[farmerList.size()];
                        for (int i = 0; i < farmerList.size(); i++) {
                            farmArr[i] = farmerList.get(i).getName();
                        }

                        ArrayAdapter<String> farmerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, farmArr);
                        farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        farmerSpinner.setAdapter(farmerAdapter);

                        farmerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                farmerID = farmerList.get(position).getId();
                                farmerName = farmerList.get(position).getName();

                                Log.v("farmerName",farmerName+"");

                                if (farmerID!=null){

                                    String[] parts = farmerID.split("-");
                                    farmer_id = parts[0];
                                    lat_farmer = parts[1];
                                    lon_farmer = parts[2];

                                    SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
                                    SharedPreferences.Editor editor  = prefs.edit();
                                    editor.putString("lat",lat_farmer);
                                    editor.putString("lon",lon_farmer);
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




}