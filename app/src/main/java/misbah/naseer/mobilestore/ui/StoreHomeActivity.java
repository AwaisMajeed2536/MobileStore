package misbah.naseer.mobilestore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.UtilHelper;

public class StoreHomeActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button placeOrderBtn;
    protected TextView orTextview;
    protected Button trackOrdersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_store_home);
        initView();

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        if (view.getId() == R.id.place_order_btn) {
            intent= new Intent(this, PlaceOrderActivity.class);
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
