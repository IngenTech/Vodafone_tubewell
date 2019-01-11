package com.wrms.vodafone.mapfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.wrms.vodafone.R;
import com.wrms.vodafone.database.DBAdapter;
import com.wrms.vodafone.entities.AllFarmDetail;
import com.wrms.vodafone.entities.CropQueryData;
import com.wrms.vodafone.entities.FarmAdvisoryDataSet;
import com.wrms.vodafone.entities.FarmInformationData;
import com.wrms.vodafone.home.AuthenticateService;
import com.wrms.vodafone.home.ExternalStorageGPS;
import com.wrms.vodafone.home.HomeActivity;
import com.wrms.vodafone.home.NavigationDrawerActivity;
import com.wrms.vodafone.utils.AppConstant;
import com.wrms.vodafone.utils.AppManager;
import com.wrms.vodafone.utils.CustomHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.wrms.vodafone.utils.AppConstant.isLogin;
import static com.wrms.vodafone.utils.AppConstant.user_id;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocateYoutFarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocateYoutFarmFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CALLING_ACTIVITY = "callingActivity";
    private static final String FARM_NAME = "FarmName";
    private static final String ALL_POINTS = "AllLatLngPount";
    private static final String AREA = "area";

    // TODO: Rename and change types of parameters
    private int callingActivity;
    private String selectedFarmName;
    private String area;
    String data = null;

    String value = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocateYoutFarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocateYoutFarmFragment newInstance(String callingActivty, String farmName, String AllLatLngPount, String area) {
        LocateYoutFarmFragment fragment = new LocateYoutFarmFragment();
        Bundle args = new Bundle();
        args.putString(CALLING_ACTIVITY, callingActivty);
        args.putString(FARM_NAME, farmName);
        args.putString(ALL_POINTS, AllLatLngPount);
        args.putString(AREA, area);
        fragment.setArguments(args);
        return fragment;
    }

    public LocateYoutFarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            callingActivity = Integer.valueOf(getArguments().getString(CALLING_ACTIVITY));
            selectedFarmName = getArguments().getString(FARM_NAME);
            allDrawLatLngPoint = getArguments().getString(ALL_POINTS);
            area = getArguments().getString(AREA);
            System.out.println("locate your farm fragment Area : " + area);
        }
    }

    EditText farmerName;
    EditText farmerNumber;
    EditText farmArea;
    EditText farmName;
    Spinner cropSpiner1;
    EditText varietySpiner;
    EditText sowPeriodFrom;
    EditText sowPeriodTo;
    Spinner baselDoseSpiner;
    EditText valueN;
    EditText valueP;
    EditText valueK;
    EditText otherNutrition;
    Spinner yourConcernSpiner;
    Button submitForm;
    ArrayList<String> cropValue;
    ArrayList<String> applyBasalDose;
    ArrayList<String> varietyValue;
    DBAdapter db;
    LinearLayout ly;
    LinearLayout ll_nutrition;
    FarmInformationData farmInformationData;
    AllFarmDetail allFarmDetail;
    ArrayAdapter<String> varietySpiner1;
    //    HashMap<String, String> pickCropIdOrValue;
    Calendar myCalendar;
    String allDrawLatLngPoint;
    String creatString;
    private Toolbar toolbar;
    Boolean displayDate = false;
    ArrayList<FarmAdvisoryDataSet> dataSet;
    ArrayList<String> yourConcern;
    int displayDateInEditText;
    Button crop1, crop2, crop3, crop4;
    ArrayList<CropQueryData> cropQueryData = new ArrayList<>();
    private CropQueryData selectedCropQueryCount = null;

    String latitude, longitude, stateId;
    Cursor allCrop;

    TextView emailBTN;

    EditText plotArea;

    TextView additionBTN;

    String sowDateStr = null;
    String seedVarietyStr = null;
    String lastCropSownStr = null;
    String irriSourceStr = null;
    String irriPatternStr = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locate_yout_farm, container, false);

       /* LatLng location = new LatLng(29.9903793091887, 74.23791787265395);
        LatLng location1 = new LatLng(29.9903737660602, 74.23774894138751);
        LatLng location2 = new LatLng(29.9903899210913, 74.23762827823543);
        LatLng location3 = new LatLng(29.9904015417683, 74.23750839252739);
        LatLng location4 = new LatLng(29.9903991993498, 74.23737515928565);
        LatLng location5 = new LatLng(29.9903938646943, 74.23724436094517);
        LatLng location6 = new LatLng(29.9904004682055, 74.23712358004957);
        LatLng location7 = new LatLng(29.9904043841381, 74.23700013965538);
        LatLng location8 = new LatLng(29.9904183014485, 74.23687096597814);
        LatLng location9 = new LatLng(29.9904000041112, 74.23676513976125);
        LatLng location10 = new LatLng(29.9904080091934, 74.23662238720885);
        LatLng location11 = new LatLng(29.9904023235703, 74.23651433167493);
        LatLng location12 = new LatLng(29.990391990672, 74.23635059465187);
        LatLng location13 = new LatLng(29.9903257774501, 74.23619456410268);
        LatLng location14 = new LatLng(29.9902271005743, 74.23620928575838);
        LatLng location15 = new LatLng(29.9901349319673, 74.23619796449361);
        LatLng location16 = new LatLng(29.9900208647674, 74.2362061711475);
        LatLng location17 = new LatLng(29.9898921148266, 74.23619611811989);
        LatLng location18 = new LatLng(29.9897618327177, 74.23619908312457);
        LatLng location19 = new LatLng(29.9896695051768, 74.23620529565551);
        LatLng location20 = new LatLng(29.989560059195, 74.23621796321368);
        LatLng location21 = new LatLng(29.989530753033, 74.23633908719853);
        LatLng location22 = new LatLng(29.989532654199, 74.23645292191338);
        LatLng location23 = new LatLng(29.9895248238735, 74.2366143124325);
        LatLng location24 = new LatLng(29.9895379202474, 74.23675240570788);
        LatLng location25 = new LatLng(29.9895385522799, 74.23686635388731);
        LatLng location26 = new LatLng(29.9895400115776, 74.23699379454138);
        LatLng location27 = new LatLng(29.9895352508845, 74.23709996258617);
        LatLng location28 = new LatLng(29.9895505634248, 74.23724872431694);
        LatLng location29 = new LatLng(29.9895477169709, 74.23735839143433);
        LatLng location30 = new LatLng(29.9895524465475, 74.23751100425034);
        LatLng location31 = new LatLng(29.9895421719814, 74.23766954763369);
        LatLng location32 = new LatLng(29.989541960165, 74.23777865993134);
        LatLng location33 = new LatLng(29.9895539979821, 74.2379207272354);
        LatLng location34 = new LatLng(29.9896576656594, 74.23793660186243);
        LatLng location35 = new LatLng(29.9897562362521, 74.23793681102234);
        LatLng location36 = new LatLng(29.9898876612965, 74.23794593946207);
        LatLng location37 = new LatLng(29.9900046422554, 74.23793978434583);
        LatLng location38 = new LatLng(29.9901203100433, 74.23792558561672);
        LatLng location39 = new LatLng(29.9902393644821, 74.23791955356819);
        LatLng location40 = new LatLng(29.9903301719211, 74.23792367605563);
        LatLng location41 = new LatLng(29.9903639305282, 74.23793199951422);

        ArrayList<LatLng> arrayPointsDemo = new ArrayList<LatLng>();
        arrayPointsDemo.add(location);
        arrayPointsDemo.add(location1);
        arrayPointsDemo.add(location2);
        arrayPointsDemo.add(location3);
        arrayPointsDemo.add(location4);
        arrayPointsDemo.add(location5);
        arrayPointsDemo.add(location6);
        arrayPointsDemo.add(location7);
        arrayPointsDemo.add(location8);
        arrayPointsDemo.add(location9);
        arrayPointsDemo.add(location10);
        arrayPointsDemo.add(location11);
        arrayPointsDemo.add(location12);
        arrayPointsDemo.add(location13);
        arrayPointsDemo.add(location14);
        arrayPointsDemo.add(location15);
        arrayPointsDemo.add(location16);
        arrayPointsDemo.add(location17);
        arrayPointsDemo.add(location18);
        arrayPointsDemo.add(location19);
        arrayPointsDemo.add(location20);
        arrayPointsDemo.add(location21);
        arrayPointsDemo.add(location22);
        arrayPointsDemo.add(location23);
        arrayPointsDemo.add(location24);
        arrayPointsDemo.add(location25);
        arrayPointsDemo.add(location26);
        arrayPointsDemo.add(location27);
        arrayPointsDemo.add(location28);
        arrayPointsDemo.add(location29);
        arrayPointsDemo.add(location30);
        arrayPointsDemo.add(location31);
        arrayPointsDemo.add(location32);
        arrayPointsDemo.add(location33);
        arrayPointsDemo.add(location34);
        arrayPointsDemo.add(location35);
        arrayPointsDemo.add(location36);
        arrayPointsDemo.add(location37);
        arrayPointsDemo.add(location38);
        arrayPointsDemo.add(location39);
        arrayPointsDemo.add(location40);
        arrayPointsDemo.add(location41);

        Double areadlll = SphericalUtil.computeArea(arrayPointsDemo);

        Log.v("areaaaa",areadlll+"");*/

        db = new DBAdapter(getActivity());
        db.open();
        ly = (LinearLayout) view.findViewById(R.id.linearLayout);
        ll_nutrition = (LinearLayout) view.findViewById(R.id.nutrition_ll);
        farmName = (EditText) view.findViewById(R.id.farmName);
        farmerName = (EditText) view.findViewById(R.id.farmerName);
        farmerNumber = (EditText) view.findViewById(R.id.farmerNumber);

        farmerName.setText(AppConstant.visible_Name);
        farmerNumber.setText(AppConstant.mobile_no);

        farmArea = (EditText) view.findViewById(R.id.farm_area);
        cropSpiner1 = (Spinner) view.findViewById(R.id.crop);
        varietySpiner = (EditText) view.findViewById(R.id.variety);
        sowPeriodFrom = (EditText) view.findViewById(R.id.editTextShowPeriodFrom);
        sowPeriodTo = (EditText) view.findViewById(R.id.editTextShowPeriodTo);
        baselDoseSpiner = (Spinner) view.findViewById(R.id.basalDose);
        valueN = (EditText) view.findViewById(R.id.baselDoseTableN);
        valueP = (EditText) view.findViewById(R.id.baselDoseTableP);
        valueK = (EditText) view.findViewById(R.id.baselDoseTableK);
        yourConcernSpiner = (Spinner) view.findViewById(R.id.yourConcern);
        otherNutrition = (EditText) view.findViewById(R.id.nutrition);
        submitForm = (Button) view.findViewById(R.id.submit);
        crop1 = (Button) view.findViewById(R.id.crop1);
        crop2 = (Button) view.findViewById(R.id.crop2);
        crop3 = (Button) view.findViewById(R.id.crop3);
        crop4 = (Button) view.findViewById(R.id.crop4);

        additionBTN = (TextView) view.findViewById(R.id.additional_info);

        additionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  additionalAlert();
                Log.v("addtional_click", "click_outside");
                goFarMoreInfo();
            }
        });

        plotArea = (EditText) view.findViewById(R.id.plot_area);
        if (area != null) {
            plotArea.setText(area);
        }

        emailBTN = (TextView) view.findViewById(R.id.emailJson);

        emailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filename = "ggrc.txt";
                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
                Uri path = Uri.fromFile(filelocation);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
                emailIntent.setType("vnd.android.cursor.dir/email");
                String to[] = {"vishal.tripathi@iembsys.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Failed Json file");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayShowTitleEnabled(false); //Make the default lable invisible
        farmInformationData = new FarmInformationData();
        allFarmDetail = new AllFarmDetail();
        if (selectedFarmName != null && selectedFarmName.length() > 0) {
            Cursor getFarmByFarmName = db.getFarmByFarmName(selectedFarmName);
            if (getFarmByFarmName.getCount() > 0) {
                getFarmByFarmName.moveToFirst();
                allFarmDetail.setFarmId(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.FARM_ID)));
                allFarmDetail.setFarmName(selectedFarmName);
                farmName.setText(selectedFarmName);
                farmName.setEnabled(false);
                allFarmDetail.setUserId(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.USER_ID)));
                allFarmDetail.setFarmerName(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.FARMER_NAME)));
                allFarmDetail.setFarmerPhone(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.FARMER_PHONE)));
                allFarmDetail.setActualFarmArea(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.FARM_AREA)));

                allFarmDetail.setSowingDate(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.SOWING_DATE)));
                allFarmDetail.setSeedVariety(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.SEED_VARIETY)));
                allFarmDetail.setLastCropSown(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.LAST_CROP_SOWN)));
                allFarmDetail.setIrrigationSource(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.IRRIGATION_SOURCE)));
                allFarmDetail.setIrrigationPattern(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.IRRIGATION_PATTERN)));


                allFarmDetail.setArea(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.AREA)));

                farmerName.setText(allFarmDetail.getFarmerName());
                farmerNumber.setText(allFarmDetail.getFarmerPhone());
                farmArea.setText(allFarmDetail.getActualFarmArea());

                sowDateStr = allFarmDetail.getSowingDate();
                seedVarietyStr = allFarmDetail.getSeedVariety();
                lastCropSownStr = allFarmDetail.getLastCropSown();
                irriSourceStr = allFarmDetail.getIrrigationSource();
                irriPatternStr = allFarmDetail.getIrrigationPattern();

                if (area != null) {
                    plotArea.setText(area);
                } else {
                    plotArea.setText(allFarmDetail.getArea());
                }


                allFarmDetail.setContour(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.CONTOUR)));
                allFarmDetail.setState(getFarmByFarmName.getString(getFarmByFarmName.getColumnIndex(DBAdapter.STATE_ID)));

            }
        } else {
            farmName.setEnabled(true);
        }

//        checkValue(); //loading crop list in spinner view


        System.out.println("State id for crop list : " + AppConstant.stateID);
        allCrop = db.getCropByState(AppConstant.stateID);
        final int allCropCount = allCrop.getCount();
        String[] cropStringArray = new String[allCropCount + 1];
        cropStringArray[0] = "Select Crop";
        if (allCropCount > 0) {
            allCrop.moveToFirst();
            for (int i = 1; i <= allCropCount; i++) {
                cropStringArray[i] = allCrop.getString(allCrop.getColumnIndex(DBAdapter.CROP));
                allCrop.moveToNext();
            }
        }

        ArrayAdapter<String> cropArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cropStringArray); //selected item will look like a spinner set from XML
        cropArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropSpiner1.setAdapter(cropArrayAdapter);
        cropSpiner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cropId = "-1";
                String cropName = "";
                System.out.println("Inside CropId selection " + position);
                if (position > 0) {
                    allCrop.moveToPosition(position - 1);
                    cropId = allCrop.getString(allCrop.getColumnIndex(DBAdapter.CROP_ID));
                    cropName = allCrop.getString(allCrop.getColumnIndex(DBAdapter.CROP));
                }
                farmInformationData.setCrop(cropName);
                farmInformationData.setCropID(cropId);
                //setVariety(AppConstant.stateID, cropId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cropQueryData = new ArrayList<>();
        switch (callingActivity) {
            case AppConstant.HomeActivity:
                if (selectedFarmName != null) {
                    Cursor getSelectedFarmsValue = db.getSelectedFarmsValue(selectedFarmName);
                    if (getSelectedFarmsValue.getCount() > 0) {
                        getSelectedFarmsValue.moveToFirst();
                        String farmId = getSelectedFarmsValue.getString(getSelectedFarmsValue.getColumnIndex(DBAdapter.FARM_ID));
                        stateId = getSelectedFarmsValue.getString(getSelectedFarmsValue.getColumnIndex(DBAdapter.STATE_ID));
                        String contour = getSelectedFarmsValue.getString(getSelectedFarmsValue.getColumnIndex(DBAdapter.CONTOUR));
                        String[] latLng = contour.split("-")[0].split(",");
                        latitude = latLng[0];
                        longitude = latLng[1];

                        Cursor getCropQuery = db.getCropQueryByFarmId(farmId);
                        if (getCropQuery.getCount() > 0) {
                            getCropQuery.moveToFirst();
                            do {
                                CropQueryData cropQuery = new CropQueryData(getCropQuery);
                                cropQueryData.add(cropQuery);
                            } while (getCropQuery.moveToNext());
                        }

                    }
                }

                break;
            case AppConstant.AddFarmMap: // this will call whaen you choose farm from the list
                String[] latLng = allDrawLatLngPoint.split("-")[0].split(",");

                if (allDrawLatLngPoint != null && latLng.length>1) {
                    latitude = latLng[0];
                    longitude = latLng[1];
                }

                stateId = AppConstant.stateID;
                break;
        }

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                disPlayDate();
            }

        };
        sowPeriodFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayDateInEditText = AppConstant.SOW_PERIOD_FROM;
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        sowPeriodTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                displayDateInEditText = AppConstant.SOW_PERIOD_TO;
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFarmDetail();
            }
        });



        applyBasalDose = new ArrayList<String>();
        applyBasalDose.add("Select");
        applyBasalDose.add("Yes");
        applyBasalDose.add("No");
        // data will come from database

        ArrayAdapter<String> baselDoseSpiner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, applyBasalDose);
        baselDoseSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.baselDoseSpiner.setAdapter(baselDoseSpiner);
        this.baselDoseSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    ly.setVisibility(View.GONE);
                    ll_nutrition.setVisibility(View.GONE);
                }
                if (position == 1) {  //yes condition
                    ll_nutrition.setVisibility(View.GONE);
                    ly.setVisibility(View.VISIBLE);

                }
                if (position == 2) //no condition
                {
                    ly.setVisibility(View.GONE);
                    ll_nutrition.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yourConcern = new ArrayList<String>();
        yourConcern.add("Select");
        yourConcern.add("Increase my revenue");
        yourConcern.add("Increase my yield");
        yourConcern.add("Get me better price for my produce");
        final ArrayAdapter<String> yourConcernSpiner1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, yourConcern);
        yourConcernSpiner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.yourConcernSpiner.setAdapter(yourConcernSpiner1);


        this.yourConcernSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position>0) {
                    AppConstant.CONCERN_ID = yourConcernSpiner.getSelectedItemPosition();
                }else {
                    AppConstant.CONCERN_ID = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


// below method will display all farm detail on the farminformation class of every edittext or spinner

        crop1.setOnClickListener(crop1ClickListner);
        crop2.setOnClickListener(crop2ClickListner);
        crop3.setOnClickListener(crop3ClickListner);
        crop4.setOnClickListener(crop4ClickListner);

        crop1.performClick();

        if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
            submitForm.setText("SAVE");

        } else {
            submitForm.setText("SUBMIT");
        }

//        displayDetailOfSelectedFarm(selectedFarmName, selectedCropQueryCount);

        return view;
    }


    /////////////////function decleared//////////////////// function decleared//////////////////////function decleared//////////////////////

    public void submitFarmDetail() {
        if (!isValidate()) {
            return;
        } else {


            switch (callingActivity) {
                case AppConstant.HomeActivity:
                    SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, Context.MODE_PRIVATE);
                    farmInformationData.setUserID(allFarmDetail.getUserId().toString());
                    farmInformationData.setFarmId(allFarmDetail.getFarmId().toString());
                    farmInformationData.setAllLatLngPoint(allFarmDetail.getContour().toString());
                    farmInformationData.setState(allFarmDetail.getState().toString());

                    break;
                case AppConstant.AddFarmMap: // this will call whaen you choose farm from the list
                    farmInformationData.setUserID(user_id);
                    farmInformationData.setFarmId("0");
                    farmInformationData.setAllLatLngPoint(allDrawLatLngPoint); // this is conture
                    farmInformationData.setState(AppConstant.stateID);
                    break;
            }

           /* farmInformationData.setCrop(cropSpiner1.getSelectedItem().toString());
            farmInformationData.setCropID(pickCropIdOrValue.get(farmInformationData.getCrop().toString()));*/

            String uservalue = farmName.getText().toString();
            uservalue = uservalue.replace("'", "");

            farmInformationData.setFarmName(uservalue);
            System.out.println("FARMER NAME : " + farmerName.getText().toString());
            farmInformationData.setFarmerName(farmerName.getText().toString());
            farmInformationData.setFarmerNumber(farmerNumber.getText().toString());

            farmInformationData.setSowingDate(sowDateStr);
            farmInformationData.setSeedVariety(seedVarietyStr);
            farmInformationData.setLastCropSown(lastCropSownStr);
            farmInformationData.setIrrigationSource(irriSourceStr);
            farmInformationData.setIrrigationPattern(irriPatternStr);

            farmInformationData.setActualFarmArea(farmArea.getText().toString());
            farmInformationData.setVariety(varietySpiner.getText().toString());
          //  farmInformationData.setYourCencern(String.valueOf(yourConcernSpiner.getSelectedItemPosition()));
           // AppConstant.CONCERN_ID = yourConcernSpiner.getSelectedItemPosition(); //this will decide which api will be called
            farmInformationData.setBasalDoseN(valueN.getText().toString());
            farmInformationData.setBasalDoseP(valueP.getText().toString());
            farmInformationData.setBasalDoseK(valueK.getText().toString());
            farmInformationData.setOtherNutrition(otherNutrition.getText().toString());/*
            if (baselDoseSpiner.getSelectedItem().toString().trim() == "Yes") {
                farmInformationData.setOtherNutrition("0".toString());
                farmInformationData.setBasalDoseN(valueN.getText().toString());
                farmInformationData.setBasalDoseP(valueP.getText().toString());
                farmInformationData.setBasalDoseK(valueK.getText().toString());
                System.out.println();
            } else {
                farmInformationData.setBasalDoseN("0");
                farmInformationData.setBasalDoseP("0");
                farmInformationData.setBasalDoseK("0");
            }
            farmInformationData.setBesalDoseApply(baselDoseSpiner.getSelectedItem().toString());   ///apply condition yes/no/nothing*/
            farmInformationData.setBasalDoseN("0");
            farmInformationData.setBasalDoseP("0");
            farmInformationData.setBasalDoseK("0");
            farmInformationData.setBesalDoseApply("No");

            farmInformationData.setSowPeriodForm(sowPeriodFrom.getText().toString());
            farmInformationData.setSowPeriodTo(sowPeriodTo.getText().toString());
        }

        if (AppConstant.APP_MODE == AppConstant.OFFLINE) {
/*

            if (saveInitialFarm(DBAdapter.SAVE)) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
            return;
*/

            saveInitialFarm(DBAdapter.SAVE);
            loginAlert();

        } else {


            saveInitialFarm(DBAdapter.SAVE);

            Log.v("cropQueryData", "--" + cropQueryData.size());

            String jsonParameterString = createJsonParameterForSaveForm(farmInformationData, cropQueryData);

            creatString = AppManager.getInstance().removeSpaceForUrl(jsonParameterString);

            Log.v("Submit data", "--" + creatString);
            new sentRequestForFarmSave(creatString).execute();
        }

        /*Log.v("cropQueryData","--"+cropQueryData.size());

        String jsonParameterString = createJsonParameterForSaveForm(farmInformationData, cropQueryData);

        creatString = AppManager.getInstance().removeSpaceForUrl(jsonParameterString);




        if (isLogin) {
            new sentRequestForFarmSave(creatString).execute();
        } else {

       //     writeToSDFile(creatString);

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            String latLngStateId = latitude + "," + longitude + "," + stateId;
            Fragment fragment = FarmAdvisoryFragment.newInstance(cropQueryData, latLngStateId);
            ft.replace(R.id.fragmentContainer, fragment, "NewFragmentTag");
            ft.commit();



//                new sentFarmInformationData().execute();
        }*/

        /*} else {
            Toast.makeText(getActivity(), R.string.network_not_available, Toast.LENGTH_LONG).show();
        }*/


    }


    private class sentRequestForFarmSave extends AsyncTask<Void, Void, String> {

        String result = null;
        String createdString;

        public sentRequestForFarmSave(String createdString) {
            this.createdString = createdString;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Processing . . ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            String sendPath = AppManager.getInstance().saveFarmInfo;
            Cursor isUserSaved = db.isUserSaved(AppConstant.user_id);

            System.out.println("Save URL : " + sendPath + " Is user saved : " + isUserSaved.getCount() + " For Userid : " + AppConstant.user_id);

            if (isUserSaved.getCount() > 0) {

                //   writeToSDFile( data);

                String result = saveUser(isUserSaved);

                if (result.contains("OK")) {
                    farmInformationData.setUserID(AppConstant.user_id);

                    String jsonParameterString = createJsonParameterForSaveForm(farmInformationData, cropQueryData);
                    createdString = AppManager.getInstance().removeSpaceForUrl(jsonParameterString);

                    Log.v("vishal", "--" + creatString);
                    Log.v("tripathi", "---" + jsonParameterString);
                    data = createdString;

                    response = AppManager.getInstance().httpRequestPutMethod(sendPath, createdString);
                } else {
                    return AppConstant.SERVER_CONNECTION_ERROR;
                }
            } else {
                response = AppManager.getInstance().httpRequestPutMethod(sendPath, createdString);
            }

            System.out.println("Save Response :---" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();
            progressDialog.dismiss();

            Log.v("response_saved_farm", response.toString() + "");

            if (response != null) {
                if (response.contains("Success")) {
                    response = response.replace("\"", "");
                    String[] resArray = response.split(":");
                    if (resArray.length > 0) {
                        int i = 0;
                        do {
                            if (resArray[i].contains("Success")) {
                                if (resArray.length > i + 1) {
                                    String newFormId = resArray[i + 1];
                                    farmInformationData.setFarmId(newFormId);
                                    break;
                                }
                            }
                            i++;
                        } while (i < resArray.length);
                    }
                    saveInitialFarm(DBAdapter.SENT);
                    //   Toast.makeText(getActivity(), "Farm save successfully", Toast.LENGTH_LONG).show();
                    if (AppManager.isOnline(getActivity())) {
//                    new sentFarmInformationData().execute();
                     /*   final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        String latLngStateId = latitude + "," + longitude + "," + stateId;
                        Fragment fragment = FarmAdvisoryFragment.newInstance(cropQueryData, latLngStateId);
                        ft.replace(R.id.fragmentContainer, fragment, "NewFragmentTag");
                        ft.commit();*/

                        Intent in = new Intent(getActivity(), NavigationDrawerActivity.class);
                        in.putExtra("data", "allfarm");
                        startActivity(in);
                        getActivity().finish();

                    } else {
                        Toast.makeText(getActivity(), "network not available", Toast.LENGTH_LONG).show();
                    }
                }
                if (response.contains("NotSave")) {
                    saveInitialFarm(DBAdapter.SENT);
                    Toast.makeText(getActivity(), "Server Rejected the Request", Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.contains("Error")) {
                    saveInitialFarm(DBAdapter.SENT);
                    Toast.makeText(getActivity(), "Farm already exist!", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(getActivity(), NavigationDrawerActivity.class);
                    in.putExtra("data", "allfarm");
                    startActivity(in);
                    getActivity().finish();
                    return;
                }
                if (response.contains(AppConstant.SERVER_CONNECTION_ERROR)) {
                    saveInitialFarm(DBAdapter.SAVE);

                    //

                    //       writeToSDFile( data);
                    // Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_LONG).show();
                    savingAlert();
                    return;
                }
            } else {
                //          writeToSDFile( data);
                saveInitialFarm(DBAdapter.SAVE);
                savingAlert();
                // Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_LONG).show();
            }


        }
    }

   /* private void writeToSDFile(String data){

        File root = android.os.Environment.getExternalStorageDirectory();

        try {
            File myFile = new File (root.getAbsolutePath() + "/ggrc.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            Log.v("jsonData","--"+data);
            myOutWriter.append("New JSON request added from here:---   "+data);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(getActivity(), "Done writing SD 'ggrc.txt'", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/


    private boolean saveInitialFarm(String sendingStatus) {
        boolean isSave = false;

        {
            String maxLat = "";
            String maxLon = "";
            String minLat = "";
            String minLon = "";
            String centerLat = "";
            String centerLon = "";


            String[] allPoints = allDrawLatLngPoint.split("-");
            if (allPoints.length > 0) {
                for (int i = 0; i < allPoints.length; i++) {
                    String[] latlng = allPoints[i].split(",");
                    if (i == 1) {
                        maxLat = latlng[0];
                        maxLon = latlng[1];
                    }
                    int middle = allPoints.length / 2;
                    if (i == middle) {
                        centerLat = latlng[0];
                        centerLon = latlng[1];
                    }
                    int last = allPoints.length - 1;
                    if (i == last) {
                        minLat = latlng[0];
                        minLon = latlng[1];
                    }
                }
            }
            Cursor farmAllreadyExist = db.isFarmAlreadyExist(farmInformationData.getFarmName(), AppConstant.user_id);

            String farmId = "";
            if (farmInformationData.getFarmId() != null && farmInformationData.getFarmId().trim().length() > 0) {
//                farmAllreadyExist = db.isFarmAlreadyExist(farmInformationData.getFarmId());
                farmId = farmInformationData.getFarmId();
            } else {
                farmId = String.valueOf(System.currentTimeMillis() / 1000);
            }
            ContentValues initialValues = new ContentValues();
            initialValues.put(DBAdapter.FARM_ID, farmId);
            initialValues.put(DBAdapter.FARM_NAME, farmInformationData.getFarmName());
            initialValues.put(DBAdapter.FARMER_NAME, farmInformationData.getFarmerName());
            initialValues.put(DBAdapter.FARMER_PHONE, farmInformationData.getFarmerNumber());
            initialValues.put(DBAdapter.FARM_AREA, farmInformationData.getActualFarmArea());
            System.out.println("Saved userId : " + AppConstant.user_id);
            initialValues.put(DBAdapter.USER_ID, AppConstant.user_id);
            initialValues.put(DBAdapter.CONTOUR, farmInformationData.getAllLatLngPoint());
            initialValues.put(DBAdapter.STATE_ID, AppConstant.stateID);
            initialValues.put(DBAdapter.CONCERN, farmInformationData.getYourCencern());
            initialValues.put(DBAdapter.AREA, area);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            initialValues.put(DBAdapter.LOG_DATE, sdf.format(date));
            initialValues.put(DBAdapter.MAX_LAT, maxLat);
            initialValues.put(DBAdapter.MAX_LON, maxLon);
            initialValues.put(DBAdapter.MIN_LAT, minLat);
            initialValues.put(DBAdapter.MIN_LON, minLon);
            initialValues.put(DBAdapter.CENTRE_LAT, centerLat);
            initialValues.put(DBAdapter.CENTRE_LON, centerLon);
            initialValues.put(DBAdapter.SOWING_DATE, sowDateStr);
            initialValues.put(DBAdapter.SEED_VARIETY, seedVarietyStr);
            initialValues.put(DBAdapter.LAST_CROP_SOWN, lastCropSownStr);
            initialValues.put(DBAdapter.IRRIGATION_SOURCE, irriSourceStr);
            initialValues.put(DBAdapter.IRRIGATION_PATTERN, irriPatternStr);
            initialValues.put(DBAdapter.SENDING_STATUS, sendingStatus);
            long k = -1;
            if (farmAllreadyExist.getCount() > 0) {
                k = db.db.update(DBAdapter.DATABASE_TABLE_ALL_FARM_DETAIL, initialValues, DBAdapter.FARM_NAME + "='" + farmInformationData.getFarmName() + "' AND " + DBAdapter.USER_ID + " ='" + AppConstant.user_id + "'", null);
                System.out.println("Farm updated + " + k);
            } else {
                k = db.db.insert(DBAdapter.DATABASE_TABLE_ALL_FARM_DETAIL, null, initialValues);
                System.out.println("Farm inserted + " + k);
            }
            System.out.println("farm detail saved : " + k);
            Cursor getCropQueryByFarmId = db.getCropQueryByFarmId(farmId);
            if (getCropQueryByFarmId.getCount() > 0) {
                getCropQueryByFarmId.moveToFirst();
                int i = 0;
                do {
                    String id = getCropQueryByFarmId.getString(getCropQueryByFarmId.getColumnIndex(DBAdapter.ID));
                    if (cropQueryData.size() > i) {
                        cropQueryData.get(i).setDbId(id);
                    }
                    i++;
                } while (getCropQueryByFarmId.moveToNext());
            }
            for (CropQueryData data : cropQueryData) {
                data.setFarmId(farmId);


                long j = data.insert(db, sendingStatus);

                System.out.println("cropData inserted : " + j);
            }


            if (k != -1) {
                Toast.makeText(getActivity(), "Farm has been saved successfully", Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(getActivity(), "Farm not saved", Toast.LENGTH_LONG).show();
            }
//            return;
        }

        return isSave;
    }


    private String createJsonParameterForSaveForm(FarmInformationData farmData, ArrayList<CropQueryData> cropQueryData) {
        String parameterString = "";
        if (isLogin) {
            JSONArray cropInfo = new JSONArray();

            for (CropQueryData data : cropQueryData) {
                try {

                    JSONObject cropInfoJsonObject = new JSONObject();
                    cropInfoJsonObject.put("CropID", data.getCropID());
                    cropInfoJsonObject.put("CropName", data.getCrop());
                    cropInfoJsonObject.put("Variety", data.getVariety());
                    cropInfoJsonObject.put("BasalDoseApply", data.getBesalDoseApply());
                    cropInfoJsonObject.put("N", data.getBasalDoseN());
                    cropInfoJsonObject.put("P", data.getBasalDoseP());
                    cropInfoJsonObject.put("K", data.getBasalDoseK());
                    cropInfoJsonObject.put("SowDate", data.getSowPeriodForm());
                    cropInfoJsonObject.put("CropFrom", data.getSowPeriodForm());
                    cropInfoJsonObject.put("CropTo", data.getSowPeriodTo());
                    cropInfoJsonObject.put("OtherNutrient", data.getOtherNutrition());

                    cropInfo.put(cropInfoJsonObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            JSONObject finalJson = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            try {
//                jsonObject.put("UserID", farmData.getUserID());
                jsonObject.put("FarmID", farmData.getFarmId());
                jsonObject.put("FarmName", farmData.getFarmName());
                jsonObject.put("FarmerName", farmData.getFarmerName());
                jsonObject.put("PhoneNo", farmData.getFarmerNumber());
                jsonObject.put("FarmArea", farmData.getActualFarmArea());
                jsonObject.put("Contour", farmData.getAllLatLngPoint());
                jsonObject.put("CropID", farmData.getCropID());
                jsonObject.put("State", farmData.getState());
                jsonObject.put("CropInfo", cropInfo);
                jsonObject.put("Concerns", farmData.getYourCencern());
                jsonObject.put("Area", area);
                jsonObject.put("SeedVariety", farmData.getSeedVariety());
                jsonObject.put("LastCropSown", farmData.getLastCropSown());
                jsonObject.put("SowingDate", farmData.getSowingDate());
                jsonObject.put("IrrigationSource", farmData.getIrrigationSource());
                jsonObject.put("IrrigationPattern", farmData.getIrrigationPattern());

                System.out.println("Sended Area : " + area);
                finalJson.put("UserID", farmData.getUserID());
                finalJson.put("FarmInfo", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //      jsonObject.put("guarderiasIdGuarderias",jsonObject2);


            parameterString = finalJson.toString();

        }
        String replaceString = "\"";
        return parameterString;
    }


    public String createdStringforYieldInprovemnt() {
        String str =

                AppConstant.latitude + "/" + AppConstant.longitude + "/" +
                        farmInformationData.getBasalDoseN() + "/" +
                        farmInformationData.getBasalDoseP() + "/" +
                        farmInformationData.getBasalDoseK() + "/" +
                        farmInformationData.getCropID() + "/" +
                        farmInformationData.getVariety() + "/" +
                        farmInformationData.getSowPeriodTo() + "/" +
                        farmInformationData.getState();

        return str;

    }

    public boolean isValidate() {

        if (!(farmName.getText().toString().length() > 0)) {
            Toast.makeText(getActivity(), "Enter Farm Name", Toast.LENGTH_LONG).show();
            return false;
        }

/*

        CropQueryData data = null;
        if(selectedCropQueryCount==-1){
            data = new CropQueryData();
        }else{
//            if(selectedCropQueryCount<cropQueryData.size())
                data = cropQueryData.get(selectedCropQueryCount);
        }
*/


        // Remove Some validation due to client demand 21-03-18(Ankit Ne bola tha)


        if (!(cropSpiner1.getSelectedItem().toString().trim() != "Select Crop")) {
            /*Toast.makeText(getActivity(), "Please Select Crop", Toast.LENGTH_LONG).show();
            return false;*/
        } else {
            String cropName = cropSpiner1.getSelectedItem().toString().trim();
            selectedCropQueryCount.setCrop(cropName);
            selectedCropQueryCount.setCropID(db.getCropIdByName(cropName));
        }
        if ((varietySpiner.getText().toString().trim() == null || varietySpiner.getText().toString().trim().length()<1 )) {
           /* Toast.makeText(getActivity(), "Please Enter variety", Toast.LENGTH_LONG).show();
            return false;*/
        } else {
            String variety = varietySpiner.getText().toString().trim();
            selectedCropQueryCount.setVariety(variety);
        }

        if (!(sowPeriodFrom.getText().toString().length() > 0 && sowPeriodTo.getText().toString().length() > 0)) {
           /* Toast.makeText(getActivity(), "Please Select Showing Duration", Toast.LENGTH_LONG).show();
            return false;*/
        } else {
            selectedCropQueryCount.setSowPeriodForm(sowPeriodFrom.getText().toString());
            selectedCropQueryCount.setSowPeriodTo(sowPeriodTo.getText().toString());
        }


        // Remove Some validation due to client demand 21-03-18(Ankit Ne bola tha)

       /* if (!(yourConcernSpiner.getSelectedItem().toString().trim() != "Select")) {
            Toast.makeText(getActivity(), "Select Your Concern", Toast.LENGTH_LONG).show();
            return false;
        } else {
            selectedCropQueryCount.setYourCencern(String.valueOf(yourConcernSpiner.getSelectedItemPosition()));
        }*/


       /* if (baselDoseSpiner.getSelectedItem().toString().trim() == "Yes") {
            if (!(valueN.getText().toString().length() > 0 && valueP.getText().toString().length() > 0 && valueK.getText().toString().length() > 0)) {
                Toast.makeText(getActivity(), "Fill Besal Dose Apply Field", Toast.LENGTH_LONG).show();
                return false;
            } else {
                selectedCropQueryCount.setBasalDoseN(valueN.getText().toString());
                selectedCropQueryCount.setBasalDoseP(valueP.getText().toString());
                selectedCropQueryCount.setBasalDoseK(valueK.getText().toString());
            }
            selectedCropQueryCount.setBesalDoseApply("1");
        } else if (baselDoseSpiner.getSelectedItem().toString().trim() == "No") {
            if (!(otherNutrition.getText().toString().length() > 0)) {
                Toast.makeText(getActivity(), "fill Nutrition", Toast.LENGTH_LONG).show();
                return false;
            } else {
                selectedCropQueryCount.setNutrient(otherNutrition.getText().toString());
            }
            selectedCropQueryCount.setBesalDoseApply("2");
        } else {
            Toast.makeText(getActivity(), "Please apply besal dose", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return true;
    }

    public void displayDetailOfSelectedFarm(String farmName, CropQueryData data) {
        System.out.println("Inside display form : " + (data != null) + " and " + (allDrawLatLngPoint == null));
        String name = farmName;
        if (allDrawLatLngPoint == null) {

            Cursor c = db.getSelectedFarmsValue(name);
            if (c.moveToFirst()) {
                if (c.getCount() > 0) {
                    boolean isValueExist = this.farmName.getText().toString() != null && this.farmName.getText().toString().length() > 0;
                    if (!isValueExist) {
                        do {

                            allFarmDetail.setFarmId(c.getString(c.getColumnIndex(DBAdapter.FARM_ID)));
                            String str = c.getString(c.getColumnIndex(DBAdapter.FARM_ID));
                            allFarmDetail.setFarmName(c.getString(c.getColumnIndex(DBAdapter.FARM_NAME)));
                            this.farmName.setText(allFarmDetail.getFarmName().toString());

                            allFarmDetail.setFarmerName(c.getString(c.getColumnIndex(DBAdapter.FARMER_NAME)));
                            allFarmDetail.setFarmerPhone(c.getString(c.getColumnIndex(DBAdapter.FARMER_PHONE)));
                            allFarmDetail.setActualFarmArea(c.getString(c.getColumnIndex(DBAdapter.FARM_AREA)));
                            this.farmerName.setText(allFarmDetail.getFarmerName().toString());
                            this.farmerNumber.setText(allFarmDetail.getFarmerPhone().toString());

                            sowDateStr = allFarmDetail.getSowingDate();
                            seedVarietyStr = allFarmDetail.getSeedVariety();
                            lastCropSownStr = allFarmDetail.getLastCropSown();
                            irriSourceStr = allFarmDetail.getIrrigationSource();
                            irriPatternStr = allFarmDetail.getIrrigationPattern();

                            allFarmDetail.setSowingDate(c.getString(c.getColumnIndex(DBAdapter.SOWING_DATE)));
                            allFarmDetail.setSeedVariety(c.getString(c.getColumnIndex(DBAdapter.SEED_VARIETY)));
                            allFarmDetail.setLastCropSown(c.getString(c.getColumnIndex(DBAdapter.LAST_CROP_SOWN)));
                            allFarmDetail.setIrrigationSource(c.getString(c.getColumnIndex(DBAdapter.IRRIGATION_SOURCE)));
                            allFarmDetail.setIrrigationPattern(c.getString(c.getColumnIndex(DBAdapter.IRRIGATION_PATTERN)));


                            this.farmArea.setText(allFarmDetail.getActualFarmArea().toString());

                            if (area != null) {
                                plotArea.setText(area);
                            } else {

                                this.plotArea.setText(allFarmDetail.getArea().toString());
                            }

                            allFarmDetail.setUserId(AppConstant.user_id);
                            allFarmDetail.setContour(c.getString(c.getColumnIndex(DBAdapter.CONTOUR)));
                            allFarmDetail.setState(c.getString(c.getColumnIndex(DBAdapter.STATE_ID)));
                            allFarmDetail.setConcern(c.getString(c.getColumnIndex(DBAdapter.CONCERN)));
                            int concernLocation = 0;
                            try {
                                concernLocation = Integer.parseInt(data.getYourCencern());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                      //      this.yourConcernSpiner.setSelection(concernLocation);


                        }
                        while (c.moveToNext());
                    }

                }
            }
        }
        if (data != null) {
            System.out.println("data.getSowPeriodForm() : " + data.getSowPeriodForm());
            sowPeriodFrom.setText(data.getSowPeriodForm());
            System.out.println("data.getSowPeriodTo() : " + data.getSowPeriodTo());
            sowPeriodTo.setText(data.getSowPeriodTo());
            System.out.println("data.getCropID() : " + data.getCropID() + (data.getCropID() != null && (!data.getCropID().equals("-1"))));

            /*String variety = data.getVariety();
            if(variety.trim().length()>0){
                isVarietySelection = true;
            }else{
                isVarietySelection = false;
            }*/

            if (data.getCropID() != null && (!data.getCropID().equals("-1"))) {
                int cropCount = allCrop.getCount();
                allCrop.moveToFirst();
                for (int d = 1; d <= cropCount; d++) {
                    if (data.getCropID().trim().equals(allCrop.getString(allCrop.getColumnIndex(DBAdapter.CROP_ID)))) {
                        cropSpiner1.setSelection(d, true);
                        System.out.println("data.getVariety() : " + data.getVariety());
                        if (data.getVariety() != null && data.getVariety().trim().length() > 0) {
                            varietySpiner.setText(data.getVariety().toString().trim());
                        }
                        break;
                    }
                    allCrop.moveToNext();
                }
            } else {
                cropSpiner1.setSelection(0);
            }

           /* System.out.println("data.getBesalDoseApply() : " + data.getBesalDoseApply());
            this.baselDoseSpiner.setSelection(applyBasalDose.indexOf(data.getBesalDoseApply()));
            if (data.getBasalDoseN() != null && (!data.getBasalDoseN().equals("0"))) {
                this.baselDoseSpiner.setSelection(applyBasalDose.indexOf("Yes"));
            }
            if (data.getBesalDoseApply().trim().equalsIgnoreCase("2")) {
                valueN.setText("0");
                valueP.setText("0");
                valueK.setText("0");
            } else {
                System.out.println("data.getBasalDoseN() : " + data.getBasalDoseN());
                valueN.setText(data.getBasalDoseN());
                System.out.println("data.getBasalDoseP() : " + data.getBasalDoseP());
                valueP.setText(data.getBasalDoseP());
                System.out.println("data.getBasalDoseK() : " + data.getBasalDoseK());
                valueK.setText(data.getBasalDoseK().toString());


            }*/
            System.out.println("data.getOtherNutrition() : " + data.getOtherNutrition());
            otherNutrition.setText(data.getOtherNutrition());

//                    this.yourConcernSpiner.setSelection(yourConcern.indexOf(data.getYourCencern()));
        }


    }

 /*   Cursor varietyCursor;
    boolean isVarietySelection = false;

    private void setVariety(String stateId, String cropId) {

        varietyCursor = db.getVarietyByStateAndCrop(stateId, cropId);
        System.out.println("cropName inside set Variety  : " + cropId);

        final int varietyCount = varietyCursor.getCount();
        System.out.println("varietyCount  : " + varietyCount);

        String[] varietyStringArray = new String[varietyCount + 1];
        varietyStringArray[0] = "Select Variety";
        if (varietyCount > 0) {
            varietyCursor.moveToFirst();
            for (int i = 1; i <= varietyCount; i++) {
                varietyStringArray[i] = varietyCursor.getString(varietyCursor.getColumnIndex(DBAdapter.VARIETY));
                varietyCursor.moveToNext();
            }
        }

        ArrayAdapter<String> varietyArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, varietyStringArray); //selected item will look like a spinner set from XML
        varietyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        varietySpiner.setAdapter(varietyArrayAdapter);
        varietySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String varietyName = "-1";
                if (position > 0) {
                    varietyCursor.moveToPosition(position - 1);
                    varietyName = varietyCursor.getString(varietyCursor.getColumnIndex(DBAdapter.VARIETY));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (isVarietySelection) {
            isVarietySelection = false;
            int terCount = varietyCursor.getCount();
            System.out.println("variety : " + selectedCropQueryCount.getVariety() + " varietyCount : " + terCount);
            if (terCount > 0 && (selectedCropQueryCount.getVariety().trim().length() > 0)) {
                varietyCursor.moveToFirst();
                for (int d = 1; d <= terCount; d++) {
                    System.out.println(d + ":" + varietyCursor.getString(varietyCursor.getColumnIndex(DBAdapter.VARIETY)));
                    if (selectedCropQueryCount.getVariety().trim().equals(varietyCursor.getString(varietyCursor.getColumnIndex(DBAdapter.VARIETY)))) {
                        varietySpiner.setSelection(d, true);
                        break;
                    }
                    varietyCursor.moveToNext();
                }
            }
        }

    }
*/

    public void loadCropSpinner() {
        ArrayAdapter<String> cropSpiner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cropValue);
        cropSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.cropSpiner1.setAdapter(cropSpiner);

        this.cropSpiner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = cropSpiner1.getSelectedItem().toString();

                varietyValue = new ArrayList<String>();


                Cursor c = db.getVariety(item);
                int x = c.getCount();
//
                Log.d("getcount:----", "curserValue--" + x);
                if (c.moveToFirst()) {
                    if (c.getCount() > 0) {

                        do {
                            String fn = c.getString(0);
                            Log.d("FarmInformations:----", "String fn = c.getString(2).toString();" + fn);
                            if (!varietyValue.contains(fn)) {
                                varietyValue.add(fn);

                            }
                            Log.d("FarmInformations:----", "selectVariety--" + fn);
                        }
                        while (c.moveToNext());
                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private View.OnClickListener crop1ClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println("crop query length : " + cropQueryData.size());
            if (cropQueryData.size() > 0) {
                selectedCropQueryCount = cropQueryData.get(0);
            } else {
                selectedCropQueryCount = new CropQueryData();
                cropQueryData.add(selectedCropQueryCount);
            }

            crop1.setBackgroundResource(R.drawable.clicked_line);
            crop2.setBackgroundResource(R.drawable.line);
            crop3.setBackgroundResource(R.drawable.line);
            crop4.setBackgroundResource(R.drawable.line);
            displayDetailOfSelectedFarm(selectedFarmName, selectedCropQueryCount);
        }
    };

    private View.OnClickListener crop2ClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (isValidate()) {

                if (cropQueryData.size() > 1) {
                    selectedCropQueryCount = cropQueryData.get(1);
                } else {
                    selectedCropQueryCount = new CropQueryData();
                    cropQueryData.add(selectedCropQueryCount);
                }


//                selectedCropQueryCount = cropQueryData.get(1);
                crop1.setBackgroundResource(R.drawable.line);
                crop2.setBackgroundResource(R.drawable.clicked_line);
                crop3.setBackgroundResource(R.drawable.line);
                crop4.setBackgroundResource(R.drawable.line);
                displayDetailOfSelectedFarm(selectedFarmName, selectedCropQueryCount);
            }
        }
    };

    private View.OnClickListener crop3ClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (isValidate()) {

                if (cropQueryData.size() > 2) {
                    selectedCropQueryCount = cropQueryData.get(2);
                } else {
                    selectedCropQueryCount = new CropQueryData();
                    cropQueryData.add(selectedCropQueryCount);
                }


//                selectedCropQueryCount = cropQueryData.get(2);
                crop1.setBackgroundResource(R.drawable.line);
                crop2.setBackgroundResource(R.drawable.line);
                crop3.setBackgroundResource(R.drawable.clicked_line);
                crop4.setBackgroundResource(R.drawable.line);
                displayDetailOfSelectedFarm(selectedFarmName, selectedCropQueryCount);
            }
        }
    };

    private View.OnClickListener crop4ClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (isValidate()) {

                if (cropQueryData.size() > 3) {
                    selectedCropQueryCount = cropQueryData.get(3);
                } else {
                    selectedCropQueryCount = new CropQueryData();
                    cropQueryData.add(selectedCropQueryCount);
                }


//                selectedCropQueryCount = cropQueryData.get(3);
                crop1.setBackgroundResource(R.drawable.line);
                crop2.setBackgroundResource(R.drawable.line);
                crop3.setBackgroundResource(R.drawable.line);
                crop4.setBackgroundResource(R.drawable.clicked_line);
                displayDetailOfSelectedFarm(selectedFarmName, selectedCropQueryCount);
            }
        }
    };


    private void disPlayDate() {

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        switch (displayDateInEditText) {
            case AppConstant.SOW_PERIOD_FROM:
                sowPeriodFrom.setText(sdf.format(myCalendar.getTime()));
                break;
            case AppConstant.SOW_PERIOD_TO:
                sowPeriodTo.setText(sdf.format(myCalendar.getTime()));
                break;
        }

    }

    private void savingAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Farm Saved");
        builder.setMessage("Farm can not be sent because of Low Network.\n But farm has been saved.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void loginAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Not Login");
        builder.setMessage("You are not in offline mode.'\n' Please Login first and submit the farm.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    private String saveUser(Cursor cursor) {
        cursor.moveToFirst();
        String message = "Error";
        String id = cursor.getString(cursor.getColumnIndex(DBAdapter.ID));
        String mailId = cursor.getString(cursor.getColumnIndex(DBAdapter.EMAIL_ADDRESS));
        String userName = cursor.getString(cursor.getColumnIndex(DBAdapter.USER_NAME));
        String password = cursor.getString(cursor.getColumnIndex(DBAdapter.PASSWORD));
        String mobileNo = cursor.getString(cursor.getColumnIndex(DBAdapter.MOBILE_NO));
        String visibleName = cursor.getString(cursor.getColumnIndex(DBAdapter.VISIBLE_NAME));
        String createdDateTime = cursor.getString(cursor.getColumnIndex(DBAdapter.CREATED_DATE_TIME));
        String oldUserId = cursor.getString(cursor.getColumnIndex(DBAdapter.USER_ID));
        System.out.println("gotUser id : " + oldUserId);
        String completeStringForRegister = mailId + "/" + userName + "/" + password + "/" + visibleName + "/" + mobileNo + "/" + AppConstant.villageID + "/" + AppConstant.fatherName + "/" + AppConstant.revenueNo + "/" + AppConstant.gutNo + "/" + AppConstant.mobile_type;

        String response;


        try {
            ExternalStorageGPS.write_file("MFI_RESPONSE_LOG", true, AuthenticateService.format.format(AuthenticateService.cal().getTime()) + "---USER REGISTER STRING : " + "http://wwf.myfarminfo.com/yfirest.svc/Register/" + completeStringForRegister + " \n\r");
            response = CustomHttpClient.executeHttpPut("http://myfarminfo.com/yfirest.svc/registerUser_Jalna/" + completeStringForRegister);
            ExternalStorageGPS.write_file("MFI_RESPONSE_LOG", true, AuthenticateService.format.format(AuthenticateService.cal().getTime()) + "---USER REGISTER RESPONSE : " + response + " \n\r");
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
                values.put(DBAdapter.MOBILE_NO, mobileNo);
                values.put(DBAdapter.USER_ID, oldUserId);
                values.put(DBAdapter.CREATED_DATE_TIME, createdDateTime);
                values.put(DBAdapter.SENDING_STATUS, DBAdapter.SENT);

                db.db.update(DBAdapter.TABLE_CREDENTIAL, values, DBAdapter.ID + " = '" + id + "'", null);
                message = "Error";
            }
            response = response.trim();
           // response = response.substring(1, response.length() - 1);
            response = response.replace("\\", "");
            response = response.replace("\\", "");
            response = response.replace("\"{", "{");
            response = response.replace("}\"", "}");
            response = response.replace("\"[", "[");
            response = response.replace("]\"", "]");
            JSONArray jArray = new JSONArray(response);
            Log.d("afterfilterResponse", response);

            if (jArray.length() == 0) {
                message = "No Registered";
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
                values.put(DBAdapter.MOBILE_NO, mobileNo);
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

                message = "OK";
            }
        } catch (Exception e) {
//
            e.printStackTrace();
            Log.d("Status", "" + e);
            message = "Could not connect to server";
        }
        return message;
    }

    private void additionalAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add More Info.");
        builder.setMessage("Do you want to specify addition information?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();

                goFarMoreInfo();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    public void goFarMoreInfo() {

        Log.v("addtional_click", "click_inside");
        value = null;
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.additional_info, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(promptsView);


        final EditText showingDate = (EditText) promptsView.findViewById(R.id.addi_showingdate);
        final EditText seedVariety = (EditText) promptsView.findViewById(R.id.addi_seedvariety);
        final Spinner lastSown = (Spinner) promptsView.findViewById(R.id.addi_lastcrop_sown);


        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                showingDate.setText(sdf.format(myCalendar.getTime()));
            }

        };
        showingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final EditText irrigationSource = (EditText) promptsView.findViewById(R.id.addi_irrigation);
        Spinner patternSpinner = (Spinner) promptsView.findViewById(R.id.addi_pattern);
        Log.v("ldmls", "---" + AppConstant.stateID);
        final Cursor allCrop1 = db.getCropByState(AppConstant.stateID);
        final int allCropCount = allCrop1.getCount();
        Log.v("ldmls", allCropCount + "---" + AppConstant.stateID);
        String[] cropStringArray = new String[allCropCount + 1];
        cropStringArray[0] = "Select Last Crop sown";
        if (allCropCount > 0) {
            allCrop1.moveToFirst();
            for (int i = 1; i <= allCropCount; i++) {
                cropStringArray[i] = allCrop1.getString(allCrop1.getColumnIndex(DBAdapter.CROP));
                allCrop1.moveToNext();
            }
        }

        ArrayAdapter<String> cropArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cropStringArray); //selected item will look like a spinner set from XML
        cropArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lastSown.setAdapter(cropArrayAdapter);
        lastSown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lastcropName = "";
                System.out.println("Inside CropId selection " + position);
                if (position > 0) {
                    allCrop1.moveToPosition(position - 1);
                    lastCropSownStr = allCrop1.getString(allCrop1.getColumnIndex(DBAdapter.CROP_ID));
                    lastcropName = allCrop1.getString(allCrop1.getColumnIndex(DBAdapter.CROP));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayList<String> patternValue = new ArrayList<String>();
        patternValue.add("Select Irrigation Pattern");
        patternValue.add("Drip");
        patternValue.add("Flood");


        ArrayAdapter<String> patterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, patternValue);
        // Drop down layout style - list view with radio button
        patterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patternSpinner.setAdapter(patterAdapter);

        Log.v("dslfkjsdkl", "elkfjskld0");

        if (sowDateStr != null) {
            showingDate.setText(sowDateStr);
        }
        if (seedVarietyStr != null) {
            seedVariety.setText(seedVarietyStr);
        }
        Log.v("dslfkjsdkl", "elkfjskld0");

        if (lastCropSownStr != null && (!lastCropSownStr.equals("-1"))) {
            int cropCount = allCrop1.getCount();
            allCrop1.moveToFirst();
            for (int d = 1; d <= cropCount; d++) {
                if (lastCropSownStr.equals(allCrop1.getString(allCrop1.getColumnIndex(DBAdapter.CROP_ID)))) {
                    lastSown.setSelection(d, true);

                    break;
                }
                allCrop.moveToNext();
            }
        } else {
            lastSown.setSelection(0);
        }

        if (irriSourceStr != null) {
            irrigationSource.setText(irriSourceStr);
        }
        if (irriPatternStr != null) {
            String compareValue = irriPatternStr;

            if (compareValue != null) {
                int spinnerPosition = patterAdapter.getPosition(compareValue);
                patternSpinner.setSelection(spinnerPosition);
            }
        }


        patternSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    value = patternValue.get(position);
                } else {
                    value = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String sowDate = showingDate.getText().toString().trim();
                String seedV = seedVariety.getText().toString().trim();

                String irrig_source = irrigationSource.getText().toString().trim();
              /*  if (sowDate == null || sowDate.length() < 1) {
                    Toast.makeText(getActivity(), "please enter sowing date", Toast.LENGTH_SHORT).show();
                } else if (seedV == null || seedV.length() < 1) {
                    Toast.makeText(getActivity(), "please enter Seed Variety", Toast.LENGTH_SHORT).show();
                } else if (lastSow == null || lastSow.length() < 1) {
                    Toast.makeText(getActivity(), "Please enter Last Crop Sown ", Toast.LENGTH_SHORT).show();
                } else if (irrig_source == null || irrig_source.length() < 1) {
                    Toast.makeText(getActivity(), "Please enter Irrigation Source", Toast.LENGTH_SHORT).show();
                } else if (value == null || value.length() < 1) {
                    Toast.makeText(getActivity(), "Please select irrigation pattern", Toast.LENGTH_SHORT).show();
                } else {*/


                sowDateStr = sowDate;
                seedVarietyStr = seedV;

                irriSourceStr = irrig_source;
                irriPatternStr = value;

                //  new SubmitAdditionalInfo(str).execute();
                //   }

            }
        });


        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }

        });
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.show();

        Log.v("dslfkjsdkl", "elkfjskld0");


   /* private class SubmitAdditionalInfo extends AsyncTask<Void, Void, String> {

        String result = null;
        String createdString;

        public SubmitAdditionalInfo(String createdString) {
            this.createdString = createdString;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending addition info... ");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            String sendPath = AppManager.getInstance().additionalInfo;
            response = AppManager.getInstance().httpRequestPutMethod(sendPath, createdString);


            System.out.println("additional Response :---" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPreExecute();
            progressDialog.dismiss();

            Log.v("response_addi", response.toString() + "");

            if (response != null) {
                if (response.contains("Success")) {
                    response = response.replace("\"", "");
                    Log.v("response_addi info", response.toString() + "");
                }

            } else {
                Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_LONG).show();
            }


        }
    }*/


    }
}
