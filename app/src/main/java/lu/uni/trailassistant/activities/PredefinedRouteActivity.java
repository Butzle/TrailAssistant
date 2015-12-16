package lu.uni.trailassistant.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.db.TrainingProgramCursorAdapter;


public class PredefinedRouteActivity extends AppCompatActivity {
    private Button editProgramButton, startTrainingProgramButton;
    private ListView trainingProgramListView;
    private TrainingProgramCursorAdapter tpca;
    private int lastSelectedIndex=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predefined_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set references to our elements and disable the "Show history" button on startup
        editProgramButton = (Button) findViewById(R.id.edit_program_button);
        editProgramButton.setEnabled(false);

        startTrainingProgramButton = (Button) findViewById(R.id.start_training_program_button);
        startTrainingProgramButton.setEnabled(false);

        trainingProgramListView = (ListView) findViewById(R.id.trainingProgramsListView);

        // populate list view with training programs from the database (coming from a cursor)
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        tpca = new TrainingProgramCursorAdapter(this, dbc.getTrainingProgramCursor(), 0);
        trainingProgramListView.setAdapter(tpca);
        dbc.closeConnection();

        // set onItemClick listener on training program list view
        trainingProgramListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if(trainingProgramListView.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
                    showHistoryButton.setEnabled(true);
                    openProgramButton.setEnabled(true);
                } else {
                    showHistoryButton.setEnabled(false);
                    openProgramButton.setEnabled(false);
                }*/
                // enable Buttons as soon as user clicks on an item in the ListView
                //showHistoryButton.setEnabled(true);
                editProgramButton.setEnabled(true);
                startTrainingProgramButton.setEnabled(true);
                lastSelectedIndex = position;
            }
        });
    }

    // Start a new Training Program
    public void onClickStartTrainingProgram(View view) {
        Intent intent = new Intent(this, PredefinedTrailTrainingProgramActivity.class);
        //Log.i(PredefinedRouteActivity.class.getName(), "Training Program ID set by intent: " + trainingProgramRowID);
        intent.putExtra("training_program_id", tpca.getItemId(lastSelectedIndex));
        startActivity(intent);
    }

    public void newTrainingProgram(View view){
        Intent intent = new Intent(this, GoogleMapsDefineShortestRouteActivity.class);
        //Log.i(PredefinedRouteActivity.class.getName(), "Training Program ID set by intent: " + trainingProgramRowID);
        // intent.putExtra("training_program_id", tpca.getItemId(lastSelectedIndex));
        startActivity(intent);
    }

    // Edit an existing training program
    public void onClickEditProgramButton(View view) {
        Intent intent = new Intent(this, EditTrainingProgramExercisesActivity.class);
        intent.putExtra("training_program_id", tpca.getItemId(lastSelectedIndex));
        startActivity(intent);
    }
}
