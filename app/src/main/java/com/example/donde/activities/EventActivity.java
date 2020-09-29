package com.example.donde.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.donde.R;
import com.example.donde.events_recycler_view.EventsListModel;
import com.example.donde.events_recycler_view.EventsListViewModel;
import com.example.donde.utils.ViewPagerAdapter;

import java.util.List;

public class EventActivity extends AppCompatActivity {
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

    }

}
