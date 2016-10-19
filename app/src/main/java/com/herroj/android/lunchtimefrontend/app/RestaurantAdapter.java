package com.herroj.android.lunchtimefrontend.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.herroj.android.lunchtimefrontend.app.data.RestaurantContract;

/**
 * {@link RestaurantAdapter} exposes a list of restaurant forecasts
 * + * from a {@link Cursor} to a {@link android.widget.ListView}.
 */

public class RestaurantAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView bkView;
        public final TextView restaurantView;
        public final TextView horaAperturaView;
        public final TextView horaCierreView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            bkView = (TextView) view.findViewById(R.id.list_item_bk_textview);
            restaurantView = (TextView) view.findViewById(R.id.list_item_restaurant_textview);
            horaAperturaView = (TextView) view.findViewById(R.id.list_item_hora_apertura_textview);
            horaCierreView = (TextView) view.findViewById(R.id.list_item_hora_cierra_textview);
        }
    }

    public RestaurantAdapter(Context context, Cursor c, int flags) {
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

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        // Read weather forecast from cursor
        String bk = "Restaurant";
        viewHolder.bkView.setText(bk);

        // Read date from cursor
        String restaurant = cursor.getString(RestaurantFragment.COL_RESTAURANT);
        viewHolder.restaurantView.setText(restaurant);

        // For accessibility, add a content description to the icon field
        viewHolder.iconView.setContentDescription(restaurant);

        // Read date from cursor
        String horaApertura = cursor.getString(RestaurantFragment.COL_HORA_APERTURA);
        viewHolder.horaAperturaView.setText(horaApertura);

        // Read date from cursor
        String horaCierre = cursor.getString(RestaurantFragment.COL_HORA_CIERRE);
        // Find TextView and set formatted date on it
        viewHolder.horaCierreView.setText(horaCierre);

    }
}
