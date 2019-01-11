package com.wrms.vodafone.live_cotton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 31-08-2017.
 */
public class LogOldFragment extends Fragment {



    RecyclerView recyclerView;
    LinearLayout noData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Button startNewBTN;
    FloatingActionButton addFloatingButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.log_old_fragment, container, false);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("My Messages");
        farmInfo.setTextColor(Color.WHITE);

        recyclerView = (RecyclerView)view.findViewById(R.id.messageRecyclerView);
        noData = (LinearLayout) view.findViewById(R.id.nodata);

        addFloatingButton = (FloatingActionButton)view.findViewById(R.id.fab);

        addFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LogNewFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();

            }
        });

        startNewBTN = (Button)view.findViewById(R.id.start_new);
        startNewBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LogNewFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadMessageList(AppConstant.mobile_no);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        /*searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumb = phoneET.getText().toString();
                if (phoneNumb==null || phoneNumb.length()<7){
                    Toast.makeText(getActivity(),"Please enter the valid mobile no.",Toast.LENGTH_LONG).show();
                }else {
                    loadMessageList(phoneNumb);
                }

            }
        });*/

        loadMessageList(AppConstant.mobile_no);



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

       // loadMessageList(AppConstant.mobile_no);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("My Messages");
        farmInfo.setTextColor(Color.WHITE);
    }

    ProgressDialog dialog;

    public void loadMessageList(String mobileno) {
        dialog = ProgressDialog.show(getActivity(), "", "Fetching Old messages. Please wait...", true);

        Log.v("sjdks","http://pdjalna.myfarminfo.com/PDService.svc/Threads/"+mobileno);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,"http://pdjalna.myfarminfo.com/PDService.svc/Threads/"+mobileno,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        mSwipeRefreshLayout.setRefreshing(false);
                        response = response.trim();
                       // response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");

                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("old log response : " + response);

                        if (!response.equalsIgnoreCase("\"NoData\"")) {
                            try {

                                ArrayList<MessegeListBean> messageList = new ArrayList<MessegeListBean>();
                                JSONArray ja = new JSONArray(response);
                                for (int i = 0; i < ja.length(); i++) {
                                    MessegeListBean bean = new MessegeListBean();
                                    JSONObject jsonObject = ja.getJSONObject(i);
                                    bean.setRequestID(jsonObject.getString("Reqid"));
                                    bean.setDate(jsonObject.getString("RequestDate"));
                                    bean.setMessage(jsonObject.getString("Message"));
                                    bean.setStatus(jsonObject.getString("Status"));
                                    bean.setImageName(jsonObject.getString("ImagePath"));
                                    messageList.add(bean);

                                }

                                if (messageList.size() > 0) {
                                    noData.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    LogOldMessageAdapter adapter = new LogOldMessageAdapter(getActivity(), messageList);
                                    recyclerView.setAdapter(adapter);

                               /* adapter.notifyDataSetChanged();
                                if (adapter.getItemCount() > 1) {
                                    // scrolling to bottom of the recycler view

                                    Log.v("ssscrolllllll","ssscrolllllll");
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount()-1);
                                }
*/
                                } else {
                                    noData.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            noData.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                System.out.println("Volley Error : " + error);
                noInternetMethod();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void noInternetMethod() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("No Internet").
                setMessage("Do You want to Refresh?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        loadMessageList(AppConstant.mobile_no);
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