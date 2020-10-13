package com.bas.donde.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bas.donde.R;
import com.bas.donde.models.EventModel;
import com.bas.donde.models.InvitedInEventUserModel;
import com.bas.donde.utils.OfflineDataTransfer;
import com.bas.donde.utils.ViewPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;


public class EventActivity extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 5432;
    private static String myUserId;
    final int INFO_TAB = 0;
    final int MAP_TAB = 1;
    final int CHAT_TAB = 2;
    FirebaseFirestore firebaseFirestore;
    OfflineDataTransfer offlineDataTransfer;
    private TextView textViewInfoLabel;
    private TextView textViewMapLabel;
    private TextView textViewChatLabel;
    private TextView infoEventName;
    private TextView infoDescription;
    private TextView infoLocationName;
    private TextView infoCreatorUsername;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private String eventID;
    private int position;
    private EventModel eventModel;
    //    private int position;
    private String TAG = "EventActivity";
    private ArrayList<InvitedInEventUserModel> invitedUserInEventModelList = new ArrayList<>();
    private String currUserID;
    private int currentUserIndexInInvitedUsersList = 0; // current user is always at beginning
    private HashMap<String, Bitmap> invitedUserInEventBitmaps;

    public static String getMyUserId() {
        return myUserId;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, Bitmap> getInvitedUserInEventBitmaps() {
        return invitedUserInEventBitmaps;
    }

    public ArrayList<InvitedInEventUserModel> getInvitedUserInEventModelList() {
        return invitedUserInEventModelList;
    }

    public OfflineDataTransfer getOfflineDataTransfer() {
        return offlineDataTransfer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initializeFields();
//        initializeInvitedUsersList();
        initializeListeners();
        GeoPoint geoPoint = new GeoPoint(0, 0);
        offlineDataTransfer = new OfflineDataTransfer(currUserID, geoPoint, this, "write something...");
        offlineDataTransfer.startAdvertising();
        myUserId = currUserID;

    }


    /**
     * Called when the user has accepted (or denied) our permission request.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "error_missing_permissions", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        offlineDataTransfer.stopAll();
    }


//    private void initializeInvitedUsersList() {
//        firebaseFirestore.collection(getString(R.string.ff_Events)).document(eventID).collection(getString(R.string.ff_InvitedInEventUsers)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                firebaseFirestore.runTransaction(new Transaction.Function<ArrayList<InvitedInEventUserModel>>() {
//
//                    @Nullable
//                    @Override
//                    public ArrayList<InvitedInEventUserModel> apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
//                        ArrayList<InvitedInEventUserModel> invitedInEventUserModels = new ArrayList<>();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Log.d(TAG, String.format("Adding query snapshot name: %s",
//                                    documentSnapshot.get(getString(R.string.ff_InvitedInEventUsers_invitedInEventUserName))));
//                            String userId =
//                                    documentSnapshot.getString(getString(R.string.ff_InvitedInEventUsers_invitedInEventUserID));
//
//                            Log.d(TAG, "checking if " + userId + "==" + currUserID);
//                            if (TextUtils.equals(userId, currUserID)) {
//                                Log.d(TAG, "enteres");
//                                invitedInEventUserModels.add(0,
//                                        documentSnapshot.toObject(InvitedInEventUserModel.class));
//                            } else {
//
//                                invitedInEventUserModels.add(documentSnapshot.toObject(InvitedInEventUserModel.class));
//                            }
//
//
//                        }
//                        Log.d(TAG, "index 0: " + invitedInEventUserModels.get(0)
//                                .getInvitedInEventUserName());
//                        return invitedInEventUserModels;
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<ArrayList<InvitedInEventUserModel>>() {
//                    @Override
//                    public void onSuccess(ArrayList<InvitedInEventUserModel> invitedInEventUserModels) {
//                        Log.d(TAG, String.format("Size of array after transaction is %s and first " +
//                                        "user is %s", invitedInEventUserModels.size(),
//                                invitedInEventUserModels.size() == 0 ? "NO " +
//                                        "USER" : invitedInEventUserModels.get(0)));
//                        invitedUserInEventModelList = invitedInEventUserModels;
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, String.format("Transaction failed with error: %s", e.getMessage()));
//                    }
//                });
//
//            }
//        });
//    }


    public EventModel getEvent() {
        return eventModel;
    }

    public String getEventID() {
        return eventID;
    }

    private void initializeFields() {
        textViewInfoLabel = findViewById(R.id.event_textView_info_label);
        textViewMapLabel = findViewById(R.id.event_textView_map_label);
        textViewChatLabel = findViewById(R.id.event_textView_chat_label);
        viewPager = findViewById(R.id.event_viewPager);
        eventID = getIntent().getStringExtra(getString(R.string.arg_event_id));
        position = getIntent().getIntExtra(getString(R.string.arg_position), -1);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // get event object from intent
        Gson gson = new Gson();
        eventModel = gson.fromJson(getIntent().getStringExtra(getString(R.string.arg_event_model)),
                EventModel.class);

        String invitedUserInEventModelListJson =
                getIntent().getStringExtra(getString(R.string.arg_invitedInEventUserModels_intent));
        invitedUserInEventModelList = gson.fromJson(invitedUserInEventModelListJson,
                new TypeToken<ArrayList<InvitedInEventUserModel>>() {
                }.getType());

        String invitedUserInEventBitmapsJson =
                getIntent().getStringExtra(getString(R.string.arg_users_bitmaps_intent));
        invitedUserInEventBitmaps = gson.fromJson(invitedUserInEventBitmapsJson,
                new TypeToken<HashMap<String, Bitmap>>() {
                }.getType());

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), eventID, position);
        viewPager.setAdapter(viewPagerAdapter);
        // meaning all 3 screens will always be loaded
        viewPager.setOffscreenPageLimit(2);
        currUserID = FirebaseAuth.getInstance().getUid();


    }


    private void initializeListeners() {
        textViewInfoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(INFO_TAB);
            }
        });
        textViewMapLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(MAP_TAB);
            }
        });
        textViewChatLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(CHAT_TAB);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void changeTabs(int position) {
        TextView mainTab;
        TextView subTab1, subTab2;

        switch (position) {
            case INFO_TAB:
                mainTab = textViewInfoLabel;
                subTab1 = textViewMapLabel;
                subTab2 = textViewChatLabel;
                break;
            case MAP_TAB:
                subTab1 = textViewInfoLabel;
                mainTab = textViewMapLabel;
                subTab2 = textViewChatLabel;
                break;
            case CHAT_TAB:
                subTab1 = textViewInfoLabel;
                subTab2 = textViewMapLabel;
                mainTab = textViewChatLabel;
                break;
            default:
                mainTab = null;
                subTab1 = null;
                subTab2 = null;


        }
        mainTab.setTextSize(20);
        subTab1.setTextSize(15);
        subTab2.setTextSize(15);
    }

}
