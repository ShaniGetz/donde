//package com.example.donde;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentActivity;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//
//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.IdpResponse;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    int RC_SIGN_IN = 9001;
//    String TAG = "Main activity";
////    private NavController navController;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////
////        // Choose authentication providers
////        List<AuthUI.IdpConfig> providers = Arrays.asList(
////                new AuthUI.IdpConfig.EmailBuilder().build(),
////                new AuthUI.IdpConfig.GoogleBuilder().build()
////        );
////// Create and launch sign-in intent
////        startActivityForResult(
////                AuthUI.getInstance()
////                        .createSignInIntentBuilder()
////                        .setAvailableProviders(providers)
////                        .build(),
////                RC_SIGN_IN);
////
////        AuthUI.getInstance()
////                .signOut(this)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    public void onComplete(@NonNull Task<Void> task) {
////                        // ...
////                    }
////                });
////        AuthUI.getInstance()
////                .delete(this)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        // ...
////                    }
////                });
////
//
////        navController = Navigation.findNavController(this.);
//
//    }
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        if (requestCode == RC_SIGN_IN) {
////            IdpResponse response = IdpResponse.fromResultIntent(data);
////
////            if (resultCode == RESULT_OK) {
////                // Successfully signed in
////                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////
//////                TextView welcomeTextView = findViewById(R.id.textview_welcome);
//////                welcomeTextView.setText(welcomeTextView.getText().toString() +user.getDisplayName());
////
////                // ...
////                Log.i(TAG, "signed in success");
////            } else {
////                // Sign in failed. If response is null the user canceled the
////                // sign-in flow using the back button. Otherwise check
////                // response.getError().getErrorCode() and handle the error.
////                // ...
////                Log.i(TAG, "signed in failed");
////            }
////        }else{
////            Log.i(TAG, "signed in failed");
////        }
////    }
//}
package com.example.donde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int RC_SIGN_IN = 9001;
    String TAG = "ActivityMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i(TAG, "Succusfuly logged in");
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.i(TAG, "Not Succusfuly logged in");
            }
        } else {

            Log.i(TAG, "Not Succusfuly logged in");
        }
    }
}