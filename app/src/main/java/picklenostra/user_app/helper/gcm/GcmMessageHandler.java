package picklenostra.user_app.helper.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Collection;

import picklenostra.user_app.R;
/**
 * Created by Syukri Mullia Adil P on 5/17/2016.
 */
public class GcmMessageHandler extends IntentService {

    private String title,text;
    public static final int MESSAGE_NOTIFICATION_ID = 578578;

    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
//        Log.e("tes GCM", "GCM masuk nih1");
    }

    @Override
    public void onCreate() {
//        Log.e("tes GCM", "GCM masuk nih2");
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
//        Log.e("tes GCM", "GCM masuk nih3");
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        title = extras.getString("title");
        text = extras.getString("text");

        createNotification(title,text);
        Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),title , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createNotification(String title, String body){
//        Log.e("tes GCM", "GCM masuk nih4");
//        Log.e("tes GCM", "Title + " + title + ", body " + body);
        if (title == null || body == null) {
            return;
        }

        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.brandinglogo).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent myIntent = null;
        try {
            myIntent = new Intent(this,Class.forName("picklenostra.user_app.HistoryActivity"));
            myIntent.putExtra("id", "1");
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setVibrate(new long[]{500, 500, 500, 500, 500});
            mBuilder.setLights(Color.RED, 1000, 1000);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(notification);
            mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
