package com.wrms.vodafone.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.MessageAdapter;
import com.wrms.vodafone.entities.DataBean;
import com.wrms.vodafone.entities.VillageBean;
import com.wrms.vodafone.entities.VoiceMessageBean;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 25-07-2017.
 */
public class AdvisoryActivity extends AppCompatActivity {

    Spinner districtSpinner,villageSpinner;
    Button submitBtn;
    ImageView backBTN;

    private String cityArr[];
    String villageID = null;
    String vill_id = null;
    String lat = null;
    String lon = null;
    String villageName = null;
    RecyclerView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advisory_activity);

        districtSpinner = (Spinner)findViewById(R.id.advisor_district);
        villageSpinner = (Spinner)findViewById(R.id.advisor_village);
        submitBtn = (Button)findViewById(R.id.advisory_submit);
        backBTN = (ImageView)findViewById(R.id.backBTN);

        SharedPreferences prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);

         lat = prefs.getString("lat", null);
         lon = prefs.getString("lon", null);

        list = (RecyclerView)findViewById(R.id.list_voice);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vill_id==null){
                    Toast.makeText(getApplicationContext(),"please select Village",Toast.LENGTH_SHORT).show();
                }else {
                    messageData();
                }
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NavigationDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ArrayList<String> districtList = new ArrayList<>();
        final ArrayList<String> districtID = new ArrayList<>();


        districtList.add("-Select-");
        districtList.add("Bhavnagar");
        districtList.add("Botad");
        districtList.add("Chhota Udaipur");
      //  districtList.add("Jagityal");
      //  districtList.add("Jalna");
        districtList.add("Jamnagar");
        districtList.add("Junagadh");
        districtList.add("Rajkot");
        districtList.add("Sabarkantha");
        districtList.add("Surendranagar");
        districtList.add("Vadodara");




        districtID.add("0");
        districtID.add("15841");
        districtID.add("16311");
        districtID.add("16441");
       // districtID.add("16321");
       // districtID.add("16032");
        districtID.add("15844");
        districtID.add("15845");
        districtID.add("15854");
        districtID.add("15855");
        districtID.add("15857");
        districtID.add("15859");


        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, districtList); //selected item will look like a spinner set from XML
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(),NavigationDrawerActivity.class);
        startActivity(intent);
        finish();
    }

    public String villages(String ID) {
        String message = "NoValue";
        String sendRequest = null;
        sendRequest = "http://myfarminfo.com/yfirest.svc/Clients/GGRC/Villages/"+ID;
        System.out.println("Village Request URL : " + sendRequest);
        String response = AppManager.getInstance().httpRequestGetMethod(AppManager.getInstance().removeSpaceForUrl(sendRequest));
        System.out.println("state id is - --" + response);
        System.out.println("state id is - fcasdfasfasdfasf--");

        if (response == "") {
            return message;
        } else {

            message = "Success";
            return message;
        }
    }


    ProgressDialog dialoug;

    public void loadVillagesData(String ID) {
        dialoug = ProgressDialog.show(AdvisoryActivity.this, "",
                "Fetching Villages. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
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



                        DataBean bean = new DataBean();
                        bean = getEventTypeList( response);
                        ArrayList<VillageBean> cityList = new ArrayList<VillageBean>();
                        cityList = bean.getCityList();
                        cityArr = new String[cityList.size()];
                        for (int i = 0; i < cityList.size(); i++) {
                            cityArr[i] = cityList.get(i).getVilageName();
                        }

                        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(AdvisoryActivity.this, android.R.layout.simple_spinner_item, cityArr);
                        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        villageSpinner.setAdapter(eventTypeAdapter);

                        final DataBean finalBean = bean;
                        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                villageID = finalBean.getCityList().get(position).getVillageID();
                                villageName = finalBean.getCityList().get(position).getVilageName();

                                vill_id = villageID;
                                Log.v("ksjkls",villageID);

                                SharedPreferences prefs =getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                                lat = prefs.getString("lat",lat);
                                lon = prefs.getString("lon",lon);

                                SharedPreferences.Editor editor  = prefs.edit();
                                editor.putString("villageId",villageID);
                                editor.putString("villageName",villageName);
                                editor.apply();
/*
                                if (villageID!=null){

                                    String[] parts = villageID.split("-");
                                    vill_id = parts[0];
                                    lat = parts[1];
                                    lon = parts[2];
                                }*/
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


    ProgressDialog dialoug1;

    public void messageData() {
        dialoug1 = ProgressDialog.show(AdvisoryActivity.this, "",
                "Fetching Messages Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/Clients/GGRC/SMS/Data/"+vill_id+"/"+"Village"+"/"+lat+"/"+lon+"/"+villageName+"/"+"0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug1.cancel();
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
                        if (response!=null){

                            try{

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("DTLegends");

                                ArrayList<VoiceMessageBean> msgList = new ArrayList<VoiceMessageBean>();


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    VoiceMessageBean messageBean = new VoiceMessageBean();
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    messageBean.setMessageId(jsonObject1.getString("ID"));
                                    messageBean.setMessageText(jsonObject1.getString("Message"));

                                    messageBean.setVillageId(jsonObject1.getString("VillageID"));
                                    messageBean.setScheduleDate(jsonObject1.getString("ScheduleDate"));
                                    messageBean.setStatus(jsonObject1.getString("Status"));
                                    messageBean.setMessageType(jsonObject1.getString("MessageType"));
                                    msgList.add(messageBean);
                                }

                                if (msgList.size()>0){

                                    MessageAdapter adapter = new MessageAdapter(AdvisoryActivity.this,msgList);
                                    list.setAdapter(adapter);
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
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
