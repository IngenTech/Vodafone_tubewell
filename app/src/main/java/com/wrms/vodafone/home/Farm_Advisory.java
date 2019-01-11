package com.wrms.vodafone.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.CustomAdapter;
import com.wrms.vodafone.entities.FarmAdvisoryDataSet;
import com.wrms.vodafone.utils.AppConstant;

import java.util.ArrayList;

/**
 * Created by Yogendra Singh on 16-10-2015.
 */
public class Farm_Advisory extends AppCompatActivity {
    private Toolbar toolbar;
    String N;
    String P;
    String K;
    String sowingFrom;
    String sowingTo;
   // ArrayList<FarmAdvisoryDataSet> dataSet;
    ListView listView;
    String parcelableValue;
    EditText appliedBaselDoseN;
    EditText appliedBaselDoseP;
    EditText appliedBaselDoseK;

    EditText idealBaselDoseN;
    EditText idealBaselDoseP;
    EditText idealBaselDoseK;
    TextView idealCropDuration;

    String a_valueN;
    ArrayList<FarmAdvisoryDataSet> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.farm_advisory);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        listView = (ListView)findViewById(R.id.listView);
        appliedBaselDoseN = (EditText)findViewById(R.id.appliedBaselDoseN);
        appliedBaselDoseP = (EditText)findViewById(R.id.appliedBaselDoseP);
        appliedBaselDoseK = (EditText)findViewById(R.id.appliedBaselDoseK);

        idealBaselDoseN = (EditText)findViewById(R.id.idealBaselDoseN);
        idealBaselDoseP = (EditText)findViewById(R.id.idealBaselDoseP);
        idealBaselDoseK = (EditText)findViewById(R.id.idealBaselDoseK);
        idealCropDuration = (TextView)findViewById(R.id.textView);
        TextView farmInfo = (TextView) findViewById(R.id.logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Cotton Doctor");
        farmInfo.setTextColor(Color.WHITE);
        Intent intent = getIntent();
        dataSet = intent.getParcelableArrayListExtra(AppConstant.DATA_SET);
        /*if(tempDataSet!=null) {
            for (Parcelable data: tempDataSet){
                FarmAdvisoryDataSet farmData = (FarmAdvisoryDataSet)data;
                dataSet.add(farmData);
            }
        }*/
         // ArrayList<FarmAdvisoryDataSet> list = intent.getExtras().getParcelableArrayList(AppConstant.DATA_SET);
        String a_valueN = intent.getStringExtra(AppConstant.APPLIED_VALUE_OF_N);
        String a_valueP = intent.getStringExtra(AppConstant.APPLIED_VALUE_OF_P);
        String a_valueK = intent.getStringExtra(AppConstant.APPLIED_VALUE_OF_K);

        String i_valueN = intent.getStringExtra(AppConstant.IDEAL_VALUE_OF_N);
        String i_valueP = intent.getStringExtra(AppConstant.IDEAL_VALUE_OF_P);
        String i_valueK = intent.getStringExtra(AppConstant.IDEAL_VALUE_OF_K);
        String sowingFrom = intent.getStringExtra(AppConstant.SOWING_DATE_FROM);
        String sowingTo = intent.getStringExtra(AppConstant.SOWING_DATE_TO);

       if(AppConstant.SOWING_DATE_FROM!=null && AppConstant.SOWING_DATE_TO!=null){
           if(sowingFrom.isEmpty())
               sowingFrom="-";
            if(sowingTo.isEmpty())
                sowingTo="-";
            idealCropDuration.setText("You should sow your crop between "+sowingFrom+" and "+sowingTo+" to maximize yield");
        }
        appliedBaselDoseN.setText(a_valueN.isEmpty()?"0":a_valueN);
        appliedBaselDoseP.setText(a_valueP.isEmpty()?"0":a_valueP);
        appliedBaselDoseK.setText(a_valueK.isEmpty()?"0":a_valueK);
        idealBaselDoseN.setText(i_valueN);
        idealBaselDoseP.setText(i_valueP);
        idealBaselDoseK.setText(i_valueK);
        listView.setAdapter(new CustomAdapter(this, dataSet));
        Log.d("Reahed setAdapter","listView.setAdapter(new CustomAdapter(this, dataSet))");

    }
}
