package misbah.naseer.mobilestore.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.services.TimerService;

/**
 * Created by Awais Majeed on 30-Jan-18.
 */

//Activity implements the Callbacks interface which defined in the Service
public class TimerServiceTestActivity extends AppCompatActivity implements TimerService.Callbacks {

    ToggleButton toggleButton;
    ToggleButton tbStartTask;
    TextView tvServiceState;
    TextView tvServiceOutput;
    Intent serviceIntent;
    TimerService myService;
    int seconds;
    int minutes;
    int hours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_service_test);
        serviceIntent = new Intent(TimerServiceTestActivity.this, TimerService.class);
        setViewsWidgets();
    }

    private void setViewsWidgets() {
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(btListener);
        tbStartTask = (ToggleButton) findViewById(R.id.tbStartServiceTask);
        tbStartTask.setOnClickListener(btListener);
        tvServiceState = (TextView) findViewById(R.id.tvServiceState);
        tvServiceOutput = (TextView) findViewById(R.id.tvServiceOutput);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Toast.makeText(TimerServiceTestActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            myService.registerClient(TimerServiceTestActivity.this); //Activity register in the service as client for callabcks!
            tvServiceState.setText("Connected to service...");
            tbStartTask.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(TimerServiceTestActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
            tvServiceState.setText("Service disconnected");
            tbStartTask.setEnabled(false);
        }
    };

    View.OnClickListener btListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == toggleButton) {
                if (toggleButton.isChecked()) {
                    startService(serviceIntent); //Starting the service
                    bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!
                    Toast.makeText(TimerServiceTestActivity.this, "Button checked", Toast.LENGTH_SHORT).show();
                } else {
                    unbindService(mConnection);
                    stopService(serviceIntent);
                    Toast.makeText(TimerServiceTestActivity.this, "Button unchecked", Toast.LENGTH_SHORT).show();
                    tvServiceState.setText("Service disconnected");
                    tbStartTask.setEnabled(false);
                }
            }

            if (v == tbStartTask) {
                if (tbStartTask.isChecked()) {
                    myService.startCounter();
                } else {
                    myService.stopCounter();
                }
            }
        }
    };

    @Override
    public void updateClient(long millis) {
        seconds = (int) (millis / 1000) % 60;
        minutes = (int) ((millis / (1000 * 60)) % 60);
        hours = (int) ((millis / (1000 * 60 * 60)) % 24);

        tvServiceOutput.setText((hours > 0 ? String.format("%d:", hours) : "") + ((this.minutes < 10 && this.hours > 0) ? "0" + String.format("%d:", minutes) : String.format("%d:", minutes)) + (this.seconds < 10 ? "0" + this.seconds : this.seconds));
    }
}
