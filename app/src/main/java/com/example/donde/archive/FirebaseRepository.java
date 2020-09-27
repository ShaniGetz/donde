package com.example.donde.archive;

import android.util.Log;

import androidx.annotation.NonNull;

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

        String user_email = mAuth.getCurrentUser().getEmail();
        Log.e ("Repo", user_email);

        Query query = eventsRef.whereArrayContains("invited_people", mAuth.getCurrentUser().getEmail());

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
