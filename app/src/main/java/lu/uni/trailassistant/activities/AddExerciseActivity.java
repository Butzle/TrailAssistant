package lu.uni.trailassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import lu.uni.trailassistant.R;
import lu.uni.trailassistant.objects.Exercise;
import lu.uni.trailassistant.objects.GYM_MODE;
import lu.uni.trailassistant.objects.GymExercise;
import lu.uni.trailassistant.objects.RunningExercise;
import lu.uni.trailassistant.objects.SPEED_MODE;

/*
    Spinner: http://developer.android.com/guide/topics/ui/controls/spinner.html
 */

public class AddExerciseActivity extends AppCompatActivity {
    private HashMap<Integer, Exercise> exercisesToBeAdded;
    private int currentExerciseIndex = 0;

    private TextView distanceTextView, durationTextView, repetitionsTextView, speedModeTextView, gymModeTextView, totalRemainingDistance;
    private Spinner spinnerExerciseMode, speedModeSpinner, gymModeSpinner;
    private EditText distanceEditText, durationEditText, repetitionsEditText;
    private Button addButton, finishButton;
    private int  totalRemainingDistanceInMeter;
    private String totalRemainingDistanceToDefine;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        totalRemainingDistanceInMeter = (int)(getIntent().getDoubleExtra("totalDistanceInMeter", 0)+ 0.5d);
        totalRemainingDistance = (TextView) findViewById(R.id.total_remaining_distance);
        totalRemainingDistanceToDefine = totalRemainingDistance.getText().toString();
        totalRemainingDistance.setText(totalRemainingDistance.getText() +" "+ totalRemainingDistanceInMeter);

        // Enable finish button only when entire distance is defined
        finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setEnabled(false);

        // set references to our widgets
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        distanceEditText = (EditText) findViewById(R.id.distanceEditText);
        durationTextView = (TextView) findViewById(R.id.durationTextView);
        durationEditText = (EditText) findViewById(R.id.durationEditText);
        repetitionsTextView = (TextView) findViewById(R.id.repetitionsTextView);
        repetitionsEditText = (EditText) findViewById(R.id.repetitionsEditText);
        speedModeTextView = (TextView) findViewById(R.id.speedModeTextView);
        speedModeSpinner = (Spinner) findViewById(R.id.speedModeSpinner);
        gymModeTextView = (TextView) findViewById(R.id.gymModeTextView);
        gymModeSpinner = (Spinner) findViewById(R.id.gymModeSpinner);
        spinnerExerciseMode = (Spinner) findViewById(R.id.spinner_exercise_mode);
        addButton = (Button) findViewById(R.id.addButton);


        // Create ArrayAdapters using the string array/enums and a default spinner layout
        speedModeSpinner.setAdapter(new ArrayAdapter<SPEED_MODE>(this, android.R.layout.simple_spinner_item, SPEED_MODE.values()));
        gymModeSpinner.setAdapter(new ArrayAdapter<GYM_MODE>(this, android.R.layout.simple_spinner_item, GYM_MODE.values()));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exercise_mode, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerExerciseMode.setAdapter(adapter);

        // determine request code from intent (are we modifying an exercise or adding new ones?)
        //requestCode = getIntent().getIntExtra("request_code",0);
        //if(getIntent().)

        // initialize the linked list that will contain the new exercises
        exercisesToBeAdded = new HashMap<Integer, Exercise>();

        // add an onItemClickListener to the main spinner that chooses the type of exercises we want, necessary to enable/disable correct views
        spinnerExerciseMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (parent.getSelectedItem().toString().equals("Running")) {
                    distanceTextView.setVisibility(View.VISIBLE);
                    distanceEditText.setVisibility(View.VISIBLE);
                    durationTextView.setVisibility(View.GONE);
                    durationEditText.setVisibility(View.GONE);
                    repetitionsTextView.setVisibility(View.GONE);
                    repetitionsEditText.setVisibility(View.GONE);
                    speedModeTextView.setVisibility(View.VISIBLE);
                    speedModeSpinner.setVisibility(View.VISIBLE);
                    gymModeTextView.setVisibility(View.GONE);
                    gymModeSpinner.setVisibility(View.GONE);
                    if (checkRunningExerciseConditions()) {
                        addButton.setEnabled(true);
                    } else {
                        addButton.setEnabled(false);
                    }
                } else if (parent.getSelectedItem().toString().equals("Gym")) {
                    distanceTextView.setVisibility(View.GONE);
                    distanceEditText.setVisibility(View.GONE);
                    durationTextView.setVisibility(View.VISIBLE);
                    durationEditText.setVisibility(View.VISIBLE);
                    repetitionsTextView.setVisibility(View.VISIBLE);
                    repetitionsEditText.setVisibility(View.VISIBLE);
                    speedModeTextView.setVisibility(View.GONE);
                    speedModeSpinner.setVisibility(View.GONE);
                    gymModeTextView.setVisibility(View.VISIBLE);
                    gymModeSpinner.setVisibility(View.VISIBLE);
                    if (checkGymExerciseConditions()) {
                        addButton.setEnabled(true);
                    } else {
                        addButton.setEnabled(false);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // if we change the contents in any of the EditTexts, then check if we should enable/disable the "ADD" button if the conditions are met
        distanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (checkRunningExerciseConditions()) {
                    if( totalRemainingDistanceInMeter - Integer.parseInt(distanceEditText.getText().toString()) > 0) {
                        addButton.setEnabled(true);
                    }else if (totalRemainingDistanceInMeter - Integer.parseInt(distanceEditText.getText().toString()) < 0) {
                        addButton.setEnabled(false);
                    }
                } else {
                    addButton.setEnabled(false);
                }
            }
        });
        durationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (checkGymExerciseConditions()) {
                    addButton.setEnabled(true);
                } else {
                    addButton.setEnabled(false);
                }
            }
        });
        repetitionsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (checkGymExerciseConditions()) {

                    addButton.setEnabled(true);
                } else {
                    addButton.setEnabled(false);
                }
            }
        });
        durationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (checkGymExerciseConditions()) {
                    addButton.setEnabled(true);
                } else {
                    addButton.setEnabled(false);
                }
            }
        });

        speedModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(checkRunningExerciseConditions()) {
                    addButton.setEnabled(true);
                } else {
                    addButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        gymModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(checkGymExerciseConditions()) {
                    addButton.setEnabled(true);
                } else {
                    addButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void onClickAddButton(View view) {
        // add Exercise to the LinkedList
        if(spinnerExerciseMode.getSelectedItem().toString().equals("Running")) {
            RunningExercise runningExercise = new RunningExercise(0, Integer.parseInt(distanceEditText.getText().toString()), (SPEED_MODE)speedModeSpinner.getSelectedItem());
            exercisesToBeAdded.put(currentExerciseIndex, runningExercise);
            currentExerciseIndex++;
            totalRemainingDistanceInMeter -= Integer.parseInt(distanceEditText.getText().toString());
            totalRemainingDistance.setText(totalRemainingDistanceToDefine +" "+ totalRemainingDistanceInMeter);
            if(totalRemainingDistanceInMeter == 0){
                addButton.setEnabled(false);
                finishButton.setEnabled(true);
            }
            distanceEditText.setText("");

            Toast.makeText(this, "Running exercise was created successfully!", Toast.LENGTH_SHORT).show();
        } else if(spinnerExerciseMode.getSelectedItem().toString().equals("Gym")) {
            addButton.setEnabled(true);
            if(totalRemainingDistanceInMeter == 0){
                finishButton.setEnabled(true);
            }
            GymExercise gymExercise = new GymExercise(0, Integer.parseInt(durationEditText.getText().toString()), Integer.parseInt(repetitionsEditText.getText().toString()), (GYM_MODE)gymModeSpinner.getSelectedItem());
            exercisesToBeAdded.put(currentExerciseIndex, gymExercise);
            currentExerciseIndex++;
            Toast.makeText(this, "Gym exercise was created successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickFinishButton(View view) {
        Intent parentIntent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("exercises_to_be_added", exercisesToBeAdded);
        bundle.putInt("amount_of_exercises", currentExerciseIndex);
        parentIntent.putExtras(bundle);
        setResult(RESULT_OK, parentIntent);
        finish();
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private boolean checkRunningExerciseConditions() {
        return !isEmpty(distanceEditText) && speedModeSpinner.getSelectedItemPosition() != -1;
    }

    private boolean checkGymExerciseConditions() {
        return !isEmpty(durationEditText) && !isEmpty(repetitionsEditText) && gymModeSpinner.getSelectedItemPosition() != -1;
    }
}
