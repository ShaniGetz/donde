package com.bas.donde.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bas.donde.R;
import com.bas.donde.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.bas.donde.utils.CodeHelpers.myAssert;


public class AccountActivity extends Activity {
    // Constants
    private final String TAG = "tagAccountActivity";
    private final Uri DEFAULT_AVATAR_RES_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResources().getResourcePackageName(R.drawable.avatar2))
            .appendPath(getResources().getResourceTypeName(R.drawable.avatar2))
            .appendPath(getResources().getResourceEntryName(R.drawable.avatar2))
            .build();
    private final String DEFAULT_AVATAR_STORAGE_STRING = "avatar2.png";

    private final Uri DEFAULT_AVATAR_STORAGE_URI = Uri.parse(DEFAULT_AVATAR_STORAGE_STRING);


    // Firebase
    private FirebaseStorage firebaseStorage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersCollectionRef;

    // Views
    private Button buttonChangeProfilePic;
    private ImageView profileImage;
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonDeleteAccount;
    private EditText textViewName;
    private ProgressBar progressBar;

    // Data
    private String userID;
    private String userEmail;
    private String userName;
    private boolean didComeFromRegister;
    private String userProfilePicURI;
    private String newUserProfilePicURI;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);
        initializeFields();
        didComeFromRegister = savedInstanceState.getBoolean(getString(R.string.arg_did_come_from_register_intent));
        if (didComeFromRegister) {
            initializeFromRegister();

        } else {
            initializeFromUpdate();
        }

    }

    private void initializeFields() {
        initializeViews();
        initializeFirebaseFields();
        initializeDataFields();
    }

    private void initializeViews() {
        buttonChangeProfilePic = findViewById(R.id.account_change_profile_pic);
        profileImage = findViewById(R.id.account_profile_pic);
        buttonSave = findViewById(R.id.account_button_save);
        buttonCancel = findViewById(R.id.account_button_cancel);
        buttonDeleteAccount = findViewById(R.id.account_button_delete_account);
        textViewName = findViewById(R.id.account_editText_name);
        progressBar = findViewById(R.id.account_progressBar);
        setSaveButtonOnClick();
    }


    private void initializeFirebaseFields() {
        myAssert(FirebaseAuth.getInstance().getCurrentUser() != null, "User in null upon account " +
                "activity");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        firebaseStorage = FirebaseStorage.getInstance();

    }

    private void initializeDataFields() {
        userID = firebaseUser.getUid();
        userEmail = firebaseUser.getEmail();
        userName = "";
    }


    private void initializeFromRegister() {
        buttonCancel.setVisibility(View.INVISIBLE);
        buttonDeleteAccount.setVisibility(View.INVISIBLE);
        buttonChangeProfilePic.setText("Add profile picture");
        userProfilePicURI = DEFAULT_AVATAR_STORAGE_STRING;
        setChangeProfilePicButtonOnClick();
    }

    private void initializeFromUpdate() {
        // TODO show progress bar
        buttonCancel.setVisibility(View.VISIBLE);
        buttonDeleteAccount.setVisibility(View.VISIBLE);
        buttonChangeProfilePic.setText("Change profile picture");
        usersCollectionRef.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName = documentSnapshot.getString(getString(R.string.ff_Users_userName));
                userProfilePicURI = documentSnapshot.getString(getString(R.string.ff_Users_userProfilePicURL));
                showName();
                showProfilePic();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this, "Failed retrieving data", Toast.LENGTH_SHORT).show();
                gotoMainActivity();
            }
        });
        setChangeProfilePicButtonOnClick();
        setCancelButtonOnClick();

    }

    private void setChangeProfilePicButtonOnClick() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }


    private void showName() {
        textViewName.setText(userName);
    }

    private void showProfilePic() {
        StorageReference userStorageRef = firebaseStorage.getReference().child(userProfilePicURI);

        userStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
    }

    private void setSaveButtonOnClick() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference userRef = usersCollectionRef.document(userID);
                if (validateFields()) {
                    if (didComeFromRegister) {
                        saveNewUser(userRef);
                    } else {
                        updateExistingUser(userRef);
                    }

                }

            }
        });
    }

    private void saveNewUser(DocumentReference userRef) {
        if (!TextUtils.isEmpty(newUserProfilePicURI)) {
            saveProfilePicToStorage(newUserProfilePicURI);
        }
        UserModel newUserModel = new UserModel(userID, userName, userEmail, userProfilePicURI);
        userRef.set(newUserModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onUserSaveSuccess();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onUserSaveFailure(e);

            }
        });
    }

    private void updateExistingUser(DocumentReference userRef) {
        Map<String, Object> userUpdatesMap = new HashMap<>();
        userUpdatesMap.put(getString(R.string.ff_Users_userName), textViewName.getText());
        userUpdatesMap.put(getString(R.string.ff_Users_userProfilePicURL), userProfilePicURI);
        userRef.update(userUpdatesMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onUserSaveSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onUserSaveFailure(e);
            }
        });
    }


    private void onUserSaveSuccess() {
        Toast.makeText(AccountActivity.this, "User created successfully",
                Toast.LENGTH_SHORT).show();
    }

    private void onUserSaveFailure(Exception e) {
        Toast.makeText(AccountActivity.this, "User could not be created, " +
                "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private boolean validateFields() {
        boolean isNameEmpty = TextUtils.isEmpty(textViewName.getText());
        boolean isProfilePicURLEmpty = TextUtils.isEmpty(userProfilePicURI);
        return !isNameEmpty && !isProfilePicURLEmpty;
    }


    private void setCancelButtonOnClick() {
        gotoMainActivity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                Picasso.get().load(imageUri).into(profileImage);

            }
        }
    }


    private void saveProfilePicToStorage(Uri profilePicToUpload) {
        firebaseStorage.getReference().child(userID).putFile(profilePicToUpload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AccountActivity.this, "Profile picture uploaded",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this, "Profile Picture couldn't upload", Toast.LENGTH_SHORT).show();
            }
        });
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
