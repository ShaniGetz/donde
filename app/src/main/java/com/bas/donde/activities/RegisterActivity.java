package com.bas.donde.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bas.donde.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity {
    private final String TAG = "logtagRegisterActivity";


    Button buttonGotoLogin;
    Button buttonRegister;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    boolean isHidden = true;
    private Button showHideBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //

        initializeFields();
        initializeListeners();
    }

    private void initializeFields() {
        buttonGotoLogin = findViewById(R.id.register_button_goto_login);
        buttonRegister = findViewById(R.id.register_button_register);
        editTextEmail = findViewById(R.id.register_editText_email);
        editTextPassword = findViewById(R.id.register_editText_password);
        progressBar = findViewById(R.id.register_progressBar);
        fAuth = FirebaseAuth.getInstance();
        showHideBtn = findViewById(R.id.showHideBtn);
        showHideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    editTextPassword.setTransformationMethod(null);
                    showHideBtn.setBackgroundResource(R.drawable.ic_baseline_remove_red_eye_24);
                    isHidden = false;
                } else {
                    isHidden = true;
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showHideBtn.setBackgroundResource(R.drawable.ic_eye_visibility_off_24);
                }
            }
        });
    }

    private void initializeListeners() {
        buttonGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we call finish because the previous activity on the stack is the login activity
                finish();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String sUserEmail = editTextEmail.getText().toString();
                String sUserPassword = editTextPassword.getText().toString();
                if (isRegisterFieldsValid(sUserEmail, sUserPassword)) {
                    //show progress
                    progressBar.setVisibility(View.VISIBLE);

                    fAuth.createUserWithEmailAndPassword(sUserEmail, sUserPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            gotoAccountActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error creating username: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(RegisterActivity.this, "One or more of the fields is invalid",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        handleUserAlreadyLoggedIn();

    }

    private void handleUserAlreadyLoggedIn() {
        // if a user is already logged in, he shouldn't be on this page
        if (fAuth.getCurrentUser() != null) {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        // prevent option to back-click back to here
        finish();
    }

    private void gotoAccountActivity() {
        Log.d(TAG, "in gotoAccountActivity");
        Intent accountIntent = new Intent(RegisterActivity.this, AccountActivity.class);
        accountIntent.putExtra(getString(R.string.arg_did_come_from_register_intent), true);

        startActivity(accountIntent);
        // prevent option to back-click back to here
        finish();
    }

    /*
    returns true if the login fields are valid (e.g. if they are not empty etc.)
     */
    private boolean isRegisterFieldsValid(String userEmail, String userPassword) {
        boolean isEmailEmpty = TextUtils.isEmpty(userEmail);
        boolean isPasswordEmpty = TextUtils.isEmpty(userPassword);
        return !isEmailEmpty && !isPasswordEmpty;
    }
}
