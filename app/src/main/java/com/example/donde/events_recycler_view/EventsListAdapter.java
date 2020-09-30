package com.example.donde.events_recycler_view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donde.R;

import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventViewHolder> {
    private List<EventsListModel> eventsListModels;
    private OnEventListItemClicked onEventListItemClicked;


    public EventsListAdapter(OnEventListItemClicked onEventListItemClicked) {
        this.onEventListItemClicked = onEventListItemClicked;
    }

    public void setEventsListModels(List<EventsListModel> eventsListModels) {
        Log.e("EventListAdapter", "in setEventListModels");

        this.eventsListModels = eventsListModels;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("EventListAdapter", "in onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event_item,
                parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Log.e("EventListAdapter", "in onBindViewHolder");

        EventsListModel currentEvent = eventsListModels.get(position);

        holder.listName.setText(currentEvent.getEventName());
        holder.listDesc.setText(currentEvent.getEventDescription());
        holder.listLocationName.setText(currentEvent.getEventLocationName());
        holder.setEventID(currentEvent.getEventID());
//        long milliseconds = currentEvent.getTime_starting().toDate().getTime();
//        String sDateStarting = DateFormat.format("dd/MM/yyyy", new Date(milliseconds))
//        holder.listTimeStarting.setText(currentEvent.getTime_starting().toDate().toString());


//        String creator_id = currentEvent.getCreator_id();
//        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        firebaseFirestore.collection("Users").document(currentEvent.getCreator_id()).get;
//        holder.listCreatorUserName.setText(currentEvent.getCreator_username());
        holder.setCreatorName();
    }

    @Override
    public int getItemCount() {
        Log.e("EventListAdapter", "in getItemCount");

        if (eventsListModels == null) {
            return 0;
        } else {
            return eventsListModels.size();
        }
    }

    public interface OnEventListItemClicked {
        void onItemClicked(String eventID);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView listName;
        private TextView listDesc;
        private TextView listLocationName;
        private TextView listLocation;
        private TextView listCreatorUserName;
        private TextView listTimeCreated;
        private TextView listTimeStarting;
        private Button listGotoEvent;

        private String eventID;

        public EventViewHolder(@NonNull View itemView) {

            super(itemView);
            Log.e("EventListAdapter", "in EventViewHolder");

            listName = itemView.findViewById(R.id.list_name);
            listDesc = itemView.findViewById(R.id.list_desc);
            listLocationName = itemView.findViewById(R.id.list_location_name);
            listLocation = itemView.findViewById(R.id.list_location);
            listCreatorUserName = itemView.findViewById(R.id.list_creator_username);
            listTimeCreated = itemView.findViewById(R.id.list_time_created);
            listTimeStarting = itemView.findViewById(R.id.list_time_starting);
            listGotoEvent = itemView.findViewById(R.id.list_goto_event);

            listGotoEvent.setOnClickListener(this);
        }

        public void setEventID(String eventID) {
            this.eventID = eventID;
        }

        private void setCreatorName() {

        }

        @Override
        public void onClick(View v) {
//            Log.e("Onclick adapter", "Event id: " + eventID);
//            onEventListItemClicked.onItemClicked(getAdapterPosition());
            onEventListItemClicked.onItemClicked(eventID);
        }
    }
}
