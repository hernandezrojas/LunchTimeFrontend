package com.herroj.android.lunchtime.app.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.content.Context;
import android.os.Bundle;

/**
 * Manages "Authentication" to Sunshine's backend service.  The SyncAdapter framework
 * requires an authenticator object, so syncing to a service that doesn't need authentication
 * typically means creating a stub authenticator like this one.
 * This code is copied directly, in its entirety, from
 * http://developer.android.com/training/sync-adapters/creating-authenticator.html
 * Which is a pretty handy reference when creating your own syncadapters.  Just sayin'.
 */
class LunchTimeAuthenticator extends AbstractAccountAuthenticator {

    /**
     * Instantiates a new Lunch time authenticator.
     *
     * @param context the context
     */
    LunchTimeAuthenticator(final Context context) {
        super(context);
    }

    // No properties to edit.
    @Override
    public final Bundle editProperties(
            final AccountAuthenticatorResponse accountAuthenticatorResponse, final String s) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    // Because we're not actually adding an account to the device, just return null.
    @Override
    public final Bundle addAccount(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final String s,
            final String s1,
            final String[] strings,
            final Bundle bundle) {
        return null;
    }

    // Ignore attempts to confirm credentials
    @Override
    public final Bundle confirmCredentials(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account,
            final Bundle bundle) {
        return null;
    }

    // Getting an authentication token is not supported
    @Override
    public final Bundle getAuthToken(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account,
            final String s,
            final Bundle bundle) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    // Getting a label for the auth token is not supported
    @Override
    public final String getAuthTokenLabel(final String s) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    // Updating user credentials is not supported
    @Override
    public final Bundle updateCredentials(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account,
            final String s, final Bundle bundle) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    // Checking features for the account is not supported
    @Override
    public final Bundle hasFeatures(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account, final String[] strings) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }
}
