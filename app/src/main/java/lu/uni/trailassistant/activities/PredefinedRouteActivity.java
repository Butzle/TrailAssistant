package lu.uni.trailassistant.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private Button showHistoryButton, openProgramButton;
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
        showHistoryButton = (Button) findViewById(R.id.showHistoryButton);
        showHistoryButton.setEnabled(false);
        openProgramButton = (Button) findViewById(R.id.openProgramButton);
        openProgramButton.setEnabled(false);
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
                showHistoryButton.setEnabled(true);
                openProgramButton.setEnabled(true);
                // reset background color of last selected ListView item (if needed)
                Drawable defaultBackground = parent.getChildAt(position).getBackground();
                if(lastSelectedIndex > -1) {
                    parent.getChildAt(lastSelectedIndex).setBackground(defaultBackground);
                }
                // put some background color on the newly selected ListView item and remember it's position
                lastSelectedIndex = position;
                view.setBackgroundColor(Color.GREEN);
            }
        });
    }

    public void onClickShowHistoryButton(View view) {
        // TODO: launch intent to a new activity that displays the associated history records
    }

    public void newTrainingProgram(View view){
        Intent intent = new Intent(this, GoogleMapsDefineRouteActivity.class);
        startActivity(intent);
    }

    public void onClickOpenProgramButton(View view) {
        Intent intent = new Intent(this, EditTrainingProgramExercisesActivity.class);
        long trainingProgramRowID = tpca.getItemId(lastSelectedIndex);
        //Log.i(PredefinedRouteActivity.class.getName(), "Training Program ID set by intent: " + trainingProgramRowID);
        intent.putExtra("training_program_id", tpca.getItemId(lastSelectedIndex));
        startActivity(intent);
    }

}
