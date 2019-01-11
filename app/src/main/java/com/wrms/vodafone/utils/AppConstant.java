package com.wrms.vodafone.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 *
 */
public class AppConstant {

    public static final String SHARED_PREFRENCE_NAME = "jalna";
    public static final String PREFRENCE_KEY_EMAIL = "pref_key_email";
    public static final String PREFRENCE_KEY_PASS = "pref_key_pass";
    public static final String PREFRENCE_KEY_ISSAVED = "pref_key_issaved";
    public static final String PREFRENCE_KEY_ISLOGIN = "pref_key_islogin";
    public static final String PREFRENCE_KEY_USER_ID = "pref_key_user_id";
    public static final String PREFRENCE_KEY_MOBILE = "mobile";
    public static final String PREFRENCE_KEY_VISIBLE_NAME = "pref_key_visible_name";
    public static final String PREFRENCE_KEY_ROLE = "role";

    public static final int ONLINE = 0;
    public static final int OFFLINE = 1;
    public static int APP_MODE = ONLINE; //0 for online mode  and 1 for offline mode

    public static String state = null;
    public static String stateID;
    public static String user_id;
    public static String visible_Name;
    public static String mobile_no;
    public static String role;
    public static Boolean isLogin=false;
    public static String latitude;
    public static String longitude;
    //////////////////////////////////////////////////////////////////
    public static final String DATA_SET = "data_set";
    public static final String MANDI_DETAIL = "mandi_detail";
    public static final String APPLIED_VALUE_OF_N = "a_value_N";
    public static final String APPLIED_VALUE_OF_P = "a_value_P";
    public static final String APPLIED_VALUE_OF_K = "a_value_K";

    public static final String IDEAL_VALUE_OF_N = "i_value_N";
    public static final String IDEAL_VALUE_OF_P = "i_value_P";
    public static final String IDEAL_VALUE_OF_K = "i_value_K";
    public static final String SOWING_DATE_FROM = "sowing_From";
    public static final String SOWING_DATE_TO = "sowing_To";
    //////////////////////////////////////////////////////////////////
    public static int CONCERN_ID = 0;
    public static final int INCREASE_REVENIUE=1;
    public static final int INCREASE_YIELD=2;
    public static final int BEST_PRICE=3;
    ///////////////////////////////////////////////////////////////
    public static final int SOW_PERIOD_FROM=0;
    public static final int SOW_PERIOD_TO=1;

    public static final String STATE_ID = "state_id";
    public static final String CROP_INITIAL = "crop_initial";
    public static final String CROP_ALL_INITIAL = "crop_all_initial";
    public static String[] syncArray = {STATE_ID,CROP_INITIAL, CROP_ALL_INITIAL};

    ////////////observing activity
    public static final int HomeActivity = 1001;
    public static final int AddFarmMap = 1002;

    public static final int selectForm=101;
    public static final int tellMeMore=102;

    public static ArrayList<LatLng> routeArray = new ArrayList<>();

    public static Double maxDistance = 25.0;

    public static boolean isWrite = false;
    public static boolean isRequestedWrite = false;

    public static final String REGISTER_USER = "register_user";
    public static final String SERVER_CONNECTION_ERROR = "Could Not Connect Server";


    public static String fatherName;
    public static String villageName;
    public static String villageID;
    public static String revenueNo;
    public static String gutNo;
    public static String mobile_type;
}
