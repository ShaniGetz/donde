package com.example.donde;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;

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

    public EventInfoFragment() {
        // Required empty public constructor
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
        navController = Navigation.findNavController(view);
        position = EventInfoFragmentArgs.fromBundle(getArguments()).getPosition();

        infoEventName = view.findViewById(R.id.info_event_name);
        infoDescription = view.findViewById(R.id.info_description);
        infoLocationName = view.findViewById(R.id.info_location_name);
        infoCreatorUsername = view.findViewById(R.id.info_creator_username);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventsListViewModel = new ViewModelProvider(getActivity()).get(EventsListViewModel.class);
        eventsListViewModel.getEventsListModelData().observe(getViewLifecycleOwner(), new Observer<List<EventsListModel>>() {
            @Override
            public void onChanged(List<EventsListModel> eventsListModels) {
                infoEventName.setText(eventsListModels.get(position).getName());
                infoDescription.setText(eventsListModels.get(position).getDescription());
                infoLocationName.setText(eventsListModels.get(position).getLocation_name());
                infoCreatorUsername.setText(eventsListModels.get(position).getCreator_username());
            }
        });
    }
}
