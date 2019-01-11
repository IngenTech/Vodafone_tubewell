package com.wrms.vodafone.mapfragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.wrms.vodafone.R;
import com.wrms.vodafone.adapter.MotorStopAdapter;
import com.wrms.vodafone.adapter.ShowPieDataAdapter;
import com.wrms.vodafone.adapter.StartDataAdapter;
import com.wrms.vodafone.bean.ElectricStatusBean;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin on 05-04-2018.
 */
public class StartStopFrag  extends Fragment implements OnChartGestureListener,
        OnChartValueSelectedListener {



    ArrayList<MotorStartBean> arrayListMotorStart = new ArrayList<MotorStartBean>();

    private LineChart mChart;

    TextView xAxisName,yAxisName;

    public StartStopFrag(ArrayList<MotorStartBean> list10) {
        // Required empty public constructor

        arrayListMotorStart = list10;





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.start_stop_lay, container, false);



        xAxisName = (TextView)view.findViewById(R.id.percent);
        yAxisName = (TextView)view.findViewById(R.id.year);

        xAxisName.setText("Value");
        yAxisName.setText("Time");


        mChart = (LineChart) view.findViewById(R.id.lineChart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        mChart.setVisibleXRangeMaximum(10);

        // add data


        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        LimitLine upper_limit = new LimitLine(47f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit = new LimitLine(0f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        // leftAxis.addLimitLine(upper_limit);
        // leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(1f);

        leftAxis.setAxisMinValue(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setGranularity(1f); // interval 1
        leftAxis.setLabelCount(2, true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        mChart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
       /* XAxis xAxis = mChart.getXAxis();

        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        xAxis.setLabelsToSkip(0);
*/

        //  dont forget to refresh the drawing
        mChart.invalidate();
        maxMethod();

        if (arrayListMotorStart.size()<1){
            noDataMethod();
        }

        return view;
    }

    private void noDataMethod(){

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("No Data").
                setMessage("No data available for the period submitted").
                setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void maxMethod() {


        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        LimitLine upper_limit = new LimitLine(47f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit = new LimitLine(0f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        // leftAxis.addLimitLine(upper_limit);
        // leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(1f);
        leftAxis.setAxisMinValue(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setGranularity(1f); // interval 1
        leftAxis.setLabelCount(2, true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        mChart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
       /* XAxis xAxis = mChart.getXAxis();

        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        xAxis.setLabelsToSkip(0);

*/
        //  dont forget to refresh the drawing
        mChart.invalidate();

        setDataMax1();


        mChart.clear();
        mChart.setVisibleXRangeMaximum(10);
        allDataMethod();

    }



    public void allDataMethod() {


        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        LimitLine upper_limit = new LimitLine(47f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit = new LimitLine(0f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        // leftAxis.addLimitLine(upper_limit);
        // leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(1f);
        leftAxis.setAxisMinValue(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setGranularity(1f); // interval 1
        leftAxis.setLabelCount(2, true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        mChart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

      /*  XAxis xAxis = mChart.getXAxis();

        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        xAxis.setLabelsToSkip(0);*/


        //  dont forget to refresh the drawing
        mChart.invalidate();

        setAllDataMaxMin();



    }



    @Override
    public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

    }

    @Override
    public void onChartScale(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }

    private ArrayList<String> setXAxisValuesMax1() {
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < arrayListMotorStart.size(); i++) {
            String a = arrayListMotorStart.get(i).getxValue();
            if (a!=null){
                xVals.add(a);
            }

        }




        return xVals;
    }

    private ArrayList<Entry> setYAxisValuesMax1() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < arrayListMotorStart.size(); i++) {
            String ss = arrayListMotorStart.get(i).getyValue();
            if (ss != null) {
                yVals.add(new Entry(Float.parseFloat(ss), i));
            }
        }

        return yVals;
    }

    private void setDataMax1() {
        ArrayList<String> xVals = setXAxisValuesMax1();

        ArrayList<Entry> yVals = setYAxisValuesMax1();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "");

        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        //   set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

    }







    private void setAllDataMaxMin() {
        ArrayList<String> xVals = setXAxisValuesMax1();

        ArrayList<Entry> yVals = setYAxisValuesMax1();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "");

        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        //   set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);



        // set data
        mChart.setData(data);

    }


  /*  public class MyXAxisValueFormatter implements XAxisValueFormatter {
        private SimpleDateFormat mFormat,mFormat1;

        public MyXAxisValueFormatter() {
            //    mFormat = new DecimalFormat("###,###,###,##0.0");
            mFormat = new SimpleDateFormat("dd-MM HH:mm");
            mFormat1 = new SimpleDateFormat("dd-MM-yy HH:mm");
        }
        @Override
        public String getXValue(String s, int i, ViewPortHandler viewPortHandler) {

            Log.v("ldldsl",s+"");

            Date date = null;
            try {
                date = mFormat1.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return mFormat.format(date)+"";

        }




    }
*/

}