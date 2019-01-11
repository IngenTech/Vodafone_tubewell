package com.wrms.vodafone.adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.wrms.vodafone.bean.DashFormerBean;
import com.wrms.vodafone.utils.Utility;

import java.util.ArrayList;

/**
 * Created by Admin on 28-08-2017.
 */
public class DashFarmerAdapter extends RecyclerView.Adapter<DashFarmerAdapter.ViewHolder> {
    private ArrayList<DashFormerBean> mDataset = new ArrayList<DashFormerBean>();

    public Context mContext;
    String imageString;
    int totalAmount = 0;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView farmerName, cluster, phone;


        public ViewHolder(View v) {
            super(v);
            farmerName = (TextView) v.findViewById(R.id.farmer_name);
            cluster = (TextView) v.findViewById(R.id.cluster);

            phone = (TextView) v.findViewById(R.id.phone);


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
    public DashFarmerAdapter(Context con, ArrayList<DashFormerBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.former_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.farmerName.setText(mDataset.get(position).getFormerName());
        holder.cluster.setText(mDataset.get(position).getCluster());
        String udata = mDataset.get(position).getPhoneNo();
        if (udata!=null) {
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            holder.phone.setText(content);
        }

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aa = mDataset.get(position).getPhoneNo();

                Log.v("sknkas",aa+"");
                if (aa != null && aa.length() > 9) {

                    callSmsMethod(aa);

                }else {
                    Toast.makeText(mContext,"Phone number not valid",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void callSmsMethod(final String number){

        final Dialog dialog = new Dialog(mContext);

        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.5f;

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        // Include dialog.xml file
        dialog.setContentView(R.layout.call_sms_popup);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout callBTN = (RelativeLayout)dialog.findViewById(R.id.call_btn);
        RelativeLayout smsBTN = (RelativeLayout)dialog.findViewById(R.id.sms_btn);

        callBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                try {
                    boolean resultCam = Utility.checkPermissionCall(mContext);
                    if (resultCam) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + number));
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            mContext.startActivity(callIntent);
                        }

                    }

                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        smsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                boolean resultCam = Utility.checkPermissionSMS(mContext);
                if (resultCam) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
                    intent.putExtra("sms_body", "Type here");
                    mContext.startActivity(intent);
                }
            }
        });
        dialog.show();



    }

}