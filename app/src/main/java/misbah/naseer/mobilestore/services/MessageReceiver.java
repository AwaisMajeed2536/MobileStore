package misbah.naseer.mobilestore.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.model.UserInformationModel;

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            HashMap<String, String> intentData = (HashMap<String, String>) intent.getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY);
            UserInformationModel user = UtilHelper.getLoggedInUser(context);
            String userType = "none";
            if (user != null) {
                userType = user.getUserType();
                if (userType.equalsIgnoreCase(Constants.USER_TYPE_ADMIN) &&
                        intentData.get(Constants.MESSAGE_FROM).startsWith("s")) {
                        NotificationUtils.newInstance(context).showGeneralNotification(intentData);
                } else if(userType.equalsIgnoreCase(Constants.USER_TYPE_DISTRIBUTOR) &&
                        intentData.get(Constants.MESSAGE_FROM).startsWith("a")){
                        NotificationUtils.newInstance(context).showGeneralNotification(intentData);
                }
            }

        }
    }
}
