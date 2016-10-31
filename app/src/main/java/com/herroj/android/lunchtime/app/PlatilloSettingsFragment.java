package com.herroj.android.lunchtime.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


/**
 * fragment de la pantalla de configuracion
 */
public class PlatilloSettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener{

    /**
     * es llamado al inicializar la creacion de un fragment
     *
     * @param savedInstanceState Si el fragment se vuelve a crear desde un estado guardado, este
     *                           es el estado
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.platillo_pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_platillo_key)));

    }

    /**
     * Se fija un listener por lo que el summary siempre se actualiza con el valor de preferencia.
     * Tambien lanza el listener una vez, para inicializar el summary (por lo que se muestra antes
     * de que cambia de valor)
     *
     * @param preference preferencia a enlazar
     */
    private void bindPreferenceSummaryToValue(final Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Llamado cuando un Preference ha sido cambiado por el usuario
     *
     * @param preference Preference cambiado
     * @param o el nuevo valor de la Preference
     * @return devuelve true si el valor es cambiado
     */
    @Override
    public final boolean onPreferenceChange(final Preference preference, final Object o) {
        final String stringValue = o.toString();

        if (preference instanceof ListPreference) {
            final ListPreference listPreference = (ListPreference) preference;
            final int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

}
