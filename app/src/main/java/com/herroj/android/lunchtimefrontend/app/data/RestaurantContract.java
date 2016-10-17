package com.herroj.android.lunchtimefrontend.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Roberto Hernandez on 13/10/2016.
 */

public class RestaurantContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.herroj.android.lunchtimefrontend.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_RESTAURANT = "restaurant";

    /* Inner class that defines the table contents of the restaurant table */
    public static final class RestaurantEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;

        // Table name
        public static final String TABLE_NAME = "restaurant";
        // Column with the foreign key into the platillo table.
        public static final String COLUMN_TIPO_RESTAURANT_ID = "tipoRestaurantidTipoRestaurant";
        public static final String COLUMN_RESTAURANT = "restaurant";
        public static final String COLUMN_HORA_APERTURA = "horaApertura";
        public static final String COLUMN_HORA_CIERRE = "horaCierre";

        public static Uri buildRestaurantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildRestaurantUri() {
            return CONTENT_URI;
        }

        public static Uri buildRestaurantporNombreUri(String nombre) {
            return CONTENT_URI.buildUpon().appendPath(nombre).build();
        }

        public static String getRestaurantSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
