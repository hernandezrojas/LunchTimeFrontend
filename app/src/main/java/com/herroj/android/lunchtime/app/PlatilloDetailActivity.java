package com.herroj.android.lunchtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity de restaurantes, que agrega los elementos necesarios dentro
 * de la pantalla de detalle
 */
public class PlatilloDetailActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_platillo_detail);
        if (savedInstanceState == null) {

            /*
            crea un fragment de detalle y lo agrega en la actividad usando
            una transaccion fragment
             */
            final Bundle arguments = new Bundle();
            arguments.putParcelable(
                    PlatilloDetailFragment.PLATILLO_DETAIL_URI, getIntent().getData());

            final PlatilloDetailFragment fragment = new PlatilloDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.platillo_detail_container, fragment)
                    .commit();
        }

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

        // inflar el menu, esto agrega elementos a la barra de accion si esta presente
        getMenuInflater().inflate(R.menu.platillo_detail, menu);
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

        final int id = item.getItemId();

        if (id == R.id.platillo_action_settings) {
            // se invoca la pantalla de configuración
            startActivity(new Intent(this, PlatilloSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}

