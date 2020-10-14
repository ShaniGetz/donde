package com.bas.donde.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bas.donde.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "tagMainActivity";
    private FloatingActionButton fabCreateEvent;
    private FirebaseAuth mAuth;
    private CollectionReference usersCollectionRef;

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    private void initializeFields() {
        mAuth = FirebaseAuth.getInstance();
        usersCollectionRef = FirebaseFirestore.getInstance().collection(getString(R.string.ff_Users));
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
//        getWindow().setFormat(PixelFormat.RGBA_8888);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.activity_main);
        initializeFields();
        initializeListeners();
    }


    @Override
    protected void onStart() {

        super.onStart();
        Log.d(TAG, "in onStart");
//      if current user is null, we want to log in
        checkIfUserExistsAndAct();

    }

    private void checkIfUserExistsAndAct() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // if user does not exist in auth, goto login
        if (currentUser == null) {
            gotoLogin();
        } else {

            // if he does exist in auth, he might still posses token (remains for one hour after
            // accout is deleted from auth
            String uid = currentUser.getUid();
            DocumentReference userRef = usersCollectionRef.document(uid);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists()) {
                        gotoLogin();
                    } else {
                        Log.d(TAG, String.format("User with email %s is logged in", currentUser.getEmail()));
                        String userName = documentSnapshot.getString(getString(R.string.ff_Users_userName));
                    }
                }

            });
        }
    }

    private void gotoLogin() {
        Log.d(TAG, "in goToLogin");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        // finish() makes sure user can't press back button and get here
        finish();
    }

    private void gotoAccount() {
        Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
        accountIntent.putExtra(getString(R.string.arg_did_come_from_register_intent), false);

        startActivity(accountIntent);
        // finish() makes sure user can't press back button and get here
//        finish();
    }


    private void logout() {
        mAuth.signOut();
        gotoLogin();
        finish();
    }
}