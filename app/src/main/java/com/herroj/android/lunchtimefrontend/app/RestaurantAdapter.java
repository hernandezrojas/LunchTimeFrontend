package com.herroj.android.lunchtimefrontend.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;

/**
 * Created by Roberto Hernandez on 16/10/2016.
 */

public class RestaurantAdapter extends CursorAdapter {
    public RestaurantAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_restaurant = cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT);
        int idx_hora_apertura = cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_HORA_APERTURA);
        int idx_hora_cierre = cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_HORA_CIERRE);
        int idx_tipo_restaurant_id = cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT_ID);

        return cursor.getString(idx_restaurant) + " - "  + cursor.getString(idx_hora_apertura) + " - " + cursor.getString(idx_hora_cierre);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_restaurant, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView) view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
