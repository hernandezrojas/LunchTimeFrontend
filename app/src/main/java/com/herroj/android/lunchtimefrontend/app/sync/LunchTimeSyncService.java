package com.herroj.android.lunchtimefrontend.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Roberto Hernandez on 18/10/2016.
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
