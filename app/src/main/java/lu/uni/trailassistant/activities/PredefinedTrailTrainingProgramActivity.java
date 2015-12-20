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
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.directions.route.Routing;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.mock.MockLocationProvider;
import lu.uni.trailassistant.objects.Exercise;
import lu.uni.trailassistant.objects.GPSCoord;
import lu.uni.trailassistant.objects.GymExercise;
import lu.uni.trailassistant.objects.RunningExercise;
import lu.uni.trailassistant.objects.TrainingProgram;

// copied the class from: http://www.androidhive.info/2012/01/android-text-to-speech-tutorial/
// enabling text-to-speech required for receiving instructions: http://www.greenbot.com/article/2105862/how-to-get-started-with-google-text-to-speech.html

public class PredefinedTrailTrainingProgramActivity extends TrailActivity implements TextToSpeech.OnInitListener {
    private TrainingProgram trainingProgram;
    private Thread nextLocation;
    private Location startLocation;
    private MarkerOptions startMarker;
    private MarkerOptions currentPosition;
    private List<LatLng> predefinedPathPoints;
    private ListIterator<Exercise> exerciseListIterator;
    private Exercise currentExercise;
    private Thread exerciseStack;
    private boolean isPerformingGymExercise;

    private Marker lastMarker;

    private TextToSpeech tts;
    private boolean isTextToSpeechSuccess;
    private boolean isNewExercise;
    private boolean isSpeakInitialized;

    // needed to calculate the distance to compare with the running exercise distance
    private LatLng lastCheckpoint;
    private LatLng currentLocation;
    private LatLng lastLocation;

    private Iterator<LatLng> predefinedPathPointsIterator;


    private static final String TAG = "TrailTrainingProgram";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        isMockEnabled = false;
        isInForeground = true;
        isPerformingGymExercise = false;

        lastLocation = null;


        isTextToSpeechSuccess = false;
        isNewExercise = true;
        tts = new TextToSpeech(this, this);
        isSpeakInitialized = false;


        currentExercise = null;

        predefinedPathPointsIterator = null;


        predefinedPathPoints = new ArrayList<LatLng>();

        lastMarker = null;

        // retrieve selected ID from Intent
        Intent intent = getIntent();
        Long trainingProgramIDLong = getIntent().getLongExtra("training_program_id", 0);
        int trainingProgramID = Integer.valueOf(trainingProgramIDLong.intValue());

        // retrieve corresponding training program from the database using the ID
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        trainingProgram = dbc.getTrainingProgramFromID(trainingProgramID);
        dbc.closeConnection();

        lastCheckpoint = null;

        exerciseListIterator = trainingProgram.getExercisesAsListIterator();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);

        // create waypoints and polylines from the GPS coordinates on the map
        Iterator<GPSCoord> gpsCoordsIterator = trainingProgram.getGPSCoordsAsListIterator();
        drawPredefinedPath = true;
        while (gpsCoordsIterator.hasNext()) {
            GPSCoord currentGPSCoord = gpsCoordsIterator.next();
            LatLng latLng = new LatLng(currentGPSCoord.getLattitude(), currentGPSCoord.getLongitude());
            predefinedPathPoints.add(latLng);
            traceRoute(predefinedPathPoints);
            drawPredefinedPath = true;
        }

        if (service != null) {
            service.removeUpdates(this);
        }

        if(lastCheckpoint == null) {
            lastCheckpoint = predefinedPathPoints.get(0);
            currentLocation = lastCheckpoint;
            predefinedPathPointsIterator = predefinedPathPoints.iterator();
            executeExerciseStack();
        }

        isMockEnabled = isMockLocationEnabled();
        // check if in debuggable mode and if mock locations are enabled and if so, start the mock, otherwise use the current location of the user
        if (((getApplication().getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0) && isMockEnabled) {
            if (mock == null) {
                mock = new MockLocationProvider("Predefined_Map", this);
            }
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

            nextLocation = new Thread(new Runnable() {
                public void run() {
                    LatLng nextPoint;
                    while (isInForeground && predefinedPathPointsIterator.hasNext()) {
                        if (!isPerformingGymExercise || !isSpeakInitialized) {
                            nextPoint = predefinedPathPointsIterator.next();
                            mock.pushLocation(nextPoint.latitude, nextPoint.longitude);

                            try {
                                // ask every 10 seconds for a new location
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }

                    }
                }
            });
            nextLocation.start();

        } else {        // mock locations or debuggable mode not enabled
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            service.requestLocationUpdates(provider, 10000, 0, this);
        }
    }


    public void executeExerciseStack() {
        exerciseStack = new Thread(new Runnable() {
            // indicates if the end of an exercise has been reached
            public void run() {
                // wait for onInit(int status) to get called
                while(!isSpeakInitialized){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                // to check if the user run more than the distance of the running exercise
                float differenceFromLastRunnungExercise = 0;
                float distanceOfRunningExercise = 0, currentDistanceToLastCheckPoint = 0;
                while (isInForeground) {
                    if (currentExercise == null && exerciseListIterator.hasNext()){
                        currentExercise = exerciseListIterator.next();
                        Log.i(TAG,"initialize");
                    }
                    if (currentExercise instanceof RunningExercise) {


                        isPerformingGymExercise = false;
                        RunningExercise runningExercise = (RunningExercise) currentExercise;

                        if(isNewExercise && isTextToSpeechSuccess){
                            speak(runningExercise.toString());
                            isNewExercise = false;
                        }

                        distanceOfRunningExercise = runningExercise.getDistance() - differenceFromLastRunnungExercise;

                        Log.i(TAG, Double.toString(distanceOfRunningExercise));

                        currentDistanceToLastCheckPoint += getDistanceBetweenTwoPoints(lastLocation, currentLocation);
                        lastLocation = currentLocation;

                        if(currentDistanceToLastCheckPoint == 0){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.i(TAG, "current distance = " + Double.toString(currentDistanceToLastCheckPoint));
                        // double precision problems, therefore <= 1 and not <= 0
                        if (distanceOfRunningExercise - currentDistanceToLastCheckPoint <= 1) {
                            differenceFromLastRunnungExercise = currentDistanceToLastCheckPoint - distanceOfRunningExercise;
                            Log.i(TAG, "Running Exercise Terminated");
                            if(exerciseListIterator.hasNext()) {
                                currentExercise = exerciseListIterator.next();
                                lastCheckpoint = currentLocation;
                                lastLocation = lastCheckpoint;
                                currentDistanceToLastCheckPoint = 0;
                                isNewExercise = true;
                                Log.i(TAG, "has Next");
                                continue;
                            }
                        }

                    } else if (currentExercise instanceof GymExercise) {
                        isPerformingGymExercise = true;
                        GymExercise gymExercise = (GymExercise) currentExercise;

                        if(isTextToSpeechSuccess){
                            speak(gymExercise.toString());
                        }
                        int duration = gymExercise.getDuration();
                        Log.i(TAG, gymExercise.toString());
                        try {
                            Thread.sleep(duration * 1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        if (exerciseListIterator.hasNext()) {
                            currentExercise = exerciseListIterator.next();
                            Log.i(TAG, "has Next");
                            isNewExercise = true;
                            continue;
                        }

                    }
                    if (!exerciseListIterator.hasNext()){
                        while(predefinedPathPointsIterator.hasNext()){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(isTextToSpeechSuccess){
                            speak("Training Program Terminated");
                        }
                        Log.i(TAG, "Training Program Terminated!");
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        exerciseStack.start();
    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation = currentLocation;
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
            if (lastMarker != null) {
                lastMarker.remove();
            }
            map.addMarker(startMarker);
            lastMarker = map.addMarker(currentPosition);
            drawPredefinedPath = false;
            traceRoute(waypoints);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            map.moveCamera(center);
        }
    }

    @Override
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
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void finished() {
        Intent intent = new Intent(this, StartScreenActivity.class);
        reset();
        startActivity(intent);
    }


    // trace the route of the user
    protected void traceRoute(List<LatLng> points) {

        // initialize an async. request using the direction api
        if (points.size() >= 2) {
            LatLng fromIntermediatePoint = points.get(points.size() - 2);
            LatLng toIntermediatePoint = points.get(points.size() - 1);
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
    public void onInit(int status) {

        Log.v(TAG, "onInit");

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                isTextToSpeechSuccess = true;
            }

        } else {
            Log.v(TAG,"TTS Initilization Failed!");
        }
        isSpeakInitialized = true;

    }

    private void speak(String text) {

        // the not deprecated method requires API lvl 21
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }


    public void reset() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        isInForeground = false;
        waypoints = new ArrayList<LatLng>();
        service.removeUpdates(this);
        predefinedPathPoints = new ArrayList<LatLng>();
        drawPredefinedPath = true;

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();

    }

}