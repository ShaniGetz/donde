package com.example.donde;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donde.event_data.EventDetails;
import com.firebase.client.Firebase;


public class EventActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase ref, reference1, reference2;
    TextView eventName, eventDate, eventLocation, invitedUser;

    protected void setLayout() {
        eventName.setText(EventDetails.eventName);
//        eventLocation.setText(EventDetails.eventLocation);
//        invitedUser.setText(EventDetails.inviteUser);
//        eventDate.setText(EventDetails.eventDate);


    }

    protected void bindLayout() {

        eventName = findViewById(R.id.textView_event_name);
        eventDate = findViewById(R.id.textView_event_date);
        eventLocation = findViewById(R.id.textView_event_location);
        invitedUser = findViewById(R.id.textView_invited_user);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);

        setLayout();
        bindLayout();
        Firebase.setAndroidContext(this);

        ref = new Firebase("https://donde-4cda4.firebaseio.com/events/" + EventDetails.eventName + "_" + UserDetails.username);

//        reference1 = new Firebase("https://donde-4cda4.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
//        reference2 = new Firebase("https://donde-4cda4.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String messageText = messageArea.getText().toString();
//
//                if(!messageText.equals("")){
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("message", messageText);
//                    map.put("user", UserDetails.username);
//                    reference1.push().setValue(map);
//                    reference2.push().setValue(map);
//                    messageArea.setText("");
//                }
//            }
//        });

//        reference1.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map map = dataSnapshot.getValue(Map.class);
//                String message = map.get("message").toString();
//                String userName = map.get("user").toString();
//
//                if(userName.equals(UserDetails.username)){
//                    addMessageBox(message, 1);
//                }
//                else{
//                    addMessageBox(message, 2);
//                }
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
//    public void addMessageBox(String message, int type){
//        TextView textView = new TextView(Event.this);
//        textView.setText(message);
//
//        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp2.weight = 7.0f;
//
//        if(type == 1) {
//            lp2.gravity = Gravity.LEFT;
//            textView.setBackgroundResource(R.drawable.bubble_in);
//        }
//        else{
//            lp2.gravity = Gravity.RIGHT;
//            textView.setBackgroundResource(R.drawable.bubble_out);
//        }
//        textView.setLayoutParams(lp2);
//        layout.addView(textView);
//        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}