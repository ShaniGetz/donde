package com.bas.donde.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bas.donde.R;
import android.text.method.PasswordTransformationMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity {
    Button buttonGotoLogin;
    Button buttonRegister;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    private Button showHideBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                Log.d("TAG", "sdfgsdg");
                if (showHideBtn.getText().equals("Show")) {
                    editTextPassword.setTransformationMethod(null);
                    showHideBtn.setText("Hide");
                } else {
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showHideBtn.setText("Show");
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

                    fAuth.createUserWithEmailAndPassword(sUserEmail, sUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // register successful
                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "User added successfully",
                                        Toast.LENGTH_LONG).show();
                                gotoAccountActivity();
                            } else {
                                // show error to user
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                            // after progress we don't want to see progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                        }


                    });

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
        Intent mainIntent = new Intent(RegisterActivity.this, AccountActivity.class);
        startActivity(mainIntent);
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
