package lu.uni.trailassistant.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GOMES Leandro on 07.11.15.
 *
 */
public abstract class Exercise implements Parcelable {
    protected int exerciseID;

    public Exercise(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    protected Exercise(Parcel in) {
        exerciseID = in.readInt();
    }

    public int getExerciseID() { return exerciseID; }

    @Override
    public abstract String toString();

    // base Parcelable implementation (rest is done in inherited classes)
    // this is needed so that we can serialize this object in order to send it through Bundles to other activities
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(exerciseID);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
