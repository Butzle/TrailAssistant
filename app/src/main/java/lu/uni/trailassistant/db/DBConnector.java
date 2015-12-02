package lu.uni.trailassistant.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lu.uni.trailassistant.objects.GYM_MODE;
import lu.uni.trailassistant.objects.Exercise;
import lu.uni.trailassistant.objects.GPSCoord;
import lu.uni.trailassistant.objects.GymExercise;
import lu.uni.trailassistant.objects.HistoryRecord;
import lu.uni.trailassistant.objects.RunningExercise;
import lu.uni.trailassistant.objects.SPEED_MODE;
import lu.uni.trailassistant.objects.TrainingProgram;

/**
 * Created by leandrogil on 27.11.15.
 * This class communicates with the SQLite database and provides various methods for
 * writing, modifying, deleting and retrieving rows from the respective tables in the database.
 */
public class DBConnector {
    /* Singleton implementation (not possible atm without requiring the Context as a parameter to getInstance() )
    private static DBConnector instance;
    private DBConnector(Context context) {
        // private constructor prevents instantiation by other classes
    }

    public static DBConnector getInstance() {
        if(instance != null) {
            instance = new DBConnector();
        }
        return instance;
    }
       ======================== */

    private TrailAssistantDBBuilder sqliteHelper;
    private SQLiteDatabase trailAssistantDB;

    public void openConnection() {
        trailAssistantDB = sqliteHelper.getWritableDatabase();
        // enable foreign key constraints checks
        //trailAssistantDB.setForeignKeyConstraintsEnabled(true);
    }

    public void closeConnection() {
        trailAssistantDB.close();
    }

    public DBConnector(Context context) {
        sqliteHelper = new TrailAssistantDBBuilder(context);
    }

    private Exercise getExerciseFromCursor(Cursor cursor) {
        int exerciseID = cursor.getInt(0);
        int type =  cursor.getInt(1);
        Exercise exercise;
        // type 0 = RunningExercise, type 1 = GymExercise
        if(type == 0) {
            int distance = cursor.getInt(4);
            SPEED_MODE speedMode = RunningExercise.getSpeedModeFromInt(cursor.getInt(5));
            exercise = new RunningExercise(exerciseID, speedMode, distance);
        } else {
            int repetitions = cursor.getInt(2);
            int duration = cursor.getInt(3);
            GYM_MODE gymMode = GymExercise.getGymModeFromInt(cursor.getInt(7));
            exercise = new GymExercise(exerciseID, duration, repetitions, gymMode);
        }
        return exercise;
    }

    private GPSCoord getGPSCoordFromCursor(Cursor cursor) {
        int gpsCoordID = cursor.getInt(0);
        float longitude = cursor.getFloat(1);
        float lattitude = cursor.getFloat(2);
        GPSCoord gpsCoord = new GPSCoord(gpsCoordID, longitude, lattitude);
        return gpsCoord;
    }

    private HistoryRecord getHistoryRecordFromCursor(Cursor cursor) {
        int historyRecordID = cursor.getInt(0);
        int time = cursor.getInt(1);
        int caloriesBurned = cursor.getInt(2);
        HistoryRecord historyRecord = new HistoryRecord(historyRecordID, time, caloriesBurned);
        return historyRecord;
    }

    public int[] getAllTrainingProgramIDs() {
        String queryString = "select _id from TrainingProgram";
        Cursor cursor = trailAssistantDB.rawQuery(queryString, null);
        int totalAmountOfTPs = cursor.getCount();
        if(totalAmountOfTPs == 0) {
            return null;
        }
        int IDs[] = new int[totalAmountOfTPs];
        cursor.moveToFirst();
        for(int i=0; i<totalAmountOfTPs; i++) {
            IDs[i] = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        return IDs;
    }

    public TrainingProgram getTrainingProgramFromID(int trainingProgramID) {
        // first, retrieve training program from DB
        String queryStringTrainingProgram = "select * from TrainingProgram " +
                                            "where TrainingProgram._id=?";
        String queryArgs[] = {Integer.toString(trainingProgramID)};
        Cursor cursor = trailAssistantDB.rawQuery(queryStringTrainingProgram, queryArgs);
        if(cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        TrainingProgram tp = new TrainingProgram(trainingProgramID, cursor.getString(1));
        cursor.close();

        // then, retrieve GPS coordinates that represent the trail for that particular training program
        String queryStringGPSCoords =   "select * from GPSCoord " +
                                        "inner join TrainingProgram on GPSCoord.fkey_training_program_id=TrainingProgram._id " +
                                        "where TrainingProgram._id=? " +
                                        "order by GPSCoord.coord_order ascending";
        cursor = trailAssistantDB.rawQuery(queryStringGPSCoords, queryArgs);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            GPSCoord gpsCoord = getGPSCoordFromCursor(cursor);
            tp.appendGPSCoordToTrail(gpsCoord);
            cursor.moveToNext();
        }
        cursor.close();

        // finally, retrieve exercises that belong to this training program
        String queryStringExercises =   "select * from Exercise " +
                                        "inner join TrainingProgram on Exercise.fkey_training_program_id=TrainingProgram " +
                                        "where TrainingProgram._id=? " +
                                        "order by Exercise.exercise_order ascending";
        cursor = trailAssistantDB.rawQuery(queryStringExercises, queryArgs);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Exercise exercise = getExerciseFromCursor(cursor);
            tp.appendExerciseToTail(exercise);
            cursor.moveToNext();
        }
        cursor.close();
        return tp;
    }

    public Cursor getTrainingProgramCursor() {
        String queryString = "select _id, name from TrainingProgram";
        return trailAssistantDB.rawQuery(queryString, null);
    }
}
