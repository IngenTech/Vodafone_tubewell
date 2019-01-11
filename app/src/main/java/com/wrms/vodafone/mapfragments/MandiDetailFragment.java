package com.wrms.vodafone.mapfragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.wrms.vodafone.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MandiDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MandiDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RESPONSE_STRING = "response_string";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mandiDetailResponse;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MandiDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MandiDetailFragment newInstance(String param1, String param2) {
        MandiDetailFragment fragment = new MandiDetailFragment();
        Bundle args = new Bundle();
        args.putString(RESPONSE_STRING, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MandiDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mandiDetailResponse = getArguments().getString(RESPONSE_STRING);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TableLayout table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mandi_detail, container, false);
        table = (TableLayout) view.findViewById(R.id.mandiDetailTable);
        initializeTable();
        return view;
    }

    private void initializeTable() {
        TableRow tr_head = new TableRow(getActivity());
//        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView commodity = new TextView(getActivity());
//        label_date.setId(20);
        commodity.setText("Commodity");
        commodity.setTextColor(Color.BLACK);
        commodity.setPadding(5, 5, 5, 5);
        commodity.setGravity(Gravity.CENTER);
        tr_head.addView(commodity);// add the column to the table row here

        TextView variety = new TextView(getActivity());
//        label_weight_kg.setId(21);// define id that must be unique
        variety.setText("Variety"); // set the text for the header
        variety.setTextColor(Color.BLACK); // set the color
        variety.setPadding(5, 5, 5, 5); // set the padding (if required)
        variety.setGravity(Gravity.CENTER);
        tr_head.addView(variety); // add the column to the table row here

        TextView pricePerQuintle = new TextView(getActivity());
//        label_weight_kg.setId(21);// define id that must be unique
        pricePerQuintle.setText("Price(Rs)/Quintal"); // set the text for the header
        pricePerQuintle.setTextColor(Color.BLACK); // set the color
        pricePerQuintle.setPadding(5, 5, 5, 5); // set the padding (if required)
        pricePerQuintle.setGravity(Gravity.CENTER);
        tr_head.addView(pricePerQuintle); // add the column to the table row here

        TextView MinPrice = new TextView(getActivity());
//        label_weight_kg.setId(21);// define id that must be unique
        MinPrice.setText("MinPrice"); // set the text for the header
        MinPrice.setTextColor(Color.BLACK); // set the color
        MinPrice.setPadding(5, 5, 5, 5); // set the padding (if required)
        MinPrice.setGravity(Gravity.CENTER);
        tr_head.addView(MinPrice); // add the column to the table row here


        table.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        System.out.println("MANDI RESPONSE : " + mandiDetailResponse);
        try {
            JSONObject jsonObject = new JSONObject(mandiDetailResponse);

            if (jsonObject.has("DT")) {
                JSONArray jsonArray = jsonObject.getJSONArray("DT");
                Integer count = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dataObject = jsonArray.getJSONObject(i);

                    String location = dataObject.getString("Location");
                    String commodityValue = dataObject.getString("Commodity");
                    String varietyValue = dataObject.getString("Variety");
                    String pricePerQuintleValue = dataObject.getString("Price(Rs) / Quintal");
                    String minPriceValue = dataObject.getString("MinPrice");
                    String maxPriceValue = dataObject.getString("MaxPrice");
                    String dateValue = dataObject.getString("Date");
// Create the table row
                    TableRow tr = new TableRow(getActivity());
                    if (count % 2 != 0) {
                        tr.setBackgroundResource(R.color.table_row_alternate_1);
                    } else {
                        tr.setBackgroundResource(R.color.table_row_alternate_2);
                    }
                    tr.setId(100 + count);
                    tr.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

//Create two columns to add as table data
                    // Create a TextView to add date
                    TextView labelDATE = new TextView(getActivity());
                    labelDATE.setId(200 + count);
                    labelDATE.setText(commodityValue);
                    labelDATE.setPadding(2, 0, 5, 0);
                    labelDATE.setTextColor(Color.DKGRAY);
                    labelDATE.setGravity(Gravity.CENTER);
                    tr.addView(labelDATE);

                    TextView labelWEIGHT = new TextView(getActivity());
                    labelWEIGHT.setId(200 + count);
                    labelWEIGHT.setText(varietyValue);
                    labelWEIGHT.setTextColor(Color.DKGRAY);
                    labelWEIGHT.setGravity(Gravity.CENTER);
                    tr.addView(labelWEIGHT);

                    TextView priceTextView = new TextView(getActivity());
                    priceTextView.setId(200 + count);
                    priceTextView.setText(pricePerQuintleValue);
                    priceTextView.setTextColor(Color.DKGRAY);
                    priceTextView.setGravity(Gravity.CENTER);
                    tr.addView(priceTextView);

                    TextView minPriceTextView = new TextView(getActivity());
                    minPriceTextView.setId(200 + count);
                    minPriceTextView.setText(minPriceValue);
                    minPriceTextView.setTextColor(Color.DKGRAY);
                    minPriceTextView.setGravity(Gravity.CENTER);
                    tr.addView(minPriceTextView);

// finally add this to the table row
                    table.addView(tr, new TableLayout.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    count++;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
