package com.bas.donde.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bas.donde.R;
import com.bas.donde.activities.App;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class EventDeleter {

    public static final String TAG = "EventDeleter";
    FirebaseFirestore db;
    String eventDocumentID;
    DocumentReference eventDocumentRef;
    Transaction ta;
    ArrayList<DocumentReference> documentsToDelete;


    public EventDeleter(FirebaseFirestore db,
                        String eventDocumentID, DocumentReference eventDocumentRef) {
        Log.d(TAG, String.format("in constructor, trying to delete %s", eventDocumentID));
        this.db = db;
        this.eventDocumentID = eventDocumentID;
        this.eventDocumentRef = eventDocumentRef;
        this.documentsToDelete = new ArrayList<>();

    }

    private void doReads() throws FirebaseFirestoreException {
        // add the event to documents that should be deleted
        Log.d(TAG, String.format("in doReads"));

        documentsToDelete.add(eventDocumentRef);

        // iterate over user sub-collection and delete event from users
        DocumentSnapshot eventSnapshot = ta.get(eventDocumentRef);
        ArrayList<String> eventInvitedUserIDs = (ArrayList<String>) eventSnapshot.get(App.getRes().getString(R.string.ff_Events_eventInvitedUserIDs));
        for (int i = 0; i < eventInvitedUserIDs.size(); i++) {
            readInEventUser(eventInvitedUserIDs.get(i));
        }
    }

    private void readInEventUser(String inEventUserID) {
        Log.d(TAG, String.format("in readInEventUser, trying to delete %s", inEventUserID));

        DocumentReference invitedInEventUserRef =
                eventDocumentRef.collection(App.getRes().getString(R.string.ff_InvitedInEventUsers)).document(inEventUserID);
        documentsToDelete.add(invitedInEventUserRef);
        removeInUserEvent(inEventUserID);

    }

    private void removeInUserEvent(String userID) {
        Log.d(TAG, String.format("in removeInUserEvent, trying to delete event from %s", userID));

        DocumentReference inUserEventRef =
                db.collection(App.getRes().getString(R.string.ff_Users)).document(userID)
                        .collection(App.getRes().getString(R.string.ff_InvitedInUserEvents))
                        .document(eventDocumentID);
        documentsToDelete.add(inUserEventRef);
    }

    private void doWrites() {
        Log.d(TAG, String.format("in doWrites, trying to delete %s documents",
                documentsToDelete.size()));
        for (DocumentReference docRefToDelete : documentsToDelete) {
            ta.delete(docRefToDelete);
        }

    }

    public void deleteEvent() {
        db.runTransaction(new Transaction.Function<Void>() {

            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                ta = transaction;
                doReads();
                doWrites();

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, String.format("in onSuccess, Event %s officially deleted successfully",
                        eventDocumentID));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, String.format("in onFailure, failed to delete %s",
                        eventDocumentID));

            }
        });

    }
}
