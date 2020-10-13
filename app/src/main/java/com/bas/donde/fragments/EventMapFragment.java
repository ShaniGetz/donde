/*
BATSHEVAAA
 */

package com.bas.donde.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bas.donde.R;
import com.bas.donde.activities.EventActivity;
import com.bas.donde.models.InvitedInEventUserModel;
import com.bas.donde.utils.OfflineDataTransfer;
import com.bas.donde.utils.map_utils.MyClusterItem;
import com.bas.donde.utils.map_utils.MyClusterManagerRenderer;
import com.bas.donde.utils.map_utils.OfflineTileProvider;
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
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bas.donde.utils.CodeHelpers.myAssert;

public class EventMapFragment extends Fragment implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int currentUserIndex = 0;
    public static final int locationUpdateInterval = 20;
    private static final String TAG = "tagEventMapFragment";
    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;
    FusedLocationProviderClient mFusedLocationClient;
    GeoPoint mLastLocation;
    //    Marker mCurrLocationMarker;
    ArrayList<InvitedInEventUserModel> invitedUsersList;
    private String myUserId;
    private String status = "";
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private OfflineDataTransfer offlineDataTransfer;


    private ArrayList<MyClusterItem> mMyClusterItems;
    private HashMap<String, Bitmap> mUsersBitmaps;

    private LocationCallback mLocationCallback;
    private AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "in onCreateView");

        return inflater.inflate(R.layout.fragment_event_map, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "in onViewCreated");
        initializeFields();

    }

    private void setOnLocationCallback() {
        Log.d(TAG, "in setOnLocationCallback");
        // TODO: This wasn't a field but rather a local variable

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() <= 0) {
                    return;
                }
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                offlineDataTransfer.updateLocation(mLastLocation);
                LatLng localLatling = new LatLng(location.getLatitude(), location.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localLatling,
                        17));
                offlineDataTransfer.connect();

                updateUsersData();
            }
        };
    }

    private void updateUsersData() {
        Log.d(TAG, "in updateUsersData");

        assert invitedUsersList != null;

        for (int i = 0; i < invitedUsersList.size(); i++) {
            InvitedInEventUserModel user = invitedUsersList.get(i);
            LatLng updatedLocation = new LatLng(user.getInvitedInEventUserCurrentLocation().getLatitude(), user.getInvitedInEventUserCurrentLocation().getLongitude());
            mMyClusterItems.get(i).setPosition(updatedLocation);
            mClusterManagerRenderer.setUpdateMarker(mMyClusterItems.get(i));

            // update user in users list
            String id = user.getInvitedInEventUserID();
            String status = offlineDataTransfer.getOtherStatus(id);
            GeoPoint location = offlineDataTransfer.getOtherLocation(id);
            user.setInvitedInEventUserStatus(status);
            user.setInvitedInEventUserCurrentLocation(location);

        }
        // update cluster
        mClusterManager.cluster();

    }


    private void initializeFields() {
        Log.d(TAG, "in initializeFields");
        // TODO: What should be the order of these?
        myUserId = EventActivity.getMyUserId();
        offlineDataTransfer = ((EventActivity) getActivity()).getOfflineDataTransfer();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mLastLocation = (((EventActivity) getActivity()).getEvent().getEventLocation());
        myAssert(mLastLocation != null, "mLastLocation is null");
        initStatusDialogBox();
        initializeMapFragment();
        initializeInvitedUsersList();
        initializeInvitedUserBitmaps();
//        initializeUsersBitmaps();
//        initializeUsersData();
        setOnLocationCallback();


    }

//    private void initializeUsersBitmaps() {
//        Log.d(TAG, "in initializeUsersBitmaps");
//        mUsersBitmaps = new HashMap<>();
//        for (InvitedInEventUserModel user : invitedUsersList) {
//            Bitmap userBitmap = getUserAvatar(user);
//            mUsersBitmaps.put(user.getInvitedInEventUserID(), userBitmap);
//        }
//
//        Gson gson = new Gson();
//        gson.toJson(mUsersBitmaps);
//    }

    private void initializeInvitedUserBitmaps() {
        Log.d(TAG, "in initializeInvitedUserBitmaps");
        mUsersBitmaps = ((EventActivity) getActivity()).getInvitedUserInEventBitmaps();
        myAssert(mUsersBitmaps != null, "invitedUsersList is null");
        myAssert(mUsersBitmaps.size() > 0, "invitedUsersList size is 0");

    }

    private void initializeInvitedUsersList() {
        Log.d(TAG, "in initializeInvitedUsersList");
        invitedUsersList = ((EventActivity) getActivity()).getInvitedUserInEventModelList();
        myAssert(invitedUsersList != null, "invitedUsersList is null");
        myAssert(invitedUsersList.size() > 0, "invitedUsersList size is 0");

    }

    private void initializeMapFragment() {
        Log.d(TAG, "in initializeMapFragment");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "in onPause");
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
        Log.d(TAG, "in onMapReady");
        initializeGoogleMap(googleMap);
        initializeLocationRequest();
        initializeClusterFields();
        setClusterMarkerOnClick();
        initializeUsersData();

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

    }

    private void initializeLocationRequest() {
        Log.d(TAG, "in initializeLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(locationUpdateInterval);
        mLocationRequest.setFastestInterval(locationUpdateInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void initializeGoogleMap(GoogleMap googleMap) {
        Log.d(TAG, "in initializeGoogleMap");
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(new OfflineTileProvider(getContext())));

    }


    private void initializeUsersData() {
        Log.d(TAG, "in initializeUsersData");
        myAssert(invitedUsersList != null, "invitedUserList is null");
        for (int i = 0; i < invitedUsersList.size(); i++) {
            InvitedInEventUserModel user = invitedUsersList.get(i);
            initializeMapMarker(user);
            initializeMarkerStatus(user);

        }
        mClusterManager.cluster();

    }

    private void initializeClusterFields() {
        Log.d(TAG, "in initializeClusterFields");
        myAssert(mGoogleMap != null, "mGoogleMap is null");
        mClusterManager = new ClusterManager<MyClusterItem>(getContext(), mGoogleMap);
        mClusterManagerRenderer = new MyClusterManagerRenderer(getContext(), mGoogleMap, mClusterManager);
        mMyClusterItems = new ArrayList<>();
        mClusterManager.setRenderer(mClusterManagerRenderer);

    }

    private void initializeMapMarker(InvitedInEventUserModel user) {
        Log.d(TAG, "in initializeMapMarker for " + user.getInvitedInEventUserName());
        // TODO: Bottleneck #2
        // TODO: Make all these changed irrelevant by requiring them in EventActivity
        myAssert(mGoogleMap != null, "googleMap is null");
        myAssert(mClusterManager != null, "clusterManager is null");
        myAssert(mClusterManagerRenderer != null, "clusterManagerRenderer is null");
        myAssert(mMyClusterItems != null, "clusterMarkers is null");
        myAssert(invitedUsersList != null, "invitedUserList is null");


        // initialize user cluster marker
        Bitmap userBitmap = mUsersBitmaps.get(user.getInvitedInEventUserID());
        myAssert(userBitmap != null, "user bitmap is null for " + user.getInvitedInEventUserName());
        MyClusterItem userMyClusterItem = new MyClusterItem(
                user.getInvitedInEventUserID(), new LatLng(user.getInvitedInEventUserCurrentLocation().getLatitude(),
                user.getInvitedInEventUserCurrentLocation().getLongitude()),
                user.getInvitedInEventUserName(),
                user.getInvitedInEventUserStatus(),
                userBitmap);
        mMyClusterItems.add(userMyClusterItem);
        mClusterManager.addItem(userMyClusterItem);

        // TODO: Should this be in user loop? or outside?
    }


    private void setClusterMarkerOnClick() {
        Log.d(TAG, "in setClusterMarkerOnClick");
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<MyClusterItem>() {
                    @Override
                    public boolean onClusterItemClick(MyClusterItem myClusterItem) {
                        Log.d(TAG, "Clicked on marker for " + myClusterItem.getSnippet());
                        if (!myClusterItem.getUserID().equals(myUserId)) {
                            return false;
                        }
                        // else: clicked on my user marker
                        showStatusDialog();
                        Log.d(TAG, "after showStatusDialog line, status is " + status);
                        //update my stats also in list

//                        clusterItem.setSnippet(status);
//                        invitedUsersList.get(currentUserIndex).setInvitedInEventUserStatus(status);
//                        mClusterMarkers.get(currentUserIndex).setSnippet(status);
//                        mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(currentUserIndex));
//                        offlineDataTransfer.updateStatus(status);

                        // if true, click handling stops here and do not show info view, do not move camera
                        // you can avoid this by calling:
                        return false;
                    }
                });
    }

    private void initializeMarkerStatus(InvitedInEventUserModel user) {
        Log.d(TAG, "in initializeMarkerStatus with " + user.getInvitedInEventUserStatus());
        if (user.getInvitedInEventUserID().equals(myUserId)) {
            user.setInvitedInEventUserCurrentLocation(mLastLocation);
            user.setInvitedInEventUserStatus("Click to post your status");
            offlineDataTransfer.updateStatus("Click to post your status");
        } else {
            user.setInvitedInEventUserStatus("");
            offlineDataTransfer.updateStatus("");
        }
    }

    private void initStatusDialogBox() {
        View view = getLayoutInflater().inflate(R.layout.layout_status_dialog, null);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Status")
                .setIcon(R.drawable.ic_baseline_mode_comment_24)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        status = ((EditText) view.findViewById(R.id.edit_status)).getText().toString();
                        invitedUsersList.get(currentUserIndex).setInvitedInEventUserStatus(status);
                        mMyClusterItems.get(currentUserIndex).setSnippet(status);
                        mClusterManager.updateItem(mMyClusterItems.get(currentUserIndex));
                        mClusterManagerRenderer.setUpdateMarker(mMyClusterItems.get(currentUserIndex));
//                        mMyClusterItems.get(currentUserIndex).
                        //                        mMyClusterItems.get(
                        offlineDataTransfer.updateStatus(status);
                        invitedUsersList.get(0).setInvitedInEventUserStatus(status);
                        Log.d(TAG, "clicked and set status to " + status);
                        mClusterManager.cluster();


//                                                        listener.applyText(status);
                    }
                })
                .create();
    }

    private void showStatusDialog() {

        alertDialog.show();
    }


    private void checkLocationPermission() {
        Log.d(TAG, "in checkLocationPermission");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                showLocationPermissionRequestDialog();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void showLocationPermissionRequestDialog() {
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
                    Log.d("Event MAp Fragment", "permission denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
