package com.herroj.android.lunchtime.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Clase utilitaria para toda la aplicacion
 */
public final class Utilidad {

    /**
     * Obtiene el restaurant que esta ingresado en la pantalla de configuracion
     *
     * @param context el context que se usara
     * @return devuelve el nombre del restaurante ingresado en la pantalla de configuracion
     */
    public static String getPreferredRestaurant(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_restaurant_key),
                context.getString(R.string.pref_restaurant_default));
    }

    public static String getPreferredPlatillo(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_platillo_key),
                context.getString(R.string.pref_platillo_default));
    }

}
