package com.herroj.android.lunchtime.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.herroj.android.lunchtime.app.data.RestaurantContract.RestaurantEntry;

/**
 * RestaurantDbHelper contiene el código para crear e inicializar la base de datos
 */

class RestaurantDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "lunchtime.db";

    RestaurantDbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public final void onCreate(final SQLiteDatabase sqLiteDatabase) {
        final String sqlCreateRestaurantTable =
                "CREATE TABLE " + RestaurantEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                RestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RestaurantEntry.COLUMN_RESTAURANT + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_HORA_APERTURA + " TEXT, " +
                RestaurantEntry.COLUMN_HORA_CIERRE + " TEXT); ";

                //RestaurantEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +

                //// Set up the location column as a foreign key to location table.
                //" FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                //LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                //// To assure the application have just one weather entry per day
                //// per location, it's created a UNIQUE constraint with REPLACE strategy
                //" UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                //WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(sqlCreateRestaurantTable);
    }

    @Override
    public final void onUpgrade(
            final SQLiteDatabase sqLiteDatabase, final int i, final int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RestaurantEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
