package lu.uni.trailassistant.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.directions.route.BuildConfig;
import com.directions.route.Route;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import lu.uni.trailassistant.R;

/**
 * Created by Jo on 26/11/15.
 */
/*
* Defines the general properties of the two Map Activities
* Check if GPS is enabled: http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
* Current Location : http://stackoverflow.com/questions/16005223/android-google-map-api-v2-current-location
 */
public abstract class AbstractRouteActivity extends FragmentActivity implements RoutingListener, LocationListener, OnMapReadyCallback {

    protected ArrayList<Polyline> polylines;
    private static final String TAG = "MapsActivity";
    protected GoogleMap map;
    protected LocationManager service;
    protected String provider;
    protected List<LatLng> waypoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map = null;
        service = null;
        waypoints = new ArrayList<LatLng>();
        // create a new array of polylines
        polylines = new ArrayList<>();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    protected void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS satellites",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap map){
        this.map = map;
        // GettingManager object from System Service LOCATION_SERVICE
        service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        provider = service.getBestProvider(criteria, true);
    }


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        // select the shortest path
        if(!route.isEmpty()) {
            Route path = route.get(shortestRouteIndex);
            // add polylines to the map and the array
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(R.color.colorPrimary);
            polyOptions.width(13);
            polyOptions.addAll(path.getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            // log the distance and duration
            Log.i(TAG, "[Route] distance: " + path.getDistanceText() + ", duration: " + path.getDurationValue() + " sec");
        }


    }

    public void onRoutingSuccess(PolylineOptions mPolyOptions)
    {
        PolylineOptions polyoptions = new PolylineOptions();
        polyoptions.color(Color.BLUE);
        polyoptions.width(10);
        polyoptions.addAll(mPolyOptions.getPoints());
        map.addPolyline(polyoptions);
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingCancelled() {

    }

    public float getDistanceBetweenTwoPoints(LatLng start, LatLng finish) {
        if (finish != null && start != null) {
            // The computed distance is stored in results[0].
            //If results has length 2 or greater, the initial bearing is stored in results[1].
            //If results has length 3 or greater, the final bearing is stored in results[2].
            float[] results = new float[1];
            Location.distanceBetween(start.latitude, start.longitude, finish.latitude, finish.longitude, results);
            return results[0];
        }
        return 0;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
