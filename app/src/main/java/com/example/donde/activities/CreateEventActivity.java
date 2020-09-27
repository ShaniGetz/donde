package com.example.donde.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.donde.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends Activity {
    private EditText editTextEventName;
    private EditText editTextLocationName;
    private EditText editTextLongitude;
    private EditText editTextLatitude;
    private Button buttonCreateEvent;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private void initializeFields() {
        editTextEventName = findViewById(R.id.create_editText_event_name);
        editTextLocationName = findViewById(R.id.create_editText_location_name);
        editTextLongitude = findViewById(R.id.create_editText_longitude);
        editTextLatitude = findViewById(R.id.create_editText_latitude);
        buttonCreateEvent = findViewById(R.id.create_button_create);
        progressBar = findViewById(R.id.create_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initializeListeners() {
        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEventName = editTextEventName.getText().toString();
                String sEventLocationName = editTextLocationName.getText().toString();
                long sLongitude = Long.parseLong(editTextLongitude.getText().toString());
                long sLatitude = Long.parseLong(editTextLatitude.getText().toString());
                String currentUserID = firebaseAuth.getCurrentUser().getUid();
                Timestamp timeCreated = new Timestamp(new Date());

                if (isEventFieldsValid(sEventName, sEventLocationName, sLongitude, sLatitude)) {
                    progressBar.setVisibility(View.VISIBLE);

                    Map<String, Object> newEventMap = new HashMap<>();
                    newEventMap.put("eventName", sEventName);
                    newEventMap.put("creatorUID", currentUserID);
                    newEventMap.put("timeCreated", timeCreated);
                    newEventMap.put("eventLocationName", sEventLocationName);
                    newEventMap.put("eventLocation", new GeoPoint(sLatitude, sLongitude));

                } else {
                    Toast.makeText(CreateEventActivity.this, "Some fields are missing",
                            Toast.LENGTH_LONG).show();

                }

            }
        });
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

    private boolean isEventFieldsValid(String eventName, String locationName, Long longitude,
                                       Long latitude) {
        boolean isEventNameValid = !TextUtils.isEmpty(eventName);
        boolean isLocationNameValid = !TextUtils.isEmpty(locationName);
        boolean isLongitudeValid = true;
        boolean isLatitudeValid = true;
        return isEventNameValid && isLocationNameValid && isLatitudeValid && isLongitudeValid;
    }
}
