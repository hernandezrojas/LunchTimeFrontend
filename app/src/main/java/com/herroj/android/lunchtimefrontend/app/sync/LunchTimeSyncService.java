package com.herroj.android.lunchtimefrontend.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * LunchTimeSyncService es el servicio de sincronización de datos de la aplicación
 * Nota: un servicio es una tarea que puede continuar cuando la actividad no es visible,
 * como el oir musica
 */

public class LunchTimeSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static LunchTimeSyncAdapter sLunchTimeSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("LunchTimeSyncService", "onCreate - LunchTimeSyncService");
        synchronized (sSyncAdapterLock) {
            if (sLunchTimeSyncAdapter == null) {
                sLunchTimeSyncAdapter = new LunchTimeSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sLunchTimeSyncAdapter.getSyncAdapterBinder();
    }
}
