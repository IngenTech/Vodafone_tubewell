package com.wrms.vodafone.mapfragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.wrms.vodafone.adapter.NDVIAdapter;
import com.wrms.vodafone.adapter.WMAdapter;
import com.wrms.vodafone.bean.NDBI_Bean;
import com.wrms.vodafone.bean.WaterBean;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Admin on 09-10-2017.
 */
public class WaterManagemnetFragment extends Fragment {

    String nextResponse = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocateYoutFarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaterManagemnetFragment newInstance() {
        WaterManagemnetFragment fragment = new WaterManagemnetFragment();

        return fragment;
    }

    public WaterManagemnetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

  /*  Spinner districtSpinner,villageSpinner;
    Button submitBtn;
*/


    RecyclerView recyclerView;
    Button nextBtn;

    Spinner cropSpinner,soilSpinner;
    private String cityArr[];
    String cropId = null;
    String cropName = null;
    String soilId = null;
    String soilName = null;

    Button searchButton;

    String lat,lon,lat1,lon1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.water_management, container, false);

        cropSpinner = (Spinner)view.findViewById(R.id.water_crops_spinner);
        soilSpinner = (Spinner)view.findViewById(R.id.water_soil_spinner);

        String role = AppConstant.role;
        Log.v("roleeeeeelllll",role+"");

        if (role.equalsIgnoreCase("Admin")){
            setHasOptionsMenu(true);
        }else {

        }

        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
        lat1 =  prefs.getString("lat","25");
        lon1 = prefs.getString("lon","77");

        if (lat1==null || lon1==null){
            lat = "25";
            lon = "77";
        }else {
            lat  = lat1.substring(0, 2);
            lon  = lon1.substring(0, 2);
        }

        searchButton = (Button)view.findViewById(R.id.water_submit);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soilName==null){
                    Toast.makeText(getActivity(),"Please select soil.",Toast.LENGTH_SHORT).show();
                }else if (cropName==null){
                    Toast.makeText(getActivity(),"Please select crop.",Toast.LENGTH_SHORT).show();
                }else {
                    messageData();

                }
            }
        });


        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Water Management");
        farmInfo.setTextColor(Color.WHITE);

     /*   districtSpinner = (Spinner)view.findViewById(R.id.dashboard_district);
        villageSpinner = (Spinner)view.findViewById(R.id.dashboard_village);
        submitBtn = (Button)view.findViewById(R.id.dashboard_submit);
*/



        nextBtn = (Button)view.findViewById(R.id.next_btn);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new WaterManagementTech();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack("moi").commit();
            }
        });

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_water);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        loadCropList("2");

        final ArrayList<String> districtList = new ArrayList<>();
        final ArrayList<String> districtID = new ArrayList<>();


        districtList.add("-Select-");
        districtList.add("Clayey");
        districtList.add("Sandy");
        districtList.add("Silty");




        districtID.add("0");
        districtID.add("Clayey");
        districtID.add("Sandy");
        districtID.add("Silty");

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, districtList); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soilSpinner.setAdapter(varietyArrayAdapter);
        soilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    soilId  = districtList.get(position);
                    soilName = districtList.get(position);
                }else {

                    soilId  = null;
                    soilName =null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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


            return true;
        }else if (id == R.id.action_error){
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

                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(getActivity(), "No email application is available to share error log file", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getActivity(), "GGRC ErrorLog file does not exist ", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }


    public void loadCropList(String ID) {
        final ProgressDialog dialoug = ProgressDialog.show(getActivity(), "",
                "Fetching Villages. Please wait...", true);
        Log.v("knsknklanl","http://wwf.myfarminfo.com/yfirest.svc/WatReq/Crops/"+ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://wwf.myfarminfo.com/yfirest.svc/WatReq/Crops/"+ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley water Response : " + response);

                        response = response.trim();
                  //      response = response.substring(1, response.length() - 1);
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
                        cityArr = new String[cityList.size()];
                        cityArr[0] = "select crop";
                        for (int i = 1; i < cityList.size(); i++) {
                            cityArr[i] = cityList.get(i).getVilageName();
                        }

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityArr);
                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        cropSpinner.setAdapter(eventTypeAdapter);

                        final DataBean finalBean = bean;
                        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position>0) {
                                    cropId = finalBean.getCityList().get(position).getVillageID();
                                    cropName = finalBean.getCityList().get(position).getVilageName();
                                }else {
                                    cropId = null;
                                    cropName = null;
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


    @Override
    public void onResume() {
        super.onResume();
        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Water Management");
        farmInfo.setTextColor(Color.WHITE);

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
                    typeBean.setVilageName(jsonArray.getJSONObject(i).getString("Crop"));
                    typeBean.setVillageID(jsonArray.getJSONObject(i).getString("CropID"));
                    eventTypeList.add(typeBean);

                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            dataBean.setCityList(eventTypeList);



        }

        return dataBean;
    }


    ProgressDialog dialoug1;

    public void messageData() {
        dialoug1 = ProgressDialog.show(getActivity(), "",
                "Fetching Data Please wait...", true);

       // Log.v("kkkkk","http://wwf.myfarminfo.com/yfirest.svc/Clients/GGRC/Data/"+villageID+"/"+"Village"+"/"+lat+"/"+lon+"/"+villageName);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/WatReq/Data/"+lat+"/"+lon+"/"+cropId+"/"+soilName+"/2/0/India",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug1.cancel();
                        // Display the first 500 characters of the response string.


                        response = response.trim();
                       // response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");

                        System.out.println(" Response : " + response);

                        if (response!=null){

                            nextResponse = response;
                            nextBtn.setVisibility(View.VISIBLE);

                            try{

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("DT");

                                ArrayList<WaterBean> msgList = new ArrayList<WaterBean>();


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    WaterBean bean = new WaterBean();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    bean.setSNo(jsonObject1.getString("SNo"));
                                    bean.setStage(jsonObject1.getString("Stage"));
                                    bean.setStage_Requirement(jsonObject1.getString("Stage Water Requirement"));
                                    bean.setExp_rain(jsonObject1.getString("Expected Rain"));
                                    bean.setWater_needed(jsonObject1.getString("Water Needed"));

                                    msgList.add(bean);
                                }

                                if (msgList.size()>0){

                                    //     ndviTxt.setVisibility(View.VISIBLE);

                                    WMAdapter adapter = new WMAdapter(getActivity(),msgList);
                                    recyclerView.setAdapter(adapter);
                                }else {
                                    //ndviTxt.setVisibility(View.GONE);

                                }

                            }catch (JSONException e){
                                e.printStackTrace();
                            }


                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialoug1.cancel();
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

    private void noInternetMethod() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Internet Error").
                setMessage("Do You want to Refresh?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        messageData();
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
}