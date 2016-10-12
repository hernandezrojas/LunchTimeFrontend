package com.herroj.android.lunchtimefrontend.app;

/**
 * Created by Roberto Hernandez on 10/10/2016.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantFragment extends Fragment {

    private ArrayAdapter<String> mRestaurantAdapter;

    public RestaurantFragment() {
    }

    /*
        2.04 inflate menu

        se agrega el elemento del menu actualizar al menu, aun sin ejecutar alguna acción
        en el punto 2.04

      */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.restaurantfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.restaurant_action_refresh) {

            /* RHR
                2.05 execute fetchestauranttask
             */

            FetchRestaurantTask restaurantTask = new FetchRestaurantTask();
            restaurantTask.execute("-1");

            // Fin 2.05 execute fetchestauranttask

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Fin 2.04 inflate menu

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         /*
                RHR

                1.04 Creación de dummy datos para mostrarlos por el momento
         */

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "FCQ - 7:00 - 19:00 - Cafetería",
                "Hotdogs - 8:30 - 19:30 - Puesto",
                "FOD -  -  - Cafetería",
                "FCFM - 8:00 - 17:00 - Cafetería",
                "Gimnasio - 6:00 - 19:00 - Cafetería",
                "FACPYA - 8:00 - 21:00 - Cafetería",
                "FCI - 7:00 - 20:30 - Cafetería"
        };
        List<String> restaurantes = new ArrayList<String>(Arrays.asList(data));
        // Termina 1.04 Creación de dummy datos


        /*
                RHR

                1.05 Create ArrayAdapter to eventually use to populate the ListView
        */
        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mRestaurantAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_restaurant, // The name of the layout ID.
                        R.id.list_item_restaurant_textview, // The ID of the textview to populate.
                        restaurantes);
        // 1.05 Create ArrayAdapter to eventually use to populate the ListView


        View rootView = inflater.inflate(R.layout.fragment_restaurant_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_restaurant);
        listView.setAdapter(mRestaurantAdapter);

        return rootView;
    }

    public class FetchRestaurantTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchRestaurantTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {


            // RHR 2.07 Build URL with params

            if (params.length == 0) {
                return null;
            }

            /*
                RHR
                2.01 Add the network call code

                Nota: es normal en el 2.01 que no corra la aplicacion en este punto,
                hay que hacer algo mas
            */

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                //ahora con el url perteneciente al proyecto Lunch Time
                final String RESTAURANT_BASE_URL = "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/com.herroj.lunchtimebackend.restaurant/";
                final String ID_PARAM = "IdRestaurant";

                Uri builtUri = null;

                if (params[0].compareTo("-1") == 0) {
                    builtUri = Uri.parse(RESTAURANT_BASE_URL).buildUpon()
                            .build();
                } else {
                    builtUri = Uri.parse(RESTAURANT_BASE_URL).buildUpon()
                            .appendQueryParameter(ID_PARAM, params[0])
                            .build();
                }

                URL url = new URL(builtUri.toString());
                /*
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));
                */

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
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
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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
            return null;
        }
    }
}