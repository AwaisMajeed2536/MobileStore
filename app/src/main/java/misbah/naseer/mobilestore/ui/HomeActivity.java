package misbah.naseer.mobilestore.ui;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.model.UserInformationModel;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private ListView driversListLV;
    private List<String> driversList = new ArrayList<>();
    private LinearLayout homeRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeRootView = (LinearLayout) findViewById(R.id.home_root_view);

        //driversListLV = (ListView) findViewById(R.id.drivers_list_lv);
//        driversList = getTempDataForAdmin();
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                android.R.id.text1, driversList);
//        driversListLV.setAdapter(adapter);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UserInformationModel model = UtilHelper.getLoggedInUser();
        if(model.getUserType().equalsIgnoreCase("admin")){
            showAdminInterface(mMap);
        } else{
            showEmployeeInterface(mMap);
        }

    }

    private void showEmployeeInterface(GoogleMap mMap) {
        LinearLayout bottomSection = new LinearLayout(this);
        bottomSection.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.topMargin = 20;
        bottomSection.setLayoutParams(params);
        Button startButton = new Button(this);
        startButton.setText("Start");
        startButton.setLayoutParams(buttonParams);
        Button stopButton = new Button(this);
        stopButton.setText("Stop");
        stopButton.setLayoutParams(buttonParams);
        Button doneButton = new Button(this);
        doneButton.setText("Done");
        doneButton.setLayoutParams(buttonParams);
        bottomSection.addView(startButton);
        bottomSection.addView(stopButton);
        bottomSection.addView(doneButton);
        homeRootView.addView(bottomSection);

        LatLng sydney1 = new LatLng(33.6669636, 73.0545597);
        LatLng sydney2 = new LatLng(33.6808241, 73.0534263);
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Store Location"));
        mMap.addMarker(new MarkerOptions().position(sydney2).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 11));

    }


    private void showAdminInterface(GoogleMap mMap){
        ListView drvrLv = new ListView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                0);
        params.weight = 1.0f;
        drvrLv.setLayoutParams(params);

        driversList = getTempDataForAdmin();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, driversList);
        drvrLv.setAdapter(adapter);
        homeRootView.addView(drvrLv);

        LatLng sydney1 = new LatLng(33.6662536, 74.0553297);
        LatLng sydney2 = new LatLng(33.6529636, 74.0545597);
        LatLng sydney3 = new LatLng(33.6669636, 74.0563597);
        LatLng sydney4 = new LatLng(33.6529636, 74.0545597);
        LatLng sydney5 = new LatLng(33.6665236, 74.0525537);
        LatLng sydney6 = new LatLng(33.6529636, 74.0546997);
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Driver1"));
        mMap.addMarker(new MarkerOptions().position(sydney2).title("Driver2"));
        mMap.addMarker(new MarkerOptions().position(sydney3).title("Driver3"));
        mMap.addMarker(new MarkerOptions().position(sydney4).title("Driver4"));
        mMap.addMarker(new MarkerOptions().position(sydney5).title("Driver5"));
        mMap.addMarker(new MarkerOptions().position(sydney6).title("Driver6"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 11));
    }


    public List<String> getTempDataForAdmin() {
        List<String> returnList = new ArrayList<>();
        for(int i=1; i<7; i++){
            returnList.add("Driver"+i);
        }
        return returnList;
    }
}
