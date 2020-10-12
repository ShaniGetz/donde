package com.example.donde.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.donde.R;
import com.example.donde.models.EventModel;
import com.example.donde.models.InvitedInEventUserModel;
import com.example.donde.recycle_views.events_recycler_view.EventsListViewModel;
import com.example.donde.utils.ViewPagerAdapter;
import com.example.donde.utils.map_utils.StatusDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.gson.Gson;

import java.util.ArrayList;


public class EventActivity extends AppCompatActivity implements StatusDialog.StatusDialogListener {

    private static String status;
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
    private String currUserID;
    private int currentUserIndexInInvitedUsersList = 0; // current user is always at beginning

    public static String getStatus() {
        return status;
    }

    public static String getMyUserId() {
        return myUserId;
    }

    public ArrayList<InvitedInEventUserModel> getInvitedUserInEventModelList() {
        return invitedUserInEventModelList;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initializeFields();
        initializeListeners();
        initializeInvitedUsersList();
        myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        changeUriToBitmap("profile.jpg");
//        b = loadImageFromStorage(path);
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

    @Override
    public void applyText(String status) {
        this.status = status;
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
