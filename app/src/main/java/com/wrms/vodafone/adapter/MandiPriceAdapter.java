package com.wrms.vodafone.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.MandiPriceBean;

import java.util.ArrayList;

/**
 * Created by Admin on 27-07-2017.
 */
public class MandiPriceAdapter  extends  RecyclerView.Adapter<MandiPriceAdapter.ViewHolder> {
    private ArrayList<MandiPriceBean> mDataset = new ArrayList<MandiPriceBean>();

    public Context mContext;
    String imageString;
    int totalAmount=0;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView commodity,variety,price,date;





        public ViewHolder(View v) {
            super(v);
            commodity = (TextView) v.findViewById(R.id.comm);
            variety = (TextView) v.findViewById(R.id.vari);
            price = (TextView) v.findViewById(R.id.price);
            date = (TextView) v.findViewById(R.id.date);




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
    public MandiPriceAdapter(Context con, ArrayList<MandiPriceBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mandi_price_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.commodity.setText(mDataset.get(position).getCommodity());
        holder.variety.setText(mDataset.get(position).getVariety());
        holder.price.setText(mDataset.get(position).getPrice());
        holder.date.setText(mDataset.get(position).getDate());

    }

    private void exitMethod(final int pos, final String docID){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("EXIT").
                setMessage("Do you want to Delete this item?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        //   deleteDocument(docID);

                        remove(pos);
                    }
                }).
                setNegativeButton("NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}