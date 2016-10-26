package com.herroj.android.lunchtime.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity de la pantalla de configuracion
 */
public class RestaurantSettingsActivity extends PreferenceActivity {

    /**
     * se llama cuando el activity se crea
     *
     * @param savedInstanceState Si la actividad se reinicializa después previamente
     *                           de ser cerrado, este bundle contiene los datos
     *                           que más recientemente ha suministrado en los onSaveInstanceState
     *                           (Bundle). Nota: En caso contrario es nulo.
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new RestaurantSettingsFragment()).commit();

    }

    /**
     * Obtiene un Intent que lanzara un activity explicito por la logica del activity parent
     *
     * @return un nuevo Intent que apunta a los parent de esta activity o nulo si no es un parent
     * valido
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public final Intent getParentActivityIntent() {

        if (super.getParentActivityIntent() != null) {
            return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else {
            return null;

        }
    }

}
