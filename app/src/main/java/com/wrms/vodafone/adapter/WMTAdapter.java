package com.wrms.vodafone.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wrms.vodafone.R;
import com.wrms.vodafone.home.MoreInfoActivity;
;

/**
 * Created by Admin on 11-10-2017.
 */
public class WMTAdapter  extends BaseAdapter {
    String [] nameL;
    String [] irrigationL;
    String url;

    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public WMTAdapter(Context con, String[] nl,String[] iL, int[] img) {
        // TODO Auto-generated constructor stub
        nameL=nl;
        irrigationL=iL;
        context=con;
        imageId=img;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return nameL.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView name,saveIr,link;
        ImageView img;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.wmt_row, null);
        holder.name=(TextView) rowView.findViewById(R.id.name);
        holder.saveIr=(TextView) rowView.findViewById(R.id.i_w_save);
        holder.link=(TextView) rowView.findViewById(R.id.info);
        holder.img=(ImageView) rowView.findViewById(R.id.pic);
        holder.name.setText(nameL[position]);
        holder.saveIr.setText(irrigationL[position]);
        holder.img.setImageResource(imageId[position]);

        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0){
                    url = "http://dswcpunjab.gov.in/contents/data_folder/laser_level.htm";
                }else if (position==1){

                    url = "http://scienceandnature.org/IJEMS-Vol4(4)-Oct2013/IJEMS_V4(4)2013-7.pdf";

                }else {
                    url = "http://scienceandnature.org/IJEMS-Vol4(4)-Oct2013/IJEMS_V4(4)2013-7.pdf";

                }

                Intent intent = new Intent(context, MoreInfoActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("pos",""+position);
                context.startActivity(intent);
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);

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
                dialog.setContentView(R.layout.image_popup);
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel_popup);
                final ImageView img = (ImageView) dialog.findViewById(R.id.image_popup);


                img.setImageResource(imageId[position]);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });

            }
        });

        return rowView;
    }

}