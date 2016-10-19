package com.herroj.android.lunchtimefrontend.app.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;
import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract.RestaurantEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.herroj.android.lunchtimefrontend.app.Utility.darformatoCadenaHora;
import static com.herroj.android.lunchtimefrontend.app.Utility.getStrCampo;

/**
 * Created by Roberto Hernandez on 13/10/2016.
 */

public class LunchTimeService extends IntentService {

    private ArrayAdapter<String> mRestaurantAdapter;
    public static final String RESTAURANT_QUERY_EXTRA = "rqe";
    private final String LOG_TAG = LunchTimeService.class.getSimpleName();

    public LunchTimeService() {
        super("LunchTime");
    }

    String TipoRestaurant;
    String NombreRestaurant;
    String horaApertura;
    String horaCierre;

    @Override
    protected void onHandleIntent(Intent intent) {
        String restaurantQuery = intent.getStringExtra(RESTAURANT_QUERY_EXTRA);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String restaurantJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String RESTAURANT_BASE_URL =
                    "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/com.herroj.lunchtimebackend.restaurant/";
            final String NOMBRE_RESTAURANT_PARAM = "restaurant";

            Uri builtUri = null;

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
            StringBuffer buffer = new StringBuffer();
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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            restaurantJsonStr = buffer.toString();
            getRestaurantDataFromJson(restaurantJsonStr, "pendiente tipo restaurant");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        return;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getRestaurantDataFromJson(String restaurantJsonStr,
                                           String tipoRestaurantSetting)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        final String OWM_TIPO_RESTAURANT_ID = "tipoRestaurantidTipoRestaurant";
        final String OWM_RESTAURANT = "restaurant";
        final String OWM_HORA_APERTURA = "horaApertura";
        final String OWM_HORA_CIERRE = "horaCierre";

        try {

            JSONArray restaurantArray = new JSONArray(restaurantJsonStr);

            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(restaurantArray.length());

            for (int i = 0; i < restaurantArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject objRestaurant = restaurantArray.getJSONObject(i);

                TipoRestaurant = getStrCampo(objRestaurant, OWM_RESTAURANT);

                NombreRestaurant = getStrCampo(objRestaurant, OWM_RESTAURANT);

                horaApertura = darformatoCadenaHora(getStrCampo(objRestaurant, OWM_HORA_APERTURA));
                horaCierre = darformatoCadenaHora(getStrCampo(objRestaurant, OWM_HORA_CIERRE));

                ContentValues restaurantValues = new ContentValues();

                restaurantValues.put(RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID, TipoRestaurant);
                restaurantValues.put(RestaurantEntry.COLUMN_RESTAURANT, NombreRestaurant);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_APERTURA, horaApertura);
                restaurantValues.put(RestaurantEntry.COLUMN_HORA_CIERRE, horaCierre);

                cVVector.add(restaurantValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                this.getContentResolver().delete(RestaurantEntry.CONTENT_URI, null, null);
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(RestaurantEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Lunch Time Service Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, LunchTimeService.class);
            sendIntent.putExtra(LunchTimeService.RESTAURANT_QUERY_EXTRA, intent.getStringExtra(LunchTimeService.RESTAURANT_QUERY_EXTRA));
            context.startService(sendIntent);

        }
    }
}