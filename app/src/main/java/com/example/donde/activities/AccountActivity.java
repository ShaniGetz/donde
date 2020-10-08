package com.example.donde.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.donde.BuildConfig;
import com.example.donde.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class AccountActivity extends Activity {
    private Button buttonSave;
    private EditText textViewName;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initializeFields();
        initializeListeners();
        retrieveAccountDetails();

    }


    private void retrieveAccountDetails() {
        // details are loading
        progressBar.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);

        // check if user with given ID exists
        CollectionReference usersRef = firebaseFirestore.collection(getString(R.string.ff_users_collection));
        Query userExistsQuery = usersRef.whereEqualTo(getString(R.string.ff_users_userID), userID);
        userExistsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (BuildConfig.DEBUG && task.getResult().size() > 1) {
                        throw new AssertionError("More than one user exists with given ID");
                    } else if (task.getResult().size() == 1) {
                        DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                        String userName = userDocument.getString(getString(R.string.ff_users_userName));
                        textViewName.setText(userName);
                    }
                } else {
                    Toast.makeText(AccountActivity.this, String.format("Error retrieving user details: %s", task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                }
                // details finished loading
                progressBar.setVisibility(View.INVISIBLE);
                buttonSave.setEnabled(true);
            }
        });
    }


    private void initializeFields() {
        buttonSave = findViewById(R.id.account_button_save);
        textViewName = findViewById(R.id.account_editText_name);
        progressBar = findViewById(R.id.account_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
    }

    private void initializeListeners() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sUserName = textViewName.getText().toString();
                if (isSaveFieldsValid(sUserName)) {
                    //show progress
                    progressBar.setVisibility(View.VISIBLE);
                    // get user ID for current user
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put(getString(R.string.ff_users_userID), userID);
                    userMap.put(getString(R.string.ff_users_userEmail), userEmail);
                    userMap.put(getString(R.string.ff_users_userName), sUserName);
                    firebaseFirestore.collection(getString(R.string.ff_users_collection)).document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // account save successful
                            if (task.isSuccessful()) {

                                Toast.makeText(AccountActivity.this, "Account updated successfully",
                                        Toast.LENGTH_LONG).show();
                                gotoMainActivity();
                            } else {
                                // show error to user
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(AccountActivity.this, "Error: " + errorMessage,
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

    /*
    returns true if the login fields are valid (e.g. if they are not empty etc.)
     */
    private boolean isSaveFieldsValid(String userName) {
        boolean isNameEmpty = TextUtils.isEmpty(userName);
        return !isNameEmpty;
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    private void gotoMainActivity() {
        Intent mainIntent = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(mainIntent);
        // prevent option to back-click back to here
        finish();
    }
}
