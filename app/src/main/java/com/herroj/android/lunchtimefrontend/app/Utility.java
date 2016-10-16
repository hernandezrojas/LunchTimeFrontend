package com.herroj.android.lunchtimefrontend.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Roberto Hernandez on 16/10/2016.
 */

public class Utility {


    private static SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
    private static SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");

    public static String getPreferredRestaurant(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_restaurant_key),
                context.getString(R.string.pref_restaurant_default));
    }

    public static String getStrCampo(JSONObject objeto, String campo) {

        try {
            if (objeto.has(campo)) {
                return objeto.getString(campo);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return "";

    }

    public static String getStrCampo(ContentValues values, String campo) {

        String salida = values.getAsString(campo);

        if (salida == null){
            return "";

        } else {
            return salida;

        }

    }

    public static String darformatoCadenaHora(String hora) {

        if (hora.compareTo("") == 0) {
            return hora;
        }

        hora = hora.substring(hora.indexOf('T') + 1, hora.length());
        hora = hora.substring(0, hora.indexOf('-') - 3);
        try {
            Date _24HourDt = _24HourSDF.parse(hora);
            hora = _12HourSDF.format(_24HourDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hora;
    }

}
