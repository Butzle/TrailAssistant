package lu.uni.trailassistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by leandrogil on 26.11.15.
 * This class is in charge of creating and updating the SQLite database
 */
public class TrailAssistantDBBuilder extends SQLiteOpenHelper {
    // Database information
    private static final String DB_NAME = "trailassistant";
    private static final int DB_VERSION = 13;

    // RunningExercise table
    private static final String DB_RUNNING_EXERCISE_TABLE_NAME = "RunningExercise";
    private static final String DB_RUNNING_EXERCISE_ID = "_id integer primary key autoincrement";
    private static final String DB_RUNNING_EXERCISE_DISTANCE = "distance integer default 0";
    private static final String DB_RUNNING_EXERCISE_SPEED_MODE = "speed_mode tinyint not null";
    private static final String DB_RUNNING_EXERCISE_ORDER = "exercise_order tinyint not null";
    private static final String DB_RUNNING_EXERCISE_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";

    // GymExercise table
    private static final String DB_GYM_EXERCISE_TABLE_NAME = "GymExercise";
    private static final String DB_GYM_EXERCISE_ID = "_id integer primary key autoincrement";
    private static final String DB_GYM_EXERCISE_DURATION = "duration integer default 0";
    private static final String DB_GYM_EXERCISE_REPETITIONS = "repetitions integer default 0";
    private static final String DB_GYM_EXERCISE_GYM_MODE = "gym_mode tinyint not null";
    private static final String DB_GYM_EXERCISE_ORDER = "exercise_order tinyint not null";
    private static final String DB_GYM_EXERCISE_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";

    // TrainingProgram table
    private static final String DB_TRAINING_PROGRAM_TABLE_NAME = "TrainingProgram";
    private static final String DB_TRAINING_PROGRAM_ID = "_id integer primary key autoincrement";
    private static final String DB_TRAINING_PROGRAM_NAME = "name varchar(64)";

    // GPSCoord table
    private static final String DB_GPS_COORD_TABLE_NAME = "GPSCoord";
    private static final String DB_GPS_COORD_ID = "_id integer primary key autoincrement";
    private static final String DB_GPS_COORD_LONGITUDE = "longitude double not null";
    private static final String DB_GPS_COORD_LATTITUDE = "lattitude double not null";
    private static final String DB_GPS_COORD_ORDER = "coord_order tinyint not null";
    private static final String DB_GPS_COORD_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";

    // HistoryRecord table
    private static final String DB_HISTORY_TABLE_NAME = "HistoryRecord";
    private static final String DB_HISTORY_ID = "_id integer primary key autoincrement";
    private static final String DB_HISTORY_TIME = "time integer not null";
    private static final String DB_HISTORY_CALORIES_BURNED = "calories_burned integer not null";
    private static final String DB_HISTORY_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";


    // create table SQL command for table Exercise
    private static final String DB_CREATE_RUNNING_EXERCISE_TABLE =
            "create table " + DB_RUNNING_EXERCISE_TABLE_NAME + "(" + DB_RUNNING_EXERCISE_ID + ", " +
             DB_RUNNING_EXERCISE_DISTANCE + ", " + DB_RUNNING_EXERCISE_SPEED_MODE + ", " + DB_RUNNING_EXERCISE_ORDER + ", " + DB_RUNNING_EXERCISE_TRAINING_PROGRAM_FKEY + ")";

    // create table SQL command for table Exercise
    private static final String DB_CREATE_GYM_EXERCISE_TABLE =
            "create table " + DB_GYM_EXERCISE_TABLE_NAME + "(" + DB_GYM_EXERCISE_ID + ", " + DB_GYM_EXERCISE_DURATION + ", " +
                    DB_GYM_EXERCISE_REPETITIONS + ", " + DB_GYM_EXERCISE_GYM_MODE + ", " + DB_GYM_EXERCISE_ORDER + ", " + DB_GYM_EXERCISE_TRAINING_PROGRAM_FKEY + ")";

    // create table SQL command for table GPSCoord
    private static final String DB_CREATE_GPS_COORD_TABLE =
            "create table " + DB_GPS_COORD_TABLE_NAME + "(" + DB_GPS_COORD_ID + ", " + DB_GPS_COORD_LONGITUDE + ", " + DB_GPS_COORD_LATTITUDE + ", " +
                    DB_GPS_COORD_ORDER + ", " + DB_GPS_COORD_TRAINING_PROGRAM_FKEY + ")";

    // create table SQL command for table TrainingProgram
    private static final String DB_CREATE_TRAINING_PROGRAM_TABLE =
            "create table " + DB_TRAINING_PROGRAM_TABLE_NAME + "(" + DB_TRAINING_PROGRAM_ID + ", " + DB_TRAINING_PROGRAM_NAME + ")";

    // create table SQL command for table HistoryRecord
    private static final String DB_CREATE_HISTORY_TABLE =
            "create table " + DB_HISTORY_TABLE_NAME + "(" + DB_HISTORY_ID + ", " + DB_HISTORY_TIME + ", " + DB_HISTORY_CALORIES_BURNED + ", " + DB_HISTORY_TRAINING_PROGRAM_FKEY + ")";

    // trigger for RunningExercise
    private static final String DB_RUNNING_EXERCISE_TRIGGER =
            "create trigger update_exercise_order after insert on RunningExercise " +
            "for each row begin " +
            "declare ";

    public TrailAssistantDBBuilder(Context context) {
        // this constructor basically says which database we want to open
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE_TRAINING_PROGRAM_TABLE);
        database.execSQL(DB_CREATE_RUNNING_EXERCISE_TABLE);
        database.execSQL(DB_CREATE_GYM_EXERCISE_TABLE);
        database.execSQL(DB_CREATE_GPS_COORD_TABLE);
        database.execSQL(DB_CREATE_HISTORY_TABLE);
        insertDummyData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TrailAssistantDBBuilder.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ". " +
                "All existing data will be destroyed, and the new database will replace the previous one!");
        //database.execSQL("drop table if exists " + DB_GPS_COORD_TABLE_NAME + ", " + DB_HISTORY_TABLE_NAME + ", " + DB_GYM_EXERCISE_TABLE_NAME + ", " + DB_RUNNING_EXERCISE_TABLE_NAME + ", " + DB_TRAINING_PROGRAM_TABLE_NAME);
        database.execSQL("drop table if exists " + DB_GPS_COORD_TABLE_NAME);
        database.execSQL("drop table if exists " + DB_HISTORY_TABLE_NAME);
        database.execSQL("drop table if exists " + DB_GYM_EXERCISE_TABLE_NAME);
        database.execSQL("drop table if exists " + DB_RUNNING_EXERCISE_TABLE_NAME);
        database.execSQL("drop table if exists " + DB_TRAINING_PROGRAM_TABLE_NAME);
        onCreate(database);
    }

    private void insertDummyData(SQLiteDatabase database) {
        String insertTrainingProgram = "insert into TrainingProgram values (NULL, 'Test Training Program')";
        //String retrieveTrainingProgramID = "select _id from TrainingProgram order by training_program_id desc limit 1";
        String insertGPSCoords =    "insert into GPSCoord values (NULL, 6.094379, 49.600464, 1, 1), " +
                                    "(NULL, 6.095712, 49.600309, 2, 1), " +
                                    "(NULL, 6.097900, 49.605406, 3, 1), " +
                                    "(NULL, 6.109276, 49.607214, 4, 1), " +
                                    "(NULL, 6.111636, 49.604339, 5, 1), " +
                                    "(NULL, 6.112159, 49.606251, 6, 1)";
        String insertRunningExercises =    "insert into RunningExercise values (NULL, 4000, 2, 1, 1), " +
                                    "(NULL, 1000, 3, 2, 1), " +
                                    "(NULL, 500, 1, 3, 1)";
        String insertGymExercises = "insert into GymExercise values (NULL, 20, 0, 0, 4, 1)";

        // insert new training program and retrieve its ID
        database.execSQL(insertTrainingProgram);
        /*Cursor cursor = database.rawQuery(retrieveTrainingProgramID, null);
        cursor.moveToFirst();
        int lastID = cursor.getInt(0);
        cursor.close();*/
        // insert GPS coordinates that represent trail (here in this example, it's a trail that goes from my home to the Merl public parc)
        database.execSQL(insertGPSCoords);
        // insert some dummy exercises that belong to that training program
        database.execSQL(insertRunningExercises);
        database.execSQL(insertGymExercises);

    }

}
