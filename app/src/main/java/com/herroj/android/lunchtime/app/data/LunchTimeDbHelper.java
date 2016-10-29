package com.herroj.android.lunchtime.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.herroj.android.lunchtime.app.data.LunchTimeContract.RestaurantEntry;

/**
 * LunchTimeDbHelper una clase auxiliar para la creacion de bases de datos y manejo de versiones
 */
class LunchTimeDbHelper extends SQLiteOpenHelper {

    /**
     * DATABASE_VERSION si se cambia la estructura de la base de datos, se debera incrementar
     * esta version de base de datos
     */
    private static final int DATABASE_VERSION = 7;

    /**
     * DATABASE_NAME nombre con el que se identificara la base de datos
     */
    private static final String DATABASE_NAME = "lunchtime.db";

    /**
     * Instancia un nuevo Lunch Time database helper
     *
     * @param context el contexto en el cual interectuara la base de datos
     */
    LunchTimeDbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * onCreate se llama cuando la base de datos se crea por primera vez
     *
     * @param sqLiteDatabase base de datos
     */
    @Override
    public final void onCreate(final SQLiteDatabase sqLiteDatabase) {

        final String sqlCreateRestaurantTable =
                "CREATE TABLE " + RestaurantEntry.TABLE_NAME + " (" +
                        RestaurantEntry._ID + " INTEGER PRIMARY KEY," +
                        RestaurantEntry.COLUMN_RESTAURANT + " TEXT NOT NULL, " +
                        RestaurantEntry.COLUMN_HORA_APERTURA + " TEXT, " +
                        RestaurantEntry.COLUMN_HORA_CIERRE + " TEXT, " +
                        RestaurantEntry.COLUMN_TIPO_RESTAURANT + " INTEGER); ";

        sqLiteDatabase.execSQL(sqlCreateRestaurantTable);

    }

    /**
     * onUpgrade Se llama cuando la base de datos necesita hacer un upgrae. La aplicación
     * debe utilizar este método para eliminar tablas, añadir tablas, o hacer cualquier otra
     * cosa que necesita para actualizar a la nueva versión del esquema.
     *
     * @param sqLiteDatabase la base de datos
     * @param i la version anterior de la base de datos
     * @param i1 la version nueva de la base de datos
     */
    @Override
    public final void onUpgrade(
            final SQLiteDatabase sqLiteDatabase, final int i, final int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RestaurantEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

}
