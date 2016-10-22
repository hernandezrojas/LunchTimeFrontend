package com.herroj.android.lunchtime.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Utilidad {

    public static String getPreferredRestaurant(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_restaurant_key),
                context.getString(R.string.pref_restaurant_default));
    }

}
