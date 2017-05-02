package misbah.naseer.mobilestore.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.UtilHelper;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UtilHelper.GoToActivityAsNewTask(SplashActivity.this, LandingActivity.class, R.anim.enter_activity, R.anim.hold_activity);
            }
        }, 2500);
    }



}
