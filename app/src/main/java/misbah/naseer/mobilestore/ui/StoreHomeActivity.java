package misbah.naseer.mobilestore.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.UtilHelper;

public class StoreHomeActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button placeOrderBtn;
    protected TextView orTextview;
    protected Button trackOrdersBtn;
    private DatabaseReference orderTrackerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_store_home);
        initView();
        orderTrackerRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/orderTracks/");
        orderTrackerRef.addChildEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
            HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
            String order = value.get("order");
            String byDriver = value.get("byDriver");
            String orderStatus = value.get("orderStatus");
            if (orderStatus == null)
                new AlertDialog.Builder(StoreHomeActivity.this).setTitle("New Order")
                        .setMessage("Order: " + order + "\n" + "ByDriver: " + byDriver)
                        .setPositiveButton("Confirm Order", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                String key = dataSnapshot.getKey();
                                orderTrackerRef.child(key).child("orderStatus").setValue("DONE");
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onClick(View view) {
        Intent intent = null;
        if (view.getId() == R.id.place_order_btn) {
            intent = new Intent(this, PlaceOrderActivity.class);
        } else if (view.getId() == R.id.track_orders_btn) {
            intent = new Intent(this, OrderTrackingActivity.class);
        }
        startActivity(intent);
    }

    private void initView() {
        placeOrderBtn = (Button) findViewById(R.id.place_order_btn);
        placeOrderBtn.setOnClickListener(StoreHomeActivity.this);
        orTextview = (TextView) findViewById(R.id.or_textview);
        trackOrdersBtn = (Button) findViewById(R.id.track_orders_btn);
        trackOrdersBtn.setOnClickListener(StoreHomeActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            UtilHelper.endLoginSession(this);
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
