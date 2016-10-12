package com.herroj.android.lunchtimefrontend.app;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

        ArrayAdapter<String> mForecastAdapter;

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
            mForecastAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_restaurant, // The name of the layout ID.
                            R.id.list_item_restaurant_textview, // The ID of the textview to populate.
                            restaurantes);

            // 1.05 Create ArrayAdapter to eventually use to populate the ListView

            View rootView = inflater.inflate(R.layout.fragment_restaurant_main, container, false);
            return rootView;
        }
    }
}
