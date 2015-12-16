package lu.uni.trailassistant.objects;

/**
 * Created by leandrogil on 16.12.15.
 */

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import org.junit.Test;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GymExerciseTest {
    @Test
    public void testToStringLogic() {
        // test the different logic paths of the method, first for Stretching
        GymExercise gymExercise = new GymExercise(1, 40, 0, GYM_MODE.STRETCHING);
        assertTrue(gymExercise.toString().equals("Stretching exercise for 40 seconds"));
        gymExercise.setRepetitions(34);
        assertTrue(gymExercise.toString().equals("Stretching exercise, 34 repetitions for 40 seconds"));
        gymExercise.setDuration(0);
        assertTrue(gymExercise.toString().equals("Stretching exercise, 34 repetitions"));

        // now for Toning
        gymExercise = new GymExercise(4, 0, 15, GYM_MODE.TONING);
        assertTrue(gymExercise.toString().equals("Toning exercise, 15 repetitions"));
        gymExercise.setDuration(120);
        assertTrue(gymExercise.toString().equals("Toning exercise, 15 repetitions for 120 seconds"));
        gymExercise.setRepetitions(0);
        assertTrue(gymExercise.toString().equals("Toning exercise for 120 seconds"));
    }

    @Test
    public void getGymModeFromIntShouldReturnCorrectGymMode() {
        assertEquals(GymExercise.getGymModeFromInt(GYM_MODE.STRETCHING.ordinal()), GYM_MODE.STRETCHING);
        assertEquals(GymExercise.getGymModeFromInt(GYM_MODE.TONING.ordinal()), GYM_MODE.TONING);
    }

    @Test
    public void verifyClassParcelable() {
        // construct Parcelable object
        GymExercise gymExercise = new GymExercise(2, 49, 22, GYM_MODE.STRETCHING);
        Parcel parcel = Parcel.obtain();
        gymExercise.writeToParcel(parcel, 0);

        // reset parcel for reading
        parcel.setDataPosition(0);

        // reconstruct original exercise from the Parcelable object
        GymExercise gymExercise2 = GymExercise.CREATOR.createFromParcel(parcel);
        assertEquals(gymExercise, gymExercise2);
    }
}
