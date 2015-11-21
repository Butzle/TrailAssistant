package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 21.11.15.
 */
public class ToningActivity extends Activity {
    public enum EXERCISE_MODE {STRETCHING, TONING}
    private int duration;
    private int repetitions;
    private EXERCISE_MODE exerciseMode;

    public ToningActivity(String activityName, int duration, int repetitions, EXERCISE_MODE exerciseMode) {
        super(activityName);
        this.duration = duration;
        this.repetitions = repetitions;
        this.exerciseMode = exerciseMode;
    }

    public String toString() {
        return "Duration=" + duration + ", Repetitions=" + repetitions + ", Exercise mode=" + exerciseMode;
    }
}
