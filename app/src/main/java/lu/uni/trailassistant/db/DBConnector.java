package lu.uni.trailassistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Iterator;
import java.util.ListIterator;

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
        // enable foreign key constraint checks
        //trailAssistantDB.setForeignKeyConstraintsEnabled(true);
    }

    public void closeConnection() {
        trailAssistantDB.close();
    }

    public DBConnector(Context context) {
        sqliteHelper = new TrailAssistantDBBuilder(context);
    }

    private RunningExercise getRunningExerciseFromCursor(Cursor cursor) {
        int runningExerciseID = cursor.getInt(0);
        int distance = cursor.getInt(1);
        SPEED_MODE speedMode = RunningExercise.getSpeedModeFromInt(cursor.getInt(2));
        RunningExercise runningExercise = new RunningExercise(runningExerciseID, distance, speedMode);
        return runningExercise;
    }

    private GymExercise getGymExerciseFromCursor(Cursor cursor) {
        int gymExerciseID = cursor.getInt(0);
        int duration = cursor.getInt(1);
        int repetitions = cursor.getInt(2);
        GYM_MODE gymMode = GymExercise.getGymModeFromInt(cursor.getInt(3));
        GymExercise gymExercise = new GymExercise(gymExerciseID, duration, repetitions, gymMode);
        return gymExercise;
    }

    private GPSCoord getGPSCoordFromCursor(Cursor cursor) {
        int gpsCoordID = cursor.getInt(0);
        double longitude = cursor.getDouble(1);
        double lattitude = cursor.getDouble(2);
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
                                        "order by GPSCoord.coord_order asc";
        cursor = trailAssistantDB.rawQuery(queryStringGPSCoords, queryArgs);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            GPSCoord gpsCoord = getGPSCoordFromCursor(cursor);
            tp.appendGPSCoordToTrail(gpsCoord);
            cursor.moveToNext();
        }
        cursor.close();

        // finally, retrieve exercises that belong to this training program (first the running exercises, then the gym exercises)
        String queryStringRunningExercises = "select * from RunningExercise " +
                                        "inner join TrainingProgram on RunningExercise.fkey_training_program_id=TrainingProgram._id " +
                                        "where TrainingProgram._id=? " +
                                        "order by RunningExercise.exercise_order asc";
        String queryStringGymExercises =   "select * from GymExercise " +
                                        "inner join TrainingProgram on GymExercise.fkey_training_program_id=TrainingProgram._id " +
                                        "where TrainingProgram._id=? " +
                                        "order by GymExercise.exercise_order asc";
        Cursor runningExerciseCursor = trailAssistantDB.rawQuery(queryStringRunningExercises, queryArgs);
        runningExerciseCursor.moveToFirst();
        Cursor gymExerciseCursor = trailAssistantDB.rawQuery(queryStringGymExercises, queryArgs);
        gymExerciseCursor.moveToFirst();
        int currentCursor=1;
        while(!runningExerciseCursor.isAfterLast() || !gymExerciseCursor.isAfterLast()) {
            // add exercises in correct order to the training program, one by one
            if(!runningExerciseCursor.isAfterLast() && runningExerciseCursor.getInt(3)==currentCursor) {
                tp.appendExerciseToTail(getRunningExerciseFromCursor(runningExerciseCursor));
                runningExerciseCursor.moveToNext();
                currentCursor++;
            }
            if(!gymExerciseCursor.isAfterLast() && gymExerciseCursor.getInt(4)==currentCursor) {
                tp.appendExerciseToTail(getGymExerciseFromCursor(gymExerciseCursor));
                gymExerciseCursor.moveToNext();
                currentCursor++;
            }
        }
        runningExerciseCursor.close();
        gymExerciseCursor.close();
        return tp;
    }

    public Cursor getTrainingProgramCursor() {
        String queryString = "select _id, name from TrainingProgram";
        return trailAssistantDB.rawQuery(queryString, null);
    }

    public int writeTrainingProgramToDB(TrainingProgram tp) {
        int lastTrainingProgramID = 0;
        trailAssistantDB.beginTransaction();
        try {
            // write the new training program to DB
            ContentValues cv = new ContentValues();
            cv.put("name", tp.getProgramName());
            trailAssistantDB.insert("TrainingProgram", null, cv);

            // retrieve ID of the training program that was just added
            String retrieveTrainingProgramID = "select _id from TrainingProgram order by _id desc limit 1";
            Cursor cursor = trailAssistantDB.rawQuery(retrieveTrainingProgramID, null);
            cursor.moveToFirst();
            lastTrainingProgramID = cursor.getInt(0);
            cursor.close();

            // insert exercises into their respective tables, using the training program ID as a foreign key reference
            ListIterator<Exercise> exerciseIterator = tp.getExercisesAsListIterator();
            int order = 1;
            while (exerciseIterator.hasNext()) {
                Exercise exercise = exerciseIterator.next();
                if (exercise instanceof RunningExercise) {
                    RunningExercise runningExercise = (RunningExercise) exercise;
                    ContentValues runningExerciseContentValues = new ContentValues();
                    runningExerciseContentValues.put("distance", runningExercise.getDistance());
                    runningExerciseContentValues.put("speed_mode", runningExercise.getSpeedMode().ordinal());
                    runningExerciseContentValues.put("exercise_order", order);
                    runningExerciseContentValues.put("fkey_training_program_id", lastTrainingProgramID);
                    trailAssistantDB.insert("RunningExercise", null, runningExerciseContentValues);
                } else if (exercise instanceof GymExercise) {
                    GymExercise gymExercise = (GymExercise) exercise;
                    ContentValues gymExerciseContentValues = new ContentValues();
                    gymExerciseContentValues.put("duration", gymExercise.getDuration());
                    gymExerciseContentValues.put("repetitions", gymExercise.getRepetitions());
                    gymExerciseContentValues.put("gym_mode", gymExercise.getGymMode().ordinal());
                    gymExerciseContentValues.put("exercise_order", order);
                    gymExerciseContentValues.put("fkey_training_program_id", lastTrainingProgramID);
                    trailAssistantDB.insert("GymExercise", null, gymExerciseContentValues);
                }
                order++;
            }

            // insert GPS coordinates
            ListIterator<GPSCoord> gpsCoordIterator = tp.getGPSCoordsAsListIterator();
            order = 1;
            while (gpsCoordIterator.hasNext()) {
                GPSCoord gpsCoord = gpsCoordIterator.next();
                ContentValues gpsCoordContentValues = new ContentValues();
                gpsCoordContentValues.put("longitude", gpsCoord.getLongitude());
                gpsCoordContentValues.put("lattitude", gpsCoord.getLattitude());
                gpsCoordContentValues.put("coord_order", order);
                gpsCoordContentValues.put("fkey_training_program_id", lastTrainingProgramID);
                trailAssistantDB.insert("GPSCoord", null, gpsCoordContentValues);
                order++;
            }
            trailAssistantDB.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(DBConnector.class.getName(), "Error occured during insert transaction! Changes will be rolled back.");
        } finally {
            trailAssistantDB.endTransaction();
        }
        return lastTrainingProgramID;
    }

    public void updateExistingTrainingProgram(TrainingProgram tp) {
        trailAssistantDB.beginTransaction();
        try {
            // update training program name
            String whereClause = "_id=?";
            String whereArgs[] = new String[]{Integer.toString(tp.getTrainingProgramID())};
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", tp.getProgramName());
            trailAssistantDB.update("TrainingProgram", contentValues, whereClause, whereArgs);

            // update exercises
            Iterator<Exercise> iterator = tp.getExercisesAsListIterator();
            int order=1;
            while (iterator.hasNext()) {
                Exercise tempExercise = iterator.next();
                if (tempExercise instanceof RunningExercise) {
                    RunningExercise runningExercise = (RunningExercise) tempExercise;
                    contentValues = new ContentValues();
                    contentValues.put("distance", runningExercise.getDistance());
                    contentValues.put("speed_mode", runningExercise.getSpeedMode().ordinal());
                    contentValues.put("exercise_order", order);
                    if (runningExercise.getExerciseID() > 0) {
                        // if the exercise has an ID that is not 0, then it already has a record in the database
                        whereClause = "_id=?";
                        whereArgs = new String[]{Integer.toString(runningExercise.getExerciseID())};
                        trailAssistantDB.update("RunningExercise", contentValues, whereClause, whereArgs);
                    } else {
                        // otherwise, create a new record with a new ID for that exercise and add reference to the correct training program
                        contentValues.put("fkey_training_program_id", tp.getTrainingProgramID());
                        trailAssistantDB.insert("RunningExercise", null, contentValues);
                    }
                } else {
                    GymExercise gymExercise = (GymExercise) tempExercise;
                    contentValues = new ContentValues();
                    contentValues.put("duration", gymExercise.getDuration());
                    contentValues.put("repetitions", gymExercise.getRepetitions());
                    contentValues.put("gym_mode", gymExercise.getGymMode().ordinal());
                    contentValues.put("exercise_order", order);
                    if (gymExercise.getExerciseID() > 0) {
                        // if the exercise has an ID that is not 0, then it already has a record in the database
                        whereClause = "_id=?";
                        whereArgs = new String[]{Integer.toString(gymExercise.getExerciseID())};
                        trailAssistantDB.update("GymExercise", contentValues, whereClause, whereArgs);
                    } else {
                        // otherwise, create a new record with a new ID for that exercise and add reference to the correct training program
                        contentValues.put("fkey_training_program_id", tp.getTrainingProgramID());
                        trailAssistantDB.insert("GymExercise", null, contentValues);
                    }
                }
                order++;
            }

            // update GPS coordinates
            Iterator<GPSCoord> gpsCoordsIterator = tp.getGPSCoordsAsListIterator();
            order=1;
            while(iterator.hasNext()) {
                GPSCoord gpsCoord = gpsCoordsIterator.next();
                contentValues = new ContentValues();
                contentValues.put("lattitude", gpsCoord.getLattitude());
                contentValues.put("longitude", gpsCoord.getLongitude());
                contentValues.put("coord_order", order);
                contentValues.put("fkey_training_program_id", tp.getTrainingProgramID());
                if(gpsCoord.getGPSCoordID() > 0) {
                    // if the GPS coordinate has an ID that is not 0, then it already has a record in the database
                    whereClause = "_id=?";
                    whereArgs = new String[]{Integer.toString(gpsCoord.getGPSCoordID())};
                    trailAssistantDB.update("GPSCoord", contentValues, whereClause, whereArgs);
                } else {
                    // otherwise, create a new record with a new ID for that GPS coordinate and add reference to the correct training program
                    contentValues.put("fkey_training_program_id", tp.getTrainingProgramID());
                    trailAssistantDB.insert("GPSCoord", null, contentValues);
                }
                order++;
            }

            trailAssistantDB.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(DBConnector.class.getName(), "Error occured during update transaction! Changes will be rolled back.");
        } finally {
            trailAssistantDB.endTransaction();
        }
    }
}
