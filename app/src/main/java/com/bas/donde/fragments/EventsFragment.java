/*
 * Who last worked here?
 * alon 06/10/20_15:20
 *
 * (Add new name and date above this line)
 */

package com.bas.donde.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bas.donde.R;
import com.bas.donde.activities.EventActivity;
import com.bas.donde.activities.MainActivity;
import com.bas.donde.models.EventModel;
import com.bas.donde.models.InvitedInUserEventModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private final String TAG = "TagEventsFragment";
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference eventsCollectionRef;
    private RecyclerView recyclerViewEventsList;
    private FirestoreRecyclerAdapter eventsRecyclerAdapter;

    public EventsFragment() {
        Log.e("EventsFragment", "Constructor");

        // Required empty public constructor
    }

    private void initializeFields(View view) {
        recyclerViewEventsList = view.findViewById(R.id.events_recyclerView_events);
        firebaseFirestore = FirebaseFirestore.getInstance();
        eventsCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Events));

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

        String userID = ((MainActivity) getActivity()).getFirebaseAuth().getCurrentUser().getUid();
        DocumentReference userDocumentRef =
                firebaseFirestore.collection(getString(R.string.ff_Users)).document(userID);
        CollectionReference eventsInUser = userDocumentRef.collection(getString(R.string.ff_InvitedInUserEvents));


        Query userEventsQuery =
                eventsInUser.orderBy(getString(R.string.ff_InvitedInUserEvents_invitedInUserEventTimeStarting),
                        Query.Direction.DESCENDING);
        // recycler view inflater
        FirestoreRecyclerOptions<InvitedInUserEventModel> eventOptions =
                new FirestoreRecyclerOptions.Builder<InvitedInUserEventModel>().setQuery(userEventsQuery,
                        InvitedInUserEventModel.class).build();
        eventsRecyclerAdapter =
                new FirestoreRecyclerAdapter<InvitedInUserEventModel, EventsViewHolder>(eventOptions) {

                    @Override
                    protected void onBindViewHolder(@NonNull EventsViewHolder holder, int position, @NonNull InvitedInUserEventModel model) {
                        holder.textViewEventName.setText(String.format(model.getInvitedInUserEventName()));
                        holder.textViewEventCreatorName.setText(String.format("Creator name: %s",
                                model.getInvitedInUserEventCreatorName()));
                        holder.textViewEventLocationName.setText(String.format("Location: %s", model.getInvitedInUserEventLocationName()));
                        holder.textViewEventTimeStarting.setText(String.format("Time " +
                                "starting: %s", model.getInvitedInUserEventTimeStarting()));
                        holder.buttonGotoEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseFirestore.collection(getString(R.string.ff_Events)).document(model.getInvitedInUserEventId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        EventModel eventModel =
                                                documentSnapshot.toObject(EventModel.class);

                                        Intent eventIntent = new Intent(getActivity(), EventActivity.class);

                                        eventIntent.putExtra(getString(R.string.arg_position), position);
                                        // gson helps pass objects
                                        Gson gson = new Gson();
                                        String eventJson = gson.toJson(eventModel);
                                        eventIntent.putExtra(getString(R.string.arg_event_model), eventJson);
                                        eventIntent.putExtra(getString(R.string.arg_event_id),
                                                eventModel.getEventID());
                                        startActivity(eventIntent);
                                    }
                                });

                            }
                        });
                        setEventDeleteButtonOnClick(holder, model);
                    }


                    @NonNull
                    @Override
                    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =
                                LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event_item, parent, false);
                        return new EventsViewHolder(view);
                    }


                };

        eventsRecyclerAdapter.startListening();
//                recyclerViewEventsList.setHasFixedSize(true);
        recyclerViewEventsList.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerViewEventsList.getContext(),
                        DividerItemDecoration.VERTICAL);
//        recyclerViewEventsList.addItemDecoration(dividerItemDecoration);
        recyclerViewEventsList.setAdapter(eventsRecyclerAdapter);
    }


//        userDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
////                userInvitedEventIDs = (List<String>) documentSnapshot.get(getString(R.string.ff_users_userInvitedEventIDs));
////                Log.d(TAG, String.format("userInvitedEventIDs size is %s", userInvitedEventIDs.size()));
//
//
////                Query eventsQuery =
////                        eventsCollectionRef.whereIn(FieldPath.documentId(), userInvitedEventIDs).orderBy(
////                                getString(R.string.ff_Events_eventTimeStarting));
//                // TODO: Fix to time starting


//        });


//    }

    private void deleteEventFromInvitedUsers(InvitedInUserEventModel model,
                                             CollectionReference invitedInEventUsersRef) {

        WriteBatch usersDeleteBatch = firebaseFirestore.batch();

        CollectionReference usersRef =
                firebaseFirestore.collection(getString(R.string.ff_Users));
        invitedInEventUsersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot userInEventSnapshot : queryDocumentSnapshots) {
                    String userInEventID = userInEventSnapshot.getId();
                    // TODO: show loading bar and handle onFailure
                    usersDeleteBatch.delete(usersRef.document(userInEventID).collection(getString(R.string.ff_InvitedInUserEvents)).document(model.getInvitedInUserEventId()));
                }
                usersDeleteBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        //TODO: Should be done with cloud functions with node.js
                        // first delete subcollection
                        deleteEventSubcollection(model, invitedInEventUsersRef);


                    }
                });


            }


        });
    }

    private void setEventDeleteButtonOnClick(@NonNull EventsViewHolder holder, @NonNull InvitedInUserEventModel model) {
        holder.buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference eventDocumentRef = eventsCollectionRef.document(model.getInvitedInUserEventId());
                CollectionReference invitedInEventUsersRef =
                        eventDocumentRef.collection(getString(R.string.ff_InvitedInEventUsers));

                // delete event from invited users
                deleteEventFromInvitedUsers(model, invitedInEventUsersRef);


            }
        });

    }

    private void deleteEventSubcollection(InvitedInUserEventModel model,
                                          CollectionReference invitedInEventUsersRef) {
        WriteBatch deleteEventSubcollectionBatch = firebaseFirestore.batch();

        invitedInEventUsersRef.get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentReference : queryDocumentSnapshots) {
                            deleteEventSubcollectionBatch.delete(invitedInEventUsersRef.document(documentReference.getId()));
                        }
                        deleteEventSubcollectionBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteEventDocument(model);
                            }
                        });
                    }
                }
        );
    }

    private void deleteEventDocument(InvitedInUserEventModel model) {
        // then delete document itself


        eventsCollectionRef.document(model.getInvitedInUserEventId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), String.format("Event \"%s\" deleted successfully",
                        model.getInvitedInUserEventName()), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Toast.makeText(getContext(), String.format("Could" +
                                " not delete event. Error: %s", e.getMessage()),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (eventsRecyclerAdapter != null) {

            eventsRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (eventsRecyclerAdapter != null) {

            eventsRecyclerAdapter.stopListening();
        }
    }


    class EventsViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewEventName;
        private TextView textViewEventTimeStarting;
        private TextView textViewEventCreatorName;
        private TextView textViewEventLocationName;
        private CheckBox checkBoxEventIsGoing;
        private Button buttonGotoEvent;
        private Button buttonDeleteEvent;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.event_item_textView_event_name);
            textViewEventTimeStarting = itemView.findViewById(R.id.event_item_textView_event_time_starting);
            textViewEventCreatorName = itemView.findViewById(R.id.event_item_textView_event_creator_name);
            textViewEventLocationName = itemView.findViewById(R.id.event_item_textView_event_location_name);
            checkBoxEventIsGoing = itemView.findViewById(R.id.event_item_checkBox_is_going);
            buttonGotoEvent = itemView.findViewById(R.id.event_item_button_goto_event);
            buttonDeleteEvent = itemView.findViewById(R.id.event_item_button_delete_event);
        }
    }

}
