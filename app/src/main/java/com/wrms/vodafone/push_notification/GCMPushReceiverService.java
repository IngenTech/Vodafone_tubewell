package com.wrms.vodafone.push_notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wrms.vodafone.R;
import com.wrms.vodafone.home.NavigationDrawerActivity;

import org.json.JSONException;
import org.json.JSONObject;



public class GCMPushReceiverService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d("PushNotifications",remoteMessage.getData().toString());
       // Log.d(TAG, "Notification: " + remoteMessage.getNotification().toString());
       /* Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "From: " + remoteMessage.getData().get("custom").toString());

        String data = remoteMessage.getData().get("custom").toString();
        try {
            JSONObject js = new JSONObject(data.toString());
            String pushType = js.getString("push_type");

            Log.d(TAG, "From: " + pushType);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

       // sendNotification(remoteMessage.getData().get("message"));
        sendNotification("New notification from IRD");
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        intent.putExtra("data","advi");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final String packageName = getPackageName();
      //  Uri defaultSoundUri = Uri.parse("android.resource://" + packageName + "R.raw.putt_sound");
        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/putt_sound");
        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.push_icon);
            notificationBuilder.setColor(Color.parseColor("#4ed35b"));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        notificationBuilder.setContentTitle("Cotton Doctor Jalna");
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder .setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int id = (int) System.currentTimeMillis();
        notificationManager.notify(id, notificationBuilder.build());
    }
}