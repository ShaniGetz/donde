/*
last edited by alon on 06/10/20
 */

package com.example.donde.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.donde.ClusterMarker;
import com.example.donde.MyClusterManagerRenderer;
import com.example.donde.R;
import com.example.donde.utils.CustomMapTileProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class EventMapFragment extends Fragment implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;
    FusedLocationProviderClient mFusedLocationClient;
    //    LocationCallback mLocationCallback;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    LocationCallback mLocationCallback = new

            LocationCallback() {

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
                        //add marker pic
                        addMapMarkers();
                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                    }
                }
            };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_map, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mGoogleMap = googleMap;
        String tilesDir = getContext().getFilesDir().toString();

        mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(new CustomMapTileProvider(tilesDir)));


//        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(120000); // two minute interval
//        mLocationRequest.setFastestInterval(120000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
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

    public void ChangeType(View view) {
        if (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void addMapMarkers() {
        if (mGoogleMap != null) {
            if (mClusterManager == null) {
                mClusterManager =
                        new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(),
                                mGoogleMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getContext(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
//            for(UserLocation userLocation: mUserLocations){
//                Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString());
//                try{
//                    String snippet = "";
//                    if(userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())){
//                        snippet = "This is you";
//                    }
//                    else{
//                        snippet = "Determine route to " + userLocation.getUser().getUsername() + "?";
//                    }
//                    int avatar = R.drawable.avatar; // set the default avatar
//                    try{
//                        avatar = Integer.parseInt(userLocation.getUser().getAvatar());
//                    }catch (NumberFormatException e){
//                        Log.d(TAG, "addMapMarkers: no avatar for " + userLocation.getUser().getUsername() + ", setting default.");
//                    }
//                    ClusterMarker newClusterMarker = new ClusterMarker(
//                            new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
//                            userLocation.getUser().getUsername(),
//                            snippet,
//                            avatar,
//                            userLocation.getUser()
//                    );
//                    mClusterManager.addItem(newClusterMarker);
//                    mClusterMarkers.add(newClusterMarker);
//                }catch (NullPointerException e){
//                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
//                }
//            }
            int avatar = R.drawable.shani_getz;
            ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude()), "Shani Getz", "This is you", avatar);
            mClusterManager.addItem(newClusterMarker);
            mClusterMarkers.add(newClusterMarker);

            ClusterMarker alonClusterMarker = new ClusterMarker(new LatLng(32.0, 34.0),
                    "alon", "This is your friend", R.drawable.avatar);
            mClusterManager.addItem(alonClusterMarker);
            mClusterMarkers.add(alonClusterMarker);

            mClusterManager.cluster();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}