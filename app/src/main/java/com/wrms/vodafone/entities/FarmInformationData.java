package com.wrms.vodafone.entities;


import com.wrms.vodafone.utils.AppManager;

/**
 * Created by piyush on 9/28/2015.
 */
public class FarmInformationData {
    private String besalDoseApply;
    private String userID;
    private String userName;
    private String farmName;
    private String farmerName;
    private String crop;
    private String cropID;
    private String variety;
    private String sowPeriodForm;
    private String sowPeriodTo;
    private String basalDoseN;
    private String otherNutrition;
    private String farmId;
    private String state;
    private String allLatLngPoint;
    private String basalDoseP;
    private String basalDoseK;
    private String yourCencern;
    private String area;

    private String farmerNumber;
    private String actualFarmArea;

    private String sowingDate;
    private String seedVariety;
    private String lastCropSown;
    private String irrigationSource;
    private String irrigationPattern;

    public String getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(String sowingDate) {
        this.sowingDate = sowingDate;
    }

    public String getIrrigationPattern() {
        return irrigationPattern;
    }

    public void setIrrigationPattern(String irrigationPattern) {
        this.irrigationPattern = irrigationPattern;
    }

    public String getIrrigationSource() {
        return irrigationSource;
    }

    public void setIrrigationSource(String irrigationSource) {
        this.irrigationSource = irrigationSource;
    }

    public String getLastCropSown() {
        return lastCropSown;
    }

    public void setLastCropSown(String lastCropSown) {
        this.lastCropSown = lastCropSown;
    }

    public String getSeedVariety() {
        return seedVariety;
    }

    public void setSeedVariety(String seedVariety) {
        this.seedVariety = seedVariety;
    }

    public String getActualFarmArea() {
        return actualFarmArea;
    }

    public void setActualFarmArea(String actualFarmArea) {
        this.actualFarmArea = actualFarmArea;
    }

    public String getFarmerNumber() {
        return farmerNumber;
    }

    public void setFarmerNumber(String farmerNumber) {
        this.farmerNumber = farmerNumber;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAllLatLngPoint() {
        return allLatLngPoint;
    }

    public void setAllLatLngPoint(String allLatLngPoint) {
        this.allLatLngPoint = allLatLngPoint;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }


    public String getOtherNutrition() {
        return otherNutrition;
    }

    public void setOtherNutrition(String otherNutrition) {
        this.otherNutrition = otherNutrition;
    }

    public String getBesalDoseApply() {
        return besalDoseApply;
    }

    public void setBesalDoseApply(String besalDoseApply) {
        this.besalDoseApply = besalDoseApply;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getCropID() {
        return cropID;
    }

    public void setCropID(String cropID) {
        this.cropID = cropID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getVariety() {

        return AppManager.getInstance().removeSpaceForUrl(AppManager.getInstance().removeShaleshFromVariety(variety));
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getSowPeriodForm() {
        return sowPeriodForm;
    }

    public void setSowPeriodForm(String sowPeriodForm) {
        this.sowPeriodForm = sowPeriodForm;
    }

    public String getSowPeriodTo() {
        return sowPeriodTo;
    }

    public void setSowPeriodTo(String sowPeriodTo) {
        this.sowPeriodTo = sowPeriodTo;
    }

    public String getBasalDoseN() {
        return basalDoseN;
    }

    public void setBasalDoseN(String basalDoseN) {
        this.basalDoseN = basalDoseN;
    }

    public String getBasalDoseP() {
        return basalDoseP;
    }

    public void setBasalDoseP(String basalDoseP) {
        this.basalDoseP = basalDoseP;
    }

    public String getBasalDoseK() {
        return basalDoseK;
    }

    public void setBasalDoseK(String basalDoseK) {
        this.basalDoseK = basalDoseK;
    }

    public String getYourCencern() {
        return yourCencern;
    }

    public void setYourCencern(String yourCencern) {
        this.yourCencern = yourCencern;
    }



//    public void farmInfo(FarmInformationData dataFarmInfo)
//
//    {
//
//
//        //use get method to fetch all data from server and first store in database and disply list view
//    }
}
