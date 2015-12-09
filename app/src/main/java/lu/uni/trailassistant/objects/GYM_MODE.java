package lu.uni.trailassistant.objects;

/**
 * Created by Jo on 30/11/15.
 */
public enum GYM_MODE {
    STRETCHING("Stretching"),
    TONING("Toning");

    private String gymModeLabel;

    private GYM_MODE(String gymModeLabel) {
        this.gymModeLabel = gymModeLabel;
    }

    @Override
    public String toString() {
        return gymModeLabel;
    }
}
