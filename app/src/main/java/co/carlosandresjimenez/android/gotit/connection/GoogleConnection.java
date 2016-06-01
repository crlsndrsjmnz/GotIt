/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Carlos Andres Jimenez <apps@carlosandresjimenez.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package co.carlosandresjimenez.android.gotit.connection;

import android.accounts.Account;
import android.app.Activity;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Observable;

/**
 * Created by carlosjimenez on 10/15/15.
 */
public class GoogleConnection extends Observable
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = GoogleConnection.class.getSimpleName();

    /* RequestCode for resolutions involving sign-in */
    public static final int RC_SIGN_IN = 1;

    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    public static final int RC_PERM_GET_ACCOUNTS = 2;

    public static final int CS_RES_OK = 200; // OK
    public static final int CS_RES_REGISTRATION_REQ = 201; // REGISTRATION REQUIRED
    public static final int CS_RES_VALIDATION_FAILED = 300; // TOKEN VALIDATION FAILED

    private static GoogleConnection sGoogleConnection;

    private WeakReference<Activity> mActivityWeakReference;

    private GoogleApiClient.Builder mGoogleApiClientBuilder;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private ConnectionState mCurrentState;
    private String accessToken;

    private GoogleConnection(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);

        mGoogleApiClientBuilder = new GoogleApiClient.Builder(mActivityWeakReference.get().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL));

        mGoogleApiClient = mGoogleApiClientBuilder.build();
        mCurrentState = ConnectionState.CLOSED;
    }

    public static GoogleConnection getInstance(Activity activity) {
        if (null == sGoogleConnection) {
            sGoogleConnection = new GoogleConnection(activity);
        }

        return sGoogleConnection;
    }

    public void destroyConnection() {
        disconnect();
        deleteObservers();
        sGoogleConnection = null;
    }

    public ConnectionState getCurrentState() {
        return mCurrentState;
    }

    public void connect() {
        mCurrentState.connect(this);
    }

    public void disconnect() {
        mCurrentState.disconnect(this);
    }

    public void revokeAccessAndDisconnect() {
        mCurrentState.revokeAccessAndDisconnect(this);
    }

    public void renewToken() {
        mCurrentState.renewToken(this);
    }

    @Override
    public void onConnected(Bundle hint) {

        changeState(ConnectionState.OPENED);
        fetchToken();
    }

    @Override
    public void onConnectionSuspended(int cause) {

        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        changeState(ConnectionState.CLOSED);
        connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (mCurrentState.equals(ConnectionState.CLOSED) && connectionResult.hasResolution()) {

            changeState(ConnectionState.CREATED);

            this.mConnectionResult = connectionResult;

            onSignUp();
        } else {
            connect();
        }
    }

    public void onActivityResult(int result) {

        if (result == Activity.RESULT_OK) {
            // If the error resolution was successful we should continue
            // processing errors.
            changeState(ConnectionState.CREATED);
        } else {
            // If the error resolution was not successful or the user canceled,
            // we should stop processing errors.
            changeState(ConnectionState.CLOSED);
        }

        // If Google Play services resolved the issue with a dialog then
        // onStart is not called so we need to re-attempt connection here.
        onSignIn();
    }

    public String getAccountName() {
        return Plus.AccountApi.getAccountName(mGoogleApiClient);
    }

    public Person getCurrentPerson() {
        return Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
    }

    protected void onSignIn() {

        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting())
            mGoogleApiClient.connect();
    }

    protected void onSignOut() {

        if (mGoogleApiClient.isConnected()) {
            // We clear the default account on sign out so that Google Play
            // services will not return an onConnected callback without user
            // interaction.
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            changeState(ConnectionState.CLOSED);
        }
    }

    protected void onSignUp() {

        // We have an intent which will allow our user to sign in or
        // resolve an error.  For example if the user needs to
        // select an account to sign in with, or if they need to consent
        // to the permissions your app is requesting.

        try {
            // Send the pending intent that we stored on the most recent
            // OnConnectionFailed callback.  This will allow the user to
            // resolve the error currently preventing our connection to
            // Google Play services.
            changeState(ConnectionState.OPENING);
            mConnectionResult.startResolutionForResult(mActivityWeakReference.get(), RC_SIGN_IN);
        } catch (IntentSender.SendIntentException e) {
            // The intent was canceled before it was sent.  Attempt to connect to
            // get an updated ConnectionResult.
            changeState(ConnectionState.CREATED);
            mGoogleApiClient.connect();
        }
    }

    protected void onRevokeAccessAndDisconnect() {

        // After we revoke permissions for the user with a GoogleApiClient
        // instance, we must discard it and create a new one.
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);

        // Our sample has caches no user data from Google+, however we
        // would normally register a callback on revokeAccessAndDisconnect
        // to delete user data so that we comply with Google developer
        // policies.
        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
        mGoogleApiClient = mGoogleApiClientBuilder.build();
        changeState(ConnectionState.CLOSED);
    }

    protected void onRenewToken() {

        if (mGoogleApiClient.isConnected())
            fetchToken();

    }

    private void changeState(ConnectionState state) {
        mCurrentState = state;
        setChanged();
        notifyObservers(state);
    }

    public void fetchToken() {
        new FetchTokenTask().execute();
    }

    public String getToken() {
        return accessToken;
    }

    public void setTokenExpired() {
        changeState(ConnectionState.TOKEN_EXPIRED);
    }

    public boolean isSignedIn() {
        return mCurrentState == ConnectionState.OPENED ||
                mCurrentState == ConnectionState.TOKEN_OK ||
                mCurrentState == ConnectionState.TOKEN_ERROR ||
                mCurrentState == ConnectionState.TOKEN_EXPIRED ? true : false;
    }

    public boolean isConnecting() {
        return mCurrentState == ConnectionState.OPENING ? true : false;
    }

    public class FetchTokenTask extends AsyncTask<Void, Void, String> {

        private final String LOG_TAG = FetchTokenTask.class.getSimpleName();

        // TODO: Add the Google Client Id for mobile
        String SERVER_CLIENT_ID = "";

        @Override
        protected String doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
            try {
                return GoogleAuthUtil.getToken(mActivityWeakReference.get().getApplicationContext(), account, scopes);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                accessToken = response;
                changeState(ConnectionState.TOKEN_OK);
            } else {
                changeState(ConnectionState.TOKEN_ERROR);
            }
        }
    }
}