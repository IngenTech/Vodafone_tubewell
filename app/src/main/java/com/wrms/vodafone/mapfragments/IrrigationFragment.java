package com.wrms.vodafone.mapfragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.MessageAdapter;
import com.wrms.vodafone.adapter.MultiselectionAdapter;
import com.wrms.vodafone.entities.VoiceMessageBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 09-10-2017.
 */
public class IrrigationFragment extends Fragment {


    EditText time, date, stopTime;
    private int mYear, mMonth, mDay;
    private int hou, min, sec;

    FloatingActionButton updateDeviceBTN;
    String onOffString;
    String deviceID = null;

    String sDate ;
    String sTime ;
    String stop_time;
    String userID = null;
    SharedPreferences prefs;

    public static IrrigationFragment newInstance() {
        IrrigationFragment fragment = new IrrigationFragment();

        return fragment;
    }

    public IrrigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.irrigation_fragment, container, false);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Water Management");
        farmInfo.setTextColor(Color.WHITE);
        onOffString = "#9:N;";


        prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);


        userID = prefs.getString(AppConstant.PREFRENCE_KEY_USER_ID, "");


        updateDeviceBTN = (FloatingActionButton) view.findViewById(R.id.phone_update);

        updateDeviceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ss = getResources().getString(R.string.update_device);
                noDevicePopup(ss);
            }
        });

        Switch simpleSwitch = (Switch) view.findViewById(R.id.simpleSwitch);
        time = (EditText) view.findViewById(R.id.timeET);
        date = (EditText) view.findViewById(R.id.dateET);
        stopTime = (EditText) view.findViewById(R.id.hoursET);

        simpleSwitch.setTextOn("On"); // displayed text of the Switch whenever it is in checked or on state
        simpleSwitch.setTextOff("Off");

        SharedPreferences prf = getActivity().getSharedPreferences("irrigation", getActivity().MODE_PRIVATE);

        String st = prf.getString("start_time",null);
        String sd= prf.getString("start_date",null);
        String sh= prf.getString("start_hours",null);
        String oo= prf.getString("on_off",null);
        if (st!=null){

            time.setText(st);
        }
        if (sd!=null){
            date.setText(sd);
        }
        if (sh!=null){

            stopTime.setText(sh);
        }
        if (oo!=null){
            onOffString  = oo;
        }

       /* if (onOffString.equalsIgnoreCase("#9:N;")){
            time.setText("");
            date.setText("");
            stopTime.setText("");
        }
*/

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getActivity(), "The Switch is " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    onOffString = "#9:Y;";
                    smsOnOffMethod();
                } else {
                    onOffString = "#9:N;";
                    smsOnOffMethod();
                }
            }
        });

        SharedPreferences pref11 = getActivity().getSharedPreferences("irrigation", getActivity().MODE_PRIVATE);


        String saveData = pref11.getString("on_off", null);
        if (saveData!=null && saveData.equalsIgnoreCase("#9:N;")){
            irrigationStartPopup();
        }else {

        }


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean resultCam = Utility.checkPermissionSMS(getActivity());
                if (resultCam) {

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
                            String Dates = mFormat.format(Double.valueOf(year)) + "-" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "-" + mFormat.format(Double.valueOf(dayOfMonth));

                            date.setText(Dates);


                        }
                    }, mYear, mMonth, mDay);
                    //    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    dpd.setTitle("Select Date");
                    dpd.show();
                }
            }

        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                final Calendar c = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        final Calendar mcurrentTime1 = Calendar.getInstance();
                        final Calendar c1 = Calendar.getInstance();
                        mcurrentTime1.set(Calendar.HOUR_OF_DAY,selectedHour);
                        mcurrentTime1.set(Calendar.MINUTE,selectedMinute);

                        Log.v("lslas",""+mcurrentTime1.getTimeInMillis() +"---"+ c1.getTimeInMillis());

                        if (mcurrentTime1.getTimeInMillis() > c1.getTimeInMillis()) {

                            String output = String.format("%02d:%02d", selectedHour, selectedMinute);
                            time.setText( output);

                        }else {
                            Toast.makeText(getActivity(),"Please select future time",Toast.LENGTH_SHORT).show();
                            time.setText( "");
                        }

                    }
                }, hour, minute, false);

                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();

            }
        });


        stopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                final Calendar c = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);

                final TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String output = String.format("%02d:%02d", selectedHour, selectedMinute);
                        String ssss = time.getText().toString();

                        if (ssss!=null && ssss.length()>2) {
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                Date date1 = simpleDateFormat.parse(ssss);
                                Date date2 = simpleDateFormat.parse(output);

                                long difference = date2.getTime() - date1.getTime();
                                int days = (int) (difference / (1000 * 60 * 60 * 24));
                                int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                                int minn = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                               /* hours = (hours < 0 ? -hours : hours);
                                minn = (minn < 0 ? -minn : minn);*/

                                int aa = (hours*60)+minn;
                                Log.v("heylkdjdj", minn + "--" + hours+"--"+aa);
                                if (aa>4){
                                    stopTime.setText(output);
                                }else {
                                    stopTime.setText("");
                                    Toast.makeText(getActivity(),"Stop time must greater than start. ",Toast.LENGTH_SHORT).show();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getActivity(),"Please select start time first",Toast.LENGTH_SHORT).show();
                        }


                       /* if (mcurrentTime.getTimeInMillis() > c.getTimeInMillis()) {

                            String output = String.format("%02d:%02d", selectedHour, selectedMinute);
                            stopTime.setText(output);
                        }else {
                            Toast.makeText(getActivity(),"Please select future time",Toast.LENGTH_SHORT).show();
                        }*/
                    }
                }, hour, minute, false);


                mTimePicker.setTitle("Select Stop Time");
                mTimePicker.show();

            }
        });

        deviceID = prefs.getString("Device_Id", null);


        Log.v("kjsnckjsn",deviceID+"");

        if (deviceID==null || deviceID.length()<10){

            String ss = getResources().getString(R.string.no_device);
            noDevicePopup(ss);
        }

       // deviceID = "7053089569";


        return view;
    }

    public void smsOnOffMethod(){
        sDate = date.getText().toString().trim();
        sTime = time.getText().toString().trim();
        stop_time = stopTime.getText().toString().trim();
        prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
        deviceID = prefs.getString("Device_Id", null);

        Log.v("Vishal Response", "" + deviceID + "--" + onOffString);

        if (sDate == null || sDate.length() < 0) {
            Toast.makeText(getActivity(), "Please enter start date", Toast.LENGTH_SHORT).show();
        } else if (sTime == null || sTime.length() < 0) {
            Toast.makeText(getActivity(), "Please enter start time", Toast.LENGTH_SHORT).show();
        } else if (stop_time == null || stop_time.length() < 0) {
            Toast.makeText(getActivity(), "Please enter Hours ", Toast.LENGTH_SHORT).show();
        } else if (deviceID == null || deviceID.length() < 10) {
            Toast.makeText(getActivity(), "Receiver mobile number is blank.", Toast.LENGTH_SHORT).show();

        } else {
            sendMethod();

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Water Management");
        farmInfo.setTextColor(Color.WHITE);

    }


    public  void sendMethod(){

        boolean resultCam = Utility.checkPermissionSMS(getActivity());
        if (resultCam) {
            Log.v("hashkjah","On/Off code - " + onOffString + "\n" + "Start Date - " + sDate + "\n" + "Start Time - " + sTime + "\n" + "Number Of hours - " + stop_time);

            try {

              /*  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + deviceID));
               // intent.putExtra("sms_body", "On/Off code - " + onOffString + "\n" + "Start Date - " + sDate + "\n" + "Start Time - " + sTime + "\n" + "Number Of hours - " + sHours);

                intent.putExtra("sms_body", onOffString);
                startActivity(intent);*/

              /*  SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(deviceID, null, onOffString, null, null);
*/
                if (!onOffString.equalsIgnoreCase("#9:N;")) {

                    SharedPreferences prefs = getActivity().getSharedPreferences("irrigation", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("start_time", sTime);
                    editor.putString("start_date", sDate);
                    editor.putString("start_hours", stop_time);
                    editor.putString("on_off", onOffString);
                    editor.apply();
                }else {
                    SharedPreferences pref = getActivity().getSharedPreferences("irrigation", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                }


            }catch (Exception e) {
                Toast.makeText(getActivity(),"SMS failed, please try again.", Toast.LENGTH_LONG).show(); e.printStackTrace();
            }

        }
    }


    public void noDevicePopup(String str) {


        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        // Include dialog.xml file
        dialog.setContentView(R.layout.no_device_popup);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout doneBTN = (RelativeLayout) dialog.findViewById(R.id.done_nodevice);
        RelativeLayout cancelBTN = (RelativeLayout) dialog.findViewById(R.id.cancel_nodevice);
        TextView deviceText = (TextView)dialog.findViewById(R.id.device_text);
        deviceText.setText(str);

        doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                updateDevicePopup();
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void updateDevicePopup() {


        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        // Include dialog.xml file
        dialog.setContentView(R.layout.update_device_popup);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText deviceNumber = (EditText)dialog.findViewById(R.id.update_device_et);
        RelativeLayout doneBTN = (RelativeLayout) dialog.findViewById(R.id.done_update_device);
        RelativeLayout cancelBTN = (RelativeLayout) dialog.findViewById(R.id.cancel_update_device);



        doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d_number = deviceNumber.getText().toString().trim();
                Log.v("slll",""+d_number.length());

                if (d_number==null || d_number.length()<10){
                    Toast.makeText(getActivity(),"Please enter valid Device number.",Toast.LENGTH_SHORT).show();
                }else if (userID==null || userID.length()<1) {

                    Toast.makeText(getActivity(),"User id not found.",Toast.LENGTH_SHORT).show();

                }else {
                    dialog.cancel();
                    updateDeviceMethod(d_number,userID);
                }
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void updateDeviceMethod(final String device_num, String user_id) {
        final Dialog dialoug1 = ProgressDialog.show(getActivity(), "",
                "Fetching Messages Please wait...", true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppManager.getInstance().updateDeviceURL+user_id+"/"+device_num,
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

                        System.out.println("Volley State Response : " + response);
                        if (response!=null){


                               SharedPreferences   prf = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);
                               SharedPreferences.Editor editor = prf.edit();
                               editor.putString("Device_Id", device_num);
                               editor.commit();


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

    public  void sendMethod1(){

        boolean resultCam = Utility.checkPermissionSMS(getActivity());
        if (resultCam) {
            Log.v("hashkjah","On/Off code - " + onOffString + "\n" + "Start Date - " + sDate + "\n" + "Start Time - " + sTime + "\n" + "Number Of hours - " + stop_time);

            try {

              /*  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + deviceID));
               // intent.putExtra("sms_body", "On/Off code - " + onOffString + "\n" + "Start Date - " + sDate + "\n" + "Start Time - " + sTime + "\n" + "Number Of hours - " + sHours);

                intent.putExtra("sms_body", onOffString);
                startActivity(intent);*/

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(deviceID, null, onOffString, null, null);

                if (!onOffString.equalsIgnoreCase("#9:N;")) {

                    SharedPreferences prefs = getActivity().getSharedPreferences("irrigation", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("start_time", sTime);
                    editor.putString("start_date", sDate);
                    editor.putString("start_hours", stop_time);
                    editor.putString("on_off", onOffString);
                    editor.apply();
                }else {
                    SharedPreferences pref = getActivity().getSharedPreferences("irrigation", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                }


            }catch (Exception e) {
                Toast.makeText(getActivity(),"SMS failed, please try again.", Toast.LENGTH_LONG).show(); e.printStackTrace();
            }

        }
    }


    public void irrigationStartPopup() {


        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        // Include dialog.xml file
        dialog.setContentView(R.layout.start_popup);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout doneBTN = (RelativeLayout) dialog.findViewById(R.id.done_start);
        RelativeLayout cancelBTN = (RelativeLayout) dialog.findViewById(R.id.cancel_start);

        doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

                onOffString = "#9:N;";
                sendMethod1();
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

}
