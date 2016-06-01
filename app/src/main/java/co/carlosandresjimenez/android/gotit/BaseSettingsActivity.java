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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.cloud.ApplicationState;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.CloudManager;
import co.carlosandresjimenez.android.gotit.cloud.UserManager;

/**
 * Created by carlosjimenez on 11/1/15.
 */
public class BaseSettingsActivity extends Activity implements Observer {

    private final String LOG_TAG = BaseSettingsActivity.class.getSimpleName();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {

        if (saveUserDetails())
            return;

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
            if (saveUserDetails())
                return true;

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public boolean saveUserDetails() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean accountModified = sp.getBoolean(getString(R.string.pref_account_modified_key), false);

        if (accountModified) {
            showProgressDialog(getString(R.string.progressdialog_saving));

            User user = Utility.getUserPreferenceValues(this);

            CloudManager cloudManager = CloudFactory.getManager(this, user);
            cloudManager.addObserver(this);
            cloudManager.save();

            return true;
        }

        return false;
    }

    public void openMainSettings() {
        Intent homeIntent = new Intent(this, SettingsActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof ApplicationState) {
            checkApplicationState((ApplicationState) data, observable);
        }
    }

    public void checkApplicationState(ApplicationState applicationState, Observable observable) {

        observable.deleteObserver(this);

        switch (applicationState) {
            case USER_SAVED:
                onUserSaved((UserManager) observable);

                break;
            case USER_NOT_SAVED:
                onUserNotSaved();

                break;
            case ACCESS_UNAUTHORIZED:
                onAccessUnauthorized();

                break;
            case NO_INTERNET:
                onNoInternet();

                break;
            default:
                onNoApplicationState(applicationState.name());
        }
    }

    public void onUserSaved(UserManager userManager) {

        progressDialog.dismiss();

        Toast.makeText(this, "User saved", Toast.LENGTH_SHORT).show();

        Utility.changeUserSettingsEditFlag(this, false);

        openMainSettings();
    }

    public void onUserNotSaved() {

        progressDialog.dismiss();

        Toast.makeText(this, "Error: User not saved", Toast.LENGTH_SHORT).show();

        openMainSettings();
    }

    public void onAccessUnauthorized() {
        Log.e(LOG_TAG, ":::::::::: BaseSettingsActivity - Access unauthorized");

        progressDialog.dismiss();

        Toast.makeText(this, "Error: Access unauthorized", Toast.LENGTH_SHORT).show();

        openMainSettings();

    }

    public void onNoInternet() {
        Log.e(LOG_TAG, ":::::::::: BaseSettingsActivity - No internet");

        progressDialog.dismiss();

        Toast.makeText(this, "Error: No internet", Toast.LENGTH_SHORT).show();

        openMainSettings();

    }

    public void onNoApplicationState(String stateName) {
        Log.e(LOG_TAG, ":::::::::: BaseSettingsActivity - No Application State " + stateName);

        progressDialog.dismiss();

        Toast.makeText(this, "Unspecified Error", Toast.LENGTH_SHORT).show();

        openMainSettings();
    }

    public void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(this, null, message, true, false);
    }

}