package com.wrms.vodafone.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.CropQueryData;
import com.wrms.vodafone.entities.FarmInformationData;
import com.wrms.vodafone.entities.Register;
import com.wrms.vodafone.mapfragments.LatLonCellID;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.utils.ConnectionDetector;
import com.wrms.vodafone.utils.CustomHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import static com.wrms.vodafone.utils.AppConstant.isLogin;

public class SignUpActivity extends AppCompatActivity {

    private Toolbar toolbar;
 //   EditText create_username;
    EditText visible_namename;
    EditText password;
    EditText confirm_password;
    EditText email_address;
    EditText mobileNo;
    EditText city;
    Spinner state;
    Button signIn;
    Button signUp;
    Button submit;
    Register register;
    String creatString;
    SharedPreferences pref;
    DBAdapter db;

    AutoCompleteTextView villageName;
    EditText fatherName;
    EditText revenueNo;
    EditText gutNo;
    RadioGroup radioGroup;
    RadioButton androidType, nonAndroidType;

    String villageID = null;
    String checkType = "android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setBackgroundResource(R.color.heading_color);
        final TextView farmInfo = (TextView) findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Register");
        farmInfo.setTextColor(Color.WHITE);
     //   create_username = (EditText) findViewById(R.id.create_username);
        visible_namename = (EditText) findViewById(R.id.visible_namename);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        email_address = (EditText) findViewById(R.id.email_address);
        mobileNo = (EditText) findViewById(R.id.mobile_number);
        submit = (Button) findViewById(R.id.submit);
        // signIn = (Button)findViewById(R.id.sign_in);
        db = new DBAdapter(SignUpActivity.this);



        villageName = (AutoCompleteTextView) findViewById(R.id.village_name);
        fatherName = (EditText) findViewById(R.id.father_name);
        revenueNo = (EditText) findViewById(R.id.revenue_no);
        gutNo = (EditText) findViewById(R.id.gut_no);
        radioGroup = (RadioGroup) findViewById(R.id.mobile_type_radio_group);
        androidType = (RadioButton) findViewById(R.id.android_type);
        nonAndroidType = (RadioButton) findViewById(R.id.non_android_type);

        checkType = "android";

        androidType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nonAndroidType.setChecked(false);
                    androidType.setChecked(true);
                    checkType = "android";
                }
            }
        });

        nonAndroidType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nonAndroidType.setChecked(true);
                    androidType.setChecked(false);
                    checkType = "non android";
                }
            }
        });

        db.open();
        ArrayList<String> villageList = new ArrayList<>();
        final ArrayList<String> villageIdList = new ArrayList<>();
        final Cursor villageCursor = db.getAllVillages();

        if (villageCursor.getCount() > 0) {
            villageCursor.moveToFirst();
            do {
                villageList.add(villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE_NAME)));
                villageIdList.add(villageCursor.getString(villageCursor.getColumnIndex(DBAdapter.VILLAGE_ID)));
            } while (villageCursor.moveToNext());
        }else {

            ConnectionDetector con = new ConnectionDetector(this);
            if (con.isConnectingToInternet()) {
                loadVillageData();
            }
        }
        ArrayAdapter<String> stateListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, villageList);
       // stateListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageName.setAdapter(stateListAdapter);

        villageName.setThreshold(1);
        villageName.setTextColor(Color.BLACK);
      //  db.close();


        villageName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    String ss = String.valueOf(parent.getItemAtPosition(position));
                    db.open();
                    final Cursor villageCursor1 = db.getVillageIdByName(ss);

                    if (villageCursor1.getCount() > 0) {
                        villageCursor1.moveToFirst();
                        do {
                            villageID = villageCursor1.getString(villageCursor1.getColumnIndex(DBAdapter.VILLAGE_ID));

                        } while (villageCursor1.moveToNext());
                    }

                } else {
                    villageID = null;
                }

                Log.v("villageiDD", villageID + "");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidate())
                    return;


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                register = new Register();
                register.setUserName(mobileNo.getText().toString());
                register.setVisibleName(visible_namename.getText().toString());
                register.setPassword(password.getText().toString());
                if (email_address.getText().toString()!=null && email_address.getText().toString().length()>8) {
                    register.setMailId(email_address.getText().toString());
                }else {
                    register.setMailId(mobileNo.getText().toString());
                }
                Date date = new Date();
                register.setCreatedDateTime(sdf.format(date));

                String userId = String.valueOf(System.currentTimeMillis() / 1000);
                register.setUser_id(userId);
                creatString = creatStringForRegister();

                Log.d("creatstring", creatString);

                if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
                    db.open();
                    boolean isSaved = register.save(db, DBAdapter.SAVE);
                    if (!isSaved) {
                        Toast.makeText(SignUpActivity.this, "Email address already exist. Please try another email or login with this Id", Toast.LENGTH_LONG).show();
                        return;
                    }
                    db.close();
                    if (pref == null) {
                        pref = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    System.out.println("Saved User id : " + userId);
                    editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, userId);
                    editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, register.getVisibleName());
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, true);
                    editor.putString(AppConstant.PREFRENCE_KEY_EMAIL, register.getMailId());
                    editor.putString(AppConstant.PREFRENCE_KEY_PASS, register.getPassword());
                    editor.putBoolean(AppConstant.PREFRENCE_KEY_ISSAVED, true);
                    editor.putString("villageName", AppConstant.villageName);
                    editor.putString("villageId", villageID);
                    editor.commit();
                    AppConstant.isLogin = true;
                    AppConstant.user_id = userId;
                    finish();
                    Intent intent = new Intent(SignUpActivity.this, AddFarmWithoutMap.class);
                    intent.putExtra("calling-activity", AppConstant.HomeActivity);
                    intent.putExtra("lat", String.valueOf(LatLonCellID.currentLat));
                    intent.putExtra("log", String.valueOf(LatLonCellID.currentLon));
                    startActivity(intent);
                }/* else if (AppManager.isOnline(SignUpActivity.this)) {
                    new syncFarRegister(creatString).execute();

                } */ else {
                    new syncFarRegister(creatString).execute();
//                    Toast.makeText(getBaseContext(), "Network not available", Toast.LENGTH_LONG).show();
                }


            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       /* confirm_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence string, int start, int before, int count) {
                String value = string.toString();

                String passwordString = password.getText().toString();
                if (passwordString.length() > 0) {
                    if (value.length() > 0) {
                        if (!passwordString.contains(value)) {
                            Toast.makeText(SignUpActivity.this, "Password do not match. Please try again.", Toast.LENGTH_LONG).show();
                        } else {

                        }
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Please Enter the password.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });*/
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        signUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isValid()) {
//                    sentRegistration();
//                }
//
//            }
//        });

        // Button action_signin = (Button)findViewById(R.id.sign_in);
//        action_signin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//            }
//        });

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class syncFarRegister extends AsyncTask<Void, Void, String> {


        String result = null;
        String createdString;

        public syncFarRegister(String createdString) {


            this.createdString = createdString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.show(SignUpActivity.this, "", "Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... params) {

            result = syncForRegistration(AppManager.getInstance().removeSpaceForUrl(createdString), register);
            Log.d("result-----", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPreExecute();
            if (pref == null) {
                pref = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
            }
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, register.getUser_id());
            editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, register.getVisibleName());
            editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, true);
            editor.putString(AppConstant.PREFRENCE_KEY_EMAIL, register.getMailId());
            editor.putString(AppConstant.PREFRENCE_KEY_PASS, register.getPassword());
            editor.putBoolean(AppConstant.PREFRENCE_KEY_ISSAVED, true);
            AppConstant.mobile_no = mobileNo.getText().toString();
            editor.putString(AppConstant.PREFRENCE_KEY_MOBILE, AppConstant.mobile_no);


           /* Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
            intent.putExtra(AppConstant.REGISTER_USER, register);
*/
            if (result != null) {
                if (result.contains("OK")) {

                    editor.commit();
                    AppConstant.isLogin = true;
                    finish();
                    db.open();
                    register.save(db, DBAdapter.SENT);
                    db.close();

                } else if (result.contains("Error")) {
                    Toast.makeText(SignUpActivity.this, "UserExist/EmailExist Try Again", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(SignUpActivity.this, "Could not connect to server\nUser has been saved", Toast.LENGTH_LONG).show();
                    db.open();
                    register.save(db, DBAdapter.SAVE);
                    db.close();
                    editor.commit();
                    AppConstant.isLogin = true;
                    AppConstant.user_id = register.getUser_id();
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Could not connect to server\nUser has been saved", Toast.LENGTH_LONG).show();
                db.open();
                register.save(db, DBAdapter.SAVE);
                db.close();
                editor.commit();
                AppConstant.isLogin = true;
                AppConstant.user_id = register.getUser_id();
            }
//            progressDialog.dismiss();
            //  SignUpActivity.this.finish();
            //  startActivity(intent);


            SharedPreferences.Editor editor1 = pref.edit();
            AppConstant.isLogin = false;
            editor1.putString(AppConstant.PREFRENCE_KEY_USER_ID, "");
            editor1.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, "");
            editor1.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
            editor1.commit();


            Intent in = new Intent(getApplicationContext(), HomeActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            finish();


        }


    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String creatStringForRegister() {
        String str = "";
        AppConstant.villageName = villageName.getText().toString().trim();
        AppConstant.villageID = villageID;
        AppConstant.revenueNo = revenueNo.getText().toString().trim();
        AppConstant.gutNo = gutNo.getText().toString().trim();
        AppConstant.fatherName = fatherName.getText().toString().trim();

/*

        str = register.getMailId() + "/" +
                register.getUserName() + "/" +
                register.getPassword() + "/" +
                register.getVisibleName() + "/" +
                mobileNo.getText().toString().trim() + "/" +
                villageID + "/" +
                fatherName.getText().toString().trim() + "/" +
                revenueNo.getText().toString().trim() + "/" +
                gutNo.getText().toString().trim() + "/" +
                checkType;
*/

        String parameterString = "";

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("EMailID", register.getMailId());
            jsonObject.put("UserName", register.getUserName());
            jsonObject.put("Password", register.getPassword());
            jsonObject.put("VisibleName", register.getVisibleName());
            jsonObject.put("PhoneNo", mobileNo.getText().toString().trim());
            jsonObject.put("VillageID", villageID);
            jsonObject.put("FatherName", fatherName.getText().toString().trim() );
            jsonObject.put("RevenueNo", revenueNo.getText().toString().trim());
            jsonObject.put("GutNo", gutNo.getText().toString().trim() );
            jsonObject.put("MobileType", checkType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        parameterString = jsonObject.toString();

        Log.v("registerString",parameterString+"");

        return parameterString;
    }



    public static String syncForRegistration(String data, Register register) {
        String completeStringForRegister = "";
        String message = "";
        completeStringForRegister = data;
        String response = null;
        try {


                 //   response = CustomHttpClient.executeHttpPut("http://myfarminfo.com/yfirest.svc/registerUser_Jalna/" + completeStringForRegister);
            response = AppManager.getInstance().httpRequestPutMethod("http://myfarminfo.com/yfirest.svc/registerUser_Jalna", completeStringForRegister);

            Log.d("RegistrationData", response);
            if (response.contains("Error")) {
                String s = "Error";
                return s;
            }
            response = response.trim();


//            response "[{\"UserID\":100055,\"VisibleName\":\"jyfjyd\",\"UserSince\":\"September 2015\"}]"
            response = response.replace("\"{", "{");
            response = response.replace("}\"", "}");
            response = response.replace("\"[", "[");
            response = response.replace("]\"", "]");
            response = response.replace("\\", "");
            System.out.println("response_register " + response);
            JSONObject jb = new JSONObject(response.toString());
            JSONArray jArray =jb.getJSONArray("registerUser_JalnaResult");


            if (jArray.length() == 0) {
                message = "No Registered";
                return message;
            }
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                System.out.println("UserID " + jObject.getString("UserID"));
                AppConstant.user_id = jObject.getString("UserID");
                register.setUser_id(jObject.getString("UserID"));
                String s = AppConstant.user_id;
                System.out.println("appconstant " + s);
                System.out.println("VisibleName " + jObject.getString("VisibleName"));
                AppConstant.visible_Name = jObject.getString("VisibleName");
                register.setVisibleName(jObject.getString("VisibleName"));
                String s1 = AppConstant.user_id;
                System.out.println("appconstant " + s1);
                message = "OK";
            }
            return message;
        } catch (Exception e) {
//
            e.printStackTrace();
            Log.d("Status", "" + e);

            return "NoResponse";
        }

    }

    ProgressDialog dialoug;

    public void loadVillageData() {
        dialoug = ProgressDialog.show(SignUpActivity.this, "",
                "Loading Villages. Please wait...", true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://myfarminfo.com/yfirest.svc/JalnaVillages",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialoug.cancel();
                        // Display the first 500 characters of the response string.
                        System.out.println("Volley State Response : " + response);

                        response = response.trim();
                     //   response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        db.open();
                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                        SqliteDB.beginTransaction();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                db.db.execSQL("delete from " + DBAdapter.TABLE_VILLAGE);
                            }

                            String query = "INSERT INTO " + DBAdapter.TABLE_VILLAGE + "(" + DBAdapter.VILLAGE_ID + "," + DBAdapter.VILLAGE_NAME + ") VALUES (?,?)";
                            SQLiteStatement stmt = SqliteDB.compileStatement(query);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject territoryElementArray = jsonArray.getJSONObject(i);


                                stmt.bindString(1, territoryElementArray.getString("Id"));
                                stmt.bindString(2, territoryElementArray.getString("Village_Final"));
                                stmt.execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this, "Response Formatting Error", Toast.LENGTH_LONG).show();
                        }
                        SqliteDB.setTransactionSuccessful();
                        SqliteDB.endTransaction();
                        db.getClass();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialoug.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private boolean isValidate() {

        if (mobileNo.getText().toString().length() > 9) {

            if (visible_namename.getText().toString().length() > 0) {

                if (fatherName.getText().toString().length() > 0) {

                    if (villageID != null) {

                        if (mobileNo.getText().toString().length() > 9) {

                            if (revenueNo.getText().toString().length() > 0) {

                                if (gutNo.getText().toString().length() > 0) {

                                    if (password.getText().toString().length() > 3) {

                                        if (confirm_password.getText().toString().length() > 3) {

                                            if (password.getText().toString().trim().equalsIgnoreCase(confirm_password.getText().toString().trim())) {

                                                return true;

                                            } else {
                                                Toast.makeText(getBaseContext(), "Password and confirm password must be same", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), "Confirm Password atleast 4 characters", Toast.LENGTH_LONG).show();

                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Password  atleast 4 characters", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please enter gut no.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter revenue no.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getBaseContext(), "Mobile no must be of 10 digits", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Please Choose Village", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Enter Father name", Toast.LENGTH_LONG).show();

                }

            } else {
                Toast.makeText(getBaseContext(), "Please Enter Farmer name", Toast.LENGTH_LONG).show();

            }

        } else {
            Toast.makeText(getBaseContext(), " Mobile no must be of 10 digits", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    private boolean isValid() {

        if (mobileNo.getText().toString().trim().length() > 0) {
            register.setUserName(mobileNo.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter user name", Toast.LENGTH_LONG).show();
            return false;
        }


        if (visible_namename.getText().toString().trim().length() > 0) {
            register.setVisibleName(visible_namename.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter visible name", Toast.LENGTH_LONG).show();

            return false;
        }

        if (password.getText().toString().trim().length() > 0) {
            register.setPassword(password.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
            return false;
        }


        if (confirm_password.getText().toString().trim().length() > 0) {
//            data.setPassword(confirm_password.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter confirm password", Toast.LENGTH_LONG).show();
            return false;
        }

        if (email_address.getText().toString().trim().length() > 0) {
            register.setMailId(email_address.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter email address", Toast.LENGTH_LONG).show();
            return false;
        }


        if (city.getText().toString().trim().length() > 0) {
//            data.setCity(city.getText().toString());
        } else {
            Toast.makeText(SignUpActivity.this, "Please enter city", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_signup) {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


}
