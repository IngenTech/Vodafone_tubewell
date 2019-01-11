package com.wrms.vodafone.live_cotton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.MessageBean;
import com.wrms.vodafone.home.AppController;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.utils.Utility;
import com.wrms.vodafone.volley_class.CustomJSONObjectRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Admin on 12-09-2017.
 */
public class MessageDetailActivity extends AppCompatActivity {

    RecyclerView ddRecyclerView;
    private RecyclerView.LayoutManager ddLayoutManager;
    ImageView backBTN;

    String isReopen;

    RelativeLayout reopenLay;
    ImageView reopenImage;
    EditText reopenMessage;
    Button reopenBTN;

    String uploadedPicName = null;

    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    String imageString;
    private String userChoosenTask;
    String id = null;

    String msg = null;
    ArrayList<MessageBean> messageList;
    ChatRoomThreadAdapter adapter;
    String imageName = null;
    RelativeLayout audioStart;


    public static final int RequestPermissionCode = 1;
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    MediaPlayer mediaPlayer ;
    Button uploadBTN;
    String sendFlag = null;
    String responsePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_activity);

        audioStart = (RelativeLayout) findViewById(R.id.start);
       /* mediaRecorder=new MediaRecorder();
        random = new Random();
        audioStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        sendFlag = null;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startrecord();                            }
                        }, 1000);

                        break;
                    case MotionEvent.ACTION_UP:
                        Toast.makeText(getApplicationContext(),"Stop Recording",Toast.LENGTH_SHORT).show();
                        stoprecord();
                        break;
                }
                return false;
            }
        });
*/

        audioStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               voicePopup();
            }
        });

        backBTN = (ImageView) findViewById(R.id.backBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reopenLay = (RelativeLayout) findViewById(R.id.reopen_lay);
        reopenImage = (ImageView) findViewById(R.id.reopen_img);
        reopenMessage = (EditText) findViewById(R.id.reopen_msg);
        reopenBTN = (Button) findViewById(R.id.reopen_btn);

        isReopen = getIntent().getStringExtra("isReopen");
        imageName = getIntent().getStringExtra("imageName");

       /* if (isReopen != null && isReopen.equalsIgnoreCase("Resolved")) {
            reopenLay.setVisibility(View.VISIBLE);
            reopenImage.setVisibility(View.VISIBLE);
        } else if (isReopen != null && isReopen.equalsIgnoreCase("UnResolved")) {
            reopenLay.setVisibility(View.VISIBLE);
            reopenImage.setVisibility(View.GONE);
        } else if (isReopen != null && isReopen.equalsIgnoreCase("sent")) {
            reopenLay.setVisibility(View.VISIBLE);
            reopenImage.setVisibility(View.GONE);
        }*/

        ddRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        ddRecyclerView.setHasFixedSize(true);
        ddLayoutManager = new LinearLayoutManager(this);
        ddRecyclerView.setLayoutManager(ddLayoutManager);
        // ddRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        ddRecyclerView.setItemAnimator(new DefaultItemAnimator());

        id = getIntent().getStringExtra("req_id");
        if (id != null) {
            loadMessageListById(id);
        }

        reopenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageString = null;
                selectImage();
            }
        });

        reopenBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msg = reopenMessage.getText().toString().trim();
                if (imageString != null && imageString.length() > 10) {
                    if (msg != null && msg.length() > 0) {
                        uploadImage();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter message", Toast.LENGTH_SHORT).show();
                    }
                } else if (responsePath!=null && responsePath.length()>2){

                    if (msg != null && msg.length() > 0) {
                        createNewLogStringToServer(null,responsePath,msg);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter message", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    if (msg != null && msg.length() > 0) {
                        createNewLogStringToServer(imageName,null,msg);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter message", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }


    private void startrecord() {
        if(checkPermission()) {

            AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.mp3";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            sendFlag = "start";

            Toast.makeText(getApplicationContext(), "Recording started",
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }

        vibrate();
    }

    private void stoprecord() {
        mediaRecorder.stop();
        vibrate();

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });

                //   uploadAudio();
                // post();
                if (sendFlag!=null) {
                    if (isNetworkAvailable()) {
                        sendData();
                    }else {
                        Toast.makeText(getApplicationContext(),"No internet connections",Toast.LENGTH_SHORT).show();

                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Please Hold more than 1 second for recording",Toast.LENGTH_SHORT).show();
                }

            }
        }).start();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void sendData(){

        String url = "http://pdjalna.myfarminfo.com/PDService.svc/UploadVoice/";
        File file = new File(AudioSavePathInDevice);
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(true); // Send in multiple parts if needed
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            System.out.println("Result : " + result);

            if (result!=null) {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("UploadVoiceResult")){
                    responsePath = jsonObject.getString("UploadVoiceResult");

                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MessageDetailActivity.this);
                                    builder.setTitle("Success").
                                            setMessage("Voice uploaded successfully.").
                                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    if (id != null) {
                                                        createNewLogStringToServer(null,responsePath,"Voice_Message");
                                                    }
                                                }
                                            });

                                    android.support.v7.app.AlertDialog dialog = builder.create();
                                    dialog.show();
                                    Log.v("jksajkjsk",responsePath+"");
                                }
                            });


                        }
                    }).start();

                }else {
                    responsePath = null;
                }
            }
            //Do something with response...



        } catch (Exception e) {
            // show error
        }
    }



    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(MessageDetailActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }



    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Select From Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //  builder.setTitle("Upload Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean resultCam = Utility.checkPermissionCamera(MessageDetailActivity.this);
                    if (resultCam) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Select From Gallery")) {
                    userChoosenTask = "Select From Gallery";
                    boolean resultCam = Utility.checkPermissionGallery(MessageDetailActivity.this);
                    if (resultCam) {
                        galleryIntent();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE_START);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_START);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_START) {
                onCaptureImageResult(data);
            } else if (requestCode == SELECT_FILE_START) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        reopenImage.setImageBitmap(thumbnail);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reopenImage.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (IllegalAccessError e) {
            e.printStackTrace();
        }


    }


    public void loadMessageListById(String id) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Fetching messages. Please wait...", true);

        Log.v("sjdks", "http://pdjalna.myfarminfo.com/PDService.svc/Conversation/" + id);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://pdjalna.myfarminfo.com/PDService.svc/Conversation/" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        response = response.trim();
                     //   response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("old log response : " + response);
                        try {

                            messageList = new ArrayList<MessageBean>();
                            JSONArray ja = new JSONArray(response);
                            for (int i = 0; i < ja.length(); i++) {
                                MessageBean bean = new MessageBean();
                                JSONObject jsonObject = ja.getJSONObject(i);
                                bean.setName(jsonObject.getString("Name"));
                                bean.setLocation(jsonObject.getString("Location"));
                                bean.setMessage(jsonObject.getString("Message"));
                                bean.setMessageType(jsonObject.getString("Messagetype"));
                                bean.setMsgDate(jsonObject.getString("MessageDate"));
                                bean.setReqid(jsonObject.getString("Reqid"));
                                bean.setPhoneno(jsonObject.getString("Phoneno"));
                                bean.setRequestUser(jsonObject.getString("User"));
                                bean.setCrop(jsonObject.getString("Crop"));
                                bean.setCropId(jsonObject.getString("CropId"));
                                bean.setRequestDate(jsonObject.getString("RequestDate"));
                                bean.setUser(jsonObject.getString("User"));
                                bean.setImage(jsonObject.getString("ImagePath"));
                                bean.setAudioPath(jsonObject.getString("VoiceFile"));

                                messageList.add(bean);

                            }

                            if (messageList.size() > 0) {
                                adapter = new ChatRoomThreadAdapter(MessageDetailActivity.this, messageList);
                                ddRecyclerView.setAdapter(adapter);

                                adapter.notifyDataSetChanged();
                                if (adapter.getItemCount() > 1) {
                                    // scrolling to bottom of the recycler view
                                    Log.v("ssscrolllllll", "ssscrolllllll");
                                    ddRecyclerView.getLayoutManager().smoothScrollToPosition(ddRecyclerView, null, adapter.getItemCount() - 1);
                                }

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
                noInternetMethod();
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void uploadImage() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.putOpt("ImageString", imageString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(Request.Method.PUT, AppManager.getInstance().uploadImageURL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.cancel();
                Log.i("Response upload image", "" + response.toString());
                getResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                Log.v("Response vishal coupon", "" + error.toString());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getResponse(JSONObject response) {

        uploadedPicName = null;

        if (response != null) {

            try {

                uploadedPicName = response.getString("uploadBase64ImageResult");
                if (uploadedPicName != null) {
                    reopenImage.setImageBitmap(null);
                    createNewLogStringToServer(uploadedPicName,null,msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private void createNewLogStringToServer(String picName,String voiceFile,String msssg) {

        final ProgressDialog dialog = ProgressDialog.show(this, "", "Reopening thread. Please wait...", true);
        String userName = AppConstant.visible_Name;

        String url = "http://pdjalna.myfarminfo.com/PDService.svc/Thread/Reopen/" + id + "/" + picName + "/" + msssg + "/" + userName+ "/" + voiceFile;
        url = url.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        response = response.trim();
                     //   response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("old log response : " + response);

                        uploadedPicName = null;

                        reopenMessage.setText("");
                        //finish();
                        responsePath = null;
                        loadMessageListById(id);


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


       /* new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                String message = "";
                String userName = AppConstant.visible_Name;

                String response = null;
                try {

                    response = CustomHttpClient.executeHttpGet("http://livecotton.myfarminfo.com/PDService.svc/Thread/Reopen/" + id + "/" + picName + "/" + msssg + "/" + userName+ "/" + voiceFile);
                    System.out.println("Message send Response " + response);
                    // Log.v("Message send Response",response+"");

                    response = response.trim();
                    response = response.substring(1, response.length() - 1);
                    response = response.replace("\\", "");

                    System.out.println("Message send Response " + response);




                } catch (Exception e) {
                    e.printStackTrace();


                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                uploadedPicName = null;

                reopenMessage.setText("");
                //finish();
                responsePath = null;
                loadMessageListById(id);
            }
        }.execute();*/
    }

    private void noInternetMethod() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("No Internet").
                setMessage("Do You want to Refresh?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        if (id != null) {
                            loadMessageListById(id);
                        }
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

    public void voicePopup(){

        sendFlag = null;
        final Dialog dialog = new Dialog(MessageDetailActivity.this);

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
        dialog.setContentView(R.layout.audio_activity);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        dialog.show();

        uploadBTN = (Button)dialog.findViewById(R.id.uploadBTN);
        Button cancelBTN = (Button)dialog.findViewById(R.id.cancelBTN);
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFlag =null;
                responsePath = null;
                dialog.dismiss();
            }
        });
        uploadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responsePath = null;
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                        if (sendFlag!=null) {
                            if (isNetworkAvailable()) {
                                sendData();
                            }else {
                                Toast.makeText(getApplicationContext(),"No internet connections",Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Please record voice first",Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();

                    }
                }).start();


            }
        });

        buttonStart = (Button) dialog.findViewById(R.id.button);
        buttonStop = (Button) dialog.findViewById(R.id.button2);
        buttonPlayLastRecordAudio = (Button) dialog.findViewById(R.id.button3);
        buttonStopPlayingRecording = (Button)dialog.findViewById(R.id.button4);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.mp3";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(getApplicationContext(), "Recording started",
                            Toast.LENGTH_LONG).show();

                    sendFlag = "start";
                } else {
                    requestPermission();
                }

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(getApplicationContext(), "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }



}
