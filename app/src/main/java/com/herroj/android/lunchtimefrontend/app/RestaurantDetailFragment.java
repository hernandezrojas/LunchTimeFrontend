package com.herroj.android.lunchtimefrontend.app;

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

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;

public class RestaurantDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String RESTAURANT_DETAIL_URI = "URI";

    private static final String RESTAURANT_SHARE_HASHTAG = " #LunchTimeApp";

    private ShareActionProvider mShareActionProvider;

    private String mRestaurant;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry._ID,
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

    private TextView mRestaurantView;
    private TextView mHoraAperturaView;
    private TextView mHoraCierreView;

    public RestaurantDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(RestaurantDetailFragment.RESTAURANT_DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
        mRestaurantView = (TextView) rootView.findViewById(R.id.restaurant_detail_restaurant_textview);
        mHoraAperturaView = (TextView) rootView.findViewById(R.id.restaurant_detail_hora_apertura_textview);
        mHoraCierreView = (TextView) rootView.findViewById(R.id.restaurant_detail_hora_cierre_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.restaurantdetailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mRestaurant != null) {
            mShareActionProvider.setShareIntent(createShareRestaurantIntent());
        }
    }

    private Intent createShareRestaurantIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mRestaurant + RESTAURANT_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onRestaurantChanged(String newRestaurant) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            mUri = RestaurantContract.RestaurantEntry.buildRestaurantporNombreUri(newRestaurant);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {


            // Read date from cursor and update views for day of week and date
            String restaurant = data.getString(COL_RESTAURANT);
            mRestaurantView.setText(restaurant);

            String horaApertura = data.getString(COL_HORA_APERTURA);
            mHoraAperturaView.setText(horaApertura);

            String horaCierre = data.getString(COL_HORA_CIERRE);
            mHoraCierreView.setText(horaCierre);

            // We still need this for the share intent
            mRestaurant = String.format("%s - %s - %s", restaurant, horaApertura, horaCierre);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareRestaurantIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
