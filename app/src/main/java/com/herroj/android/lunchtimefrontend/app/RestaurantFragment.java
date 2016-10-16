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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;

public class RestaurantFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RESTAURANT_LOADER = 0;

    private static final String[] RESTAURANT_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry._ID,
            RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT,
            RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA,
            RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE,
            RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_RESTAURANT_ID = 0;
    static final int COL_RESTAURANT = 1;
    static final int COL_HORA_APERTURA = 2;
    static final int COL_HORA_CIERRE = 3;
    static final int COL_TIPO_RESTAURANT_ID = 4;

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

        // The CursorAdapter will take data from our cursor and populate the ListView.
        mRestaurantAdapter = new RestaurantAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_restaurant_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_restaurant);
        listView.setAdapter(mRestaurantAdapter);

        // We'll call our MainActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String restaurantSetting = Utility.getPreferredRestaurant(getActivity());
                    Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class)
                            .setData(RestaurantContract.RestaurantEntry.buildRestaurantUri());
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RESTAURANT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateRestaurant() {

        FetchRestaurantTask restaurantTask = new FetchRestaurantTask(getActivity());
        String restaurant = Utility.getPreferredRestaurant(getActivity());

        restaurantTask.execute(restaurant);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateRestaurant();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String restaurantSetting = Utility.getPreferredRestaurant(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT + " ASC";
        Uri restaurantUri = RestaurantContract.RestaurantEntry.buildRestaurantUri();

        return new CursorLoader(getActivity(),
                restaurantUri,
                RESTAURANT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mRestaurantAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mRestaurantAdapter.swapCursor(null);
    }

}