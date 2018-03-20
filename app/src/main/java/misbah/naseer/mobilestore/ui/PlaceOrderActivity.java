package misbah.naseer.mobilestore.ui;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.adapter.PlaceOrderAdapter;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.model.AvailableItemsModel;
import misbah.naseer.mobilestore.model.PlaceOrderModel;

/**
 * Created by Devprovider on 31/07/2017.
 */

public class PlaceOrderActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    protected Button orderButton;
    protected ListView itemsLv;
    private DatabaseReference messageRef;
    private DatabaseReference ordersRef;
    private DatabaseReference itemsRef;
    private int orderIndex;
    private int messageIndex;
    private List<PlaceOrderModel> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_place_order);
        initView();
        UtilHelper.showWaitDialog(this, "Loading View!", "please wait...");
        messageRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages/a01");

        ordersRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userOrders/" + UtilHelper.getLoggedInUser(this).getUserId());
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
        itemsRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/itemsAvailable/");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AvailableItemsModel> dataList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    dataList.add(child.getValue(AvailableItemsModel.class));
                }
                itemsList = getItemsList(dataList);
                PlaceOrderAdapter adapter = new PlaceOrderAdapter(PlaceOrderActivity.this, itemsList);
                itemsLv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<PlaceOrderModel> getItemsList(List<AvailableItemsModel> dataList) {
        List<PlaceOrderModel> toReturn = new ArrayList<>();
        for (AvailableItemsModel obj : dataList) {
            toReturn.add(new PlaceOrderModel(obj.getItemName(), 0));
        }
        return toReturn;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.order_button) {
            new AlertDialog.Builder(this).setTitle("Send Message")
                    .setMessage("Are you sure you order the following items:\n" + getMessage())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            sendMessage(messageIndex);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
    }

    private void initView() {
        orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(PlaceOrderActivity.this);
        itemsLv = (ListView) findViewById(R.id.items_lv);
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
        messageData.put(Constants.MESSAGE_BODY, getMessage());
        orderData.put(Constants.ORDER_TEXT_KEY, getMessage());
        messageData.put(Constants.MESSAGE_DATE, timeAndDate);
        orderData.put(Constants.ORDER_STATUS_KEY, Constants.ORDER_PENDING);
        messageData.put(Constants.MESSAGE_LOCATION, location.getLatitude() + "|" + location.getLongitude());
        UtilHelper.showWaitDialog(this, "Sending Message!", "please wait...");
        ordersRef.child(String.valueOf(orderIndex++)).setValue(orderData);
        messageRef.child(String.valueOf(count++)).setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UtilHelper.dismissWaitDialog();
                Toast.makeText(PlaceOrderActivity.this, "Message Sent!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private String getMessage() {
        String toReturn = "";
        for (PlaceOrderModel obj : itemsList) {
            if (obj.getQuantiity() > 0) {
                toReturn += obj.getItemName() + " x" + obj.getQuantiity() + "\n";
            }
        }
        return toReturn;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
