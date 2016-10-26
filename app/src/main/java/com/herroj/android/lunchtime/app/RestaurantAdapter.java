package com.herroj.android.lunchtime.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link RestaurantAdapter} expone una lista de restaurantes
 * desde un {@link Cursor} a un {@link android.widget.ListView}.
 */
class RestaurantAdapter extends CursorAdapter {

    /**
     * constructor que permite el control de auto-requery
     *
     * @param context el contexto a utilizar
     * @param cursor  el cursor del cual se obtendran los datos
     * @param flags si es verdadero el adaptador podra llamar a reuery() en el cursor cada vez
     *              que cambie, asi siempre obtendra los datos mas recientes. No se recomienda el
     *              uso de verdadero
     */
    RestaurantAdapter(final Context context, final Cursor cursor, final int flags) {
        super(context, cursor, flags);
    }

    /**
     * newView hace un nuevo view que mantiene los datos a los cuales apunta el cursor
     *
     * @param context interfaz a la informacion de la aplicacion
     * @param cursor cursor con el que se obienen los datos
     * @param parent el parent al cual la vista se une
     * @return la vista creada
     */
    @Override
    public final View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final View view =
                LayoutInflater.from(context).inflate(R.layout.list_item_restaurant, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /**
     * bindView enlaza una vista existente a los datos apuntados por el cursor
     *Existing view, returned earlier by newView
     * @param view view existente, que regresa anteriormente por newView
     * @param context interfaz a la informacion de la aplicacion
     * @param cursor cursor con el que se obienen los datos
     */
    @Override
    public final void bindView(final View view, final Context context, final Cursor cursor) {

        // se crea una instancia de ViewHolder que maneja las vistas
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Lectura de datos del cursor
        final String restaurant = cursor.getString(RestaurantFragment.IDX_COL_RESTAURANT);
        viewHolder.m_restaurantView.setText(restaurant);

        final String horaApertura = cursor.getString(RestaurantFragment.IDX_COL_HORA_APERTURA);
        viewHolder.m_horaAperturaView.setText(horaApertura);

        final String horaCierre = cursor.getString(RestaurantFragment.IDX_COL_HORA_CIERRE);

        viewHolder.m_horaCierreView.setText(horaCierre);

    }

    /**
     * Clase interna que contiene los views que se manejan en la pantalla de restaurant
     */
    private static class ViewHolder {

        /**
         * TextView que maneja el nombre del restaurant
         */
        final TextView m_restaurantView;

        /**
         * TextView que maneja la hora de apertura
         */
        final TextView m_horaAperturaView;

        /**
         * TextView que maneja la hora de cierre
         */
        final TextView m_horaCierreView;

        /**
         * Constructor que inicializa las vistas
         *
         * @param view se usa para obtener la informacion de las views
         */
        ViewHolder(final View view) {

            super();

            m_restaurantView = (TextView) view.findViewById(R.id.list_item_restaurant_textview);
            m_horaAperturaView =
                    (TextView) view.findViewById(R.id.list_item_hora_apertura_textview);
            m_horaCierreView = (TextView) view.findViewById(R.id.list_item_hora_cierra_textview);

        }

    }

}
