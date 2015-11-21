package lu.uni.trailassistant.objects;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 * Simple wrapper class that implements a queue of activities
 */
public class TrainingProgram {
    // queue that contains all of the instructions belonging to a training program
    private LinkedList<Activity> activitiesQueue;
    private boolean showTrainingInstructions;   // TODO: do we still need this? not sure...
    GPSCoord startingCoord;

    public TrainingProgram() {
        activitiesQueue = new LinkedList<Activity>();
        showTrainingInstructions = false;
        startingCoord = null;
    }

    public void appendActivityToTail(Activity activity) {
        activitiesQueue.addLast(activity);
    }
    public void appendActivityToHead(Activity activity) {
        activitiesQueue.addFirst(activity);
    }

    // replaces an activity in the queue with another one (can be used to modify an activity as well)
    // (TODO: maybe introduce own modify() method just for modifications? might be more efficient)
    public void replaceActivity(int index, Activity newActivity) {
        activitiesQueue.set(index, newActivity);
    }

    public void removeActivity(int index) {
        activitiesQueue.remove(index);
    }

    // returns a ListIterator object that can be used to traverse through the queue in proper order
    public ListIterator<Activity> getQueueAsListIterator() {
        return activitiesQueue.listIterator();
    }
}