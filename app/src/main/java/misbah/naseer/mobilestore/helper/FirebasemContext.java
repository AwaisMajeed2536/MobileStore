package misbah.naseer.mobilestore.helper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Awais on 2/20/2017.
 */

public class FirebasemContext extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
