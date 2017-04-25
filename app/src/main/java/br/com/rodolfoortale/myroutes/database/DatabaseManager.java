package br.com.rodolfoortale.myroutes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class responsible to manage database.
 */

public class DatabaseManager extends SQLiteOpenHelper {

    public static final String TABLE_JOURNEYS = "journeys";
    public static final String JOURNEY_COLUMN_ID = "_id";
    public static final String JOURNEY_COLUMN_NAME = "name";
    public static final String JOURNEY_COLUMN_START = "start_time";
    public static final String JOURNEY_COLUMN_END = "end_time";

    public static final String TABLE_POSITIONS = "positions";
    public static final String POSITIONS_COLUMN_ID = "_id";
    public static final String POSITIONS_COLUMN_JOURNEY_ID = "journey_id";
    public static final String POSITIONS_COLUMN_LATITUDE = "latitude";
    public static final String POSITIONS_COLUMN_LONGITUDE = "longitude";

    private static final String DATABASE_NAME = "my_routes.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseManager databaseManager;

    /**
     * Tables creation
     */
    private static final String CREATE_JOURNEYS = "CREATE TABLE "
            + TABLE_JOURNEYS + "("
                + JOURNEY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JOURNEY_COLUMN_NAME + " VARCHAR(250) NOT NULL, "
                + JOURNEY_COLUMN_START + " DATETIME NOT NULL, "
                + JOURNEY_COLUMN_END + " DATETIME"
            + ");";

    private static final String CREATE_POSITIONS = "CREATE TABLE "
            + TABLE_POSITIONS + "("
                + POSITIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + POSITIONS_COLUMN_JOURNEY_ID + " INTEGER NOT NULL, "
                + POSITIONS_COLUMN_LATITUDE + " DOUBLE NOT NULL, "
                + POSITIONS_COLUMN_LONGITUDE + " DOUBLE NOT NULL"
            + ");";

    /**
     * Creates the database
     *
     * @param context
     */
    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Singleton to get the instance of DatabaseManager.
     *
     * @param context
     * @return
     */
    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }

        return databaseManager;
    }

    /**
     * Creates tables.
     *
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_JOURNEYS);
        database.execSQL(CREATE_POSITIONS);
    }

    /**
     * Handles database upgrade.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseManager.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_JOURNEYS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_POSITIONS);
        onCreate(db);
    }

}