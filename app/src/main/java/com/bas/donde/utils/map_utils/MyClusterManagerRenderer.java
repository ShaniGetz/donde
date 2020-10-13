package com.bas.donde.utils.map_utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bas.donde.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<MyClusterItem> {

    public static final String TAG = "tagEventMapFragment";
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public MyClusterManagerRenderer (Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager){
        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
        Log.d(TAG, "in onBeforeClusterItemRendered with " + item.getSnippet());

//        imageView.setImageResource(item.getIconPicture());
        imageView.setImageBitmap(item.getIconPictureBitmap());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyClusterItem> cluster) {
        return false;
    }

    /**
     * Update the GPS coordinate of a ClusterItem
     * @param myClusterItem
     */
    public void setUpdateMarker(MyClusterItem myClusterItem) {
        Log.d(TAG, "in setUpdateMarker with " + myClusterItem.getSnippet());
        Marker marker = getMarker(myClusterItem);
        if (marker != null) {
            marker.setPosition(myClusterItem.getPosition());
            marker.setSnippet(myClusterItem.getSnippet());
        }
    }

}
