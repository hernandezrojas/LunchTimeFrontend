package com.herroj.android.lunchtime.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.herroj.android.lunchtime.app.data.LunchTimeContract;
import com.herroj.android.lunchtime.app.data.LunchTimeProvider;
import com.herroj.android.lunchtime.app.sync.LunchTimeSyncAdapter;

/**
 * Fragment que muestra una lista de restaurantes, implementa una interfaz de cursor
 */
public class RestaurantFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Instancia que expone una lista de restaurantes desde un cursor a un list view
     */
    private RestaurantAdapter m_restaurantAdapter;

    /**
     * listView que mostrara los restaurantes
     */
    private ListView m_listView;

    /**
     * variable que muestra la posicion del cursor en la lista
     */
    private int m_position = ListView.INVALID_POSITION;

    /**
     * Llave de posicion seleccionada
     */
    private static final String SELECTED_KEY = "selected_position";

    /**
     * identificador para el fragment de lista de restaurantes en el loader
     */
    private static final int RESTAURANT_LOADER = 0;

    /**
     * Campos que se muestran en la tabla de restaurant
     */
    private static final String[] RESTAURANT_COLUMNS = {
            LunchTimeContract.RestaurantEntry.TABLE_NAME + '.' +
                    LunchTimeContract.RestaurantEntry._ID,
            LunchTimeContract.RestaurantEntry.COLUMN_RESTAURANT,
            LunchTimeContract.RestaurantEntry.COLUMN_HORA_APERTURA,
            LunchTimeContract.RestaurantEntry.COLUMN_HORA_CIERRE
    };

    /**
     * indice del campo restaurante
     */
    static final int IDX_COL_RESTAURANT = 1;

    /**
     * indice del campo hora de apertura
     */
    static final int IDX_COL_HORA_APERTURA = 2;

    /**
     * indice del campo hora de cierre
     */
    static final int IDX_COL_HORA_CIERRE = 3;

    /**
     * Una interfaz Callback que todas las actividades que implementan fragment deben implementar.
     * Este mecanismo permite a las activities ser notificado del elemento seleccionado
     */
    public interface Callback {
        /**
         * implementacion del evento de elemento seleccionado
         */
        void onItemSelected(Uri restaurantUri);
    }

    /**
     * es llamado al inicializar la creacion de un fragment
     *
     * @param savedInstanceState Si el fragment se vuelve a crear desde un estado guardado, este
     *                           es el estado
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        /*
        el RestaurantAdapter tomara datos desde una fuente y los usa para poblar
        la ListView enlazada
         */
        m_restaurantAdapter = new RestaurantAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_restaurant_main, container, false);

        // Obtiene una referencia a la ListView, y la enlaza al adaptador
        m_listView = (ListView) rootView.findViewById(R.id.listview_restaurant);
        m_listView.setAdapter(m_restaurantAdapter);

        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> adapterView,
                                    final View view, final int i, final long l) {

                /*
                CursorAdapter regresa un cursor a la correcta posicion del getItem(), o nulo
                si no encuentra la posicion
                 */
                final Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(LunchTimeContract.RestaurantEntry.
                                    buildRestaurantporNombreUri(
                                            cursor.getString(IDX_COL_RESTAURANT)));
                }
                m_position = i;
            }
        });

        if ((savedInstanceState != null) && savedInstanceState.containsKey(SELECTED_KEY)) {
            m_position = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;

    }

    /**
     * Llamado cuando el fragment del activity ha sido creado y este fragment de view
     * instanciado
     * @param savedInstanceState Si el fragmento se vuelve a crear a partir de un estado guardado
     *                           anterior, este es el estado.
     */
    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        getLoaderManager().initLoader(RESTAURANT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * al cambiar el nombre del restaurant en configuracion, actualizamos los datos
     */
    final void onRestaurantChanged() {
        updateRestaurant();
        getLoaderManager().restartLoader(RESTAURANT_LOADER, null, this);
    }

    /**
     * se manda a sincronizar los datos, al llamar a actualizar Restaurant
     */
    private void updateRestaurant() {
        LunchTimeSyncAdapter.syncImmediately(getActivity());
    }

    /**
     * es llamado para preguntar al fragment para salvar su estado dinamico actual, por lo que
     * mas tarde puede ser reconstruido en una nueva instancia si el proceso se reinicia
     *
     * @param outState Bundle donde se guarda el estado
     */
    @Override
    public final void onSaveInstanceState(final Bundle outState) {

        if (m_position != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, m_position);
        }
        super.onSaveInstanceState(outState);
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

        final String sortOrder = LunchTimeContract.RestaurantEntry.COLUMN_RESTAURANT + " ASC";

        String restaurantSetting = Utilidad.getPreferredRestaurant(getActivity());

        final Uri restaurantUri;

        if (restaurantSetting.compareTo("") == 0) {
            restaurantUri = LunchTimeContract.RestaurantEntry.CONTENT_URI;
        } else {
            restaurantUri = LunchTimeContract.RestaurantEntry
                    .buildRestaurantporNombreUri(restaurantSetting);
        }

        return new CursorLoader(getActivity(),
                restaurantUri,
                RESTAURANT_COLUMNS,
                LunchTimeContract.RestaurantEntry.COLUMN_TIPO_RESTAURANT + " = ?",
                new String[]{LunchTimeContract.s_filtroSeleccionado},
                sortOrder);

    }

    /**
     * es llamado cuando un loader creado previamente ha terminado de cargarse
     *
     * @param loader el loader que ha terminado de cargarse
     * @param data el data que es generado por el Loader
     */
    @Override
    public final void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        m_restaurantAdapter.swapCursor(data);
        if (m_position != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            m_listView.smoothScrollToPosition(m_position);
        }
    }

    /**
     * Se llama cuando un cargador creado anteriormente se restablece
     *
     * @param loader el loader que se esta restableciendo
     */
    @Override
    public final void onLoaderReset(final Loader<Cursor> loader) {
        m_restaurantAdapter.swapCursor(null);
    }

}