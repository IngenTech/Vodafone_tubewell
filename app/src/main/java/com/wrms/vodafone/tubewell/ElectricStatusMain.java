package com.wrms.vodafone.tubewell;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.wrms.vodafone.R;
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
import com.wrms.vodafone.mapfragments.ElecStatusFrag;
import com.wrms.vodafone.mapfragments.StartStopFrag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10-04-2018.
 */
public class ElectricStatusMain extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    Button nextBTN;

    ArrayList<Max1Bean> arrayListMax1 = new ArrayList<Max1Bean>();
    ArrayList<Max2Bean> arrayListMax2 = new ArrayList<Max2Bean>();
    ArrayList<Max3Bean> arrayListMax3 = new ArrayList<Max3Bean>();
    ArrayList<Min1Bean> arrayListMin1 = new ArrayList<Min1Bean>();
    ArrayList<Min2Bean> arrayListMin2 = new ArrayList<Min2Bean>();
    ArrayList<Min3Bean> arrayListMin3 = new ArrayList<Min3Bean>();

    ArrayList<MaxCur1> arrayListMaxCur1 = new ArrayList<MaxCur1>();
    ArrayList<MaxCur2> arrayListMaxCur2 = new ArrayList<MaxCur2>();
    ArrayList<MaxCur3> arrayListMaxCur3 = new ArrayList<MaxCur3>();

    ArrayList<MotorStartBean> arrayListMotorStart = new ArrayList<MotorStartBean>();

    ArrayList<ElectricStatusBean> arrayListElectric = new ArrayList<ElectricStatusBean>();

    ArrayList<ElectricStart> electricityStartStatusList = new ArrayList<ElectricStart>();
    ArrayList<ElectricStop> electricityStopStatusList = new ArrayList<ElectricStop>();

    ArrayList<MotorStatus> arrayStartList = new ArrayList<MotorStatus>();
    ArrayList<MotorStopSatus> arrayStopList = new ArrayList<MotorStopSatus>();
    String statusType = null;

    ElectricityTable fragmentOne;
    ElecStatusFrag fragmentTwo;
    ElectricityPieFrag fragmentThree;
    String e_on = "0";
    String e_off = "100";
    String m_on = "0";
    String m_off = "100";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.electric_status_main);
        ImageView backBTN = (ImageView) findViewById(R.id.backBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        arrayListMax1 = (ArrayList<Max1Bean>) getIntent().getSerializableExtra("list1");
        arrayListMax2 = (ArrayList<Max2Bean>) getIntent().getSerializableExtra("list2");
        arrayListMax3 = (ArrayList<Max3Bean>) getIntent().getSerializableExtra("list3");
        arrayListMin1 = (ArrayList<Min1Bean>) getIntent().getSerializableExtra("list4");

        arrayListMin2 = (ArrayList<Min2Bean>) getIntent().getSerializableExtra("list5");
        arrayListMin3 = (ArrayList<Min3Bean>) getIntent().getSerializableExtra("list6");

        arrayListMaxCur1 = (ArrayList<MaxCur1>) getIntent().getSerializableExtra("list7");
        arrayListMaxCur2 = (ArrayList<MaxCur2>) getIntent().getSerializableExtra("list8");
        arrayListMaxCur3 = (ArrayList<MaxCur3>) getIntent().getSerializableExtra("list9");
        arrayListMotorStart = (ArrayList<MotorStartBean>) getIntent().getSerializableExtra("list10");
        arrayListElectric = (ArrayList<ElectricStatusBean>) getIntent().getSerializableExtra("list11");
        electricityStartStatusList = (ArrayList<ElectricStart>) getIntent().getSerializableExtra("list12");
        electricityStopStatusList = (ArrayList<ElectricStop>) getIntent().getSerializableExtra("list13");
        arrayStartList = (ArrayList<MotorStatus>) getIntent().getSerializableExtra("list14");
        arrayStopList = (ArrayList<MotorStopSatus>) getIntent().getSerializableExtra("list15");
        statusType = getIntent().getStringExtra("status");
        e_on = getIntent().getStringExtra("e_on");
        e_off = getIntent().getStringExtra("e_off");

        m_on = getIntent().getStringExtra("m_on");
        m_off = getIntent().getStringExtra("m_off");

        if (m_on != null) {
            String[] splited = m_on.split("\\s+");
            if (splited.length > 0) {
                m_on = splited[0];
            }
        } else {
            m_on = "100";
        }

        if (m_off != null) {
            String[] splited = m_off.split("\\s+");
            if (splited.length > 0) {
                m_off = splited[0];
            }
        } else {
            m_off = "100";
        }

        if (e_on!=null){
            String[] splited = e_on.split("\\s+");
            if (splited.length>0){
                e_on = splited[0];
            }
        }else {
            e_on = "100";
        }

        if (e_off!=null){
            String[] splited = e_off.split("\\s+");
            if (splited.length>0){
                e_off = splited[0];
            }
        }else {
            e_off = "100";
        }


        Log.v("el_on",e_on);
        Log.v("el_off",e_off);


        nextBTN = (Button) findViewById(R.id.nextBTN);

        if (statusType != null && statusType.equalsIgnoreCase("0")) {

            nextBTN.setVisibility(View.GONE);
        } else {
            nextBTN.setVisibility(View.VISIBLE);
        }

        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(), StartStopMain.class);
                in.putExtra("list1", arrayListMax1);
                in.putExtra("list2", arrayListMax2);
                in.putExtra("list3", arrayListMax3);
                in.putExtra("list4", arrayListMin1);
                in.putExtra("list5", arrayListMin2);
                in.putExtra("list6", arrayListMin3);
                in.putExtra("list7", arrayListMaxCur1);
                in.putExtra("list8", arrayListMaxCur2);
                in.putExtra("list9", arrayListMaxCur3);
                in.putExtra("list10", arrayListMotorStart);
                in.putExtra("list11", arrayStartList);
                in.putExtra("list12", arrayStopList);
                in.putExtra("m_on",m_on);
                in.putExtra("m_off",m_off);

                in.putExtra("status", "1");
                startActivity(in);

            }
        });


     /*   viewPager = (ViewPager)findViewById(R.id.pager);
        setupViewPager(viewPager);*/

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // tabLayout.setupWithViewPager(viewPager);

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setSize(2, 1);
        linearLayout.setDividerPadding(0);
        linearLayout.setDividerDrawable(drawable);

        bindWidgetsWithAnEvent();
        setupTabLayout();

    }

    private void setupTabLayout() {
        fragmentOne = new ElectricityTable(electricityStartStatusList, electricityStopStatusList);
        fragmentTwo = new ElecStatusFrag(arrayListElectric);
        fragmentThree= new ElectricityPieFrag(e_on,e_off);
        tabLayout.addTab(tabLayout.newTab().setText("Table View"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Line Chart"));
        tabLayout.addTab(tabLayout.newTab().setText("Pie Chart"));
    }

    private void bindWidgetsWithAnEvent() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentOne);
                break;
            case 1:
                replaceFragment(fragmentTwo);
                break;
            case 2:
                replaceFragment(fragmentThree);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new ElectricityTable(electricityStartStatusList, electricityStopStatusList), "TableView");
        adapter.addFragment(new ElecStatusFrag(arrayListElectric), "LineChart");
        if (e_on == null) {
            e_on = "0";
            e_off = "100";
        }
        adapter.addFragment(new ElectricityPieFrag(e_on, e_off), "Pie Chart");


        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
