package lu.uni.trailassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.objects.Exercise;
import lu.uni.trailassistant.objects.GPSCoord;
import lu.uni.trailassistant.objects.TrainingProgram;

/**
 * Created by Jo on 11/12/15.
 */
public class NewTrainingProgramActivity extends AppCompatActivity {
    TrainingProgram trainingProgram;
    EditText trainingProgramNameEditText;
    ArrayList<Double> latitudes;
    ArrayList<Double> longitudes;
    Double totalDistanceInMeter;
    static final int ADD_EXERCISES=1;
    ArrayAdapter<Exercise> exerciseAdapter;
    Button moveUpButton, moveDownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_training_program_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        trainingProgramNameEditText = (EditText) findViewById(R.id.trainingProgramNameEditText);
        trainingProgramNameEditText.setText("Enter Name");
        trainingProgramNameEditText.selectAll();

        latitudes = (ArrayList<Double>)getIntent().getSerializableExtra("latitudesArrayList");
        longitudes = (ArrayList<Double>)getIntent().getSerializableExtra("longitudesArrayList");
        totalDistanceInMeter = getIntent().getDoubleExtra("totalDistanceInMeter", 0);


        moveUpButton = (Button)findViewById(R.id.moveUpButton);
        moveDownButton = (Button)findViewById(R.id.moveDownButton);

        moveUpButton.setVisibility(View.GONE);
        moveDownButton.setVisibility(View.GONE);

       if(!latitudes.isEmpty() && !longitudes.isEmpty() && totalDistanceInMeter != 0) {
            // create new training program
           trainingProgram = new TrainingProgram();
           exerciseAdapter = new ArrayAdapter<Exercise>(this, R.layout.exercises_list_view_item, trainingProgram.getExercises());
            Iterator<Double> latitudesIterator = latitudes.iterator();
            Iterator<Double> longitudesIterator = longitudes.iterator();
            // copy GPS coordinates to the training program
            while(latitudesIterator.hasNext() && longitudesIterator.hasNext()) {
                GPSCoord gpsCoord = new GPSCoord(0, longitudesIterator.next(), latitudesIterator.next());
                trainingProgram.appendGPSCoordToTrail(gpsCoord);
            }
       }
    }

    public void onClickSaveButton(View view) {
        if(trainingProgram != null) {
            DBConnector dbc = new DBConnector(this);
            dbc.openConnection();

            trainingProgram.setTrainingProgramName(trainingProgramNameEditText.getText().toString());
            dbc.writeTrainingProgramToDB(trainingProgram);

            dbc.closeConnection();
            finish();
            Toast.makeText(this, "New Training Program successfully created!", Toast.LENGTH_SHORT).show();
        }else {
            finish();
        }

    }

    public void onClickAddTrainingExerciseButton(View view) {
        Intent intent = new Intent(this, AddExerciseActivity.class);
        intent.putExtra("totalDistanceInMeter", totalDistanceInMeter);
        startActivityForResult(intent, ADD_EXERCISES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(trainingProgram != null) {
            if (requestCode == ADD_EXERCISES) {
                if (resultCode == RESULT_OK) {
                    HashMap<Integer, Exercise> exercisesToBeAdded = (HashMap<Integer, Exercise>) data.getSerializableExtra("exercises_to_be_added");
                    int amountOfExercises = data.getIntExtra("amount_of_exercises", 0);
                    if (amountOfExercises > 0) {
                        for (int counter = 0; counter < amountOfExercises; counter++) {
                            Exercise exercise = exercisesToBeAdded.get(counter);
                            trainingProgram.appendExerciseToTail(exercise);
                        }
                        exerciseAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}
