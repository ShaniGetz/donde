/*
 * Who last worked here?
 * alon 06/10/20_15:20
 *
 * (Add new name and date above this line)
 */

package com.bas.donde.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bas.donde.models.InvitedInEventUserModel;
import com.bas.donde.models.InvitedInUserEventModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.bas.donde.utils.CodeHelpers.myAssert;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private final String TAG = "TagEventsFragment";
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference eventsCollectionRef;
    private RecyclerView recyclerViewEventsList;
    private FirestoreRecyclerAdapter eventsRecyclerAdapter;
    private String currUserID;

    private HashMap<String, Bitmap> mUsersBitmaps;
    private String eventJson;
    private ArrayList<InvitedInEventUserModel> invitedInEventUserModelsList;
    private String invitedUserInEventModelListJson;


    public EventsFragment() {
        Log.e("EventsFragment", "Constructor");

        // Required empty public constructor
    }

    private void initializeFields(View view) {
        recyclerViewEventsList = view.findViewById(R.id.events_recyclerView_events);
        firebaseFirestore = FirebaseFirestore.getInstance();
        eventsCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Events));
        mUsersBitmaps = new HashMap<>();

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

    private void initializeInvitedUsersList(CollectionReference invitedInEventUsersCollectionRef,
                                            String currUserID, int position, EventModel eventModel) {
        invitedInEventUsersCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                firebaseFirestore.runTransaction(new Transaction.Function<ArrayList<InvitedInEventUserModel>>() {

                    @Nullable
                    @Override
                    public ArrayList<InvitedInEventUserModel> apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        ArrayList<InvitedInEventUserModel> invitedInEventUserModels = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d(TAG, String.format("Adding query snapshot name: %s",
                                    documentSnapshot.get(getString(R.string.ff_InvitedInEventUsers_invitedInEventUserName))));
                            String userId =
                                    documentSnapshot.getString(getString(R.string.ff_InvitedInEventUsers_invitedInEventUserID));

                            Log.d(TAG, "checking if " + userId + "==" + currUserID);
                            if (TextUtils.equals(userId, currUserID)) {
                                Log.d(TAG, "enteres");
                                invitedInEventUserModels.add(0,
                                        documentSnapshot.toObject(InvitedInEventUserModel.class));
                            } else {

                                invitedInEventUserModels.add(documentSnapshot.toObject(InvitedInEventUserModel.class));
                            }


                        }
                        Log.d(TAG, "index 0: " + invitedInEventUserModels.get(0)
                                .getInvitedInEventUserName());
                        return invitedInEventUserModels;
                    }
                }).addOnSuccessListener(new OnSuccessListener<ArrayList<InvitedInEventUserModel>>() {
                    @Override
                    public void onSuccess(ArrayList<InvitedInEventUserModel> invitedInEventUserModels) {

                        Gson gson = new Gson();
                        eventJson = gson.toJson(eventModel);
                        invitedInEventUserModelsList = invitedInEventUserModels;
                        invitedUserInEventModelListJson = gson.toJson(invitedInEventUserModelsList);
                        initializeUsersBitmaps();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, String.format("Transaction failed with error: %s", e.getMessage()));
                    }
                });

            }
        });
    }

    private void initializeEventsList() {

        currUserID = ((MainActivity) getActivity()).getFirebaseAuth().getCurrentUser().getUid();
        DocumentReference userDocumentRef =
                firebaseFirestore.collection(getString(R.string.ff_Users)).document(currUserID);
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
                        setGotoEventOnClick(holder, position, model);
                        setIsGoingEventOnClick(holder, position, model);
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
        recyclerViewEventsList.addItemDecoration(dividerItemDecoration);
        recyclerViewEventsList.setAdapter(eventsRecyclerAdapter);
    }

    private void setIsGoingEventOnClick(@NonNull EventsViewHolder holder, int position, @NonNull InvitedInUserEventModel model) {
        holder.checkBoxEventIsGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Downloading invited users list",
                        Toast.LENGTH_SHORT).show();
                DocumentReference eventRef =
                        firebaseFirestore.collection(getString(R.string.ff_Events)).document(model.getInvitedInUserEventId());
                eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventModel eventModel =
                                documentSnapshot.toObject(EventModel.class);
                        CollectionReference invitedInEventUsersCollectionRef =
                                eventRef.collection(getString(R.string.ff_InvitedInEventUsers));
                        initializeInvitedUsersList(invitedInEventUsersCollectionRef, currUserID,
                                position, eventModel);
                    }
                });
            }
        });
    }

    private void initializeUsersBitmaps() {
        Log.d(TAG, "in initializeUsersBitmaps");
        Toast.makeText(getContext(), "Getting users bitmaps", Toast.LENGTH_SHORT).show();
        for (InvitedInEventUserModel user : invitedInEventUserModelsList) {
            getUserAndPutAvatar(user);
        }

    }

    private void setGotoEventOnClick(@NonNull EventsViewHolder holder, int position, @NonNull InvitedInUserEventModel model) {
        holder.buttonGotoEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent eventIntent = new Intent(getActivity(), EventActivity.class);
                eventIntent.putExtra(getString(R.string.arg_position), position);
                eventIntent.putExtra(getString(R.string.arg_event_model), eventJson);
                eventIntent.putExtra(getString(R.string.arg_event_id), model.getInvitedInUserEventId());
                eventIntent.putExtra(getString(R.string.arg_invitedInEventUserModels_intent), invitedUserInEventModelListJson);
                Gson gson = new Gson();
                String invitedUsersInEventBitmapsJson = gson.toJson(mUsersBitmaps);

                eventIntent.putExtra(getString(R.string.arg_users_bitmaps_intent), invitedUsersInEventBitmapsJson);


                startActivity(eventIntent);

//
//                DocumentReference eventRef =
//                        firebaseFirestore.collection(getString(R.string.ff_Events)).document(model.getInvitedInUserEventId());
//                eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        EventModel eventModel =
//                                documentSnapshot.toObject(EventModel.class);
//                        CollectionReference invitedInEventUsersCollectionRef =
//                                eventRef.collection(getString(R.string.ff_InvitedInEventUsers));
//                        initializeInvitedUsersList(invitedInEventUsersCollectionRef, currUserID,
//                                position, eventModel);


//                        Intent eventIntent = new Intent(getActivity(), EventActivity.class);
//
//                        eventIntent.putExtra(getString(R.string.arg_position), position);
//                        // gson helps pass objects
//                        Gson gson = new Gson();
//                        String eventJson = gson.toJson(eventModel);
//                        eventIntent.putExtra(getString(R.string.arg_event_model), eventJson);
//                        eventIntent.putExtra(getString(R.string.arg_event_id),
//                                eventModel.getEventID());
//                        startActivity(eventIntent);
//                    }
//                });
//
//            }
//        });
            }
        });
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

    private Bitmap getUserAndPutAvatar(InvitedInEventUserModel user) {
        Log.d(TAG, "in getUserAvatar for " + user.getInvitedInEventUserName());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(user.getInvitedInEventUserID() + ".jpg");
//                        StorageReference gsReference = storage.getReferenceFromUrl(user.getInvitedInEventUserProfilePicURL());
        imageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String dir = saveToInternalStorage(bitmap);

                Bitmap avatar = loadImageFromStorage(dir);
                mUsersBitmaps.put(user.getInvitedInEventUserID(), avatar);
                Log.d(TAG, "put avatar for " + user.getInvitedInEventUserName());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO: Implement default avatar
                myAssert(false, "Failed to get user avatar for " + user.getInvitedInEventUserName());
//                avatar = defaultAvatar();
            }
        });
        return null;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "photo.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private Bitmap loadImageFromStorage(String path) {
        try {
            File f = new File(path, "photo.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //todo: conect to the rellevant clustermarker b
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
