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
import com.herroj.android.lunchtime.app.data.RestaurantContract;
import com.herroj.android.lunchtime.app.data.RestaurantContract.RestaurantEntry;

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

    private final String m_logTag = LunchTimeSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = (1000 * 60 * 60 * 24);
    private static final int RESTAURANT_NOTIFICATION_ID = 3004;
    private static final SimpleDateFormat SDF_12_HOUR =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat SDF_24_HOUR =
            new SimpleDateFormat("HH:mm", Locale.getDefault());


    private static final String[] NOTIFY_RESTAURANT_PROJECTION = {
            RestaurantEntry.COLUMN_RESTAURANT,
            RestaurantEntry.COLUMN_HORA_APERTURA,
            RestaurantEntry.COLUMN_HORA_CIERRE
    };

    // these indices must match the projection
    private static final int INDEX_RESTAURANT = 0;
    private static final int INDEX_HORA_APERTURA = 1;
    private static final int INDEX_HORA_CIERRE = 2;

    /**
     * Instantiates a new Lunch time sync adapter.
     *
     * @param context        the context
     * @param autoInitialize the auto initialize
     */
    LunchTimeSyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public final void onPerformSync(final Account account, final Bundle bundle, final String s,
                                    final ContentProviderClient contentProviderClient,
                                    final SyncResult syncResult) {
        Log.d(m_logTag, "Starting sync");
        final String restaurantQuery = Utilidad.getPreferredRestaurant(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String restaurantBaseUrl =
                    "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/" +
                            "com.herroj.lunchtimebackend.restaurant" + File.separator;

            final Uri builtUri;

            if (restaurantQuery.compareTo("") == 0) {
                builtUri = Uri.parse(restaurantBaseUrl).buildUpon()
                        .build();
            } else {
                final String nomRestaurantParam = "restaurant";
                builtUri = Uri.parse(restaurantBaseUrl + nomRestaurantParam + '=' +
                        restaurantQuery).buildUpon().build();
            }

            final URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            final String mediaType = "application/json";
            urlConnection.setRequestProperty("Accept", mediaType);
            urlConnection.connect();

            // Read the input stream into a String
            final InputStream inputStream = urlConnection.getInputStream();
            final StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {

                String line = reader.readLine();
                while (line != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                final String restaurantJsonStr = buffer.toString();
                getRestaurantDataFromJson(restaurantJsonStr);
            }

        } catch (MalformedURLException e) {
            Log.e(m_logTag, "Error ", e);
        } catch (ProtocolException e) {
            Log.e(m_logTag, "Error ", e);
        } catch (final IOException e) {
            Log.e(m_logTag, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getRestaurantDataFromJson(final String restaurantJsonStr) {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        try {

            final JSONArray restaurantArray = new JSONArray(restaurantJsonStr);

            // Insert the new weather information into the database
            final Collection<ContentValues> cVArrayList = new ArrayList<>(restaurantArray.length());

            final String ownRestaurant = "restaurant";
            final String ownHoraApertura = "horaApertura";
            final String owmHoraCierre = "horaCierre";

            int numRestaurantes = restaurantArray.length();
            ContentValues restaurantValues;

            for (int i = 0; i < numRestaurantes; i++) {

                // Get the JSON object representing the day
                final JSONObject objRestaurant = restaurantArray.getJSONObject(i);

                String nombreRestaurant = getStrCampo(objRestaurant, ownRestaurant);

                String horaApertura =
                        darformatoCadenaHora(getStrCampo(objRestaurant, ownHoraApertura));
                String horaCierre =
                        darformatoCadenaHora(getStrCampo(objRestaurant, owmHoraCierre));

                restaurantValues = new ContentValues();
                restaurantValues.put(RestaurantEntry.COLUMN_RESTAURANT, nombreRestaurant);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_APERTURA, horaApertura);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_CIERRE, horaCierre);

                cVArrayList.add(restaurantValues);
            }

            // add to database
            if (!cVArrayList.isEmpty()) {
                getContext().getContentResolver().delete(RestaurantEntry.CONTENT_URI, null, null);
                final ContentValues[] cvArray = new ContentValues[cVArrayList.size()];
                cVArrayList.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(RestaurantEntry.CONTENT_URI, cvArray);
                notifyLunchTime();
            }

            if (BuildConfig.DEBUG) {
                Log.d(m_logTag, "Sync Complete. " + cVArrayList.size() + " Inserted");
            }
        } catch (final JSONException e) {
            Log.e(m_logTag, e.getMessage(), e);
        }

    }

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

    private static String darformatoCadenaHora(final String hora) {

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

    private void notifyLunchTime() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        final String dispNotificationsKey =
                context.getString(R.string.pref_enable_notifications_key);
        final boolean dispNotifications = prefs.getBoolean(dispNotificationsKey,
                Boolean.parseBoolean(
                        context.getString(R.string.pref_enable_notifications_default)));

        if (dispNotifications) {

            final String lastNotificationKey = context.getString(R.string.pref_last_notification);
            final long lastSync = prefs.getLong(lastNotificationKey, 0L);

            if ((System.currentTimeMillis() - lastSync) >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                final String restaurantQuery = Utilidad.getPreferredRestaurant(context);

                final Uri weatherUri = RestaurantContract.RestaurantEntry
                        .buildRestaurantporNombreUri(restaurantQuery);

                // we'll query our contentProvider, as always
                try ( Cursor cursor = context.getContentResolver()
                        .query(weatherUri, NOTIFY_RESTAURANT_PROJECTION, null, null, null)){

                    if ((cursor != null) && cursor.moveToFirst()) {
                        final String restaurant = cursor.getString(INDEX_RESTAURANT);
                        final String horaApertura = cursor.getString(INDEX_HORA_APERTURA);
                        final String horaCierre = cursor.getString(INDEX_HORA_CIERRE);

                        final String title = context.getString(R.string.app_name);

                        // Define the text of the forecast.
                        final String contentText =
                                String.format(context.getString(R.string.format_notification),
                                        restaurant,
                                        horaApertura,
                                        horaCierre);

                        // NotificationCompatBuilder is a very convenient
                        // way to build backward-compatible
                        // notifications.  Just throw in some data.
                        final NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getContext())
                                        .setColor(ContextCompat
                                                .getColor(context, R.color.lunch_time_light_red))
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle(title)
                                        .setContentText(contentText);

                        // Make something interesting happen when the user clicks on
                        // the notification.
                        // In this case, opening the app is sufficient.
                        final Intent resultIntent =
                                new Intent(context, RestaurantMainActivity.class);

                        // The stack builder object will contain an artificial back stack for the
                        // started Activity.
                        // This ensures that navigating backward from the Activity leads out of
                        // your application to the Home screen.
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
                        // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                        mNotificationManager.notify(RESTAURANT_NOTIFICATION_ID, mBuilder.build());

                        //refreshing last sync
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
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(final Context context) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /*
         * Helper method to schedule the sync adapter periodic execution
         */
    private static void configurePeriodicSync(
            final Context context, final int syncInterval, final int flexTime) {
        final Account account = getSyncAccount(context);
        final String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
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
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(final Context context) {
        // Get an instance of the Android account manager
        final AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        final Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(final Account newAccount, final Context context) {
                /*
         * Since we've created an account
         */
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

                       /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(
                newAccount, context.getString(R.string.content_authority), true);

                        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Initialize sync adapter.
     *
     * @param context the context
     */
    public static void initializeSyncAdapter(final Context context) {
        getSyncAccount(context);
    }
}
