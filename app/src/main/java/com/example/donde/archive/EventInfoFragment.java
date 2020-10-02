package com.example.donde.archive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.donde.R;
import com.example.donde.events_recycler_view.EventsListViewModel;
import com.example.donde.models.EventModel;
import com.example.donde.models.UserModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

//import com.example.donde.EventInfoFragmentArgs;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventInfoFragment extends Fragment {
    private int position;
    private NavController navController;
    private EventsListViewModel eventsListViewModel;

    private TextView textViewEventName;
    private TextView textViewEventDescription;
    private TextView textViewLocationName;
//    private TextView infoCreatorUsername;
//
//    private Button buttonGotoAllEvents;
//    private Button buttonGotoChat;
//    private Button buttonGotoMap;

    private String eventID;
    private String eventName;
    private TextView textViewTitle;

    private FirebaseFirestore firebaseFirestore;
    private List<UserModel> invitedUsers;


    //
    public EventInfoFragment() {
        // Required empty public constructor
    }

    private void initializeFields(View view) {
        eventID = getArguments().getString(getString(R.string.arg_event_id));
        position = getArguments().getInt(getString(R.string.arg_position));
//        Toast.makeText(getContext(),"Err "+position, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(),"Err "+eventID, Toast.LENGTH_SHORT).show();
//        eventName = getArguments().getString(getString(R.string.arg_event_name));

        textViewTitle = view.findViewById(R.id.info_textView_title);
//        textViewTitle.setText(eventID);
        textViewEventName = view.findViewById(R.id.info_textView_event_name);
        textViewEventDescription = view.findViewById(R.id.info_textView_event_description);
        textViewLocationName = view.findViewById(R.id.info_textView_location_name);

        firebaseFirestore = FirebaseFirestore.getInstance();
        invitedUsers = new ArrayList<>();

        populateInvitedUsersList();


    }

    private void populateInvitedUsersList() {
        CollectionReference usersRef = firebaseFirestore.collection(getString(R.string.ff_users_collection));
//        Query invitedUsersQuery = usersRef.whe()
        firebaseFirestore.collection(getString(R.string.ff_users_collection)).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                for (DocumentChange doc : value.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        UserModel users = doc.getDocument().toObject(UserModel.class);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_event_info, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeFields(view);
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
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Toast.makeText(getContext(), "Err " + position, Toast.LENGTH_SHORT).show();

        eventsListViewModel = new ViewModelProvider(getActivity()).get(EventsListViewModel.class);
        eventsListViewModel.getEventsListModelData().observe(getViewLifecycleOwner(), new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventsListModels) {
                textViewTitle.setText(eventsListModels.get(position).getEventName());
                textViewEventName.setText(eventsListModels.get(position).getEventName());
                textViewEventDescription.setText(eventsListModels.get(position).getEventDescription());
                textViewLocationName.setText(eventsListModels.get(position).getEventLocationName());
//                infoCreatorUsername.setText(eventsListModels.get(position).getCreator_username());
            }
        });
    }
}
