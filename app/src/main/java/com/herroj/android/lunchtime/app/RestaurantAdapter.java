package com.herroj.android.lunchtime.app;

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
        final TextView m_restaurantView;
        final TextView m_horaAperturaView;
        final TextView m_horaCierreView;

        ViewHolder(final View view) {
            super();
            m_restaurantView = (TextView) view.findViewById(R.id.list_item_restaurant_textview);
            m_horaAperturaView =
                    (TextView) view.findViewById(R.id.list_item_hora_apertura_textview);
            m_horaCierreView = (TextView) view.findViewById(R.id.list_item_hora_cierra_textview);
        }
    }

    RestaurantAdapter(final Context context, final Cursor cursor, final int flags) {
        super(context, cursor, flags);
    }

    @Override
    public final View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final View view =
                LayoutInflater.from(context).inflate(R.layout.list_item_restaurant, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public final void bindView(final View view, final Context context, final Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read date from cursor
        final String restaurant = cursor.getString(RestaurantFragment.COL_RESTAURANT);
        viewHolder.m_restaurantView.setText(restaurant);

        // Read date from cursor
        final String horaApertura = cursor.getString(RestaurantFragment.COL_HORA_APERTURA);
        viewHolder.m_horaAperturaView.setText(horaApertura);

        // Read date from cursor
        final String horaCierre = cursor.getString(RestaurantFragment.COL_HORA_CIERRE);
        // Find TextView and set formatted date on it
        viewHolder.m_horaCierreView.setText(horaCierre);

    }
}
