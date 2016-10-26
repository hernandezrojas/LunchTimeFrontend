package com.herroj.android.lunchtime.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.herroj.android.lunchtime.app.sync.LunchTimeSyncAdapter;

/**
 * Activity de restaurantes, que agrega los elementos necesarios dentro
 * de la pantalla de detalle
 */
public class RestaurantMainActivity extends AppCompatActivity
        implements RestaurantFragment.Callback {

    /**
     * etiqueta que identifica al fragment del detalle de los restaurantes
     */
    private static final String RESTAURANTDETAILFRAGMENT_TAG = "RDFTAG";

    /**
     * variable que indica si se van a mostrar uno o dos paneles a la vez
     */
    private boolean m_twoPane;

    /**
     * restaurant elegido en la pantalla de configuracion
     */
    private String m_restaurant;

    /**
     * se llama cuando el activity se crea
     *
     * @param savedInstanceState Si la actividad se reinicializa después previamente
     *                           de ser cerrado, este bundle contiene los datos
     *                           que más recientemente ha suministrado en los onSaveInstanceState
     *                           (Bundle). Nota: En caso contrario es nulo.
     */
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);
        if (findViewById(R.id.restaurant_detail_container) != null) {

            m_twoPane = true;

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

    /**
     * Inicializar el contenido del menú de opciones estándar de la actividad.
     * se debe colocar sus elementos de menú al menú.
     *
     * @param menu El menu de opciones en la que colocan los articulos
     * @return verdadero si sera visto
     */
    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.restaurant_main, menu);

        return true;

    }

    /**
     * este metodo se llama cada vez que se elige un elemento del menu
     *
     * @param item el elemento del menu seleccionado
     * @return regresa falso si se procesa de manera normal, verdadero si se consume
     */
    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {

        if (item.getItemId() == R.id.restaurant_action_settings) {
            // se invoca la pantalla de configuración
            startActivity(new Intent(this, RestaurantSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Llamado cuando el fragment es visible al usuario y corriendo activamente
     */
    @Override
    protected final void onResume() {
        super.onResume();
        final String restaurant = Utilidad.getPreferredRestaurant(this);

        if ((restaurant != null) && !restaurant.equals(m_restaurant)) {
            final RestaurantFragment restaurantFragment =
                    (RestaurantFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_restaurant);
            if (restaurantFragment != null) {
                restaurantFragment.onRestaurantChanged();
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

    /**
     * este metodo implementa la interfaz del fragment de onItemSelected
     *
     * @param restaurantUri uri del elemento seleccionado
     */
    @Override
    public final void onItemSelected(final Uri restaurantUri) {
        if (m_twoPane) {
            final Bundle args = new Bundle();
            args.putParcelable(RestaurantDetailFragment.RESTAURANT_DETAIL_URI, restaurantUri);

            final RestaurantDetailFragment fragment = new RestaurantDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.restaurant_detail_container,
                            fragment, RESTAURANTDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            final Intent intent = new Intent(this, RestaurantDetailActivity.class)
                    .setData(restaurantUri);
            startActivity(intent);
        }
    }

}
