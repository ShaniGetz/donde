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
import com.example.donde.fragments.EventInfoFragment;
import com.example.donde.models.EventModel;
import com.example.donde.models.InvitedUserModel;
import com.example.donde.models.UserModel;
import com.example.donde.recycle_views.events_recycler_view.EventsListViewModel;
import com.example.donde.utils.ViewPagerAdapter;
import com.example.donde.utils.map_utils.StatusDialog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;


public class EventActivity extends AppCompatActivity implements StatusDialog.StatusDialogListener {
    private static String status;
    final int INFO_TAB = 0;
    final int MAP_TAB = 1;
    final int CHAT_TAB = 2;

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
    private EventModel event;

    private EventsListViewModel eventsListViewModel;
//    private int position;

    FirebaseFirestore firebaseFirestore;
    public ArrayList<UserModel> usersList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initializeFields();
        initializeListeners();
//        initializeUsersList();
    }

//    private NavController navController;
//    private EventsListViewModel eventsListViewModel;

//    private TextView textViewEventName;
//    private TextView textViewDescription;
//    private TextView textViewLocationName;
//    private TextView textViewCreatorUsername;
//
//    private Button buttonGotoEvents;
//    private Button buttonGotoChat;
//    private Button buttonGotoMap;

    public EventModel getEvent() {
        return event;
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

        // get event object from intent
        Gson gson = new Gson();
        event = gson.fromJson(getIntent().getStringExtra(getString(R.string.arg_event_model)),
                EventModel.class);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), eventID, position);
        viewPager.setAdapter(viewPagerAdapter);
        // meaning all 3 screens will always be loaded
        viewPager.setOffscreenPageLimit(2);


//        eventsListViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);
//        eventsListViewModel.getEventsListModelData().observe(this,
//                new Observer<List<EventsListModel>>() {
//                    @Override
//                    public void onChanged(List<EventsListModel> eventsListModels) {
//                        infoEventName.setText(eventsListModels.get(position).getEventName());
//                        infoDescription.setText(eventsListModels.get(position).getEventDescription());
//                        infoLocationName.setText(eventsListModels.get(position).getEventLocationName());
////                        infoCreatorUsername.setText(eventsListModels.get(position).getCreator_username());
//                    }
//                });


//        navController = Navigation.findNavController(view);
//        position = EventInfoFragmentArgs.fromBundle(getArguments()).getPosition();

//        textViewEventName = findViewById(R.id.event_textView_event_name);
//        textViewDescription = findViewById(R.id.event_textView_event_description);
//        textViewLocationName = findViewById(R.id.event_textView_location_name);
//        textViewCreatorUsername = findViewById(R.id.event_textView_creator_name);
//        buttonGotoEvents = findViewById(R.id.event_button_goto_events);
//        buttonGotoChat = findViewById(R.id.event_button_goto_chat);
//        buttonGotoMap = findViewById(R.id.event_button_goto_map);
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

//    }

    @Override
    public void applyText(String status) {
        this.status = status;
    }

    public static String getStatus() {
        return status;
    }
}
