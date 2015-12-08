package lu.uni.trailassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.objects.Exercise;
import lu.uni.trailassistant.objects.TrainingProgram;

public class EditTrainingProgramExercisesActivity extends AppCompatActivity {
    int trainingProgramID;
    TrainingProgram trainingProgram;
    EditText trainingProgramNameEditText;
    Button addExerciseButton, finishButton;
    ListView exercisesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_training_program_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // retrieve training program ID from intent and the associated training program from the database
        Long trainingProgramIDLong = getIntent().getLongExtra("training_program_id", 0);
        trainingProgramID = Integer.valueOf(trainingProgramIDLong.intValue());
        //Log.i(EditTrainingProgramExercisesActivity.class.getName(), "Training Program ID that was received by intent: " + trainingProgramID);
        // TODO: maybe check for case in which intent has no data appended to it (=0)? is this possible? ask teacher
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        trainingProgram = dbc.getTrainingProgramFromID(trainingProgramID);
        Log.i(EditTrainingProgramExercisesActivity.class.getName(), "Training Program contents: " + trainingProgram.toString()
        );
        trainingProgramNameEditText = (EditText) findViewById(R.id.trainingProgramNameEditText);
        trainingProgramNameEditText.setText(trainingProgram.getProgramName());

        // populate list view with exercises
        exercisesListView = (ListView) findViewById(R.id.exercisesListView);
        ArrayAdapter<Exercise> exerciseAdapter = new ArrayAdapter<Exercise>(this, R.layout.exercises_list_view_item, trainingProgram.getExercises());
        exercisesListView.setAdapter(exerciseAdapter);
        dbc.closeConnection();
    }

    public void onClickAddTrainingExerciseButton(View view) {
        Intent intent = new Intent(this, AddExerciseActivity.class);
        startActivity(intent);
    }

    public void onClickFinishButton(View view) {
        // TODO: store changes to the database
        finish();
    }
}
