package com.herroj.android.lunchtime.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.BuildConfig;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.herroj.android.lunchtime.app.R;
import com.herroj.android.lunchtime.app.RestaurantMainActivity;
import com.herroj.android.lunchtime.app.Utilidad;
import com.herroj.android.lunchtime.app.data.LunchTimeContract;
import com.herroj.android.lunchtime.app.data.LunchTimeContract.RestaurantEntry;
import com.herroj.android.lunchtime.app.data.LunchTimeContract.PlatilloEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * LunchTimeSyncAdapter es un lugar central para colocar todas las transferencias de datos del
 * dispositivo en un solo lugar
 */
public class LunchTimeSyncAdapter extends AbstractThreadedSyncAdapter {

    /**
     * m_logTag etiqueta que muestra la clase en la que se ejecuta el log
     */
    private final String m_logTag = LunchTimeSyncAdapter.class.getSimpleName();

    /**
     * SYNC_INTERVAL intervalo deseado al cual se sincroniza la aplicacion, en segundos.
     * 60 segundos (1 minuto) * 180 = 3 horas
     */
    private static final int SYNC_INTERVAL = 60 * 180;

    /**
     * SYNC_FLEXTIME la cantidad de tiempo flexible en segundos antes del tiempo deseado
     * que permitas para que la sincronizacion tenga lugar
     */
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    /**
     * DAY_IN_MILLIS la cantidad de milisegundos que tiene un dia
     */
    private static final long DAY_IN_MILLIS = (1000 * 60 * 60 * 24);

    /**
     * RESTAURANT_NOTIFICATION_ID es un identificador único para esta notificación dentro de la
     * aplicación.
     */
    private static final int RESTAURANT_NOTIFICATION_ID = 3004;

    /**
     * SDF_12_HOUR formato con el que se mostrara la hora en pantalla
     */
    private static final SimpleDateFormat SDF_12_HOUR =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());

    /**
     * SDF_24_HOUR formato auxiliar en el proceso de formato de hora a mostrar
     */
    private static final SimpleDateFormat SDF_24_HOUR =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * NOTIFY_RESTAURANT_PROJECTION informacion que mostrará la notificacion
     */
    private static final String[] NOTIFY_RESTAURANT_PROJECTION = {
            RestaurantEntry.TABLE_NAME + '.' + RestaurantEntry._ID,
            RestaurantEntry.COLUMN_RESTAURANT,
            RestaurantEntry.COLUMN_HORA_APERTURA,
            RestaurantEntry.COLUMN_HORA_CIERRE,
            RestaurantEntry.COLUMN_TIPO_RESTAURANT
    };

    /**
     * INDEX_RESTAURANT indice del campo 1 restaurant
     */
    private static final int INDEX_RESTAURANT = 1;

    /**
     * INDEX_HORA_APERTURA indice del campo 2 hora de apertura
     */
    private static final int INDEX_HORA_APERTURA = 2;

    /**
     * INDEX_HORA_CIERRE indice del campo 3 hora de cierre
     */
    private static final int INDEX_HORA_CIERRE = 3;

    /**
     * Instancia un nuevo Lunch Time sync adapter.
     *
     * @param context contexto en el que se ejecutara
     * @param autoInitialize si es verdadero se podra autoinicializar
     */
    LunchTimeSyncAdapter(final Context context, final boolean autoInitialize) {

        super(context, autoInitialize);

    }

    /**
     * onPerformSync realiza una sincronizacion de la cuenta
     *
     * @param account cuenta que debera sincronizarse
     * @param bundle parametros SyncAdapter-specific
     * @param s el authority de la solicitud de sincronizacion
     * @param contentProviderClient un ContentProviderClient que apunta al proveedor de contenido
     *                              de esta authority
     * @param syncResult parametros SyncAdapter-specific
     */
    @Override
    public final void onPerformSync(final Account account, final Bundle bundle, final String s,
                                    final ContentProviderClient contentProviderClient,
                                    final SyncResult syncResult) {

        Log.d(m_logTag, "Starting sync");

        String baseUrl = "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/" +
                "com.herroj.lunchtimebackend.restaurant" + File.separator;
        String json = createJson(baseUrl);
        if (json != null) {
            getRestaurantDataFromJson(json);
        }

        baseUrl = "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/" +
                "com.herroj.lunchtimebackend.platillo" + File.separator;
        json = createJson(baseUrl);
        if (json != null) {
            getPlatilloDataFromJson(createJson(baseUrl));
        }
    }

    public String createJson(final String baseUrl){

        HttpURLConnection urlConnection = null;

        try {

            final Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .build();

            final URL url = new URL(builtUri.toString());

            // se crea una solicitu a Lunch Time Backend, y se abre la conexion
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            final String mediaType = "application/json";
            urlConnection.setRequestProperty("Accept", mediaType);
            urlConnection.setConnectTimeout(10 * 1000);          // 10 s.
            urlConnection.connect();

            // lectura del flujo de entrada a una cadena
            final InputStream inputStream = urlConnection.getInputStream();
            final StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // si no se obtiene nada de la nube, se aborta el proceso de sincronizacion
                return null;
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {

                String line = reader.readLine();
                while (line != null) {
                    buffer.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }

                if (buffer.length() == 0) {
                    // si no se obtiene nada del flujo, no hay que sincronizar
                    return null;
                }

                urlConnection.disconnect();

                return buffer.toString();

            }

        } catch (MalformedURLException e) {
            Log.e(m_logTag, "Error ", e);
        } catch (ProtocolException e) {
            Log.e(m_logTag, "Error ", e);
        } catch (final IOException e) {
            //Log.e(m_logTag, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }



    /**
     * getRestaurantDataFromJson Se tiene un String representando la informacion completa
     * de la aplicacion en formato JSON y hay que extraer los datos necesarios
     * para construir String manejables
     *
     * @param lunchTimeJsonStr cadena que contiene la informacion en formato JSON
     */
    private void getRestaurantDataFromJson(final String lunchTimeJsonStr) {

        try {

            final JSONArray restaurantArray = new JSONArray(lunchTimeJsonStr);

            // Ingresa informacion nueva de restaurant obtenida de la base de datos
            final Collection<ContentValues> cVArrayList = new ArrayList<>(restaurantArray.length());

            final String ownIdRestaurant = "idRestaurant";
            final String ownRestaurant = "restaurant";
            final String ownHoraApertura = "horaApertura";
            final String owmHoraCierre = "horaCierre";
            final String ownTipoRestaurantAux = "tipoRestaurantidTipoRestaurant";
            final String ownTipoRestaurant = "idTipoRestaurant";


            int numRestaurantes = restaurantArray.length();
            ContentValues restaurantValues;

            for (int i = 0; i < numRestaurantes; i++) {

                final JSONObject objRestaurant = restaurantArray.getJSONObject(i);

                String idRestaurant = getStrCampo(objRestaurant, ownIdRestaurant);

                String nombreRestaurant = getStrCampo(objRestaurant, ownRestaurant);

                String horaApertura =
                        darFormatoCadenaHora(getStrCampo(objRestaurant, ownHoraApertura));
                String horaCierre =
                        darFormatoCadenaHora(getStrCampo(objRestaurant, owmHoraCierre));

                String tipoRestaurant = getStrCampo(objRestaurant, ownTipoRestaurantAux);

                JSONObject jsonObjectAux = new JSONObject(tipoRestaurant);

                tipoRestaurant = getStrCampo(jsonObjectAux, ownTipoRestaurant);

                restaurantValues = new ContentValues();

                restaurantValues.put(RestaurantEntry._ID, idRestaurant);
                restaurantValues.put(RestaurantEntry.COLUMN_RESTAURANT, nombreRestaurant);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_APERTURA, horaApertura);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_CIERRE, horaCierre);
                restaurantValues.put(RestaurantEntry.COLUMN_TIPO_RESTAURANT, tipoRestaurant);

                cVArrayList.add(restaurantValues);
            }

            // se agrega a la base de datos local
            if (!cVArrayList.isEmpty()) {
                getContext().getContentResolver().delete(RestaurantEntry.CONTENT_URI, null, null);
                final ContentValues[] cvArray = new ContentValues[cVArrayList.size()];
                cVArrayList.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(RestaurantEntry.CONTENT_URI, cvArray);
                notifyLunchTime();
            }

            if (BuildConfig.DEBUG) {
                Log.d(m_logTag, "Sincronizacion completa. " + cVArrayList.size() + " Insertado");
            }
        } catch (final JSONException e) {
            Log.e(m_logTag, e.getMessage(), e);
        }

    }

    private void getPlatilloDataFromJson(final String lunchTimeJsonStr) {

        try {

            final JSONArray platilloArray = new JSONArray(lunchTimeJsonStr);

            // Ingresa informacion nueva de restaurant obtenida de la base de datos
            final Collection<ContentValues> cVArrayList = new ArrayList<>(platilloArray.length());

            final String ownIdPlatillo = "idPlatillo";
            final String ownPlatillo = "platillo";
            final String ownPrecio = "precio";
            final String ownRestaurantAux = "restaurantidRestaurant";
            final String ownRestaurant = "restaurant";
            final String ownTipoPlatilloAux = "tipoPlatilloidTipoPlatillo";
            final String ownTipoPlatillo = "idTipoPlatillo";


            int numPlatillos = platilloArray.length();
            ContentValues platilloValues;

            for (int i = 0; i < numPlatillos; i++) {

                final JSONObject objPlatillo = platilloArray.getJSONObject(i);

                String idPlatillo = getStrCampo(objPlatillo, ownIdPlatillo);

                String platillo = getStrCampo(objPlatillo, ownPlatillo);

                String precio = getStrCampo(objPlatillo, ownPrecio);


                String restaurant = getStrCampo(objPlatillo, ownRestaurantAux);

                JSONObject jsonObjectAux = new JSONObject(restaurant);

                restaurant = getStrCampo(jsonObjectAux, ownRestaurant);


                String tipoPlatillo = getStrCampo(objPlatillo, ownTipoPlatilloAux);

                jsonObjectAux = new JSONObject(tipoPlatillo);

                tipoPlatillo = getStrCampo(jsonObjectAux, ownTipoPlatillo);

                platilloValues = new ContentValues();

                platilloValues.put(PlatilloEntry._ID, idPlatillo);
                platilloValues.put(PlatilloEntry.COLUMN_PLATILLO, platillo);
                platilloValues.put(PlatilloEntry.COLUMN_PRECIO, precio);
                platilloValues.put(PlatilloEntry.COLUMN_RESTAURANT, restaurant);
                platilloValues.put(PlatilloEntry.COLUMN_TIPO_PLATILLO, tipoPlatillo);

                cVArrayList.add(platilloValues);
            }

            // se agrega a la base de datos local
            if (!cVArrayList.isEmpty()) {
                getContext().getContentResolver().delete(PlatilloEntry.CONTENT_URI, null, null);
                final ContentValues[] cvArray = new ContentValues[cVArrayList.size()];
                cVArrayList.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(PlatilloEntry.CONTENT_URI, cvArray);

            }

            if (BuildConfig.DEBUG) {
                Log.d(m_logTag, "Sincronizacion completa. " + cVArrayList.size() + " Insertado");
            }
        } catch (final JSONException e) {
            Log.e(m_logTag, e.getMessage(), e);
        }

    }

    /**
     * getStrCampo maneja la obtencion de los campos desde el JSONObject
     *
     * @param objeto objeto JSON del cual se extraera la informacion
     * @param campo campo del cual se obtendra la cadena
     * @return devuelve una cadena que representa el campo seleccionado
     */
    private static String getStrCampo(final JSONObject objeto, final String campo) {

        try {
            if (objeto.has(campo)) {
                return objeto.getString(campo);
            }
        } catch (final JSONException e) {
            Log.e(LunchTimeSyncAdapter.class.getSimpleName(), e.getMessage(), e);

        }

        return "";

    }

    /**
     * darFormatoCadenaHora devuelve la hora en el formato de salida deseado
     *
     * @param hora String con la hora sin formato
     * @return devuelve un String de la hora con el formato deseado
     */
    private static String darFormatoCadenaHora(final String hora) {

        String strHora = hora;

        if (strHora.compareTo("") == 0) {
            return strHora;
        }

        strHora = strHora.substring(strHora.indexOf('T') + 1, strHora.length());
        strHora = strHora.substring(0, strHora.indexOf('-') - 3);
        try {
            final Date dateTwentyFourHour = SDF_24_HOUR.parse(strHora);
            strHora = SDF_12_HOUR.format(dateTwentyFourHour);
        } catch (final ParseException e) {
            Log.e(LunchTimeSyncAdapter.class.getSimpleName(), e.getMessage(), e);
        }

        return strHora;

    }

    /**
     * notifyLunchTime metodo que genera la notificacion del proyecto
     */
    private void notifyLunchTime() {

        Context context = getContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String dispNotificationsKey =
                context.getString(R.string.pref_enable_notifications_key);
        final boolean dispNotifications = prefs.getBoolean(dispNotificationsKey,
                Boolean.parseBoolean(
                        context.getString(R.string.pref_enable_notifications_default)));
        final String restaurantPref = Utilidad.getPreferredRestaurant(getContext());

        // verifica que esten activadas las notificaciones en la pantalla de configuacion
        if (dispNotifications) {

            final String lastNotificationKey = context.getString(R.string.pref_last_notification);
            final long lastSync = prefs.getLong(lastNotificationKey, 0L);

            // Si la ultima sincronizacion fue hace um dia o mas, se envia una notificacion
            if ((System.currentTimeMillis() - lastSync) >= DAY_IN_MILLIS) {

                final Uri restaurantUri =
                        LunchTimeContract.RestaurantEntry
                                .buildRestaurantporNombreUri(restaurantPref);

                try ( Cursor cursor = context.getContentResolver()
                        .query(restaurantUri, NOTIFY_RESTAURANT_PROJECTION, null, null, null)){

                    if ((cursor != null) && cursor.moveToFirst()) {
                        final String restaurant = cursor.getString(INDEX_RESTAURANT);
                        final String horaApertura = cursor.getString(INDEX_HORA_APERTURA);
                        final String horaCierre = cursor.getString(INDEX_HORA_CIERRE);

                        final String title = context.getString(R.string.app_name);

                        // Se define el texto de la notificacion
                        final String contentText =
                                String.format(context.getString(R.string.format_notification),
                                        restaurant,
                                        horaApertura,
                                        horaCierre);

                        /*
                        NotificationCompatBuilder es una muy manera muy conveniente de construir
                        notificaciones retrocompatibles
                         */
                        final NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getContext())
                                        .setColor(ContextCompat
                                                .getColor(context, R.color.lunch_time_light_red))
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle(title)
                                        .setContentText(contentText);

                        // Cuando se hace doble click sobre la notificacion se abre la aplicacion
                        final Intent resultIntent =
                                new Intent(context, RestaurantMainActivity.class);

                        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntent(resultIntent);
                        final PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);

                        final NotificationManager mNotificationManager =
                                (NotificationManager) getContext()
                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                        // RESTAURANT_NOTIFICATION_ID te permite actualizar la notificacion despues
                        mNotificationManager.notify(RESTAURANT_NOTIFICATION_ID, mBuilder.build());

                        // actualiza variable de ultima notificacion
                        final SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong(lastNotificationKey, System.currentTimeMillis());
                        editor.apply();
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }


    /**
     * syncImmediately metodo helper que sirve para sincronizar inmediatamente el sync adapter
     *
     * @param context El contexto usado para acceder al servicio de la cuena
     */
    public static void syncImmediately(final Context context) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * configurePeriodicSync metodo helper para programar la ejecucion
     * de sync adapter periodicamente
     *
     * @param context El contexto usado para acceder al servicio de la cuena
     * @param syncInterval intervalo deseado al cual se sincroniza la aplicacion
     * @param flexTime la cantidad de tiempo flexible en segundos antes del tiempo deseado
     *                 que permitas para que la sincronizacion tenga lugar
     */
    private static void configurePeriodicSync(
            final Context context, final int syncInterval, final int flexTime) {
        final Account account = getSyncAccount(context);
        final String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // podemos habilitar timers inexactos en nuestra sincronizacion periodica
            final SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * getSyncAccount metodo helper para obtener una cuenta fake a ser usada con SyncApater,
     * o hacer un nuevo si la cuenta fake no existe
     *
     * @param context context a ser usado para ser accesado al servicio de la cuenta
     * @return una cuenta fake
     */
    private static Account getSyncAccount(final Context context) {
        // obtiene una cuenta del Android account manager
        final AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // crea la account type y una cuenta predeterminada
        final Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // si la contraseña no existe, la cuenta no existe
        if (accountManager.getPassword(newAccount) == null) {

        /*
         * Agrega la cuenta y el tipo de cuenta, sin contraseña o datos de usuario
         * Si es exitosam regresa un objeto Account, de otro modo reporta un error
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * Si no se establece android:syncable="true" en el elemento <provider> en el manifiesto
             * despues llama ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             */
            onAccountCreated(newAccount, context);
        }

        return newAccount;

    }

    /**
     * onAccountCreated metodo que se ejecuta al crear la cuenta
     *
     * @param newAccount la nueva cuenta que se procesara
     * @param context contexto que se usara
     */
    private static void onAccountCreated(final Account newAccount, final Context context) {

        // creamos una cuenta
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        // sin llamar setSyncAutomatically, nuestra sincronizacion periodica podria no se habilitada
        ContentResolver.setSyncAutomatically(
                newAccount, context.getString(R.string.content_authority), true);

        // finalmente, se hace una sincronizacion para comenzar
        syncImmediately(context);

    }

    /**
     * Initializa el sync adapter.
     *
     * @param context el contexto que se usara
     */
    public static void initializeSyncAdapter(final Context context) {

        getSyncAccount(context);

    }

}
