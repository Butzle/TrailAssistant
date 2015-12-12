package lu.uni.trailassistant.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.db.DBConnector;
import lu.uni.trailassistant.objects.TrainingProgram;

/**
 * Created by Jo on 11/12/15.
 */
public class NewTrainingProgramActivity extends AppCompatActivity {
    TrainingProgram trainingProgram;
    EditText trainingProgramNameEditText;
    ArrayList<Double> latitudes;
    ArrayList<Double> longitutdes;
    Double totalDistanceInMeter;

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
        longitutdes = (ArrayList<Double>)getIntent().getSerializableExtra("longitutdesArrayList");
        totalDistanceInMeter = getIntent().getDoubleExtra("totalDistanceInMeter", 0);

        if(latitudes != null && longitutdes != null && totalDistanceInMeter != 0) {
            // TODO create new training program
            //trainingProgram = new TrainingProgram(0);
        }
    }

    public void onClickSaveButton(View view) {
        DBConnector dbc = new DBConnector(this);
        dbc.openConnection();
        trainingProgram.setTrainingProgramName(trainingProgramNameEditText.getText().toString());
        dbc.updateExistingTrainingProgram(trainingProgram);
        dbc.closeConnection();
        finish();
        Toast.makeText(this, "New Training Program successfully created!", Toast.LENGTH_SHORT).show();
    }
}
