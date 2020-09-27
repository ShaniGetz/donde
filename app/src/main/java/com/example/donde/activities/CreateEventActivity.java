package com.example.donde.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.donde.R;

public class CreateEventActivity extends Activity {
    private EditText editTextEventName;
    private EditText editTextLocationName;
    private EditText editTextLongitude;
    private EditText editTextLatitude;
    private Button buttonCreateEvent;

    private void initializeFields() {
        editTextEventName = findViewById(R.id.create_editText_event_name);
        editTextLocationName = findViewById(R.id.create_editText_location_name);
        editTextLongitude = findViewById(R.id.create_editText_longitude);
        editTextLatitude = findViewById(R.id.create_editText_latitude);
        buttonCreateEvent = findViewById(R.id.create_button_create);
    }

    private void initializeListeners() {

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
}
