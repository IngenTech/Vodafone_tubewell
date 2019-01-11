package com.wrms.vodafone.live_cotton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
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
import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.CropBean;
import com.wrms.vodafone.bean.MultiBean;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Admin on 31-08-2017.
 */
public class LogNewFragment extends Fragment {

  //  AutoCompleteTextView location;
    int selected = 0;
    Double latitude, longitude;
    String addressLocation;

    EditText phoneET, nameET, messageET;
    Spinner cropSpinner;
    ImageView imageView;
    Button logBTN;

    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    String imageString;
    private String userChoosenTask;
    String cropID = "12";
    String cropName="Cotton";
    String uploadedPicName;

    String phone ;
    String name;
    String loc ;
    String msg;
    String locality ;
    String country ;
    String zipCode;
    String subLocality;
    String district;
    String lati;
    String longi;
    String selectedState;
    Button voiceBTN;

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

    ArrayList<CropBean> cropList;
    ArrayList<MultiBean> multiArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.log_new_fragment, container, false);

        cropSpinner = (Spinner)view.findViewById(R.id.crop_spinner);

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Log New Request");
        farmInfo.setTextColor(Color.WHITE);

        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, getActivity().MODE_PRIVATE);

         lati = prefs.getString("lat",null);
         longi = prefs.getString("lon",null);
        String villageID = prefs.getString("villageId",null);

        if (villageID!=null) {
            getCropList(villageID);
        }

        if (lati!=null && longi!=null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(lati), Double.parseDouble(longi), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                subLocality = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                locality = addresses.get(0).getLocality();
                selectedState = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                zipCode = addresses.get(0).getPostalCode();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        phoneET = (EditText) view.findViewById(R.id.log_new_phone);
        phoneET.setText(AppConstant.mobile_no);

        nameET = (EditText) view.findViewById(R.id.log_new_name);
        nameET.setText(AppConstant.visible_Name);

        voiceBTN = (Button)view.findViewById(R.id.voiceBtn);
        voiceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voicePopup();
            }
        });

        messageET = (EditText) view.findViewById(R.id.log_new_message);

       /* cropSpinner = (Spinner) view.findViewById(R.id.log_new_crop_spinner);

        final ArrayList<String> cropListArray = new ArrayList<>();
        final ArrayList<String> cropIDArray = new ArrayList<>();


        cropListArray.add("Select Crop");
        cropListArray.add("Cotton");

        cropIDArray.add("0");
        cropIDArray.add("2");

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cropListArray); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropSpinner.setAdapter(varietyArrayAdapter);
        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    //    districtSpinner.setSelection(position);

                    cropID = cropIDArray.get(position);
                    cropName = cropListArray.get(position);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        imageView = (ImageView) view.findViewById(R.id.log_new_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        logBTN = (Button) view.findViewById(R.id.log_request_btn);

        logBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    uploadImage();
                }


            }
        });

        /*location = (AutoCompleteTextView) view.findViewById(R.id.log_new_location);
        location.setThreshold(1);

        location.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4) {
                    placesTask = new PlacesTask();
                    placesTask.execute(s.toString());

                    if (selected == 1) {
                        try {
                            location.dismissDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            location.showDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (selected == 1) {
                        try {
                            location.dismissDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            location.showDropDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

                selected = 1;
                try {
                    location.dismissDropDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                location.setText(hm.get("description"));

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));


                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);
                if (selected == 1) {
                    try {
                        location.dismissDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        location.showDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

//
        location.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (selected == 1) {
                    try {
                        location.dismissDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        location.showDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView farmInfo = (TextView) getActivity().findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Log New Request");
        farmInfo.setTextColor(Color.WHITE);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Select From Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //  builder.setTitle("Upload Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean resultCam = Utility.checkPermissionCamera(getActivity());
                    if (resultCam) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Select From Gallery")) {
                    userChoosenTask = "Select From Gallery";
                    boolean resultCam = Utility.checkPermissionGallery(getActivity());
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

        imageView.setImageBitmap(thumbnail);

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
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (IllegalAccessError e) {
            e.printStackTrace();
        }


    }


   /* private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console

            String key = "key=" + getResources().getString(R.string.browser_key);  //previous key
            //  String key = "key="+getResources().getString(R.string.browser_key);
            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // place searched by country
            String country = "components=country:in";

            // Sensor enabled
            String sensor = "sensor=true";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + country + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                System.out.println("URL  : " + url);
                // Fetching the data from we service
                data = downloadUrl(url);
               // System.out.println("DATA : " + data); //complete address of locations

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.trim().length() > 0) {
                // Creating ParserTask
                parserTask = new ParserTask(PLACES);

                // Starting Parsing the JSON string returned by Web Service
                parserTask.execute(result);
                if (AppConstant.APP_MODE != AppConstant.OFFLINE) {
                    if (selected == 1) {
                        location.dismissDropDown();
                    } else {
                        location.showDropDown();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Could not connect to the location API", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getPlaceDetailsUrl(String ref) {


        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=" + getResources().getString(R.string.browser_key);

        // reference of place
        String reference = "reference=" + ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
              //  Log.d("HomeActicity-------", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jobject = null;
            try {

                jobject = new JSONObject(result);
                JSONObject jsonObject = jobject.getJSONObject("result");
                JSONArray jsonArray = jsonObject.getJSONArray("address_components");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    JSONArray types = jObject.getJSONArray("types");
                    for (int k = 0; k < types.length(); k++) {
                        if (types.optString(k).contains("administrative_area_level_1")) {
                            selectedState= jObject.getString("long_name");
                            System.out.println("State Name---" + selectedState);
                            Log.v("selected location",jObject.toString()+"");
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    placesParserTask.execute(result);

                    if (selected == 1) {
                        location.dismissDropDown();
                    } else {
                        location.showDropDown();
                    }

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);

                    if (selected == 1) {
                        location.dismissDropDown();
                    } else {
                        location.showDropDown();
                    }

            }
        }
    }
*/
    /**
     * A class to parse the Google Places in JSON format
     */

  /*  ProgressDialog pDialog;

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected void onPreExecute() {
            if (parserType == PLACES_DETAILS) {
                pDialog = ProgressDialog.show(getActivity(), "",
                        "Please wait.....", true);
            }
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                        //we will pick here state

                        //     JSONObject  job = new JSONObject(jsonData[0]);


                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }


        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch (parserType) {
                case PLACES:
                    if (result != null) {
                        String[] from = new String[]{"description"};
                        int[] to = new int[]{android.R.id.text1};

                        // Creating a SimpleAdapter for the AutoCompleteTextView
                        SimpleAdapter adapter = new SimpleAdapter(getActivity(), result, android.R.layout.simple_list_item_1, from, to);

                        // Setting the adapter
                        location.setAdapter(adapter);

                        if (selected == 1) {
                            location.dismissDropDown();
                        } else {
                            location.showDropDown();
                        }

                    }
                    break;
                case PLACES_DETAILS:
                    if (pDialog != null) {
                        pDialog.cancel();
                    }
                    if (result != null) {

                        HashMap<String, String> hm = result.get(0);
                        // Getting latitude from the parsed data
                        latitude = Double.parseDouble(hm.get("lat"));
                        // Getting longitude from the parsed data
                        longitude = Double.parseDouble(hm.get("lng"));
                        addressLocation = location.getText().toString();
                        selected = 0;

                        Log.v("Place Detail Latitude",  latitude + " longitude : " + longitude+"lkl"+result.toString());

                        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
                            if (addresses.size() > 0)
                            {
                                locality = addresses.get(0).getLocality();
                                country = addresses.get(0).getCountryName();
                                zipCode = addresses.get(0).getPostalCode();
                                subLocality = addresses.get(0).getSubLocality();
                                district = addresses.get(0).getSubAdminArea();
                                lati = latitude+"";
                                longi = longitude+"";
                            }


                        }
                        catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }

                    break; //End of second case
            }
        }
    }

*/


    private void createNewLogStringToServer(String picName,String message){

        phone = AppConstant.mobile_no;
        name = AppConstant.visible_Name;

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.put("filename", picName);
            jsonObject.put("PhoneNumber", phone);
            jsonObject.put("state", selectedState);
            jsonObject.put("district", subLocality);
            jsonObject.put("locality", locality);
            jsonObject.put("country", country);
            jsonObject.put("zipcode", zipCode);
            jsonObject.put("lat", lati);
            jsonObject.put("lon", longi);
            jsonObject.put("Name", name);
            jsonObject.put("Crop", cropName);
            jsonObject.put("CropID", cropID);
            jsonObject.put("VisibleName", name);
            jsonObject.put("Message", message);
            jsonObject.put("VoiceFile",responsePath);

            Log.v("request",jsonObject.toString()+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(Request.Method.PUT, AppManager.getInstance().createNewLogURL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.cancel();
                Log.i("Response new log", "" + response.toString());
                getlogResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                Log.v("Response new log", "" + error.toString());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getlogResponse(JSONObject response) {

        uploadedPicName = null;

        if (response != null) {

            /*try {
               JSONObject jsonObject = new JSONObject();
                Intent intent = new Intent(getActivity(), LiveCottonActivity.class);
                startActivity(intent);
                getActivity().finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            Intent intent = new Intent(getActivity(), LiveCottonActivity.class);
            startActivity(intent);
            getActivity().finish();


        }

    }



    private void uploadImage() {

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                if (uploadedPicName!=null) {
                    createNewLogStringToServer(uploadedPicName,msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public boolean isValid() {

        boolean isValid = true;

         phone = AppConstant.mobile_no;
         name = AppConstant.visible_Name;

         msg = messageET.getText().toString().trim();
        if (imageString == null || imageString.length() < 7) {

            Toast.makeText(getActivity(), "Please click or choose image.", Toast.LENGTH_SHORT).show();
            return false;

        } else if (phone == null || phone.length() < 7) {

            Toast.makeText(getActivity(), "Please enter valid phone number.", Toast.LENGTH_SHORT).show();
            return false;

        } else if (name == null || name.length() < 2) {
            Toast.makeText(getActivity(), "Please enter valid name", Toast.LENGTH_SHORT).show();
            return false;

        } else if (msg == null || msg.length() < 1) {
            Toast.makeText(getActivity(), "Please enter message", Toast.LENGTH_SHORT).show();
            return false;

        }
        return isValid;
    }

    public void voicePopup(){

        sendFlag = null;
        final Dialog dialog = new Dialog(getActivity());

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
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                        if (sendFlag!=null) {
                            if (isNetworkAvailable()) {
                                sendData();
                            }else {
                                Toast.makeText(getActivity(),"No internet connections",Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            Toast.makeText(getActivity(),"Please record voice first",Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(getActivity(), "Recording started",
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

                Toast.makeText(getActivity(), "Recording Completed",
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
                Toast.makeText(getActivity(), "Recording Playing",
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
                }else {
                    responsePath = null;
                }
            }
            if (responsePath!=null){
                new Thread(new Runnable() {
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {



                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                builder.setTitle("Success").
                                        setMessage("Voice uploaded successfully.").
                                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                createNewLogStringToServer("","Voice_Message");
                                            }
                                        });

                                android.support.v7.app.AlertDialog dialog = builder.create();
                                dialog.show();
                                Log.v("jksajkjsk",responsePath+"");
                            }
                        });

                    }
                }).start();



            }


        } catch (Exception e) {
            // show error
        }
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
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
                        Toast.makeText(getActivity(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    public void getCropList(String ID) {
        final ProgressDialog cropDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Crops. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.myfarminfo.com/yfirest.svc/Clients/WWFJalna/Crops/"+ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cropDialog.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley State Response : " + response);

                        response = response.trim();
                  //      response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");

                        cropList = new ArrayList<CropBean>();




                        try{

                            JSONArray jb = new JSONArray(response);


                            for (int i = 0; i < jb.length(); i++) {

                                CropBean typeBean = new CropBean();
                                typeBean.setName(jb.getJSONObject(i).getString("Name"));
                                typeBean.setId(jb.getJSONObject(i).getString("ID"));
                                cropList.add(typeBean);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                        String[] cropArray = new String[cropList.size()];
                        for (int i = 0; i < cropList.size(); i++) {
                            cropArray[i] = cropList.get(i).getName();
                        }

                        ArrayAdapter<String> farmerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cropArray);
                        farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        cropSpinner.setAdapter(farmerAdapter);

                        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.v("cropName",cropList.get(position).getName()+"--"+cropList.get(position).getId());
                                if (cropList.get(position).getName().equalsIgnoreCase(null)){
                                    cropID = "12";
                                    cropName = "Cotton";
                                }else {
                                    cropID = cropList.get(position).getId();
                                    cropName = cropList.get(position).getName();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cropDialog.cancel();
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