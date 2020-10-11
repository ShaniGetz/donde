package com.example.donde.activities;

import android.os.Bundle;
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
    public ArrayList<InvitedInEventUserModel> getInvitedUserInEventModelList() {
        return invitedUserInEventModelList;
    }
    private String currUserID;
    private int currentUserIndexInInvitedUsersList = 0; // current user is always at beginning

    public static String getStatus() {
        return status;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initializeFields();
        initializeListeners();
        initializeInvitedUsersList();
        myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static String getMyUserId() {
        return myUserId;
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

                            if (userId == currUserID) {
                                invitedInEventUserModels.add(0,
                                        documentSnapshot.toObject(InvitedInEventUserModel.class));
                            } else {

                                invitedInEventUserModels.add(documentSnapshot.toObject(InvitedInEventUserModel.class));
                            }


                        }
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

    @Override
    public void applyText(String status) {
        this.status = status;
    }
}
