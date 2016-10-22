package com.herroj.android.lunchtime.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.File;

/**
 * RestaurantContract es un acuerdo entre el modelo de datos, el almacenamiento y la vista de
 * presentación, describiendo como la información es accesada
 */
public enum RestaurantContract {
    ;

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    static final String CONTENT_AUTHORITY = "com.herroj.android.lunchtimefrontend.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    static final String PATH_RESTAURANT = "restaurant";

    /* Inner class that defines the table contents of the restaurant table */
    public static class RestaurantEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + File.separator +
                        CONTENT_AUTHORITY + File.separator + PATH_RESTAURANT;

        // Table name
        public static final String TABLE_NAME = "restaurant";
        // Column with the foreign key into the platillo table.
        public static final String COLUMN_RESTAURANT = "restaurant";
        public static final String COLUMN_HORA_APERTURA = "horaApertura";
        public static final String COLUMN_HORA_CIERRE = "horaCierre";

        static Uri buildRestaurantUri(final long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRestaurantporNombreUri(final String nombre) {
            return CONTENT_URI.buildUpon().appendPath(nombre).build();
        }

        static String getRestaurantSettingFromUri(final Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
