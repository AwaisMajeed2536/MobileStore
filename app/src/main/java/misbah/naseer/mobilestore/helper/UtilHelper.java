package misbah.naseer.mobilestore.helper;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.interfaces.AlertDialogCallback;
import misbah.naseer.mobilestore.model.UserInformationModel;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Devprovider on 28/04/2017.
 */

public class UtilHelper {

    private static Context staticContext;
    private static ProgressDialog waitDialog;
    private static Location bestLocation;

    public static void setContext(Context mcontext) {
        staticContext = mcontext;
    }

    public static void GoToActivityAsNewTask(Activity context, Class cls, int enterTransition, int exitTransition) {
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
        if (enterTransition != 0)
            context.overridePendingTransition(enterTransition, exitTransition);
    }

    public static void showMessage(Context context, String message){
        new AlertDialog.Builder(context).setTitle("Alert!").setMessage(message).create().show();
    }

    public static void initGenericToolbar(final AppCompatActivity context, String title, boolean isHomeEnabled) {
        View viewActionBar = context.getLayoutInflater().inflate(R.layout.actionbar_home_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.app_bar_title);
        textviewTitle.setText(title);
        context.getSupportActionBar().setCustomView(viewActionBar, params);
        context.getSupportActionBar().setDisplayShowCustomEnabled(true);
        context.getSupportActionBar().setDisplayShowTitleEnabled(false);
        context.getSupportActionBar().setDisplayHomeAsUpEnabled(isHomeEnabled);
    /*     context.getActionBar().setCustomView(viewActionBar, params);
        context.getActionBar().setDisplayShowCustomEnabled(true);
        context.getActionBar().setDisplayShowTitleEnabled(false);
        context.getActionBar().setDisplayHomeAsUpEnabled(false);
        context.getActionBar().setHomeButtonEnabled(true);*/


    }

    public static void showAlertDialog(Context context, String title, @Nullable String message) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
                .setMessage("" + message).setCancelable(true)
                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public static void showAlertDialog(Context context, @Nullable String title, @Nullable String message,
                                       String buttonText, final AlertDialogCallback callback) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title)
                .setMessage("" + message).setCancelable(true)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onClick();
                    }
                }).create();
        dialog.show();
    }

    public static void showWaitDialog(Context context, @Nullable String title, @Nullable String message) {
        try {
            if (waitDialog != null) {
                if (!waitDialog.isShowing()) {
                    waitDialog = new ProgressDialog(context);
                    waitDialog.setTitle(title);
                    waitDialog.setMessage(message);
                    waitDialog.setIndeterminate(true);
                    waitDialog.setCancelable(false);
                    waitDialog.show();
                }
            } else {
                waitDialog = new ProgressDialog(context);
                waitDialog.setTitle(title);
                waitDialog.setMessage(message);
                waitDialog.setIndeterminate(true);
                waitDialog.setCancelable(false);
                waitDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissWaitDialog() {
        try {
            if (waitDialog != null && waitDialog.isShowing()) {
                waitDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static final String USER_INFO_KEY = "user_info_key";

    public static void createLoginSession(Context context, UserInformationModel userInfo) {
        staticContext = context;
        String json = new Gson().toJson(userInfo);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(USER_INFO_KEY, json).apply();
    }

    public static boolean isUserLoggedIn(Context context) {
        staticContext = context;
        String userInfo = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_INFO_KEY, null);
        return userInfo != null;
    }

    public static UserInformationModel getLoggedInUser(Context context) {
        String userInfo = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_INFO_KEY, null);
        if (userInfo == null)
            return null;
        return new Gson().fromJson(userInfo, UserInformationModel.class);
    }

    public static void endLoginSession(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Typeface getFont() {
        return Typeface.createFromAsset(staticContext.getAssets(), "fonts/RobotoRegular.ttf");
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        bestLocation = null;
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }

        }
        if (bestLocation == null) {
            requestLocationUpdates(context, locationManager);
        }
        return bestLocation;
    }

    private static void requestLocationUpdates(final Context context, LocationManager locationManager) {
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }

    public static String getCityNameFromLocation(@NonNull Context context, @NonNull Location location) {
        String cityAndCountryNanme = "";
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                cityAndCountryNanme = addresses.get(0).getLocality();
                cityAndCountryNanme += ", " + addresses.get(0).getCountryName();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return cityAndCountryNanme;
    }

    public static Location getLocationObject(Double latitude, Double longitude){
        Location location = new Location("GPS");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public static String getRandomId(){
        return "item" + Calendar.getInstance().getTimeInMillis();
    }
}
