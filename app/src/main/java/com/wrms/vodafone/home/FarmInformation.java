package com.wrms.vodafone.home;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.vodafone.R;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.AllFarmDetail;
import com.wrms.vodafone.entities.FarmAdvisoryDataSet;
import com.wrms.vodafone.entities.FarmInformationData;
import com.wrms.vodafone.entities.MandiDetail;
import com.wrms.vodafone.mapfragments.MandiDetailOnMap;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.wrms.vodafone.utils.AppConstant.APPLIED_VALUE_OF_K;
import static com.wrms.vodafone.utils.AppConstant.APPLIED_VALUE_OF_N;
import static com.wrms.vodafone.utils.AppConstant.APPLIED_VALUE_OF_P;
import static com.wrms.vodafone.utils.AppConstant.DATA_SET;
import static com.wrms.vodafone.utils.AppConstant.IDEAL_VALUE_OF_K;
import static com.wrms.vodafone.utils.AppConstant.IDEAL_VALUE_OF_N;
import static com.wrms.vodafone.utils.AppConstant.IDEAL_VALUE_OF_P;
import static com.wrms.vodafone.utils.AppConstant.MANDI_DETAIL;
import static com.wrms.vodafone.utils.AppConstant.SOWING_DATE_FROM;
import static com.wrms.vodafone.utils.AppConstant.SOWING_DATE_TO;
import static com.wrms.vodafone.utils.AppConstant.isLogin;
import static com.wrms.vodafone.utils.AppConstant.user_id;


public class FarmInformation extends AppCompatActivity {

    EditText farmName;
    Spinner cropSpiner1;
    Spinner varietySpiner;
    EditText sowPeriodFrom;
    EditText sowPeriodTo;
    Spinner baselDoseSpiner;
    EditText valueN;
    EditText valueP;
    EditText valueK;
    EditText otherNutrition;
    Spinner yourConcernSpiner;
    Button submitForm;
    ArrayList<String> cropValue;
    ArrayList<String> applyBasalDose;
    ArrayList<String> varietyValue;
    DBAdapter db;
    LinearLayout ly;
    LinearLayout ll_nutrition;
    FarmInformationData farmInformationData;
    AllFarmDetail allFarmDetail;
    ArrayAdapter<String> varietySpiner1;
    HashMap<String, String> pickCropIdOrValue;
    Calendar myCalendar;
    String allDrawLatLngPoint;
    String creatString;
    private Toolbar toolbar;
    Boolean displayDate = false;
    ArrayList<FarmAdvisoryDataSet> dataSet;
    ArrayList<String> yourConcern;
    int displayDateInEditText;
    String selectedFarmName;
    int callingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBAdapter(this);
        setContentView(R.layout.layout_farm_information);
        ly = (LinearLayout) findViewById(R.id.linearLayout);
        ll_nutrition = (LinearLayout) findViewById(R.id.nutrition_ll);
        farmName = (EditText) findViewById(R.id.farmName);
        cropSpiner1 = (Spinner) findViewById(R.id.crop);
        varietySpiner = (Spinner) findViewById(R.id.variety);
        sowPeriodFrom = (EditText) findViewById(R.id.editTextShowPeriodFrom);
        sowPeriodTo = (EditText) findViewById(R.id.editTextShowPeriodTo);
        baselDoseSpiner = (Spinner) findViewById(R.id.basalDose);
        valueN = (EditText) findViewById(R.id.baselDoseTableN);
        valueP = (EditText) findViewById(R.id.baselDoseTableP);
        valueK = (EditText) findViewById(R.id.baselDoseTableK);
        yourConcernSpiner = (Spinner) findViewById(R.id.yourConcern);
        otherNutrition = (EditText) findViewById(R.id.nutrition);
        submitForm = (Button) findViewById(R.id.submit);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView farmInfo = (TextView) findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Cotton Doctor");
        farmInfo.setTextColor(Color.WHITE);
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayShowTitleEnabled(false); //Make the default lable invisible
        farmInformationData = new FarmInformationData();
        allFarmDetail = new AllFarmDetail();

        checkValue(); //loading crop list in spinner view
        callingActivity = getIntent().getIntExtra("calling-activity", 0);
        switch (callingActivity) {
            case AppConstant.HomeActivity:
                selectedFarmName = getIntent().getStringExtra("FarmName");
                pickCropIdOrValue = (HashMap<String, String>) getIntent().getSerializableExtra("hashMapValue");

                break;
            case AppConstant.AddFarmMap: // this will call whaen you choose farm from the list
                allDrawLatLngPoint = getIntent().getStringExtra("AllLatLngPount");
                pickCropIdOrValue = (HashMap<String, String>) getIntent().getSerializableExtra("hashMapValue");
                break;
        }

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                disPlayDate();
            }

        };
        sowPeriodFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayDateInEditText = AppConstant.SOW_PERIOD_FROM;
                // TODO Auto-generated method stub
                new DatePickerDialog(FarmInformation.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        sowPeriodTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                displayDateInEditText = AppConstant.SOW_PERIOD_TO;
                new DatePickerDialog(FarmInformation.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFarmDetail();
            }
        });

        this.varietySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ly.setVisibility(View.GONE); // disable three field N P K
            }
        });

        applyBasalDose = new ArrayList<String>();
        applyBasalDose.add("Select");
        applyBasalDose.add("Yes");
        applyBasalDose.add("No");
        // data will come from database

        ArrayAdapter<String> baselDoseSpiner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, applyBasalDose);
        baselDoseSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.baselDoseSpiner.setAdapter(baselDoseSpiner);
        this.baselDoseSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ly.setVisibility(View.GONE);
                    ll_nutrition.setVisibility(View.GONE);
                }
                if (position == 1) {  //yes condition
                    ll_nutrition.setVisibility(View.GONE);
                    ly.setVisibility(View.VISIBLE);

                }
                if (position == 2) //no condition
                {
                    ly.setVisibility(View.GONE);
                    ll_nutrition.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yourConcern = new ArrayList<String>();
        yourConcern.add("Select");
        yourConcern.add("Increase my revenue");
        yourConcern.add("Increase my yield");
        yourConcern.add("Get me better price for my produce");
        final ArrayAdapter<String> yourConcernSpiner1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yourConcern);
        yourConcernSpiner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.yourConcernSpiner.setAdapter(yourConcernSpiner1);


        this.yourConcernSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AppConstant.CONCERN_ID = yourConcernSpiner.getSelectedItemPosition();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
// below method will display all farm detail on the farminformation class of every edittext or spinner
        displayDetailOfSelectedFarm(selectedFarmName);

    }
/////////////////function decleared//////////////////// function decleared//////////////////////function decleared//////////////////////
    public void submitFarmDetail() {


        if (!isValidate()) {
            return;
        } else {

            switch (callingActivity) {
                case AppConstant.HomeActivity:
                    farmInformationData.setUserID(allFarmDetail.getUserId().toString());
                    farmInformationData.setFarmId(allFarmDetail.getFarmId().toString());
                    farmInformationData.setAllLatLngPoint(allFarmDetail.getContour().toString());
                    farmInformationData.setState(allFarmDetail.getState().toString());

                    break;
                case AppConstant.AddFarmMap: // this will call whaen you choose farm from the list
                    farmInformationData.setUserID(user_id);
                    farmInformationData.setFarmId("0");
                    farmInformationData.setAllLatLngPoint(allDrawLatLngPoint); // this is conture
                    farmInformationData.setState(AppConstant.stateID);
                    break;
            }

            farmInformationData.setCrop(cropSpiner1.getSelectedItem().toString());
            farmInformationData.setCropID(pickCropIdOrValue.get(farmInformationData.getCrop().toString()));
            farmInformationData.setFarmName(farmName.getText().toString());
            farmInformationData.setVariety(varietySpiner.getSelectedItem().toString());
            farmInformationData.setYourCencern(yourConcernSpiner.getSelectedItem().toString());
            AppConstant.CONCERN_ID = yourConcernSpiner.getSelectedItemPosition(); //this will decide which api will be called
            farmInformationData.setBasalDoseN(valueN.getText().toString());
            farmInformationData.setBasalDoseP(valueP.getText().toString());
            farmInformationData.setBasalDoseK(valueK.getText().toString());
            farmInformationData.setOtherNutrition(otherNutrition.getText().toString());
            if (baselDoseSpiner.getSelectedItem().toString().trim() == "Yes") {
                farmInformationData.setOtherNutrition("0".toString());
                farmInformationData.setBasalDoseN(valueN.getText().toString());
                farmInformationData.setBasalDoseP(valueP.getText().toString());
                farmInformationData.setBasalDoseK(valueK.getText().toString());
                System.out.println();
            } else {
                farmInformationData.setBasalDoseN("0");
                farmInformationData.setBasalDoseP("0");
                farmInformationData.setBasalDoseK("0");
            }
            farmInformationData.setBesalDoseApply(baselDoseSpiner.getSelectedItem().toString());   ///apply condition yes/no/nothing
            farmInformationData.setSowPeriodForm(sowPeriodFrom.getText().toString());
            farmInformationData.setSowPeriodTo(sowPeriodTo.getText().toString());
        }
        creatString = AppManager.getInstance().removeSpaceForUrl(createdJsonStringForFarmSave());

        if (AppManager.isOnline(FarmInformation.this)) {
            if(isLogin) {
                new sentRequestForFarmSave(creatString).execute();
            }else{
                new sentFarmInformationData().execute();
            }

        } else {
            Toast.makeText(getBaseContext(), R.string.network_not_available, Toast.LENGTH_LONG).show();
        }
    }


    public void checkValue() {
        cropValue = new ArrayList<String>();
        // cropValue.add("Select");
        cropValue.add("Select Crop");
        db.open();

        Cursor c = db.getCropList();
        if (c.moveToFirst()) {
//
            do {
                String fn = c.getString(0).toString();

                if (!cropValue.contains(fn)) {
                    cropValue.add(fn);
                    Log.d("FarmInformationsS:----", "data from database--" + fn);
                }

            }
            while (c.moveToNext());
        }
        db.close();
        loadCropSpinner();

    }


    private class sentRequestForFarmSave extends AsyncTask<Void, Void, String> {

        String result = null;
        String createdString;

        public sentRequestForFarmSave(String createdString) {
            this.createdString = createdString;
        }
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FarmInformation.this);
            progressDialog.setMessage("Processing . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String response=null;
            String sendPath = AppManager.getInstance().saveFarmInfo;
            response = AppManager.getInstance().httpRequestPutMethod(sendPath, createdString);
            System.out.println("Response :---"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();
            if(response.contains("Success")){
                Toast.makeText(getBaseContext(), "Farm save successfully", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                if(AppManager.isOnline(FarmInformation.this)) {
                    new sentFarmInformationData().execute();
                }else{
                    Toast.makeText(getBaseContext(), "network not available", Toast.LENGTH_LONG).show();
                }
            }
            if(response.contains("NotSave")){
                Toast.makeText(getBaseContext(), "Request processing error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                return;
            }
            if(response.contains("Error")){
                Toast.makeText(getBaseContext(), "Farm already exist!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                return;
            }
            if(response.contains("Error1")){
                Toast.makeText(getBaseContext(), "Could not connect to server", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                return;
            }
            progressDialog.dismiss();

        }
    }

    private class sentFarmInformationData extends AsyncTask<Void, Void, String> {

        String result = null;
        String createdString;

        public sentFarmInformationData() {

        }
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FarmInformation.this);
            progressDialog.setMessage("Processing . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String sendRequest = null;
            String response=null;
                switch (AppConstant.CONCERN_ID)
                {
                    case AppConstant.INCREASE_REVENIUE:
                        sendRequest = AppManager.getInstance().FarmYieldImprove
                                + AppManager.getInstance().removeSpaceForUrl(createdStringforFieldInprovemnt());
                        response = AppManager.getInstance().httpRequestGetMethod(sendRequest);
                        System.out.println("Increas my reveniue" + response);
                        break;
                    case AppConstant.INCREASE_YIELD:
                        System.out.println("INCREASE_MY_YIELD");
                        sendRequest = AppManager.getInstance().FarmYieldImprove +
                                AppManager.getInstance().removeSpaceForUrl(createdStringforFieldInprovemnt());
                        response = AppManager.getInstance().httpRequestGetMethod(sendRequest);
                        System.out.println("Increas my yield" + response);
                        break;
                    case AppConstant.BEST_PRICE:
                        sendRequest = AppManager.getInstance().mandiOptimal + farmInformationData.getCropID() + "/" +farmInformationData.getVariety() + "/" + AppConstant.latitude + "/" + AppConstant.longitude;
                        System.out.println("get me better price url :" + sendRequest);
                        response = AppManager.getInstance().httpRequestGetMethod(sendRequest);
                        System.out.println("get me better price" + response);
                        break;
                }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();

                switch (AppConstant.CONCERN_ID) {
                    case AppConstant.INCREASE_REVENIUE:
                        progressDialog.dismiss();
                        showFarmAdvisory(response);
                        break;
                    case AppConstant.INCREASE_YIELD:
                        progressDialog.dismiss();
                        showFarmAdvisory(response);
                        break;
                    case AppConstant.BEST_PRICE:
                        progressDialog.dismiss();
                            if (response.contains("NoData")) {
                                Toast.makeText(getBaseContext(), "No mandi found", Toast.LENGTH_LONG).show();
                            } else {
                                Log.d("mandi response", response);
                                showMandiDetailOnMap(response);
                            }
                        break;
                }

            }


        }

    public void showFarmAdvisory(String response) {

        String vlaueN = "";
        String vlaueP = "";
        String vlaueK = "";
        String sowDateFrom = "";
        String sowDateTo = "";
        dataSet = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(response);
            Log.d("sentFarmInformationData", response);
            if (jArray.length() == 0) {
                Toast.makeText(getBaseContext(), "String is empty", Toast.LENGTH_LONG).show();
            } else {

                for (int i = 0; i < jArray.length(); i++) {

                    JSONArray innerArray = jArray.optJSONArray(i);
                    for (int j = 0; j < innerArray.length(); j++) {
                        if (i == 0) {
                            JSONObject jObject = innerArray.getJSONObject(j);
                            String nutrient = jObject.getString("Nutrient");
                            Log.d("nutrient", nutrient);
                            String content = jObject.getString("Content");
                            Log.d("content", content);
                            String soilApplication = jObject.getString("SoilApplication");
                            Log.d("soilApplication", soilApplication);
                            dataSet.add(new FarmAdvisoryDataSet(nutrient, content, soilApplication));

                        }
                        if (i == 1) {
                            JSONObject jObject = innerArray.getJSONObject(j);
                            vlaueN = jObject.getString("N");
                            Log.d("N", vlaueN);
                            vlaueP = jObject.getString("P");
                            Log.d("P", vlaueP);
                            vlaueK = jObject.getString("K");
                            Log.d("K", vlaueK);

                        }
                        if (i == 2) {
                            JSONObject jObject = innerArray.getJSONObject(j);
                            sowDateFrom = jObject.getString("SowingFrom");
                            Log.d("sowingFrom", sowDateFrom);
                            sowDateTo = jObject.getString("SowingTo");
                            Log.d("sowingTo", sowDateTo);

                        }

                    }
                }

            }

            Intent intent = new Intent(FarmInformation.this, Farm_Advisory.class);
            intent.putParcelableArrayListExtra(DATA_SET, dataSet);
            intent.putExtra(APPLIED_VALUE_OF_N, farmInformationData.getBasalDoseN().toString());
            intent.putExtra(APPLIED_VALUE_OF_P, farmInformationData.getBasalDoseP().toString());
            intent.putExtra(APPLIED_VALUE_OF_K, farmInformationData.getBasalDoseK().toString());
            intent.putExtra(IDEAL_VALUE_OF_N, vlaueN);
            intent.putExtra(IDEAL_VALUE_OF_P, vlaueP);
            intent.putExtra(IDEAL_VALUE_OF_K, vlaueK);
            intent.putExtra(SOWING_DATE_FROM, sowDateFrom);
            intent.putExtra(SOWING_DATE_TO, sowDateTo);
            startActivity(intent);

        }
        catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Myfarminfo:" + e.toString());
            Toast.makeText(getBaseContext(), "No response from server try again!", Toast.LENGTH_SHORT).show();

        }
    }


    public void showMandiDetailOnMap(String result) {
        ArrayList<MandiDetail> mandiDetail = new ArrayList<MandiDetail>();
        try {
            if (result == null) {
                Toast.makeText(getBaseContext(), "Could Connect to server Try Again", Toast.LENGTH_LONG).show();
            } else {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    mandiDetail.add(new MandiDetail(jArray.getJSONObject(i)));
                }
                Intent intent = new Intent(FarmInformation.this, MandiDetailOnMap.class);
                intent.putParcelableArrayListExtra(MANDI_DETAIL, mandiDetail);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "No response from server try again!", Toast.LENGTH_LONG).show();

        }

    }

   /* public String createdFarmSaveString() {
        String str = "";
        if (isLogin) {

            // farmInformationData.getUserID() + "/" + farmInformationData.getFarmId() + "/" + farmInformationData.getFarmName() + "/" + farmInformationData.getAllLatLngPoint() + "/" + farmInformationData.getCropID() + "/" +farmInformationData.getState() + "/" + farmInformationData.getVariety() + "/" +farmInformationData.getYourCencern() + "/" + farmInformationData.getBasalDoseN() + "/" + farmInformationData.getBasalDoseP() + "/" +farmInformationData.getBasalDoseK() + "/" + farmInformationData.getBesalDoseApply() + "/" +farmInformationData.getOtherNutrition() + "/" +farmInformationData.getSowPeriodForm() + "/" + farmInformationData.getSowPeriodTo();
            //firest.svc/saveFarmInfo


            str = "'{\"UserID\":\"" + farmInformationData.getUserID() +
                    "\",\"FarmID\":\"" + farmInformationData.getFarmId() +
                    "\",\"FarmName\":\"" + farmInformationData.getFarmName() +
                    "\",\"Contour\":\"" + farmInformationData.getAllLatLngPoint() +
                    "\",\"CropID\":\"" + farmInformationData.getCropID() +
                    "\",\"State\":\"" + farmInformationData.getState() +
                    "\",\"Variety\":\"" + farmInformationData.getVariety() +
                    "\",\"Concerns\":\"" + farmInformationData.getYourCencern() +
                    "\",\"N\":\"" + farmInformationData.getBasalDoseN() +
                    "\",\"P\":\"" + farmInformationData.getBasalDoseP() +
                    "\",\"K\":\"" + farmInformationData.getBasalDoseK() +
                    "\",\"BasalDoseApply\":\"" + farmInformationData.getBesalDoseApply() +
                    "\",\"OtherNutrient\":\"" + farmInformationData.getOtherNutrition() +
                    "\",\"CropPeriodFrom\":\"" + farmInformationData.getSowPeriodForm() +
                    "\",\"CropPeriodTo\":\"" + farmInformationData.getSowPeriodTo() + "\"}";


            return str;
        }
        return null;

    }*/

    public String createdJsonStringForFarmSave() {
        String json = "";
        if (isLogin) {

            // farmInformationData.getUserID() + "/" + farmInformationData.getFarmId() + "/" + farmInformationData.getFarmName() + "/" + farmInformationData.getAllLatLngPoint() + "/" + farmInformationData.getCropID() + "/" +farmInformationData.getState() + "/" + farmInformationData.getVariety() + "/" +farmInformationData.getYourCencern() + "/" + farmInformationData.getBasalDoseN() + "/" + farmInformationData.getBasalDoseP() + "/" +farmInformationData.getBasalDoseK() + "/" + farmInformationData.getBesalDoseApply() + "/" +farmInformationData.getOtherNutrition() + "/" +farmInformationData.getSowPeriodForm() + "/" + farmInformationData.getSowPeriodTo();
            //firest.svc/saveFarmInfo
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("UserID", farmInformationData.getUserID());
                jsonObject.put("FarmID", farmInformationData.getFarmId());
                jsonObject.put("FarmName", farmInformationData.getFarmName());
                jsonObject.put("Contour", farmInformationData.getAllLatLngPoint());
                jsonObject.put("CropID", farmInformationData.getCropID());
                jsonObject.put("State", farmInformationData.getState());
                jsonObject.put("Variety", farmInformationData.getVariety());
                jsonObject.put("Concerns", farmInformationData.getYourCencern());
                jsonObject.put("N", farmInformationData.getBasalDoseN());
                jsonObject.put("P", farmInformationData.getBasalDoseP());
                jsonObject.put("K", farmInformationData.getBasalDoseK());
                jsonObject.put("BasalDoseApply", farmInformationData.getBesalDoseApply());
                jsonObject.put("OtherNutrient", farmInformationData.getOtherNutrition());
                jsonObject.put("CropPeriodFrom", farmInformationData.getSowPeriodForm());
                jsonObject.put("CropPeriodTo", farmInformationData.getSowPeriodTo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //      jsonObject.put("guarderiasIdGuarderias",jsonObject2);
            json = jsonObject.toString();

        }
        return json;

    }

    public String createdStringforFieldInprovemnt() {
        String str =

                        AppConstant.latitude + "/" + AppConstant.longitude + "/" +
                        farmInformationData.getBasalDoseN() + "/" +
                        farmInformationData.getBasalDoseP() + "/" +
                        farmInformationData.getBasalDoseK() + "/" +
                        farmInformationData.getCropID() + "/" +
                        farmInformationData.getVariety() + "/" +
                        farmInformationData.getSowPeriodTo() + "/" +
                        farmInformationData.getState();

        return str;

    }
       /* public  ArrayList<NameValuePair> createdValueForSave()
    {

        yfirest.svc/saveFarmInfo
        '{"UserID":"' + UserID + '","FarmID":"' + FarmID + '","FarmName":"' + FarmName + '","Contour":"' + Contour + '","CropID":"' + CropID + '","State":"' + State + '","Variety":"' + Variety + '","Concerns":"' + Concerns + '","N":"' + N + '","P":"' + P + '","K":"' + K + '","BasalDoseApply":"' + BasalDoseApply + '","OtherNutrient":"' + OtherNutrient + '","CropPeriodFrom":"' + SowDate + '","CropPeriodTo":"' + SowDate + '"}';
        ArrayList<NameValuePair> perameters = new ArrayList<>();
        perameters.add(new BasicNameValuePair( "'{\"UserID\":\"","+"+farmInformationData.getUserID()));
        perameters.add(new BasicNameValuePair( "\",\"FarmID\":\"", farmInformationData.getFarmId()));
        perameters.add(new BasicNameValuePair("FarmName", farmInformationData.getFarmName()));
        perameters.add(new BasicNameValuePair("Contour", farmInformationData.getAllLatLngPoint()));
        perameters.add(new BasicNameValuePair("CropID",farmInformationData.getCropID()));
        perameters.add(new BasicNameValuePair("State", farmInformationData.getState()));
        perameters.add(new BasicNameValuePair("Variety", farmInformationData.getVariety()));
        perameters.add(new BasicNameValuePair("Concerns",farmInformationData.getYourCencern()));
        perameters.add(new BasicNameValuePair("N", farmInformationData.getBasalDoseN()));
        perameters.add(new BasicNameValuePair("P", farmInformationData.getBasalDoseP()));
        perameters.add(new BasicNameValuePair("K", farmInformationData.getBasalDoseK()));
        perameters.add(new BasicNameValuePair("BasalDoseApply", farmInformationData.getBesalDoseApply()));
        perameters.add(new BasicNameValuePair("OtherNutrient",farmInformationData.getOtherNutrition()));
        perameters.add(new BasicNameValuePair("CropPeriodFrom",  farmInformationData.getSowPeriodForm() ));
        perameters.add(new BasicNameValuePair("CropPeriodTo", farmInformationData.getSowPeriodTo()));

        return perameters;


    }*/
    public boolean isValidate() {

        if (!(farmName.getText().toString().length() > 0)) {
            Toast.makeText(getBaseContext(), "Enter Farm Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!(cropSpiner1.getSelectedItem().toString().trim() != "Select Crop")) {
            Toast.makeText(getBaseContext(), "Please Select Crop", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!(varietySpiner.getSelectedItem().toString().trim() != "Select")) {
            Toast.makeText(getBaseContext(), "Please Select variety", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!(sowPeriodFrom.getText().toString().length() > 0 && sowPeriodTo.getText().toString().length() > 0)) {
            Toast.makeText(getBaseContext(), "Please Select Showing Duration", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!(yourConcernSpiner.getSelectedItem().toString().trim() != "Select")) {
            Toast.makeText(getBaseContext(), "Select Your Concern", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!(baselDoseSpiner.getSelectedItem().toString().trim() == "Yes")) {
            if (!(valueN.getText().toString().length() > 0 && valueP.getText().toString().length() > 0 && valueK.getText().toString().length() > 0)) {
                Toast.makeText(getBaseContext(), "Fill Besal Dose Apply Field", Toast.LENGTH_LONG).show();
                return false;
            }

        }else if (baselDoseSpiner.getSelectedItem().toString().trim() == "No") {
            if (!(otherNutrition.getText().toString().length() > 0) ){
                Toast.makeText(getBaseContext(), "fill Nutrition", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getBaseContext(), "Please apply besal dose", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    public void displayDetailOfSelectedFarm(String farmName) {

        String name = farmName;
        if (allDrawLatLngPoint == null) {
                     db.open();
            Cursor c = db.getSelectedFarmsValue(name);
            if (c.moveToFirst()) {
                if (c.getCount() > 0) {
                    do {

                        allFarmDetail.setFarmId(c.getString(c.getColumnIndex(DBAdapter.FARM_ID)));
                        String str = c.getString(c.getColumnIndex(DBAdapter.FARM_ID));
                        allFarmDetail.setFarmName(c.getString(c.getColumnIndex(DBAdapter.FARM_NAME)));
                        this.farmName.setText(allFarmDetail.getFarmName().toString());
                        allFarmDetail.setUserId(c.getString(c.getColumnIndex(DBAdapter.USER_ID)));
                        allFarmDetail.setContour(c.getString(c.getColumnIndex(DBAdapter.CONTOUR)));
                        allFarmDetail.setCropId(c.getString(c.getColumnIndex(DBAdapter.CROP_ID)));
                        allFarmDetail.setState(c.getString(c.getColumnIndex(DBAdapter.STATE_ID)));
                        allFarmDetail.setCropFrom(c.getString(c.getColumnIndex(DBAdapter.CROP_FROM)));
                        sowPeriodFrom.setText(allFarmDetail.getCropFrom());
                        allFarmDetail.setCropTo(c.getString(c.getColumnIndex(DBAdapter.CROP_TO)));
                        sowPeriodTo.setText(allFarmDetail.getCropTo().toString());
                        this.cropSpiner1.setSelection(cropValue.indexOf(pickCropIdOrValue.get(allFarmDetail.getCropId())));
                        allFarmDetail.setBasalDoseApply(c.getString(c.getColumnIndex(DBAdapter.BASAL_DOSE_APPLY)));
                        this.baselDoseSpiner.setSelection(applyBasalDose.indexOf(allFarmDetail.getBasalDoseApply()));
                        allFarmDetail.setValueN(c.getString(c.getColumnIndex(DBAdapter.VALUE_N)));
                        valueN.setText(allFarmDetail.getValueN().toString());
                        allFarmDetail.setValueP(c.getString(c.getColumnIndex(DBAdapter.VALUE_P)));
                        valueP.setText(allFarmDetail.getValueP().toString());
                        allFarmDetail.setValueK(c.getString(c.getColumnIndex(DBAdapter.VALUE_K)));
                        valueK.setText(allFarmDetail.getValueK().toString());
                        allFarmDetail.setOtherNutrient(c.getString(c.getColumnIndex(DBAdapter.OTHER_NUTRIENT)));
                        otherNutrition.setText(allFarmDetail.getOtherNutrient().toString());
                        allFarmDetail.setConcern(c.getString(c.getColumnIndex(DBAdapter.CONCERN)));
                        System.out.println("concern is " + allFarmDetail.getConcern());
                        this.yourConcernSpiner.setSelection(yourConcern.indexOf(allFarmDetail.getConcern()));


                    }
                    while (c.moveToNext());

                }
                db.close();

            }

        }//-
    }



    public void loadCropSpinner() {
        ArrayAdapter<String> cropSpiner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cropValue);
        cropSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.cropSpiner1.setAdapter(cropSpiner);

        this.cropSpiner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = cropSpiner1.getSelectedItem().toString();

                varietyValue = new ArrayList<String>();

                db.open();
                Cursor c = db.getVariety(item);
                int x = c.getCount();
//
                Log.d("getcount:----", "curserValue--" + x);
                if (c.moveToFirst()) {
                    if (c.getCount() > 0) {

                        do {
                            String fn = c.getString(0);
                            Log.d("FarmInformations:----", "String fn = c.getString(2).toString();" + fn);
                            if (!varietyValue.contains(fn)) {
                                varietyValue.add(fn);

                            }
                            Log.d("FarmInformations:----", "selectVariety--" + fn);
                        }
                        while (c.moveToNext());
                    }
                    db.close();
                }

                varietySpiner1 = new ArrayAdapter<String>(FarmInformation.this, android.R.layout.simple_spinner_item, varietyValue);
                // Drop down layout style - list view with radio button
                varietySpiner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                FarmInformation.this.varietySpiner.setAdapter(varietySpiner1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void disPlayDate() {

        String myFormat = "dd-MM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        switch (displayDateInEditText) {
            case AppConstant.SOW_PERIOD_FROM:
                sowPeriodFrom.setText(sdf.format(myCalendar.getTime()));
                break;
            case AppConstant.SOW_PERIOD_TO:
                sowPeriodTo.setText(sdf.format(myCalendar.getTime()));
                break;
        }

    }



}
