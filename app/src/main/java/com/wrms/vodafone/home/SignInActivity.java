package com.wrms.vodafone.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.vodafone.R;
import com.wrms.vodafone.entities.SignInData;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.CustomHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {


    private Toolbar toolbar;
    EditText username;
    EditText password;
    Button signIn;
    private SignInData data;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setBackgroundResource(R.color.heading_color);
        TextView farmInfo = (TextView)findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("My Farm Info");

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        signIn = (Button)findViewById(R.id.sign_in);

        data = new SignInData();

        Button signup = (Button)findViewById(R.id.sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    signIn(data);
                }
            }
        });

    }

    private void signIn(final SignInData data){

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                String message = "";

                String response = null;
                try {

                    response = CustomHttpClient.executeHttpGet("http://myfarminfo.com/yfirest.svc/Login/" + data.getUserName() + "/" + data.getPassword());
                    System.out.println("SignInResponse " + response);
                    Log.v("SignInResponse",response);

                    response = response.trim();
                 //   response = response.substring(1, response.length() - 1);
                    response = response.replace("\\", "");
                    response = response.replace("\\", "");
                    response = response.replace("\"{", "{");
                    response = response.replace("}\"", "}");
                    response = response.replace("\"[", "[");
                    response = response.replace("]\"", "]");

                    System.out.println("SignInResponse " + response);


                    JSONArray jArray = new JSONArray(response);

                    if (jArray.length() == 0) {
                        message = "Not Registered";
//                        return message;
                    }
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        System.out.println("UserID " + jObject.getInt("UserID"));
                        AppConstant.user_id = jObject.getString("UserID");
                        System.out.println("VisibleName " + jObject.getString("VisibleName"));
                        AppConstant.visible_Name = jObject.getString("VisibleName");
                        AppConstant.mobile_no = jObject.getString("PhNo");
                        AppConstant.role = jObject.getString("Role");
                        System.out.println("UserSince " + jObject.getString("UserSince"));

                        if (prefs == null) {
                            prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(AppConstant.PREFRENCE_KEY_MOBILE, AppConstant.mobile_no);
                        editor.commit();
                    }

                    AppConstant.isLogin=true;
                // fetch all the data from the service and insert into database
                    Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();


                }
                return null;
            }


        }.execute();

    }

    private boolean isValid(){

        if(username.getText().toString().trim().length()>0){
            data.setUserName(username.getText().toString());
        }else{
            Toast.makeText(SignInActivity.this, "Please enter user name", Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.getText().toString().trim().length()>0){
            data.setPassword(password.getText().toString());
        }else{
            Toast.makeText(SignInActivity.this,"Please enter password",Toast.LENGTH_LONG).show();;
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_signup) {

            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_in);

            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



}
