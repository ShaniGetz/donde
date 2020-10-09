package com.example.donde.utils;

import android.util.Log;

import com.example.donde.R;
import com.example.donde.activities.App;
import com.example.donde.models.EventModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class FirebaseRepository {
    private OnFirestoreTaskComplete onFirestoreTaskComplete;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private FirebaseAuth firebaseAuth;

    public FirebaseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {

        initializeFields(onFirestoreTaskComplete);
    }

    private void initializeFields(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
        firebaseAuth = FirebaseAuth.getInstance();
        eventsRef = firebaseFirestore.collection(App.getRes().getString(R.string.ff_Events));
        usersRef = firebaseFirestore.collection(App.getRes().getString(R.string.ff_Users));


    }


    public void getEventsData() {
        Log.e("FirebaseRepository", "in getEventData");

//        String user_email = mAuth.getCurrentUser().getEmail();
//        Log.e ("Repo", user_email);
//
//        Query query = eventsRef.whereArrayContains(App.getRes().getString(R.string.ff_event_invited_users),
//                firebaseAuth.getCurrentUser().getUid());
        Query query = eventsRef;

        query.get().addOnCompleteListener(task -> {
            Log.e("FirebaseRepository", "in onComplete");
            if (task.isSuccessful()) {
                onFirestoreTaskComplete.eventsListDataAdded(task.getResult().toObjects(EventModel.class));
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }

    public void getUserIDFromEmail(String userEmail) {
//        usersRef.

    }

    public interface OnFirestoreTaskComplete {
        void eventsListDataAdded(List<EventModel> eventsListModelList);

        void onError(Exception e);
    }
}
