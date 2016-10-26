package com.herroj.android.lunchtime.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * El servicio que permite al framework sync adapter accesar al authenticator
 */
public class LunchTimeAuthenticatorService extends Service {

    /**
     * Instancia que almacena el objeto authentificador
     */
    private LunchTimeAuthenticator m_authenticator;

    /**
     * Llamado por el sistema cuando el servicio es creado por primera vez
     */
    @Override
    public final void onCreate() {

        m_authenticator = new LunchTimeAuthenticator(this);

    }

    /**
     * Regresa el canal de comunicacion al servicio
     *
     * @param intent el intent que fue usado para unirse al servicio
     * @return regresa un IBinder a traves del cual los clientes pueden llamar para el servicio.
     */
    @Override
    public final IBinder onBind(final Intent intent) {

        return m_authenticator.getIBinder();

    }

}
