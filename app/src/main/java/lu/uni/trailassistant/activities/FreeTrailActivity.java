package lu.uni.trailassistant.activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import lu.uni.trailassistant.R;



/*
 * Current Location : http://stackoverflow.com/questions/16005223/android-google-map-api-v2-current-location
 * Timer : http://stackoverflow.com/questions/4597690/android-timer-how
 * TODO what happens if the user pushes app to background?
 */

public class FreeTrailActivity extends AbstractRouteActivity{

    // where the user is located when he starts the free trail activity. This will be taken as start
    // position for his training program.
    private Location startLocation;
    private MarkerOptions startMarker;
    private MarkerOptions currentPosition;
    private TextView timerTextView;
    private Button startAndPauseButton;
    long startTime = 0;
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

    public void startOrPause(View view){
        if(startAndPauseButton.getText().equals("Reset")){
            timerHandler.removeCallbacks(timerRunnable);
            startAndPauseButton.setText("Start");
        }else {
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


    private void showResultsToUser(){
        String distanceInKM;
        if(startMarker != null && currentPosition != null) {
            float[] results = getDistanceBetweenTwoPoints(startMarker.getPosition(), currentPosition.getPosition());
            float distanceInMeter = results[0] / 1000;        // total distance in
            distanceInKM = String.format("%.2f", distanceInMeter);
        } else {
            distanceInKM = "0,00";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Statistics for your run: \n" +
                "Elapsed Time: " + timerTextView.getText().toString() + "\n" +
                "Total Distance: " + distanceInKM +"km")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finished();
                            }

                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void finished(){
        Intent intent = new Intent(this, StartScreenActivity.class);
        startActivity(intent);
    }


    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);
    }

    @Override
    public void onLocationChanged(Location location) {
        // redraw the markers when get location update.
        drawMarker(location);
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
            map.addMarker(startMarker);
            traceRoute(startMarker, currentPosition);
        }
    }


    private void drawMarker(Location location){
        // Remove any existing markers on the map
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentPosition).title("ME"));

        // http://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
        CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        map.moveCamera(center);
        map.animateCamera(zoom);
    }


    public Location getStartLocation(){
        return startLocation;
    }


}