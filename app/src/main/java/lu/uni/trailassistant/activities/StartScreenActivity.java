package lu.uni.trailassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import lu.uni.trailassistant.R;

/*
 */
public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void predefinedRoute(View view){
        Intent intent = new Intent(this, PredefinedRouteActivity.class);
        startActivity(intent);
    }

    public void freeTrail(View view){
        Intent intent = new Intent(this, FreeTrailActivity.class);
        startActivity(intent);
    }
}
