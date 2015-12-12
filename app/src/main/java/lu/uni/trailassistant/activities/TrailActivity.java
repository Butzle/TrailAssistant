package lu.uni.trailassistant.activities;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.directions.route.BuildConfig;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import lu.uni.trailassistant.R;

/**
 * Created by Jo on 12/12/15.
 * Activity used to define both the Free Trail Activity and the Predefined Route Activity
 */
public abstract class TrailActivity extends AbstractRouteActivity {
    protected TextView timerTextView;
    private long startTime = 0;
    private Button startAndPauseButton;
    protected boolean isInForeground;
    protected boolean isMockEnabled;

    protected Handler timerHandler = new Handler();

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
        setContentView(R.layout.activity_trail);

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        startAndPauseButton = (Button) findViewById(R.id.start_pause_button);

        isInForeground = true;

        isMockEnabled = isMockLocationEnabled();


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || isMockEnabled){

        }else{
            showGPSDisabledAlertToUser();
        }

    }


    @Override
    public void onMapReady(GoogleMap map){
        super.onMapReady(map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // test if debuggable mode and mock location are disabled
        if (!(((getApplication().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) && isMockEnabled)){
            service.requestLocationUpdates(provider, 2000, 0, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

    // http://stackoverflow.com/questions/33003553/how-to-read-selected-mock-location-app-in-android-m-api-23/33066797#33066797
    public boolean isMockLocationEnabled()
    {
        boolean isMockLocation = false;
        try
        {
            //if marshmallow
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                AppOpsManager opsManager = (AppOpsManager) this.getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
            }
            else
            {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(this.getApplicationContext().getContentResolver(), "mock_location").equals("0");
            }
        }
        catch (Exception e)
        {
            return isMockLocation;
        }

        return isMockLocation;
    }

    public abstract void onFinishedExercise(View view);
}