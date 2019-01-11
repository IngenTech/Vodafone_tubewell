package com.wrms.vodafone.live_cotton;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.wrms.vodafone.R;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;


import java.util.ArrayList;

/**
 * Created by Admin on 09-09-2017.
 */
public class ResolveRequestActivity extends AppCompatActivity {

    TextView phoneNo,location,message;
    ImageView imageView;
    Spinner resolutionType;
    EditText resolution;

    Button save,saveDraft,cancel;

    ImageView backBTN;
    String resolution_id = null;
    String rqId = null;

    ImageView voiceBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_resolve_activity);



        String ph = getIntent().getStringExtra("phone");
        String loc = getIntent().getStringExtra("location");
        String conv = getIntent().getStringExtra("conversation");
         rqId = getIntent().getStringExtra("requestID");
        String img = getIntent().getStringExtra("image");
        final String voice = getIntent().getStringExtra("audioPath");


        backBTN = (ImageView)findViewById(R.id.backBTN);

        voiceBTN = (ImageView)findViewById(R.id.voiceMsgBtn);
        if (voice!=null && voice.length()>4){
            voiceBTN.setVisibility(View.VISIBLE);
        }else {
            voiceBTN.setVisibility(View.GONE);
        }

        voiceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    if (voice!=null) {
                        String audPath = "http://pdjalna.myfarminfo.com/LogVoice/"+voice;
                        Log.v("audioPath",audPath+"--"+audPath.length());

                        MediaPlayer player = new MediaPlayer();
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource(audPath);
                        player.prepare();
                        player.start();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        phoneNo = (TextView)findViewById(R.id.action_phone);
        location = (TextView)findViewById(R.id.action_location);
        message = (TextView)findViewById(R.id.action_message);
        message.setMovementMethod(ScrollingMovementMethod.getInstance());

        resolutionType = (Spinner)findViewById(R.id.resolution_spinner);
        imageView = (ImageView)findViewById(R.id.action_image);
        resolution = (EditText)findViewById(R.id.resolution_et);

        save = (Button)findViewById(R.id.save_resolve);
        saveDraft = (Button)findViewById(R.id.save_as_draft);
        cancel = (Button)findViewById(R.id.cancel);

        if (ph!=null){
            phoneNo.setText(ph);
        }
        if (loc!=null){
            location.setText(loc);
        }
        if (conv!=null){
            conv = conv.replace("&nbsp","");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                message.setText(Html.fromHtml(conv, Html.FROM_HTML_MODE_COMPACT));
            }else {
                message.setText(Html.fromHtml(conv));
            }
        }

        final String finalConv = conv;
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ResolveRequestActivity.this);

                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.dimAmount = 0.5f;

                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                // Include dialog.xml file
                dialog.setContentView(R.layout.message_popup);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel_popup);
                final TextView txt = (TextView) dialog.findViewById(R.id.text_popup_msg);



                if (finalConv !=null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        txt.setText(Html.fromHtml(finalConv, Html.FROM_HTML_MODE_COMPACT));
                    }else {
                        txt.setText(Html.fromHtml(finalConv));
                    }
                }

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });
            }
        });

        if (img!=null){
            Picasso.with(this).load(img).into(imageView);
        }

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        ArrayList<String> resolutionTypeArray = new ArrayList<>();
        final ArrayList<String> resolutionID = new ArrayList<>();


        resolutionTypeArray.add("Select");
        resolutionTypeArray.add("Disease");
        resolutionTypeArray.add("Nutrient");
        resolutionTypeArray.add("Pest");

        resolutionID.add("0");
        resolutionID.add("Disease");
        resolutionID.add("nutrient");
        resolutionID.add("pest");

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resolutionTypeArray); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resolutionType.setAdapter(varietyArrayAdapter);
        resolutionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    //    districtSpinner.setSelection(position);

                    resolution_id = resolutionID.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(ResolveRequestActivity.this);
                builder.setTitle("Sent Confirmation?");
                builder.setMessage("Are you sure to send the resolution message.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {dialogInterface.dismiss();

                        if (rqId==null || rqId.length()<1){
                            Toast.makeText(getApplicationContext(),"Request id not found",Toast.LENGTH_SHORT).show();
                        }else if (resolution_id==null){
                            Toast.makeText(getApplicationContext(),"Please select resolution type.",Toast.LENGTH_SHORT).show();
                        }else if (resolution.getText().toString().length()<1){
                            Toast.makeText(getApplicationContext(),"Please enter resolution.",Toast.LENGTH_SHORT).show();

                        }else {

                            String nm = AppConstant.visible_Name;

                            sendResolveRequest(rqId,resolution.getText().toString(),nm,resolution_id);

                        }


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {dialogInterface.dismiss();


                    }
                });
                builder.show();
            }
        });

    }


    public void sendResolveRequest(String reqid,String resol,String username,String resType) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Sending Resolve Request...", true);



        StringRequest stringRequest = new StringRequest(Request.Method.GET,"http://pdjalna.myfarminfo.com/PDService.svc/ResolveRequest/"+reqid+"/"+resol+"/"+username+"/"+resType,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        response = response.trim();
                    //    response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("old log response : " + response);

                        finish();

                       /* try {

                           JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
*/
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
