package com.herroj.android.lunchtime.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * RestaurantProvider es un proveedor de contenidos que nos ayuda eficientemente sicronizar desde
 * internet combinado con las utilidades del android framework
 */
public class RestaurantProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher S_URI_MATCHER = buildUriMatcher();
    private RestaurantDbHelper m_openHelper;

    private static final int RESTAURANT = 100;
    private static final int RESTAURANT_WITH_NAME = 101;

    private static final SQLiteQueryBuilder S_RESTAURANT_QUERY_BUILDER;

    static {
        S_RESTAURANT_QUERY_BUILDER = new SQLiteQueryBuilder();

        S_RESTAURANT_QUERY_BUILDER.setTables(
                RestaurantContract.RestaurantEntry.TABLE_NAME);

        /* RHR Pendiente esta adaptacion para mas adelante
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sRestaurantByTypeQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
                        */
    }

    //Restaurant.restaurant = ?
    private static final String S_RESTAURANT_SETTING_SELECTION =
            RestaurantContract.RestaurantEntry.TABLE_NAME +
                    '.' + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT + " = ? ";


    private Cursor getRestaurantByNameSetting(
            final Uri uri, final String[] projection, final String sortOrder) {
        final String restaurantSetting =
                RestaurantContract.RestaurantEntry.getRestaurantSettingFromUri(uri);

        String[] selectionArgs = null;
        String selection = null;

        if (restaurantSetting != null) {
            selection = S_RESTAURANT_SETTING_SELECTION;
            selectionArgs = new String[]{restaurantSetting};
        }

        /*
        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }
        */

        return S_RESTAURANT_QUERY_BUILDER.query(m_openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RestaurantContract.CONTENT_AUTHORITY;

        //// For each type of URI you want to add, create a corresponding code.
        //matcher.addURI(authority, RestaurantContract.PATH_WEATHER, WEATHER);
        //matcher.addURI(authority, RestaurantContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        //matcher.addURI(authority,
        // RestaurantContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, RestaurantContract.PATH_RESTAURANT, RESTAURANT);
        matcher.addURI(authority, RestaurantContract.PATH_RESTAURANT + File.separator  + '*',
                RESTAURANT_WITH_NAME);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public final boolean onCreate() {
        m_openHelper = new RestaurantDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public final String getType(@NonNull final Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = S_URI_MATCHER.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case RESTAURANT:
                return RestaurantContract.RestaurantEntry.CONTENT_TYPE;
            case RESTAURANT_WITH_NAME:
                return RestaurantContract.RestaurantEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public final Cursor query(@NonNull final Uri uri, final String[] strings, final String s,
                              final String[] strings1, final String s1) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        final Cursor retCursor;
        switch (S_URI_MATCHER.match(uri)) {
            // "restaurant"
            case RESTAURANT_WITH_NAME:
                retCursor = getRestaurantByNameSetting(uri, strings, s1);
                break;
            case RESTAURANT: {
                retCursor = m_openHelper.getReadableDatabase().query(
                        RestaurantContract.RestaurantEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public final Uri insert(@NonNull final Uri uri, final ContentValues contentValues) {
        final SQLiteDatabase db = m_openHelper.getWritableDatabase();
        final int match = S_URI_MATCHER.match(uri);
        final Uri returnUri;

        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT: {
                final long id =
                        db.insert(RestaurantContract.RestaurantEntry.TABLE_NAME,
                                null, contentValues);
                if (id > 0L) {
                    returnUri = RestaurantContract.RestaurantEntry.buildRestaurantUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public final int delete(
            @NonNull final Uri uri, final String s, final String[] strings) {
        final SQLiteDatabase db = m_openHelper.getWritableDatabase();
        final int match = S_URI_MATCHER.match(uri);
        String strSelection = s;

        // this makes delete all rows return the number of rows deleted
        if (strSelection == null) {
            strSelection = "1";
        }
        final int rowsDeleted;
        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                rowsDeleted = db.delete(
                        RestaurantContract.RestaurantEntry.TABLE_NAME, strSelection, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if ((rowsDeleted != 0) && (getContext() != null)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public final int update(@NonNull final Uri uri, final ContentValues contentValues,
                            final String s, final String[] strings) {
        final SQLiteDatabase db = m_openHelper.getWritableDatabase();

        final int match = S_URI_MATCHER.match(uri);
        final int rowsUpdated;

        switch (match)

        {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                rowsUpdated = db.update(RestaurantContract.RestaurantEntry.TABLE_NAME,
                        contentValues, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if ((rowsUpdated != 0) && (getContext() != null))

        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public final int bulkInsert(@NonNull final Uri uri, @NonNull final ContentValues[] values) {
        final SQLiteDatabase db = m_openHelper.getWritableDatabase();
        final int match = S_URI_MATCHER.match(uri);
        if ((match == RESTAURANT_WITH_NAME) || (match == RESTAURANT)) {

            db.beginTransaction();
            int returnCount = 0;
            try {
                for (final ContentValues value : values) {
                    final long id = db.insert(
                            RestaurantContract.RestaurantEntry.TABLE_NAME, null, value);
                    if (id != -1L) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return returnCount;
        } else {
            return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public final void shutdown() {
        m_openHelper.close();
        super.shutdown();
    }
}