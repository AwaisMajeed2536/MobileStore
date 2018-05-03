package misbah.naseer.mobilestore.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.Stopwatch;
import misbah.naseer.mobilestore.helper.UtilHelper;
import okhttp3.internal.Util;

public class DistributorHomeActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener , View.OnClickListener{

    private Button doneBtn;
    HashMap<String, String> receivedMessage;
    GoogleMap googleMap;
    Location driverLocation;
    LatLng storeLatLong;
    private List<Polyline> polylines = new ArrayList<>();
    private DatabaseReference orderTrackerRef;
    private int orderTimesCounter = 0;
    private String orderBody = "";
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimaryLight, R.color.colorAccent, R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_distributor_home);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        doneBtn = (Button) findViewById(R.id.done_btn);
        doneBtn.setOnClickListener(this);
        orderTrackerRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/orderTracks/");
        orderTrackerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderTimesCounter = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (getIntent().getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY) != null) {
            receivedMessage = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY);
            String location = receivedMessage.get(Constants.MESSAGE_LOCATION);
            String[] latLong = location.split("\\|");
            orderBody = receivedMessage.get(Constants.MESSAGE_BODY);
            driverLocation = UtilHelper.getLastKnownLocation(this);
            LatLng driverLatLong = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
            saveCurrentLocation(driverLatLong);
            BitmapDescriptor carIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);
            googleMap.addMarker(new MarkerOptions().position(driverLatLong).title("Store Location")).setIcon(carIcon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLong, 13));
            showEmployeeInterface(Double.valueOf(latLong[0]), Double.valueOf(latLong[1]));
            doneBtn.setEnabled(true);
        } else {
            driverLocation = UtilHelper.getLastKnownLocation(this);
            LatLng driverLatLong = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
            saveCurrentLocation(driverLatLong);
            BitmapDescriptor carIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);
            googleMap.addMarker(new MarkerOptions().position(driverLatLong).title("Store Location")).setIcon(carIcon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLong, 13));
        }
    }

    private void saveCurrentLocation(LatLng driverLatLong) {
        HashMap<String, String> locationMap = new HashMap<>();
        locationMap.put("latitude", String.valueOf(driverLatLong.latitude));
        locationMap.put("longitude", String.valueOf(driverLatLong.longitude));
        DatabaseReference locationRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userLocations/" + UtilHelper.getLoggedInUser(this).getUserId());
        locationRef.setValue(locationMap);
    }

    private void showEmployeeInterface(double latitude, double longitude) {
        storeLatLong = new LatLng(latitude, longitude);
        BitmapDescriptor storeIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_store);
        googleMap.addMarker(new MarkerOptions().position(storeLatLong).title("Current Location")).setIcon(storeIcon);
        UtilHelper.showAlertDialog(this, "New Message!", orderBody);
        getRouteToMarker(storeLatLong);
    }

    private void getRouteToMarker(LatLng storeLatLong) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()), storeLatLong)
                .build();
        routing.execute();
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


    @Override
    public void onRoutingFailure(RouteException e) {
// The Routing request failed
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        googleMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 8);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);

        }
        try {
            new AlertDialog.Builder(this).setTitle("Your Route Information")
                    .setMessage("Distance: " + String.format("%02d", (route.get(0).getDistanceValue() / 1000d)) + "Kms\n"
                            + "ETA: " + String.format("%02d", (route.get(0).getDurationValue() / 3600d) + "Hours"))
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));
        googleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(storeLatLong);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_store));
        googleMap.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.done_btn){
            String key = orderTrackerRef.push().getKey();
            orderTrackerRef.child(key).child("order").setValue(orderBody);
            orderTrackerRef.child(key).child("byDriver").setValue(UtilHelper.getLoggedInUser(this).getUserId());
            doneBtn.setEnabled(false);
        }
    }
}
