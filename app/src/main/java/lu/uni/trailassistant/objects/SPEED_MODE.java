package lu.uni.trailassistant.objects;

/**
 * Created by Jo on 30/11/15.
 */

public enum SPEED_MODE {
    FAST_WALK("Fast and walk"),
    WALK_AND_BREATHE("Walk and breathe"),
    NORMAL("Normal"),
    SPRINT("Sprint");

    private String speedModeLabel;

    private SPEED_MODE(String speedModeLabel) {
        this.speedModeLabel = speedModeLabel;
    }

    @Override
    public String toString() {
        return speedModeLabel;
    }
}