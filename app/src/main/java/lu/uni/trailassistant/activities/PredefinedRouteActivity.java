package lu.uni.trailassistant.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.db.TrainingProgramCursorAdapter;

public class PredefinedRouteActivity extends AppCompatActivity {
    private Button showHistoryButton;
    private ListView trainingProgramListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predefined_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set references to our elements and disable the "Show history" button on startup
        showHistoryButton = (Button) findViewById(R.id.showHistoryButton);
        showHistoryButton.setEnabled(false);
        trainingProgramListView = (ListView) findViewById(R.id.trainingProgramsListView);

        // populate list view with training programs from the database (coming from a cursor)
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        TrainingProgramCursorAdapter tpca = new TrainingProgramCursorAdapter(this, dbc.getTrainingProgramCursor(), 0);
        trainingProgramListView.setAdapter(tpca);
        dbc.closeConnection();
    }

    public void newTrainingProgram(View view){
        Intent intent = new Intent(this, GoogleMapsDefineRouteActivity.class);
        startActivity(intent);
    }

}
