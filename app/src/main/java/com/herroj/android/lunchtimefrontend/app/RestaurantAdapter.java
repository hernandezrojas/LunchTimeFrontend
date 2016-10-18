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

        //// Read weather icon ID from cursor
        //int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.mipmap.ic_launcher);

        // Read weather forecast from cursor
        String bk = "Restaurant";
        // Find TextView and set weather forecast on it
        TextView descriptionView = (TextView) view.findViewById(R.id.list_item_bk1_textview);
        descriptionView.setText(bk);

        // Read date from cursor
        String restaurant = cursor.getString(RestaurantFragment.COL_RESTAURANT);
        // Find TextView and set formatted date on it
        TextView restaurantView = (TextView) view.findViewById(R.id.list_item_restaurant_textview);
        restaurantView.setText(restaurant);

        // Read date from cursor
        String horaApertura = cursor.getString(RestaurantFragment.COL_HORA_APERTURA);
        // Find TextView and set formatted date on it
        TextView horaAperturaView = (TextView) view.findViewById(R.id.list_item_hora_apertura_textview);
        horaAperturaView.setText(horaApertura);

        // Read date from cursor
        String horaCierre = cursor.getString(RestaurantFragment.COL_HORA_CIERRE);
        // Find TextView and set formatted date on it
        TextView horaCierreView = (TextView) view.findViewById(R.id.list_item_hora_cierra_textview);
        horaCierreView.setText(horaCierre);

    }
}
