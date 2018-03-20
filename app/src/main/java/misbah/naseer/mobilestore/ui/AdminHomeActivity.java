package misbah.naseer.mobilestore.ui;

import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.interfaces.AlertDialogCallback;
import misbah.naseer.mobilestore.model.UserInformationModel;
import okhttp3.internal.Util;

public class AdminHomeActivity extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener, AdapterView.OnItemClickListener {

    public static final String TAG = AdminHomeActivity.class.getName();

    private GoogleMap mMap;
    private ListView driversListLV;
    private ArrayList<String> driversList = new ArrayList<>();
    private ArrayList<LatLng> driversPositionList = new ArrayList<>();
    private DatabaseReference driverListRef;
    private DatabaseReference locationsRef;
    private ArrayAdapter<String> adapter;
    private Menu menu = null;
    private HashMap<String, String> notificationData;
    private HashMap<String, HashMap<String, String>> userLocations = new HashMap<>();
    private HashMap<String, HashMap<String, String>> usersData;
    private boolean isNotificationReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "intent received: ");
            notificationData = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY);
            isNotificationReceived = true;
            invalidateOptionsMenu();
        }
        driverListRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/UserAccounts");
        locationsRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userLocations");
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userLocations = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        driverListRef.addListenerForSingleValueEvent(this);
        driversListLV = (ListView) findViewById(R.id.drivers_list_lv);
        driversListLV.setOnItemClickListener(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, driversList);
        driversListLV.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDriversData(mMap);
    }

    private void getDriversData(final GoogleMap googleMap) {
        HashMap<String, HashMap<String, String>> driverMapData = new HashMap<String, HashMap<String, String>>();
        HashMap<String, HashMap<String, String>> storemapData = new HashMap<String, HashMap<String, String>>();
        for (HashMap.Entry<String, HashMap<String, String>> entry : usersData.entrySet()) {
            String key = entry.getKey();
            HashMap<String, String> value = entry.getValue();
            if (isDistributor(key)) {
                driverMapData.put(key, userLocations.get(key));
            } else if( key.startsWith("s") || key.startsWith("S")){
                storemapData.put(key,userLocations.get(key));
            }
        }
        addDriverMarkers(driverMapData);
        addStoreMarkers(storemapData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notification) {
            Intent intent = new Intent(this, MessagingActivity.class);
            intent.putExtra(Constants.MESSAGES_PASS_KEY, notificationData);
            startActivity(intent);
            isNotificationReceived = false;
            notificationData = null;
        } else if (item.getItemId() == R.id.action_track) {
            Intent intent = new Intent(this, OrderTimesActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_add_remove_items) {
            startActivity(new Intent(this, AddRemoveItemsActivity.class));
        } else if (item.getItemId() == R.id.action_sign_out) {
            UtilHelper.endLoginSession(this);
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        usersData = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
        for (HashMap.Entry<String, HashMap<String, String>> entry : usersData.entrySet()) {
            String id = entry.getKey();
            HashMap<String, String> userData = entry.getValue();
            UserInformationModel value = new UserInformationModel(userData.get(Constants.USER_ID),
                    userData.get(Constants.USER_NAME), userData.get(Constants.CONTACT), userData.get(Constants.EMAIL),
                    String.valueOf(userData.get(Constants.PASSWORD)), userData.get(Constants.USER_TYPE));
            if (isDistributor(value)) {
                driversList.add(value.getUserName() + "-" + id);
            }
            String dList = new Gson().toJson(driversList);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Constants.DRIVERS_LIST_KEY, dList).apply();
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, driversList);
        driversListLV.setAdapter(adapter);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(AdminHomeActivity.this);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        onPrepareOptionsMenu(menu);
    }

    private boolean isDistributor(UserInformationModel model) {
        return (model.getUserType().equalsIgnoreCase(Constants.USER_TYPE_DISTRIBUTOR));
    }

    private boolean isStore(UserInformationModel model) {
        return (model.getUserType().equalsIgnoreCase(Constants.USER_TYPE_STORE));
    }

    private boolean isDistributor(String driver) {
        return (driver.startsWith("d") || driver.startsWith("D"));
    }

    private void addDriverMarkers(HashMap<String, HashMap<String, String>> mapData) {
        double latitude, longitude;
        LatLng position = null;
        for (HashMap.Entry<String, HashMap<String, String>> entry : mapData.entrySet()) {
            String key = entry.getKey();
            HashMap<String, String> value = entry.getValue();
            latitude = Double.parseDouble(value.get(Constants.LAT_KEY));
            longitude = Double.parseDouble(value.get(Constants.LONG_KEY));
            position = new LatLng(latitude, longitude);
            driversPositionList.add(position);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);
            mMap.addMarker(new MarkerOptions().position(position).title(key)).setIcon(icon);
        }
        if (position != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 8));
    }

    private void addStoreMarkers(HashMap<String, HashMap<String, String>> mapData) {
        double latitude, longitude;
        LatLng position = null;
        for (HashMap.Entry<String, HashMap<String, String>> entry : mapData.entrySet()) {
            String key = entry.getKey();
            HashMap<String, String> value = entry.getValue();
            latitude = Double.parseDouble(value.get(Constants.LAT_KEY));
            longitude = Double.parseDouble(value.get(Constants.LONG_KEY));
            position = new LatLng(latitude, longitude);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_store);
            mMap.addMarker(new MarkerOptions().position(position)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(key)).setIcon(icon);

        }
        if (position != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 8));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null && menu.findItem(R.id.action_notification) != null) {
            if (isNotificationReceived)
                menu.findItem(R.id.action_notification).setIcon(R.drawable.notification_icon);
            else
                menu.findItem(R.id.action_notification).setIcon(R.drawable.no_notification_icon);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driversPositionList.get(position), 12));
    }

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        final String clickedDistributor = marker.getTitle();
//        if (clickedDistributor.startsWith("s")){
//            UtilHelper.showAlertDialog(AdminHomeActivity.this, "Distributor Selected!", "You cannot send this message to a store",
//                    "Retry", new AlertDialogCallback() {
//                        @Override
//                        public void onClick() {
//                        }
//                    });
//            return false;
//        }
//        UtilHelper.showWaitDialog(AdminHomeActivity.this, "Sending Message", "please wait...");
//        final DatabaseReference messageRef = FirebaseDatabase.getInstance()
//                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages/" + clickedDistributor);
//        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int messageNumber = (int) dataSnapshot.getChildrenCount();
//                notificationData.put(Constants.MESSAGE_FROM, "a01");
//                messageRef.child(String.valueOf(messageNumber)).setValue(notificationData)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        UtilHelper.dismissWaitDialog();
//                        new AlertDialog.Builder(AdminHomeActivity.this).setTitle("Message Deliverd")
//                                .setMessage("your message was successfully delivered to " + clickedDistributor)
//                        .create().show();
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return false;
//    }
}
