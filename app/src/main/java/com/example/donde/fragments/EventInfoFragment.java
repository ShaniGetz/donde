package com.example.donde.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.donde.R;
import com.example.donde.activities.EventActivity;
import com.example.donde.models.EventModel;

public class EventInfoFragment extends Fragment {

    // view fields
    private TextView textViewEventName;
    private TextView textViewEventDescription;
    private TextView textViewEventLocationName;
    private TextView textViewEventCreatorName;
    private TextView textViewEventTimeStarting;
    private TextView textViewEventTimeCreated;

    private EventModel event;

    private void initializeFields(View view) {
        textViewEventName = view.findViewById(R.id.info_textView_event_name);
        textViewEventDescription = view.findViewById(R.id.info_textView_event_description);
        textViewEventLocationName = view.findViewById(R.id.info_textView_location_name);
        textViewEventCreatorName = view.findViewById(R.id.info_textView_creator_name);
        textViewEventTimeStarting = view.findViewById(R.id.info_textView_time_starting);
        textViewEventTimeCreated = view.findViewById(R.id.info_textView_time_created);

        event = ((EventActivity) getActivity()).getEvent();
    }

    private void initializeViews() {
        textViewEventName.setText(String.format("Event name: %s",event.getEventName()));
        textViewEventDescription.setText(String.format("Event description: %s",
                event.getEventDescription()));
        textViewEventLocationName.setText(String.format("Event location name: %s",
                event.getEventLocationName()));
        textViewEventCreatorName.setText(String.format("Event creator name: %s",
                event.getEventCreatorName()));
        textViewEventTimeStarting.setText(String.format("Event time starting: %s",
                event.getEventTimeStarting()));
        textViewEventTimeCreated.setText(String.format("Event time created: %s",
                event.getEventTimeCreated()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_info, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeFields(view);
        initializeViews();
    }
}