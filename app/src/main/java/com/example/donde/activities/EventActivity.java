package com.example.donde.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.donde.R;
import com.example.donde.events_recycler_view.EventsListViewModel;
import com.example.donde.utils.ViewPagerAdapter;


public class EventActivity extends AppCompatActivity {
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

    private int position;

    private EventsListViewModel eventsListViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initializeFields();
        initializeListeners();
    }
    //    private int position;
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

    private void initializeFields() {
        textViewInfoLabel = findViewById(R.id.event_textView_info_label);
        textViewMapLabel = findViewById(R.id.event_textView_map_label);
        textViewChatLabel = findViewById(R.id.event_textView_chat_label);
        viewPager = findViewById(R.id.event_viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        position = getIntent().getIntExtra("position", -1);
        Toast.makeText(EventActivity.this, "position: " + position, Toast.LENGTH_SHORT).show();


        eventsListViewModel = new ViewModelProvider(this).get(EventsListViewModel.class);
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
}
