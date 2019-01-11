package com.wrms.vodafone.utils;

import java.util.HashMap;

/**
 * Created by Yogendra Singh on 07-11-2015.
 */
public class StateName  {

    HashMap<String, String> stateName;



    public  StateName()
    {
        stateName = new HashMap<String, String>();
        stateName.put("Bihar","5");
        stateName.put("Uttar Pradesh","6");
        stateName.put("Rajasthan","7");


    }
    public String getStateID(String stateName){

        return this.stateName.get(stateName);

    }










}
