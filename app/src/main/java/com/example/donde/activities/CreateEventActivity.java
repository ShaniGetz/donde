package com.example.donde.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.donde.BuildConfig;
import com.example.donde.R;
import com.example.donde.models.EventModel;
import com.example.donde.models.InvitedInEventUserModel;
import com.example.donde.models.InvitedInUserEventModel;
import com.example.donde.utils.map_utils.CustomMapTileProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final String TAG = "tagCreateEventActivity";
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    private EditText editTextEventName;
    private EditText editTextEventDescription;
    private EditText editTextEventDay;
    private EditText editTextEventMonth;
    private EditText editTextEventYear;
    private EditText editTextEventHour;
    private EditText editTextEventMinute;
    private EditText editTextInvitedUser1;
    private EditText editTextInvitedUser2;
    private EditText editTextInvitedUser3;
    private Button buttonCreateEvent;
    private Button buttonDebugAutofill;
    private ProgressBar progressBar;
    private SearchView searchViewLocationSearch;
    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersCollectionRef;
    private CollectionReference eventsCollectionRef;
    // Fields for storing data to push to FirebaseFirestore (ff)
    private String ffEventName;
    private String ffEventDescription;
    private String ffEventLocationName;
    private GeoPoint ffEventLocation;
    private String ffEventCreatorUID;
    private String ffEventCreatorName;
    private Date ffEventTimeCreated;
    private Date ffEventTimeStarting;
    private List<InvitedInEventUserModel> ffInvitedUserInEventModels;

    void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // This part I didn't implement,because for my case it isn't needed
                Log.i("TAG", "Unexpected flow");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CreateEventActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForPermissions();
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initializeFields() {

        // Views
        editTextEventName = findViewById(R.id.create_editText_event_name);
        editTextEventDescription = findViewById(R.id.create_editText_event_description);
        buttonCreateEvent = findViewById(R.id.create_button_create);
        buttonDebugAutofill = findViewById(R.id.create_button_debug_fill_default);
        progressBar = findViewById(R.id.create_progressBar);
        editTextEventDay = findViewById(R.id.create_editText_event_day);
        editTextEventMonth = findViewById(R.id.create_editText_event_month);
        editTextEventYear = findViewById(R.id.create_editText_event_year);
        editTextEventHour = findViewById(R.id.create_editText_event_hour);
        editTextEventMinute = findViewById(R.id.create_editText_event_minute);
        editTextInvitedUser1 = findViewById(R.id.create_editText_invited_user1);
        editTextInvitedUser2 = findViewById(R.id.create_editText_invited_user2);
        editTextInvitedUser3 = findViewById(R.id.create_editText_invited_user3);
        searchViewLocationSearch = findViewById(R.id.create_searchView_location_search);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        eventsCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Events));


        // Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.create_mapView);

        mapFrag.getMapAsync(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    //Place current location marker
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");

//                markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.shani_getz));

//                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shani_getz));

                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                    //move map camera
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                }
            }
        };


        setEventCreatorUID();
        setEventCreatorName();
    }

    private void initializeSearchQuery() {
        searchViewLocationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchViewLocationSearch.getQuery().toString();
                Toast.makeText(CreateEventActivity.this, "Search query is: " + location,
                        Toast.LENGTH_SHORT).show();
                List<Address> addressList = new ArrayList<>();
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(CreateEventActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LatLng latling;
                    if (addressList.size() == 0) {
                        Toast.makeText(CreateEventActivity.this, "Search query failed", Toast.LENGTH_SHORT).show();
                        latling = new LatLng(10, 10);
                    } else {

                        Address address = addressList.get(0);
                        latling = new LatLng(address.getLatitude(), address.getLongitude());
                    }
//                    TileOverlay offlineTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions()
//                            .tileProvider(new OfflineTileProvider()));
                    TileOverlay onlineTileOverlay =
                            mGoogleMap.addTileOverlay(new TileOverlayOptions()
                                    .tileProvider(new CustomMapTileProvider(getFilesDir().getAbsolutePath())));
                    mGoogleMap.addMarker(new MarkerOptions().position(latling).title(location));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latling, 15));
                    setEventLocation(latling.latitude, latling.longitude);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initializeListeners() {
        initializeSearchQuery();
        initializeDebugAutofill();
        initializeCreateEventListener();
    }

    private void initializeDebugAutofill() {
        buttonDebugAutofill.setOnClickListener(v -> {
            editTextEventName.setText("A Debug Event Name");
            editTextEventDescription.setText("A debug description for an event. This text is kind of long but" +
                    " also not too long.");
            searchViewLocationSearch.setQuery("Ein Bokek", true);
            editTextEventDay.setText("16");
            editTextEventMonth.setText("10");
            editTextEventYear.setText("2020");
            editTextEventHour.setText("20");
            editTextEventMinute.setText("30");
            editTextInvitedUser1.setText("shani@gmail.com");
            editTextInvitedUser2.setText("batsheva@gmail.com");
            editTextInvitedUser3.setText("alon@gmail.com");
        });
    }

    private void initializeCreateEventListener() {
        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    private void gotoEvents() {
        Intent eventsIntent = new Intent(CreateEventActivity.this, MainActivity.class);
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private EventModel createNewEventModel() {
        EventModel createdEvent = new EventModel();
        createdEvent.setEventName(ffEventName);
        createdEvent.setEventDescription(ffEventDescription);
        createdEvent.setEventLocationName(ffEventLocationName);
        createdEvent.setEventLocation(ffEventLocation);
        createdEvent.setEventTimeCreated(ffEventTimeCreated);
        createdEvent.setEventTimeStarting(ffEventTimeStarting);
        createdEvent.setEventCreatorUID(ffEventCreatorUID);
        createdEvent.setEventCreatorName(ffEventCreatorName);
        return createdEvent;
    }

    private void addInvitedInEventUser(CollectionReference invitedInEventUsersRef,
                                       InvitedInEventUserModel invitedInEventUserModel) {
        invitedInEventUsersRef.document(invitedInEventUserModel.getInvitedInEventUserID()).set(invitedInEventUserModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, String.format("Added user %s to event",
                        invitedInEventUserModel.getInvitedInEventUserName()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, String.format("Failed to add user %s to event, error: %s",
                        invitedInEventUserModel.getInvitedInEventUserName(), e.getMessage()));

            }
        });

    }


    private void addInvitedInUserEvent(String newEventId, EventModel newEventModel,
                                       CollectionReference invitedInUserEventsRef,
                                       InvitedInEventUserModel invitedInUserEventModel) {
        InvitedInUserEventModel newInvitedInUserEventModel =
                new InvitedInUserEventModel(newEventId, newEventModel.getEventName(),
                        newEventModel.getEventLocationName(), newEventModel.getEventCreatorName());
        invitedInUserEventsRef.document(newEventId).set(newInvitedInUserEventModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, String.format("Added event %s to user", newEventModel.getEventName()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, String.format("Failed to add event %s to user, error: %s",
                        newEventModel.getEventName(), e.getMessage()));

            }
        });
    }

    private void createEvent() {
        boolean didSetFields = retrieveAndSetEventFields();
        if (didSetFields) {
            progressBar.setVisibility(View.VISIBLE);

            EventModel newEventModel = createNewEventModel();

            eventsCollectionRef.add(newEventModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // add invited users to event
                    String newEventId = documentReference.getId();
                    CollectionReference invitedInEventUsersRef =
                            documentReference.collection(getString(R.string.ff_InvitedInEventUsers));
                    for (InvitedInEventUserModel invitedInEventUserModel : ffInvitedUserInEventModels) {
                        addInvitedInEventUser(invitedInEventUsersRef, invitedInEventUserModel);

                        // add event to invited users
                        String invitedInEventUserId = invitedInEventUserModel.getInvitedInEventUserID();
                        CollectionReference invitedInUserEventsRef =
                                usersCollectionRef.document(invitedInEventUserId).collection(getString(R.string.ff_InvitedInUserEvents));
                        addInvitedInUserEvent(newEventId, newEventModel, invitedInUserEventsRef,
                                invitedInEventUserModel);
                    }

                    Toast.makeText(CreateEventActivity.this, "Event created successfully",
                            Toast.LENGTH_SHORT).show();
                    gotoEvents();

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateEventActivity.this,
                            "Error while creating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            progressBar.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(this, "Event could not be created", Toast.LENGTH_SHORT).show();
        }
    }


    private void addEventToInvitedUser(String invitedUserId, String eventId) {

        usersCollectionRef.document(invitedUserId).update("userInvitedEventIDs",
                FieldValue.arrayUnion(eventId));
    }

    private boolean retrieveAndSetEventFields() {
        String toastErrorMessage = "";


        if (!setEventName(editTextEventName.getText().toString())) {
            toastErrorMessage = "Fix event name";
        } else if (!setEventCreatorName()) {
            toastErrorMessage = "Error getting creator name";

        } else if (!setInvitedUsers(new ArrayList<>(Arrays.asList(
                editTextInvitedUser1.getText().toString(),
                editTextInvitedUser2.getText().toString(),
                editTextInvitedUser3.getText().toString())))) {

            toastErrorMessage = "Error inviting guests";
        } else if (!setEventDescription(editTextEventDescription.getText().toString())) {
            toastErrorMessage = "Fix event description";
        } else if (!setEventLocationName(searchViewLocationSearch.getQuery().toString())) {
            toastErrorMessage = "Fix event location name";
            // ffEventLocation should be full since it is retrieved from the search query
        } else if (this.ffEventLocation == null) {
            toastErrorMessage = "Fix event location";
        } else if (!setEventCreatorUID()) {
            toastErrorMessage = "Error getting creator UID";
        } else if (!setEventTimeCreated()) {
            toastErrorMessage = "Error setting creation time";
            // TODO: Time starting not working (giving weird times)
        } else if (!setEventTimeStarting(
                Integer.parseInt(editTextEventDay.getText().toString()),
                Integer.parseInt(editTextEventMonth.getText().toString()),
                Integer.parseInt(editTextEventYear.getText().toString()),
                Integer.parseInt(editTextEventHour.getText().toString()),
                Integer.parseInt(editTextEventMinute.getText().toString()))) {
            toastErrorMessage = "Fix time starting";
        }
        if (!TextUtils.isEmpty(toastErrorMessage)) {
            Toast.makeText(this, toastErrorMessage, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    /*
 Return true iff location is valid
  */
    private boolean setEventLocation(double latitude, double longitude) {
        boolean validLatitude = (latitude >= -90) && (latitude <= 90);
        boolean validLongitude = (longitude >= -180) && (longitude <= 180);
        if (validLatitude && validLongitude) {
            this.ffEventLocation = new GeoPoint(latitude, longitude);
            return true;
        } else {
            return false;
        }
    }

    private boolean setEventName(String eventName) {
        boolean nameNotEmpty = !TextUtils.isEmpty(eventName);
        if (nameNotEmpty) {
            this.ffEventName = eventName;
            return true;
        } else {
            return false;
        }
    }

    private boolean setEventDescription(String eventDescription) {
        boolean descriptionNotEmpty = !TextUtils.isEmpty(eventDescription);
        if (descriptionNotEmpty) {
            this.ffEventDescription = eventDescription;
            return true;
        } else {
            return false;
        }
    }

    private boolean setEventLocationName(String eventLocationName) {
        boolean nameNotEmpty = !TextUtils.isEmpty(eventLocationName);
        if (nameNotEmpty) {
            this.ffEventLocationName = eventLocationName;
            return true;
        } else {
            return false;
        }
    }

    private boolean setEventCreatorUID() {
        this.ffEventCreatorUID = this.firebaseUser.getUid();
        return true;
    }

    private boolean setEventCreatorName() {
        String creatorUID = this.firebaseUser.getUid();
        Query creatorUserQuery =
                usersCollectionRef.whereEqualTo(getString(R.string.ff_Users_userID), creatorUID);
        this.progressBar.setVisibility(View.VISIBLE);
        creatorUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (BuildConfig.DEBUG && task.getResult().size() != 1) {
                        throw new AssertionError("Either no or more than one user with given UID " +
                                "exists.");
                    } else {
                        DocumentSnapshot creatorDocument = task.getResult().getDocuments().get(0);
                        ffEventCreatorName =
                                creatorDocument.getString(getString(R.string.ff_Users_userName));
                        Log.d("create", "event creator name is " + ffEventCreatorName);

                    }
                } else {
                    Toast.makeText(CreateEventActivity.this, String.format("Error getting creator" +
                                    " user name: %s", task.getException().getMessage()),
                            Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);

            }
        });


        return true;
    }

    private boolean setEventTimeCreated() {
        this.ffEventTimeCreated = Timestamp.now().toDate();
        return true;
    }

    private boolean setEventTimeStarting(int day, int month, int year, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        Date startDate = new Date(year, month, day, hour, minute);
        cal.setLenient(false);
        cal.setTime(startDate);
        try {
            cal.getTime();
            this.ffEventTimeStarting = startDate;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean setInvitedUsers(ArrayList<String> userEmails) {

        ffInvitedUserInEventModels = new ArrayList<>();
        CollectionReference usersRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        // add self to invitees
        userEmails.add(firebaseUser.getEmail());
        for (String userEmail : userEmails) {
            if (TextUtils.isEmpty(userEmail)) {
                continue;
            }
            Query userByEmailQuery = usersRef.whereEqualTo(getString(R.string.ff_Users_userEmail)
                    , userEmail);

            progressBar.setVisibility(View.VISIBLE);
            userByEmailQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 1) {
                            DocumentSnapshot invitedUserDoc = task.getResult().getDocuments().get(0);
                            String invitedUserID = invitedUserDoc.getId();
                            String invitedUserEmail =
                                    invitedUserDoc.getString(getString(R.string.ff_Users_userEmail));
                            String invitedUserName =
                                    invitedUserDoc.getString(getString(R.string.ff_Users_userName));
                            // TODO: retrieve
//                            String invitedUserProfilePicURL = invitedUserDoc.getString(
//                                    getString(R.string.ff_InvitedInEventUser_));
                            Log.d("CreateEvent", String.format("adding user to ffInvited: %s", invitedUserEmail));
                            ffInvitedUserInEventModels.add(new InvitedInEventUserModel(invitedUserID,
                                    invitedUserName, invitedUserEmail));

                        } else if (task.getResult().size() == 0) {
                            Toast.makeText(CreateEventActivity.this, String.format("No user found" +
                                            " with email %s", userEmail),
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.getResult().size() > 1) {
                            Toast.makeText(CreateEventActivity.this, String.format("Found more " +
                                            "than one user with email %s", userEmail),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(CreateEventActivity.this, String.format("Error processing " +
                                        "email %s, error: %s", userEmail, task.getException().getMessage()),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return true;


    }
}

// TODO: don't allow inviting same person twice (or self)