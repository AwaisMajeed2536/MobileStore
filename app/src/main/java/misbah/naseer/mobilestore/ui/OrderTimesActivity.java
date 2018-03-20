package misbah.naseer.mobilestore.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.adapter.OrderTimesAdapter;

public class OrderTimesActivity extends AppCompatActivity {

    protected ListView orderTimesLv;
    private DatabaseReference orderTimesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_order_times);
        initView();
        orderTimesRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/orderTimes/s01");
        orderTimesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> data = (ArrayList<String>) dataSnapshot.getValue();
                OrderTimesAdapter adapter = new OrderTimesAdapter(OrderTimesActivity.this, data);
                orderTimesLv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        orderTimesLv = (ListView) findViewById(R.id.order_times_lv);
    }
}
