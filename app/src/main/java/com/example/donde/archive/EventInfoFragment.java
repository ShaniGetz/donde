package com.example.donde.archive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.donde.R;
import com.example.donde.events_recycler_view.EventsListViewModel;

//import com.example.donde.EventInfoFragmentArgs;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventInfoFragment extends Fragment {
    private int position;
    private NavController navController;
    private EventsListViewModel eventsListViewModel;

    private TextView infoEventName;
    private TextView infoDescription;
    private TextView infoLocationName;
    private TextView infoCreatorUsername;

    private Button buttonGotoAllEvents;
    private Button buttonGotoChat;
    private Button buttonGotoMap;

    public EventInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_event_info, container, false);

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        navController = Navigation.findNavController(view);
//        position = EventInfoFragmentArgs.fromBundle(getArguments()).getPosition();
//
//        infoEventName = view.findViewById(R.id.event_textView_event_name);
//        infoDescription = view.findViewById(R.id.event_textView_event_description);
//        infoLocationName = view.findViewById(R.id.event_textView_location_name);
//        infoCreatorUsername = view.findViewById(R.id.event_textView_creator_name);
//
//        buttonGotoAllEvents = view.findViewById(R.id.event_button_goto_events);
//        buttonGotoChat = view.findViewById(R.id.event_button_goto_chat);
//        buttonGotoMap = view.findViewById(R.id.event_button_goto_map);
//
//        buttonGotoAllEvents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_eventInfoFragment_to_eventsFragment);
//            }
//        });
//        buttonGotoChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_eventInfoFragment_to_chatFragment);
//            }
//        });
//        buttonGotoMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_eventInfoFragment_to_mapFragment);
//            }
//        });
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        eventsListViewModel = new ViewModelProvider(getActivity()).get(EventsListViewModel.class);
//        eventsListViewModel.getEventsListModelData().observe(getViewLifecycleOwner(), new Observer<List<EventsListModel>>() {
//            @Override
//            public void onChanged(List<EventsListModel> eventsListModels) {
//                infoEventName.setText(eventsListModels.get(position).getEventName());
//                infoDescription.setText(eventsListModels.get(position).getEventDescription());
//                infoLocationName.setText(eventsListModels.get(position).getEventLocationName());
////                infoCreatorUsername.setText(eventsListModels.get(position).getCreator_username());
//            }
//        });
//    }
}
