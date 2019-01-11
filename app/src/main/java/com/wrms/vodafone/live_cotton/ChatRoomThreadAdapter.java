package com.wrms.vodafone.live_cotton;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wrms.vodafone.R;
import com.wrms.vodafone.bean.MessageBean;
import com.wrms.vodafone.utils.AppConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();


    private int SELF = 100;
    private static String today;


    private Context mContext;
    private ArrayList<MessageBean> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;
        ImageView image;
        ImageView audioPlay;
        LinearLayout hideRow;


        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            image = (ImageView) itemView.findViewById(R.id.image);
            audioPlay = (ImageView)itemView.findViewById(R.id.audio_play);
            hideRow = (LinearLayout)itemView.findViewById(R.id.aaaaa);
        }
    }


    public ChatRoomThreadAdapter(Context mContext, ArrayList<MessageBean> messageArrayList) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        MessageBean message = messageArrayList.get(position);
        String user = AppConstant.visible_Name;
        if (message.getUser().equalsIgnoreCase(user)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MessageBean msg = messageArrayList.get(position);

       /* if (msg.getType().equalsIgnoreCase("2")) {

            ((ViewHolder) holder).message.setVisibility(View.GONE);

            ((ViewHolder) holder).map.setVisibility(View.VISIBLE);
        } else {

            ((ViewHolder) holder).message.setVisibility(View.VISIBLE);

            ((ViewHolder) holder).map.setVisibility(View.GONE);
        }
        ((ViewHolder) holder).map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q="+msg.getMessage()));
                mContext.startActivity(intent);

            }
        });
*/

        String msggg = msg.getMessage();
        if (msggg.equalsIgnoreCase("Voice_Message")){
            ((ViewHolder) holder).hideRow.setVisibility(View.GONE);
        }else {
            ((ViewHolder) holder).hideRow.setVisibility(View.VISIBLE);

        }

        ((ViewHolder) holder).message.setText(msg.getMessage());


        String timestamp = msg.getMsgDate();
        String sss = null;

        if (timestamp!=null) {

            List<String> dateList = Arrays.asList(timestamp.split("T"));
            if (dateList.size() > 1) {
                sss = dateList.get(0) + " & " + dateList.get(1);
            }

        }
        if (msg.getUser() != null)
            timestamp = msg.getUser() + ", " + sss;

        ((ViewHolder) holder).timestamp.setText(timestamp);

        String img = msg.getImage();
        if (img!=null && img.length()>4){

            Log.v("imagellll",img+"--"+img.length());

            ((ViewHolder)holder).image.setVisibility(View.VISIBLE);
            String imagePath = "http://pdjalna.myfarminfo.com/LogImage/"+img;
            Picasso.with(mContext).load(imagePath).into(((ViewHolder) holder).image);

        }else {

            ((ViewHolder) holder).image.setVisibility(View.GONE);
        }


        final String aud = msg.getAudioPath();
        if (aud!=null && aud.length()>4){

            ((ViewHolder)holder).audioPlay.setVisibility(View.VISIBLE);

        }else {

            ((ViewHolder) holder).audioPlay.setVisibility(View.GONE);
        }

        ((ViewHolder)holder).audioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    if (aud!=null) {
                        String audPath = "http://pdjalna.myfarminfo.com/LogVoice/"+aud;
                        Log.v("audioPath",audPath+"--"+audPath.length());

                        MediaPlayer player = new MediaPlayer();
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource(audPath);
                        player.prepare();
                        player.start();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });


        ((ViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
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

                String str = messageArrayList.get(position).getImage();
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

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}

