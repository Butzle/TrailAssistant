package lu.uni.trailassistant.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.objects.Exercise;
import lu.uni.trailassistant.objects.TrainingProgram;

public class EditTrainingProgramExercisesActivity extends AppCompatActivity {
    static final int ADD_EXERCISES=1, EDIT_EXERCISE=2;

    int trainingProgramID;
    TrainingProgram trainingProgram;
    EditText trainingProgramNameEditText;
    Button addExerciseButton, moveUpButton, moveDownButton;
    ListView exercisesListView;
    ArrayAdapter<Exercise> exerciseAdapter;
    int lastSelectedIndex = -1;
    boolean editTextIsFocused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_training_program_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // No distance left to define => if button enabled here, need to check if the user always defines exactly the total distance
        addExerciseButton = (Button)findViewById(R.id.add_exercise_button);
        addExerciseButton.setEnabled(false);
        moveUpButton = (Button)findViewById(R.id.moveUpButton);
        moveUpButton.setVisibility(View.INVISIBLE);
        moveDownButton = (Button)findViewById(R.id.moveDownButton);
        moveDownButton.setVisibility(View.INVISIBLE);

        editTextIsFocused = true;


        // retrieve training program ID from intent and the associated training program from the database
        Long trainingProgramIDLong = getIntent().getLongExtra("training_program_id", 0);
        trainingProgramID = Integer.valueOf(trainingProgramIDLong.intValue());
        //Log.i(EditTrainingProgramExercisesActivity.class.getName(), "Training Program ID that was received by intent: " + trainingProgramID);
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        trainingProgram = dbc.getTrainingProgramFromID(trainingProgramID);
        trainingProgramNameEditText = (EditText) findViewById(R.id.trainingProgramNameEditText);
        trainingProgramNameEditText.setText(trainingProgram.getProgramName());
        trainingProgramNameEditText.requestFocus();
        trainingProgramNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    moveUpButton.setVisibility(View.INVISIBLE);
                    moveDownButton.setVisibility(View.INVISIBLE);
                    editTextIsFocused = true;
                }else {
                    editTextIsFocused = false;
                }
            }
        });

        // populate list view with exercises
        exercisesListView = (ListView) findViewById(R.id.exercisesListView);
        exerciseAdapter = new ArrayAdapter<Exercise>(this, R.layout.exercises_list_view_item, trainingProgram.getExercises());
        exercisesListView.setAdapter(exerciseAdapter);
        dbc.closeConnection();

        // set listener on ListView
        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveUpButton.setVisibility(View.VISIBLE);
                moveDownButton.setVisibility(View.VISIBLE);
                if(lastSelectedIndex == -1) {
                    moveUpButton.setEnabled(true);
                    moveDownButton.setEnabled(true);
                }
                lastSelectedIndex = position;
            }
        });
    }

    public void onClickAddTrainingExerciseButton(View view) {
        Intent intent = new Intent(this, AddExerciseActivity.class);
        startActivityForResult(intent, ADD_EXERCISES);
    }

    public void onClickSaveButton(View view) {
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        trainingProgram.setTrainingProgramName(trainingProgramNameEditText.getText().toString());
        dbc.updateExistingTrainingProgram(trainingProgram);
        dbc.closeConnection();
        finish();
        Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_EXERCISES) {
            if(resultCode == RESULT_OK) {
                HashMap<Integer,Exercise> exercisesToBeAdded = (HashMap<Integer,Exercise>)data.getSerializableExtra("exercises_to_be_added");
                int amountOfExercises = data.getIntExtra("amount_of_exercises", 0);
                if(amountOfExercises>0) {
                    for(int counter=0; counter<amountOfExercises; counter++) {
                        Exercise exercise = exercisesToBeAdded.get(counter);
                        trainingProgram.appendExerciseToTail(exercise);
                    }
                    exerciseAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(this, "An error occured while trying to save the exercises! Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == EDIT_EXERCISE) {
            if(resultCode == RESULT_OK) {

            }
        }
    }

    public void onClickMoveUpButton(View view) {
        if(lastSelectedIndex > 0) {
            Exercise tempExercise = trainingProgram.getExercises().get(lastSelectedIndex);
            Exercise tempExercise2 = trainingProgram.getExercises().get(lastSelectedIndex-1);
            trainingProgram.getExercises().set(lastSelectedIndex, tempExercise2);
            trainingProgram.getExercises().set(lastSelectedIndex-1, tempExercise);
            lastSelectedIndex--;
            exerciseAdapter.notifyDataSetChanged();
            exercisesListView.setSelection(lastSelectedIndex);
        }
    }

    public void onClickMoveDownButton(View view) {
        if(lastSelectedIndex < exercisesListView.getCount()-1) {
            Exercise tempExercise = trainingProgram.getExercises().get(lastSelectedIndex);
            Exercise tempExercise2 = trainingProgram.getExercises().get(lastSelectedIndex+1);
            trainingProgram.getExercises().set(lastSelectedIndex, tempExercise2);
            trainingProgram.getExercises().set(lastSelectedIndex+1, tempExercise);
            lastSelectedIndex++;
            exerciseAdapter.notifyDataSetChanged();
            exercisesListView.setSelection(lastSelectedIndex);
        }
    }

    public void checkIfEditTextIsFocused(View view){
        if(editTextIsFocused){
            trainingProgramNameEditText.clearFocus();
        }
        moveUpButton.setVisibility(View.INVISIBLE);
        moveDownButton.setVisibility(View.INVISIBLE);
    }

}
