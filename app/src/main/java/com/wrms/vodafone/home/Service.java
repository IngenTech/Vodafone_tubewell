package com.wrms.vodafone.home;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.wrms.vodafone.R;


/**
 * Created by piyush on 10/1/2015.
 */
public class Service extends Activity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setBackgroundResource(R.color.heading_color);
        TextView farmInfo = (TextView)findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Cotton Doctor");
        farmInfo.setTextColor(Color.WHITE);

    }
}
