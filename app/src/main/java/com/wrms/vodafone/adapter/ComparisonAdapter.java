package com.wrms.vodafone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.ComparisonBean;

import java.util.ArrayList;

/**
 * Created by Admin on 28-09-2017.
 */
public class ComparisonAdapter extends RecyclerView.Adapter<ComparisonAdapter.ViewHolder>{
private ArrayList<ComparisonBean>mDataset=new ArrayList<ComparisonBean>();

public Context mContext;
        String imageString;
        int totalAmount=0;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case

    TextView date, minAct, minFor, maxAct, maxFor, rainAct, rainFor;


    public ViewHolder(View v) {
        super(v);
        date = (TextView) v.findViewById(R.id.date_comp);
        minAct = (TextView) v.findViewById(R.id.min_temp_act);
        minFor = (TextView) v.findViewById(R.id.min_temp_for);
        maxAct = (TextView) v.findViewById(R.id.max_temp_act);
        maxFor = (TextView) v.findViewById(R.id.max_temp_for);
        rainAct = (TextView) v.findViewById(R.id.rain_act);
        rainFor = (TextView) v.findViewById(R.id.rain_for);


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
    public ComparisonAdapter(Context con, ArrayList<ComparisonBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comparison_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.date.setText(mDataset.get(position).getDate());
        holder.minAct.setText(mDataset.get(position).getMinAct());
        holder.minFor.setText(mDataset.get(position).getMinFor());
        holder.maxAct.setText(mDataset.get(position).getMaxAct());
        holder.maxFor.setText(mDataset.get(position).getMaxFor());
        holder.rainAct.setText(mDataset.get(position).getRainAct());
        holder.rainFor.setText(mDataset.get(position).getRainFor());



    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}