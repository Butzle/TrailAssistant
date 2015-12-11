package lu.uni.trailassistant.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leandrogil on 11/19/15.
 */
public class RunningExercise extends Exercise {

    private SPEED_MODE speedMode;
    private int distance, duration;

    public RunningExercise(int exerciseID, int duration, int distance, SPEED_MODE speedMode) {
        super(exerciseID);
        this.speedMode = speedMode;
        this.duration = duration;
        this.distance = distance;
    }

    public SPEED_MODE getSpeedMode() { return speedMode; }
    public int getDistance() { return distance; }
    public int getDuration() { return duration; }

    public void setSpeedMode(SPEED_MODE speedMode) { this.speedMode = speedMode; }
    public void setDistance(int distance) { this.distance = distance; }
    public void setDuration(int duration) { this.duration = duration; }

    public String toString() {
        String str = "";
        switch(speedMode) {
            case FAST_WALK:
                str = str + "Fast walk";
                break;
            case NORMAL:
                str = str + "Run normally";
                break;
            case SPRINT:
                str = str + "Sprint";
                break;
            default:
                str = str + "Walk and breathe";
                break;
        }
        if(duration == 0) {
            str = str + " for " + distance + "m";
        } else if (distance == 0) {
            str = str + " for " + duration + " seconds";
        } else {
            str = str + " for " + distance + "m and " + duration + "seconds";
        }
        return str;
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

    // Parcelable implementation
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(speedMode.ordinal());
        out.writeInt(distance);
        out.writeInt(duration);
    }
    public static final Creator<RunningExercise> CREATOR = new Parcelable.Creator<RunningExercise>() {
        public RunningExercise createFromParcel(Parcel in) {
            int exerciseID = in.readInt();
            SPEED_MODE speedMode = getSpeedModeFromInt(in.readInt());
            int distance = in.readInt();
            int duration = in.readInt();
            return new RunningExercise(exerciseID, duration, distance, speedMode);
        }

        public RunningExercise[] newArray(int size) {
            return new RunningExercise[size];
        }
    };
}