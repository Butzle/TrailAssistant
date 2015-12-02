package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 21.11.15.
 */
public class GymExercise extends Exercise {
    private int duration;
    private int repetitions;
    private GYM_MODE gymMode;

    public GymExercise(int exerciseID, int duration, int repetitions, GYM_MODE gymMode) {
        super(exerciseID);
        this.gymMode = gymMode;
        this.duration = duration;
        this.repetitions = repetitions;
    }

    public String toString() {
        return "[Gym exercise] Name: Duration=" + duration + ", Repetitions=" + repetitions + ", Exercise mode=" + gymMode.toString();
    }

    public static GYM_MODE getGymModeFromInt(int speed_mode) {
        switch(speed_mode) {
            case 0: return GYM_MODE.STRETCHING;
            case 1: return GYM_MODE.TONING;
            default: return GYM_MODE.STRETCHING;
        }
    }
}
