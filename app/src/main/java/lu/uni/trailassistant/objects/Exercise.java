package lu.uni.trailassistant.objects;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 */
public abstract class Exercise {
    protected int exerciseID;

    public Exercise(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getExerciseID() { return exerciseID; }

    @Override
    public abstract String toString();
}
