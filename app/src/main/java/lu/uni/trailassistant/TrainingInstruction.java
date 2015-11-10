package lu.uni.trailassistant;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 */
public class TrainingInstruction {
    public enum TrainingMode {SPRINT, WALK, NORMAL, STRETCH}

    // running distance
    private int distance;
    private int duration = 0;
    private TrainingMode trainingMode;
}
