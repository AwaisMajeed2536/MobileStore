package misbah.naseer.mobilestore.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.ui.NotificationHandlerActivity;


/**
 * Created by Ravi on 01/06/15.
 */
public class NotificationUtils {


    private String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils() {
    }

    private NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static NotificationUtils newInstance(Context mContext) {
        return new NotificationUtils(mContext);
    }

    public void showGeneralNotification(HashMap<String, String> intentData) {
        Notification notification = buildNotification(1, intentData).build();
        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.notification_tone);
        notification.sound = uri;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    protected NotificationCompat.Builder buildNotification(int notificationId, HashMap<String, String> intentData) {
        Intent intent = new Intent(mContext, NotificationHandlerActivity.class);
        intent.putExtra(Constants.SERVICE_DATA_PASS_KEY, intentData);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(
                mContext,
                notificationId,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                // Set Icon
                .setSmallIcon(R.drawable.notification_icon)
                // Set Ticker Message
                .setTicker(mContext.getString(R.string.app_name))
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent);
            // Build a simpler notification, without buttons
            //
            builder = builder.setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(intentData.get(Constants.MESSAGE_FROM))
                    .setSmallIcon(R.drawable.notification_icon);
        return builder;
    }




}