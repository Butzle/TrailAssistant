package lu.uni.trailassistant.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.directions.route.Routing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;

import lu.uni.trailassistant.R;

/*
 * Current Location : http://stackoverflow.com/questions/16005223/android-google-map-api-v2-current-location
 */
public class GoogleMapsDefineShortestRouteActivity extends AbstractRouteActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private MarkerOptions origin;
    private MarkerOptions destination;

    // start and destination
    private LatLng startPoint, destinationPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_define_route);

        // initialize object attributes
        origin = null;
        destination = null;

        startPoint = null;
        destinationPoint = null;

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    public void onMapClick(LatLng point) {
        if(origin != null) {
            map.clear();
            startPoint = point;
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

            destinationPoint = point;

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

    }

    private void createDestinationMarker() {
        // safe guard
        if (destination == null)
            return;

        // add the marker to the map
        map.addMarker(destination);

        // center the camera with a zoom of 16
       // map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination.getPosition(), 16));
    }



    public void defineTrainingProgram(View view) {
        double totalDistanceInMeter = 0;
        if (destination != null) {
            totalDistanceInMeter = getDistanceBetweenTwoPoints(origin.getPosition(), destination.getPosition());
        }

        ArrayList<Double> latitudes = new ArrayList<>();
        ArrayList<Double> longitutdes = new ArrayList<>();

        if(startPoint != null && destinationPoint != null) {

            latitudes.add(startPoint.latitude);
            latitudes.add(destinationPoint.latitude);

            longitutdes.add(startPoint.longitude);
            longitutdes.add(destinationPoint.longitude);

            waypoints.add(startPoint);
            waypoints.add(destinationPoint);
        }


        Intent intent = new Intent(this, NewTrainingProgramActivity.class);
        intent.putExtra("latitudesArrayList", latitudes);
        intent.putExtra("longitudesArrayList", longitutdes);
        intent.putExtra("totalDistanceInMeter", totalDistanceInMeter);
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
            // center the camera with a zoom of 16
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(), 16));

            service.removeUpdates(this);
        }
    }

    // trace the route of the user
    protected void traceRoute(MarkerOptions origin, MarkerOptions destination) {
        // initialize an async. request using the direction api
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(origin.getPosition(), destination.getPosition())
                .build();

        // launch the request
        routing.execute();
    }

}