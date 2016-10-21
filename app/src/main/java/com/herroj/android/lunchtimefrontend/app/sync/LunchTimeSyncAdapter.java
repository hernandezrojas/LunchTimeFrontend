package com.herroj.android.lunchtimefrontend.app.sync;

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

import com.herroj.android.lunchtimefrontend.app.R;
import com.herroj.android.lunchtimefrontend.app.RestaurantMainActivity;
import com.herroj.android.lunchtimefrontend.app.Utility;
import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;
import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract.RestaurantEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * LunchTimeSyncAdapter es un lugar central para colocar todas las transferencias de datos del
 * dispositivo en un solo lugar
 */

public class LunchTimeSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = LunchTimeSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int RESTAURANT_NOTIFICATION_ID = 3004;
    private static final SimpleDateFormat _12HourSDF =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private static final SimpleDateFormat _24HourSDF =
            new SimpleDateFormat("HH:mm", Locale.getDefault());


    private static final String[] NOTIFY_RESTAURANT_PROJECTION = new String[]{
            RestaurantEntry.COLUMN_RESTAURANT,
            RestaurantEntry.COLUMN_HORA_APERTURA,
            RestaurantEntry.COLUMN_HORA_CIERRE
    };

    // these indices must match the projection
    private static final int INDEX_RESTAURANT = 0;
    private static final int INDEX_HORA_APERTURA = 1;
    private static final int INDEX_HORA_CIERRE = 2;

    LunchTimeSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String restaurantQuery = Utility.getPreferredRestaurant(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String restaurantJsonStr;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String RESTAURANT_BASE_URL =
                    "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/" +
                            "com.herroj.lunchtimebackend.restaurant/";
            final String NOMBRE_RESTAURANT_PARAM = "restaurant";

            Uri builtUri;

            if (restaurantQuery.compareTo("") == 0) {
                builtUri = Uri.parse(RESTAURANT_BASE_URL).buildUpon()
                        .build();
            } else {
                builtUri = Uri.parse(RESTAURANT_BASE_URL + NOMBRE_RESTAURANT_PARAM + "=" +
                        restaurantQuery).buildUpon().build();
            }

            URL url = new URL(builtUri.toString());

            final String MEDIA_TYPE = "application/json";
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", MEDIA_TYPE);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            restaurantJsonStr = buffer.toString();
            getRestaurantDataFromJson(restaurantJsonStr);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
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
    private void getRestaurantDataFromJson(String restaurantJsonStr) {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        final String OWM_RESTAURANT = "restaurant";
        final String OWM_HORA_APERTURA = "horaApertura";
        final String OWM_HORA_CIERRE = "horaCierre";

        String NombreRestaurant;
        String horaApertura;
        String horaCierre;

        try {

            JSONArray restaurantArray = new JSONArray(restaurantJsonStr);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<>(restaurantArray.length());

            for (int i = 0; i < restaurantArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject objRestaurant = restaurantArray.getJSONObject(i);

                NombreRestaurant = getStrCampo(objRestaurant, OWM_RESTAURANT);

                horaApertura = darformatoCadenaHora(getStrCampo(objRestaurant, OWM_HORA_APERTURA));
                horaCierre = darformatoCadenaHora(getStrCampo(objRestaurant, OWM_HORA_CIERRE));

                ContentValues restaurantValues = new ContentValues();

                restaurantValues.put(RestaurantEntry.COLUMN_RESTAURANT, NombreRestaurant);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_APERTURA, horaApertura);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_CIERRE, horaCierre);

                cVVector.add(restaurantValues);
            }

            // add to database
            if (cVVector.size() > 0) {
                getContext().getContentResolver().delete(RestaurantEntry.CONTENT_URI, null, null);
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(RestaurantEntry.CONTENT_URI, cvArray);
                notifyLunchTime();
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private static String getStrCampo(JSONObject objeto, String campo) {

        try {
            if (objeto.has(campo)) {
                return objeto.getString(campo);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return "";

    }

    private static String darformatoCadenaHora(String hora) {

        if (hora.compareTo("") == 0) {
            return hora;
        }

        hora = hora.substring(hora.indexOf('T') + 1, hora.length());
        hora = hora.substring(0, hora.indexOf('-') - 3);
        try {
            Date _24HourDt = _24HourSDF.parse(hora);
            hora = _12HourSDF.format(_24HourDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hora;
    }

    private void notifyLunchTime() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(
                        context.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications) {

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                String restaurantQuery = Utility.getPreferredRestaurant(context);

                Uri weatherUri = RestaurantContract.RestaurantEntry
                        .buildRestaurantporNombreUri(restaurantQuery);

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().
                        query(weatherUri, NOTIFY_RESTAURANT_PROJECTION, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    String restaurant = cursor.getString(INDEX_RESTAURANT);
                    String horaApertura = cursor.getString(INDEX_HORA_APERTURA);
                    String horaCierre = cursor.getString(INDEX_HORA_CIERRE);

                    String title = context.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText =
                            String.format(context.getString(R.string.format_notification),
                            restaurant,
                            horaApertura,
                            horaCierre);

                    // NotificationCompatBuilder is a very convenient
                    // way to build backward-compatible
                    // notifications.  Just throw in some data.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setColor(ContextCompat
                                            .getColor(context, R.color.lunch_time_light_red))
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(context, RestaurantMainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext()
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(RESTAURANT_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.apply();
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /*
         * Helper method to schedule the sync adapter periodic execution
         */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
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
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

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

    private static void onAccountCreated(Account newAccount, Context context) {
                /*
         * Since we've created an account
         */
        LunchTimeSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

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

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
