package lu.uni.trailassistant.objects;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 * Simple wrapper class that implements a queue of activities
 */
public class TrainingProgram {
    private int trainingProgramID;
    private String programName;
    // queue that contains all of the instructions belonging to a training program
    private LinkedList<Exercise> exercisesQueue;
    private boolean showTrainingInstructions;   //
    // another queue that represents our GPS trail
    private LinkedList<GPSCoord> gpsCoords;

    public TrainingProgram(int trainingProgramID, String programName) {
        this.trainingProgramID = trainingProgramID;
        this.programName = programName;
        exercisesQueue = new LinkedList<Exercise>();
        showTrainingInstructions = false;
        gpsCoords = new LinkedList<GPSCoord>();
    }

    public TrainingProgram() {
        trainingProgramID = 0;      // NOTE: an ID equal to 0 indicates that this program has just been created and does not have a row in the appropriate table in the database
        programName = "";
        exercisesQueue = new LinkedList<Exercise>();
        showTrainingInstructions = false;
        gpsCoords = new LinkedList<GPSCoord>();
    }
    
    public String getProgramName(){ return programName; }
    public LinkedList<Exercise> getExercises() { return exercisesQueue; }
    public GPSCoord getFirstGPSCoord() { return gpsCoords.getFirst(); }
    public GPSCoord getLastGPSCoord() { return gpsCoords.getLast(); }
    public int getTrainingProgramID() { return trainingProgramID; }

    public void setTrainingProgramName(String programName) { this.programName = programName; }
    
    public void appendExerciseToTail(Exercise exercise) {
        exercisesQueue.addLast(exercise);
    }
    public void appendExerciseToHead(Exercise exercise) {
        exercisesQueue.addFirst(exercise);
    }
    public void appendGPSCoordToTrail(GPSCoord gpsCoord) {
        gpsCoords.addLast(gpsCoord);
    }

    // replaces an activity in the queue with another one (can be used to modify an activity as well)
    public void replaceExercise(int index, Exercise newActivity) {
        exercisesQueue.set(index, newActivity);
    }

    public void removeExercise(int index) {
        exercisesQueue.remove(index);
    }

    // returns a ListIterator object that can be used to traverse through the exercises in proper order
    public ListIterator<Exercise> getExercisesAsListIterator() {
        return exercisesQueue.listIterator();
    }

    // returns a ListIterator object that can be used to traverse through the GPS coordinates in proper order
    public ListIterator<GPSCoord> getGPSCoordsAsListIterator() {
        return gpsCoords.listIterator();
    }
}