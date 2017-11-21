package misbah.naseer.mobilestore.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import misbah.naseer.mobilestore.R;
import misbah.naseer.mobilestore.helper.Constants;
import misbah.naseer.mobilestore.helper.MSButton;
import misbah.naseer.mobilestore.helper.MSEditText;
import misbah.naseer.mobilestore.helper.UtilHelper;
import misbah.naseer.mobilestore.model.UserInformationModel;

import static misbah.naseer.mobilestore.helper.Constants.USER_TYPE_ADMIN;
import static misbah.naseer.mobilestore.helper.Constants.USER_TYPE_DISTRIBUTOR;
import static misbah.naseer.mobilestore.helper.Constants.USER_TYPE_STORE;

public class LandingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public static final String TAG = "LANDING_ACTIVITY";

    private static final int RC_SIGN_IN = 647;
    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;
    private EditText emailET;
    private EditText passwordET;
    private Button signInButton;
    private Button signUpButton;
    private DatabaseReference signinRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        initView();
        if(UtilHelper.isUserLoggedIn(this)){
            login();
        }
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        //MSButton signInButton = (MSButton) findViewById(R.id.sign_in_google_button);

        //signInButton.setOnClickListener(this);
    }

    private void login() {
        UserInformationModel model = UtilHelper.getLoggedInUser(this);
        if(model == null){
            return;
        }
        Intent intent = null;
        if (model.getUserType().equalsIgnoreCase(USER_TYPE_ADMIN)) {
            intent = new Intent(LandingActivity.this, AdminHomeActivity.class);
        } else if (model.getUserType().equalsIgnoreCase(USER_TYPE_DISTRIBUTOR)) {
            intent = new Intent(LandingActivity.this, DistributorHomeActivity.class);
        } else if (model.getUserType().equalsIgnoreCase(USER_TYPE_STORE)) {
            intent = new Intent(LandingActivity.this, StoreHomeActivity.class);
        }
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "SignIn successful!", Toast.LENGTH_SHORT).show();
            //GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    private boolean checkInputs() {
        if (TextUtils.isEmpty(emailET.getText())) {
            emailET.setError("Enter a valid email");
            emailET.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(passwordET.getText())) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return false;
        }
        return true;
    }

    private void initView() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(this);
        emailET = (EditText) findViewById(R.id.email_et);
        passwordET = (EditText) findViewById(R.id.password_et);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.sign_in_google_button:
            //signIn();
            //break;
            case R.id.sign_in_button:
                if (checkInputs()) {
                    UtilHelper.showWaitDialog(this, "Authenticating", "please wait...");
                    signinRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://mobilestore-f02a5.firebaseio.com/UserAccounts/"+emailET.getText().toString());
                    signinRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UtilHelper.dismissWaitDialog();
                            if(dataSnapshot.getValue() == null){
                                UtilHelper.showAlertDialog(LandingActivity.this, "Login Failed!", "Invalid email or password...");
                            } else{
                                HashMap<String, String> userData = (HashMap<String, String>) dataSnapshot.getValue();
                                userData.put(Constants.USER_ID, dataSnapshot.getKey());
                                UserInformationModel userInfo = new UserInformationModel(userData.get(Constants.USER_ID),
                                        userData.get(Constants.USER_NAME), userData.get(Constants.CONTACT), userData.get(Constants.EMAIL),
                                        String.valueOf(userData.get(Constants.PASSWORD)), userData.get(Constants.USER_TYPE));
                                UtilHelper.createLoginSession(LandingActivity.this, userInfo);
                                if(userInfo.getPassword().equals(passwordET.getText().toString())){
                                    login();
                                } else{
                                    UtilHelper.showAlertDialog(LandingActivity.this, "Login Failed!", "Invalid email or password...");
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                break;
            case R.id.sign_up_button:
                startActivity(new Intent(this, SignupActivity.class));
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

}
