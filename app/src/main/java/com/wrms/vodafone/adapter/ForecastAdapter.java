package com.wrms.vodafone.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.TempBean;

import java.util.ArrayList;

/**
 * Created by Admin on 30-08-2017.
 */
public class ForecastAdapter  extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private ArrayList<TempBean> mDataset = new ArrayList<TempBean>();

    public Context mContext;
    String imageString;
    TextToSpeech t1;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView day,date,minTemp,maxTemp,weatherText,humidity,wind;
        public ImageView imageView;



        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.forecast_date);
            day = (TextView) v.findViewById(R.id.forecast_day);

            minTemp = (TextView) v.findViewById(R.id.minTemp);
            maxTemp = (TextView) v.findViewById(R.id.maxTemp);
            weatherText = (TextView) v.findViewById(R.id.forecast_text);
            humidity = (TextView) v.findViewById(R.id.humidity);
            wind = (TextView) v.findViewById(R.id.wind);

            imageView = (ImageView)v.findViewById(R.id.forecast_image);

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
    public ForecastAdapter(Context con, ArrayList<TempBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_adapter, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.date.setText(mDataset.get(position).getDate());
        holder.day.setText(mDataset.get(position).getDay());
        holder.humidity.setText(mDataset.get(position).getHumidity());
        holder.minTemp.setText(mDataset.get(position).getMinTemp());
        holder.maxTemp.setText(mDataset.get(position).getMaxTemp());
        holder.weatherText.setText(mDataset.get(position).getWeatherText());
        holder.wind.setText(mDataset.get(position).getWindSpeed());

        if (mDataset.get(position).getImageType().equalsIgnoreCase("12")){
            holder.imageView.setImageResource(R.drawable.img_12);
        }else if (mDataset.get(position).getImageType().equalsIgnoreCase("35")){
            holder.imageView.setImageResource(R.drawable.img_35);
        }else if (mDataset.get(position).getImageType().equalsIgnoreCase("41")){
            holder.imageView.setImageResource(R.drawable.img_41);
        }else if (mDataset.get(position).getImageType().equalsIgnoreCase("36")){
            holder.imageView.setImageResource(R.drawable.img_36);
        }else {
            holder.imageView.setImageResource(R.drawable.img_36);
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}