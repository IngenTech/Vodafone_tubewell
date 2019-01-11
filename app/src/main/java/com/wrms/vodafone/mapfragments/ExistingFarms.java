package com.wrms.vodafone.mapfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.MultiselectionAdapter;
import com.wrms.vodafone.adapter.NDVIAdapter;
import com.wrms.vodafone.bean.CropBean;
import com.wrms.vodafone.bean.FarmerBean;
import com.wrms.vodafone.bean.MultiBean;
import com.wrms.vodafone.bean.NDBI_Bean;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.AllFarmDetail;
import com.wrms.vodafone.entities.CropQueryData;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.SignInData;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.home.NavigationDrawerActivity;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 06-10-2017.
 */
public class ExistingFarms extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CALLING_ACTIVITY = "callingActivity";
    private static final String FARM_NAME = "FarmName";
    private static final String ALL_POINTS = "AllLatLngPount";
    private static final String AREA = "area";

    // TODO: Rename and change types of parameters
    private int callingActivity;
    private String selectedFarmName;
    private String area;
    String data = null;

    String nextResponse = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocateYoutFarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExistingFarms newInstance() {
        ExistingFarms fragment = new ExistingFarms();

        return fragment;
    }

    public ExistingFarms() {
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

    String cropID = null;
    String cropName  = null;


    TextView noData;

    private String cityArr[];
    String villageID = null;
    String vill_id = null;
    String lat = null;
    String lon = null;
    String villageName = null;

    Spinner farmerSpinner;
    EditText cropSpinner;
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




    Spinner chooseYourFarmSpiner,districtSpinner,villageSpinner;
    DBAdapter db;
    double latitude;
    double longitude;

    public static String syncFor = AppConstant.STATE_ID;
    public String syncMsg = "Syncronizing " + AppConstant.STATE_ID;
    int syncCount = 1;
    HashMap<String, String> hashMap;
    int callingMethod;
    String storeCurrentStateId = "noValue";

    TextView farmText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.existing_fragment, container, false);
        db = new DBAdapter(getActivity());
        db.open();

        setHasOptionsMenu(true);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Saved Farms");
        farmInfo.setTextColor(Color.WHITE);


        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

        lat = prefs.getString("lat", null);
        lon = prefs.getString("lon", null);


        chooseYourFarmSpiner = (Spinner)view.findViewById(R.id.chooseYourFarmSpiner);
        farmText = (TextView)view.findViewById(R.id.farmtext);

        getAllFarmName(); //This will get all farm name from the database and set the farm list in spinnerView
        this.chooseYourFarmSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!chooseYourFarmSpiner.getSelectedItem().toString().equals("Select")) {
                    db.open();
                    Cursor c = db.getStateFromSelectedFarm(chooseYourFarmSpiner.getSelectedItem().toString());
                    if (c.moveToFirst()) {
                        do {
                            AppConstant.stateID = c.getString(c.getColumnIndex(DBAdapter.STATE_ID));
                            String contour = c.getString(c.getColumnIndex(DBAdapter.CONTOUR));
                            getAtLeastOneLatLngPoint(contour);
                        }
                        while (c.moveToNext());
                    }
                    db.close();
                    callingMethod = AppConstant.selectForm;
                    if (storeCurrentStateId.contains(AppConstant.stateID)) {
                        openActivity();
                    } else {
                        syncCount = 2;
                        syncFor = AppConstant.CROP_INITIAL;
                        syncMsg = "Syncronizing " + AppConstant.CROP_INITIAL;
                        new appDataSetting().execute();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        return view;
    }
    public void getAllFarmName() {


        System.out.println("getAllFarmCalled");
        ArrayList<String> str = new ArrayList<String>();
        str.add("Select");

        db.open();
        Cursor c = db.getallFarmName(AppConstant.user_id);
        if (c.moveToFirst()) {
            do {
                str.add(c.getString(0).toString());
            } while (c.moveToNext());
        }
        db.close();
        ArrayAdapter<String> chooseYourFarm = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, str);
        chooseYourFarm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.chooseYourFarmSpiner.setAdapter(chooseYourFarm);

        if (str.size()>1){
            farmText.setText("Please Select any farm from above.");
        }else {
            farmText.setText("You don't have any created farm.");
        }

        if (c.getCount()<1){
            new getFarmDetailAsyncTask().execute();
        }
    }

    private class getFarmDetailAsyncTask extends AsyncTask<Void, Void, String> {
        SignInData data;
        String result = "";

        public getFarmDetailAsyncTask() {
            this.data = data;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Get Farm Detail Progress . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                                @Override
                                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                                    return false;
                                                }
                                            }
            );
        }

        @Override
        protected String doInBackground(Void... params) {
            String sendRequest = null;
            try {
                sendRequest = AppManager.getInstance().getFarmList + AppConstant.user_id;
                Log.d("get farm url", sendRequest);
                String response = AppManager.getInstance().httpRequestGetMethod(sendRequest);
                System.out.println("farm detial :" + response);
                return response;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null; //show network problm
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();
            try {

                if (response != null) {
                    if (response.contains("No Farms")) {
                        System.out.println("Farm not available");
                        Toast.makeText(getActivity(), "Farm not available", Toast.LENGTH_LONG).show();

                    } else {
                        AllFarmDetail addFarmDetail;

                        db.open();
                        //  db.deleteAllFarmDetailTable();
                        System.out.println("farm detail response " + response);

                        JSONArray jArray = new JSONArray(AppManager.getInstance().placeSpaceIntoString(response));
                        System.out.println("farm detail response " + jArray.length());
                        if (jArray.length() > 0) {
                            int deleteCount = db.db.delete(DBAdapter.DATABASE_TABLE_ALL_FARM_DETAIL, DBAdapter.SENDING_STATUS + " = '" + DBAdapter.SENT + "'", null);
                            int deleteCount1 = db.db.delete(DBAdapter.TABLE_QUERY_CROP, DBAdapter.SENDING_STATUS + " = '" + DBAdapter.SENT + "'", null);
                            System.out.println("deleteCount : " + deleteCount + " deleteCount1 : " + deleteCount1);
                        }
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject jsonObject = jArray.getJSONObject(i);
                            addFarmDetail = new AllFarmDetail(jsonObject);
                            addFarmDetail.setUserId(AppConstant.user_id);
                            String farmId = addFarmDetail.getFarmId();
                            String farmName = addFarmDetail.getFarmName();
                            String concern = addFarmDetail.getConcern();
                            Long l = db.insertAllFarmDetail(addFarmDetail, DBAdapter.SENT);
                            if (jsonObject.has("CropInfo")) {
                                JSONArray corpInfoArray = jsonObject.getJSONArray("CropInfo");

                                for (int j = 0; j < corpInfoArray.length(); j++) {
                                    JSONObject cropJsonObject = corpInfoArray.getJSONObject(j);
                                    CropQueryData data = new CropQueryData();
                                    data.setFarmId(farmId);
                                    data.setFarmName(farmName);
                                    data.setYourCencern(concern);
                                    data.setCropID(cropJsonObject.isNull("CropID") ? "" : cropJsonObject.getString("CropID"));
                                    data.setCrop(cropJsonObject.isNull("CropName") ? "" : cropJsonObject.getString("CropName"));
                                    String variety = cropJsonObject.isNull("Variety") ? "" : cropJsonObject.getString("Variety");
                                    data.setVariety(variety.replaceAll("%20", " "));
                                    data.setBasalDoseN(cropJsonObject.isNull("N") ? "0" : cropJsonObject.getString("N"));
                                    data.setBasalDoseP(cropJsonObject.isNull("P") ? "0" : cropJsonObject.getString("P"));
                                    data.setBasalDoseK(cropJsonObject.isNull("K") ? "0" : cropJsonObject.getString("K"));
                                    data.setSowPeriodForm(cropJsonObject.isNull("SowDate") ? "" : cropJsonObject.getString("SowDate"));
                                    data.setOtherNutrition(cropJsonObject.isNull("OtherNutrient") ? "" : cropJsonObject.getString("OtherNutrient"));
                                    data.setBesalDoseApply(cropJsonObject.isNull("BasalDoseApply") ? "" : cropJsonObject.getString("BasalDoseApply"));
                                    long inserted = data.insert(db, DBAdapter.SENT);
                                    System.out.println("database return value=" + l);
                                }
                            }

                        }
                        db.close();
                        getAllFarmName(); //This will get all farm deatail from the database and set the farm list in spinnerView
                    }
                } else {
                    Toast.makeText(getActivity(), "could not connect to server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                System.out.println("catch block Pls Try again");
            }

            progressDialog.dismiss();

        }
    }

    public void getAtLeastOneLatLngPoint(String contour) {

        String newContour = "";

        int pos = contour.indexOf("-");
        if (pos != -1) {
            newContour = contour.substring(0, pos);
        }
        int posOfComma = newContour.indexOf(",");
        AppConstant.latitude = newContour.substring(0, posOfComma);
        System.out.println("dgasdgsdfgsg" + AppConstant.latitude);
        String str = newContour.substring(newContour.indexOf(","));
        AppConstant.longitude = str.replace(",", "");
        System.out.println("dgasdgsdfgsg" + AppConstant.longitude);

    }

    class appDataSetting extends AsyncTask<Void, Void, String> {
        boolean firstRound = true;

        ProgressDialog dialoug;

        public appDataSetting() {
        }

        @Override
        protected void onPreExecute() {
            dialoug = ProgressDialog.show(getActivity(), "" + syncMsg, " Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = syncForApplicationDataSetting(syncFor);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                String msgDisplayInDailogBox = null;
                dialoug.dismiss();
                if (!result.contains("Success")) {
                    if (syncCount == 1) {

                        msgDisplayInDailogBox = "State id sync Not Succesfully!";

                    }
                    if (syncCount == 2) {

                        msgDisplayInDailogBox = "Crop initial sync Not Succesfully!";

                    }
                    if (syncCount == 3) {

                        msgDisplayInDailogBox = "Crop all initial sync Not Succesfully!";

                    }
                    syncMsg = "Syncronizing for " + syncFor;
                    alertDialogBox(msgDisplayInDailogBox, syncCount);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (storeCurrentStateId!=null && storeCurrentStateId.contains(AppConstant.stateID)) {

                openActivity();
            } else {

                if (syncCount < AppConstant.syncArray.length) {

                    syncFor = AppConstant.syncArray[syncCount];
                    System.out.println("value in syncFor " + syncFor);
                    syncMsg = "Syncronizing for " + syncFor;
                    syncCount++;
                    new appDataSetting().execute();

                } else {
                    openActivity();
                }

            }
        }
    }
    public String syncForApplicationDataSetting(String value) {
        String returnResult = null;
        System.out.println("value:" + value);
        if (value.contains(AppConstant.STATE_ID)) {
            returnResult = syncForStateId();
            return returnResult;

        }
        /*if (value.contains(AppConstant.CROP_INITIAL)) {
            db.open();
            long l = db.deleteAllCropVarietyTableRecord();
            db.close();
            System.out.print("value of long" + l);
            hashMap.clear();
            returnResult = syncForCropInitial();
            System.out.println("crop_initial_called");

        }
        if (value.contains(AppConstant.CROP_ALL_INITIAL)) {
            returnResult = syncForCropAllInitial();
            System.out.println("crop_all_initial called");

        }*/
        return returnResult;
    }

    public String syncForStateId() {
        String message = "NoValue";
        String sendRequest = null;
        sendRequest = AppManager.getInstance().getStateId + AppConstant.state + "/" + latitude + "/" + longitude;
        System.out.println("State Request URL : " + sendRequest);
        String response = AppManager.getInstance().httpRequestGetMethod(AppManager.getInstance().removeSpaceForUrl(sendRequest));
        System.out.println("state id is - --" + response);
        System.out.println("state id is - fcasdfasfasdfasf--");

        if (response == "") {
            return message;
        } else {
            AppConstant.stateID = response;
            message = "Success";
            return message;
        }
    }//+

    public void alertDialogBox(String msg, int syncCount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new appDataSetting().execute();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
    public void openActivity() {

        storeCurrentStateId = AppConstant.stateID;

        switch (callingMethod) {

            case AppConstant.selectForm: //if you select the farm

//                Intent i = new Intent(HomeActivity.this, FarmInformation.class);
                Intent i = new Intent(getActivity(), NavigationDrawerActivity.class);
                i.putExtra("calling-activity", AppConstant.HomeActivity);
                i.putExtra("FarmName", chooseYourFarmSpiner.getSelectedItem().toString());
                i.putExtra("hashMapValue", hashMap);
                startActivity(i);
                break;

        }

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

    public void villageMethod(){

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

        districtSpinner = (Spinner)dialog.findViewById(R.id.popup_district);
        villageSpinner = (Spinner)dialog.findViewById(R.id.popup_village);

        cropSpinner = (EditText) dialog.findViewById(R.id.popup_village_crop);

        cropSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiArray.size()>0){

                    selectCropPopup();
                }else {
                    Toast.makeText(getActivity(),"Please select village",Toast.LENGTH_SHORT).show();
                }
            }
        });

        farmerSpinner = (Spinner)dialog.findViewById(R.id.popup_farmer);
        Button okBTN = (Button)dialog.findViewById(R.id.popup_submit);
        okBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (farmer_id==null){
                    Toast.makeText(getActivity(),"please select Farmer",Toast.LENGTH_SHORT).show();
                }else {
                    Fragment fragment = new DashboardFragment(nextResponse);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
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
        Log.v("knsknklanl","http://myfarminfo.com/yfirest.svc/Clients/GGRC/Villages/"+ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley village Response : " + response);

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

                                Log.v("ksjkls",villageID);

                                SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

                                lat = prefs.getString("lat", null);
                                lon = prefs.getString("lon", null);
                                vill_id = villageID;

                                vill_id = villageID;

                                SharedPreferences.Editor editor  = prefs.edit();
                                editor.putString("villageId",villageID);
                                editor.putString("villageName",villageName);
                                editor.apply();
                               /* if (villageID!=null){

                                    String[] parts = villageID.split("-");
                                    vill_id = parts[0];
                                    lat = parts[1];
                                    lon = parts[2];

                                    SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
                                    SharedPreferences.Editor editor  = prefs.edit();
                                    editor.putString("lat",lat);
                                    editor.putString("lon",lon);
                                    editor.putString("villageId",villageID);
                                    editor.putString("villageName",villageName);
                                    editor.apply();

                                }
*/
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
                       // response = response.substring(1, response.length() - 1);
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
                       // response = response.substring(1, response.length() - 1);
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

                                    lat = lat_farmer;
                                    lon = lon_farmer;

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

    @Override
    public void onResume() {
        super.onResume();
        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Saved Farms");
        farmInfo.setTextColor(Color.WHITE);

    }




    ProgressDialog dialoug1;

    public void messageData() {
        dialoug1 = ProgressDialog.show(getActivity(), "",
                "Fetching Data Please wait...", true);

        Log.v("kkkkk","http://wwf.myfarminfo.com/yfirest.svc/Clients/GGRC/Data/"+villageID+"/"+"Village"+"/"+lat+"/"+lon+"/"+villageName);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://wwf.myfarminfo.com/yfirest.svc/Clients/GGRC/Data/"+villageID+"/"+"Village"+"/"+lat+"/"+lon+"/"+villageName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug1.cancel();
                        // Display the first 500 characters of the response string.


                        response = response.trim();
                    //    response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println(" Response : " + response);

                        if (response!=null){

                            nextResponse = response;

                            try{

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("DTChartDesc");

                                ArrayList<NDBI_Bean> msgList = new ArrayList<NDBI_Bean>();


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    NDBI_Bean bean = new NDBI_Bean();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    bean.setId(jsonObject1.getString("ID"));
                                    bean.setDate(jsonObject1.getString("Date"));
                                    bean.setVillageId(jsonObject1.getString("District_Id"));
                                    bean.setImage(jsonObject1.getString("Final_Img"));
                                    bean.setStartDate(jsonObject1.getString("Start_Date"));
                                    bean.setDistrictId(jsonObject1.getString("District_Id"));
                                    bean.setVillage_mean(jsonObject1.getString("Village_mean"));
                                    msgList.add(bean);
                                }

                                if (msgList.size()>0){

                                    //     ndviTxt.setVisibility(View.VISIBLE);

                                    NDVIAdapter adapter = new NDVIAdapter(getActivity(),msgList);
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