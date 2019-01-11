package com.wrms.vodafone.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

/**
 * Created by piyush on 9/24/15.
 */
public class AppManager {

    private String URL_BASE_PATH = "https://myfarminfo.com/YFIRest.svc/";
    public String cropsInitial = URL_BASE_PATH + "Crops/Initial/";
    public String FarmYieldImprove = URL_BASE_PATH + "Farm/YieldImprove/";
    public String saveFarmInfo = URL_BASE_PATH + "saveFarmInfo2";
//    public String saveFarmInfo = URL_BASE_PATH + "saveFarmInfo3";
//    saveFarmInfoMC
    public String cropsAllInitial = URL_BASE_PATH+ "Crops/All/Initial/";
    public String login =  URL_BASE_PATH +  "LoginWDeviceToken";
    public String mandiOptimal = URL_BASE_PATH + "Mandi/OptimalMandi/";
    public String getFarmList = URL_BASE_PATH + "Farms/2/";
    public String getStateId = URL_BASE_PATH + "StateID/";

    public String updateDeviceURL = URL_BASE_PATH+"Irrigation/Update/";

    public String getFarmReport = URL_BASE_PATH + "getFilteredFarms";
    public String additionalInfo = URL_BASE_PATH + "additional_info";

    public String uploadImageURL = "http://pdjalna.myfarminfo.com/PDService.svc/uploadBase64Image";
    public String createNewLogURL = "http://pdjalna.myfarminfo.com/PDService.svc/logRequest";

    public String getAllUnResolveURL = "http://pdjalna.myfarminfo.com/PDService.svc/getAllUnresolved";

    public String searchHistoryURL = "http://pdjalna.myfarminfo.com/PDService.svc/Search";

    private static AppManager appManager;

    private AppManager() {
    }

    public static AppManager getInstance() {


        if (appManager == null)
            appManager = new AppManager();
        return appManager;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isOnline(Context context)
    {
        boolean isConnected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(context.CONNECTIVITY_SERVICE);
            isConnected = cm.getActiveNetworkInfo().isConnected();
        }catch (Exception ex){
            isConnected = false;
        }
        return  isConnected;
    }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String removeSpaceForUrl(String str){
        if(str==null){
            return str;
        }
        str = str.replace(" ","%20");
        Log.d("after replacing space", str);
        return str;
    }
    public String placeSpaceIntoString(String str){
        if(str==null){
            return str;
        }
        str = str.replace("%20"," ");
        Log.d("after replacing space", str);
        return str;
    }
    public String removeShaleshFromVariety(String str)
    {
        if(str==null) {
        }
           return str =str.replace("/","~");

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    public String httpRequestGetMethod(String path) {

        String completeUrl=path;
        Log.d("complete url",completeUrl);
        String response = "";
        try {

            response = CustomHttpClient.executeHttpGet(completeUrl);
            System.out.println("response"+response);

            if(response==null){
                return response;
            }
            if(!response.isEmpty()) {
                response = response.trim();
                //response = response.substring(1, response.length() - 1);
                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.replace("\"[", "[");
                response = response.replace("]\"", "]");
            }

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            response = AppConstant.SERVER_CONNECTION_ERROR;
            System.out.println(""+e.getMessage());
        } catch (ProtocolException e) {
            e.printStackTrace();
            response = AppConstant.SERVER_CONNECTION_ERROR;
            System.out.println(""+e.getMessage());
        } catch (IOException e) {
            System.out.println(""+e.getMessage());
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(""+e.getMessage());
            response = AppConstant.SERVER_CONNECTION_ERROR;
        }
        return response;


    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String httpRequestPutMethod(String path, String parameters) {

        String completeUrl=path;

        Log.d("complete url",completeUrl);
        String response = null;
        try {
            response = CustomHttpClient.executeHttpPut(path, parameters);
            System.out.println("before trim response " + response+"--"+response.length());
            if(response.contains("Error1")){
                return "Error1";
            }
            if(!response.isEmpty()) {
                response = response.trim();
              //  response = response.substring(1, response.length() - 1);
                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.replace("\"[", "[");
                response = response.replace("]\"", "]");
            }
            return response;

        }catch (MalformedURLException e) {
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
        } catch (ProtocolException e) {
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
            System.out.println("Request processing error Try again!");
        } catch (IOException e) {
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            response = AppConstant.SERVER_CONNECTION_ERROR;
        }
    return response;
    }


    public String httpRequestPutMethodLogin(String path, String parameters) {

        String completeUrl=path;

        Log.d("complete url",completeUrl);
        String response = null;
        try {
            response = CustomHttpClient.executeHttpPut(path, parameters);
            System.out.println("before trim response " + response+"--"+response.length());
            if(response.contains("Error1")){
                return "Error1";
            }
            if(!response.isEmpty()) {
                response = response.trim();
                response = response.replace("\\", "");
                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.replace("\"[", "[");
                response = response.replace("]\"", "]");
            }
            return response;

        }catch (MalformedURLException e) {
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
        } catch (ProtocolException e) {
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
            System.out.println("Request processing error Try again!");
        } catch (IOException e) {
            response = AppConstant.SERVER_CONNECTION_ERROR;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            response = AppConstant.SERVER_CONNECTION_ERROR;
        }
        return response;
    }

    public String httpRequestPutMethod(String path) {

        String completeUrl=path;
        Log.d("complete url",completeUrl);
        String response = null;
        try {
            response = CustomHttpClient.executeHttpPut(path);
            System.out.println("response " + response);
            if(response==null){
                return response;
            }
            if(!response.isEmpty()) {
                response = response.trim();
              //  response = response.substring(1, response.length() - 1);
                response = response.replace("\\", "");

                response = response.replace("\"{", "{");
                response = response.replace("}\"", "}");
                response = response.replace("\"[", "[");
                response = response.replace("]\"", "]");
            }
            return response;

        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
            System.out.println("Request processing error Try again!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            String str = "NoResponse";
            return str;
        }
        return null;
    }




}
