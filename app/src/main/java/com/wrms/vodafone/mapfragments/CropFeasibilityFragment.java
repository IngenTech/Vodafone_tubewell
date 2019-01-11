package com.wrms.vodafone.mapfragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.home.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;*/

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CropFeasibilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/*public class CropFeasibilityFragment extends Fragment implements
        OnChartValueSelectedListener {*/
public class CropFeasibilityFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String STATE_ID = "stateId";

    // TODO: Rename and change types of parameters
    private String latitude;
    private String longitude;
    private String stateId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CropFeasibilityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CropFeasibilityFragment newInstance(String param1, String param2, String param3) {

        CropFeasibilityFragment fragment = new CropFeasibilityFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, param1);
        args.putString(LONGITUDE, param2);
        args.putString(STATE_ID,param3);
        fragment.setArguments(args);
        return fragment;
    }

    public CropFeasibilityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getString(LATITUDE);
            longitude = getArguments().getString(LONGITUDE);
            stateId = getArguments().getString(STATE_ID);
        }
    }
    private static View view;
    ArrayList<Crop> cropArray = new ArrayList<>();
    Spinner cropSpinner;
    Button feasibilitySubmit;
    String csid,crop;
//    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_crop_feasibility, container, false);

        cropSpinner = (Spinner)view.findViewById(R.id.feasibilityCropSpinner);
        feasibilitySubmit = (Button) view.findViewById(R.id.feasibilitySubmit);

        feasibilitySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFeasibilityReport(csid,crop);
            }
        });

        getCropList(stateId);

        /*mChart = (LineChart) view.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tf);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
//        xl.setTypeface(tf);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(100f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);

        *//*if(android.os.Build.VERSION.SDK_INT>21) {
            mChart.setBackgroundTintList(new ColorStateList(new int[][]{{20, 40}, {40, 80}, {80, 100}}, new int[]{R.color.ColorPrimary, R.color.ColorPrimaryDark, R.color.ColorPrimaryLight}));
        }*//*

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);*/

        String[] testing = new String[]{"2011","2012","2013","2014","2015"};
        for(String input : testing){
//            addEntry(input);
        }


        return view;
    }

/*    private void addEntry(String year) {

        LineData data = mChart.getData();

        if (data != null) {

            LineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            *//*data.addXValue(mMonths[data.getXValCount() % img_12] + " "
                    + (year + data.getXValCount() / img_12));*//*
            data.addXValue(year);
            data.addEntry(new Entry((float) (Math.random() * 40) + 30f, set.getEntryCount()), 0);


            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getXValCount() - 121);

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }*/


    private void getCropList(final String stateId) {

        StringRequest stringCropRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/Feasibility/Crops/" + stateId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley Crop Response : " + response);
                        try {
                            response = response.trim();
                        //    response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");
                            ArrayList<String> cropSpinnerArray = new ArrayList<>();
//                            JSONArray jsonArray = new JSONArray(response);
                            JSONArray jsonInnerArray =  new JSONArray(response);
                            if (jsonInnerArray.length() > 0) {

                                for (int i = 0; i < jsonInnerArray.length(); i++) {
                                    JSONObject jsonObject = jsonInnerArray.getJSONObject(i);
                                    String csid = jsonObject.isNull("CSID") ? "" : jsonObject.getString("CSID");
                                    String crop = jsonObject.isNull("Crop") ? "" : jsonObject.getString("Crop");
                                    cropSpinnerArray.add(crop);
                                    cropArray.add(new Crop(csid,crop));
                                }
                            }
                            ArrayAdapter<String> cropSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cropSpinnerArray);
//                            cropSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            cropSpinner.setAdapter(cropSpinnerAdapter);
                            cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (i > 0) {
                                        Crop cropObj = cropArray.get(i);
                                        csid = cropObj.getCSID();
                                        crop = cropObj.getCropName();
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

    private void getFeasibilityReport(String csid,String crop){
//        yfirest.svc/Feasibility/25/77/25774/Cotton/6
        StringRequest stringFeasibilityRequest = new StringRequest(Request.Method.GET, "http://wwf.myfarminfo.com/yfirest.svc/Feasibility/" + latitude + "/" + longitude + "/" + csid+"/"+crop+"/"+stateId+"/"+"india",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String varietyResponse) {
                        try {
                            System.out.println("Feasibility Respose : " + varietyResponse);
                            varietyResponse = varietyResponse.trim();
                            varietyResponse = varietyResponse.substring(1, varietyResponse.length() - 1);
                            varietyResponse = varietyResponse.replace("\\", "");
                            /*seasonArray = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(varietyResponse);
                            if (jsonArray.length() > 0) {
                                seasonArray.add("Select Variety");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.isNull("Variety") ? "" : jsonObject.getString("Variety");
                                    seasonArray.add(name);
                                }
                            }
                            ArrayAdapter<String> varietySpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, seasonArray);
                            varietySpinner.setAdapter(varietySpinnerAdapter);
                            varietySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (i > 0) {
                                        String variety = seasonArray.get(i);
                                        getMandiDetail(cropId, variety, latitude, longitude);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });*/
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

        AppController.getInstance().addToRequestQueue(stringFeasibilityRequest);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

   /* @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }
*/
    private class Crop {
        private String CSID;
        private String cropName;

        public Crop(String CSID, String cropName) {
            this.CSID = CSID;
            this.cropName = cropName;
        }

        public String getCSID() {
            return CSID;
        }

        public void setCSID(String CSID) {
            this.CSID = CSID;
        }

        public String getCropName() {
            return cropName;
        }

        public void setCropName(String cropName) {
            this.cropName = cropName;
        }
    }
}
