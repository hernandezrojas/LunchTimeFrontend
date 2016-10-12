package com.herroj.android.lunchtimefrontend.app;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RHR
 * <p>
 * 1.01_hola_mundo
 * Esta clase representa la ventana principal de los restaurantes, contiene un menú
 * con un elemento que se llama configuración, esta pantalla muestra un fragment
 * que contiene el texto de hola mundo
 *
 * 1.04 Creación de dummy datos
 *
 * 1.05 Create ArrayAdapter to eventually use to populate the ListView
 *
 * 1.06 Get a reference to the ListView, and attach this adapter to it.
 *
 */

public class RestaurantMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.restaurant_container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.restaurant_action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ArrayAdapter<String> mRestaurantAdapter;

        public PlaceholderFragment() {
        }

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

            // 1.06 Get a reference to the ListView, and attach this adapter to it.
            ListView listView = (ListView) rootView.findViewById(R.id.listview_restaurant);
            listView.setAdapter(mRestaurantAdapter);

            /*
                RHR
                2.01 Add the network call code

                Nota: es normal que no corra la aplicacion en este punto, hay que hacer algo mas
            */

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
                String baseUrl = "http://robertofcfm.mooo.com:8080/LunchTimeBackend/webresources/com.herroj.lunchtimebackend.restaurant";
                URL url = new URL(baseUrl);
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                //String apiKey = "&APPID=" + "\"9df3e8a2ea20a727bb82439b8aa424cf\"";
                //URL url = new URL(baseUrl.concat(apiKey));

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
                restaurantJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }  finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }


            // Fin 2.01

            return rootView;
        }
    }
}
