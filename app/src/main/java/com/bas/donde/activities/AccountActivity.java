package com.bas.donde.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bas.donde.BuildConfig;
import com.bas.donde.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class AccountActivity extends Activity {
    private final String TAG = "tagAccountActivity";
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonDeleteAccount;
    private EditText textViewName;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersCollectionRef;
    private String userID;
    private String userEmail;
    private String userProfilePicURL;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    Button buttonChangeProfilePic;
    ImageView profileImage;


    Uri DEFAULT_PROFILE_PIC_URI;


//            parse("android.resource://com.example.donde/drawable/avatar2.png");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initializeFields();
        initializeListeners();
        retrieveAccountDetails();
        // TODO: Default pic shoud not upload on account edit
        DEFAULT_PROFILE_PIC_URI=(new Uri.Builder())
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getResources().getResourcePackageName(R.drawable.avatar2))
                .appendPath(getResources().getResourceTypeName(R.drawable.avatar2))
                .appendPath(getResources().getResourceEntryName(R.drawable.avatar2))
                .build();
        setProfilePic();

    }


    private void retrieveAccountDetails() {
        // details are loading
        progressBar.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);
        // TODO: set other buttons as enabled/disabled

        // check if user with given ID exists
        CollectionReference usersRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        Query userExistsQuery = usersRef.whereEqualTo(getString(R.string.ff_Users_userID), userID);
        userExistsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (BuildConfig.DEBUG && task.getResult().size() > 1) {
                        throw new AssertionError("More than one user exists with given ID");
                    } else if (task.getResult().size() == 1) {
                        DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                        String userName =
                                userDocument.getString(getString(R.string.ff_Users_userName));
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
        buttonCancel = findViewById(R.id.account_button_cancel);
        buttonDeleteAccount = findViewById(R.id.account_button_delete_account);
        textViewName = findViewById(R.id.account_editText_name);
        progressBar = findViewById(R.id.account_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        if (firebaseAuth.getCurrentUser() != null) {
            buttonDeleteAccount.setVisibility(View.VISIBLE);
        }
        fAuth = FirebaseAuth.getInstance();
        buttonChangeProfilePic = findViewById(R.id.change_profile_pic);
        profileImage = findViewById(R.id.profile_pic);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
    }

    private void initializeListeners() {
        setButtonSaveOnClick();
        setButtonCancelOnClick();
        setButtonDeleteAccountOnClick();
        buttonChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
//                profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase storage
//        userProfilePicURL = "avatar2.png";
        setProfilePic(imageUri);
    }

    private void setButtonCancelOnClick() {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMainActivity();
            }
        });
    }

    private void setButtonDeleteAccountOnClick() {
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: handle deleting users from events theyre invited to
                // delete user from Users collection
                usersCollectionRef.document(userID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AccountActivity.this, String.format("Successfully deleted " +
                                        "account with email %s", userEmail),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(AccountActivity.this, String.format("Failed to delete " +
                                        "account with email %s, error:", userEmail, e.getMessage()),
                                Toast.LENGTH_SHORT).show();
                    }
                });

                // delete user from Events collection


                // delete user from authentication
                firebaseAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully deleted user from auth");
                        gotoLoginActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountActivity.this, "Failed to delete user from FirebaseAuth", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void setProfilePic(){
        setProfilePic(DEFAULT_PROFILE_PIC_URI);
    }

    private void setProfilePic(Uri imageUri) {
        Toast.makeText(this, "`adding deafult pic", Toast.LENGTH_SHORT).show();
        StorageReference fileRef = storageReference.child(fAuth.getCurrentUser().getUid() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        userProfilePicURL = fAuth.getCurrentUser().getUid()+".jpg";
                        Toast.makeText(AccountActivity.this, "`added default pic", Toast.LENGTH_SHORT).show();
                    };
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this, "Failed", Toast.LENGTH_SHORT);
            }
        });
    }


    private void setButtonSaveOnClick() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sUserName = textViewName.getText().toString();
                if (isSaveFieldsValid(sUserName)) {
                    //show progress
                    progressBar.setVisibility(View.VISIBLE);
                    // TODO: check if this finishes before saving user
                    // get user ID for current user
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put(getString(R.string.ff_Users_userID), userID);
                    userMap.put(getString(R.string.ff_Users_userEmail), userEmail);
                    userMap.put(getString(R.string.ff_Users_userName), sUserName);
                    userMap.put("userProfilePicURL", userProfilePicURL);
                    userMap.put("userTimeCreatedProfile", Timestamp.now());
                    usersCollectionRef.document(userID).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void gotoLoginActivity() {
        Intent loginIntent = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        // prevent option to back-click back to here
        finish();
    }

    private void gotoMainActivity() {
        Intent mainIntent = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(mainIntent);
        // prevent option to back-click back to here
        finish();
    }
}
