package com.wrms.vodafone.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.vodafone.R;
import com.wrms.vodafone.entities.FeedBackData;

/**
 * Created by piyush on 10/1/2015.
 */
public class Contact extends AppCompatActivity {

    EditText your_name;
    EditText your_mail;
    EditText your_phoneNo;
    EditText your_Msg;
     Button sendMessage;
    FeedBackData feedBackData;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView farmInfo = (TextView) findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Cotton Doctor");
        farmInfo.setTextColor(Color.WHITE);

        your_name = (EditText) findViewById(R.id.yourName);
        your_mail = (EditText) findViewById(R.id.yourEmale);
        your_phoneNo = (EditText) findViewById(R.id.yourPhone);
        your_Msg = (EditText) findViewById(R.id.yourMessage);
        sendMessage = (Button)findViewById(R.id.sendMessage);

        feedBackData = new FeedBackData();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {

                    feedBackData.setYourName(your_name.getText().toString());
                    feedBackData.setYourEmail(your_mail.getText().toString());
                    feedBackData.setYourPhoneNo(your_phoneNo.getText().toString());
                    feedBackData.setYourMessage(your_Msg.getText().toString());
                    sentFeedBack();
                }

            }
        });


    }
    private boolean isValid(){

        if(your_name.getText().toString().trim().length()>0){
           // data.setCreateUsername(create_username.getText().toString());

        }else{
            Toast.makeText(Contact.this, "Please enter your Name", Toast.LENGTH_LONG).show();
            return false;
        }


        if(your_mail.getText().toString().trim().length()>0){

        }else{
            Toast.makeText(Contact.this,"Please enter Email",Toast.LENGTH_LONG).show();;
            return false;
        }

        if(your_phoneNo.getText().toString().trim().length()>0){
           // data.setPassword(password.getText().toString());
        }else{
            Toast.makeText(Contact.this,"Please enter phone No",Toast.LENGTH_LONG).show();;
            return false;
        }


        if(sendMessage.getText().toString().trim().length()>0){
//            data.setPassword(confirm_password.getText().toString());
        }else{
            Toast.makeText(Contact.this,"Please enter confirm password",Toast.LENGTH_LONG).show();;
            return false;
        }


        return true;
    }
public void  sentFeedBack() {
    new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {

            FeedBackData.sentFeedBackData(feedBackData);

//                RegistrationData.getJsonData("", data);

            return null;
        }

    }.execute();
}



}
