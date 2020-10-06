package com.example.donde.events_recycler_view;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.donde.models.EventModel;
import com.example.donde.utils.FirebaseRepository;

import java.util.List;

public class EventsListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    private MutableLiveData<List<EventModel>> eventsListModelData = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;


    public EventsListViewModel() {
        Log.e("EventsListViewModel", "in EventsListViewModel");
        firebaseRepository = new FirebaseRepository(this);
        firebaseRepository.getEventsData();
    }

    public LiveData<List<EventModel>> getEventsListModelData() {
        Log.e("EventsListViewModel", "in getEventsListModelData");

        return eventsListModelData;
    }

    @Override
    public void eventsListDataAdded(List<EventModel> eventsListModelList) {
        Log.e("EventsListViewModel", "in eventsListDataAdded");

        eventsListModelData.setValue(eventsListModelList);
    }

    @Override
    public void onError(Exception e) {
        Log.e("EventsListViewModel", e.getMessage());


    }
}
