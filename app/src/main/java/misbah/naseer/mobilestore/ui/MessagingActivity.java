package misbah.naseer.mobilestore.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.adapter.MessagingAdapter;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.interfaces.AlertDialogCallback;
import misbah.naseer.mobilestore.interfaces.ClickListenCallback;

/**
 * Created by Devprovider on 26/07/2017.
 */

public class MessagingActivity extends AppCompatActivity implements ClickListenCallback {

    private ListView messagingLV;
    private List<HashMap<String, String>> dataList = new ArrayList<>();
    private ArrayList<String> driversList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        messagingLV = (ListView) findViewById(R.id.messages_lv);
        driversList = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.DRIVERS_LIST_KEY,""), new TypeToken<ArrayList<String>>(){}.getType());
        if(getIntent().getExtras() != null){
            dataList.add((HashMap<String, String>) getIntent().getSerializableExtra(Constants.MESSAGES_PASS_KEY));
            if(dataList != null && dataList.size() >0 && dataList.get(0) != null) {
                MessagingAdapter adapter = new MessagingAdapter(this, dataList, this);
                messagingLV.setAdapter(adapter);
            }
            else{
                goBack();
            }
        } else {
            goBack();
        }
    }

    private void goBack() {
        UtilHelper.showAlertDialog(this, "No Messages", "you have no new messages", "go back",
                new AlertDialogCallback() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
    }

    @Override
    public void clicked(final int position) {
        final String [] dList = driversList.toArray(new String[driversList.size()]);
        new AlertDialog.Builder(this).setTitle("Choose Driver").setSingleChoiceItems(dList, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forwardMessage(dList[which].split("-")[1], position);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public void forwardMessage(final String clickedDistributor, final int position){
        if (clickedDistributor.startsWith("s")){
            UtilHelper.showAlertDialog(this, "Distributor Selected!", "You cannot send this message to a store",
                    "Retry", new AlertDialogCallback() {
                        @Override
                        public void onClick() {
                        }
                    });
            return;
        }
        UtilHelper.showWaitDialog(this, "Sending Message", "please wait...");
        final DatabaseReference messageRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages/" + clickedDistributor);
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int messageNumber = (int) dataSnapshot.getChildrenCount();
                HashMap<String, String> fwMessage = dataList.get(position);
                fwMessage.put(Constants.MESSAGE_FROM, "a01");
                messageRef.child(String.valueOf(messageNumber)).setValue(fwMessage)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UtilHelper.dismissWaitDialog();
                                new AlertDialog.Builder(MessagingActivity.this).setTitle("Message Deliverd")
                                        .setMessage("your message was successfully delivered to " + clickedDistributor)
                                        .create().show();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
