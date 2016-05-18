package picklenostra.user_app.helper.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import picklenostra.user_app.R;
/**
 * Created by Syukri Mullia Adil P on 5/17/2016.
 */
public class GcmMessageHandler extends IntentService {

    private String title,text;
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
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
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.brandinglogo).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent myIntent = null;
        try {
            myIntent = new Intent(this,Class.forName("picklenostra.picklebankapp.NotifikasiDetailActivity"));
            myIntent.putExtra("id","1");
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
