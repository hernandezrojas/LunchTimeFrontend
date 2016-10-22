package com.herroj.android.lunchtime.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herroj.android.lunchtime.app.data.RestaurantContract;

public class RestaurantDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String RESTAURANT_DETAIL_URI = "URI";

    private static final String RESTAURANT_SHARE_HASHTAG = " #LunchTimeApp";

    private ShareActionProvider m_shareActionProvider;

    private String m_restaurant;
    private Uri m_uri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            RestaurantContract.RestaurantEntry.TABLE_NAME + '.' +
                    RestaurantContract.RestaurantEntry._ID,
            RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT,
            RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA,
            RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE

            // Sirve para traer despues si se ocupa el tipo de restaurant
            // /// This works because the WeatherProvider returns location data joined with
            // // weather data, even though they're stored in two different tables.
            // WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_RESTAURANT = 1;
    private static final int COL_HORA_APERTURA = 2;
    private static final int COL_HORA_CIERRE = 3;

    private TextView m_restaurantView;
    private TextView m_horaAperturaView;
    private TextView m_horaCierreView;

    public RestaurantDetailFragment() {
        super();
        setHasOptionsMenu(true);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            m_uri = arguments.getParcelable(RESTAURANT_DETAIL_URI);
        }

        final View rootView =
                inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
        m_restaurantView =
                (TextView) rootView.findViewById(R.id.restaurant_detail_restaurant_textview);
        m_horaAperturaView =
                (TextView) rootView.findViewById(R.id.restaurant_detail_hora_apertura_textview);
        m_horaCierreView =
                (TextView) rootView.findViewById(R.id.restaurant_detail_hora_cierre_textview);
        return rootView;
    }

    @Override
    public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.restaurantdetailfragment, menu);

        // Retrieve the share menu item
        final MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        m_shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (m_restaurant != null) {
            m_shareActionProvider.setShareIntent(createShareRestaurantIntent());
        }
    }

    private Intent createShareRestaurantIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, m_restaurant + RESTAURANT_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    final void onRestaurantChanged(final String newRestaurant) {
        if (m_uri != null) {
            m_uri = RestaurantContract.RestaurantEntry.buildRestaurantporNombreUri(newRestaurant);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public final Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        if (m_uri != null) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    m_uri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public final void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if ((data != null) && data.moveToFirst()) {


            // Read date from cursor and update views for day of week and date
            final String restaurant = data.getString(COL_RESTAURANT);
            m_restaurantView.setText(restaurant);

            final String horaApertura = data.getString(COL_HORA_APERTURA);
            m_horaAperturaView.setText(horaApertura);

            final String horaCierre = data.getString(COL_HORA_CIERRE);
            m_horaCierreView.setText(horaCierre);

            // We still need this for the share intent
            m_restaurant = String.format("%s - %s - %s", restaurant, horaApertura, horaCierre);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (m_shareActionProvider != null) {
                m_shareActionProvider.setShareIntent(createShareRestaurantIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
    }

}
