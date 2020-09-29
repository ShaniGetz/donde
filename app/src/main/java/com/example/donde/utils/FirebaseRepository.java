package com.example.donde.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.donde.R;
import com.example.donde.activities.App;
import com.example.donde.events_recycler_view.EventsListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseRepository {
    private OnFirestoreTaskComplete onFirestoreTaskComplete;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = firebaseFirestore.collection("EventsList");

    public FirebaseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    public void getEventsData() {
        Log.e("FirebaseRepository", "in getEventData");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

//        String user_email = mAuth.getCurrentUser().getEmail();
//        Log.e ("Repo", user_email);

        Query query =
                eventsRef.whereArrayContains(App.getRes().getString(R.string.ff_event_invited_users),
                        mAuth.getCurrentUser().getUid());
//        Query query = eventsRef.whereArrayContains("invitedUsers", "Ov8ktisRIQWi7JkT8w4BAg0hzWc2");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e("FirebaseRepository", "in onComplete");
                if (task.isSuccessful()) {
                    onFirestoreTaskComplete.eventsListDataAdded(task.getResult().toObjects(EventsListModel.class));
                } else {
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }
        });
    }

    public interface OnFirestoreTaskComplete {
        void eventsListDataAdded(List<EventsListModel> eventsListModelList);

        void onError(Exception e);
    }
}
