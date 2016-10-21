package com.herroj.android.lunchtimefrontend.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RestaurantAdapter} exposes a list of restaurant forecasts
 * + * from a {@link Cursor} to a {@link android.widget.ListView}.
 */

class RestaurantAdapter extends CursorAdapter {

    private static class ViewHolder {
        final TextView bkView;
        final TextView restaurantView;
        final TextView horaAperturaView;
        final TextView horaCierreView;

        ViewHolder(View view) {
            bkView = (TextView) view.findViewById(R.id.list_item_bk_textview);
            restaurantView = (TextView) view.findViewById(R.id.list_item_restaurant_textview);
            horaAperturaView = (TextView) view.findViewById(R.id.list_item_hora_apertura_textview);
            horaCierreView = (TextView) view.findViewById(R.id.list_item_hora_cierra_textview);
        }
    }

    RestaurantAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_restaurant, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather forecast from cursor
        String bk = "Restaurant";
        viewHolder.bkView.setText(bk);

        // Read date from cursor
        String restaurant = cursor.getString(RestaurantFragment.COL_RESTAURANT);
        viewHolder.restaurantView.setText(restaurant);

        // Read date from cursor
        String horaApertura = cursor.getString(RestaurantFragment.COL_HORA_APERTURA);
        viewHolder.horaAperturaView.setText(horaApertura);

        // Read date from cursor
        String horaCierre = cursor.getString(RestaurantFragment.COL_HORA_CIERRE);
        // Find TextView and set formatted date on it
        viewHolder.horaCierreView.setText(horaCierre);

    }
}
