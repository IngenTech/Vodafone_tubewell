package com.wrms.vodafone.mapfragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wrms.vodafone.adapter.CustomAdapter;
import com.wrms.vodafone.entities.CropQueryData;
import com.wrms.vodafone.entities.FarmAdvisoryDataSet;
import com.wrms.vodafone.home.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FarmAdvisoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FarmAdvisoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CROP_QUERY_ARRAY = "cropQueryArray";
    private static final String LAT_LNG_STATE_ID = "latLngStateId";

    // TODO: Rename and change types of parameters
    private ArrayList<CropQueryData> cropQueryArray;
    private String latLngStateId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cropQueryData Parameter 1.
     * @param latLngStateId Parameter 2.
     * @return A new instance of fragment FarmAdvisoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmAdvisoryFragment newInstance(ArrayList<CropQueryData> cropQueryData, String latLngStateId) {
        FarmAdvisoryFragment fragment = new FarmAdvisoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CROP_QUERY_ARRAY, cropQueryData);
        args.putString(LAT_LNG_STATE_ID, latLngStateId);
        fragment.setArguments(args);
        return fragment;
    }

    public FarmAdvisoryFragment() {
        // Required empty public constructor
    }

    private Spinner queryCropSpinner;

    // ArrayList<FarmAdvisoryDataSet> dataSet;
    ListView listView;
    String parcelableValue;
    EditText appliedBaselDoseN;
    EditText appliedBaselDoseP;
    EditText appliedBaselDoseK;

    EditText idealBaselDoseN;
    EditText idealBaselDoseP;
    EditText idealBaselDoseK;
    TextView idealCropDuration;

    String a_valueN;
    ArrayList<FarmAdvisoryDataSet> dataSet = new ArrayList<>();
    String latitude;
    String longitude;
    String stateId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cropQueryArray = getArguments().getParcelableArrayList(CROP_QUERY_ARRAY);
            latLngStateId = getArguments().getString(LAT_LNG_STATE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farm_advisory, container, false);

        queryCropSpinner = (Spinner)view.findViewById(R.id.queryCropSpinner);
        listView = (ListView)view.findViewById(R.id.listView);
        appliedBaselDoseN = (EditText)view.findViewById(R.id.appliedBaselDoseN);
        appliedBaselDoseP = (EditText)view.findViewById(R.id.appliedBaselDoseP);
        appliedBaselDoseK = (EditText)view.findViewById(R.id.appliedBaselDoseK);

        idealBaselDoseN = (EditText)view.findViewById(R.id.idealBaselDoseN);
        idealBaselDoseP = (EditText)view.findViewById(R.id.idealBaselDoseP);
        idealBaselDoseK = (EditText)view.findViewById(R.id.idealBaselDoseK);
        idealCropDuration = (TextView)view.findViewById(R.id.textView);
/*
        TextView farmInfo = (TextView) view.findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("My Farm Info");
*/

        if(latLngStateId!=null){
            String[] latLngStateArray = latLngStateId.split(",");
            if(latLngStateArray.length==3){
                latitude = latLngStateArray[0];
                longitude = latLngStateArray[1];
                stateId = latLngStateArray[2];
            }
        }
        if(cropQueryArray!=null){
            ArrayList<String> cropsName = new ArrayList<>();
            for(CropQueryData data : cropQueryArray){
                cropsName.add(data.getCrop());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,cropsName);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            queryCropSpinner.setAdapter(adapter);
        }

        queryCropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                getIncreaseRevenuRequest(cropQueryArray.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        idealCropDuration.setText("You should sow your crop between " + "-" + " and " + "-" + " to maximize yield");


        return view;
    }

    private void getIncreaseRevenuRequest(final CropQueryData cropQueryData){

        appliedBaselDoseN.setText(cropQueryData.getBasalDoseN().isEmpty()?"0":cropQueryData.getBasalDoseN());
        appliedBaselDoseP.setText(cropQueryData.getBasalDoseP().isEmpty()?"0":cropQueryData.getBasalDoseP());
        appliedBaselDoseK.setText(cropQueryData.getBasalDoseK().isEmpty()?"0":cropQueryData.getBasalDoseK());
        idealBaselDoseN.setText("");
        idealBaselDoseP.setText("");
        idealBaselDoseK.setText("");
        idealCropDuration.setText("You should show your crop between " + "-" + " and " + "-" + " to maximize yield");
        listView.setAdapter(null);

        System.out.println("cropId : "+cropQueryData.getCropID());
        String url = "http://myfarminfo.com/YFIRest.svc/Farm/YieldImprove/"+latitude + "/" + longitude + "/"+ cropQueryData.getBasalDoseN() + "/" + cropQueryData.getBasalDoseP() + "/" + cropQueryData.getBasalDoseK() + "/" + cropQueryData.getCropID()+"/" + cropQueryData.getVariety() + "/" + cropQueryData.getSowPeriodForm() + "/" + stateId;
        url = url.replaceAll(" ","%20");
        System.out.println("URL  " + url);
        StringRequest revenueRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        try {
                            response = response.trim();
                         //   response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");
                            System.out.println("JSON RESPONSE STRING : "+response);

                            JSONArray jsonArray = new JSONArray(response);

                            JSONArray nutrientArrayObject = jsonArray.getJSONArray(0);
                            ArrayList<FarmAdvisoryDataSet> farmAdvisoryDataSet = new ArrayList<>();
                            for(int i = 0 ; i < nutrientArrayObject.length() ; i++) {
                                JSONObject nutrientObject = nutrientArrayObject.getJSONObject(i);
                                String Nutrient = nutrientObject.getString("Nutrient");
                                String Content = nutrientObject.getString("Content");
                                String SoilApplication = nutrientObject.getString("SoilApplication");
                                FarmAdvisoryDataSet data = new FarmAdvisoryDataSet(Nutrient,Content,SoilApplication);
                                System.out.println(Nutrient + " , " + Content + " , " + SoilApplication);
                                farmAdvisoryDataSet.add(data);
                            }
                            listView.setAdapter(new CustomAdapter(getActivity(), farmAdvisoryDataSet));

                            JSONArray npkArrayObject = jsonArray.getJSONArray(1);
                            JSONObject npkJsonObject = npkArrayObject.getJSONObject(0);
                            String N = npkJsonObject.getString("N");
                            String P = npkJsonObject.getString("P");
                            String K = npkJsonObject.getString("K");
                            if(!cropQueryData.getBasalDoseN().trim().equals(N)){
                                appliedBaselDoseN.setBackgroundColor(Color.RED);
                            }
                            if(!cropQueryData.getBasalDoseP().trim().equals(P)){
                                appliedBaselDoseP.setBackgroundColor(Color.RED);
                            }
                            if(!cropQueryData.getBasalDoseK().trim().equals(K)){
                                appliedBaselDoseK.setBackgroundColor(Color.RED);
                            }

                            System.out.println(N+" , "+P+" , "+K);
                            idealBaselDoseN.setText(N);
                            idealBaselDoseP.setText(P);
                            idealBaselDoseK.setText(K);

                            JSONArray durationArrayObject = jsonArray.getJSONArray(2);
                            JSONObject durationJsonObject = durationArrayObject.getJSONObject(0);
                            String SowingFrom = durationJsonObject.getString("SowingFrom");
                            String SowingTo = durationJsonObject.getString("SowingTo");
                            System.out.println(SowingFrom+" , "+SowingTo);
                            idealCropDuration.setText("You should sow your crop between " + SowingFrom + " and " + SowingTo + " to maximize yield");


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Could not connect to the server", Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(revenueRequest);

    }

    private void getIncreaseYieldRequest(CropQueryData cropQueryData){

        appliedBaselDoseN.setText(cropQueryData.getBasalDoseN().isEmpty()?"0":cropQueryData.getBasalDoseN());
        appliedBaselDoseP.setText(cropQueryData.getBasalDoseP().isEmpty()?"0":cropQueryData.getBasalDoseP());
        appliedBaselDoseK.setText(cropQueryData.getBasalDoseK().isEmpty()?"0":cropQueryData.getBasalDoseK());

        StringRequest revenueRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/YFIRest.svc/Farm/YieldImprove/"+latitude+"/"+longitude+"/"+cropQueryData.getBasalDoseN()+"/"+cropQueryData.getBasalDoseP()+"/"+cropQueryData.getBasalDoseK()+"/"+cropQueryData.getCropID()+"/"+cropQueryData.getVariety()+"/"+cropQueryData.getSowPeriodForm()+"/" + stateId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("Increase Revenue Volley Response : " + response);
                        try {
                            response = response.trim();
                        //    response = response.substring(1, response.length() - 1);
                            response = response.replace("\\", "");
                            response = response.replace("\\", "");
                            response = response.replace("\"{", "{");
                            response = response.replace("}\"", "}");
                            response = response.replace("\"[", "[");
                            response = response.replace("]\"", "]");
                            JSONArray locationArray = new JSONArray(response);
                            LatLngBounds.Builder bc = new LatLngBounds.Builder();
                            for (int i = 0; i < locationArray.length(); i++) {
                                JSONObject locationObject = locationArray.getJSONObject(i);

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
        AppController.getInstance().addToRequestQueue(revenueRequest);

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

}
