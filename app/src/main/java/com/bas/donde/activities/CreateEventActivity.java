package com.bas.donde.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bas.donde.R;
import com.bas.donde.models.EventModel;
import com.bas.donde.models.InvitedInEventUserModel;
import com.bas.donde.models.InvitedInUserEventModel;
import com.bas.donde.models.UserModel;
import com.bas.donde.utils.map_utils.CustomMapTileProvider;
import com.bas.donde.utils.map_utils.OfflineTileProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.firebase.firestore.WriteBatch;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    // Views
    private EditText editTextEventName;
    private EditText editTextEventDescription;

    private Button buttonCreateEvent;
    private ProgressBar progressBar;
    private SearchView searchViewLocationSearch;
    private ListView listViewInvitedUsers;
    private AutoCompleteTextView autoCompleteInvitedUsers;


    private TextView textViewEventTime;
    private TextView textViewEventDate;
    private TimePicker timePicker;
    private DatePicker datePicker;


    // Utils
    private ArrayAdapter<String> autoCompleteInvitedUsersAdapter;
    private ArrayList<String> autoCompleteInvitedUsersList;
    private ArrayAdapter<String> listViewInvitedUsersAdapter;
    private ArrayList<String> listViewInvitedUsersList;


    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DocumentReference currUserDocumentRef;
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

    private boolean didFinishSettingCreatorName;
    private boolean didFinishSettingUsers;
    private ArrayList<String> ffEventInvitedUserIDs;

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
                new AlertDialog.Builder(this, R.style.MyDialogTheme)
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
                    Log.d("createEvent", "permission denied");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initializeListViewInvitedUsers() {
        listViewInvitedUsers = findViewById(R.id.create_listView_invited_users);
        listViewInvitedUsersList = new ArrayList<>();
        listViewInvitedUsersAdapter = new ArrayAdapter<>(this,
                R.layout.listview_invited_users_single_item, listViewInvitedUsersList);
        listViewInvitedUsers.setAdapter(listViewInvitedUsersAdapter);
    }

    private void addInvitedUsersToAutocomplete() {
        ArrayList<String> invitedUsersAutocomplete;
        currUserDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> interactedUsersArray = (ArrayList<String>)
                        documentSnapshot.get(getString(R.string.ff_Users_userInteractedUserEmails));
                if (!(interactedUsersArray == null)) {
                    autoCompleteInvitedUsersList.addAll(interactedUsersArray);
                    autoCompleteInvitedUsersAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "adding interacted users to autocomplete failed");
            }
        });
    }

    private void initializeAutocompleteInvitedUsers() {
        autoCompleteInvitedUsers = findViewById(R.id.create_autoComplete_invited_users);
        autoCompleteInvitedUsersList = new ArrayList<String>();
        addInvitedUsersToAutocomplete();
        autoCompleteInvitedUsersAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_invited_user_single_item, autoCompleteInvitedUsersList);
        autoCompleteInvitedUsers.setAdapter(autoCompleteInvitedUsersAdapter);
        autoCompleteInvitedUsers.setThreshold(1);
        autoCompleteInvitedUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String invitedUser = parent.getItemAtPosition(position).toString();
                addInvitedUserToList(invitedUser);
                hideSoftKeyboard();


            }
        });
        autoCompleteInvitedUsers.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String invitedUser = v.getText().toString();
                    addInvitedUserToList(invitedUser);
                }
                return false;
            }
        });

    }

    private void hideSoftKeyboard() {
        InputMethodManager in =
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        in.hideSoftInputFromWindow(getWindow().getAttributes().token, 0);
    }

    /*
    Adds invited user from autocomplete text view to invited users list
     */
    private void addInvitedUserToList(String invitedUser) {
        if (!TextUtils.isEmpty(invitedUser)) {
            listViewInvitedUsersList.add(invitedUser);
            autoCompleteInvitedUsers.setText("");
            // Moving this to UI thread
            listViewInvitedUsersAdapter.notifyDataSetChanged();
        }
    }

    private void initializeFields() {

        // Views
        editTextEventName = findViewById(R.id.create_editText_event_name);
        editTextEventDescription = findViewById(R.id.create_editText_event_description);
        buttonCreateEvent = findViewById(R.id.create_button_create);
        progressBar = findViewById(R.id.create_progressBar);
        searchViewLocationSearch = findViewById(R.id.create_searchView_location_search);
        timePicker = findViewById(R.id.create_timePicker);
        datePicker = findViewById(R.id.create_datePicker);
        timePicker.setIs24HourView(true);


        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        eventsCollectionRef = firebaseFirestore.collection(getString(R.string.ff_Events));
        currUserDocumentRef = usersCollectionRef.document(firebaseUser.getUid());

        initializeTimeAndDate();
        initializeListViewInvitedUsers();
        initializeAutocompleteInvitedUsers();

        ffEventInvitedUserIDs = new ArrayList<>();


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

    private void initializeTimeAndDate() {
        Context createContext = this;


        initializeTimePicker(createContext);
        initializeDatePicker(createContext);

    }

    private void updateCurrentDate() {
        textViewEventDate = findViewById(R.id.create_textView_event_date);
        Date currDate = Calendar.getInstance().getTime();
        String day = (String) DateFormat.format("dd", currDate); // 20
        String monthNumber = (String) DateFormat.format("MM", currDate); // 06
        String year = (String) DateFormat.format("yyyy", currDate); // 2013
        textViewEventDate.setText(day + "/" + monthNumber + "/" + year);
    }


    private void initializeDatePicker(Context createContext) {
        updateCurrentDate();

        textViewEventDate.setOnClickListener(new View.OnClickListener() {
            //TODO: Hide soft keyboard when choosing items in craeteevent
            @Override
            public void onClick(View v) {
                String date = textViewEventDate.getText().toString();
                int day = -1;
                int month = -1;
                int year = -1;
                int idx = 0;
                for (int i = 0; i < date.length(); i++) {
                    if (date.charAt(i) == '/') {
                        if (day == -1) {
                            day = Integer.parseInt(date.substring(0, i));
                            idx = i;
                        } else if (month == -1) {
                            month = Integer.parseInt(date.substring(idx + 1, i));
                            year = Integer.parseInt(date.substring(i + 1));
                        }
                    }
                }
                textViewEventDate.setText(day + "/" + month + "/" + year);

                DatePickerDialog mDatePicker;
//                int resTheme = R.style.SpinnerTimePicker;

//                int resTheme = DatePickerDialog.THEME_HOLO_LIGHT;
                mDatePicker = new DatePickerDialog(createContext, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
//                                int correctMonth = selectedMonth + 1;
                                textViewEventDate.setText(selectedDayOfMonth + "/" + selectedMonth + "/" + selectedYear);
                            }
                        }, year, month, day);//Yes 24 hour time
                mDatePicker.setTitle("Select Date");
                mDatePicker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                mDatePicker.show();


            }

        });
    }

    private void updateTime() {
        textViewEventTime = findViewById(R.id.create_textView_event_time);
        Calendar cal = Calendar.getInstance();
        String time = cal.getTime().toString();
        String hour = "";
        String minute = "";
        for (int i = 0; i < time.length(); i++) {
            if (time.charAt(i) == ':') {
                hour = time.substring(i - 2, i);
                minute = time.substring(i + 1, i + 3);
                break;
            }
        }
        textViewEventTime.setText(hour + ":" + minute);
    }

    private void initializeTimePicker(Context createContext) {
        updateTime();
        textViewEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = -1;
                int minute = -1;
                String time = textViewEventTime.getText().toString();
                for (int i = 0; i < time.length(); i++) {
                    if (time.charAt(i) == ':') {
                        hour = Integer.parseInt(time.substring(i - 2, i));
                        minute = Integer.parseInt(time.substring(i + 1, i + 3));
                        break;
                    }
                }
                TimePickerDialog mTimePicker;
//                int resTheme = TimePickerDialog.THEME_HOLO_LIGHT;
                mTimePicker = new TimePickerDialog(createContext, R.style.TimePickerTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String viewMinute = String.format("%s%s", selectedMinute < 10 ? "0" : "",
                                        selectedMinute);

                                textViewEventTime.setText(selectedHour + ":" + viewMinute);
                            }
                        }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                mTimePicker.show();

            }

        });
    }

    private void initializeSearchQuery() {
        searchViewLocationSearch.setIconified(true);
        searchViewLocationSearch.setQueryHint("Search for event location");
        searchViewLocationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchViewLocationSearch.getQuery() == "") {
                    searchViewLocationSearch.onActionViewExpanded();
                } else {
                    searchQuerry(searchViewLocationSearch.getQuery().toString());
                }
            }
        });
        searchViewLocationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchViewLocationSearch.getQuery().toString();
                searchQuerry(location);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    void searchQuerry(String location) {
        Log.d("CreateEventActivity", "Search query is: " + location);
        List<Address> addressList = new ArrayList<>();
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(CreateEventActivity.this);
            LatLng latling = new LatLng(0, 0);

            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList.size() == 0) {
                Log.d("CreateEventActivity", "Search query failed");
            } else {

                Address address = addressList.get(0);
                latling = new LatLng(address.getLatitude(), address.getLongitude());
                Log.d("latLng ", latling.latitude + " " + latling.longitude);
            }
            // if we add these line it actually finds the location but not large enough
            //what does v do? crazy zoom when gets bigger
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latling, 17));
            setEventLocation(latling.latitude, latling.longitude);
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            TileOverlay onlineTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(new OfflineTileProvider(getBaseContext())));

            // add marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latling);
            markerOptions.title("Event Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        }
    }

    private void setUpMap(GoogleMap mMap, Double LAT, Double LON, float ZOOM) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new
                CustomMapTileProvider(getFilesDir().getAbsolutePath())));
        CameraUpdate upd = CameraUpdateFactory.newLatLngZoom(new LatLng(LAT, LON), ZOOM);
        mMap.moveCamera(upd);
    }

    private void initializeListeners() {
        initializeSearchQuery();
        initializeCreateEventListener();
    }

    private void initializeCreateEventListener() {
        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();

                boolean didSetFields = retrieveAndSetEventFields();
                if (didSetFields) {
                    // TODO: should solve not with thread sleep but rather with structuring the asynchronus calls

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            {
                                createEvent();
//                        loadingDialog.dismiss();//dismiss the dialog box once data is retreived
//                        tvUserName.setText(u_name);
                            }
                        }
                    }, 2000);
                } else {
                }// 2000 milliseconds = 2seconds

            }
        });
    }

    private void gotoEvents() {
        Intent eventsIntent = new Intent(CreateEventActivity.this, MainActivity.class);
        startActivity(eventsIntent);
        // don't allow going back to creating event
        hideProgressBar();
        finish();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOnline()) {

            setContentView(R.layout.activity_create_event);

            initializeFields();
            initializeListeners();
        } else {

            Log.d("CreateEventActivity", "Go online in order to create an event");
            gotoEvents();
        }
    }

    // TODO: Doesn't work on older devices. Need to implement advanced solution from here: https://stackoverflow.com/a/27312494/10524650
    // ICMP
    public boolean isOnline() {
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return false;
        return true;
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
                                       InvitedInEventUserModel invitedInEventUserModel,
                                       WriteBatch writeBatch) {
        String invitedUserID = invitedInEventUserModel.getInvitedInEventUserID();
        writeBatch.set(invitedInEventUsersRef.document(invitedUserID), invitedInEventUserModel);

    }


    private void addInvitedInUserEvent(String newEventId, EventModel newEventModel,
                                       CollectionReference invitedInUserEventsRef, WriteBatch writeBatch) {
        InvitedInUserEventModel newInvitedInUserEventModel =
                new InvitedInUserEventModel(newEventId, newEventModel.getEventName(),
                        newEventModel.getEventLocationName(), newEventModel.getEventCreatorName()
                        , newEventModel.getEventTimeStarting());
        writeBatch.set(invitedInUserEventsRef.document(newEventId), newInvitedInUserEventModel);
    }

    private void showProgressBar() {
        buttonCreateEvent.setText("");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        buttonCreateEvent.setText("Create Event");

    }

    private void createEvent() {


        EventModel newEventModel = createNewEventModel();

        eventsCollectionRef.add(newEventModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // add invited users to event
                String newEventId = documentReference.getId();
                CollectionReference invitedInEventUsersRef =
                        documentReference.collection(getString(R.string.ff_InvitedInEventUsers));
                WriteBatch eventCreateBatch = firebaseFirestore.batch();
                for (InvitedInEventUserModel invitedInEventUserModel : ffInvitedUserInEventModels) {


                    Log.d(TAG, "Entering addInvitedInEventUser");
                    addInvitedInEventUser(invitedInEventUsersRef, invitedInEventUserModel, eventCreateBatch);


                    String invitedInEventUserId = invitedInEventUserModel.getInvitedInEventUserID();
                    CollectionReference invitedInUserEventsRef =
                            usersCollectionRef.document(invitedInEventUserId).collection(getString(R.string.ff_InvitedInUserEvents));

                    Log.d(TAG, "Entering addInvitedInUserEvent");
                    addInteractedEmailsToUser(usersCollectionRef.document(invitedInEventUserId),
                            eventCreateBatch);
                    addInvitedInUserEvent(newEventId, newEventModel, invitedInUserEventsRef, eventCreateBatch);

                    Log.d(TAG, "Adding userID to array of IDs");
                    ffEventInvitedUserIDs.add(invitedInEventUserId);

                }

                eventCreateBatch.update(documentReference, App.getRes().getString(R.string.ff_Events_eventInvitedUserIDs), ffEventInvitedUserIDs);
                eventCreateBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "event created succesfully");
                        gotoEvents();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "event failed to create");
                        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                gotoEvents();
                            }
                        }); // TODO: Handle failure
                    }
                });


            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,
                        "Error while creating event: " + e.getMessage());
                gotoEvents();
            }
        });


    }

    private void addInteractedEmailsToUser(DocumentReference userRef, WriteBatch writeBatch) {

        writeBatch.update(userRef, getString(R.string.ff_Users_userInteractedUserEmails),
                FieldValue.arrayUnion((Object[]) listViewInvitedUsersList.toArray(new String[listViewInvitedUsersList.size()])));
    }


    private void addEventToInvitedUser(String invitedUserId, String eventId) {

        usersCollectionRef.document(invitedUserId).update("userInvitedEventIDs",
                FieldValue.arrayUnion(eventId));
    }

    private boolean retrieveAndSetEventFields() {
        String toastErrorMessage = "";


        if (!setEventName(editTextEventName.getText().toString())) {
            toastErrorMessage = "Fix event name";
//        } else if (!setEventCreatorName()) {
//            toastErrorMessage = "Error getting creator name";

        } else if (!setInvitedUsers(listViewInvitedUsersList)) {

            toastErrorMessage = "Error inviting guests";
        } else if (!setEventDescription(editTextEventDescription.getText().toString())) {
            toastErrorMessage = "Fix event description";
        } else if (!setEventLocationName(searchViewLocationSearch.getQuery().toString())) {
            toastErrorMessage = "Fix event location name";
            // ffEventLocation should be full since it is retrieved from the search query
        } else if (this.ffEventLocation == null) {
            toastErrorMessage = "Fix event location";
//        } else if (!setEventCreatorUID()) {
//            toastErrorMessage = "Error getting creator UID";
        } else if (!setEventTimeCreated()) {
            toastErrorMessage = "Error setting creation time";
            // TODO: Time starting not working (giving weird times)
            // TODO: Fix time to be taken from time picker dialog
        } else if (!setEventTimeStarting()) {
            toastErrorMessage = "Fix time starting";
        }
        if (!TextUtils.isEmpty(toastErrorMessage)) {
            Log.d("CreateEventActivity", toastErrorMessage);
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
        currUserDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ffEventCreatorName =
                        documentSnapshot.getString(getString(R.string.ff_Users_userName));
                Log.d("create", "event creator name is " + ffEventCreatorName);
                didFinishSettingCreatorName = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("CreateEventActivity", String.format("Error getting creator" +
                        " user name: %s", e.getMessage()));
            }


        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            }
        });
        return true;
    }

    private boolean setEventTimeCreated() {
        this.ffEventTimeCreated = Timestamp.now().toDate();
        return true;
    }

    private boolean setEventTimeStarting() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm", Locale.ENGLISH);
        Date date;
        try {
            date = formatter.parse(String.format("%s,%s", textViewEventDate.getText(),
                    textViewEventTime.getText()));
            this.ffEventTimeStarting = date;
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean setInvitedUsers(ArrayList<String> userEmails) {
        ffInvitedUserInEventModels = new ArrayList<>();
        CollectionReference usersRef = firebaseFirestore.collection(getString(R.string.ff_Users));
        // add self to invitees
        ArrayList<String> newListWithUser = new ArrayList<>();
        newListWithUser.add(firebaseUser.getEmail());
        newListWithUser.addAll(userEmails);
        for (String userEmail : newListWithUser) {
            if (TextUtils.isEmpty(userEmail)) {
                continue;
            }
            Query userByEmailQuery = usersRef.whereEqualTo(getString(R.string.ff_Users_userEmail)
                    , userEmail);

            userByEmailQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 1) {
                            DocumentSnapshot invitedUserDoc = task.getResult().getDocuments().get(0);
                            UserModel userModel = invitedUserDoc.toObject(UserModel.class);
                            Log.d(TAG, String.format("adding user to ffInvited: %s",
                                    userModel.getUserName()));
                            ffInvitedUserInEventModels.add(new InvitedInEventUserModel(userModel.getUserID(),
                                    userModel.getUserName(), userModel.getUserEmail(), userModel.getUserStatus(), userModel.getUserLastLocation(), userModel.getUserProfilePicURL()));

                        } else if (task.getResult().size() == 0) {
                            Toast.makeText(CreateEventActivity.this, String.format("No user " +
                                    "found" + " with " + "email %s", userEmail), Toast.LENGTH_SHORT);
                        } else if (task.getResult().size() > 1) {
                            Toast.makeText(CreateEventActivity.this,
                                    String.format("Found more " + "than one user with email %s",
                                            userEmail), Toast.LENGTH_SHORT);
                        }

                    } else {
                        Log.d("CreateEventActivity", String.format("Error processing " +
                                "email %s, error: %s", userEmail, task.getException().getMessage()));
                    }
                    didFinishSettingUsers = true;
                }
            });
        }
        return true;


    }
}

// TODO: don't allow inviting same person twice (or self)