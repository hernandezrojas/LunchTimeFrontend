package com.herroj.android.lunchtime.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.File;

/**
 * LunchTimeContract es un acuerdo entre el modelo de datos, el almacenamiento y la vista de
 * presentacion, describiendo como la información es accesada
 */
public final class LunchTimeContract {

    public static String s_filtroSeleccionado = "-1";

    public static final String FILTRO_CAFETERIA = "1";

    public static final String FILTRO_PUESTO = "2";

    /**
     * CONTENT_AUTHORITY es un nombre para el proveedor de contenido completo, similar a la
     * relacion entre un nombre de dominio y su sitio web. Un conveniente string a usarse es
     * el nombre del paquete de la aplicacion, con esto se garantiza que sea unico
     */
    static final String CONTENT_AUTHORITY = "com.herroj.android.lunchtime.app";
    /**
     * PATH_RESTAURANT es una opcion agregada al contenido base URI como una posibilidad. Por
     * ejemplo, content://com.herroj.android.lunchtimefrontend.app/restaurant es una ruta válida
     * para ver los datos de los restaurantes
     */
    static final String PATH_RESTAURANT = "restaurant";
    /**
     * BASE_CONTENT_URI se usa para crear la base de todos los URI's que la aplicacion usara
     * para conectarse al proveedor de contenido
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Restaurant entry es una clase interna que define el contenido de la tabla Restaurant
     */
    public static class RestaurantEntry implements BaseColumns {

        /**
         * CONTENT_URI es una constante que contiene la URI de la tabla Restaurant
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        /**
         * TABLE_NAME es una constante que tiene el nombre de la tabla
         */
        public static final String TABLE_NAME = "restaurant";

        /**
         * COLUMN_RESTAURANT contiene el nombre del campo restaurant
         */
        public static final String COLUMN_RESTAURANT = "restaurant";

        /**
         * COLUMN_HORA_APERTURA contiene el nombre del campo hora de apertura
         */
        public static final String COLUMN_HORA_APERTURA = "horaApertura";

        /**
         * COLUMN_HORA_CIERRE contiene el nombre del campo hora de cierre
         */
        public static final String COLUMN_HORA_CIERRE = "horaCierre";

        /**
         * COLUMN_TIPO_RESTAURANT contiene el nombre del campo tipo de restaurant
         */
        public static final String COLUMN_TIPO_RESTAURANT = "tipoRestaurant";

        /**
         * CONTENT_TYPE como su nombre lo indica establese el tipo de contenido que puede ser
         * un directorio como en este caso o algun elemento
         */
        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + File.separator +
                        CONTENT_AUTHORITY + File.separator + PATH_RESTAURANT;

        /**
         * buildRestaurantUri construye una uri que disponga un elemento de restaurant por su id
         *
         * @param id es el elemento que se busca
         * @return devuelve la uri que identifica el elemento requerido
         */
        static Uri buildRestaurantUri(final long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri BuildRestaurantPorTipo(){
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_TIPO_RESTAURANT, s_filtroSeleccionado).build();
        }

        /**
         * Build restaurantpor nombre uri uri.
         *
         * @param nombre the nombre
         * @return the uri
         */
        public static Uri buildRestaurantporNombreUri(final String nombre) {
            return CONTENT_URI.buildUpon().appendPath(nombre).build();
        }

        /**
         * Devuelve el restaurant seleccionado en la pantalla de configuracion
         *
         * @param uri es el uri de donde se obtendra la informacion
         * @return el restaurante se obtiene del segundo segmento del uri
         */
        static String getRestaurantSettingFromUri(final Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
