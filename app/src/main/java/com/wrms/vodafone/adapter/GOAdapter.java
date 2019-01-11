package com.wrms.vodafone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.GOBean;

import java.util.ArrayList;

/**
 * Created by Admin on 28-08-2017.
 */
public class GOAdapter extends  RecyclerView.Adapter<GOAdapter.ViewHolder> {
    private ArrayList<GOBean> mDataset = new ArrayList<GOBean>();

    public Context mContext;
    String imageString;
    int totalAmount=0;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView date,minTemp,maxTemp,humidityMor,humidityEve,windSpeed,radiation,rain;




        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date_go);
            minTemp = (TextView) v.findViewById(R.id.go_min);
            maxTemp = (TextView) v.findViewById(R.id.go_max);
            humidityMor = (TextView) v.findViewById(R.id.go_hum_m);
            humidityEve = (TextView) v.findViewById(R.id.go_hum_e);
            windSpeed = (TextView) v.findViewById(R.id.go_wind);
            radiation = (TextView) v.findViewById(R.id.go_rad);
            rain = (TextView) v.findViewById(R.id.go_rain);




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
    public GOAdapter(Context con, ArrayList<GOBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.go_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.date.setText(mDataset.get(position).getDate());
        holder.minTemp.setText(mDataset.get(position).getMinTemp());
        holder.maxTemp.setText(mDataset.get(position).getMaxTemp());
        holder.humidityMor.setText(mDataset.get(position).getHumidityMor());
        holder.humidityEve.setText(mDataset.get(position).getHumidityEve());
        holder.windSpeed.setText(mDataset.get(position).getWindSpeed());
        holder.rain.setText(mDataset.get(position).getRain());
        holder.radiation.setText(mDataset.get(position).getSolarRadiation());




    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}