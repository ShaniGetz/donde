package com.example.donde.recycle_views.events_recycler_view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donde.R;
import com.example.donde.models.EventModel;

import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventViewHolder> {
    private List<EventModel> eventsListModels;
    private OnEventListItemClicked onEventListItemClicked;


    public EventsListAdapter(OnEventListItemClicked onEventListItemClicked) {
        this.onEventListItemClicked = onEventListItemClicked;
    }

    public void setEventsListModels(List<EventModel> eventsListModels) {
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

        EventModel currentEvent = eventsListModels.get(position);

        holder.listName.setText("Name: " + currentEvent.getEventName());
        holder.listDesc.setText("Description:\n" + currentEvent.getEventDescription());
        holder.listLocationName.setText("Location name: " + currentEvent.getEventLocationName());
        holder.listTimeStarting.setText("Time starting: "+currentEvent.getEventTimeStarting());
        holder.listCreatorUserName.setText("Creator username: "+currentEvent.getEventCreatorUID());
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
        void onItemClicked(int position, String eventID);
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

            listName = itemView.findViewById(R.id.event_item_textView_event_name);
            listDesc = itemView.findViewById(R.id.event_item_textView_event_description);
            listLocationName = itemView.findViewById(R.id.event_item_textView_event_location_name);
//            listLocation = itemView.findViewById(R.id.list_location);
            listCreatorUserName = itemView.findViewById(R.id.event_item_textView_event_creator_name);
//            listTimeCreated = itemView.findViewById(R.id.list_time_created);
            listTimeStarting = itemView.findViewById(R.id.event_item_textView_event_time_starting);
            listGotoEvent = itemView.findViewById(R.id.event_item_button_goto_event);

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
            onEventListItemClicked.onItemClicked(getAdapterPosition(), eventID);
        }
    }
}
