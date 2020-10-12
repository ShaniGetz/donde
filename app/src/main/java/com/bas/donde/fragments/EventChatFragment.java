package com.bas.donde.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bas.donde.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventChatFragment extends Fragment {
    private EditText editTextMessage;
    private Button buttonSendMessage;
    private ProgressBar progressBarSendProgress;
    private String eventID;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    private void initializeFields(View view) {
        String argEventID = getArguments().getString("eventID");
        eventID = argEventID;
        editTextMessage = view.findViewById(R.id.chat_editText_message);
        buttonSendMessage = view.findViewById(R.id.chat_button_send_message);
        progressBarSendProgress = view.findViewById(R.id.chat_progressBar_send_progress);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


    }


//    private void initializeListeners() {
//        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String sMessage = editTextMessage.getText().toString();
//                String sUID = firebaseAuth.getUid();
//                if (isMessageValid(sMessage)) {
//                    progressBarSendProgress.setVisibility(View.VISIBLE);
//                    Map<String, Object> messageMap = new HashMap<>();
//                    messageMap.put(getString(R.string.ff_chat_message_content_key), sMessage);
//                    messageMap.put(getString(R.string.ff_chat_sender_uid_key), sUID);
//                    firebaseFirestore.collection("EventsList/" + eventID + "/Chat").add(messageMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentReference> task) {
//                            progressBarSendProgress.setVisibility(View.INVISIBLE);
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            editTextMessage.setText("");
//                            Toast.makeText(getActivity(), "Message sent successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
//    }

    private boolean isMessageValid(String message) {
        boolean isMessageEmpty = TextUtils.isEmpty(message);
        return !isMessageEmpty;
    }

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_event_chat, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeFields(view);
//        initializeListeners();
    }
}