package br.com.rodolfoortale.myroutes.controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.database.DatabaseManager;
import br.com.rodolfoortale.myroutes.model.Journey;

/**
 * Journeys Controller
 */

public class JourneysController {
    private final String TAG = JourneysController.class.getSimpleName();

    private Context context;
    private DatabaseManager databaseManager;
    private SQLiteDatabase sqLiteDatabase;
    private SharedPreferences mPrefs;

    public JourneysController() {

    }

    /**
     *
     * @param context
     */
    public JourneysController(Context context) {
        this.context = context;
    }

    /**
     * Stores journey on database saving only start date and time first.
     *
     * @param journey
     * @return
     */
    public long createNew(Journey journey) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(DatabaseManager.JOURNEY_COLUMN_NAME, journey.getName());
        insertValues.put(DatabaseManager.JOURNEY_COLUMN_START, journey.getStartTime());
        insertValues.put(DatabaseManager.JOURNEY_COLUMN_END, journey.getEndTime());

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();
        long id = sqLiteDatabase.insert(DatabaseManager.TABLE_JOURNEYS, null, insertValues);
        databaseManager.close();

        Log.v(TAG, "id: " + id + " start: " + journey.getStartTime() + " end: " + journey.getEndTime());

        return id;
    }

    /**
     * Updates the current journey with its end date and time.
     *
     * @param journey
     * @return
     */
    public long updateJourney(Journey journey) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseManager.JOURNEY_COLUMN_NAME, journey.getName());
        contentValues.put(DatabaseManager.JOURNEY_COLUMN_START, journey.getStartTime());
        contentValues.put(DatabaseManager.JOURNEY_COLUMN_END, journey.getEndTime());

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();
        long id = sqLiteDatabase.update(DatabaseManager.TABLE_JOURNEYS, contentValues, "_id=" + journey.getId(), null);
        databaseManager.close();

        Log.v(TAG, "id: " + id + " start: " + journey.getStartTime() + " end: " + journey.getEndTime());

        return id;
    }

    /**
     * Return all journeys.
     *
     * @return
     */
    public ArrayList<Journey> getAll() {
        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseManager.TABLE_JOURNEYS;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        ArrayList<Journey> journeys = new ArrayList<>();

        Log.v(TAG, "cursor: " + cursor.moveToFirst());
        if (cursor.moveToFirst()) {
            do {
                Journey journey = new Journey();
                Long id = cursor.getLong(0);
                journey.setId(id);
                String name = cursor.getString(1);
                journey.setName(name);
                String start = cursor.getString(2);
                journey.setStartTime(start);
                String end = cursor.getString(3);
                journey.setEndTime(end);
                journeys.add(journey);

                Log.v(TAG, "name: " + journey.getName() + " start: " + journey.getStartTime() + " end: " + journey.getEndTime());
            } while (cursor.moveToNext());

            return journeys;
        }
        cursor.close();
        databaseManager.close();
        return null;
    }

    /**
     * Returns last stored journey.
     *
     * @return
     */
    public Journey getLast() {
        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseManager.TABLE_JOURNEYS + " ORDER BY _id DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            Journey journey = new Journey();
            Long id = cursor.getLong(0);
            journey.setId(id);
            String name = cursor.getString(1);
            journey.setName(name);
            String start = cursor.getString(2);
            journey.setStartTime(start);
            String end = cursor.getString(3);
            journey.setEndTime(end);

            Log.v(TAG, "id last: " + id);

            return journey;
        }
        cursor.close();
        databaseManager.close();

        return null;
    }

    /**
     * Check if application is tracking.
     *
     * @return
     */
    public Boolean isTracking() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getBoolean(context.getString(R.string.lb_is_tracking), false);
    }

    /**
     * Sets tracking on.
     *
     * @param isTracking
     */
    public void setTracking(Boolean isTracking) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putBoolean(context.getString(R.string.lb_is_tracking), isTracking);
        prefsEditor.apply();
    }

    /**
     * Check if journey is active.
     *
     * @return
     */
    public Boolean isJourneyLive() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getBoolean(context.getString(R.string.lb_is_journey_live), false);
    }

    /**
     * Sets journey active.
     *
     * @param isJourneyLive
     */
    public void setJourneyLive(Boolean isJourneyLive) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putBoolean(context.getString(R.string.lb_is_journey_live), isJourneyLive);
        prefsEditor.apply();
    }
}
