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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bas.donde.R;
import com.bas.donde.activities.EditEventActivity;
import com.bas.donde.activities.EventActivity;
import com.bas.donde.activities.MainActivity;
import com.bas.donde.models.EventModel;
import com.bas.donde.models.InvitedInEventUserModel;
import com.bas.donde.models.InvitedInUserEventModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    private final String TAG = "tagEventsFragment";


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
                                            String currUserID, int position,
                                            EventModel eventModel, EventsViewHolder holder) {
        invitedInEventUsersCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                Log.d(TAG, "before running  transaction");
                firebaseFirestore.runTransaction(new Transaction.Function<ArrayList<InvitedInEventUserModel>>() {

                    @Nullable
                    @Override
                    public ArrayList<InvitedInEventUserModel> apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        Log.d(TAG, "inside apply");
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
                        initializeUsersBitmaps(holder);
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
                        holder.textViewEventLocationName.setText(String.format(model.getInvitedInUserEventLocationName()));
                        holder.textViewEventTimeStarting.setText("Starting at " + timeFormatting(model.getInvitedInUserEventTimeStarting().toString()));

                        //////

                        setGotoEventOnClick(holder, position, model);
                        setIsGoingEventOnClick(holder, position, model);
                        setEventDeleteButtonOnClick(holder, model);
                        setEditEventOnClick(holder, position, model);
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

    private void setEditEventOnClick(@NonNull EventsViewHolder holder, int position, @NonNull InvitedInUserEventModel model) {
        holder.buttonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection(getString(R.string.ff_Events)).document(model.getInvitedInUserEventId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                        Intent eventIntent = new Intent(getActivity(), EditEventActivity.class);

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
    }

    private void setIsGoingEventOnClick(@NonNull EventsViewHolder holder, int position, @NonNull InvitedInUserEventModel model) {

        holder.checkBoxEventIsGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showProgressBar();
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
                                position, eventModel, holder);
                    }
                });
            }
        });
    }

    private void initializeUsersBitmaps(EventsViewHolder holder) {
        Log.d(TAG, "in initializeUsersBitmaps");
        Toast.makeText(getContext(), "Getting users bitmaps", Toast.LENGTH_SHORT).show();
        for (InvitedInEventUserModel user : invitedInEventUserModelsList) {

            getUserAndPutAvatar(user);
            holder.hideProgressBar();
        }
        // TODO: Doesn't actually end when storage pull ends

    }

    private void setGotoEventOnClick(@NonNull EventsViewHolder holder, int position, @NonNull InvitedInUserEventModel model) {
        holder.buttonGotoEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.buttonGotoEvent.setEnabled(true);
//                holder.buttonGotoEvent.setBackground(R.drawable.rounded_corners_butten);
                Intent eventIntent = new Intent(getActivity(), EventActivity.class);
                eventIntent.putExtra(getString(R.string.arg_position), position);
                eventIntent.putExtra(getString(R.string.arg_event_model), eventJson);
                eventIntent.putExtra(getString(R.string.arg_event_id), model.getInvitedInUserEventId());
                eventIntent.putExtra(getString(R.string.arg_invitedInEventUserModels_intent), invitedUserInEventModelListJson);
                Gson gson = new Gson();
                String invitedUsersInEventBitmapsJson = gson.toJson(mUsersBitmaps);

                eventIntent.putExtra(getString(R.string.arg_users_bitmaps_intent), invitedUsersInEventBitmapsJson);


                startActivity(eventIntent);

            }
        });
    }

    private void deleteEventFromInvitedUsers(InvitedInUserEventModel model,
                                             CollectionReference invitedInEventUsersRef,
                                             EventsViewHolder holder) {
        Log.d(TAG, "in deleteEventFromInvitedUsers");
        WriteBatch usersDeleteBatch = firebaseFirestore.batch();

        CollectionReference usersRef =
                firebaseFirestore.collection(getString(R.string.ff_Users));
        invitedInEventUsersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot userInEventSnapshot : queryDocumentSnapshots) {
                    String userInEventID = userInEventSnapshot.getId();
                    usersDeleteBatch.delete(usersRef.document(userInEventID).collection(getString(R.string.ff_InvitedInUserEvents)).document(model.getInvitedInUserEventId()));
                }
                usersDeleteBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //TODO: Should be done with cloud functions with node.js
                        // first delete subcollection
                        deleteEventSubcollection(model, invitedInEventUsersRef, holder);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.hideProgressBar();
                        Log.d(TAG, "failed to delete event from invited users. Error: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to delete event " + model.getInvitedInUserEventId(),
                                Toast.LENGTH_SHORT).show();

                    }
                });


            }


        });
    }

    private void setEventDeleteButtonOnClick(@NonNull EventsViewHolder holder, @NonNull InvitedInUserEventModel model) {
        holder.buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showProgressBar();
                DocumentReference eventDocumentRef = eventsCollectionRef.document(model.getInvitedInUserEventId());
                CollectionReference invitedInEventUsersRef =
                        eventDocumentRef.collection(getString(R.string.ff_InvitedInEventUsers));
                Log.d(TAG, "Attempting to delete event " + model.getInvitedInUserEventName());
                // delete event from invited users
                deleteEventFromInvitedUsers(model, invitedInEventUsersRef, holder);


            }
        });

    }

    private void deleteEventSubcollection(InvitedInUserEventModel model,
                                          CollectionReference invitedInEventUsersRef,
                                          EventsViewHolder holder) {
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
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // TODO: Try to perform all writes and reads together in a
                                //  transaction
                                holder.hideProgressBar();
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
                Log.d("event Fragment ", String.format("Event \"%s\" deleted successfully",
                        model.getInvitedInUserEventName()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Log.d("Event MAp Fragment", String.format("Could" +
                        " not delete event. Error: %s", e.getMessage()));

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
        StorageReference imageRef = storage.getReference().child(user.getInvitedInEventUserProfilePicURL());
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
                myAssert(false,
                        "Failed to get user avatar for " + user.getInvitedInEventUserName() + " uri" +
                                ": " + user.getInvitedInEventUserID() + ".jpg" +
                                " Error: " + e.getMessage());
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

    private String timeFormatting(String data) {
        String hour = "";
        String minute = "";
        String date = "";
        String Year = data.substring((data.length() - 4));

        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == ':') {
                date = data.substring(0, i - 3);
                hour = data.substring(i - 2, i);
                minute = data.substring(i + 1, i + 3);
                break;
            }
        }
        Log.d("TIMEER", (date + "/" + Year + "    " + hour + ":" + minute));
        return (date + "/" + Year + "    " + hour + ":" + minute);
    }


    class EventsViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewEventName;
        private TextView textViewEventTimeStarting;
        private TextView textViewEventCreatorName;
        private TextView textViewEventLocationName;
        private CheckBox checkBoxEventIsGoing;
        private Button buttonGotoEvent;
        private Button buttonDeleteEvent;
        private Button buttonEditEvent;
        private ConstraintLayout constraintLayoutEventInfo;
        private ProgressBar progressBar;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.event_item_textView_event_name);
            textViewEventTimeStarting = itemView.findViewById(R.id.event_item_textView_event_time_starting);
            textViewEventCreatorName = itemView.findViewById(R.id.event_item_textView_event_creator_name);
            textViewEventLocationName = itemView.findViewById(R.id.event_item_textView_event_location_name);
            checkBoxEventIsGoing = itemView.findViewById(R.id.event_item_checkBox_is_going);
            checkBoxEventIsGoing.setChecked(false);
            buttonGotoEvent = itemView.findViewById(R.id.event_item_button_goto_event);
//            buttonGotoEvent.setEnabled(false);
            buttonGotoEvent.setBackgroundColor(R.drawable.rounded_corners_butten_gray);
            buttonDeleteEvent = itemView.findViewById(R.id.event_item_button_delete_event);
            buttonEditEvent = itemView.findViewById(R.id.Edit_Event);
            constraintLayoutEventInfo = itemView.findViewById(R.id.event_item_info_constraint);
            progressBar = itemView.findViewById(R.id.event_item_progressBar);
        }

        private void showProgressBar() {
            constraintLayoutEventInfo.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        private void hideProgressBar() {
            progressBar.setVisibility(View.INVISIBLE);
            constraintLayoutEventInfo.setVisibility(View.VISIBLE);
        }
    }

}
