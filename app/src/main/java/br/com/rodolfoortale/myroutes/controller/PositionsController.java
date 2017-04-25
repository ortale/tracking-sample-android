package br.com.rodolfoortale.myroutes.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import br.com.rodolfoortale.myroutes.database.DatabaseManager;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.model.Position;

/**
 * Positions Controller
 */

public class PositionsController {
    private final String TAG = PositionsController.class.getSimpleName();

    private Context context;
    private DatabaseManager databaseManager;
    private SQLiteDatabase sqLiteDatabase;

    public PositionsController() {

    }

    /**
     *
     * @param context
     */
    public PositionsController(Context context) {
        this.context = context;
    }

    /**
     * Creates new position from a journey.
     *
     * @param position
     * @return
     */
    public long createNew(Position position) {
        long id = -1;

        if (!positionExists(position.getLatitude(), position.getLongitude())) {
            ContentValues insertValues = new ContentValues();
            insertValues.put(DatabaseManager.POSITIONS_COLUMN_JOURNEY_ID, position.getJourney().getId());
            insertValues.put(DatabaseManager.POSITIONS_COLUMN_LATITUDE, position.getLatitude());
            insertValues.put(DatabaseManager.POSITIONS_COLUMN_LONGITUDE, position.getLongitude());

            databaseManager = DatabaseManager.getInstance(context);
            sqLiteDatabase = databaseManager.getWritableDatabase();
            id = sqLiteDatabase.insert(DatabaseManager.TABLE_POSITIONS, null, insertValues);
            databaseManager.close();

            Log.v(TAG, "latitude: " + position.getLatitude() + " longitude: " + position.getLongitude());
        }

        return id;
    }

    /**
     * Returns all LatLng position by a specific journey.
     *
     * @param journey
     * @return
     */
    public List<LatLng> getAll(Journey journey) {
        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseManager.TABLE_POSITIONS + " WHERE "+ DatabaseManager.POSITIONS_COLUMN_JOURNEY_ID + " = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, new String[] { String.valueOf(journey.getId()) });
        List<LatLng> positions = new ArrayList<>();

        Log.v(TAG, "cursor: " + cursor.moveToFirst());
        if (cursor.moveToFirst()) {
            do {
                Double latitude = cursor.getDouble(2);
                Double longitude = cursor.getDouble(3);

                LatLng position = new LatLng(latitude, longitude);

                positions.add(position);

                //Log.v(TAG, "name: " + position.getId() + " start: " + position.getLatitude() + " end: " + position.getLongitude());
            } while (cursor.moveToNext());

            return positions;
        }
        cursor.close();
        databaseManager.close();
        return null;
    }

    /**
     * Check if position exists.
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private boolean positionExists(Double latitude, Double longitude) {
        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseManager.TABLE_POSITIONS + " WHERE "+ DatabaseManager.POSITIONS_COLUMN_LATITUDE + " = ? AND " + DatabaseManager.POSITIONS_COLUMN_LONGITUDE + " = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, new String[] { String.valueOf(latitude), String.valueOf(longitude) });

        boolean exists = cursor.moveToFirst();
        cursor.close();
        databaseManager.close();

        return exists;
    }
}
