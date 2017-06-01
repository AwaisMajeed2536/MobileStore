package misbah.naseer.mobilestore.ui;

import android.content.DialogInterface;
import android.content.Intent;
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
import misbah.naseer.mobilestore.helper.MSEditText;
import misbah.naseer.mobilestore.helper.UtilHelper;

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
    DatabaseReference signUpRef;

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
            if(checkInput()){
                UtilHelper.showWaitDialog(this, "Creating Account", "please wait...");
                signUpRef = FirebaseDatabase.getInstance().getReferenceFromUrl
                        ("https://mobilestore-f02a5.firebaseio.com/UserAccounts/");
                signUpRef.child(userInfo.remove("userId")).setValue(userInfo).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UtilHelper.dismissWaitDialog();
                                Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                overridePendingTransition(R.anim.hold_activity, R.anim.enter_activity);
                                finish();
                            }
                        }
                );
            }
        }
    }

    private boolean checkInput() {
        if(TextUtils.isEmpty(accountTypeTv.getText())){
            accountTypeTv.setHint("Select user type");
            mailLayout.scrollTo(0,0);
            return false;
        } else if(TextUtils.isEmpty(userIdTv.getText())){
            userIdTv.setError("Field is required");
            userIdTv.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(firstName.getText())){
            firstName.setError("Field is required");
            firstName.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(lastName.getText())){
            lastName.setError("Field is required");
            lastName.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(email.getText())){
            email.setError("Field is required");
            email.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(password.getText())){
            password.setError("Field is required");
            password.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(confirmPassword.getText())){
            confirmPassword.setError("Field is required");
            confirmPassword.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(mobileNumber.getText())){
            mobileNumber.setError("Field is required");
            mobileNumber.requestFocus();
            return false;
        }
        userInfo.put("userType",accountTypeTv.getText().toString());
        userInfo.put("userId",userIdTv.getText().toString());
        userInfo.put("userName",firstName.getText().toString() + " " + lastName.getText().toString());
        userInfo.put("email",email.getText().toString());
        userInfo.put("password",password.getText().toString());
        userInfo.put("contact",mobileNumber.getText().toString());
        return true;
    }

    private void showClearancePortSelectionDialog() {
        String[] portNames = {"Admin","Distributer", "Store"};
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
