package com.herroj.android.lunchtimefrontend.app.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.herroj.android.lunchtimefrontend.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Roberto Hernandez on 13/10/2016.
 */

public class TestUtilities extends AndroidTestCase {
    static final String TEST_RESTAURANT = "FCFM";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default restaurant values for your database tests.
     */
    static ContentValues createRestaurantValues(long tipoRestaurantRowId) {
        ContentValues restaurantValues = new ContentValues();

        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_KEY, tipoRestaurantRowId);
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT, "Restaurant Prueba");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA, "8:00 a.m.");
        restaurantValues.put(RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE, "9:00 p.m.");


        return restaurantValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
//    static ContentValues createNorthPoleLocationValues() {
//        // Create a new map of values, where column names are the keys
//        ContentValues testValues = new ContentValues();
//        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
//        testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
//        testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
//        testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, -147.353);
//
//        return testValues;
//    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
//    static long insertNorthPoleLocationValues(Context context) {
//        // insert our test records into the database
//        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
//
//        long locationRowId;
//        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
//
//        // Verify we got a row back.
//        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);
//
//        return locationRowId;
//    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
