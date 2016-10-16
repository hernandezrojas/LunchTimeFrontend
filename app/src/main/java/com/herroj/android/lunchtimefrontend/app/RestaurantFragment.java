package com.herroj.android.lunchtimefrontend.app;

/**
 * Created by Roberto Hernandez on 10/10/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;

public class RestaurantFragment extends Fragment {

    private RestaurantAdapter mRestaurantAdapter;

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
            updateRestaurant();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Sort order:  Ascending, by date.
        String sortOrder = RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT + " ASC";
        Uri restaurantUri = RestaurantContract.RestaurantEntry.buildRestaurantUri();

        Cursor cur = getActivity().getContentResolver().query(restaurantUri,
                null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        mRestaurantAdapter = new RestaurantAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_restaurant_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_restaurant);
        listView.setAdapter(mRestaurantAdapter);

        return rootView;
    }

    private void updateRestaurant() {

        FetchRestaurantTask restaurantTask = new FetchRestaurantTask(getActivity());
        String restaurant = Utility.getPreferredLocation(getActivity());

        restaurantTask.execute(restaurant);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateRestaurant();
    }

}