package com.wrms.vodafone.live_cotton;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.squareup.picasso.Picasso;
import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.UnresolveBean;
import com.wrms.vodafone.home.AppController;

import java.util.ArrayList;

/**
 * Created by Admin on 09-09-2017.
 */
public class UnresolveAdapter  extends RecyclerView.Adapter<UnresolveAdapter.ViewHolder> {
    private ArrayList<UnresolveBean> mDataset = new ArrayList<UnresolveBean>();

    public Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView phone,crop,location,assignUser;
        ImageView imageView;
        LinearLayout row;



        public ViewHolder(View v) {
            super(v);
            phone = (TextView) v.findViewById(R.id.unresolve_phone);
            crop = (TextView) v.findViewById(R.id.unresolve_crop);
            location = (TextView) v.findViewById(R.id.unresolve_location);
            assignUser = (TextView) v.findViewById(R.id.unresolve_assign);
            imageView = (ImageView) v.findViewById(R.id.unresolve_image);

            row = (LinearLayout)v.findViewById(R.id.unresolve_row);


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
    public UnresolveAdapter(Context con, ArrayList<UnresolveBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.unresolve_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.phone.setText(mDataset.get(position).getPhoneNo());
        holder.crop.setText(mDataset.get(position).getCrop());
        holder.location.setText(mDataset.get(position).getLocation());
        holder.assignUser.setText(mDataset.get(position).getAssignUser());

        String str = mDataset.get(position).getImagePath();
        if (str!=null && str.length()>4) {
            String imagePath = "http://pdjalna.myfarminfo.com/LogImage/"+str;
            Picasso.with(mContext).load(imagePath).into(holder.imageView);
        }

        holder.row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Dialog dialog = new Dialog(mContext);

                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.dimAmount = 0.5f;

                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                // Include dialog.xml file
                dialog.setContentView(R.layout.unresolve_long_popup);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final RelativeLayout expertAssignBTN = (RelativeLayout) dialog.findViewById(R.id.assign_to_expert_btn);
                final RelativeLayout actionBTN = (RelativeLayout) dialog.findViewById(R.id.action_btn);
                final RelativeLayout conversationBTN = (RelativeLayout) dialog.findViewById(R.id.conversation_btn);
                final RelativeLayout deleteBTN = (RelativeLayout) dialog.findViewById(R.id.delete_btn);

                expertAssignBTN.setVisibility(View.GONE);
                expertAssignBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Toast.makeText(mContext,"Under maintenance",Toast.LENGTH_SHORT).show();
                    }
                });

                deleteBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        remove(position);
                        deleteRequest(mDataset.get(position).getReqID());
                    }
                });

                actionBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Intent in = new Intent(mContext,ResolveRequestActivity.class);
                        in.putExtra("phone",mDataset.get(position).getPhoneNo());
                        in.putExtra("location",mDataset.get(position).getLocation());
                        in.putExtra("conversation",mDataset.get(position).getConversation());
                        in.putExtra("requestID",mDataset.get(position).getReqID());
                        in.putExtra("audioPath",mDataset.get(position).getVoicePath());
                        String str = mDataset.get(position).getImagePath();
                        String imagePath = null;

                        if (str!=null && str.length()>4) {
                            imagePath = "http://pdjalna.myfarminfo.com/LogImage/"+str;

                        }
                        in.putExtra("image",imagePath);

                        mContext.startActivity(in);
                    }
                });

                return false;
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);

                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.dimAmount = 0.5f;

                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                // Include dialog.xml file
                dialog.setContentView(R.layout.screen_popup);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel_popup);
                final ImageView img = (ImageView) dialog.findViewById(R.id.image_popup);

                String str = mDataset.get(position).getImagePath();
                if (str!=null && str.length()>4) {
                    String imagePath = "http://pdjalna.myfarminfo.com/LogImage/"+str;
                    Picasso.with(mContext).load(imagePath).into(img);
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


    public void deleteRequest(String reqid) {
        final ProgressDialog dialog = ProgressDialog.show(mContext, "", "Deleting Request...", true);



        StringRequest stringRequest = new StringRequest(Request.Method.GET,"http://pdjalna.myfarminfo.com/PDService.svc/Threads/Delete/"+reqid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        response = response.trim();
                     //   response = response.substring(1, response.length() - 1);
                        response = response.replace("\\", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"{", "{");
                        response = response.replace("}\"", "}");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        System.out.println("delete response : " + response);
                       /* try {

                           JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                System.out.println("Volley Error : " + error);
            }
        });

        int socketTimeout = 60000;//60 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}