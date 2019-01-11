package com.wrms.vodafone.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.FormerBean;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Admin on 25-08-2017.
 */
public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.ViewHolder> {
    private ArrayList<FormerBean> mDataset = new ArrayList<FormerBean>();

    public Context mContext;
    String imageString;
    TextToSpeech t1;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView formerName;
        public TextView formerLocation;




        public ViewHolder(View v) {
            super(v);
            formerName = (TextView) v.findViewById(R.id.fomer_name);
            formerLocation = (TextView)v.findViewById(R.id.former_location);

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
    public FarmerAdapter(Context con, ArrayList<FormerBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.former_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.formerName.setText(mDataset.get(position).getFormerName());
        holder.formerLocation.setText(mDataset.get(position).getFormerLocation());

        Log.v("slkas",mDataset.get(position)+"");

        t1=new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("hin"));
                }
            }
        });



    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    ProgressDialog dialog;
    SharedPreferences prefs;




}