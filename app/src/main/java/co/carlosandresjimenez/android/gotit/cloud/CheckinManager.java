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

package co.carlosandresjimenez.android.gotit.cloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Observable;

import co.carlosandresjimenez.android.gotit.Utility;
import co.carlosandresjimenez.android.gotit.beans.Checkin;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;

/**
 * Created by carlosjimenez on 10/25/15.
 */
public class CheckinManager extends Observable implements CloudManager {

    private final static String LOG_TAG = CheckinManager.class.getCanonicalName();

    final Activity mActivity;

    /**
     * Instantiate the MessengerHandler, passing in the CloudManager to be
     * stored as a WeakReference
     */
    MessengerHandler handler = new MessengerHandler(this);
    private Checkin mCheckin;
    private ArrayList<Checkin> mCheckins;

    /**
     * Class initializes the fields.
     *
     * @param activity
     * @param checkin
     */
    CheckinManager(Activity activity, Checkin checkin) {
        this.mActivity = activity;
        this.mCheckin = checkin;
    }

    /**
     * Class initializes the fields.
     *
     * @param activity
     * @param checkins
     */
    CheckinManager(Activity activity, ArrayList<Checkin> checkins) {
        this.mActivity = activity;
        this.mCheckins = checkins;
    }

    @Override
    public void findAll() {
        if (!Utility.isNetworkAvailable(mActivity)) {
            setChanged();
            notifyObservers(ApplicationState.NO_INTERNET);
            return;
        }

        GoogleConnection googleConnection = GoogleConnection.getInstance(mActivity);

        Messenger messenger = new Messenger(handler);
        Intent intent = new Intent(mActivity, CheckinIntentService.class);
        intent.putExtra(AUTH_TOKEN_KEY, googleConnection.getToken());
        intent.putExtra(REQUEST_CODE_KEY, REQUEST_TYPE_FIND_ALL);
        intent.putExtra(MESSENGER_KEY, messenger);
        intent.putExtra(CHECKIN_FEED_KEY, mCheckin.isPersonal());
        mActivity.startService(intent);
    }

    @Override
    public void findOne() {

    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {

    }

    public ArrayList<Checkin> getCheckins() {
        return mCheckins;
    }

    public void setCheckins(ArrayList<Checkin> checkins) {
        this.mCheckins = checkins;
    }

    static class MessengerHandler extends Handler {

        // A weak reference to the enclosing class
        private final WeakReference<CheckinManager> outerClass;

        /**
         * A constructor that gets a weak reference to the enclosing class. We
         * do this to avoid memory leaks during Java Garbage Collection.
         *
         * @see https
         * ://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk
         * /lIYDavGYn5UJ
         */
        public MessengerHandler(CheckinManager outer) {
            outerClass = new WeakReference<CheckinManager>(outer);
        }

        // Handle any messages that get sent to this Handler
        @Override
        public void handleMessage(Message msg) {

            if (outerClass.get() == null) {
                Log.e(LOG_TAG, "CheckinManager's Weak reference lost, impossible to notify UI");
                return;
            }

            Bundle bundle = msg.getData();
            bundle.setClassLoader(Checkin.class.getClassLoader());

            int requestCode = bundle.getInt(REQUEST_CODE_KEY);

            switch (requestCode) {
                case REQUEST_TYPE_SAVE:
                    handleSaveMessage(bundle);
                    break;
                case REQUEST_TYPE_FIND_ONE:
                    handleFindOneMessage(bundle);
                    break;
                case REQUEST_TYPE_FIND_ALL:
                    handleFindAllMessage(bundle);
                    break;
                case REQUEST_TYPE_DELETE:
                    handleDeleteMessage(bundle);
                    break;
            }

        }

        public void handleSaveMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleSaveMessage:Message obtained");
        }

        public void handleFindOneMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleFindOneMessage:Message obtained");
        }

        public void handleFindAllMessage(Bundle bundle) {
            ArrayList<Checkin> checkins = bundle.getParcelableArrayList(CHECKIN_ENTITIES_KEY);

            int responseStatusCode = bundle.getInt(RESPONSE_CODE_KEY);

            outerClass.get().setChanged();
            switch (responseStatusCode) {
                case RESPONSE_STATUS_OK:
                    outerClass.get().setCheckins(checkins);
                    outerClass.get().notifyObservers(ApplicationState.CHECKINS_FOUND);

                    break;
                case RESPONSE_STATUS_NO_CONTENT:
                    outerClass.get().notifyObservers(ApplicationState.CHECKINS_NOT_FOUND);

                    break;
                case RESPONSE_STATUS_UNAUTHORIZED:
                    outerClass.get().notifyObservers(ApplicationState.ACCESS_UNAUTHORIZED);

                    break;
            }
        }

        public void handleDeleteMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleDeleteMessage:Message obtained");
        }
    }
}