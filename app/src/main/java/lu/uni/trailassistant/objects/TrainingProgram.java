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
    private boolean showTrainingInstructions;   // TODO: do we still need this? not sure...
    // another queue that represents our GPS trail
    private LinkedList<GPSCoord> gpsCoords;

    public TrainingProgram(int trainingProgramID, String programName) {
        this.trainingProgramID = trainingProgramID;
        this.programName = programName;
        exercisesQueue = new LinkedList<Exercise>();
        showTrainingInstructions = false;
        gpsCoords = new LinkedList<GPSCoord>();
    }
    
    public String getProgramName(){ return programName; }
    public GPSCoord getFirstGPSCoord() { return gpsCoords.getFirst(); }
    public GPSCoord getLastGPSCoord() { return gpsCoords.getLast(); }

    public void setProgramName(String name) { this.programName = programName; }
    
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
    // (TODO: maybe introduce own modify() method just for modifications? might be more efficient)
    public void replaceExercise(int index, Exercise newActivity) {
        exercisesQueue.set(index, newActivity);
    }

    public void removeExercise(int index) {
        exercisesQueue.remove(index);
    }

    // returns a ListIterator object that can be used to traverse through the queue in proper order
    public ListIterator<Exercise> getQueueAsListIterator() {
        return exercisesQueue.listIterator();
    }
}