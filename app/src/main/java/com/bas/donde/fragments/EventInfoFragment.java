package com.bas.donde.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bas.donde.R;
import com.bas.donde.activities.EventActivity;
import com.bas.donde.models.EventModel;
import com.bas.donde.models.InvitedInEventUserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EventInfoFragment extends Fragment {

    // Firebase
    FirebaseFirestore firebaseFirestore;

    // view fields
    private TextView textViewEventName;
    private TextView textViewEventDescription;
    private TextView textViewEventLocationName;
    private TextView textViewEventCreatorName;
    private TextView textViewEventTimeStarting;
    private TextView textViewEventTimeCreated;
    private RecyclerView recyclerViewInvitedUsers;

    // data fields
    private String eventId;
    private EventModel event;
    private FirestoreRecyclerAdapter invitedUsersRecyclerAdapter;

    private void initializeFields(View view) {

        textViewEventName = view.findViewById(R.id.info_textView_event_name);
        textViewEventDescription = view.findViewById(R.id.info_textView_event_description);
        textViewEventLocationName = view.findViewById(R.id.info_textView_location_name);
        textViewEventCreatorName = view.findViewById(R.id.info_textView_creator_name);
        textViewEventTimeStarting = view.findViewById(R.id.info_textView_time_starting);
        textViewEventTimeCreated = view.findViewById(R.id.info_textView_time_created);
        recyclerViewInvitedUsers = view.findViewById(R.id.info_recyclerView_invited_users);

        event = ((EventActivity) getActivity()).getEvent();
        eventId = ((EventActivity) getActivity()).getEventID();

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initializeViews() {
        textViewEventName.setText(String.format(event.getEventName()));
        textViewEventDescription.setText(String.format(event.getEventDescription()));
        textViewEventLocationName.setText(String.format("Location:" + "\n"+ "%s",
                event.getEventLocationName()));
        textViewEventCreatorName.setText(String.format("Creator name: %s",
                event.getEventCreatorName()));
        textViewEventTimeStarting.setText(String.format("Event time starting: %s",
                event.getEventTimeStarting()));
        textViewEventTimeCreated.setText(String.format("Event time created: %s",
                event.getEventTimeCreated()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_info, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeFields(view);
        initializeViews();
        initializeInvitedUsersList();
    }


    private void initializeInvitedUsersList() {

        Query invitedUsersQuery =
                firebaseFirestore.collection(getString(R.string.ff_Events)).document(this.eventId).collection(getString(R.string.ff_InvitedInEventUsers));
        FirestoreRecyclerOptions<InvitedInEventUserModel> invitedUsersOptions =
                new FirestoreRecyclerOptions.Builder<InvitedInEventUserModel>().setQuery(invitedUsersQuery, InvitedInEventUserModel.class).build();
        invitedUsersRecyclerAdapter =
                new FirestoreRecyclerAdapter<InvitedInEventUserModel, InvitedUsersViewHolder>(invitedUsersOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull InvitedUsersViewHolder holder, int position, @NonNull InvitedInEventUserModel model) {
                        Log.e("EventInfoFragment", String.format("user name is %s", model.getInvitedInEventUserName()));
                        holder.textViewInvitedUserName.setText(model.getInvitedInEventUserName());
                    }


                    @NonNull
                    @Override
                    public InvitedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =
                                LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_user_single_item,
                                        parent, false);
                        return new InvitedUsersViewHolder(view);
                    }
                };

        recyclerViewInvitedUsers.setHasFixedSize(true);
        recyclerViewInvitedUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerViewInvitedUsers.getContext(),
                        DividerItemDecoration.VERTICAL);
        recyclerViewInvitedUsers.addItemDecoration(dividerItemDecoration);
        recyclerViewInvitedUsers.setAdapter(invitedUsersRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        invitedUsersRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        invitedUsersRecyclerAdapter.stopListening();
    }

    class InvitedUsersViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewInvitedUserName;

        public InvitedUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewInvitedUserName = itemView.findViewById(R.id.invited_user_textView_user_name);
        }
    }
}

// TODO: show snippet of map of event in info page