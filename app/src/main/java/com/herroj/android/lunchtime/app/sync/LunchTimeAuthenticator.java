package com.herroj.android.lunchtime.app.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.content.Context;
import android.os.Bundle;

/**
 * Maneja la "Authentication" al servicio backend de Lunch Time. El framework SyncAdapter
 * requere un objeto autentificador, por lo que la sincronizacion con un servicio que no necesita
 * el autenticacion normalmente significa la creacion de un autentificador como este.
 * Este código es copiado, en su totalidad a partir de
 * http://developer.android.com/training/sync-adapters/creating-authenticator.html
 */
class LunchTimeAuthenticator extends AbstractAccountAuthenticator {

    /**
     * Se crea una instancia de autentificador de Lunch Time
     *
     * @param context el contexto donde se creara
     */
    LunchTimeAuthenticator(final Context context) {

        super(context);

    }

    /**
     * Devuelve un bundle que contiene el intent del activity que se puede utilizar para
     * editar las propiedades
     *
     * @param accountAuthenticatorResponse usada para establecer el resultado de la consulta
     * @param s es el accountType cuyas propiedades serán editadas
     * @return un bundle que contiene el resultado o el intent de empezar a seguir la consulta
     */
    @Override
    public final Bundle editProperties(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final String s) {

        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

    /**
     * addAccount agrega una cuenta a un especifico AccountType
     *
     * @param accountAuthenticatorResponse para enviar el resultado al AccountManager
     * @param s es el tipo de cuenta a agregar
     * @param s1 tipo de autentificacion token a recuperar despues de agregar
     *                      la cuenta
     * @param strings un arreglo de String de funciones de authenticator-specific
     *                         que la cuenta debe tener
     * @param bundle un bundle de opciones authenticator-specific
     * @return un Bundle con los resultados o nulo
     */
    @Override
    public final Bundle addAccount(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final String s,
            final String s1,
            final String[] strings,
            final Bundle bundle) {

        return null;

    }

    /**
     * confirmCredentials Comprueba que el usuario conoce las credenciales de una cuenta
     *
     * @param accountAuthenticatorResponse para enviar el resultado al AccountManager
     * @param account la cuenta de la cual se verificaran las credenciales
     * @param bundle un bundle de opciones authenticator-specific
     * @return un Bundle con los resultados o nulo
     */
    @Override
    public final Bundle confirmCredentials(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account,
            final Bundle bundle) {

        return null;

    }

    /**
     * getAuthToken obtiene el AuthToken de una cuenta
     *
     * @param accountAuthenticatorResponse para enviar el resultado al AccountManager
     * @param account la cuenta de la cual se verificaran las credenciales
     * @param s tipo de autentificacion token a recuperar despues de agregar
     *                      la cuenta
     * @param bundle un bundle de opciones authenticator-specific
     * @return un Bundle con los resultados o nulo
     */
    @Override
    public final Bundle getAuthToken(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account,
            final String s,
            final Bundle bundle) {

        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

    /**
     * getAuthTokenLabel busca un label del authTokenType
     *
     * @param s label buscada
     * @return el label
     */
    @Override
    public final String getAuthTokenLabel(final String s) {

        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

    /**
     * updateCredentials actualiza las credenciales localmente almacenadas para una cuenta
     *
     * @param accountAuthenticatorResponse para enviar el resultado al AccountManager
     * @param account la cuenta a la cual se le actualizaran las credenciales
     * @param s tipo de autentificacion token a recuperar despues de agregar la cuenta
     * @param bundle un bundle de opciones authenticator-specific
     * @return un Bundle con los resultados o nulo
     */
    @Override
    public final Bundle updateCredentials(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account,
            final String s, final Bundle bundle) {

        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

    /**
     * hasFeatures verifica si la cuenta es compatible con todas las caracteristicas
     * del autentificador especificado
     *
     * @param accountAuthenticatorResponse para enviar el resultado al AccountManager
     * @param account cuenta a verificar
     * @param strings un array con caracteristicas a verificar
     * @return un Bundle con los resultados o nulo
     */
    @Override
    public final Bundle hasFeatures(
            final AccountAuthenticatorResponse accountAuthenticatorResponse,
            final Account account, final String[] strings) {

        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

}
