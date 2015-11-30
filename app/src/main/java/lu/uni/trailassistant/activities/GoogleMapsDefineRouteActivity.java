package lu.uni.trailassistant.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import lu.uni.trailassistant.R;

/*
 * Current Location : http://stackoverflow.com/questions/16005223/android-google-map-api-v2-current-location
 */
public class GoogleMapsDefineRouteActivity extends AbstractRouteActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private MarkerOptions origin;
    private MarkerOptions destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_define_route);

        // initialize object attributes
        origin = null;
        destination = null;

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {

            // setup the map fragment widget
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            // fetch the map async.
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);
        map.setOnMapLongClickListener(this);
        map.setOnMapClickListener(this);

    }

    @Override
    public void onMapClick(LatLng point) {
        if(origin != null) {
            map.clear();
            origin = new MarkerOptions().position(point).title("Origin");
            origin.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            createOriginMarker();
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        if(origin != null) {
            // remove all markers (origin and destination)
            map.clear();

            // create the destination marker
            destination = new MarkerOptions().position(point).title("Destination");
            destination.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));

            // add both markers to the map
            createOriginMarker();
            createDestinationMarker();

            // display the route and log the duration/distance
            traceRoute(origin, destination);
        }
    }

    private void createOriginMarker() {
        // safe guard
        if (origin == null)
            return;

        // add the marker to the map
        map.addMarker(origin);

        // center the camera with a zoom of 16
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(), 16));
    }

    private void createDestinationMarker() {
        // safe guard
        if (destination == null)
            return;

        // add the marker to the map
        map.addMarker(destination);

        // center the camera with a zoom of 16
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination.getPosition(), 16));
    }



    public void createRoute(View view) {
        if (destination != null) {
            float[] results = getDistanceBetweenTwoPoints(origin.getPosition(), destination.getPosition());
            int distance = (int) results[0];
            /*Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, Integer.toString(distance), duration).show();*/
        }
        Intent intent = new Intent(this, CreateNewTrainingProgramActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        // redraw the markers when get location update.
        if (origin == null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            origin = new MarkerOptions().position(userLocation).title("Origin");
            origin.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            // add the marker to the map
            createOriginMarker();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            service.removeUpdates(this);
        }
    }

}