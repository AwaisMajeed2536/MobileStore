package misbah.naseer.mobilestore.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.Stopwatch;
import misbah.naseer.mobilestore.helper.UtilHelper;

public class DistributorHomeActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, RoutingListener {

    protected Button startButton;
    protected TextView stopWatchTv;
    protected Button stopButton;
    HashMap<String, String> receivedMessage;
    GoogleMap googleMap;
    Location driverLocation;
    LatLng storeLatLong;
    private List<Polyline> polylines = new ArrayList<>();
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,R.color.colorPrimaryLight,R.color.colorAccent,R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_distributor_home);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (getIntent().getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY) != null) {
            receivedMessage = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.SERVICE_DATA_PASS_KEY);
            String location = receivedMessage.get(Constants.MESSAGE_LOCATION);
            String[] latLong = location.split("\\|");
            showEmployeeInterface(receivedMessage.get(Constants.MESSAGE_BODY),
                    Double.valueOf(latLong[0]), Double.valueOf(latLong[1]));
        }
    }

    private void showEmployeeInterface(String message, double latitude, double longitude) {
//        GMapV2Direction md = new GMapV2Direction();
        startButton.setEnabled(true);
        stopButton.setEnabled(true);
        driverLocation = UtilHelper.getLastKnownLocation(this);
        LatLng driverLatLong = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
        storeLatLong = new LatLng(latitude, longitude);
//        Document doc = md.getDocument(sLatLong, dLatLong,
//                GMapV2Direction.MODE_DRIVING);
//        ArrayList<LatLng> directionPoint = md.getDirection(doc);
//        PolylineOptions rectLine = new PolylineOptions().width(3).color(
//                Color.RED);
//
//        for (int i = 0; i < directionPoint.size(); i++) {
//            rectLine.add(directionPoint.get(i));
//        }
//        Polyline polylin = googleMap.addPolyline(rectLine);
        BitmapDescriptor carIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car);
        BitmapDescriptor storeIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_store);
        googleMap.addMarker(new MarkerOptions().position(storeLatLong).title("Current Location")).setIcon(storeIcon);
        googleMap.addMarker(new MarkerOptions().position(driverLatLong).title("Store Location")).setIcon(carIcon);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLong, 13));
        UtilHelper.showAlertDialog(this, "New Message!", message);
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
    public void onClick(View view) {
        if (view.getId() == R.id.start_button) {
            mHandler.sendEmptyMessage(MSG_START_TIMER);
        } else if (view.getId() == R.id.stop_button) {
            mHandler.sendEmptyMessage(MSG_STOP_TIMER);
            long hours = timer.getElapsedTimeSecs() / 3600;
            long mins = (timer.getElapsedTimeSecs() / 60) % 60;
            long secs = timer.getElapsedTimeSecs() % 60;
            Toast.makeText(this, "You took " + hours + " hours, " + mins + " minutes and "
                    + secs + " seconds ", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setEnabled(false);
        startButton.setOnClickListener(DistributorHomeActivity.this);
        stopWatchTv = (TextView) findViewById(R.id.stop_watch_tv);
        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(DistributorHomeActivity.this);
    }

    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    Stopwatch timer = new Stopwatch();
    final int REFRESH_RATE = 1000;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    long hours = timer.getElapsedTimeSecs() / 3600;
                    long mins = (timer.getElapsedTimeSecs() / 60) % 60;
                    long secs = timer.getElapsedTimeSecs() % 60;
                    stopWatchTv.setText(String.format("%02d", hours) + ":" +
                            String.format("%02d", mins) + ":" + String.format("%02d", secs));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    timer.stop();//stop timer
                    hours = timer.getElapsedTimeSecs() / 3600;
                    mins = (timer.getElapsedTimeSecs() / 60) % 60;
                    secs = timer.getElapsedTimeSecs() % 60;
                    stopWatchTv.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onRoutingFailure(RouteException e) {
// The Routing request failed
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
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


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 8);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
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
}
