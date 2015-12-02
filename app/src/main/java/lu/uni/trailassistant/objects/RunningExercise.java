package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 11/19/15.
 */
public class RunningExercise extends Exercise {

    private SPEED_MODE speedMode;
    private int distance;

    public RunningExercise(int exerciseID, SPEED_MODE speedMode, int distance) {
        super(exerciseID);
        this.speedMode = speedMode;
        this.distance = distance;
    }

    public SPEED_MODE getSpeedMode() { return speedMode; }
    public int getDistance() { return distance; }

    public void setSpeedMode(SPEED_MODE speedMode) { this.speedMode = speedMode; }
    public void setDistance(int distance) { this.distance = distance; }

    public String toString() {
        return "[Running exercise] Distance=" + distance + ", Speed mode=" + speedMode.toString();
    }

    public static SPEED_MODE getSpeedModeFromInt(int speed_mode) {
        switch(speed_mode) {
            case 0: return SPEED_MODE.FAST_WALK;
            case 1: return SPEED_MODE.WALK_AND_BREATHE;
            case 2: return SPEED_MODE.NORMAL;
            case 3: return SPEED_MODE.SPRINT;
            default: return SPEED_MODE.NORMAL;
        }
    }
}