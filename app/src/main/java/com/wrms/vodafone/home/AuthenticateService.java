package com.wrms.vodafone.home;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;


import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.CropQueryData;
import com.wrms.vodafone.entities.FarmInformationData;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.utils.CustomHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AuthenticateService extends IntentService {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String AUTHENTICATION_METHOD = "imei_authentication";

    private static boolean isRunning = false;

    public AuthenticateService() {
        super("AuthenticateService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "My farm info ran!");

        String result = "";
        DBAdapter db = new DBAdapter(getApplicationContext());
        db.open();

        Cursor getSavedCredentials = db.getSavedCredentials();
        if (getSavedCredentials.getCount() > 0) {
            getSavedCredentials.moveToFirst();
            do {
                String id = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.ID));
                String mailId = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.EMAIL_ADDRESS));
                String userName = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.USER_NAME));
                String password = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.PASSWORD));
                String visibleName = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.VISIBLE_NAME));
                String createdDateTime = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
                String oldUserId = getSavedCredentials.getString(getSavedCredentials.getColumnIndex(DBAdapter.USER_ID));


                System.out.println("gotUser id : " + oldUserId);
                String completeStringForRegister = mailId + "/" + userName + "/" + password + "/" + visibleName + "/" + AppConstant.mobile_no+ "/" + AppConstant.villageID+ "/" + AppConstant.fatherName+ "/" + AppConstant.revenueNo+ "/" + AppConstant.gutNo+ "/" + AppConstant.mobile_type;

                String response;

                try {
                    ExternalStorageGPS.write_file("MFI_RESPONSE_LOG", true, format.format(cal().getTime()) + "---USER REGISTER STRING : " + "http://myfarminfo.com/yfirest.svc/Register/" + completeStringForRegister + " \n\r");
                    response = CustomHttpClient.executeHttpPut("http://myfarminfo.com/yfirest.svc/registerUser_Jalna/" + completeStringForRegister);
                    ExternalStorageGPS.write_file("MFI_RESPONSE_LOG", true, format.format(cal().getTime()) + "---USER REGISTER RESPONSE : " + response + " \n\r");
                    System.out.println("response " + response);
                    Log.d("RegistrationData", response);
                    if (response.contains("Error")) {
                        String s = "Error";
                        ContentValues values = new ContentValues();
                        values.put(DBAdapter.ID, id);
                        values.put(DBAdapter.USER_NAME, userName);
                        values.put(DBAdapter.VISIBLE_NAME, visibleName);
                        values.put(DBAdapter.PASSWORD, password);
                        values.put(DBAdapter.EMAIL_ADDRESS, mailId);
                        values.put(DBAdapter.USER_ID, oldUserId);
                        values.put(DBAdapter.CREATED_DATE_TIME, createdDateTime);
                        values.put(DBAdapter.SENDING_STATUS, DBAdapter.SENT);

                        db.db.update(DBAdapter.TABLE_CREDENTIAL, values, DBAdapter.ID + " = '" + id + "'", null);
                        continue;
                    }
                    response = response.trim();
                  //  response = response.substring(1, response.length() - 1);
                    response = response.replace("\\", "");
                    response = response.replace("\"{", "{");
                    response = response.replace("}\"", "}");
                    response = response.replace("\"[", "[");
                    response = response.replace("]\"", "]");
                    response = response.replace("\\", "");
                    JSONArray jArray = new JSONArray(response);
                    Log.d("afterfilterResponse", response);

                    if (jArray.length() == 0) {
                        String message = "No Registered";
                    }

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject jObject = jArray.getJSONObject(i);
                        String userId = jObject.getString("UserID");
                        String visibleName1 = jObject.getString("VisibleName");

                        ContentValues values = new ContentValues();
                        values.put(DBAdapter.ID, id);
                        values.put(DBAdapter.USER_NAME, userName);
                        values.put(DBAdapter.VISIBLE_NAME, visibleName1);
                        values.put(DBAdapter.PASSWORD, password);
                        values.put(DBAdapter.EMAIL_ADDRESS, mailId);
                        values.put(DBAdapter.USER_ID, userId);
                        values.put(DBAdapter.CREATED_DATE_TIME, createdDateTime);
                        values.put(DBAdapter.NEED_TO_EDIT, DBAdapter.FALSE);
                        values.put(DBAdapter.SENDING_STATUS, DBAdapter.SENT);

                        db.db.update(DBAdapter.TABLE_CREDENTIAL, values, DBAdapter.ID + " = '" + id + "'", null);
                        boolean isUpdated = db.updateFormByUserId(oldUserId, userId);
                        System.out.println("Corresponding farm is updated : " + isUpdated);

                        if (AppConstant.user_id != null) {
                            if (oldUserId.equals(AppConstant.user_id)) {
                                AppConstant.user_id = userId;
                            }
                        }

                        String message = "OK";
                    }
                } catch (Exception e) {
//
                    e.printStackTrace();
                    Log.d("Status", "" + e);
                }

            } while (getSavedCredentials.moveToNext());
        }
        getSavedCredentials.close();

        /*Cursor getAllForm = db.getAllForm();
        if(getAllForm.getCount()>0){
            getAllForm.moveToFirst();
            System.out.println("Name : "+getAllForm.getString(getAllForm.getColumnIndex(DBAdapter.FARM_NAME))+" Sending Status : "+getAllForm.getString(getAllForm.getColumnIndex(DBAdapter.SENDING_STATUS)));
        }*/


        Cursor getSavedFarm = db.getSavedForm();
        if (getSavedFarm.getCount() > 0) {
            getSavedFarm.moveToFirst();
            do {
                FarmInformationData data = new FarmInformationData();
                String id = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.ID));
                String farmId = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.FARM_ID));
                String farmName = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.FARM_NAME));
                String farmerName = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.FARMER_NAME));
                String userId = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.USER_ID));
                String contour = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.CONTOUR));
                String state = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.STATE_ID));
                String concern = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.CONCERN));
                String area = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.AREA));
                String logDate = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.LOG_DATE));
                String maxLat = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.MAX_LAT));
                String maxLng = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.MAX_LON));
                String minLat = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.MIN_LAT));
                String minLng = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.MIN_LON));
                String centerLat = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.CENTRE_LAT));
                String centerLng = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.CENTRE_LON));

                String sow = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.SOWING_DATE));
                String var = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.SEED_VARIETY));
                String last = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.LAST_CROP_SOWN));
                String irr_sour = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.IRRIGATION_SOURCE));
                String irr_patt = getSavedFarm.getString(getSavedFarm.getColumnIndex(DBAdapter.IRRIGATION_PATTERN));

                data.setFarmId(farmId);
                data.setFarmName(farmName);
                data.setFarmerName(farmerName);
                data.setUserID(userId);
                data.setAllLatLngPoint(contour);
                data.setState(state);
                data.setYourCencern(concern);
                data.setArea(area);

                data.setSowingDate(sow);
                data.setSeedVariety(var);
                data.setLastCropSown(last);
                data.setIrrigationSource(irr_sour);
                data.setIrrigationPattern(irr_patt);

                ArrayList<CropQueryData> cropQueryDataArray = new ArrayList<>();

                Cursor cropDetail = db.getCropQueryByFarmId(farmId);
                System.out.println(("CropDeatil count : " + cropDetail.getCount()));
                if (cropDetail.getCount() > 0) {
                    cropDetail.moveToFirst();
                    do {
                        CropQueryData cropData = new CropQueryData(cropDetail);
                        cropQueryDataArray.add(cropData);

                    } while (cropDetail.moveToNext());
                }
                String parameterString = createJsonParameterForSaveForm(data, cropQueryDataArray, db);

                String createdString = AppManager.getInstance().removeSpaceForUrl(parameterString);

                String sendPath = AppManager.getInstance().saveFarmInfo;
                System.out.println("Save URL : " + sendPath);

                ExternalStorageGPS.write_file("MFI_RESPONSE_LOG", true, format.format(cal().getTime()) + "---FARM STRING : " + createdString + " \n\r");

                String response = AppManager.getInstance().httpRequestPutMethod(sendPath, createdString);
                ExternalStorageGPS.write_file("MFI_RESPONSE_LOG", true, format.format(cal().getTime()) + "---FARM RESPONSE : " + response + " \n\r");
                System.out.println("Save Response :---" + response);
                if (response != null) {
                    if (response.contains("Success") || response.contains("Error:")) {
                        if (cropDetail.getCount() > 0) {
                            cropDetail.moveToFirst();
                            do {
                                String cropQueryColumnId = cropDetail.getString(cropDetail.getColumnIndex(DBAdapter.ID));
                                CropQueryData cropData = new CropQueryData(cropDetail);
                                boolean isUpdated = cropData.updateStatusToSent(db, cropQueryColumnId);
                                System.out.println("Is Crop Detail Updated : " + isUpdated);
                            } while (cropDetail.moveToNext());
                        }

                        ContentValues values = new ContentValues();
                        values.put(DBAdapter.ID, id);
                        values.put(DBAdapter.FARM_ID, farmId);
                        values.put(DBAdapter.FARM_NAME, farmName);
                        values.put(DBAdapter.USER_ID, userId);
                        values.put(DBAdapter.CONTOUR, contour);
                        values.put(DBAdapter.STATE_ID, state);
                        values.put(DBAdapter.CONCERN, concern);
                        values.put(DBAdapter.AREA, area);
                        values.put(DBAdapter.LOG_DATE, logDate);
                        values.put(DBAdapter.MAX_LAT, maxLat);
                        values.put(DBAdapter.MAX_LON, maxLng);
                        values.put(DBAdapter.MIN_LAT, minLat);
                        values.put(DBAdapter.MIN_LON, minLng);
                        values.put(DBAdapter.CENTRE_LAT, centerLat);
                        values.put(DBAdapter.CENTRE_LON, centerLng);
                        values.put(DBAdapter.SOWING_DATE, sow);
                        values.put(DBAdapter.SEED_VARIETY, var);
                        values.put(DBAdapter.LAST_CROP_SOWN, last);
                        values.put(DBAdapter.IRRIGATION_SOURCE, irr_sour);
                        values.put(DBAdapter.IRRIGATION_PATTERN, irr_patt);
                        values.put(DBAdapter.SENDING_STATUS, DBAdapter.SENT);

                        long j = db.db.update(DBAdapter.DATABASE_TABLE_ALL_FARM_DETAIL, values, DBAdapter.ID + " = '" + id + "'", null);

                        if (j != -1) {
                            System.out.println("Farm detail updated : " + j);
                        }
                    }
                }

            } while (getSavedFarm.moveToNext());
        }

    }

    private String createJsonParameterForSaveForm(FarmInformationData farmData, ArrayList<CropQueryData> cropQueryData, DBAdapter db) {
        String parameterString = "";
        JSONArray cropInfo = new JSONArray();

        for (CropQueryData data : cropQueryData) {
            try {
                if (data.getCropID().equals("-1")) {
                    continue;
                } else {
                    Cursor cursor = db.getCropNameById(data.getCropID());
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        data.setCrop(cursor.getString(cursor.getColumnIndex(DBAdapter.CROP)));
                    }
                    cursor.close();
                }
                JSONObject cropInfoJsonObject = new JSONObject();
                cropInfoJsonObject.put("CropID", data.getCropID());
                cropInfoJsonObject.put("CropName", data.getCrop());
                cropInfoJsonObject.put("Variety", data.getVariety());
                cropInfoJsonObject.put("N", data.getBasalDoseN());
                cropInfoJsonObject.put("P", data.getBasalDoseP());
                cropInfoJsonObject.put("K", data.getBasalDoseK());
                cropInfoJsonObject.put("BasalDoseApply", data.getBesalDoseApply());
                cropInfoJsonObject.put("SowDate", data.getSowPeriodForm());
                cropInfoJsonObject.put("CropFrom", data.getSowPeriodForm());
                cropInfoJsonObject.put("CropTo", data.getSowPeriodTo());
                cropInfoJsonObject.put("OtherNutrient", data.getOtherNutrition());

                cropInfoJsonObject.put("SeedVariety", farmData.getSeedVariety());
                cropInfoJsonObject.put("LastCropSown", farmData.getLastCropSown());
                cropInfoJsonObject.put("SowingDate", farmData.getSowingDate());
                cropInfoJsonObject.put("IrrigationSource", farmData.getIrrigationSource());
                cropInfoJsonObject.put("IrrigationPattern", farmData.getIrrigationPattern());

                cropInfo.put(cropInfoJsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject finalJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
//                jsonObject.put("UserID", farmData.getUserID());
            String farmId = farmData.getFarmId();
            if (farmId.length() < 5) {
                jsonObject.put("FarmID", farmId);
            } else {
                jsonObject.put("FarmID", "0");
            }
            jsonObject.put("FarmName", farmData.getFarmName());
            jsonObject.put("FarmerName", farmData.getFarmerName());
            jsonObject.put("Contour", farmData.getAllLatLngPoint());
            jsonObject.put("CropID", farmData.getCropID());
            jsonObject.put("State", farmData.getState());
            jsonObject.put("CropInfo", cropInfo);
            jsonObject.put("Concerns", farmData.getYourCencern());
            jsonObject.put("Concerns", farmData.getYourCencern());
            jsonObject.put("Area", farmData.getArea());

            finalJson.put("UserID", farmData.getUserID());
            finalJson.put("FarmInfo", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //      jsonObject.put("guarderiasIdGuarderias",jsonObject2);
        parameterString = finalJson.toString();
        String replaceString = "\"";
        return parameterString;
    }

    public static Calendar cal() {
        Calendar cal = Calendar.getInstance();
        return cal;
    }



}
