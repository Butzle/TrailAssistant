package lu.uni.trailassistant.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.BuildConfig;
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
    private Thread nextLocation;
    private boolean isInForeground;


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
        startLocation = null;
        currentPosition = null;
        mock = null;
        isMockEnabled = false;

        isInForeground = true;

        waypoints = new ArrayList<LatLng>();
        lat = 49.600896;
        lon = 6.154168;

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
            totalDistanceInKM = totalDistanceInMeter / 1000;        // total distance in km
            distanceInKM = String.format("%.2f", totalDistanceInKM);
        } else {
            distanceInKM = "0,00";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Statistics for your run: \n" +
                "Elapsed Time: " + timerTextView.getText().toString() + "\n" +
                "Total Distance: " + distanceInKM + "km")
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finished();
                            }

                        });
        alertDialogBuilder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveRoute();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void saveRoute() {
        Intent intent = new Intent(this, CreateNewTrainingProgramActivity.class);
        reset();
        startActivity(intent);
    }

    public void finished() {
        Intent intent = new Intent(this, StartScreenActivity.class);
        reset();
        startActivity(intent);
    }


    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);

        if(service != null){
            service.removeUpdates(this);
        }


        isMockEnabled = isMockLocationEnabled();
        // check if in debuggable mode and if mock locations are enabled and if so, start the mock, otherwise use the current location of the user
        if (((getApplication().getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0) && isMockEnabled) {

            if (mock == null) {
                mock = new MockLocationProvider("Map", this);
            }

            // set the start location
            mock.pushLocation(lat, lon);

            LocationManager locMgr = (LocationManager)
                    getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locMgr.requestLocationUpdates("Map", 0, 50, this);

            Context context = getApplicationContext();
            CharSequence text = "Mock!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            nextLocation = new Thread(new Runnable() {
                public void run() {
                    lat = 49.600896;
                    lon = 6.154168;
                    while (isInForeground) {
                        Random r = new Random();
                        mock.pushLocation(lat, lon);
                        // calculate random next location
                        lat += 0.001;
                        lon += 0.001 + (0.002 - 0.001) * r.nextDouble();

                        try {
                            // ask every 20 seconds a new location
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            break;
                        }

                    }
                }
            });
            nextLocation.start();

        } else {        // mock locations or debuggable mode not enabled
            service.requestLocationUpdates(provider, 2000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // redraw the markers when get location update.
        drawMarker(location);
        waypoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        currentPosition = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current Position");
        if (startMarker == null) {
            startLocation = location;
            startMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Start Position");
            startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            map.addMarker(startMarker);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(getStartLocation().getLatitude(), getStartLocation().getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            map.moveCamera(center);
            map.animateCamera(zoom);
        } else {
            map.addMarker(currentPosition);
            traceRoute();
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            map.moveCamera(center);
        }
    }


    private void drawMarker(Location location) {
        // Remove any existing markers on the map
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentPosition).title("ME"));

        // http://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        //CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        map.moveCamera(center);
        //map.animateCamera(zoom);
    }


    public Location getStartLocation() {
        return startLocation;
    }

    // trace the route of the user
    protected void traceRoute() {

        // initialize an async. request using the direction api
        if (waypoints.size() >= 2) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();
        // terminate thread when going back to another activity

    }

    public void reset() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        isInForeground = false;
        waypoints = new ArrayList<LatLng>();
        service.removeUpdates(this);
    }




}