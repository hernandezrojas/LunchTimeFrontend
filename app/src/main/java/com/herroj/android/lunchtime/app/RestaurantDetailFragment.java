package com.herroj.android.lunchtime.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herroj.android.lunchtime.app.data.LunchTimeContract;

/**
 * Fragment que muestra el restaurant de manera detallada, implementa una interfaz de cursor
 */
public class RestaurantDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * uri del restaurant detail que se usa al momento de invocar el fragment de detalle
     */
    static final String RESTAURANT_DETAIL_URI = "URI";

    /**
     * uri para identificar la informacion a compartir
     */
    private Uri m_uri;

    /**
     * id que se usa en el loader para el detalle del restaurant
     */
    private static final int DETAIL_LOADER = 0;

    /**
     * campos de la tabla restaurant que se usaran en la clase
     */
    private static final String[] DETAIL_COLUMNS = {
            LunchTimeContract.RestaurantEntry.TABLE_NAME + '.' +
                    LunchTimeContract.RestaurantEntry._ID,
            LunchTimeContract.RestaurantEntry.COLUMN_RESTAURANT,
            LunchTimeContract.RestaurantEntry.COLUMN_HORA_APERTURA,
            LunchTimeContract.RestaurantEntry.COLUMN_HORA_CIERRE
    };

    /**
     * posicion del campo restaurant
     */
    private static final int IDX_COL_RESTAURANT = 1;

    /**
     * posicion del campo hora de apertura
     */
    private static final int IDX_COL_HORA_APERTURA = 2;

    /**
     * posicion del campo hora de cierre
     */
    private static final int IDX_COL_HORA_CIERRE = 3;

    /**
     * TextView del campo restaurante
     */
    private TextView m_restaurantView;

    /**
     * TextView del campo hora apertura
     */
    private TextView m_horaAperturaView;

    /**
     * TextView del campo hora cierre
     */
    private TextView m_horaCierreView;

    /**
     * se establece que el fragment de detalle tendra menu
     */
    public RestaurantDetailFragment() {
        super();
        setHasOptionsMenu(true);
    }

    /**
     * implementacion que se usa cuando se infla con LayoutInflater
     *
     * @param inflater nombre de etiqueta a ser inflado
     * @param container el contexto donde la view se crea
     * @param savedInstanceState atributos de infar especificados en un archivo XML
     * @return la view creada
     */
    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {

        final Bundle arguments = getArguments();

        if (arguments != null) {
            m_uri = arguments.getParcelable(RESTAURANT_DETAIL_URI);
        }

        final View rootView =
                inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
        m_restaurantView =
                (TextView) rootView.findViewById(R.id.restaurant_detail_restaurant_textview);
        m_horaAperturaView =
                (TextView) rootView.findViewById(R.id.restaurant_detail_hora_apertura_textview);
        m_horaCierreView =
                (TextView) rootView.findViewById(R.id.restaurant_detail_hora_cierre_textview);
        return rootView;

    }

    /**
     * inicializa el menu de opciones de la activity
     *
     * @param menu el menu de opciones en el cual se ingresaran los elementos
     * @param inflater MenuInflater
     */
    @Override
    public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {

    }

    /**
     * Llamado cuando el fragment del activity ha sido creado y este fragment de view
     * instanciado
     * @param savedInstanceState Si el fragmento se vuelve a crear a partir de un estado guardado
     *                           anterior, este es el estado.
     */
    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * metodo que se llama al cambiar de restaurant en la pantalla de configuracion
     *
     * @param newRestaurant restaurant que se ingreso
     */
    final void onRestaurantChanged(final String newRestaurant) {
        if (m_uri != null) {
            m_uri = LunchTimeContract.RestaurantEntry.buildRestaurantporNombreUri(newRestaurant);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    /**
     * Instancia y retorna un nuevo Loader para el id dado
     *
     * @param id id cuyo Loader se va a crear
     * @param args argumentos proporcionados por el caller
     * @return regresa una nueva instancia de Loader que esta listo para empezar a cargar
     */
    @Override
    public final Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        if (m_uri != null) {

            /*
            crea y regresa un CursorLoader  que se encargara de crear un cursor para el que se
            muestran los datos.
             */
            return new CursorLoader(
                    getActivity(),
                    m_uri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    /**
     * es llamado cuando un loader creado previamente ha terminado de cargarse
     *
     * @param loader el loader que ha terminado de cargarse
     * @param data el data que es generado por el Loader
     */
    @Override
    public final void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if ((data != null) && data.moveToFirst()) {

            // Lee datos del cursor y actualiza las vistas
            final String restaurant = data.getString(IDX_COL_RESTAURANT);
            m_restaurantView.setText(restaurant);

            final String horaApertura = data.getString(IDX_COL_HORA_APERTURA);
            m_horaAperturaView.setText(horaApertura);

            final String horaCierre = data.getString(IDX_COL_HORA_CIERRE);
            m_horaCierreView.setText(horaCierre);


        }
    }

    /**
     * Se llama cuando un cargador creado anteriormente se restablece
     *
     * @param loader el loader que se esta restableciendo
     */
    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
    }

}
