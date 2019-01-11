package com.wrms.vodafone.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class AllFarmDetail {

    private String farmId;
    private String farmName;
    private String farmerName;
    private String userId;
    private String contour;
    private String cropId;
    private String variety;
    private String state;
    private String cropFrom;
    private String cropTo;
    private String basalDoseApply;
    private String valueN;
    private String valueP;
    private String valueK;
    private String otherNutrient;
    private String concern;
    private String logDate;
    private String maxLat;
    private String maxLon;
    private String minLat;
    private String minLon;
    private String centerLat;
    private String centreLon;
    private String area;
    private String farmerPhone;
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

    public String getSeedVariety() {
        return seedVariety;
    }

    public void setSeedVariety(String seedVariety) {
        this.seedVariety = seedVariety;
    }

    public String getLastCropSown() {
        return lastCropSown;
    }

    public void setLastCropSown(String lastCropSown) {
        this.lastCropSown = lastCropSown;
    }

    public String getIrrigationSource() {
        return irrigationSource;
    }

    public void setIrrigationSource(String irrigationSource) {
        this.irrigationSource = irrigationSource;
    }

    public String getIrrigationPattern() {
        return irrigationPattern;
    }

    public void setIrrigationPattern(String irrigationPattern) {
        this.irrigationPattern = irrigationPattern;
    }

    public String getActualFarmArea() {
        return actualFarmArea;
    }

    public void setActualFarmArea(String actualFarmArea) {
        this.actualFarmArea = actualFarmArea;
    }

    public AllFarmDetail() {

    }

    public AllFarmDetail(JSONObject jObject) {
        try {
            farmId = jObject.isNull("FarmID") ? "" : jObject.getString("FarmID");
            farmName = jObject.isNull("FarmName") ? "" : jObject.getString("FarmName");
            farmerName = jObject.isNull("FarmerName") ? "" : jObject.getString("FarmerName");
            farmerPhone = jObject.isNull("PhoneNo") ? "" : jObject.getString("PhoneNo");
            actualFarmArea = jObject.isNull("FarmArea") ? "" : jObject.getString("FarmArea");
            userId = jObject.isNull("UserID") ? "" : jObject.getString("UserID");
            contour = jObject.isNull("Contour") ? "" : jObject.getString("Contour");
            state = jObject.isNull("State") ? "" : jObject.getString("State");
            String concernTemp = jObject.isNull("Concerns") ? "" : jObject.getString("Concerns");
            concern = concernTemp.replaceAll("%20", " ");
            logDate = jObject.isNull("LogDate") ? "" : jObject.getString("LogDate");
            maxLat = jObject.isNull("MaxLat") ? "" : jObject.getString("MaxLat");
            maxLon = jObject.isNull("MaxLon") ? "" : jObject.getString("MaxLon");
            minLat = jObject.isNull("MinLat") ? "" : jObject.getString("MinLat");
            minLon = jObject.isNull("MinLon") ? "" : jObject.getString("MinLon");
            centerLat = jObject.isNull("CenterLat") ? "" : jObject.getString("CenterLat");
            centreLon = jObject.isNull("CenterLon") ? "" : jObject.getString("CenterLon");
            area = jObject.isNull("Area") ? "" : jObject.getString("Area");


            sowingDate = jObject.isNull("SowingDate") ? "" : jObject.getString("SowingDate");
            seedVariety = jObject.isNull("SeedVariety") ? "" : jObject.getString("SeedVariety");
            lastCropSown = jObject.isNull("LastCropSown") ? "" : jObject.getString("LastCropSown");
            irrigationSource = jObject.isNull("IrrigationSource") ? "" : jObject.getString("IrrigationSource");
            irrigationPattern = jObject.isNull("IrrigationPattern") ? "" : jObject.getString("IrrigationPattern");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getFarmerPhone() {
        return farmerPhone;
    }

    public void setFarmerPhone(String farmerPhone) {
        this.farmerPhone = farmerPhone;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContour() {
        return contour;
    }

    public void setContour(String contour) {
        this.contour = contour;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCropFrom() {
        return cropFrom;
    }

    public void setCropFrom(String cropFrom) {
        this.cropFrom = cropFrom;
    }

    public String getCropTo() {
        return cropTo;
    }

    public void setCropTo(String cropTo) {
        this.cropTo = cropTo;
    }

    public String getBasalDoseApply() {
        return basalDoseApply;
    }

    public void setBasalDoseApply(String basalDoseApply) {
        this.basalDoseApply = basalDoseApply;
    }

    public String getValueN() {
        return valueN;
    }

    public void setValueN(String valueN) {
        this.valueN = valueN;
    }

    public String getValueP() {
        return valueP;
    }

    public void setValueP(String valueP) {
        this.valueP = valueP;
    }

    public String getValueK() {
        return valueK;
    }

    public void setValueK(String valueK) {
        this.valueK = valueK;
    }

    public String getOtherNutrient() {
        return otherNutrient;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public void setOtherNutrient(String otherNutrient) {
        this.otherNutrient = otherNutrient;
    }

    public String getConcern() {
        return concern;
    }

    public void setConcern(String concern) {
        this.concern = concern;
    }

    public String getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(String maxLat) {
        this.maxLat = maxLat;
    }

    public String getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(String maxLon) {
        this.maxLon = maxLon;
    }

    public String getMinLat() {
        return minLat;
    }

    public void setMinLat(String minLat) {
        this.minLat = minLat;
    }

    public String getMinLon() {
        return minLon;
    }

    public void setMinLon(String minLon) {
        this.minLon = minLon;
    }

    public String getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(String centerLat) {
        this.centerLat = centerLat;
    }

    public String getCentreLon() {
        return centreLon;
    }

    public void setCentreLon(String centreLon) {
        this.centreLon = centreLon;
    }

    public String removeTimeFromCorpFarm(String str) {
        if (str == null) {

            return str;
        }

        String newDt = str.replace("T00:00:00", " ");

        SimpleDateFormat mdyFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date tempDate = mdyFormat.parse(newDt);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yy");
            String newDate = outputDateFormat.format(tempDate);
            System.out.println("newDate" + newDate);
            return newDate;

        } catch (ParseException e) {

            e.printStackTrace();
            return null;
        }


    }

}
