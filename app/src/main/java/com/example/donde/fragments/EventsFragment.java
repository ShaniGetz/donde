/*
 * Who last worked here?
 * alon 06/10/20_15:20
 *
 * (Add new name and date above this line)
 */

package com.example.donde.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donde.R;
import com.example.donde.activities.EventActivity;
import com.example.donde.models.EventModel;
import com.example.donde.recycle_views.events_recycler_view.EventsListAdapter;
import com.example.donde.recycle_views.events_recycler_view.EventsListViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements EventsListAdapter.OnEventListItemClicked {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerViewEventsList;
    private FirestoreRecyclerAdapter eventsRecyclerAdapter;

    private EventsListViewModel eventsListViewModel;

//    private NavController navController;
//    private EventsListAdapter adapter;
//    private Button buttonCreateNewEvent;


    public EventsFragment() {
        Log.e("EventsFragment", "Constructor");

        // Required empty public constructor
    }

    private void initializeFields(View view) {
        recyclerViewEventsList = view.findViewById(R.id.events_recyclerView_events);
        firebaseFirestore = FirebaseFirestore.getInstance();

//        recyclerViewEventsList = view.findViewById(R.id.events_recyclerView_events);
//        adapter = new EventsListAdapter(this);
//
//        recyclerViewEventsList.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewEventsList.setHasFixedSize(true);
//        recyclerViewEventsList.setAdapter(adapter);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewEventsList.getContext(),
//                DividerItemDecoration.VERTICAL);
//        recyclerViewEventsList.addItemDecoration(dividerItemDecoration);
//        navController = Navigation.findNavController(view);
//
//        buttonCreateNewEvent = view.findViewById(R.id.create_button_create);
//        buttonCreateNewEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navController.navigate(R.id.action_eventsFragment_to_addEventFragment);
//            }
//        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("EventsFragment", "in onCreateView");

        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e("EventsFragment", "in onViewCreated");

        super.onViewCreated(view, savedInstanceState);
        initializeFields(view);
        initializeEventsList();


    }

    private void initializeEventsList() {
        // query firestore for events
//        Query eventsQuery =
//                firebaseFirestore.collection(getString(R.string.ff_events_collection)).orderBy(getString(R.string.ff_event_start_time));
        Query eventsQuery =
                firebaseFirestore.collection(getString(R.string.ff_events_collection)).orderBy(
                        getString(R.string.ff_events_eventTimeStarting));
        // recycler view inflater
        FirestoreRecyclerOptions<EventModel> evenstOptions =
                new FirestoreRecyclerOptions.Builder<EventModel>().setQuery(eventsQuery,
                        EventModel.class).build();
        eventsRecyclerAdapter =
                new FirestoreRecyclerAdapter<EventModel, EventsViewHolder>(evenstOptions) {

                    @Override
                    protected void onBindViewHolder(@NonNull EventsViewHolder holder, int position, @NonNull EventModel model) {
                        holder.textViewEventName.setText(String.format("Event name: %s", model.getEventName()));
                        holder.textViewEventDescription.setText(String.format("Event " +
                                "Description:\n%s", model.getEventDescription()));
                        holder.textViewEventCreatorName.setText(String.format("Creator name: %s",
                                model.getEventCreatorName()));
                        holder.textViewEventLocationName.setText(String.format("Location " +
                                "name: %s", model.getEventLocationName()));
                        holder.textViewEventTimeStarting.setText(String.format("Time " +
                                "starting: %s", model.getEventTimeStarting()));
                        holder.buttonGotoEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent eventIntent = new Intent(getActivity(), EventActivity.class);

                                eventIntent.putExtra(getString(R.string.arg_position), position);
//                                eventIntent.putExtra(getString(R.string.arg_event_id), eventID);
                                startActivity(eventIntent);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =
                                LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event_item, parent, false);
                        return new EventsViewHolder(view);
                    }


                };

        recyclerViewEventsList.setHasFixedSize(true);
        recyclerViewEventsList.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerViewEventsList.getContext(),
                        DividerItemDecoration.VERTICAL);
        recyclerViewEventsList.addItemDecoration(dividerItemDecoration);
        recyclerViewEventsList.setAdapter(eventsRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventsRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        eventsRecyclerAdapter.stopListening();
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        Log.e("EventsFragment", "in onActivityCreated");
//
//        super.onActivityCreated(savedInstanceState);
//
////        eventsListViewModel = new ViewModelProvider(getActivity()).get(EventsListViewModel.class);
////        eventsListViewModel.getEventsListModelData().observe(getViewLifecycleOwner(), new Observer<List<EventModel>>() {
////            @Override
////            public void onChanged(List<EventModel> eventsListModels) {
////                adapter.setEventsListModels(eventsListModels);
////                adapter.notifyDataSetChanged();
////            }
////        });
//    }

    @Override
    public void onItemClicked(int position, String eventID) {
        Intent eventIntent = new Intent(getActivity(), EventActivity.class);

        eventIntent.putExtra(getString(R.string.arg_position), position);
        eventIntent.putExtra(getString(R.string.arg_event_id), eventID);
        startActivity(eventIntent);

        //        EventsFragmentDirections.ActionEventsFragmentToEventInfoFragment action =
//                EventsFragmentDirections.actionEventsFragmentToEventInfoFragment();
//        action.setPosition(position);
//
//        navController.navigate(action);
    }

    class EventsViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewEventName;
        private TextView textViewEventDescription;
        private TextView textViewEventTimeStarting;
        private TextView textViewEventCreatorName;
        private TextView textViewEventLocationName;
        private CheckBox checkBoxEventIsGoing;
        private Button buttonGotoEvent;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.event_item_textView_event_name);
            textViewEventDescription = itemView.findViewById(R.id.event_item_textView_event_description);
            textViewEventTimeStarting = itemView.findViewById(R.id.event_item_textView_event_time_starting);
            textViewEventCreatorName = itemView.findViewById(R.id.event_item_textView_event_creator_name);
            textViewEventLocationName = itemView.findViewById(R.id.event_item_textView_event_location_name);
            checkBoxEventIsGoing = itemView.findViewById(R.id.event_item_checkBox_is_going);
            buttonGotoEvent = itemView.findViewById(R.id.event_item_button_goto_event);
        }
    }

}
