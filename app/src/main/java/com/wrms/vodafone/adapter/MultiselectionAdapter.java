package com.wrms.vodafone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.MultiBean;

import java.util.ArrayList;

/**
 * Created by Admin on 28-09-2017.
 */
public class MultiselectionAdapter  extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<MultiBean> objects;
    ViewHolder holder = null;

    public MultiselectionAdapter(Context context, ArrayList<MultiBean> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.multiselection_row, parent, false);
            holder = new ViewHolder();
            view.setTag(holder);
            holder.txt = (TextView)view.findViewById(R.id.name_friend);
            holder.cbBuy = (CheckBox) view.findViewById(R.id.checkBox1);


        }else {
            holder = (ViewHolder) view.getTag();

        }

        MultiBean p = getProduct(position);


        holder.txt.setText((p.crop_name).toUpperCase());

        holder.cbBuy.setOnCheckedChangeListener(myCheckChangList);
        holder.cbBuy.setTag(position);

        holder.cbBuy.setChecked(p.box);
        return view;
    }

    MultiBean getProduct(int position) {
        return ((MultiBean) getItem(position));
    }

    public ArrayList<MultiBean> getBox() {
        ArrayList<MultiBean> box = new ArrayList<MultiBean>();
        for (MultiBean p : objects) {
            if (p.box)
                box.add(p);
        }
        return box;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };


    private static class ViewHolder {
        public TextView txt = null;
        public CheckBox cbBuy = null;
    }
}