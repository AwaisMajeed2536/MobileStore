package misbah.naseer.mobilestore.helper;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.ui.SplashActivity;

/**
 * Created by Devprovider on 01/05/2017.
 */

public class PermissionsActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_KEY = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilHelper.setContext(getApplicationContext());
        if (!UtilHelper.hasPermissions(this, Constants.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, PERMISSIONS_REQUEST_KEY);
        }else
            UtilHelper.GoToActivityAsNewTask(this, SplashActivity.class, 0, R.anim.hold_activity);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_KEY) {
            UtilHelper.GoToActivityAsNewTask(this, SplashActivity.class, 0, R.anim.hold_activity);
        }
    }
}
