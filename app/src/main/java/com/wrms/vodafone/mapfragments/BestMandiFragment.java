package com.wrms.vodafone.mapfragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.TableLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.MandiPriceAdapter;
import com.wrms.vodafone.adapter.MultiselectionAdapter;
import com.wrms.vodafone.bean.CropBean;
import com.wrms.vodafone.bean.FarmerBean;
import com.wrms.vodafone.bean.MandiPriceBean;
import com.wrms.vodafone.bean.MultiBean;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 28-07-2017.
 */
public class BestMandiFragment extends Fragment {
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
    public static BestMandiFragment newInstance(String param1, String param2) {
        BestMandiFragment fragment = new BestMandiFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, param1);
        args.putString(LONGITUDE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BestMandiFragment() {
        // Required empty public constructor
    }

    private static View view;
    private GoogleMap mMap;
    double latitude, longitude;
    ArrayList<BestMandiData> mandiArray = new ArrayList<BestMandiData>();
    ArrayList<Crop> cropArray = new ArrayList<>();
    ArrayList<String> varietyArray = new ArrayList<>();
    Spinner crop_Spinner;
    Spinner varietySpinner;
    TableLayout table;

    RelativeLayout headingTxt;
    CardView hideSection;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView listView;

    Spinner villageSpinner, districtSpinner, farmerSpinner;
    EditText cropSpinner;
    private String cityArr[];
    String villageID = null;
    String vill_id = null;
    String lat = null;
    String lon = null;
    String villageName = null;

    ArrayList<MultiBean> multiArray;
    ArrayList<String> idSelectedList;
    MultiselectionAdapter multiAdapter;
    ListView listView1;
    String result;
    ArrayList<FarmerBean> farmerList;
    String farmerID = null;
    String farmerName = null;
    String farmer_id = null;
    String lat_farmer = null;
    String lon_farmer = null;

    ArrayList<CropBean> cropList;

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
        view = (LinearLayout) inflater.inflate(R.layout.fragment_optimal_mandi, container, false);
        String role = AppConstant.role;
        Log.v("roleeeeeelllll", role + "");

        if (role.equalsIgnoreCase("Admin")) {
            setHasOptionsMenu(true);
        } else {

        }
        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Best Mandi");
        farmInfo.setTextColor(Color.WHITE);

        setUpMapIfNeeded(); // For setting up the MapFragment

        crop_Spinner = (Spinner) view.findViewById(R.id.cropSpinner);
        varietySpinner = (Spinner) view.findViewById(R.id.varietySpinner);
        table = (TableLayout) view.findViewById(R.id.mandiDetailTable);

        hideSection = (CardView) view.findViewById(R.id.hide);
        headingTxt = (RelativeLayout) view.findViewById(R.id.ttttt);
        listView = (RecyclerView) view.findViewById(R.id.mandi_price_listview);
        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(mLayoutManager);

        Log.v("kdjasj", "kijsika");

        getCropList(String.valueOf(latitude), String.valueOf(longitude));

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.navigation_drawer, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            villageMethod();
            return true;
        } else if (id == R.id.action_error) {
            File logFile = new File(Environment.getExternalStorageDirectory(), "jalna.txt");
            if (logFile.exists()) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"vishal.tripathi@iembsys.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logFile));
                // the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "jalna Error log");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "jalna app");

                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(getActivity(), "No email application is available to share error log file", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getActivity(), "jalna ErrorLog file does not exist ", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void villageMethod() {

        final Dialog dialog = new Dialog(getActivity());

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
                    Toast.makeText(getActivity(), "Please select village", Toast.LENGTH_SHORT).show();
                }
            }
        });

        farmerSpinner = (Spinner) dialog.findViewById(R.id.popup_farmer);
        Button okBTN = (Button) dialog.findViewById(R.id.popup_submit);
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (villageID != null) {
                    dialog.dismiss();


                    SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);


                    // latitude longitude selected from dashboard Village

                    String l1 = prefs.getString("lat", null);
                    String l2 = prefs.getString("lon", null);

                    Fragment fragment = OptimalMandiFragment.newInstance(l1, l2);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

                } else {
                    Toast.makeText(getActivity(), "please select village.", Toast.LENGTH_SHORT).show();
                }

            }
        });

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

    public void loadVillagesData(String ID) {
        final ProgressDialog dialoug = ProgressDialog.show(getActivity(), "",
                "Fetching Villages. Please wait...", true);
        Log.v("knsknklanl", "http://myfarminfo.com/yfirest.svc/Clients/GGRC/Villages/" + ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley village Response : " + response);

                        response = response.trim();
                       // response = response.substring(1, response.length() - 1);
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

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityArr);
                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        villageSpinner.setAdapter(eventTypeAdapter);

                        final DataBean finalBean = bean;
                        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                villageID = finalBean.getCityList().get(position).getVillageID();
                                villageName = finalBean.getCityList().get(position).getVilageName();

                                Log.v("ksjkls", villageID);
                                SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

                                lat = prefs.getString("lat", null);
                                lon = prefs.getString("lon", null);
                                vill_id = villageID;

                                vill_id = villageID;
                                SharedPreferences.Editor editor  = prefs.edit();
                                editor.putString("villageId",villageID);
                                editor.putString("villageName",villageName);
                                editor.apply();

                               /* if (villageID != null) {

                                    String[] parts = villageID.split("-");
                                    vill_id = parts[0];
                                    lat = parts[1];
                                    lon = parts[2];

                                    SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
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


    @Override
    public void onResume() {
        super.onResume();
        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Best Mandi");
        farmInfo.setTextColor(Color.WHITE);
    }

    private void getCropList(final String latitude, final String longitude) {

        double R1 = 6371; // earth radius in km
        double radius = 100; // km
        double longTO = Double.parseDouble(longitude) - Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(Double.parseDouble(latitude))));
        double longFrom = Double.parseDouble(longitude) + Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(Double.parseDouble(latitude))));
        final double latFrom = Double.parseDouble(latitude) + Math.toDegrees(radius / R1);
        double latTO = Double.parseDouble(latitude) - Math.toDegrees(radius / R1);

        String str =
                String.valueOf(latitude) + File.separator +
                        String.valueOf(longitude) + File.separator +
                        String.valueOf(latFrom) + File.separator +
                        String.valueOf(latTO) + File.separator +
                        String.valueOf(longTO) + File.separator +
                        String.valueOf(longFrom) + File.separator ;
        String abc = AppManager.getInstance().removeSpaceForUrl(str);
        Log.v("alslak", "http://wwf.myfarminfo.com/yfirest.svc/Mandi/Crops/All/" + abc);

        StringRequest stringCropRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/Mandi/Crops/" + latitude + "/" + longitude,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Crop Response : " + response);

                        try {
                            response = response.trim();
                       //     response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");

                            Log.v("sahahskj", response + "");

                            ArrayList<String> cropSpinnerArray = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            JSONArray jsonInnerArray = jsonArray.getJSONArray(0);
                            if (jsonInnerArray.length() > 0) {
                                cropArray.add(null);
                                cropSpinnerArray.add("Select Crop");
                                for (int i = 0; i < jsonInnerArray.length(); i++) {
                                    JSONObject jsonObject = jsonInnerArray.getJSONObject(i);
                                    String id = jsonObject.isNull("CropID") ? "" : jsonObject.getString("CropID");
                                    String name = jsonObject.isNull("Commodity") ? "" : jsonObject.getString("Commodity");
                                    cropArray.add(new Crop(id, name));
                                    cropSpinnerArray.add(name);
                                }
                            }
                            ArrayAdapter<String> cropSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cropSpinnerArray);
//                            cropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            crop_Spinner.setAdapter(cropSpinnerAdapter);
                            crop_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (i > 0) {
                                        Crop crop = cropArray.get(i);
                                        getVarietyList(latitude, longitude, crop.getCropId());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });


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

    private void getVarietyList(final String latitude, final String longitude, final String cropId) {
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com//yfirest.svc/Mandi/CropVariety/" + latitude + "/" + longitude + "/" + cropId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String varietyResponse) {
                        try {
                            System.out.println("Variety Respose : " + varietyResponse);
                            varietyResponse = varietyResponse.trim();
                            varietyResponse = varietyResponse.substring(1, varietyResponse.length() - 1);
                            varietyResponse = varietyResponse.replace("\\", "");
                            varietyArray = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(varietyResponse);
                            if (jsonArray.length() > 0) {
                                varietyArray.add("Select Variety");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.isNull("Variety") ? "" : jsonObject.getString("Variety");
                                    varietyArray.add(name);
                                }
                            }
                            ArrayAdapter<String> varietySpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, varietyArray);
                            varietySpinner.setAdapter(varietySpinnerAdapter);
                            varietySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (i > 0) {
                                        String variety = varietyArray.get(i);
                                        getMandiDetail(cropId, variety, latitude, longitude);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }
    private void getMandiDetail(String cropId, String variety, final String latitude, final String longitude) {
//        /YFIRest.svc/Mandi/OptimalMandi/46/Other/25/77

        double R1 = 6371; // earth radius in km
        double radius = 100; // km
        double longTO = Double.parseDouble(longitude) - Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(Double.parseDouble(latitude))));
        double longFrom = Double.parseDouble(longitude) + Math.toDegrees(radius / R1 / Math.cos(Math.toRadians(Double.parseDouble(latitude))));
        final double latFrom = Double.parseDouble(latitude) + Math.toDegrees(radius / R1);
        double latTO = Double.parseDouble(latitude) - Math.toDegrees(radius / R1);

        String str = cropId + File.separator +
                variety + File.separator +
                String.valueOf(latitude) + File.separator +
                String.valueOf(longitude) + File.separator +
                String.valueOf(latFrom) + File.separator +
                String.valueOf(latTO) + File.separator +
                String.valueOf(longTO) + File.separator +
                String.valueOf(longFrom) + File.separator +
                "100000" + File.separator +
                String.valueOf(latitude) + File.separator +
                String.valueOf(longitude);
        String abc = AppManager.getInstance().removeSpaceForUrl(str);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/YFIRest.svc/Mandi/BestMandi/" + abc,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (mMap != null) {
                            mMap.clear();
                            setUpMap();
                        }
                        System.out.println("Volley Response : " + response);
                        try {
                            mandiArray = new ArrayList<BestMandiData>();
                            response = response.trim();
                        //    response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");
                            JSONObject jb = new JSONObject(response);
                            JSONArray locationArray = jb.getJSONArray("dataTable");
                            LatLngBounds.Builder bc = new LatLngBounds.Builder();
                            for (int i = 0; i < locationArray.length(); i++) {
                                JSONObject locationObject = locationArray.getJSONObject(i);
                                BestMandiData data = new BestMandiData();
                                data.setPrice(locationObject.getString("AvgPrice"));
                                data.setLocation(locationObject.getString("Location"));
                                data.setLatitude(locationObject.getDouble("Latitude"));
                                data.setLongitude(locationObject.getDouble("Longitude"));
                                data.setDistance(locationObject.getDouble("Distance"));
                                data.setUnit(locationObject.getString("Rank"));
                               /* data.setOptimal(locationObject.getString("Optimal"));
                                data.setDate(locationObject.getString("Date"));*/

                                if (mMap != null) {
                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.home);

                                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(data.getLatitude(), data.getLongitude()))
                                            .title("Location : " + data.getLocation())
                                            .snippet("Distance : " + data.getDistance())
                                            .icon(icon);
                                    Marker mMarker = mMap.addMarker(markerOptions);
                                    if (mMarker != null) {
                                        bc.include(mMarker.getPosition());

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 8.0f));
                                        System.out.println("Marker has been added with " + data.getLatitude() + " , " + data.getLongitude() + " Location : " + data.getLocation());
                                    }
                                    data.setMarker(mMarker);
                                }
                                mandiArray.add(data);
                            }
                            if (mandiArray.size() > 0) {
                                //    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
                                initializeTable(mandiArray);

                            } else {
                                if (mMap != null) {
                                    mMap.clear();
                                    setUpMap();
                                    headingTxt.setVisibility(View.GONE);
                                    hideSection.setVisibility(View.GONE);
                                }
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

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void initializeTable(ArrayList<BestMandiData> data) {

        if (data.size() > 0) {

            hideSection.setVisibility(View.VISIBLE);
            headingTxt.setVisibility(View.VISIBLE);


            ArrayList<MandiPriceBean> list = new ArrayList<MandiPriceBean>();

            for (int i = 0; i < data.size(); i++) {
                MandiPriceBean bean = new MandiPriceBean();
                bean.setCommodity(data.get(i).getLocation());
                bean.setPrice(data.get(i).getPrice());
                bean.setVariety(String.valueOf(data.get(i).getDistance()));
                bean.setDate(data.get(i).getDate());
                list.add(bean);

            }

            MandiPriceAdapter adapter = new MandiPriceAdapter(getActivity(), list);
            listView.setAdapter(adapter);
        } else {
            headingTxt.setVisibility(View.GONE);
            hideSection.setVisibility(View.GONE);
        }

    }

    /*****
     * Sets up the map if it is possible to do so
     *****/
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mandiMap)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    setUpMap();
                }
            });
            // Check if we were successful in obtaining the map.


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
        drawCircle(new LatLng(latitude, longitude), 100000);

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 7.0f));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void drawCircle(LatLng center, int radius) {
        // Clear the map to remove the previous circle
        mMap.clear();
        // Generate the points
        List<LatLng> points = new ArrayList<LatLng>();
        int totalPonts = 30; // number of corners of the pseudo-circle
        for (int i = 0; i < totalPonts; i++) {
            points.add(getPoint(center, radius, i * 2 * Math.PI / totalPonts));
        }


        mMap.addPolygon(new PolygonOptions().addAll(points).strokeWidth(4).strokeColor(Color.RED));
        // Create and return the polygon

    }

    private LatLng getPoint(LatLng center, int radius, double angle) {
        int EARTH_RADIUS = 6371000;
        // Get the coordinates of a circle point at the given angle
        double east = radius * Math.cos(angle);
        double north = radius * Math.sin(angle);

        double cLat = center.latitude;
        double cLng = center.longitude;
        double latRadius = EARTH_RADIUS * Math.cos(cLat / 180 * Math.PI);

        double newLat = cLat + (north / EARTH_RADIUS / Math.PI * 180);
        double newLng = cLng + (east / latRadius / Math.PI * 180);

        return new LatLng(newLat, newLng);
    }


   /* @Override
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
                }
            }); // getMap is deprecated
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }*/

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class BestMandiData {

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
        listView1 = (ListView) dialog.findViewById(R.id.list_addgroup);
        multiAdapter = new MultiselectionAdapter(getActivity(), multiArray);
        listView1.setAdapter(multiAdapter);


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
                    //    response = response.substring(1, response.length() - 1);
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
                  //      response = response.substring(1, response.length() - 1);
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


}