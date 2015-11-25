package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 21.11.15.
 */
public class ToningExercise extends Exercise {
    public enum EXERCISE_MODE {STRETCHING, TONING}
    private int duration;
    private int repetitions;
    private EXERCISE_MODE exerciseMode;

    public ToningExercise(String exerciseName, int duration, int repetitions, EXERCISE_MODE exerciseMode) {
        super(exerciseName);
        this.duration = duration;
        this.repetitions = repetitions;
        this.exerciseMode = exerciseMode;
    }

    public String toString() {
        return "[Toning exercise] Name: " + exerciseName + ", Duration=" + duration + ", Repetitions=" + repetitions + ", Exercise mode=" + exerciseMode;
    }
}
