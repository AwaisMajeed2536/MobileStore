package misbah.naseer.mobilestore.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.adapter.OrderTrackingAdapter;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.interfaces.ClickListenCallback;
import misbah.naseer.mobilestore.model.OrderModel;

public class OrderTrackingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView ordersLv;
    private OrderTrackingAdapter adapter;
    private List<HashMap<String, String>> dataList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);
        ordersLv = (ListView) findViewById(R.id.orders_lv);
        ordersRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userOrders/"+UtilHelper.getLoggedInUser(this).getUserId());
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList = (List<HashMap<String, String>>) dataSnapshot.getValue();
                setAdapter(dataList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setAdapter(List<HashMap<String, String>> dataList) {
        if (dataList!=null) {
            adapter = new OrderTrackingAdapter(this, dataList);
            ordersLv.setAdapter(adapter);
            ordersLv.setOnItemClickListener(this);
        }else
            Toast.makeText(this, "No Orders to show!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if(dataList.get(position).get(Constants.ORDER_STATUS_KEY).equalsIgnoreCase(Constants.ORDER_DONE)){
            UtilHelper.showMessage(this, "order already done...");
        } else {
            new AlertDialog.Builder(this).setTitle("Order Status").setMessage("Have you received this order?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataList.get(position).put(Constants.ORDER_STATUS_KEY, Constants.ORDER_DONE);
                            ordersRef.setValue(dataList);
                            adapter.notifyDataSetChanged();
                            setAdapter(dataList);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }
}
