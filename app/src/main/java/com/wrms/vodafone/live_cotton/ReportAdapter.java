package com.wrms.vodafone.live_cotton;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wrms.vodafone.R;

import java.util.ArrayList;

/**
 * Created by Admin on 11-09-2017.
 */
public class ReportAdapter  extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private ArrayList<ReportBean> mDataset = new ArrayList<ReportBean>();

    public Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView phone,name,location,resolutionType,crop,resolveUser,status,requestDate,resolveDate,sentDate,pendingDate;

        public TextView conversation;

        public ViewHolder(View v) {
            super(v);
            phone = (TextView) v.findViewById(R.id.phone_row);
            name = (TextView) v.findViewById(R.id.name_row);
            crop = (TextView) v.findViewById(R.id.crop_row);
            location = (TextView) v.findViewById(R.id.location_row);
            conversation = (TextView) v.findViewById(R.id.conversation_row);

            conversation.setMovementMethod(ScrollingMovementMethod.getInstance());

            resolutionType = (TextView) v.findViewById(R.id.resolutiontype_row);
            resolveUser = (TextView) v.findViewById(R.id.resolveuser_row);
            requestDate = (TextView) v.findViewById(R.id.requestdate_row);
            resolveDate = (TextView) v.findViewById(R.id.resolvedate_row);

            sentDate = (TextView) v.findViewById(R.id.senddate_row);
            pendingDate = (TextView) v.findViewById(R.id.pendingdate_row);
            status = (TextView) v.findViewById(R.id.status_row);



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
    public ReportAdapter(Context con, ArrayList<ReportBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_row_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.phone.setText(mDataset.get(position).getPhoneNo());
        holder.crop.setText(mDataset.get(position).getCrop());
        holder.location.setText(mDataset.get(position).getLocation());

        String conv = mDataset.get(position).getConversation();
        conv = conv.replace("&nbsp","");

        if (conv!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                holder.conversation.setText(Html.fromHtml(conv, Html.FROM_HTML_MODE_COMPACT));
            }else {
                holder.conversation.setText(Html.fromHtml(conv));
            }
        }
        holder.name.setText(mDataset.get(position).getName());
        holder.requestDate.setText(mDataset.get(position).getRequestDate());
        holder.resolveDate.setText(mDataset.get(position).getResolveDate());
        holder.resolutionType.setText(mDataset.get(position).getResolutionType());
        holder.resolveUser.setText(mDataset.get(position).getResolverUser());
        holder.pendingDate.setText(mDataset.get(position).getPendingDate());

        holder.sentDate.setText(mDataset.get(position).getSentDate());
        holder.status.setText(mDataset.get(position).getStatus());

        final String finalConv = conv;
        holder.conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                dialog.setContentView(R.layout.message_popup);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel_popup);
                final TextView txt = (TextView) dialog.findViewById(R.id.text_popup_msg);

                String str = finalConv;

                if (str!=null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        txt.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
                    }else {
                        txt.setText(Html.fromHtml(str));
                    }
                }

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}