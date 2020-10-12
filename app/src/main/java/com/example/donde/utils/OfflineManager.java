package com.example.donde.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.donde.models.EventModel;
import com.example.donde.models.InvitedInUserEventModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OfflineManager {
    public static final String EVENT_DETAILS_FILE = "event_details.json";
    public static final String EVENT_DETAILS_DIR = "event_details_dir";
    private static final String DEBUG_FILENAME = "test0";
    private static final Gson gson = new Gson();
    private static final String TAG = "tagOfflineManager";
    // the user who is requesting the download
    private FirebaseUser userDownloading;
    // the event that needs to be downloaded
    private InvitedInUserEventModel eventToDownload;
    // app context
    private Context context;
    // file dir of the current app on phone
    private File fileDir;
    private String debugString = "Testing if this gets saved offline";
    private String currUserID;
    private File currUserBaseDir;
    private File currUserEventDir;
    private EventModel eventModel;
    private File eventDetailsDir;
    private File eventDetailsFile;
    ;

//
//    public OfflineManager(FirebaseUser userDownloading,
//                          InvitedInUserEventModel eventToDownload, Context context) {
//        Log.d(TAG, "in OfflineManager constructor");
//        this.context = context;
//        this.eventToDownload = eventToDownload;
//        this.userDownloading = userDownloading;
//        this.fileDir = context.getFilesDir();
//
//
//    }

    public OfflineManager(Context context) {
        Log.d(TAG, "in OfflineManager constructor");
        this.context = context;
    }

//    private static void downloadEventMap() {
//
//    }
//
//    private static void downloadEventGuestsNames() {
//
//    }
//
//    private static void downloadEventGuestsProfilePics() {
//        // iterate over invited users
//        // download each user's profile pic from FirebaseStorage to phone
//
////        gson.toJson()
//    }

//    public String getOfflineString(Context context) {
//        try {
//            return uploadDebug(context);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return "NULL";
//        }
//    }

//}
//
//    public String uploadDebug(Context context) throws FileNotFoundException {
//        FileInputStream fis = context.openFileInput(DEBUG_FILENAME);
//        InputStreamReader inputStreamReader =
//                new InputStreamReader(fis, StandardCharsets.UTF_8);
//        StringBuilder stringBuilder = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
//            String line = reader.readLine();
//            while (line != null) {
//                stringBuilder.append(line).append('\n');
//                line = reader.readLine();
//            }
//        } catch (IOException e) {
//            // Error occurred when opening raw file for reading.
//        } finally {
//            String contents = stringBuilder.toString();
//            return contents;
//        }
//    }

    private boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void downloadEventToOffline(EventModel eventModel) {
        Log.d(TAG, "in OfflineManager downloadEventToOffline");

        if (!isLoggedIn()) {
            Toast.makeText(context, "Error, you seem to have been logged out", Toast.LENGTH_SHORT).show();
        } else {

            currUserID = FirebaseAuth.getInstance().getUid();
            currUserBaseDir = context.getDir(currUserID, Context.MODE_PRIVATE);
            currUserEventDir = new File(currUserBaseDir, eventModel.getEventID());
            makeDir(currUserEventDir);
            this.eventModel = eventModel;

            downloadEventDetails();
            downloadEventMap();
            downloadInvitedUsers();
        }
    }

    private void makeDir(File dirToMake) {
        Log.d(TAG, "in OfflineManager makeDir");

        if (dirToMake.mkdir()) {
            Log.d(TAG, String.format("Directory %s has been created.",
                    dirToMake.getAbsolutePath()));

        } else if (dirToMake.isDirectory()) {
            Log.d(TAG, String.format("Directory %s has already been created.",
                    dirToMake.getAbsolutePath()));

        } else {
            Log.d(TAG, String.format("Directory %s could not be created.",
                    dirToMake.getAbsolutePath()));
        }
    }

    private void downloadEventDetails() {
        Log.d(TAG, "in OfflineManager downloadEventDetails");

        eventDetailsDir = new File(currUserEventDir, EVENT_DETAILS_DIR);
        makeDir(eventDetailsDir);
        eventDetailsFile = new File(eventDetailsDir, EVENT_DETAILS_FILE);

        Gson eventGson = new Gson();
        String eventJson = eventGson.toJson(eventModel, EventModel.class);
        try {
            FileOutputStream eventJsonWriter = new FileOutputStream(eventDetailsFile);
            eventJsonWriter.write(eventJson.getBytes());
            Log.d(TAG, "event details json written successfully");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadEventMap() {

    }

    private void downloadInvitedUsers() {

    }

//
//    private void downloadInvitedInEventUsers(File currUserDir, EventModel eventModel) {
//        String invitedInEventUsersDirName = "invited_in_event_users_dir";
//        File invitedInEventsUsersDir = context.getDir(invitedInEventUsersDirName, Context.MODE_PRIVATE);
//        FirebaseFirestore.getInstance().collection(context.getString(R.string.ff_Events)).document(eventModel.getEventID()).collection(context.getString(R.string.ff_InvitedInEventUsers)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
////                FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<ArrayList<InvitedInEventUserModel>>() {
//
////                    @Nullable
////                    @Override
////                    public ArrayList<InvitedInEventUserModel> apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
//                ArrayList<InvitedInEventUserModel> invitedInEventUserModels = new ArrayList<>();
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    Log.d(TAG, String.format("Adding query snapshot name: %s",
//                            documentSnapshot.get(context.getString(R.string.ff_InvitedInEventUsers_invitedInEventUserName))));
//                    String userId =
//                            documentSnapshot.getString(context.getString(R.string.ff_InvitedInEventUsers_invitedInEventUserID));
//
//                    if (TextUtils.equals(userId, getInstance().getUid())) {
//                        invitedInEventUserModels.add(0,
//                                documentSnapshot.toObject(InvitedInEventUserModel.class));
//                    } else {
//
//                        InvitedInEventUserModel invitedInEventUserModel =
//                                documentSnapshot.toObject(InvitedInEventUserModel.class);
//                        String userJson = gson.toJson(invitedInEventUserModel,
//                                InvitedInEventUserModel.class);
//                        String userFileName = invitedInEventUserModel.getInvitedInEventUserID();
//                        File userFile = new File(invitedInEventsUsersDir, userFileName);
//                        try {
//                            FileOutputStream outputStreamWriter = new FileOutputStream(userFile);
//                            outputStreamWriter.write(userJson.getBytes());
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
////                                invitedInEventUserModels.add(documentSnapshot.toObject(InvitedInEventUserModel.class));
//                    }
//
//
//                }
////                        Log.d(TAG, "index 0: " + invitedInEventUserModels.get(0)
////                                .getInvitedInEventUserName());
////                        return invitedInEventUserModels;
////                    }
////                }).addOnSuccessListener(new OnSuccessListener<ArrayList<InvitedInEventUserModel>>() {
////                    @Override
////                    public void onSuccess(ArrayList<InvitedInEventUserModel> invitedInEventUserModels) {
////                        Log.d(TAG, String.format("Size of array after transaction is %s and first " +
////                                        "user is %s", invitedInEventUserModels.size(),
////                                invitedInEventUserModels.size() == 0 ? "NO " +
////                                        "USER" : invitedInEventUserModels.get(0)));
////                        invitedUserInEventModelList = invitedInEventUserModels;
////                    }
////                }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        Log.d(TAG, String.format("Transaction failed with error: %s", e.getMessage()));
////                    }
////                });
//
//            }
//        });
//    }
//
////    public void downloadDebug() {
//////        File debugFile = new File(fileDir, DEBUG_FILENAME);
////        try (FileOutputStream fos = context.openFileOutput(DEBUG_FILENAME, Context.MODE_PRIVATE)) {
////            fos.write(debugString.getBytes());
////            Toast.makeText(context, "Successfully downloaded file", Toast.LENGTH_SHORT).show();
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
}
