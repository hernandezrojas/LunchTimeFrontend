package com.herroj.android.lunchtimefrontend.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * RestaurantProvider es un proveedor de contenidos que nos ayuda eficientemente sicronizar desde
 * internet combinado con las utilidades del android framework
 */
public class RestaurantProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RestaurantDbHelper mOpenHelper;

    private static final int RESTAURANT = 100;
    private static final int RESTAURANT_WITH_NAME = 101;

    private static final SQLiteQueryBuilder sRestaurantQueryBuilder;

    static {
        sRestaurantQueryBuilder = new SQLiteQueryBuilder();

        sRestaurantQueryBuilder.setTables(
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
    private static final String sRestaurantSettingSelection =
            RestaurantContract.RestaurantEntry.TABLE_NAME +
                    "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT + " = ? ";


    private Cursor getRestaurantByNameSetting(Uri uri, String[] projection, String sortOrder) {
        String restaurantSetting = RestaurantContract.RestaurantEntry.getRestaurantSettingFromUri(uri);

        String[] selectionArgs = null;
        String selection = null;

        if (restaurantSetting != null) {
            selection = sRestaurantSettingSelection;
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

        return sRestaurantQueryBuilder.query(mOpenHelper.getReadableDatabase(),
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
        //matcher.addURI(authority, RestaurantContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, RestaurantContract.PATH_RESTAURANT, RESTAURANT);
        matcher.addURI(authority, RestaurantContract.PATH_RESTAURANT + "/*", RESTAURANT_WITH_NAME);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new RestaurantDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

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
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "restaurant"
            case RESTAURANT_WITH_NAME:
                retCursor = getRestaurantByNameSetting(uri, projection, sortOrder);
                break;
            case RESTAURANT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RestaurantContract.RestaurantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
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
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT: {
                long _id = db.insert(RestaurantContract.RestaurantEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RestaurantContract.RestaurantEntry.buildRestaurantUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
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
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                rowsDeleted = db.delete(
                        RestaurantContract.RestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            @NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)

        {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                rowsUpdated = db.update(RestaurantContract.RestaurantEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null)

        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RestaurantContract.RestaurantEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
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
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}