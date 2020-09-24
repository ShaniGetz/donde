package com.example.donde;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements EventsListAdapter.OnEventListItemClicked {

    private RecyclerView listView;
    private EventsListViewModel eventsListViewModel;
    private NavController navController;

    private EventsListAdapter adapter;

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
        navController = Navigation.findNavController(view);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e("EventsFragment", "in onActivityCreated");

        super.onActivityCreated(savedInstanceState);
        eventsListViewModel = new ViewModelProvider(getActivity()).get(EventsListViewModel.class);
        eventsListViewModel.getEventsListModelData().observe(getViewLifecycleOwner(), new Observer<List<EventsListModel>>() {
            @Override
            public void onChanged(List<EventsListModel> eventsListModels) {
                adapter.setEventsListModels(eventsListModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        EventsFragmentDirections.ActionEventsFragmentToEventInfoFragment action =
                EventsFragmentDirections.actionEventsFragmentToEventInfoFragment();
        action.setPosition(position);
        navController.navigate(action);
    }
}
