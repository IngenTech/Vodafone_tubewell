package com.wrms.vodafone.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wrms.vodafone.R;
import com.wrms.vodafone.entities.VoiceMessageBean;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Admin on 25-07-2017.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
private ArrayList<VoiceMessageBean> mDataset = new ArrayList<VoiceMessageBean>();

public Context mContext;
        String imageString;
        TextToSpeech t1;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView expenseType;
    public Button btn;

    TextView date;


    public ViewHolder(View v) {
        super(v);
        expenseType = (TextView) v.findViewById(R.id.new_text);
        btn = (Button)v.findViewById(R.id.voice_btn);
        date = (TextView)v.findViewById(R.id.advisory_date);

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
    public MessageAdapter(Context con, ArrayList<VoiceMessageBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.expenseType.setText(mDataset.get(position).getMessageText());

        holder.date.setText(mDataset.get(position).getScheduleDate());

        Log.v("slkas",mDataset.get(position)+"");

        t1=new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("hin"));
                }
            }
        });


        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Voice Message");
                builder.setMessage("क्या आप मैसेज सुनना चाहते है?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {dialogInterface.dismiss();


                        String strrrr = mDataset.get(position).getMessageText();
                        String OutputString = null;

                       /* GoogleAPI.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");


                        try {
                            OutputString = Translate.execute(toSpeak, Language.ENGLISH, Language.HINDI);
                        }catch (Exception e){

                            System.out.println(e.getMessage());
                        }

                        Log.v("klklklk",OutputString+"");
*/
                        // Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();

                        //    String strrrr = "महत्वपूर्ण सूचना:प्रिय किसान भाइयों मौसम के अनुसार सफेद मक्खी (White fly) आने की परिस्थितियां बनी हुई है. यदि आप ने सात-दस दिन तक कोई छिडकाव नहीं किया है तो  इस रोग से फसल को बचाने के लिए निम्न रसायनों  में से किसी एक का इस्तेमाल करें।  Ulala @ 80 gram /Acre या  Lancer Gold + Phoskil या Renova + Josh का 120 लीटर पानी में मिला कर छिड़काव करे। WRMS-Unimart\n";

                        t1.setSpeechRate(0.7f);

                        t1.speak(strrrr, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
                builder.show();
            }
        });



    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    ProgressDialog dialog;
    SharedPreferences prefs;




}