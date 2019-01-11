package com.wrms.vodafone.mapfragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.ShowDataAdapter;
import com.wrms.vodafone.bean.DiseaseBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 01-09-2017.
 */
public class DiseaseAdviceFragment extends Fragment {
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

    private ProgressBar firstBar = null;
    private ProgressBar secondBar = null;

    LinearLayout parent;

    ArrayList<DiseaseBean> dataList = new ArrayList<DiseaseBean>();


    public DiseaseAdviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Spinner diseaseSpinner;

    String lat = null;
    String lon = null;

    Button showBTN;
    String d_ID = "2";

    TextView happenText;

    ProgressBar highBar, mediumBar, lowBar;
    TextView highPerText, lowPerText, mediumPerText;
    TextView maxText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.disease_advice_fragment, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

        lat = prefs.getString("lat", null);
        lon = prefs.getString("lon", null);

        if (lat == null) {
            lat = "" + LatLonCellID.lat;
            lon = "" + LatLonCellID.lon;

        }


        parent = (LinearLayout) view.findViewById(R.id.parent);
        happenText = (TextView) view.findViewById(R.id.disease_happen_text);
        maxText = (TextView) view.findViewById(R.id.maxText);

        firstBar = (ProgressBar) view.findViewById(R.id.firstBar);
        secondBar = (ProgressBar) view.findViewById(R.id.secondBar);

        highBar = (ProgressBar) view.findViewById(R.id.highBar);
        mediumBar = (ProgressBar) view.findViewById(R.id.mediumBar);
        lowBar = (ProgressBar) view.findViewById(R.id.lowBar);

        highPerText = (TextView) view.findViewById(R.id.highPercent);
        mediumPerText = (TextView) view.findViewById(R.id.mediumPercent);
        lowPerText = (TextView) view.findViewById(R.id.lowPercent);

        firstBar.setMax(100);
        secondBar.setMax(100);
        highBar.setMax(100);
        mediumBar.setMax(100);
        lowBar.setMax(100);


        diseaseSpinner = (Spinner) view.findViewById(R.id.diseaseSpinner);
        showBTN = (Button) view.findViewById(R.id.disease_show_btn);
        showBTN.setVisibility(View.GONE);
        showBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());

                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.dimAmount = 0.5f;

                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                // Include dialog.xml file
                dialog.setContentView(R.layout.show_data_popup);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel_popup);
                final RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.showDataListView);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                ShowDataAdapter adapter = new ShowDataAdapter(getActivity(), dataList);
                recyclerView.setAdapter(adapter);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });

            }
        });

        ArrayList<String> diseaseList = new ArrayList<>();
        final ArrayList<String> diseaseIdArray = new ArrayList<>();


        diseaseList.add("Whitefly");
        diseaseList.add("Pink Bollworm");

        diseaseIdArray.add("2");
        diseaseIdArray.add("26");

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, diseaseList); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseSpinner.setAdapter(varietyArrayAdapter);
        diseaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                d_ID = diseaseIdArray.get(position);

                loadDiseaseData(d_ID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        // loadForecastData(d_ID);

        return view;
    }


    ProgressDialog dialog;

    public void loadDiseaseData(String ID) {
        dialog = ProgressDialog.show(getActivity(), "", "Fetching Forecast. Please wait...", true);

        Log.v("sjdks", "http://wwf.myfarminfo.com/yfirest.svc/Disease/Advice/" + lat + "/" + lon + "/12" + "/" + ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://wwf.myfarminfo.com/yfirest.svc/Disease/Advice/" + lat + "/" + lon + "/12" + "/" + ID + "/India",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        // Display the first 500 characters of the response string.

                        parent.setVisibility(View.VISIBLE);
                        response = response.trim();
                     //   response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("Disease Response : " + response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jb = jsonObject.getJSONObject("ss");
                            String maximumText = jb.getString("Str1");
                            maxText.setText(Html.fromHtml(maximumText));

                            JSONArray jsonArray = jsonObject.getJSONArray("dataTable");
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                String firstProgress = jsonObject1.getString("Chance_Freq");
                                String secondProgress = jsonObject1.getString("Severity_TypeFreq");

                                String highProg = jsonObject1.getString("Severity_HighFreq");
                                String mediumProg = jsonObject1.getString("Severity_MediumFreq");
                                String lowProg = jsonObject1.getString("Severity_LowFreq");

                                if (firstProgress != null) {

                                    Double f = Double.valueOf(firstProgress);

                                    firstBar.setProgress(f.intValue());
                                    happenText.setText("Disease happened in " + f.intValue() + "%" + " of this years");
                                }

                                if (secondProgress != null) {
                                    Double f = Double.valueOf(secondProgress);
                                    secondBar.setProgress(f.intValue());
                                }

                                if (highProg != null) {
                                    Double f = Double.valueOf(highProg);
                                    highBar.setProgress(f.intValue());
                                    highPerText.setText(f.intValue() + "%");
                                }

                                if (mediumProg != null) {
                                    Double f = Double.valueOf(mediumProg);
                                    mediumBar.setProgress(f.intValue());
                                    mediumPerText.setText(f.intValue() + "%");
                                }

                                if (lowProg != null) {
                                    Double f = Double.valueOf(lowProg);
                                    lowBar.setProgress(f.intValue());
                                    lowPerText.setText(f.intValue() + "%");
                                }
                            }

                            dataList = new ArrayList<DiseaseBean>();
                            JSONArray jA = jsonObject.getJSONArray("dataTable2");
                            for (int i = 0; i < jA.length(); i++) {
                                DiseaseBean bean = new DiseaseBean();
                                bean.setDate(jA.getJSONObject(i).getString("Date"));
                                bean.setMaxTem(jA.getJSONObject(i).getString("MaxTemp"));
                                bean.setMinTemp(jA.getJSONObject(i).getString("MinTemp"));

                                bean.setRain(jA.getJSONObject(i).getString("MinTemp"));
                                bean.setHumidityEve(jA.getJSONObject(i).getString("HumidityEve"));
                                bean.setHimidityMor(jA.getJSONObject(i).getString("HumidityMor"));
                                dataList.add(bean);
                            }

                            if (dataList.size() > 0) {

                                showBTN.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
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