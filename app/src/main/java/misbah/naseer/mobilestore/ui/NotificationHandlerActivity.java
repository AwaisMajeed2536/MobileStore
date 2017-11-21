package misbah.naseer.mobilestore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;

import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.ui.AdminHomeActivity;
import misbah.naseer.mobilestore.ui.DistributorHomeActivity;

/**
 * Created by Shah on 6/10/2017.
 */

public class NotificationHandlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            HashMap<String, String> notificationData = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY);
            Intent intent;
            if (notificationData.get(Constants.MESSAGE_FROM).startsWith("s"))
                intent = new Intent(this, AdminHomeActivity.class);
            else
                intent = new Intent(this, DistributorHomeActivity.class);
            intent.putExtra(Constants.SERVICE_DATA_PASS_KEY, notificationData);
            startActivity(intent);
        }
    }

}
