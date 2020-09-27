package com.example.donde.archive;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.donde.R;

public class AddEventFragment extends Fragment {
    private NavController navController;
    private Button buttonGotoAllEvents;
    private Button buttonCreateEvent;

    public AddEventFragment() {

    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        Log.e("AddEventFragment", "in onCreateView");

        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e("AddEventFragment", "in onViewCreated");

        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        buttonCreateEvent = view.findViewById(R.id.create_button_create);
        buttonGotoAllEvents = view.findViewById(R.id.button_goto_all_events);


        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addEventFragment_to_eventInfoFragment);
            }
        });

        buttonGotoAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addEventFragment_to_eventsFragment);
            }
        });
    }
}