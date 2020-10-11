package com.example.donde.utils;

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

import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class OfflineDataTransfer{
    String myName;
    GeoPoint geoPoint;
    String myStatus;
    Context context;
    String EndpointId;
    String SERVICE_ID;
    Hashtable<String, GeoPoint> Location_dict = new Hashtable<String, GeoPoint>();
    Hashtable<String, String> Status_dict = new Hashtable<String, String>();

    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;


    OfflineDataTransfer(String name, GeoPoint geopoint, Context Context, String status){
        myName = name;
        geoPoint = geopoint;
        context = Context;
        myStatus = status;
        SERVICE_ID = context.getPackageName();

    }

    public void updateLocation(GeoPoint geopoint){
        geoPoint = geopoint;
    }

    public void updateStatus(String status){
        myStatus = status;
    }
    public GeoPoint getOtherLocation(String name){
        return Location_dict.get(name);

    }

    public String getOtherStatus(String name){
        return Status_dict.get(name);

    }

    private ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                    EndpointId = endpointId;
                    Log.d("TAG","Connection initiated : " + endpointId + " ," + connectionInfo.toString());
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            Log.d("TAG","Connected : " + endpointId + " ," + result.toString());
//                            Nearby.getConnectionsClient(context).stopAdvertising();
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            Log.d("TAG","Connection rejected : " + endpointId + " ," + result.toString());
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            break;
                        default:
                            // Unknown status code
                    }

                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                }
            };

    private PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    // This always gets the full data of the payload. Will be null if it's not a BYTES
                    // payload. You can check the payload type with payload.getType().
                    byte[] receivedBytes = payload.asBytes();
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
                    // after the call to onPayloadReceived().
                }
            };

    private EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                    Log.d("TAG", "Endpoint found : " + info.getEndpointName());
                    getNameAndStatus(info.getEndpointName());
                    Nearby.getConnectionsClient(context)
                            .requestConnection(info.getEndpointName() + ".1", endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    (Void unused) -> {
                                        Log.d("TAG", "Request a connection :" + info.getEndpointName());
                                        // We successfully requested a connection. Now both sides
                                        // must accept before the connection is established.
                                    })
                            .addOnFailureListener(
                                    (Exception e) -> {
                                        // Nearby Connections failed to request the connection.
                                        Log.d("TAG", "Nearby Connections failed to request the connection.");
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        getUser(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're advertising!
                            Log.d("TAG", "Advertising : " + getUser());

                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
                            Log.d("TAG", "Unable to start advertising");
                        });
    }

    public void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're discovering!
                            Log.d("TAG", "Discovering...");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                            Log.d("TAG", "We're unable to start discovering");
                        });

    }


    private void stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising();
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
                    stat = string.substring(0, i);
                    idx = i;
                    foundStat = true;
                    Status_dict.put(Name, stat);
                } else if (!foundLatitude) {
                    StringOfLatitude = string.substring(idx, i);
                    foundLatitude = true;
                    geoPoint = new GeoPoint(Double.parseDouble(StringOfLatitude),
                            Double.parseDouble(string.substring(i)));
                    Location_dict.put(Name, geoPoint);

                }
            }
        }
        return Name;
    }
}
