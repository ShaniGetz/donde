package com.example.donde.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.donde.R;
import com.example.donde.models.EventModel;
import com.example.donde.models.InvitedInEventUserModel;
import com.example.donde.recycle_views.events_recycler_view.EventsListViewModel;
import com.example.donde.utils.OfflineDataTransfer;
import com.example.donde.utils.ViewPagerAdapter;
import com.example.donde.utils.map_utils.StatusDialog;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class EventActivity extends AppCompatActivity {

    private static String myUserId;
    final int INFO_TAB = 0;
    final int MAP_TAB = 1;
    final int CHAT_TAB = 2;
    FirebaseFirestore firebaseFirestore;
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
    private EventsListViewModel eventsListViewModel;
    private String TAG = "EventActivity";
    private ArrayList<InvitedInEventUserModel> invitedUserInEventModelList = new ArrayList<>();
    public ArrayList<InvitedInEventUserModel> getInvitedUserInEventModelList() {
        return invitedUserInEventModelList;
    }
    private String currUserID;
    OfflineDataTransfer offlineDataTransfer;

    private int currentUserIndexInInvitedUsersList = 0; // current user is always at beginning
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 5432;


    public OfflineDataTransfer getOfflineDataTransfer(){
        return offlineDataTransfer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initializeFields();
        initializeListeners();
        initializeInvitedUsersList();
        GeoPoint geoPoint = new GeoPoint(0, 0);
//        GeoPoint geoPoint = new GeoPoint(31.768161300000003, 35.2127055);
//        offlineDataTransfer.startDiscovery();
        offlineDataTransfer = new OfflineDataTransfer(currUserID, geoPoint, this,"write something...");
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



    private void initializeInvitedUsersList() {
        firebaseFirestore.collection(getString(R.string.ff_Events)).document(eventID).collection(getString(R.string.ff_InvitedInEventUsers)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                firebaseFirestore.runTransaction(new Transaction.Function<ArrayList<InvitedInEventUserModel>>() {

                    @Nullable
                    @Override
                    public ArrayList<InvitedInEventUserModel> apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        ArrayList<InvitedInEventUserModel> invitedInEventUserModels = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d(TAG, String.format("Adding query snapshot name: %s",
                                    documentSnapshot.get(getString(R.string.ff_InvitedInEventUsers_invitedInEventUserName))));
                            String userId =
                                    documentSnapshot.getString(getString(R.string.ff_InvitedInEventUsers_invitedInEventUserID));

                            Log.d(TAG, "checking if " + userId + "==" + currUserID);
                            if (TextUtils.equals(userId, currUserID)) {
                                Log.d(TAG, "enteres");
                                invitedInEventUserModels.add(0,
                                        documentSnapshot.toObject(InvitedInEventUserModel.class));
                            } else {

                                invitedInEventUserModels.add(documentSnapshot.toObject(InvitedInEventUserModel.class));
                            }


                        }
                        Log.d(TAG, "index 0: " + invitedInEventUserModels.get(0)
                                .getInvitedInEventUserName());
                        return invitedInEventUserModels;
                    }
                }).addOnSuccessListener(new OnSuccessListener<ArrayList<InvitedInEventUserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<InvitedInEventUserModel> invitedInEventUserModels) {
                        Log.d(TAG, String.format("Size of array after transaction is %s and first " +
                                        "user is %s", invitedInEventUserModels.size(),
                                invitedInEventUserModels.size() == 0 ? "NO " +
                                        "USER" : invitedInEventUserModels.get(0)));
                        invitedUserInEventModelList = invitedInEventUserModels;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, String.format("Transaction failed with error: %s", e.getMessage()));
                    }
                });

            }
        });
    }


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

//    private void initializeUsersList() {
//        usersList = new ArrayList<>();
//        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
//        String myUserId = myUser.getUid();
//        UserModel myUserModel = new UserModel(myUserId, myUser.)
//        usersList.add(myUserModel);
//        Query invitedUsersQuery =
//                firebaseFirestore.collection(getString(R.string.ff_events_collection)).document(getEventID()).collection(getString(R.string.ff_eventInvitedUsers_collection));
//        FirestoreRecyclerOptions<InvitedUserModel> invitedUsersOptions =
//                new FirestoreRecyclerOptions.Builder<InvitedUserModel>().setQuery(invitedUsersQuery, InvitedUserModel.class).build();
//
//    }

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


//
//    public void changeUriToBitmap(String uri) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference imageRef = storage.getReference().child(uri);
//        imageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                String dir = saveToInternalStorage(bitmap);
//                Log.i("download photo", dir);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("download photo", e.toString());
//            }
//        });
//    }
//
//    private String saveToInternalStorage(Bitmap bitmapImage){
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"photo.jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        path = directory.getAbsolutePath();
//        return directory.getAbsolutePath();
//    }
//
//    private Bitmap loadImageFromStorage(String path)
//    {
//        try {
//            File f=new File(path, "photo.jpg");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            //todo: conect to the rellevant clustermarker b
////            ImageView img=(ImageView)findViewById(R.id.imgPicker);
////            img.setImageBitmap(b);
//            return b;
//
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
