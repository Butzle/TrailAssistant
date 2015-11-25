package lu.uni.trailassistant.objects;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 */
public abstract class Exercise {
    protected String exerciseName;

    public Exercise(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    @Override
    public abstract String toString();
}
