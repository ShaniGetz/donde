package com.example.donde.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.donde.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventActivity extends Activity {
    private EditText editTextEventName;
    private EditText editTextEventDescription;
    private EditText editTextLocationName;
    private EditText editTextLongitude;
    private EditText editTextLatitude;
    private EditText editTextEventDay;
    private EditText editTextEventMonth;
    private EditText editTextEventYear;
    private Button buttonCreateEvent;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private void initializeFields() {
        editTextEventName = findViewById(R.id.create_editText_event_name);
        editTextEventDescription = findViewById(R.id.create_editText_event_description);
        editTextLocationName = findViewById(R.id.create_editText_location_name);
        editTextLongitude = findViewById(R.id.create_editText_longitude);
        editTextLatitude = findViewById(R.id.create_editText_latitude);
        buttonCreateEvent = findViewById(R.id.create_button_create);
        progressBar = findViewById(R.id.create_progressBar);
        editTextEventDay = findViewById(R.id.create_editText_event_day);
        editTextEventMonth = findViewById(R.id.create_editText_event_month);
        editTextEventYear = findViewById(R.id.create_editText_event_year);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initializeListeners() {
        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEventName = editTextEventName.getText().toString();
                String sEventDescription = editTextEventDescription.getText().toString();
                String sEventLocationName = editTextLocationName.getText().toString();

                double sLongitude = Double.parseDouble(editTextLongitude.getText().toString());
                double sLatitude = Double.parseDouble(editTextLatitude.getText().toString());
                GeoPoint eventLocation = new GeoPoint(sLatitude, sLongitude);

                int sEventDay = Integer.parseInt(editTextEventDay.getText().toString());
                int sEventMonth = Integer.parseInt(editTextEventMonth.getText().toString());
                int sEventYear = Integer.parseInt(editTextEventYear.getText().toString());
                Timestamp startDate = new Timestamp(new Date(sEventYear, sEventMonth, sEventDay));

                String currentUserID = firebaseAuth.getCurrentUser().getUid();

                List<String> sInvitedUserIDs = Arrays.asList(currentUserID);

                Timestamp timeCreated = Timestamp.now();

                if (isEventFieldsValid(sEventName, sEventLocationName, sLongitude, sLatitude)) {
                    progressBar.setVisibility(View.VISIBLE);

                    Map<String, Object> newEventMap = new HashMap<>();
                    newEventMap.put(getString(R.string.ff_event_name), sEventName);
                    newEventMap.put(getString(R.string.ff_event_description), sEventDescription);
                    newEventMap.put(getString(R.string.ff_event_creator_uid), currentUserID);
                    newEventMap.put(getString(R.string.ff_event_time_created), timeCreated);
                    newEventMap.put(getString(R.string.ff_event_location_name), sEventLocationName);
                    newEventMap.put(getString(R.string.ff_event_location), eventLocation);
                    newEventMap.put(getString(R.string.ff_event_start_time), startDate);
                    newEventMap.put(getString(R.string.ff_event_invited_users), sInvitedUserIDs);

                    firebaseFirestore.collection("EventsList").add(newEventMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    })
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(CreateEventActivity.this, "Event created " +
                                            "successfully", Toast.LENGTH_LONG).show();
                                    gotoEvents();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(CreateEventActivity.this, "Error while trying " +
                                                    "to create event",
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                } else {
                    Toast.makeText(CreateEventActivity.this, "Some fields are missing",
                            Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void gotoEvents() {
        Intent eventsIntent= new Intent(CreateEventActivity.this, MainActivity.class);
        startActivity(eventsIntent);
        // don't allow going back to creating event
        finish();

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initializeFields();
        initializeListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean isEventFieldsValid(String eventName, String locationName, double longitude,
                                       double latitude) {
        boolean isEventNameValid = !TextUtils.isEmpty(eventName);
        boolean isLocationNameValid = !TextUtils.isEmpty(locationName);
        boolean isLongitudeValid = true;
        boolean isLatitudeValid = true;
        return isEventNameValid && isLocationNameValid && isLatitudeValid && isLongitudeValid;
    }
}
