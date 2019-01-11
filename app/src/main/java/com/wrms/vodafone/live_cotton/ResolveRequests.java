package com.wrms.vodafone.live_cotton;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.UnresolveBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.volley_class.CustomJSONObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 08-09-2017.
 */
public class ResolveRequests  extends Fragment {


    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resolve_request, container, false);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Resolve Request");
        farmInfo.setTextColor(Color.WHITE);

        recyclerView = (RecyclerView)view.findViewById(R.id.resolveRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                unResolveRequestMethod();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        unResolveRequestMethod();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Resolve Request");
        farmInfo.setTextColor(Color.WHITE);
    }

    private void unResolveRequestMethod(){

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.put("pageIndex", "0");
            jsonObject.put("priority", "0");
            jsonObject.put("Cropid", "0");
            jsonObject.put("stateid", "0");
            jsonObject.put("districtid", "0");
            jsonObject.put("assignid", "0");
            jsonObject.put("assignname", "0");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(Request.Method.PUT, AppManager.getInstance().getAllUnResolveURL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.cancel();
                Log.i("Response unresolve", "" + response.toString());
                getUnResolveResponse(response);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                Log.v("Response unresolve", "" + error.toString());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getUnResolveResponse(JSONObject response) {


        if (response != null) {

            ArrayList<UnresolveBean> listArray = new ArrayList<UnresolveBean>();

            try {
               String unResolve = response.getString("getAllUnresolvedResult");

                if (unResolve!=null && !unResolve.equalsIgnoreCase("NoData")) {
                    unResolve = unResolve.trim();
                    unResolve = unResolve.replace("\\", "");

                    Log.v("parseData", unResolve + "");

                    JSONArray jsonArray = new JSONArray(unResolve);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        UnresolveBean bean = new UnresolveBean();
                        bean.setReqID(jsonObject.getString("ReqId"));
                        bean.setPhoneNo(jsonObject.getString("Phoneno"));
                        bean.setLocation(jsonObject.getString("Location"));
                        bean.setCrop(jsonObject.getString("Crop"));
                        bean.setConversation(jsonObject.getString("Conversation"));
                        bean.setImagePath(jsonObject.getString("ImagePath"));
                        bean.setAssignUser(jsonObject.getString("AssignUser"));
                        bean.setVoicePath(jsonObject.getString("VoiceFile"));

                        listArray.add(bean);

                    }

                    if (listArray.size() > 0) {

                        UnresolveAdapter adapter = new UnresolveAdapter(getActivity(), listArray);
                        recyclerView.setAdapter(adapter);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}