package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 11/19/15.
 */
public class RunningExercise extends Exercise {
    public enum SPEED_MODE {FAST_WALK, WALK_AND_BREATH, NORMAL, SPRINT};

    private SPEED_MODE speedMode;
    private int distance;
    private GPSCoord destinationCoord;

    public RunningExercise(String exerciseName, int distance, GPSCoord destinationCoord) {
        super(exerciseName);
        this.distance = distance;
        this.destinationCoord = destinationCoord;
    }

    public String toString() {
        return "[Running exercise] Name: "+ exerciseName + ", Distance=" + distance + ", Speed mode=" + speedMode + ", Destination GPS coordinates: (" + destinationCoord.toString() + ")";
    }
}