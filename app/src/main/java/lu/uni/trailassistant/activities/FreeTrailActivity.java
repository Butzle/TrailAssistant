package lu.uni.trailassistant.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.directions.route.Routing;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.Random;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.mock.MockLocationProvider;



public class FreeTrailActivity extends TrailActivity {

    // where the user is located when he starts the free trail activity. This will be taken as start
    // position for his training program.
    private Location startLocation;
    private MarkerOptions startMarker;
    private MarkerOptions currentPosition;
    private double lat;
    private double lon;
    private Thread nextLocation;
    private double totalDistanceInMeter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLocation = null;
        currentPosition = null;
        isMockEnabled = false;

        isInForeground = true;
        lat = 49.600896;
        lon = 6.154168;

        totalDistanceInMeter = 0;
    }


    public void onFinishedExercise(View view) {
        showResultsToUser();

    }


    private void showResultsToUser() {
        String distanceInKM;
        totalDistanceInMeter = 0;
        double totalDistanceInKM = 0;
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
        Intent intent = new Intent(this, NewTrainingProgramActivity.class);
        ArrayList<Double> latitudes = new ArrayList<Double>();
        ArrayList<Double> longitudes = new ArrayList<Double>();
        for(LatLng point: waypoints){
            latitudes.add(point.latitude);
            longitudes.add(point.longitude);

           /* Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;


            Toast toast = Toast.makeText(context, Double.toString(point.latitude), duration);
            toast.show();*/
        }
        intent.putExtra("latitudesArrayList", latitudes);
        intent.putExtra("longitudesArrayList", longitudes);
        intent.putExtra("totalDistanceInMeter", totalDistanceInMeter);
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
                        lat += 0.0025;
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
            service.requestLocationUpdates(provider, 60000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // redraw the markers when get location update.
        // drawMarker(location);
        waypoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        currentPosition = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Me");
        if (startMarker == null) {
            startLocation = location;
            startMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Start Position");
            startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            map.addMarker(startMarker);
            // http://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
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


    public Location getStartLocation() {
        return startLocation;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();

    }

    public void reset() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        isInForeground = false;
        waypoints = new ArrayList<LatLng>();
        service.removeUpdates(this);
        //map.clear();
    }
}