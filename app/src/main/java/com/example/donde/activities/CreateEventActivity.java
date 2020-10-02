package com.example.donde.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.donde.R;
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
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    SearchView searchView;
    LocationCallback mLocationCallback = new LocationCallback() {
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
    private EditText editTextEventName;
    private EditText editTextEventDescription;
    private EditText editTextLocationName;
    private EditText editTextLongitude;
    private EditText editTextLatitude;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private SearchView searchViewLocationSearch;

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
        editTextEventName = findViewById(R.id.create_editText_event_name);
        editTextEventDescription = findViewById(R.id.create_editText_event_description);
        editTextLocationName = findViewById(R.id.create_editText_location_name);
//        editTextLongitude = findViewById(R.id.create_editText_longitude);
//        editTextLatitude = findViewById(R.id.create_editText_latitude);
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.create_mapView);

        mapFrag.getMapAsync(this);
    }

    private Timestamp getEventStartTime() {
        int sEventDay = Integer.parseInt(editTextEventDay.getText().toString());
        int sEventMonth = Integer.parseInt(editTextEventMonth.getText().toString());
        int sEventYear = Integer.parseInt(editTextEventYear.getText().toString());
        int sEventHour = Integer.parseInt(editTextEventHour.getText().toString());
        int sEventMinute = Integer.parseInt(editTextEventMinute.getText().toString());
        Timestamp startTime = new Timestamp(new Date(sEventYear, sEventMonth, sEventDay,
                sEventHour, sEventMinute));
        return startTime;
    }

    private List<String> getEventInvitedUserIDs() {
        String invitedEmail0 = firebaseAuth.getCurrentUser().getUid();
        String invitedEmail1 = editTextInvitedUser1.getText().toString();
        String invitedEmail2 = editTextInvitedUser2.getText().toString();
        String invitedEmail3 = editTextInvitedUser3.getText().toString();

//        FirebaseFirestore.getInstance().collection(getString(R.string.ff_users_collection)).get().addOnCompleteListener();
        List<String> invitedUserEmails = Arrays.asList(invitedEmail0, invitedEmail1,
                invitedEmail2, invitedEmail3);

        List<String> invitedUserIDs = new ArrayList<>();
        return invitedUserIDs;
    }


    private void initializeSearchQuery() {
//        GoogleMap mGoogleMap =
        searchViewLocationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchViewLocationSearch.getQuery().toString();
                Toast.makeText(CreateEventActivity.this, "Search query is: " + location,
                        Toast.LENGTH_SHORT).show();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(CreateEventActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latling = new LatLng(address.getLatitude(), address.getLongitude());
//                    TileOverlay offlineTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions()
//                            .tileProvider(new OfflineTileProvider()));
                    mGoogleMap.addMarker(new MarkerOptions().position(latling).title(location));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latling, 15));
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


        buttonDebugAutofill.setOnClickListener(v -> {
            editTextEventName.setText("A Debug Event Name");
            editTextEventDescription.setText("A debug description for an event. This text is kind of long but" +
                    " also not too long.");
            editTextLocationName.setText("Mt. Debug");
            editTextLongitude.setText("31.780189");
            editTextLatitude.setText("35.207668");
            editTextEventDay.setText("16");
            editTextEventMonth.setText("10");
            editTextEventYear.setText("2020");
            editTextEventHour.setText("20");
            editTextEventMinute.setText("30");
            editTextInvitedUser1.setText("alon@gmail.com");
            editTextInvitedUser2.setText("shani@gmail.com");
            editTextInvitedUser3.setText("batshi@gmail.com");
        });

        buttonCreateEvent.setOnClickListener(v -> {


            String sEventName = editTextEventName.getText().toString();
            String sEventDescription = editTextEventDescription.getText().toString();
            String sEventLocationName = editTextLocationName.getText().toString();

            double sLongitude = Double.parseDouble(editTextLongitude.getText().toString());
            double sLatitude = Double.parseDouble(editTextLatitude.getText().toString());
            GeoPoint eventLocation = new GeoPoint(sLatitude, sLongitude);


            Timestamp eventStartTime = getEventStartTime();

            String currentUserID = firebaseAuth.getCurrentUser().getUid();

            List<String> sInvitedUserIDs = Arrays.asList(currentUserID);
            Timestamp stamp = Timestamp.now();
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
                newEventMap.put(getString(R.string.ff_event_start_time), eventStartTime);
                newEventMap.put(getString(R.string.ff_event_invited_users), sInvitedUserIDs);

                firebaseFirestore.collection(getString(R.string.ff_events_collection)).add(newEventMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(CreateEventActivity.this, "Event created " +
                                    "successfully", Toast.LENGTH_LONG).show();
                            gotoEvents();

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

        });
    }

    private String getEventName() throws Exception {
        String sEditTextEventName = editTextEventName.getText().toString();
        if (TextUtils.isEmpty(sEditTextEventName)) {
            throw new Exception("event cannot be empty");
        } else {
            return sEditTextEventName;
        }

    }

//    private Map<String, Object> getCreatedEventMap() {
//
//        // get event name, description
//        String sEventName = editTextEventName.getText().toString();
//        String sEventDescription = editTextEventDescription.getText().toString();
//
//        // get event location details
//        String sEventLocationName = editTextLocationName.getText().toString();
//        double sLongitude = Double.parseDouble(editTextLongitude.getText().toString());
//        double sLatitude = Double.parseDouble(editTextLatitude.getText().toString());
//        GeoPoint eventLocation = new GeoPoint(sLatitude, sLongitude);
//
//        // get event starting time
//        int sEventDay = Integer.parseInt(editTextEventDay.getText().toString());
//        int sEventMonth = Integer.parseInt(editTextEventMonth.getText().toString());
//        int sEventYear = Integer.parseInt(editTextEventYear.getText().toString());
//        Timestamp startDate = new Timestamp(new Date(sEventYear, sEventMonth, sEventDay));
//
//        // add creator UID
//        String sCreatorUID = firebaseAuth.getCurrentUser().getUid();
//
//        // add invited users UIDs
//        List<String> sInvitedUIDs = new ArrayList<>();
//        sInvitedUIDs.add
//        addInvitedUsersToList(sInvitedUserIDs);
//
//        Timestamp timeCreated = Timestamp.now();
//
//
//        Map<String, Object> createdEventMap = new HashMap<>();
//        createdEventMap.put(getString(R.string.ff_event_name), sEventName);
//        createdEventMap.put(getString(R.string.ff_event_description), sEventDescription);
//        createdEventMap.put(getString(R.string.ff_event_creator_uid), sCreatorUID);
//        createdEventMap.put(getString(R.string.ff_event_time_created), timeCreated);
//        createdEventMap.put(getString(R.string.ff_event_location_name), sEventLocationName);
//        createdEventMap.put(getString(R.string.ff_event_location), eventLocation);
//        createdEventMap.put(getString(R.string.ff_event_start_time), startDate);
//        createdEventMap.put(getString(R.string.ff_event_invited_users), sInvitedUserIDs);
//        return createdEventMap;
//    }


    /*
    returns list of invited users IDs
     */
//    private ArrayList<String> getInvitedUsers() {
//        String sInvitedUser0 = currentUserID;
//        String sInvitedUser1 = editTextInvitedUser1.getText().toString());
//        String sInvitedUser2 = editTextInvitedUser2.getText().toString());

//    }

    private void gotoEvents() {
        Intent eventsIntent = new Intent(CreateEventActivity.this, MainActivity.class);
        startActivity(eventsIntent);
        // don't allow going back to creating event
        finish();

    }

//    private String getEventLocationName() {
//
//    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Called onmapready", Toast.LENGTH_SHORT).show();

        Log.d("OnMapReady", "called onMapReady");
        mGoogleMap = googleMap;
//        mGoogleMap =(GoogleMap) "dis.jfif";
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

    private class OfflineTileProvider implements TileProvider {
        private static final int TILE_WIDTH = 256;
        private static final int TILE_HEIGHT = 256;
        private static final int BUFFER_SIZE_FILE = 16384;
        private static final int BUFFER_SIZE_NETWORK = 8192;
        private final String TILES_DIR = getApplicationContext().getFilesDir().toString();
        private ConnectivityManager connectivityManager;

        @Override
        public Tile getTile(int x, int y, int z) {

            Log.d("TAG", "OfflineTileProvider.getTile(" + x + ", " + y + ", " + z + ")");
            try {
                byte[] data;
                //
                File file = new File(TILES_DIR, x + "_" + y + ".png");
                Log.d("TAG", TILES_DIR);
                if (file.exists()) {
                    data = readTile(new FileInputStream(file), BUFFER_SIZE_FILE);
                } else {
                    if (connectivityManager == null) {
                        connectivityManager = (ConnectivityManager) getSystemService(
                                Context.CONNECTIVITY_SERVICE);
                    }
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                        Log.w("TAG", "No network");
                        return NO_TILE;
                    }

                    Log.d("TAG", "Downloading tile");
                    data = readTile(new URL("https://a.tile.openstreetmap.org/" +
                                    z + "/" + x + "/" + y + ".png").openStream(),
                            BUFFER_SIZE_NETWORK);

                    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                        out.write(data);
                    }
                }
                return new Tile(TILE_WIDTH, TILE_HEIGHT, data);
            } catch (Exception ex) {
                Log.e("TAG", "Error loading tile", ex);
                return NO_TILE;
            }
        }

        private byte[] readTile(InputStream in, int bufferSize) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                int i;
                byte[] data = new byte[bufferSize];

                while ((i = in.read(data, 0, bufferSize)) != -1) {
                    buffer.write(data, 0, i);
                }
                buffer.flush();

                return buffer.toByteArray();
            } finally {
                in.close();
                buffer.close();
            }
        }
    }
}
