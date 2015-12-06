package lu.uni.trailassistant.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.directions.route.Routing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.mock.MockLocationProvider;



/*
 * Current Location : http://stackoverflow.com/questions/16005223/android-google-map-api-v2-current-location
 * Timer : http://stackoverflow.com/questions/4597690/android-timer-how
 * TODO what happens if the user pushes app to background?
 */

public class FreeTrailActivity extends AbstractRouteActivity {

    // where the user is located when he starts the free trail activity. This will be taken as start
    // position for his training program.
    private Location startLocation;
    private MarkerOptions startMarker;
    private MarkerOptions currentPosition;
    private TextView timerTextView;
    private Button startAndPauseButton;
    private List<LatLng> waypoints;
    private MockLocationProvider mock;
    long startTime = 0;
    private double lat;
    private double lon;


    Handler timerHandler = new Handler();

    // separate Thread for the clock
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_trail);

        lat = 49.600896;
        lon = 6.154168;


        startLocation = null;
        currentPosition = null;
        mock = null;

        waypoints = new ArrayList<LatLng>();

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        startAndPauseButton = (Button) findViewById(R.id.start_pause_button);

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

    public void startOrPause(View view) {
        if (startAndPauseButton.getText().equals("Reset")) {
            timerHandler.removeCallbacks(timerRunnable);
            startAndPauseButton.setText("Start");
        } else {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            startAndPauseButton.setText("Reset");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        startAndPauseButton.setText("Start");
    }

    public void onFinishedExercise(View view) {
        showResultsToUser();

    }


    private void showResultsToUser() {
        String distanceInKM;
        float totalDistanceInMeter = 0;
        float totalDistanceInKM = 0;
        if (startMarker != null && currentPosition != null) {
            for (int i = 0; i < waypoints.size() - 1; i++) {
                totalDistanceInMeter += getDistanceBetweenTwoPoints(waypoints.get(i), waypoints.get(i + 1));
            }
            totalDistanceInKM = totalDistanceInMeter / 1000;        // total distance in
            distanceInKM = String.format("%.2f", totalDistanceInKM);
        } else {
            distanceInKM = "0,00";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Statistics for your run: \n" +
                "Elapsed Time: " + timerTextView.getText().toString() + "\n" +
                "Total Distance: " + distanceInKM + "km")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finished();
                            }

                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void finished() {
        Intent intent = new Intent(this, StartScreenActivity.class);
        startActivity(intent);
    }


    public void onMapReady(GoogleMap map) {
       super.onMapReady(map);


        // if in debuggable mode
        if ((getApplication().getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0) {

            if (mock==null) {
                mock = new MockLocationProvider("Map", this);
            }

            //Set test location
            mock.pushLocation(lat, lon);

            LocationManager locMgr = (LocationManager)
                    getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locMgr.requestLocationUpdates("Map", 0, 50, this);

            new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            // ask every 20 seconds a new location
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Random r = new Random();
                        mock.pushLocation(lat, lon);
                        // calculate random next location
                        lat += 0.001;
                        lon += 0.001 + (0.002 - 0.001) * r.nextDouble();

                    }
                }
            }).start();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // redraw the markers when get location update.
        drawMarker(location);
        waypoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        currentPosition = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current Position");
        if(startMarker == null){
            startLocation = location;
            startMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Start Position");
            startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            map.addMarker(startMarker);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(getStartLocation().getLatitude(), getStartLocation().getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            map.moveCamera(center);
            map.animateCamera(zoom);
        }
        else {
            map.addMarker(currentPosition);
            traceRoute();
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            map.moveCamera(center);
        }
    }


    private void drawMarker(Location location){
        // Remove any existing markers on the map
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentPosition).title("ME"));

        // http://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
        CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        //CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        map.moveCamera(center);
        //map.animateCamera(zoom);
    }


    public Location getStartLocation(){
        return startLocation;
    }

    // trace the route of the user
    protected void traceRoute() {

        // initialize an async. request using the direction api
        if(waypoints.size() >= 2) {
            LatLng fromIntermediatePoint = waypoints.get(waypoints.size() - 2);
            LatLng toIntermediatePoint = waypoints.get(waypoints.size() - 1);
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(this)
                    .waypoints(fromIntermediatePoint, toIntermediatePoint)
                    .build();

            // launch the request
            routing.execute();
        }
    }


}