package com.example.donde.event_data;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.donde.EventActivity;
import com.example.donde.Events;
import com.example.donde.R;
import com.example.donde.UserDetails;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public class AddEvent extends AppCompatActivity {

    EditText eventName;
    EditText eventDate;
    EditText eventLocation;
    EditText invitedUser;
    ListView usersList;


    Button addEvent;
    ArrayList<String> al = new ArrayList<>();
    int totalEvents = 0;
    ProgressDialog pd;

    Firebase refEv;

    protected void initMembers() {
        eventName = findViewById(R.id.textView_event_name);
        eventName.setText(EventDetails.eventName);
        eventDate = findViewById(R.id.event_date);
        eventLocation = findViewById(R.id.event_location);
        invitedUser = findViewById(R.id.invitedUser);
        usersList = findViewById(R.id.usersList);
        addEvent = findViewById(R.id.add_event);
        refEv = new Firebase("https://donde-4cda4.firebaseio.com/events");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void addEventToFirebase(String event_name) {
        refEv.child(event_name).child("name").setValue(event_name);
        refEv.child(event_name).child("creatorUsername").setValue(UserDetails.username);
        refEv.child(event_name).child("creationTimestamp").setValue(OffsetDateTime.now().toString());
        refEv.child(event_name).child("eventStartDateTime").setValue(eventDate.getText().toString());
        refEv.child(event_name).child("eventLocation").setValue(eventLocation.getText().toString());
        refEv.child(event_name).child("invited").child(UserDetails.username).setValue(true);
        refEv.child(event_name).child("invited").child(invitedUser.getText().toString()).setValue(true);

        Toast.makeText(AddEvent.this, "event addition successful", Toast.LENGTH_LONG).show();

    }

    protected void addEventLocally(String event_name) {

        al.add(event_name);
        totalEvents++;
    }

    protected void addEventOnClick(View v) {
        showLoading();
        final String event_name = eventName.getText().toString();
        addEventLocally(event_name);

        String url = "https://donde-4cda4.firebaseio.com/events.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String s) {

                if (s.equals("null")) {
                    addEventToFirebase(event_name);
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(event_name)) {
                            addEventToFirebase(event_name);
                            Toast.makeText(AddEvent.this, "event addition successful", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(AddEvent.this, "event name already exists", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                pd.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(AddEvent.this);
        rQueue.add(request);

        startActivity(new Intent(AddEvent.this, Events.class));
    }

    protected void usersListOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventDetails.eventName = al.get(position);
        startActivity(new Intent(AddEvent.this, EventActivity.class));

    }

    protected void showLoading() {
        pd = new ProgressDialog(AddEvent.this);
        pd.setMessage("Loading...");
        pd.show();
    }

    protected void startStringRequest() {
        String url = "https://donde-4cda4.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(AddEvent.this);
        rQueue.add(request);
    }

    protected void setListeners() {
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usersListOnItemClick(parent, view, position, id);
            }


//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                usersListOnItemClick(parent, view, position, id);
//            }
        });
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventOnClick(v);
            }
        });
        refEv.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map map = dataSnapshot.getValue(Map.class);
//                String eventName = map.get("name").toString();
//
//                Log.e(eventName, eventName);
//
//                TextView newEvent = new TextView(Events.this);
//                newEvent.setText(eventName);
//                eventsList.getParent().addView(newEvent);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Firebase.setAndroidContext(this);

        initMembers();
        showLoading();
        setListeners();
        startStringRequest();
    }


    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while (i.hasNext()) {
                key = i.next().toString();

                if (!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalEvents++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (totalEvents < 1) {
//            noEventsText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
//            noEventsText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();
    }

}