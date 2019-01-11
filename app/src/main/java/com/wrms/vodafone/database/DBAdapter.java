package com.wrms.vodafone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wrms.vodafone.entities.AllFarmDetail;


public class DBAdapter {


    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "MyDB2";
    private static final int DATABASE_VERSION = 5;

    public static final String DATABASE_TABLE_CROP_VARIETY = "cropVariety";
    public static final String DATABASE_TABLE_ALL_FARM_DETAIL = "allFarmDetail";
    public static final String TABLE_CROP = "crop";
    public static final String TABLE_STATE = "state";
    public static final String TABLE_VILLAGE = "village";
    public static final String TABLE_QUERY_CROP = "query_crop";
    public static final String TABLE_CREDENTIAL = "credential";
    public static final String TABLE_CROP_VARIETY = "crop_variety";

    public static final String CROP = "crop";
    public static final String ID = "_id";
    public static final String VARIETY = "variety";
    //////////////////////////////////////////////////////////////////
    public static final String FARM_ID = "farmId";
    public static final String FARM_NAME = "farmName";
    public static final String FARMER_NAME = "farmerName";
    public static final String FARMER_PHONE = "farmerPhone";
    public static final String FARM_AREA = "farmArea";
    public static final String USER_ID = "userId";
    public static final String CONTOUR = "contour";
    public static final String CROP_ID = "cropId";
    public static final String CROPS_VARIETY = "crops_variety";
    public static final String STATE_ID = "state_id";
    public static final String STATE_NAME = "state_name";

    public static final String VILLAGE_ID = "village_id";
    public static final String VILLAGE_NAME = "village_name";

    public static final String CROP_FROM = "cropFrom";
    public static final String CROP_TO = "cropTo";
    public static final String BASAL_DOSE_APPLY = "basalDoseApply";
    public static final String VALUE_N = "valueN";
    public static final String VALUE_P = "valueP";
    public static final String VALUE_K = "valueK";
    public static final String OTHER_NUTRIENT = "otherNutrient";
    public static final String CONCERN = "concern";
    public static final String AREA = "area";
    public static final String LOG_DATE = "logDate";
    public static final String MAX_LAT = "maxLat";
    public static final String MAX_LON = "maxLon";
    public static final String MIN_LAT = "minLat";
    public static final String MIN_LON = "minLon";
    public static final String CENTRE_LAT = "centerLat";
    public static final String CENTRE_LON = "centreLon";

    public static final String SOWING_DATE = "sowing_date";
    public static final String SEED_VARIETY = "seed_variety";
    public static final String LAST_CROP_SOWN = "last_crop_sown";
    public static final String IRRIGATION_SOURCE = "irrigation_source";
    public static final String IRRIGATION_PATTERN = "irrigation_pattern";


    private static final String DATABASE_CREATE_CROP_VARIETY =
            "create table cropVariety (_id integer primary key autoincrement, "
                    + "crop text not null, variety text not null);";

    private static final String CREATE_TABLE_STATE = "create table " + TABLE_STATE + " (" + ID + " integer primary key autoincrement, " +
            STATE_ID + " text not null," +
            STATE_NAME + " text not null);";

    private static final String CREATE_TABLE_VILLAGE = "create table " + TABLE_VILLAGE + " (" + ID + " integer primary key autoincrement, " +
            VILLAGE_ID + " text not null," +
            VILLAGE_NAME + " text not null);";

    private static final String CREATE_TABLE_CROP =
            "create table " + TABLE_CROP + " (" + ID + " integer primary key autoincrement, " +
                    CROP_ID + " text not null," +
                    CROP + " text not null);";
    ///////////////////////////////////////////////////////////////////////

    public static final String USER_NAME = "user_name";
    public static final String VISIBLE_NAME = "visible_name";
    public static final String MOBILE_NO = "mobile_no";
    public static final String PASSWORD = "password";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String CREATED_DATE_TIME = "created_date_time";
    public static final String NEED_TO_EDIT = "need_to_edit";
    public static final String SENDING_STATUS = "sending_status";
    public static final String SENT = "sent";
    public static final String SAVE = "save";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private static final String CREAT_CREDENTIAL = "create table " + TABLE_CREDENTIAL + " (" + ID + " integer primary key autoincrement,"
            + USER_NAME + " text not null,"
            + VISIBLE_NAME + " text not null,"
            + PASSWORD + " text not null,"
            + EMAIL_ADDRESS + " text,"
            + MOBILE_NO + " text,"
            + USER_ID + " text not null,"
            + CREATED_DATE_TIME + " text not null,"
            + NEED_TO_EDIT + " text,"
            + SENDING_STATUS + " text not null);";

    private static final String CREATE_CROP_VATIETY = "create table " + TABLE_CROP_VARIETY + " (" + ID + " integer primary key autoincrement,"
            + STATE_ID + " text,"
            + CROP_ID + " text,"
            + CROP + " text,"
            + VARIETY + " text);";


    private static final String DATABASE_CREAT_ALL_FARM_DETAIL =
            "create table " + DATABASE_TABLE_ALL_FARM_DETAIL + " (" + ID + " integer primary key autoincrement,"
                    + FARM_ID + " text not null,"
                    + FARM_NAME + " text not null,"
                    + FARMER_NAME + " text,"
                    + FARMER_PHONE + " text,"
                    + FARM_AREA + " text,"
                    + USER_ID + " text,"
                    + CONTOUR + " text,"
                    + STATE_ID + " text,"
                    + CONCERN + " text,"
                    + AREA + " text,"
                    + LOG_DATE + " text,"
                    + MAX_LAT + " text,"
                    + MAX_LON + " text,"
                    + MIN_LAT + " text,"
                    + MIN_LON + " text,"
                    + CENTRE_LAT + " text,"
                    + CENTRE_LON + " text,"
                    + SOWING_DATE + " text,"
                    + SEED_VARIETY + " text,"
                    + LAST_CROP_SOWN + " text,"
                    + IRRIGATION_SOURCE + " text,"
                    + IRRIGATION_PATTERN + " text,"
                    + SENDING_STATUS + " text not null);";


    private static final String DATABASE_CREAT_QUERY_CROP =
            "create table " + TABLE_QUERY_CROP + " (" + ID + " integer primary key autoincrement,"
                    + FARM_ID + " text not null,"
                    + FARM_NAME + " text not null,"
                    + CROP_ID + " text,"
                    + CROPS_VARIETY + " text,"
                    + CROP_FROM + " text,"
                    + CROP_TO + " text,"
                    + BASAL_DOSE_APPLY + " text,"
                    + VALUE_N + " text,"
                    + VALUE_P + " text,"
                    + VALUE_K + " text,"
                    + OTHER_NUTRIENT + " text,"
                    + CONCERN + " text,"
                    + SENDING_STATUS + " text not null);";

	/*private static final String CREATE_TABLE_STATE = "create table "+TABLE_STATE+" ("+ID+" integer primary key autoincrement,"
            +STATE_ID+" text not null,"
			+STATE_ID+" text not null);";*/


    private final Context context;
    private DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_CROP_VARIETY);
                db.execSQL(CREATE_TABLE_CROP);
                db.execSQL(DATABASE_CREAT_QUERY_CROP);
                db.execSQL(CREAT_CREDENTIAL);
                db.execSQL(CREATE_TABLE_STATE);
                db.execSQL(CREATE_TABLE_VILLAGE);
                db.execSQL(CREATE_CROP_VATIETY);
                db.execSQL(DATABASE_CREAT_ALL_FARM_DETAIL);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");


            db.execSQL("DROP TABLE IF EXISTS "+"cropVariety");

            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_ALL_FARM_DETAIL);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CROP);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_QUERY_CROP);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CREDENTIAL);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_STATE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_VILLAGE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CROP_VARIETY);

            onCreate(db);

        }

    }

    public SQLiteDatabase getSQLiteDatabase() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        return db;
    }

    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }
    ///////////////////////////////////////////////////////////////  all query written from here
    //---insert a contact into the database---

    public long insertCropVariety(String crop, String variety) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CROP, crop);
        initialValues.put(VARIETY, variety);
        return db.insert(DATABASE_TABLE_CROP_VARIETY, null, initialValues);
    }

    public long insertAllFarmDetail(AllFarmDetail allFarmDetail, String sendingStatus) {

        long l = -1;
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(FARM_ID, allFarmDetail.getFarmId());
            initialValues.put(FARM_NAME, allFarmDetail.getFarmName());
            initialValues.put(FARMER_NAME, allFarmDetail.getFarmerName());
            initialValues.put(FARMER_PHONE, allFarmDetail.getFarmerPhone());
            initialValues.put(FARM_AREA, allFarmDetail.getActualFarmArea());
            System.out.println("Saved userId with farm : " + allFarmDetail.getUserId());
            initialValues.put(USER_ID, allFarmDetail.getUserId());
            initialValues.put(CONTOUR, allFarmDetail.getContour());
		/*initialValues.put(CROP_ID, allFarmDetail.getCropId());
		initialValues.put(CROPS_VARIETY, allFarmDetail.getVariety());*/
            initialValues.put(STATE_ID, allFarmDetail.getState());
		/*initialValues.put(CROP_FROM, allFarmDetail.getCropFrom());
		initialValues.put(CROP_TO, allFarmDetail.getCropTo());
		initialValues.put(BASAL_DOSE_APPLY, allFarmDetail.getBasalDoseApply());
		initialValues.put(VALUE_N, allFarmDetail.getValueN());
		initialValues.put(VALUE_P, allFarmDetail.getValueP());
		initialValues.put(VALUE_K, allFarmDetail.getValueK());
		initialValues.put(OTHER_NUTRIENT, allFarmDetail.getOtherNutrient());*/
            initialValues.put(CONCERN, allFarmDetail.getConcern());
            initialValues.put(AREA, allFarmDetail.getArea());
            initialValues.put(LOG_DATE, allFarmDetail.getLogDate());
            initialValues.put(MAX_LAT, allFarmDetail.getMaxLat());
            initialValues.put(MAX_LON, allFarmDetail.getMaxLon());
            initialValues.put(MIN_LAT, allFarmDetail.getMinLat());
            initialValues.put(MIN_LON, allFarmDetail.getMinLon());
            initialValues.put(CENTRE_LAT, allFarmDetail.getCenterLat());
            initialValues.put(CENTRE_LON, allFarmDetail.getCentreLon());
            initialValues.put(SOWING_DATE, allFarmDetail.getSowingDate());
            initialValues.put(SEED_VARIETY, allFarmDetail.getSeedVariety());
            initialValues.put(LAST_CROP_SOWN, allFarmDetail.getLastCropSown());
            initialValues.put(IRRIGATION_SOURCE, allFarmDetail.getIrrigationSource());
            initialValues.put(IRRIGATION_PATTERN, allFarmDetail.getIrrigationPattern());
            initialValues.put(SENDING_STATUS, sendingStatus);


            l = db.insert(DATABASE_TABLE_ALL_FARM_DETAIL, null, initialValues);
        }catch (Exception e){
            e.printStackTrace();
        }
        return l;
    }

    public Cursor getallFarmName(String userId) {
        return db.query(DATABASE_TABLE_ALL_FARM_DETAIL, new String[]{FARM_NAME}, USER_ID + " = '" + userId + "'", null, null, null, null);

    }

    public Cursor getallContour(String userId) {
        return db.query(DATABASE_TABLE_ALL_FARM_DETAIL, new String[]{FARM_NAME,CENTRE_LAT,CENTRE_LON,CONTOUR}, USER_ID + " = '" + userId + "'", null, null, null, null);

    }

    //---retrieves all the contacts---
    public Cursor getCropList() {
        return db.query(DATABASE_TABLE_CROP_VARIETY, new String[]{CROP,
                VARIETY}, null, null, null, null, null);
    }

    //---retrieves a particular contact---
    public Cursor getVariety(String name) throws SQLException {
        return db.query(true, DATABASE_TABLE_CROP_VARIETY, new String[]{
                VARIETY}, CROP + "='" + name + "'", null, null, null, null, null);

    }

    public Cursor getAllCrop() {
        return db.query(true, TABLE_CROP, new String[]{CROP_ID, CROP}, null, null, null, null, null, null);
    }

    public String getCropIdByName(String name) {
        String cropId = "";
        Cursor cursor = db.query(true, TABLE_CROP_VARIETY, new String[]{CROP_ID, CROP}, CROP + " = '" + name + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cropId = cursor.getString(cursor.getColumnIndex(CROP_ID));
        }
        cursor.close();
        return cropId;
    }

    public Cursor getSelectedFarmsValue(String farmName) {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, new String[]
                        {
                                FARM_ID,
                                FARM_NAME,
                                FARMER_NAME,
                                FARMER_PHONE,
                                FARM_AREA,
                                USER_ID,
                                CONTOUR,
                                STATE_ID,
                                CONCERN,
                                AREA
                        },
                FARM_NAME + "='" + farmName + "'", null, null, null, null, null);
    }

    public Cursor getStateFromSelectedFarm(String farmName) {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, new String[]{STATE_ID, CENTRE_LAT, CONTOUR, CENTRE_LON, STATE_ID, AREA}, FARM_NAME + "='" + farmName + "'", null, null, null, null, null);
    }

    public Cursor getSavedForm() {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, null, SENDING_STATUS + "='" + SAVE + "'", null, null, null, null, null);
    }

    public Cursor isFarmAlreadyExist(String farmName, String userId) {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, null, FARM_NAME + "='" + farmName + "' AND "+USER_ID+" ='"+userId+"'", null, null, null, null, null);
    }

    public Cursor isFarmAlreadyExist(String farmId) {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, null, FARM_ID + "='" + farmId + "'", null, null, null, null, null);
    }

    public Cursor getFarmByFarmName(String farmName) {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, null, FARM_NAME + "='" + farmName + "'", null, null, null, null, null);
    }

    public Cursor getAllForm() {
        return db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, null, null, null, null, null, null, null);
    }

    public boolean updateFormByUserId(String oldUserId, String newUserId) {
        boolean isUpdated = false;
        Cursor cursor = db.query(true, DATABASE_TABLE_ALL_FARM_DETAIL, null, USER_ID + "='" + oldUserId + "'", null, null, null, null, null);
        System.out.println("Coresponding farm with old userId : " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            values.put(ID, cursor.getString(cursor.getColumnIndex(ID)));
            values.put(FARM_ID, cursor.getString(cursor.getColumnIndex(FARM_ID)));
            values.put(FARM_NAME, cursor.getString(cursor.getColumnIndex(FARM_NAME)));
            values.put(FARMER_NAME, cursor.getString(cursor.getColumnIndex(FARMER_NAME)));
            values.put(FARMER_PHONE, cursor.getString(cursor.getColumnIndex(FARMER_PHONE)));
            values.put(FARM_AREA, cursor.getString(cursor.getColumnIndex(FARM_AREA)));
            values.put(USER_ID, newUserId);
            values.put(CONTOUR, cursor.getString(cursor.getColumnIndex(CONTOUR)));
            values.put(STATE_ID, cursor.getString(cursor.getColumnIndex(STATE_ID)));
            values.put(CONCERN, cursor.getString(cursor.getColumnIndex(CONCERN)));
            values.put(AREA, cursor.getString(cursor.getColumnIndex(AREA)));
            values.put(LOG_DATE, cursor.getString(cursor.getColumnIndex(LOG_DATE)));
            values.put(MAX_LAT, cursor.getString(cursor.getColumnIndex(MAX_LAT)));
            values.put(MAX_LON, cursor.getString(cursor.getColumnIndex(MAX_LON)));
            values.put(MIN_LAT, cursor.getString(cursor.getColumnIndex(MIN_LAT)));
            values.put(MIN_LON, cursor.getString(cursor.getColumnIndex(MIN_LON)));
            values.put(CENTRE_LAT, cursor.getString(cursor.getColumnIndex(CENTRE_LAT)));
            values.put(CENTRE_LON, cursor.getString(cursor.getColumnIndex(CENTRE_LON)));
            values.put(SOWING_DATE, cursor.getString(cursor.getColumnIndex(SOWING_DATE)));
            values.put(SEED_VARIETY, cursor.getString(cursor.getColumnIndex(SEED_VARIETY)));
            values.put(LAST_CROP_SOWN, cursor.getString(cursor.getColumnIndex(LAST_CROP_SOWN)));
            values.put(IRRIGATION_SOURCE, cursor.getString(cursor.getColumnIndex(IRRIGATION_SOURCE)));
            values.put(IRRIGATION_PATTERN, cursor.getString(cursor.getColumnIndex(IRRIGATION_PATTERN)));
            values.put(SENDING_STATUS, cursor.getString(cursor.getColumnIndex(SENDING_STATUS)));
            long k = db.update(DATABASE_TABLE_ALL_FARM_DETAIL, values, USER_ID + " = '" + oldUserId + "'", null);
            if (k != -1) {
                isUpdated = true;
            }
        }
        cursor.close();

        return isUpdated;
    }

    public long deleteAllCropVarietyTableRecord() {
        try {
            return db.delete(DATABASE_TABLE_CROP_VARIETY, null, null);

        } catch (Exception e) {

            e.printStackTrace();
            return -1;
        }
    }

/*
    public long deleteAllFarmDetailTable() {
        try {
            return db.delete(DATABASE_TABLE_ALL_FARM_DETAIL, null, null);

        } catch (Exception e) {

            e.printStackTrace();
            return -1;
        }
    }
*/

    public Cursor getCropQueryByFarmId(String farmId) {
        System.out.println("FarmId : "+farmId);
        return db.query(true, TABLE_QUERY_CROP, null, FARM_ID + "='" + farmId + "'", null, null, null, null, null);
    }

    public Cursor getCropQueryById(String id) {
        return db.query(true, TABLE_QUERY_CROP, null, ID + "='" + id + "'", null, null, null, null, null);
    }

    public Cursor getAllStates() {
        return db.query(true, TABLE_STATE, null, STATE_NAME+" != '_Unknown'", null, null, null, null, null);
    }

    public Cursor getCropByState(String stateId){
        return db.query(true, TABLE_CROP_VARIETY, null, STATE_ID+" = '"+stateId+"'", null, CROP_ID, null, null, null);
    }

    public Cursor getVarietyByStateAndCrop(String stateId,String cropId){
        return db.query(true, TABLE_CROP_VARIETY, new String[]{VARIETY}, STATE_ID+" = '"+stateId+"' AND "+CROP_ID+" = '"+cropId+"'", null, null, null, null, null);
    }

    public Cursor getAllVillages() {
        return db.query(true, TABLE_VILLAGE, null, VILLAGE_NAME+" != '_Unknown'", null, null, null, null, null);
    }

    public Cursor getVillageIdByName(String villName){
        return db.query(true, TABLE_VILLAGE, null, VILLAGE_NAME+" = '"+villName+"'", null, null, null, null, null);
    }



    public Cursor getAllCropVariety() {
        return db.query(true, TABLE_CROP_VARIETY, null, null, null, null, null, null, null);
    }

    public Cursor getCropNameById(String cropId) {
        return db.query(true, TABLE_CROP_VARIETY, null, CROP_ID+" = '"+cropId+"'", null, null, null, null, null);
    }


    public Cursor getSavedCredentials() {
        return db.query(true, TABLE_CREDENTIAL, null, SENDING_STATUS + " = '" + SAVE + "'", null, null, null, null, null);
    }


    public Cursor getAllCredentials() {
        return db.query(true, TABLE_CREDENTIAL, null, null, null, null, null, null, null);
    }


    public Cursor isUserExist(String emailAddress) {
        return db.query(true, TABLE_CREDENTIAL, null, EMAIL_ADDRESS + "='" + emailAddress + "'", null, null, null, null, null);

    }

    public Cursor isUserSaved(String userId) {
        return db.query(true, TABLE_CREDENTIAL, null, USER_ID + "='" + userId + "' AND "+SENDING_STATUS+" ='"+SAVE+"'", null, null, null, null, null);

    }

    public Cursor isAuthenticated(String emailAddress, String password) {
        return db.query(true, TABLE_CREDENTIAL, null, EMAIL_ADDRESS + "='" + emailAddress + "' AND " + PASSWORD + " = '" + password + "'", null, null, null, null, null);

    }


}