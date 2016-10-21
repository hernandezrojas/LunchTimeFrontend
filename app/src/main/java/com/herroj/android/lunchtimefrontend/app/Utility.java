package com.herroj.android.lunchtimefrontend.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {

    public static String getPreferredRestaurant(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_restaurant_key),
                context.getString(R.string.pref_restaurant_default));
    }

}
