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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bas.donde.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends Activity {
    private Button buttonLogin;
    private Button showHideBtn;

    private TextView textViewGotoRegister;
    private EditText textViewUserEmail;
    private EditText textViewUserPassword;
    private ProgressBar progressBar;
    private boolean isHidden = true;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();
        initializeListeners();

    }

    private void initializeFields() {
        buttonLogin = findViewById(R.id.login_button_login);
        textViewGotoRegister = findViewById(R.id.login_textView_goto_register);
        textViewUserEmail = findViewById(R.id.login_editText_email);
        textViewUserPassword = findViewById(R.id.login_editText_password);
        progressBar = findViewById(R.id.login_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        showHideBtn = findViewById(R.id.showHideBtn);
        showHideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    textViewUserPassword.setTransformationMethod(null);
                    showHideBtn.setBackgroundResource(R.drawable.ic_baseline_remove_red_eye_24);
                    isHidden = false;
                } else {
                    isHidden = true;
                    textViewUserPassword.setTransformationMethod(new PasswordTransformationMethod());
                    showHideBtn.setBackgroundResource(R.drawable.ic_eye_visibility_off_24);
                }
            }
        });
    }

    private void initializeListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sUserEmail = textViewUserEmail.getText().toString();
                String sUserPassword = textViewUserPassword.getText().toString();

                if (isLoginFieldsValid(sUserEmail, sUserPassword)) {
                    //show progress
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(sUserEmail, sUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // login successful
                            if (task.isSuccessful()) {

                                Toast.makeText(LoginActivity.this, "User Logged in successfully",
                                        Toast.LENGTH_SHORT).show();
                                gotoMainActivity();
                            } else {
                                // show error to user
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            }
                            // after progress we don't want to see progress bar
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                } else{
                    Toast.makeText(LoginActivity.this, "please fill out email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegister();

            }
        });
    }

    /*
    returns true if the login fields are valid (e.g. if they are not empty etc.)
     */
    private boolean isLoginFieldsValid(String userEmail, String userPassword) {
        boolean isEmailEmpty = TextUtils.isEmpty(userEmail);
        boolean isPasswordEmpty = TextUtils.isEmpty(userPassword);
        return !isEmailEmpty && !isPasswordEmpty;
    }

    @Override
    protected void onStart() {
        super.onStart();

        handleUserAlreadyLoggedIn();

    }

    private void handleUserAlreadyLoggedIn() {
        // if a user is already logged in, he shouldn't be on this page
        if (firebaseAuth.getCurrentUser() != null) {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        // prevent option to back-click back to here
        finish();
    }

    private void gotoRegister() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }


}
