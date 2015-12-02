package lu.uni.trailassistant.db;

import android.content.Context;
import android.database.Cursor;
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
    private static final int DB_VERSION = 5;

    // Exercise table
    private static final String DB_EXERCISE_TABLE_NAME = "Exercise";
    private static final String DB_EXERCISE_ID = "_id integer primary key autoincrement";
    private static final String DB_EXERCISE_TYPE = "type tinyint not null";
    private static final String DB_EXERCISE_REPETITIONS = "repetitions integer default 0";
    private static final String DB_EXERCISE_DURATION = "duration integer default 0";
    private static final String DB_EXERCISE_DISTANCE = "distance integer default 0";
    private static final String DB_EXERCISE_SPEED_MODE = "speed_mode tinyint";
    private static final String DB_EXERCISE_EXERCISE_MODE = "exercise_mode tinyint";
    private static final String DB_EXERCISE_ORDER = "exercise_order tinyint not null";
    private static final String DB_EXERCISE_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";

    // TrainingProgram table
    private static final String DB_TRAINING_PROGRAM_TABLE_NAME = "TrainingProgram";
    private static final String DB_TRAINING_PROGRAM_ID = "_id integer primary key autoincrement";
    private static final String DB_TRAINING_PROGRAM_NAME = "name varchar(64)";

    // GPSCoord table
    private static final String DB_GPS_COORD_TABLE_NAME = "GPSCoord";
    private static final String DB_GPS_COORD_ID = "_id integer primary key autoincrement";
    private static final String DB_GPS_COORD_LONGITUDE = "longitude float not null";
    private static final String DB_GPS_COORD_LATTITUDE = "lattitude float not null";
    private static final String DB_GPS_COORD_ORDER = "coord_order tinyint not null";
    private static final String DB_GPS_COORD_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";

    // HistoryRecord table
    private static final String DB_HISTORY_TABLE_NAME = "HistoryRecord";
    private static final String DB_HISTORY_ID = "_id integer primary key autoincrement";
    private static final String DB_HISTORY_TIME = "time integer not null";
    private static final String DB_HISTORY_CALORIES_BURNED = "calories_burned integer not null";
    private static final String DB_HISTORY_TRAINING_PROGRAM_FKEY = "fkey_training_program_id integer not null references TrainingProgram(_id)";


    // create table SQL command for table Exercise
    private static final String DB_CREATE_EXERCISE_TABLE =
            "create table " + DB_EXERCISE_TABLE_NAME + "(" + DB_EXERCISE_ID + ", " + DB_EXERCISE_TYPE + ", " + DB_EXERCISE_REPETITIONS + ", " +
            DB_EXERCISE_DURATION + ", " + DB_EXERCISE_DISTANCE + ", " + DB_EXERCISE_SPEED_MODE + ", " + DB_EXERCISE_EXERCISE_MODE + ", " + DB_EXERCISE_ORDER + ", " + DB_EXERCISE_TRAINING_PROGRAM_FKEY + ")";

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

    public TrailAssistantDBBuilder(Context context) {
        // this constructor basically says which database we want to open
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE_TRAINING_PROGRAM_TABLE);
        database.execSQL(DB_CREATE_EXERCISE_TABLE);
        database.execSQL(DB_CREATE_GPS_COORD_TABLE);
        database.execSQL(DB_CREATE_HISTORY_TABLE);
        insertDummyData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TrailAssistantDBBuilder.class.getName(), "Upgrading database from version" + oldVersion + "to " + newVersion + "." +
                "All existing data will be destroyed, and the new database will replace the previous one!");
        database.execSQL("drop table if exists " + DB_GPS_COORD_TABLE_NAME + ", " + DB_HISTORY_TABLE_NAME + ", " + DB_EXERCISE_TABLE_NAME + ", " + DB_TRAINING_PROGRAM_TABLE_NAME);
        onCreate(database);
    }

    private void insertDummyData(SQLiteDatabase database) {
        String insertTrainingProgram = "insert into TrainingProgram values (NULL, 'Test Training Program')";
        //String retrieveTrainingProgramID = "select training_program_id from TrainingProgram order by training_program_id desc limit 1";
        String insertGPSCoords =    "insert into GPSCoord values (NULL, 49.600464, 6.094379, 1, 1), " +
                                    "(NULL, 49.600309, 6.095712, 2, 1), " +
                                    "(NULL, 49.605406, 6.097900, 3, 1), " +
                                    "(NULL, 49.607214, 6.109276, 4, 1), " +
                                    "(NULL, 49.604339, 6.111636, 5, 1), " +
                                    "(NULL, 49.606251, 6.112159, 6, 1)";
        String insertExercises =    "insert into Exercise values (NULL, 'Starting jogging', 'RUNNING', 0, 360, 0, 'NORMAL', 1, 1), " +
                                    "(NULL, 'Final sprint', 'RUNNING', 0, 0, 300, 'SPRINT', 2, 1), " +
                                    "(NULL, 'Walk and relax', 'RUNNING', 0, 120, 0, 'WALK_AND_BREATHE', 3, 1), " +
                                    "(NULL, 'Some stretching exercise', 'TONING', 20, 0, 0, NULL, 4, 1)";

        // insert new training program and retrieve its ID
        database.execSQL(insertTrainingProgram);
        /*Cursor cursor = database.rawQuery(retrieveTrainingProgramID, null);
        cursor.moveToFirst();
        int lastID = cursor.getInt(0);
        cursor.close();*/
        // insert GPS coordinates that represent trail (here in this example, it's a trail that goes from my home to the Merl public parc)
        database.execSQL(insertGPSCoords);
        // insert some dummy exercises that belong to that training program
        database.execSQL(insertExercises);

    }

}
