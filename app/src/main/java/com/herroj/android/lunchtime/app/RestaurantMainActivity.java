package com.herroj.android.lunchtime.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.herroj.android.lunchtime.app.sync.LunchTimeSyncAdapter;

/**
 * RHR
 * <p>
 * 1.01_hola_mundo
 * Esta clase representa la ventana principal de los restaurantes, contiene un menú
 * con un elemento que se llama configuración, esta pantalla muestra un fragment
 * que contiene el texto de hola mundo
 * <p>
 * 1.04 Creación de dummy datos
 * <p>
 * 1.05 Create ArrayAdapter to eventually use to populate the ListView
 * <p>
 * 1.06 Get a reference to the ListView, and attach this adapter to it.
 */

public class RestaurantMainActivity extends AppCompatActivity
        implements RestaurantFragment.Callback {

    private static final String RESTAURANTDETAILFRAGMENT_TAG = "RDFTAG";

    private boolean m_twoPane;

    private String m_restaurant;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
        if (findViewById(R.id.restaurant_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            m_twoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.restaurant_detail_container,
                                new RestaurantDetailFragment(), RESTAURANTDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            m_twoPane = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0.0f);
            }
        }
        LunchTimeSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_main, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.restaurant_action_settings) {
            // se invoca la pantalla de configuración
            startActivity(new Intent(this, RestaurantSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected final void onResume() {
        super.onResume();
        final String restaurant = Utilidad.getPreferredRestaurant(this);
        // update the location in our second pane using the fragment manager
        if ((restaurant != null) && !restaurant.equals(m_restaurant)) {
            final RestaurantFragment restaurantFragment =
                    (RestaurantFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_restaurant);
            if (restaurantFragment != null) {
                restaurantFragment.onLocationChanged();
            }
            final RestaurantDetailFragment restaurantDetailFragment =
                    (RestaurantDetailFragment) getSupportFragmentManager()
                            .findFragmentByTag(RESTAURANTDETAILFRAGMENT_TAG);
            if (restaurantDetailFragment != null) {
                restaurantDetailFragment.onRestaurantChanged(restaurant);
            }
            m_restaurant = restaurant;
        }
    }

    @Override
    public final void onItemSelected(final Uri dateUri) {
        if (m_twoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            final Bundle args = new Bundle();
            args.putParcelable(RestaurantDetailFragment.RESTAURANT_DETAIL_URI, dateUri);

            final RestaurantDetailFragment fragment = new RestaurantDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.restaurant_detail_container,
                            fragment, RESTAURANTDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            final Intent intent = new Intent(this, RestaurantDetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }

}
