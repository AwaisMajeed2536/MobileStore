package misbah.naseer.mobilestore.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;

/**
 * Created by Devprovider on 31/07/2017.
 */

public class PlaceOrderActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    protected EditText orderEt;
    protected Button orderButton;
    private DatabaseReference messageRef;
    private DatabaseReference ordersRef;
    private int orderIndex;
    private int messageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_place_order);
        UtilHelper.showWaitDialog(this, "Loading View!", "please wait...");
        messageRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages/a01");

        ordersRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userOrders/"+UtilHelper.getLoggedInUser(this).getUserId());
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderIndex = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        messageRef.addListenerForSingleValueEvent(this);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.order_button) {
            sendMessage(messageIndex);
        }
    }

    private void initView() {
        orderEt = (EditText) findViewById(R.id.order_et);
        orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(PlaceOrderActivity.this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        UtilHelper.dismissWaitDialog();
        messageIndex = (int) dataSnapshot.getChildrenCount();
    }

    private void sendMessage(int count) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date currentDate = new Date();
        String timeAndDate = sdf.format(currentDate);
        HashMap<String, String> messageData = new HashMap<>();
        HashMap<String, String> orderData = new HashMap<>();
        Location location = UtilHelper.getLastKnownLocation(this);
        messageData.put(Constants.MESSAGE_FROM, UtilHelper.getLoggedInUser(this).getUserId());
        messageData.put(Constants.MESSAGE_BODY, orderEt.getText().toString());
        orderData.put(Constants.ORDER_TEXT_KEY, orderEt.getText().toString());
        messageData.put(Constants.MESSAGE_DATE, timeAndDate);
        orderData.put(Constants.ORDER_STATUS_KEY, Constants.ORDER_PENDING);
        messageData.put(Constants.MESSAGE_LOCATION, location.getLatitude() + "|" + location.getLongitude());
        UtilHelper.showWaitDialog(this, "Sending Message!", "please wait...");
        ordersRef.child(String.valueOf(orderIndex++)).setValue(orderData);
        messageRef.child(String.valueOf(count++)).setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UtilHelper.dismissWaitDialog();
                UtilHelper.showAlertDialog(PlaceOrderActivity.this, "Done", "message sent successfully...");
                orderEt.setText("");
            }
        });
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
