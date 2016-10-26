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
 * LunchTimeProvider es un proveedor de contenidos que nos ayuda eficientemente sicronizar desde
 * internet combinado con las utilidades del android framework
 */
public class LunchTimeProvider extends ContentProvider {

    /**
     * RESTAURANT constante que representa la uri que muestra todos los uris
     */
    private static final int RESTAURANT = 100;

    /**
     * RESTAURANT_WITH_NAME constante que representa la uri que muestra los restaurantes
     * que coincidan con el nombre puesto en la pantalla de configuracion
     */
    private static final int RESTAURANT_WITH_NAME = 101;
    /**
     * S_URI_MATCHER instancia utilitaria que se usa para ayudar en el match de uris en
     * proveedores de contenido
     */
    private static final UriMatcher S_URI_MATCHER = buildUriMatcher();

    /**
     * S_RESTAURANT_QUERY_BUILDER instancia que ayuda a crear consultas a la base de datos
     */
    private static final SQLiteQueryBuilder S_RESTAURANT_QUERY_BUILDER;

    /**
     * S_RESTAURANT_SETTING_SELECTION constante que filtra los datos con el nombre ingresado
     * en la pantalla de configuracion
     * Restaurant.restaurant = ?
     */
    private static final String S_RESTAURANT_SETTING_SELECTION =
            LunchTimeContract.RestaurantEntry.TABLE_NAME +
                    '.' + LunchTimeContract.RestaurantEntry.COLUMN_RESTAURANT + " = ? ";

    static {

        S_RESTAURANT_QUERY_BUILDER = new SQLiteQueryBuilder();

        S_RESTAURANT_QUERY_BUILDER.setTables(
                LunchTimeContract.RestaurantEntry.TABLE_NAME);

    }

    /**
     * m_openHelper instancia de la creacion y versionado de base de datos
     */
    private LunchTimeDbHelper m_openHelper;

    /**
     * buildUriMatcher construye una instancia utilitaria que se usa para ayudar en el match
     * de uris en proveedores de contenido
     *
     * @return todos los paths a el UriMatcher tienen un correspondiente codigo a regresar cuando
     * un match es encontrado. El codigo pasado en el constructor representa el codigo regresado
     * por la raiz del URI.
     */
    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LunchTimeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, LunchTimeContract.PATH_RESTAURANT, RESTAURANT);
        matcher.addURI(authority, LunchTimeContract.PATH_RESTAURANT + File.separator + '*',
                RESTAURANT_WITH_NAME);

        return matcher;

    }

    /**
     * getRestaurantByNameSetting obtiene los restaurantes que coinciden con el nombre del
     * restaurante ingresado en la pantalla de configuracion
     *
     * @param uri        identificados para obtener el restaurante ingresado en la pantalla de
     *                   configuracion
     * @param projection es una lista de los campos a regresar
     * @param sortOrder  establece como se ordenaran los restaurantes obtenidos
     * @return todos los paths a el UriMatcher tienen un correspondiente codigo a regresar cuando
     * un match es encontrado. El codigo pasado en el constructor representa el codigo regresado
     * por la raiz del URI.
     */
    private Cursor getRestaurantByNameSetting(
            final Uri uri, final String[] projection, final String sortOrder) {

        final String restaurantSetting =
                LunchTimeContract.RestaurantEntry.getRestaurantSettingFromUri(uri);

        String[] selectionArgs = null;
        String selection = null;

        if (restaurantSetting != null) {
            selection = S_RESTAURANT_SETTING_SELECTION;
            selectionArgs = new String[]{restaurantSetting};
        }

        return S_RESTAURANT_QUERY_BUILDER.query(m_openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    /**
     * onCreate se implementa para inicializar el proveedor de contenido en el arranque
     *
     * @return verdadero si se inicializar correctamente
     */
    @Override
    public final boolean onCreate() {

        m_openHelper = new LunchTimeDbHelper(getContext());

        return true;

    }

    /**
     * onCreate se implementa para manejar las solicitudes del tipo de datos MIME obtenidos del URI
     *
     * @param uri la uri a consultar
     * @return tipo de datos MIME manejados por el URI
     */
    @Override
    public final String getType(@NonNull final Uri uri) {

        final int match = S_URI_MATCHER.match(uri);

        switch (match) {
            case RESTAURANT:
            case RESTAURANT_WITH_NAME:
                return LunchTimeContract.RestaurantEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    /**
     * query implementa como se manejaran las consultas hechas por el cliente+
     *
     * @param uri      la uri a consultar
     * @param strings  lista de campos a colocar en cursor
     * @param s        criterios de seleccion para filtrar las filas
     * @param strings1 si en los criterios de seleccion se incluye ?s, en este parametro se
     *                 establecen los valores
     * @param s1       establece como las filas seran ordenadas en cursor
     * @return un cursor con los resultados de la consulta
     */
    @Override
    public final Cursor query(@NonNull final Uri uri, final String[] strings, final String s,
                              final String[] strings1, final String s1) {

        final Cursor retCursor;

        switch (S_URI_MATCHER.match(uri)) {
            case RESTAURANT_WITH_NAME:
                retCursor = getRestaurantByNameSetting(uri, strings, s1);
                break;
            case RESTAURANT: {
                retCursor = m_openHelper.getReadableDatabase().query(
                        LunchTimeContract.RestaurantEntry.TABLE_NAME,
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

    /**
     * insert esta implementacion maneja la insercion de una nueva fila
     *
     * @param uri el content:// URI de la solicitud de insercion. Este debe ser no nulo
     * @param contentValues es un set de pares campo/valor para agregar a la base de datos
     * @return la uri de los nuevos valores insertados
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
                        db.insert(LunchTimeContract.RestaurantEntry.TABLE_NAME,
                                null, contentValues);
                if (id > 0L) {
                    returnUri = LunchTimeContract.RestaurantEntry.buildRestaurantUri(id);
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

    /**
     * delete esta implementacion maneja la solicitud de eliminar una o mas filas
     *
     * @param uri la uri completa a consultar, incluyendo un ID si se requiere eliminar solo
     *            una fila
     * @param s una restriccion opcional a aplicar a las filas que serán eliminadas
     * @param strings si en los criterios de seleccion se incluye ?s, en este parametro se
     *                 establecen los valores
     * @return numeros de filas eliminadas
     */
    @Override
    public final int delete(
            @NonNull final Uri uri, final String s, final String[] strings) {

        final SQLiteDatabase db = m_openHelper.getWritableDatabase();
        final int match = S_URI_MATCHER.match(uri);
        String strSelection = s;

        if (strSelection == null) {
            strSelection = "1";
        }
        final int rowsDeleted;
        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                rowsDeleted = db.delete(
                        LunchTimeContract.RestaurantEntry.TABLE_NAME, strSelection, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if ((rowsDeleted != 0) && (getContext() != null)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    /**
     * update esta implementacion maneja la solicitud de actualizar una o mas filas
     *
     * @param uri la uri completa a consultar, incluyendo un ID si se requiere actualizar solo
     *            una fila
     * @param contentValues es un set de pares campo/valor para actualizar a la base de datos
     * @param s una restriccion opcional a aplicar a las filas que serán actualizadas
     * @param strings si en los criterios de seleccion se incluye ?s, en este parametro se
     *                 establecen los valores
     * @return numeros de filas actualizadas
     */
    @Override
    public final int update(@NonNull final Uri uri, final ContentValues contentValues,
                            final String s, final String[] strings) {

        final SQLiteDatabase db = m_openHelper.getWritableDatabase();
        final int match = S_URI_MATCHER.match(uri);
        final int rowsUpdated;

        switch (match) {
            case RESTAURANT_WITH_NAME:
            case RESTAURANT:
                rowsUpdated = db.update(LunchTimeContract.RestaurantEntry.TABLE_NAME,
                        contentValues, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if ((rowsUpdated != 0) && (getContext() != null))  {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }

    /**
     * bulkInsert maneja la insercion de un conjunto de nuevas filas, o la implementacion por
     * defecto será iterar sobre los valores y llamar al metodo insert
     *
     * @param uri la uri completa a insertar
     * @param values es un set de pares campo/valor para insertar a la base de datos
     * @return cantidad de filas insertadas
     */
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
                            LunchTimeContract.RestaurantEntry.TABLE_NAME, null, value);
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

    /**
     * shutdown maneja el cierre del proveedor de contenido
     *
     */
    @Override
    @TargetApi(11)
    public final void shutdown() {

        m_openHelper.close();
        super.shutdown();

    }
}