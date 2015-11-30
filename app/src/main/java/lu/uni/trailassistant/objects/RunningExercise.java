package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 11/19/15.
 */
public class RunningExercise extends Exercise {

    private SPEED_MODE speedMode;
    private int distance;
    private GPSCoord destinationCoord;

    public RunningExercise(EXERCISE_MODE exerciseMode, SPEED_MODE speedMode, int distance, GPSCoord destinationCoord) {
        super(exerciseMode);
        this.speedMode = speedMode;
        this.distance = distance;
        this.destinationCoord = destinationCoord;
    }

    public String toString() {
        return "[Running exercise] Mode: Distance=" + distance + ", Speed mode=" + speedMode + ", Destination GPS coordinates: (" + destinationCoord.toString() + ")";
    }
}
