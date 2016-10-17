package com.herroj.android.lunchtimefrontend.app;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;

public class RestaurantDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RestaurantDetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.restaurant_action_settings) {
            // se invoca la pantalla de configuración
            startActivity(new Intent(this, RestaurantSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class RestaurantDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = RestaurantDetailFragment.class.getSimpleName();

        private static final String RESTAURANT_SHARE_HASHTAG = " #LunchTimeApp";

        private ShareActionProvider mShareActionProvider;

        private String mRestaurant;

        private static final int DETAIL_LOADER = 0;

        private static final String[] RESTAURANT_COLUMNS = {
                RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry._ID,
                RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT,
                RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA,
                RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE,
                RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
        static final int COL_RESTAURANT_ID = 0;
        static final int COL_RESTAURANT = 1;
        static final int COL_HORA_APERTURA = 2;
        static final int COL_HORA_CIERRE = 3;
        static final int COL_TIPO_RESTAURANT_ID = 4;

        public RestaurantDetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
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
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mRestaurant + RESTAURANT_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    RESTAURANT_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) {
                return;
            }

            String restaurant = data.getString(COL_RESTAURANT);

            String horaApertura = data.getString(COL_HORA_APERTURA);

            String horaCierre = data.getString(COL_HORA_CIERRE);


            mRestaurant = String.format("%s - %s - %s", restaurant, horaApertura, horaCierre);

            TextView detailTextView = (TextView) getView().findViewById(R.id.restaurant_detail_text);
            detailTextView.setText(mRestaurant);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareRestaurantIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

    }
}

