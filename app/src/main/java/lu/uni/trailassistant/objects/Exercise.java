package lu.uni.trailassistant.objects;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 */
public abstract class Exercise {
    private String activityName;

    public Exercise(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    @Override
    public abstract String toString();
}
