/*
BATSHEVAAA
 */

package com.example.donde.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.donde.R;
import com.example.donde.activities.EventActivity;
import com.example.donde.models.InvitedInEventUserModel;
import com.example.donde.utils.OfflineDataTransfer;
import com.example.donde.utils.map_utils.ClusterMarker;
import com.example.donde.utils.map_utils.MyClusterManagerRenderer;
import com.example.donde.utils.map_utils.OfflineTileProvider;
import com.example.donde.utils.map_utils.StatusDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class EventMapFragment extends Fragment implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "tagEventMapFragment";
    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;
    FusedLocationProviderClient mFusedLocationClient;
    //    LocationCallback mLocationCallback;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<InvitedInEventUserModel> invitedUsersList;
    private String myUserId;
    private String status;
    private FragmentActivity myContext;
    private ClusterManager mClusterManager;
    private GeoPoint geoPoint;
    private LatLng laLing;
    Button updateButton;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private OfflineDataTransfer offlineDataTransfer;


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
                        //to update the location we have
                        offlineDataTransfer.updateLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker.remove();
                        }
                        //Place current location marker
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //add marker pic
                        Log.d(TAG, "Calling add map markers");

                        //move map camera
//                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        LatLng localLatling = new LatLng(location.getLatitude(), location.getLongitude());
                        if(localLatling.longitude < 1 || localLatling.latitude< 1){localLatling = laLing;};
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localLatling, 17));

                        Log.d("onLocationResult", laLing.latitude + " " + laLing.longitude);
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                        TileOverlay onlineTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions()
                                .tileProvider(new OfflineTileProvider(myContext)));

                        offlineDataTransfer.connect();
                        updateInfo();
                        addMapMarkers();
                    }
                }
            };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        geoPoint = ((EventActivity)getActivity()).getEvent().getEventLocation();
        laLing = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        offlineDataTransfer = ((EventActivity) getActivity()).getOfflineDataTransfer();

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

//        LatLng sydney = new LatLng(-34, 151);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mGoogleMap = googleMap;
//        String tilesDir = getContext().getFilesDir().toString();

//        mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(new OfflineTileProvider(getContext())));


        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(70);
        mLocationRequest.setFastestInterval(70);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(status != null){
            offlineDataTransfer.updateStatus(status);
        }
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

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
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

    private void updateInfo(){
        if(invitedUsersList == null){
            invitedUsersList = ((EventActivity) getActivity()).getInvitedUserInEventModelList();
        }
        if (invitedUsersList != null) {
            for (InvitedInEventUserModel user : invitedUsersList) {
                String id = user.getInvitedInEventUserID();
                String status = offlineDataTransfer.getOtherStatus(id);
                GeoPoint location = offlineDataTransfer.getOtherLocation(id);
                user.setInvitedInEventUserStatus(status);
                user.setInvitedInEventUserCurrentLocation(location);
            }
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

            Log.d(TAG, "getting list");
            if (invitedUsersList == null) {
                invitedUsersList = ((EventActivity) getActivity()).getInvitedUserInEventModelList();
            }
            if (invitedUsersList != null) {
                Log.d(TAG, "not null and " + invitedUsersList.size());
                for (InvitedInEventUserModel user : invitedUsersList) {
                    myUserId = EventActivity.getMyUserId();
//                    user.getInvitedInEventUserProfilePicURL()
//                    try {
                        if (user.getInvitedInEventUserID().equals(myUserId)) {
                            GeoPoint Geo;
                            if (mLastLocation == null)
                            {Geo =  new GeoPoint(31.768161300000003, 35.2127055);}
                            else{Geo =  new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());}
                            user.setInvitedInEventUserCurrentLocation(Geo);
                            Log.d(TAG, user.getInvitedInEventUserCurrentLocation().toString());
                            Log.d(TAG, mLastLocation.toString());
                            user.setInvitedInEventUserStatus("Click to post your status");
                        } else {
                            user.setInvitedInEventUserStatus("");
                        }
//                        int avatar = R.drawable.avatar2; // set the default avatar
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference imageRef = storage.getReference().child(user.getInvitedInEventUserID()+".jpg");
//                        StorageReference gsReference = storage.getReferenceFromUrl(user.getInvitedInEventUserProfilePicURL());
                    imageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                String dir = saveToInternalStorage(bitmap);
                                Log.i("download photo", dir);
                                Bitmap b = loadImageFromStorage(dir);
                                Bitmap avatar = b;
                                boolean exist = false;
                                for (int i = 0; i < mClusterMarkers.size(); i++) {
                                    if (mClusterMarkers.get(i).getUserID().equals(user.getInvitedInEventUserID())) {
                                        exist = true;
                                        LatLng updatedLocation = new LatLng(user.getInvitedInEventUserCurrentLocation().getLatitude(), user.getInvitedInEventUserCurrentLocation().getLongitude());
                                        mClusterMarkers.get(i).setPosition(updatedLocation);
                                        mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                    }
                                }
                                LatLng latLng;
                                if (!exist) {
                                    ClusterMarker newClusterMarker = new ClusterMarker(
                                            user.getInvitedInEventUserID(), new LatLng(user.getInvitedInEventUserCurrentLocation().getLatitude(),
                                            user.getInvitedInEventUserCurrentLocation().getLongitude()),
                                            user.getInvitedInEventUserName(),
                                            user.getInvitedInEventUserStatus(),
                                            avatar
                                    );
                                    mClusterManager.addItem(newClusterMarker);
                                    mClusterMarkers.add(newClusterMarker);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("download photo", e.toString());
                            }
                        });
                        mClusterManager.setOnClusterItemClickListener(
                                new ClusterManager.OnClusterItemClickListener<ClusterMarker>() {
                                    @Override
                                    public boolean onClusterItemClick(ClusterMarker clusterItem) {
                                        if (clusterItem.getUserID().equals(myUserId)) {
                                            openDialog();
                                            status = EventActivity.getStatus();
                                            clusterItem.setSnippet(status);
                                            for (int i=0; i<invitedUsersList.size(); i++){
                                                if (invitedUsersList.get(i).getInvitedInEventUserID().equals(myUserId)){
                                                    invitedUsersList.get(i).setInvitedInEventUserStatus(status);
                                                }
                                            }
                                            for (int i = 0; i < mClusterMarkers.size(); i++) {
                                                if (mClusterMarkers.get(i).getUserID().equals(myUserId)) {
                                                    mClusterMarkers.get(i).setSnippet(status);
                                                    mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                                }
                                            }
                                        } else {
                                            return false;
                                        }
                                        // if true, click handling stops here and do not show info view, do not move camera
                                        // you can avoid this by calling:
                                        return false;
                                    }
                                });
//                    } finally {
                        mClusterManager.cluster();
//                    }
                }
            }
        }
    }


    public void openDialog() {
        StatusDialog statusDialog = new StatusDialog();
        statusDialog.show(myContext.getSupportFragmentManager(), "status dialog");
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
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


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"photo.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private Bitmap loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, "photo.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //todo: conect to the rellevant clustermarker b
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
            return b;

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}


//        ((EventActivity)getActivity()).getInvitedUsersList()