package com.example.donde.archive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.donde.R;
import com.google.firebase.auth.FirebaseAuth;

//import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    NavController navController;
    private int RC_SIGN_IN = 9001;
    private String TAG = "Main activity";
    private ProgressBar startProgress;
    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//// Create and launch sign-in intent
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);
//
//        AuthUI.getInstance()
//                .signOut(this.getContext())
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//        AuthUI.getInstance()
//                .delete(this.getContext())
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == RC_SIGN_IN) {
////            IdpResponse response = IdpResponse.fromResultIntent(data);
////
////            if (resultCode == RESULT_OK) {
////                // Successfully signed in
////                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////
//////                TextView welcomeTextView = findViewById(R.id.textview_welcome);
//////                welcomeTextView.setText(welcomeTextView.getText().toString() + user.getDisplayName());
////
////                // ...
////                Log.i(TAG, "signed in succes");
////            } else {
////                // Sign in failed. If response is null the user canceled the
////                // sign-in flow using the back button. Otherwise check
////                // response.getError().getErrorCode() and handle the error.
////                // ...
////                Log.i(TAG, "signed in failed");
////            }
////        }
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStart() {
        super.onStart();
//        navController.navigate(R.id.fragment);
    }
}
