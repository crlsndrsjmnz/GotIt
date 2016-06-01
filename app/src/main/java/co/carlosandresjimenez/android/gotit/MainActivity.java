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

package co.carlosandresjimenez.android.gotit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.carlosandresjimenez.android.gotit.cloud.UserManager;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;
import co.carlosandresjimenez.android.gotit.notification.AlarmReceiver;

public class MainActivity extends BaseActivity implements Observer {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.sign_in_button)
    SignInButton mSignInButton;
    @Bind(R.id.login_progress)
    ProgressBar mProgressBar;

    @Override
    protected void onStart() {
        super.onStart();

        if (Utility.isAutoLogin(this))
            connectToGoogle();
        else
            showSignedInUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isSignedIn())
            searchUsername();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        connectToGoogle();

        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Signing in…", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (GoogleConnection.RC_SIGN_IN == requestCode)
            resolveConnection(resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermissionsResult:" + requestCode);
        if (requestCode == GoogleConnection.RC_PERM_GET_ACCOUNTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSignedInUI();
            } else {
                Log.d(LOG_TAG, "GET_ACCOUNTS Permission Denied.");
            }
        }
    }

    private void showSignedInUI() {
        updateUI(false);
    }

    private void showSigningInUI() {
        updateUI(true);
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            mSignInButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSignInButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionClosed() {
        super.onConnectionClosed();

        showSignedInUI();
    }

    @Override
    public void onConnectionCreated() {
        super.onConnectionCreated();
    }

    @Override
    public void onConnectionOpened() {
        super.onConnectionOpened();

        // Set button visibility
        showSigningInUI();
    }

    @Override
    public void onConnectionOpening() {
        super.onConnectionOpening();

        showSigningInUI();
    }

    @Override
    public void onTokenError() {
        super.onTokenError();
    }

    @Override
    public void onTokenExpired() {
        super.onTokenExpired();
    }

    @Override
    public void onTokenOk() {
        super.onTokenOk();

        if (!Utility.isAutoLogin(this))
            Utility.changeAutoLogin(this, true);

        searchUsername();
    }

    @Override
    public void onNoConnectionState(String stateName) {
        super.onNoConnectionState(stateName);

        showSignedInUI();
    }

    @Override
    public void onUserFound(UserManager userManager) {
        super.onUserFound(userManager);

        if (mUserEmail != null && !mUserEmail.isEmpty())
            Toast.makeText(this, "Signed in as " + mUserEmail, Toast.LENGTH_SHORT).show();

        Utility.setUserPreferenceValues(this, userManager.getUser());

        if (!Utility.isAccountStored(this)) {
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.setAlarm(this);
        }

        openMainScreen();
    }

    @Override
    public void onUserNotFound() {
        super.onUserNotFound();

        if (mUserEmail != null && !mUserEmail.isEmpty())
            Toast.makeText(this, "Signed in as " + mUserEmail, Toast.LENGTH_SHORT).show();

        openRegistrationScreen();
    }

    @Override
    public void onNoInternet() {
        super.onNoInternet();

        showSignedInUI();
    }

    @Override
    public void onAccessUnauthorized() {
        super.onAccessUnauthorized();

        //mGoogleConnection.renewToken();
        showSignedInUI();

    }

    @Override
    public void onNoApplicationState(String stateName) {
        super.onNoApplicationState(stateName);

        showSignedInUI();
        createSnackBar(R.string.error_unknown);

    }


}
