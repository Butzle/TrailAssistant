package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 29.11.15.
 */
public class HistoryRecord {
    int historyRecordID, time, caloriesBurned;
    public HistoryRecord(int historyRecordID, int time, int caloriesBurned) {
        this.historyRecordID = historyRecordID;
        this.time = time;
        this.caloriesBurned = caloriesBurned;
    }

    public int getHistoryRecordID() { return historyRecordID; }
    public int getTime() { return time; }
    public int getCaloriesBurned() { return caloriesBurned; }

    public void setTime(int time) { this.time = time; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
}
