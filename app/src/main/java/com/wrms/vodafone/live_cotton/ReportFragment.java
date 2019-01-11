package com.wrms.vodafone.live_cotton;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wrms.vodafone.R;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.volley_class.CustomJSONObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Admin on 05-09-2017.
 */
public class ReportFragment extends Fragment {


    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Spinner statusSpinner;
    RelativeLayout fromDateLay, toDateLay;
    TextView fromDateTV, toDateTV;

    String lat = null;
    String lon = null;
    private int mYear, mMonth, mDay;

    private int mYear1, mMonth1, mDay1;
    String status_id = null;
    Button searchBTN;

    RecyclerView recyclerView;
    TextView nodata;
    HorizontalScrollView searchLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.report_fragment_live_cotton, container, false);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Report");
        farmInfo.setTextColor(Color.WHITE);

        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

        lat = prefs.getString("lat", null);
        lon = prefs.getString("lon", null);

        statusSpinner = (Spinner) view.findViewById(R.id.status_spinner);
        fromDateLay = (RelativeLayout) view.findViewById(R.id.fromDate);
        toDateLay = (RelativeLayout) view.findViewById(R.id.toDate);
        fromDateTV = (TextView) view.findViewById(R.id.date1);
        toDateTV = (TextView) view.findViewById(R.id.date2);
        nodata = (TextView) view.findViewById(R.id.nodata);
        searchLay = (HorizontalScrollView) view.findViewById(R.id.searched_lay);

        recyclerView = (RecyclerView) view.findViewById(R.id.report_list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        searchBTN = (Button) view.findViewById(R.id.search_history_report);

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {

                    String todate = toDateTV.getText().toString().trim();
                    String fromdate = fromDateTV.getText().toString().trim();
                    getReportData(todate, fromdate, status_id);
                }
            }
        });

        final ArrayList<String> statusArray = new ArrayList<>();
        final ArrayList<String> statusIDArray = new ArrayList<>();


        statusArray.add("Select");
        statusArray.add("Unresolved");
        statusArray.add("Resolved");
        statusArray.add("Sent");
        statusArray.add("Pending");
        statusArray.add("Summary");
        statusArray.add("Received");
        statusArray.add("State");
        statusArray.add("Crop");


        statusIDArray.add("Unresolved");
        statusIDArray.add("Resolved");
        statusIDArray.add("Sent");
        statusIDArray.add("Pending");
        statusIDArray.add("Summary");
        statusIDArray.add("Received");
        statusIDArray.add("State");
        statusIDArray.add("Crop");


        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, statusArray); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(varietyArrayAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {

                    status_id = statusArray.get(position);
                }


                // loadDiseaseData(d_ID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        toDateLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        DecimalFormat mFormat = new DecimalFormat("00");
                        mFormat.format(Double.valueOf(year));
                        mFormat.setRoundingMode(RoundingMode.DOWN);
                        String Dates = mFormat.format(Double.valueOf(dayOfMonth)) + "/" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "/" + mFormat.format(Double.valueOf(year));

                        toDateTV.setText(Dates);


                    }
                }, mYear, mMonth, mDay);
                //    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                dpd.show();
            }

        });

        fromDateLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear1 = c.get(Calendar.YEAR);
                mMonth1 = c.get(Calendar.MONTH);
                mDay1 = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        DecimalFormat mFormat = new DecimalFormat("00");
                        mFormat.format(Double.valueOf(year));
                        mFormat.setRoundingMode(RoundingMode.DOWN);
                        String Dates = mFormat.format(Double.valueOf(dayOfMonth)) + "/" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "/" + mFormat.format(Double.valueOf(year));

                        fromDateTV.setText(Dates);


                    }
                }, mYear1, mMonth1, mDay1);
                //  dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                dpd.show();
            }

        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Report");
        farmInfo.setTextColor(Color.WHITE);
    }

    public boolean isValid() {

        boolean isValid = true;

        String todate = toDateTV.getText().toString().trim();
        String fromdate = fromDateTV.getText().toString().trim();

        if (todate == null || todate.length() < 5) {

            Toast.makeText(getActivity(), "Please select to date.", Toast.LENGTH_SHORT).show();
            return false;

        } else if (fromdate == null || fromdate.length() < 5) {

            Toast.makeText(getActivity(), "Please select from date", Toast.LENGTH_SHORT).show();
            return false;

        } else if (status_id == null || status_id.length() < 1) {
            Toast.makeText(getActivity(), "Please select status type", Toast.LENGTH_SHORT).show();
            return false;

        }
        return isValid;
    }


    private void getReportData(String toD, String fromD, String status) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.put("ToDate", toD);
            jsonObject.put("FromDate", fromD);
            jsonObject.put("Status", status);
            jsonObject.put("State", "Select State");
            jsonObject.put("crop", "Select Crop");
            jsonObject.put("pageIndex", "10");


            Log.v("ldkls", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(Request.Method.PUT, AppManager.getInstance().searchHistoryURL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.cancel();
                getSearchResponse(response);
                Log.v("search response", response.toString() + "");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                Log.v("Response search history", "" + error.toString());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getSearchResponse(JSONObject response) {


        if (response != null) {

            try {
                String reportData = response.getString("SearchResult");


                ArrayList<ReportBean> arrayList = new ArrayList<ReportBean>();

                if (reportData != null && !reportData.equalsIgnoreCase("NoData")) {
                    reportData = reportData.trim();
                    reportData = reportData.replace("\\", "");

                    Log.v("parseData", reportData + "");


                    nodata.setVisibility(View.GONE);
                    searchLay.setVisibility(View.VISIBLE);

                    JSONArray jsonArray = new JSONArray(reportData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ReportBean bean = new ReportBean();
                        bean.setReqID(jsonObject.getString("ReqId"));
                        bean.setPhoneNo(jsonObject.getString("Phoneno"));
                        bean.setName(jsonObject.getString("Name"));
                        bean.setLocation(jsonObject.getString("Location"));
                        bean.setCrop(jsonObject.getString("Crop"));
                        bean.setConversation(jsonObject.getString("Conversation"));
                        bean.setStatus(jsonObject.getString("Status"));
                        bean.setRequestDate(jsonObject.getString("RequestDate"));
                        bean.setResolveDate(jsonObject.getString("ResolveDate"));
                        bean.setSentDate(jsonObject.getString("SentDate"));
                        bean.setPendingDate(jsonObject.getString("PendingDate"));
                        bean.setResolverUser(jsonObject.getString("ResolveUser"));
                        bean.setResolutionType(jsonObject.getString("ResolutionType"));

                        arrayList.add(bean);

                    }

                    if (arrayList.size() > 0) {

                        ReportAdapter adapter = new ReportAdapter(getActivity(), arrayList);
                        recyclerView.setAdapter(adapter);

                    }

                } else {
                    nodata.setVisibility(View.VISIBLE);
                    searchLay.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


}