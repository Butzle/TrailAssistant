package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 21.11.15.
 */
public class GymExercise extends Exercise {
    private int duration, repetitions;
    private GYM_MODE gymMode;

    public GymExercise(int exerciseID, int duration, int repetitions, GYM_MODE gymMode) {
        super(exerciseID);
        this.gymMode = gymMode;
        this.duration = duration;
        this.repetitions = repetitions;
    }

    public GYM_MODE getGymMode() { return gymMode; }
    public int getDuration() { return duration; }
    public int getRepetitions() { return repetitions; }

    public void setDuration(int duration) { this.duration = duration; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public String toString() {
        String str = "";
        switch(gymMode) {
            case STRETCHING:
                str = str + "Stretching exercise";
                break;
            default:
                str = str + "Toning exercise";
                break;
        }
        if(repetitions == 0) {
            str = str + " for " + duration + " seconds";
        } else if(duration == 0) {
            str = str + ", " + repetitions + " repetitions";
        } else {
            str = str + ", " + repetitions + " repetitions for " + duration + " seconds";
        }
        return str;
    }

    public static GYM_MODE getGymModeFromInt(int speed_mode) {
        switch(speed_mode) {
            case 0: return GYM_MODE.STRETCHING;
            case 1: return GYM_MODE.TONING;
            default: return GYM_MODE.STRETCHING;
        }
    }
}
