package com.herroj.android.lunchtimefrontend.app.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Roberto Hernandez on 13/10/2016.
 */

public class RestaurantContract {

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    /*
    public static final class RestaurantEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";

    }
    */
    /* Inner class that defines the table contents of the restaurant table */
    public static final class RestaurantEntry implements BaseColumns {

        public static final String TABLE_NAME = "restaurant";

        // Column with the foreign key into the platillo table.
        public static final String COLUMN_TIPO_RESTAURANT_KEY = "tipo_restaurant_id";

        public static final String COLUMN_RESTAURANT = "restaurant";

        public static final String COLUMN_HORA_APERTURA = "hora_apertura";

        public static final String COLUMN_HORA_CIERRE = "hora_cierre";

    }
}
