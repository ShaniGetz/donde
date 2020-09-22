package com.example.donde;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventViewHolder> {
    private List<EventsListModel> eventsListModels;

    public void setEventsListModels(List<EventsListModel> eventsListModels) {
        this.eventsListModels = eventsListModels;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event_item,
                parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Log.e("ALON", eventsListModels.get(position).getName());
        holder.listName.setText(eventsListModels.get(position).getName());
        holder.listDesc.setText(eventsListModels.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        if (eventsListModels == null) {
            return 0;
        } else {
            return eventsListModels.size();
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView listName;
        private TextView listDesc;
        private TextView listLocationName;
        private TextView listLocation;
        private TextView listCreatorUserName;
        private TextView listTimeCreated;
        private TextView listTimeStarting;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            listName = itemView.findViewById(R.id.list_name);
            listDesc = itemView.findViewById(R.id.list_desc);
            listLocationName = itemView.findViewById(R.id.list_location_name);
            listLocation = itemView.findViewById(R.id.list_location);
            listCreatorUserName = itemView.findViewById(R.id.list_creator_username);
            listTimeCreated = itemView.findViewById(R.id.list_time_created);
            listTimeStarting = itemView.findViewById(R.id.list_time_starting);
        }
    }
}
