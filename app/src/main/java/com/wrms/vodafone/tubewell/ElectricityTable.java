package com.wrms.vodafone.tubewell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.ShowPieDataAdapter;
import com.wrms.vodafone.bean.ElectricStart;
import com.wrms.vodafone.bean.ElectricStatusBean;
import com.wrms.vodafone.bean.ElectricStop;
import com.wrms.vodafone.bean.Max1Bean;
import com.wrms.vodafone.bean.Max2Bean;
import com.wrms.vodafone.bean.Max3Bean;
import com.wrms.vodafone.bean.MaxCur1;
import com.wrms.vodafone.bean.MaxCur2;
import com.wrms.vodafone.bean.MaxCur3;
import com.wrms.vodafone.bean.Min1Bean;
import com.wrms.vodafone.bean.Min2Bean;
import com.wrms.vodafone.bean.Min3Bean;
import com.wrms.vodafone.bean.MotorStartBean;
import com.wrms.vodafone.bean.MotorStatus;
import com.wrms.vodafone.bean.MotorStopBean;
import com.wrms.vodafone.bean.MotorStopSatus;

import java.util.ArrayList;

/**
 * Created by Admin on 10-04-2018.
 */
public class ElectricityTable extends Fragment {


    ArrayList<ElectricStart> arrayStartList = new ArrayList<ElectricStart>();
    ArrayList<ElectricStop> arrayStopList = new ArrayList<ElectricStop>();

    public ElectricityTable( ArrayList<ElectricStart> list15, ArrayList<ElectricStop> list16) {

        arrayStartList = list15;
        arrayStopList = list16;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.pie_popup, container, false);

        RecyclerView listView = (RecyclerView) view.findViewById(R.id.pieListValue);
        listView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(linearLayoutManager);

        TextView noData = (TextView) view.findViewById(R.id.no_data);

        if (arrayStartList!=null || arrayStartList.size() > 0) {

            noData.setVisibility(View.GONE);




            ShowPieDataAdapter adapter = new ShowPieDataAdapter(getActivity(),arrayStartList,arrayStopList);
            listView.setAdapter(adapter);


        } else {

            noData.setVisibility(View.VISIBLE);
        }


        return view;

    }
}

