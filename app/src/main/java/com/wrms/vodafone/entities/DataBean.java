package com.wrms.vodafone.entities;

import com.wrms.vodafone.bean.VodafoneBean;

import java.util.ArrayList;

/**
 * Created by Admin on 25-07-2017.
 */
public class DataBean  {

    public ArrayList<VillageBean> villageList;
    public ArrayList<VodafoneBean> vodaList;







    public ArrayList<VillageBean> getCityList() {
        return villageList;
    }

    public void setCityList(ArrayList<VillageBean> cuisineList) {
        this.villageList = cuisineList;
    }


    public ArrayList<VodafoneBean> getVodaList() {
        return vodaList;
    }

    public void setVodaList(ArrayList<VodafoneBean> cuisineList) {
        this.vodaList = cuisineList;
    }


}