package com.wrms.vodafone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.MotorStartBean;
import com.wrms.vodafone.bean.MotorStatus;
import com.wrms.vodafone.bean.MotorStopBean;
import com.wrms.vodafone.bean.MotorStopSatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin on 08-04-2018.
 */
public class MotorStopAdapter  extends RecyclerView.Adapter<MotorStopAdapter.ViewHolder> {
    private ArrayList<MotorStatus> mDataset = new ArrayList<MotorStatus>();
    private ArrayList<MotorStopSatus> mDataset1 = new ArrayList<MotorStopSatus>();

    public Context mContext;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date,startTime,stopTime,duration;
        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            startTime = (TextView) v.findViewById(R.id.start_time);
            stopTime = (TextView) v.findViewById(R.id.stop_time);
            duration = (TextView) v.findViewById(R.id.duration);
        }
    }

      /*public void add(int position, String item) {
          mDataset.add(position, item);
          notifyItemInserted(position);
      }*/

    public void remove(int pos) {
        //   int position = mDataset.indexOf(item);
        mDataset.remove(pos);
        notifyItemRemoved(pos);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MotorStopAdapter(Context con, ArrayList<MotorStatus> myDataset,ArrayList<MotorStopSatus> myDataset1) {
        mDataset = myDataset;
        mDataset1 = myDataset1;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_pie_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        Log.v("lgfgdl",mDataset.size()+"");

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // holder.setIsRecyclable(false);

        if (mDataset1.size()>position) {

            holder.date.setText(mDataset.get(position).getDate());
            holder.startTime.setText(mDataset.get(position).getStartTime());
            holder.stopTime.setText(mDataset1.get(position).getStopTime());
            Log.v("kkkkkk", mDataset.get(position).getStartTime() + ""+mDataset1.get(position).getStopTime());
        }else {
            holder.date.setText(mDataset.get(position).getDate());
            holder.startTime.setText(mDataset.get(position).getStartTime());
            holder.stopTime.setText("");
        }

        String ss = holder.startTime.getText().toString();
        String ss1 = holder.stopTime.getText().toString();
        if ((ss!=null && ss.length()>5) && (ss1!=null && ss1.length()>5)) {
            Log.v("ppppp",position+"--"+ss+"--"+ss1);
            String aa = omriFunction(ss, ss1);

            holder.duration.setText(aa);
        }else {
            holder.duration.setText("");
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String omriFunction(String start,String stop){
        Date Start = null;
        Date End = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            Start = simpleDateFormat.parse(start);
            End = simpleDateFormat.parse(stop);}
        catch(ParseException e){
            //Some thing if its not working
        }

        long difference = End.getTime() - Start.getTime();
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        if(hours < 0){
            hours+=24;
        }if(min < 0){
            float  newone = (float)min/60 ;
            min +=60;
            hours =(int) (hours +newone);}
        String c = hours+":"+min;
        Log.d("ANSWER",c);

        return c;
    }

}