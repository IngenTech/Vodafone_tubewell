package com.wrms.vodafone.adapter;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.WaterBean;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Admin on 11-10-2017.
 */
public class WMAdapter   extends RecyclerView.Adapter<WMAdapter.ViewHolder> {
    private ArrayList<WaterBean> mDataset = new ArrayList<WaterBean>();

    public Context mContext;
    String imageString;
    int totalAmount = 0;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView stage, s_w_requirement, exp_rain,w_needed;


        public ViewHolder(View v) {
            super(v);

            stage = (TextView) v.findViewById(R.id.stage);
            s_w_requirement = (TextView) v.findViewById(R.id.s_w_require);
            exp_rain = (TextView) v.findViewById(R.id.exp_rain);
            w_needed = (TextView) v.findViewById(R.id.w_needed);


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
    public WMAdapter(Context con, ArrayList<WaterBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.water_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.stage.setText(mDataset.get(position).getStage());
        holder.s_w_requirement.setText(mDataset.get(position).getStage_Requirement());
        holder.exp_rain.setText(mDataset.get(position).getExp_rain());
        holder.w_needed.setText(mDataset.get(position).getWater_needed());


    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}