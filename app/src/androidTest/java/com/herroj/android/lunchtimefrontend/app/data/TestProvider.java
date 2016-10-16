package com.herroj.android.lunchtimefrontend.app.data;

/**
 * Created by Roberto Hernandez on 15/10/2016.
 */

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract.RestaurantEntry;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                RestaurantEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                RestaurantEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                RestaurantEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Restaurant table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                RestaurantEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Restaurant table during delete", 0, cursor.getCount());
        cursor.close();
    }


    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // RestaurantProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                RestaurantProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: RestaurantProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + RestaurantContract.CONTENT_AUTHORITY,
                    providerInfo.authority, RestaurantContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: RestaurantProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
//    public void testGetType() {
//        // content://com.example.android.sunshine.app/weather/
//        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
//        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
//        assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE",
//                WeatherEntry.CONTENT_TYPE, type);
//
//        String testLocation = "94074";
//        // content://com.example.android.sunshine.app/weather/94074
//        type = mContext.getContentResolver().getType(
//                WeatherEntry.buildWeatherLocation(testLocation));
//        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
//        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
//                WeatherEntry.CONTENT_TYPE, type);
//
//        long testDate = 1419120000L; // December 21st, 2014
//        // content://com.example.android.sunshine.app/weather/94074/20140612
//        type = mContext.getContentResolver().getType(
//                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
//        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
//        assertEquals("Error: the WeatherEntry CONTENT_URI with location and date should return WeatherEntry.CONTENT_ITEM_TYPE",
//                WeatherEntry.CONTENT_ITEM_TYPE, type);
//
//        // content://com.example.android.sunshine.app/location/
//        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
//        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
//        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
//                LocationEntry.CONTENT_TYPE, type);
//    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
//    public void testBasicWeatherQuery() {
//        // insert our test records into the database
//        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
//        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);
//
//        // Fantastic.  Now that we have a location, add some weather!
//        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
//
//        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
//        assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);
//
//        db.close();
//
//        // Test the basic content provider query
//        Cursor weatherCursor = mContext.getContentResolver().query(
//                WeatherEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//
//        // Make sure we get the correct cursor out of the database
//        TestUtilities.validateCursor("testBasicWeatherQuery", weatherCursor, weatherValues);
//    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicRestaurantQueries() {
        // insert our test records into the database
        RestaurantDbHelper dbHelper = new RestaurantDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createFCFMRestaurantValues();

        long locationRowId = TestUtilities.insertRestaurantValues(mContext);

        // Test the basic content provider query
        Cursor restaurantCursor = mContext.getContentResolver().query(
                RestaurantEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor
                ("testBasicRestaurantQueries, restaurant query", restaurantCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    restaurantCursor.getNotificationUri(), RestaurantEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateRestaurant() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createFCFMRestaurantValues();

        Uri restaurantUri = mContext.getContentResolver().
                insert(RestaurantEntry.CONTENT_URI, values);
        long restaurantRowId = ContentUris.parseId(restaurantUri);

        // Verify we got a row back.
        assertTrue(restaurantRowId != -1);
        Log.d(LOG_TAG, "New row id: " + restaurantRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(RestaurantEntry._ID, restaurantRowId);
        updatedValues.put(RestaurantEntry.COLUMN_RESTAURANT, "FACPYA");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor restaurantCursor = mContext.getContentResolver().query(RestaurantEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        restaurantCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                RestaurantEntry.CONTENT_URI, updatedValues, RestaurantEntry._ID + "= ?",
                new String[]{Long.toString(restaurantRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        restaurantCursor.unregisterContentObserver(tco);
        restaurantCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                RestaurantEntry.CONTENT_URI,
                null,   // projection
                RestaurantEntry._ID + " = " + restaurantRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateRestaurant.  Error validating restaurant entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createFCFMRestaurantValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RestaurantEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(RestaurantEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                RestaurantEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating RestaurantEntry.",
                cursor, testValues);

    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver restaurantObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RestaurantEntry.CONTENT_URI, true, restaurantObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        restaurantObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(restaurantObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 4;

    static ContentValues[] createBulkInsertRestaurantValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        ContentValues restaurantValues = new ContentValues();

        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT, "FCFM");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID, 1);
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA, "8:00 a.m.");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE, "9:00 p.m.");

        returnContentValues[0] = restaurantValues;

        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT, "FOD");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID, 1);
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA, "");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE, "");

        returnContentValues[1] = restaurantValues;

        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT, "FIME");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID, 1);
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA, "7:00 a.m.");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE, "10:00 p.m.");

        returnContentValues[2] = restaurantValues;

        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createFCFMRestaurantValues();
        Uri restaurantUri = mContext.getContentResolver().insert(RestaurantEntry.CONTENT_URI, testValues);
        long restaurantRowId = ContentUris.parseId(restaurantUri);

        // Verify we got a row back.
        assertTrue(restaurantRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                RestaurantEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating RestaurantEntry.",
                cursor, testValues);

    }
}