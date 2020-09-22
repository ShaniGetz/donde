package com.example.donde;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class EventsListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    private MutableLiveData<List<EventsListModel>> eventsListModelData =new MutableLiveData<>();

    public LiveData<List<EventsListModel>> getEventsListModelData(){
        return eventsListModelData;
    }



    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    public EventsListViewModel() {
        firebaseRepository.getEventsData();
    }

    @Override
    public void eventsListDataAdded(List<EventsListModel> eventsListModelList) {
        eventsListModelData.setValue(eventsListModelList);
    }

    @Override
    public void onError(Exception e) {

    }
}
