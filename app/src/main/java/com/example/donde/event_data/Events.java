//package com.example.donde.event_data;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.donde.R;
//import com.firebase.client.ChildEventListener;
//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//
//
//public class Events extends AppCompatActivity {
//    ListView eventsList;
//    TextView noEventsText;
//    EditText eventName;
//    Button addEvent;
//    ArrayList<String> al = new ArrayList<>();
//    int totalEvents = 0;
//    ProgressDialog pd;
//
//    Firebase refEv;
//
//    protected void bindLayout() {
//        eventsList = findViewById(R.id.eventsList);
//        noEventsText = findViewById(R.id.noEventsText);
//        addEvent = findViewById(R.id.add_event);
//        eventName = findViewById(R.id.textView_event_name);
//    }
//
//    protected void setLayout() {
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_events);
//        Firebase.setAndroidContext(this);
//
//        pd = new ProgressDialog(Events.this);
//        pd.setMessage("Loading...");
//        pd.show();
//
//        String url = "https://donde-4cda4.firebaseio.com/events.json";
//
//        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                doOnSuccess(s);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                System.out.println("" + volleyError);
//            }
//        });
//
//        RequestQueue rQueue = Volley.newRequestQueue(Events.this);
//        rQueue.add(request);
//
//        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                EventDetails.eventName = al.get(position);
//                startActivity(new Intent(Events.this, EventActivity.class));
//            }
//        });
//
////        refEv = new Firebase()
//
//        refEv = new Firebase("https://donde-4cda4.firebaseio.com/events");
//
//
//        addEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addEventOnClick(v);
//            }
//        });
//
//
//        refEv.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                Map map = dataSnapshot.getValue(Map.class);
////                String eventName = map.get("name").toString();
////
////                Log.e(eventName, eventName);
////
////                TextView newEvent = new TextView(Events.this);
////                newEvent.setText(eventName);
////                eventsList.getParent().addView(newEvent);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//    }
//
//    public void addEventOnClick(View v) {
////        EventDetails.setEventDetails(eventName.getText().toString(), .getText().toString(););
//        EventDetails.eventName = eventName.getText().toString();
//        startActivity(new Intent(Events.this, AddEvent.class));
//    }
//
//
//    public void doOnSuccess(String s) {
//        try {
//            JSONObject obj = new JSONObject(s);
//
//            Iterator i = obj.keys();
//            String key = "";
//
//            while (i.hasNext()) {
//                key = i.next().toString();
//
//                if (!key.equals(UserDetails.username)) {
//                    al.add(key);
//                }
//
//                totalEvents++;
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (totalEvents < 1) {
//            noEventsText.setVisibility(View.VISIBLE);
//            eventsList.setVisibility(View.GONE);
//        } else {
//            noEventsText.setVisibility(View.GONE);
//            eventsList.setVisibility(View.VISIBLE);
//            eventsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
//        }
//
//        pd.dismiss();
//    }
//}