package misbah.naseer.mobilestore.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.UtilHelper;
import okhttp3.internal.Util;

import static misbah.naseer.mobilestore.helper.Constants.CONTACT;
import static misbah.naseer.mobilestore.helper.Constants.USER_TYPE_ADMIN;
import static misbah.naseer.mobilestore.helper.Constants.USER_TYPE_DISTRIBUTOR;
import static misbah.naseer.mobilestore.helper.Constants.USER_TYPE_STORE;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    protected EditText accountTypeTv;
    protected EditText userIdTv;
    protected EditText firstName;
    protected EditText lastName;
    protected EditText email;
    protected EditText password;
    protected EditText confirmPassword;
    protected EditText mobileNumber;
    protected Button signupButton;
    protected RelativeLayout mailLayout;
    private HashMap<String, String> userInfo = new HashMap<>();
    DatabaseReference signUpRefAccountData;
    DatabaseReference signUpRefUserLocations;
    DatabaseReference signUpRefMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_signup);
        initView();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.account_type_tv) {
            showClearancePortSelectionDialog();
        } else if (view.getId() == R.id.signup_button) {
            if (checkInput()) {
                UtilHelper.showWaitDialog(this, "Creating Account", "please wait...");
                signUpRefAccountData = FirebaseDatabase.getInstance().getReferenceFromUrl
                        ("https://mobilestore-f02a5.firebaseio.com/UserAccounts/");
                signUpRefUserLocations = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userLocations");
                signUpRefMessages = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/userMessages");
                String userId = userInfo.remove(Constants.USER_ID);

                //setting a node in locations data
                Location location = UtilHelper.getLastKnownLocation(this);
                HashMap<String, String> locationRefData = new HashMap<>();
                locationRefData.put(Constants.LAT_KEY, String.valueOf(location.getLatitude()));
                locationRefData.put(Constants.LONG_KEY, String.valueOf(location.getLongitude()));
                signUpRefUserLocations.child(userId).setValue(locationRefData);

                //setting a node in messages data
                HashMap<String, String> messagesRefData = new HashMap<>();
                messagesRefData.put(Constants.MESSAGE_FROM, "nill");
                messagesRefData.put(Constants.MESSAGE_BODY, "nill");
                messagesRefData.put(Constants.MESSAGE_LOCATION, "nill");
                messagesRefData.put(Constants.MESSAGE_DATE, "nill");
                signUpRefMessages.child(userId).child("0").setValue(messagesRefData);

                //setting user account data
                signUpRefAccountData.child(userId).setValue(userInfo).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UtilHelper.dismissWaitDialog();
                                Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                moveForward(userInfo.get(Constants.USER_TYPE));
                                overridePendingTransition(R.anim.hold_activity, R.anim.enter_activity);
                                finish();
                            }
                        }
                );
            }
        }
    }

    private void moveForward(String userType) {
        Intent intent = null;
        if (userType.equalsIgnoreCase(USER_TYPE_ADMIN)) {
            intent = new Intent(this, AdminHomeActivity.class);
        } else if (userType.equalsIgnoreCase(USER_TYPE_DISTRIBUTOR)) {
            intent = new Intent(this, DistributorHomeActivity.class);
        } else if (userType.equalsIgnoreCase(USER_TYPE_STORE)) {
            intent = new Intent(this, StoreHomeActivity.class);
        }
        if (intent != null)
            startActivity(intent);
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(accountTypeTv.getText())) {
            accountTypeTv.setHint("Select user type");
            mailLayout.scrollTo(0, 0);
            return false;
        } else if (TextUtils.isEmpty(userIdTv.getText())) {
            userIdTv.setError("Field is required");
            userIdTv.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(firstName.getText())) {
            firstName.setError("Field is required");
            firstName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(lastName.getText())) {
            lastName.setError("Field is required");
            lastName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email.getText())) {
            email.setError("Field is required");
            email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password.getText())) {
            password.setError("Field is required");
            password.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirmPassword.getText())) {
            confirmPassword.setError("Field is required");
            confirmPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(mobileNumber.getText())) {
            mobileNumber.setError("Field is required");
            mobileNumber.requestFocus();
            return false;
        }
        userInfo.put(Constants.USER_TYPE, accountTypeTv.getText().toString());
        userInfo.put(Constants.USER_ID, userIdTv.getText().toString());
        userInfo.put(Constants.USER_NAME, firstName.getText().toString() + " " + lastName.getText().toString());
        userInfo.put(Constants.EMAIL, email.getText().toString());
        userInfo.put(Constants.PASSWORD, password.getText().toString());
        userInfo.put(Constants.CONTACT, mobileNumber.getText().toString());
        return true;
    }

    private void showClearancePortSelectionDialog() {
        String[] portNames = {"Admin", "Distributor", "Store"};
        int checkedPort = -1;
        String checkedPortName = accountTypeTv.getText().toString();
        for (int i = 0; i < portNames.length; i++) {
            if (portNames[i].trim().equalsIgnoreCase(checkedPortName.trim()))
                checkedPort = i;
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Select Clearance Port in Pakistan")
                .setSingleChoiceItems(portNames, checkedPort, null).setCancelable(true)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        if (lv.getCheckedItemPosition() != -1) {
                            String selectedPort = lv.getAdapter().getItem(lv.getCheckedItemPosition()).toString();
                            accountTypeTv.setText(selectedPort);
                            dialog.dismiss();
                        }
                    }
                }).create();
        dialog.show();
    }

    private void initView() {
        mailLayout = (RelativeLayout) findViewById(R.id.main_layout);
        accountTypeTv = (EditText) findViewById(R.id.account_type_tv);
        accountTypeTv.setOnClickListener(this);
        userIdTv = (EditText) findViewById(R.id.user_id_tv);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        mobileNumber = (EditText) findViewById(R.id.mobile_number);
        signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(this);
    }
}
