package com.example.donde.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donde.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabCreateEvent;
    private FirebaseAuth mAuth;

    private void initializeFields() {
        mAuth = FirebaseAuth.getInstance();
        fabCreateEvent = findViewById(R.id.main_create_fab);
    }

    private void initializeListeners() {
        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createEventIntent = new Intent(MainActivity.this,
                        CreateEventActivity.class);
                startActivity(createEventIntent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            case R.id.menu_account:
                gotoAccount();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeFields();
        initializeListeners();
    }


    @Override
    protected void onStart() {

        super.onStart();

//      if current user is null, we want to log in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            gotoToLogin();
        }
//
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser myUser = mAuth.getCurrentUser(); // ==null if no one is logged in
//        String myUserId = myUser.getUid();
//        Toast.makeText(this, "My ID is: " + myUserId, Toast.LENGTH_SHORT).show();
//
//
//        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
//        CollectionReference usersReference = mFirestore.collection("Users");
//        usersReference.document(myUserId).get().addOnCompleteListener(
//                new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        String userName = task.getResult().get("name").toString();
//                        Toast.makeText(MainActivity.this, "The current user name is: " + userName,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );

    }

    private void gotoToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        // finish() makes sure user can't press back button and get here
        finish();
    }

    private void gotoAccount() {
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intent);
        // finish() makes sure user can't press back button and get here
        finish();
    }


    private void logout() {
        mAuth.signOut();
        gotoToLogin();
    }
}