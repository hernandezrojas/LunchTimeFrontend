package com.herroj.android.lunchtimefrontend.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Roberto Hernandez on 16/10/2016.
 */

public class TestUriMatcher extends AndroidTestCase {
    private static final String LOCATION_QUERY = "London, UK";
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final long TEST_LOCATION_ID = 10L;

    //// content://com.example.android.sunshine.app/weather"
    //private static final Uri TEST_WEATHER_DIR = RestaurantContract.RestaurantEntry.CONTENT_URI;
    //private static final Uri TEST_WEATHER_WITH_LOCATION_DIR = RestaurantContract.RestaurantEntry.buildWeatherLocation(LOCATION_QUERY);

    // content://com.example.android.sunshine.app/restaurant"
    private static final Uri TEST_RESTAURANT_DIR = RestaurantContract.RestaurantEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = RestaurantProvider.buildUriMatcher();

        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                testMatcher.match(TEST_RESTAURANT_DIR), RestaurantProvider.RESTAURANT);
        //assertEquals("Error: The WEATHER URI was matched incorrectly.",
        //        testMatcher.match(TEST_WEATHER_DIR), WeatherProvider.WEATHER);
        //assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
        //        testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_LOCATION);
        //assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
        //        testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
        //assertEquals("Error: The LOCATION URI was matched incorrectly.",
        //        testMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
    }
}