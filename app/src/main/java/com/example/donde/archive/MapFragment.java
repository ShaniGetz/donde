package com.example.donde.archive;

import android.os.Bundle;
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

public class MapFragment extends Fragment {
    private NavController navController;


    private Button buttonGotoAllEvents;
    private Button buttonGotoEventInfo;
    private Button buttonGotoChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        navController = Navigation.findNavController(view);


        buttonGotoAllEvents = view.findViewById(R.id.button_goto_all_events);
        buttonGotoEventInfo = view.findViewById(R.id.button_goto_info);
        buttonGotoChat = view.findViewById(R.id.button_goto_chat);

        buttonGotoAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mapFragment_to_eventsFragment);
            }
        });
        buttonGotoEventInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mapFragment_to_eventInfoFragment);
            }
        });
        buttonGotoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mapFragment_to_chatFragment);
            }
        });
        super.onViewCreated(view, savedInstanceState);    }
}