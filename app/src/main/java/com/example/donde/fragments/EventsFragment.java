package com.example.donde.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donde.R;
import com.example.donde.activities.EventActivity;
import com.example.donde.events_recycler_view.EventsListAdapter;
import com.example.donde.models.EventModel;
import com.example.donde.events_recycler_view.EventsListViewModel;

import java.util.List;

//import com.example.donde.EventsFragmentDirections;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements EventsListAdapter.OnEventListItemClicked {

    private RecyclerView listView;
    private EventsListViewModel eventsListViewModel;
    private NavController navController;

    private EventsListAdapter adapter;

    private Button buttonCreateNewEvent;

    public EventsFragment() {
        Log.e("EventsFragment", "Constructor");

        // Required empty public constructor
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
        listView = view.findViewById(R.id.list_view);
        adapter = new EventsListAdapter(this);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                DividerItemDecoration.VERTICAL);
        listView.addItemDecoration(dividerItemDecoration);
        navController = Navigation.findNavController(view);

        buttonCreateNewEvent = view.findViewById(R.id.create_button_create);
        buttonCreateNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_eventsFragment_to_addEventFragment);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e("EventsFragment", "in onActivityCreated");

        super.onActivityCreated(savedInstanceState);

        eventsListViewModel = new ViewModelProvider(getActivity()).get(EventsListViewModel.class);
        eventsListViewModel.getEventsListModelData().observe(getViewLifecycleOwner(), new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventsListModels) {
                adapter.setEventsListModels(eventsListModels);
                adapter.notifyDataSetChanged();
            }
        });
    }


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
}
