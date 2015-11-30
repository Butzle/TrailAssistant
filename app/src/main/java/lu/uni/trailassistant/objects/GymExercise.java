package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 21.11.15.
 */
public class GymExercise extends Exercise {
    private int duration;
    private int repetitions;
    private EXERCISE_MODE exerciseMode;

    public GymExercise(int duration, int repetitions, EXERCISE_MODE exerciseMode) {
        super(exerciseMode);
        this.duration = duration;
        this.repetitions = repetitions;
    }

    public String toString() {
        return "[Toning exercise] Name: Duration=" + duration + ", Repetitions=" + repetitions + ", Exercise mode=" + exerciseMode;
    }
}
