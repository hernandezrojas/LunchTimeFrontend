package com.herroj.android.lunchtime.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.herroj.android.lunchtime.app.data.RestaurantContract;
import com.herroj.android.lunchtime.app.sync.LunchTimeSyncAdapter;

public class RestaurantFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RestaurantAdapter m_restaurantAdapter;

    private ListView m_listView;
    private int m_position = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int RESTAURANT_LOADER = 0;

    private static final String[] RESTAURANT_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            RestaurantContract.RestaurantEntry.TABLE_NAME + '.' +
                    RestaurantContract.RestaurantEntry._ID,
            RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT,
            RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA,
            RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_RESTAURANT = 1;
    static final int COL_HORA_APERTURA = 2;
    static final int COL_HORA_CIERRE = 3;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

    /*
        2.04 inflate menu

        se agrega el elemento del menu actualizar al menu, aun sin ejecutar alguna acción
        en el punto 2.04

      */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {

        // The RestaurantAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        m_restaurantAdapter = new RestaurantAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_restaurant_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        m_listView = (ListView) rootView.findViewById(R.id.listview_restaurant);
        m_listView.setAdapter(m_restaurantAdapter);

        // We'll call our MainActivity
        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> adapterView,
                                    final View view, final int i, final long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                final Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(RestaurantContract.RestaurantEntry.
                                    buildRestaurantporNombreUri(cursor.getString(COL_RESTAURANT)));
                }
                m_position = i;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if ((savedInstanceState != null) && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            m_position = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        getLoaderManager().initLoader(RESTAURANT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    final void onLocationChanged() {
        updateRestaurant();
        getLoaderManager().restartLoader(RESTAURANT_LOADER, null, this);
    }

    private void updateRestaurant() {
        LunchTimeSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (m_position != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, m_position);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public final Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        final String sortOrder = RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT + " ASC";

        final Uri restaurantUri = RestaurantContract.RestaurantEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                restaurantUri,
                RESTAURANT_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public final void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        m_restaurantAdapter.swapCursor(data);
        if (m_position != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            m_listView.smoothScrollToPosition(m_position);
        }
    }

    @Override
    public final void onLoaderReset(final Loader<Cursor> loader) {
        m_restaurantAdapter.swapCursor(null);
    }

}