package misbah.naseer.mobilestore.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.model.UserInformationModel;

public class MessageReceiver extends BroadcastReceiver {

    public static final String TAG = "MessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            HashMap<String, String> intentData = (HashMap<String, String>) intent.getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY);
            UserInformationModel user = UtilHelper.getLoggedInUser(context);
            String userType = "none";
            if (user != null) {
                userType = user.getUserType();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String lastShownNotification = prefs.getString(Constants.LAST_SHOWN_NOTIFICATION_KEY, null);
                if (lastShownNotification != null && lastShownNotification.trim().equalsIgnoreCase(intentData.get(Constants.MESSAGE_BODY).trim())) {
                    return;
                }
                if (userType.equalsIgnoreCase(Constants.USER_TYPE_ADMIN) &&
                        intentData.get(Constants.MESSAGE_FROM).startsWith("s")) {
                    prefs.edit().putString(Constants.LAST_SHOWN_NOTIFICATION_KEY, intentData.get(Constants.MESSAGE_BODY)).apply();
                    NotificationUtils.newInstance(context).showGeneralNotification(intentData);
                } else if (userType.equalsIgnoreCase(Constants.USER_TYPE_DISTRIBUTOR) &&
                        intentData.get(Constants.MESSAGE_FROM).startsWith("a")) {
                    prefs.edit().putString(Constants.LAST_SHOWN_NOTIFICATION_KEY, intentData.get(Constants.MESSAGE_BODY)).apply();
                    NotificationUtils.newInstance(context).showGeneralNotification(intentData);
                }
            }

        }
    }
}
