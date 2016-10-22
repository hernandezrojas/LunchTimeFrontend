package com.herroj.android.lunchtime.app.sync;

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
    private static final Object S_SYNC_ADAPTER_LOCK = new Object();
    private static LunchTimeSyncAdapter s_lunchTimeSyncAdapter;

    @Override
    public final void onCreate() {
        Log.d("LunchTimeSyncService", "onCreate - LunchTimeSyncService");
        synchronized (S_SYNC_ADAPTER_LOCK) {
            if (s_lunchTimeSyncAdapter == null) {
                s_lunchTimeSyncAdapter = new LunchTimeSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public final IBinder onBind(final Intent intent) {
        return s_lunchTimeSyncAdapter.getSyncAdapterBinder();
    }
}
