package com.herroj.android.lunchtimefrontend.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.herroj.android.lunchtimefrontend.app.sync.LunchTimeSyncAdapter;

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

    private boolean mTwoPane;

    private String mRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
        if (findViewById(R.id.restaurant_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
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
            mTwoPane = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0f);
            }
        }
        LunchTimeSyncAdapter.initializeSyncAdapter(this);
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

        if (item.getItemId() == R.id.restaurant_action_settings) {
            // se invoca la pantalla de configuración
            startActivity(new Intent(this, RestaurantSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String restaurant = Utility.getPreferredRestaurant(this);
        // update the location in our second pane using the fragment manager
        if (restaurant != null && !restaurant.equals(mRestaurant)) {
            RestaurantFragment ff =
                    (RestaurantFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_restaurant);
            if (null != ff) {
                ff.onLocationChanged();
            }
            RestaurantDetailFragment df =
                    (RestaurantDetailFragment) getSupportFragmentManager()
                            .findFragmentByTag(RESTAURANTDETAILFRAGMENT_TAG);
            if (null != df) {
                df.onRestaurantChanged(restaurant);
            }
            mRestaurant = restaurant;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(RestaurantDetailFragment.RESTAURANT_DETAIL_URI, contentUri);

            RestaurantDetailFragment fragment = new RestaurantDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.restaurant_detail_container,
                            fragment, RESTAURANTDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, RestaurantDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

}
