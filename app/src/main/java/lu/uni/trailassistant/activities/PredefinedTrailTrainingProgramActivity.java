package lu.uni.trailassistant.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.objects.GPSCoord;
import lu.uni.trailassistant.objects.TrainingProgram;

public class PredefinedTrailTrainingProgramActivity extends TrailActivity {
    private TrainingProgram trainingProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(R.color.colorGreen);
        polyOptions.width(13);
        while(gpsCoordsIterator.hasNext()) {
            GPSCoord currentGPSCoord = gpsCoordsIterator.next();
            LatLng latLng = new LatLng(currentGPSCoord.getLattitude(), currentGPSCoord.getLongitude());
            waypoints.add(latLng);
            polyOptions.add(latLng);
        }
        Polyline polyline = map.addPolyline(polyOptions);
        polylines.add(polyline);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onFinishedExercise(View view) {

    }

}
