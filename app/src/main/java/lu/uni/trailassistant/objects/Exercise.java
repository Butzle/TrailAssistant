package lu.uni.trailassistant.objects;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 */
public abstract class Exercise {
    protected EXERCISE_MODE exerciseMode;

    public Exercise(EXERCISE_MODE exerciseMode) {
        this.exerciseMode = exerciseMode;
    }

    public EXERCISE_MODE getExerciseMode() {
        return exerciseMode;
    }

    public void setExerciseMode(EXERCISE_MODE exerciseMode) {
        this.exerciseMode = exerciseMode;
    }

    @Override
    public abstract String toString();
}
