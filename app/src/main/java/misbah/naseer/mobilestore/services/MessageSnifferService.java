package misbah.naseer.mobilestore.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.model.UserInformationModel;

public class MessageSnifferService extends Service {
    public static final String TAG = "Message Receiver";
    public static final String BROADCAST_ACTION = "action.mobilestore.message";
    private DatabaseReference admMessageRef;
    private DatabaseReference disMessageRef;
    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service created");
        intent = new Intent(BROADCAST_ACTION);
        admMessageRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages/a01");
        UserInformationModel user = UtilHelper.getLoggedInUser(this);
        if (user != null && user.getUserType().equalsIgnoreCase(Constants.USER_TYPE_DISTRIBUTOR)) {
            disMessageRef = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages/" + user.getUserId());
        }
        setUpNotificationListener();
    }

    private void setUpNotificationListener() {
        admMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, String>> messages =
                        (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                int lastMessage = (int) dataSnapshot.getChildrenCount();
                HashMap<String, String> messageData = messages.get(lastMessage - 1);
                intent.putExtra(Constants.SERVICE_DATA_PASS_KEY, messageData);
                sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (disMessageRef != null) {
            disMessageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<HashMap<String, String>> messages =
                            (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                    int lastMessage = (int) dataSnapshot.getChildrenCount();
                    HashMap<String, String> messageData = messages.get(lastMessage - 1);
                    intent.putExtra(Constants.SERVICE_DATA_PASS_KEY, messageData);
                    sendBroadcast(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v(TAG, "STOP_SERVICE");
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }
}