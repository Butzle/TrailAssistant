package lu.uni.trailassistant.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.mock.MockLocationProvider;
import lu.uni.trailassistant.objects.GPSCoord;
import lu.uni.trailassistant.objects.TrainingProgram;

public class PredefinedTrailTrainingProgramActivity extends TrailActivity {
    private TrainingProgram trainingProgram;
    private Thread nextLocation;
    private Location startLocation;
    private MarkerOptions startMarker;
    private MarkerOptions currentPosition;
    private List<LatLng> predefinedPathPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isMockEnabled = false;
        isInForeground = true;

        predefinedPathPoints = new ArrayList<LatLng>();

        // retrieve selected ID from Intent
        Intent intent = getIntent();
        Long trainingProgramIDLong = getIntent().getLongExtra("training_program_id", 0);
        int trainingProgramID = Integer.valueOf(trainingProgramIDLong.intValue());

        // retrieve corresponding training program from the database using the ID
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        trainingProgram = dbc.getTrainingProgramFromID(trainingProgramID);
        dbc.closeConnection();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);

        // create waypoints and polylines from the GPS coordinates on the map
        Iterator<GPSCoord> gpsCoordsIterator = trainingProgram.getGPSCoordsAsListIterator();
        while (gpsCoordsIterator.hasNext()) {
            GPSCoord currentGPSCoord = gpsCoordsIterator.next();
            LatLng latLng = new LatLng(currentGPSCoord.getLattitude(), currentGPSCoord.getLongitude());
            predefinedPathPoints.add(latLng);

        }
        drawPredefinedPath = true;
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints( predefinedPathPoints)
                .build();

        // call onRoutingSuccess
        routing.execute();

        if(service != null){
            service.removeUpdates(this);
        }

        isMockEnabled = isMockLocationEnabled();
        // check if in debuggable mode and if mock locations are enabled and if so, start the mock, otherwise use the current location of the user
        if (((getApplication().getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0) && isMockEnabled) {
            if (mock == null) {
                mock = new MockLocationProvider("Predefined_Map", this);
            }
           // mock.pushLocation( predefinedPathPoints.get(0).latitude,  predefinedPathPoints.get(0).longitude);
            LocationManager locMgr = (LocationManager)
                    getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locMgr.requestLocationUpdates("Predefined_Map", 0, 50, this);

            Context context = getApplicationContext();
            CharSequence text = "Mock!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            // TODO : need to change this part: need to consider exercises + bugs in it
            nextLocation = new Thread(new Runnable() {
                public void run() {
                    Iterator<LatLng>  predefinedPathPointsIterator =  predefinedPathPoints.iterator();
                    LatLng nextPoint;
                    while (isInForeground && predefinedPathPointsIterator.hasNext()) {
                        nextPoint = predefinedPathPointsIterator.next();
                        mock.pushLocation(nextPoint.latitude, nextPoint.longitude);

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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            service.requestLocationUpdates(provider, 60000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Context context = getApplicationContext();
        CharSequence text = "Location Changed!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        // TODO consider exercises here as well
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
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
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

    @Override
    public void onFinishedExercise(View view) {

    }

}
