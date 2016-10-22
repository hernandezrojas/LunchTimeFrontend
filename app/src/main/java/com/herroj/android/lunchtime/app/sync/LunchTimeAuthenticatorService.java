package com.herroj.android.lunchtime.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class LunchTimeAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private LunchTimeAuthenticator m_authenticator;

    @Override
    public final void onCreate() {
        // Create a new authenticator object
        m_authenticator = new LunchTimeAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public final IBinder onBind(final Intent intent) {
        return m_authenticator.getIBinder();
    }
}
