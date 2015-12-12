package lu.uni.trailassistant.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import lu.uni.trailassistant.R;

public class PredefinedTrainingProgramActivity extends AbstractRouteActivity {

    private TextView timerTextView;
    private long startTime = 0;
    private Button startAndPauseButton;
    private boolean isInForeground;

    private Handler timerHandler = new Handler();

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
        setContentView(R.layout.activity_predefined_training_program);

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        startAndPauseButton = (Button) findViewById(R.id.start_pause_button);

        isInForeground = true;

    }

    @Override
    public void onLocationChanged(Location location) {

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
    }
}
