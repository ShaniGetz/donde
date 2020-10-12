package com.example.donde.utils;

import android.Manifest;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate.Status;
import com.google.android.gms.nearby.connection.Strategy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;


public class OfflineDataTransfer{
    String myName;
    GeoPoint geoPoint;
    String myStatus;
    Context context;
    String EndpointId;
    String SERVICE_ID;
    ConnectionLifecycleCallback connectionLifecycleCallback;
    PayloadCallback payloadCallback;
    Hashtable<String, GeoPoint> Location_dict = new Hashtable<String, GeoPoint>();
    Hashtable<String, String> Status_dict = new Hashtable<String, String>();
    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    public boolean isAdvertising = false;
    public boolean isDiscovering = false;


    public OfflineDataTransfer(String name, GeoPoint geopoint, Context Context, String status) {
        myName = name;
        geoPoint = geopoint;
        context = Context;
        myStatus = status;
        SERVICE_ID = context.getPackageName();
        Location_dict.put(myName, geopoint);
        Status_dict.put(myName, myStatus);
        payloadCallback = new PayloadCallback() {
                    @Override
                    public void onPayloadReceived(String endpointId, Payload payload) { }
                    @Override
                    public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {}
                };

        connectionLifecycleCallback = new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                        // Automatically accept the connection on both sides.
                        Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                        EndpointId = endpointId;
                        Log.d("OfflineDataTransfer", "Connection initiated : " +
                                endpointId + " ," + connectionInfo.toString());
                    }
                    @Override
                    public void onConnectionResult(String endpointId, ConnectionResolution result) {
                        switch (result.getStatus().getStatusCode()) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                // We're connected! Can now start sending and receiving data.
                                Log.d("OfflineDataTransfer", "Connected : " + endpointId + " ," + result.toString());
                                //once connected disconnect and try again
                                stopAll();
                                connect();
                                break;
                            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                                // The connection was rejected by one or both sides.
                                Log.d("OfflineDataTransfer", "Connection rejected : " + endpointId + " ," + result.toString());
                                break;
                            case ConnectionsStatusCodes.STATUS_ERROR:
                                // The connection broke before it was able to be accepted.
                                break;
                            default:
                                // Unknown status code
                        }
                    }

                    @Override
                    public void onDisconnected(String endpointId) {}
                };
    }

    public void updateLocation(GeoPoint geopoint){
        if(isAdvertising){
            stopAdvertising();
            geoPoint = geopoint;
            Location_dict.put(myName, geopoint);
            startAdvertising();
        }else{
            isDiscovering = true;
            geoPoint = geopoint;
            Location_dict.put(myName, geopoint);
        }
    }

    public void updateStatus(String status){
        if(status != null) {
            if (isAdvertising) {
                stopAdvertising();
                myStatus = status;
                Status_dict.put(myName, myStatus);
                startAdvertising();
            } else {
                myStatus = status;
                Status_dict.put(myName, myStatus);
            }
        }
    }

    public GeoPoint getOtherLocation(String name){
        if(Location_dict.get(name)== null){
            return new GeoPoint(0,0);
        }
        return Location_dict.get(name);

    }
    public String getOtherStatus(String name){
        return Status_dict.get(name);
    }

    public void stopAdvertising(){
        isAdvertising = false;
        Nearby.getConnectionsClient(context).stopAdvertising();
    }
    public void stopDiscovering(){
        isDiscovering = false;
        Nearby.getConnectionsClient(context).stopDiscovery();
    }


    private EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                    Log.d("OfflineDataTransfer", "Endpoint found : " + info.getEndpointName());
                    getNameAndStatus(info.getEndpointName());
                    Nearby.getConnectionsClient(context)
                            .requestConnection(info.getEndpointName(), endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    (Void unused) -> {
                                        Log.d("OfflineDataTransfer", "Request a connection :" + info.getEndpointName());
                                        // We successfully requested a connection. Now both sides
                                        // must accept before the connection is established.
                                        Nearby.getConnectionsClient(context).stopDiscovery();
                                        startAdvertising();
                                    })
                            .addOnFailureListener(
                                    (Exception e) -> {
                                        // Nearby Connections failed to request the connection.
                                        Log.d("OfflineDataTransfer", e.toString());
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };

    public void startAdvertising() {
        isAdvertising = true;
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        String name = getUser();
        Nearby.getConnectionsClient(context).startAdvertising(name, SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're advertising!
                            Log.d("OfflineDataTransfer", "Advertising : " + getUser());
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
//                            Log.d("TAGG", "Unable to start advertising");
                            Log.d("OfflineDataTransfer", e.toString());

                        });
    }

    public void startDiscovery() {
        isDiscovering = true;
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're discovering!
                            Log.d("OfflineDataTransfer", "Discovering...");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                            Log.d("OfflineDataTransfer", e.toString());
                        });
    }

    public void connect(){
        if(isDiscovering){
            stopDiscovering();
            isDiscovering = false;
            startAdvertising();
            isAdvertising = true;
        }else{
            stopAdvertising();
            isAdvertising = false;
            startDiscovery();
            isDiscovering = true;

        }
    }


    public void stopAll() {
        Nearby.getConnectionsClient(context).stopAdvertising();
        Nearby.getConnectionsClient(context).stopAllEndpoints();
        Nearby.getConnectionsClient(context).stopDiscovery();
    }


    public String getUser(){
        return myName + "#" + myStatus + "#" + geoPoint.getLatitude() + "#" + geoPoint.getLongitude();
    }

    public String getNameAndStatus(String string){
        boolean foundName = false;
        boolean foundStat = false;
        boolean foundLatitude = false;
        String Name ="";
        String stat ="";

        int idx = 0;
        String StringOfLatitude;
        GeoPoint geoPoint;

        for(int i =0; i < string.length(); i++) {
            if (string.charAt(i) == '#') {
                if (!foundName) {
                    foundName = true;
                    Name = string.substring(0, i);

                } else if (!foundStat) {
                    stat = string.substring(Name.length() + 1, i);
                    idx = i;
                    foundStat = true;
                    Status_dict.put(Name, stat);
                } else if (!foundLatitude) {
                    StringOfLatitude = string.substring(idx + 1, i);
                    foundLatitude = true;
                    geoPoint = new GeoPoint(Double.parseDouble(StringOfLatitude),
                            Double.parseDouble(string.substring(i + 1)));
                    Location_dict.put(Name, geoPoint);
                    break;

                }
            }
        }
        return Name;
    }
}
