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
import co.carlosandresjimenez.android.gotit.beans.Following;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;

/**
 * Created by carlosjimenez on 10/28/15.
 */
public class FollowingManager extends Observable implements CloudManager {

    private final static String LOG_TAG = FollowingManager.class.getCanonicalName();
    final Activity mActivity;

    /**
     * Instantiate the MessengerHandler, passing in the CloudManager to be
     * stored as a WeakReference
     */
    MessengerHandler handler = new MessengerHandler(this);
    private Following mFollowing;
    private ArrayList<Following> mFollowingList;

    /**
     * Class initializes the fields.
     *
     * @param context The enclosing Context
     */
    FollowingManager(Activity activity, Following following) {
        this.mActivity = activity;
        this.mFollowing = following;
    }

    /**
     * Class initializes the fields.
     *
     * @param context The enclosing Context
     */
    FollowingManager(Activity activity, ArrayList<Following> followingList) {
        this.mActivity = activity;
        this.mFollowingList = followingList;
    }

    @Override
    public void findAll() {

    }

    @Override
    public void findOne() {

    }

    @Override
    public void save() {

        if (!Utility.isNetworkAvailable(mActivity)) {
            setChanged();
            notifyObservers(ApplicationState.NO_INTERNET);
            return;
        }

        if (mFollowing == null) {
            Log.e(LOG_TAG, "FollowingManager: nothing to save");
            return;
        }

        GoogleConnection googleConnection = GoogleConnection.getInstance(mActivity);

        Messenger messenger = new Messenger(handler);
        Intent intent = new Intent(mActivity, FollowingIntentService.class);
        intent.putExtra(AUTH_TOKEN_KEY, googleConnection.getToken());
        intent.putExtra(REQUEST_CODE_KEY, REQUEST_TYPE_SAVE);
        intent.putExtra(MESSENGER_KEY, messenger);

        if (mFollowing.getFollowingUserEmail() != null)
            intent.putExtra(FOLLOWING_EMAIL_KEY, mFollowing.getFollowingUserEmail());
        else
            intent.putExtra(FOLLOWING_ENTITY_KEY, mFollowing);

        mActivity.startService(intent);

    }

    @Override
    public void delete() {

    }

    public ArrayList<Following> getFollowingList() {
        return mFollowingList;
    }

    public void setFollowingList(ArrayList<Following> followingList) {
        this.mFollowingList = followingList;
    }

    static class MessengerHandler extends Handler {

        // A weak reference to the enclosing class
        private final WeakReference<FollowingManager> outerClass;

        /**
         * A constructor that gets a weak reference to the enclosing class. We
         * do this to avoid memory leaks during Java Garbage Collection.
         *
         * @see https
         * ://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk
         * /lIYDavGYn5UJ
         */
        public MessengerHandler(FollowingManager outer) {
            outerClass = new WeakReference<FollowingManager>(outer);
        }

        // Handle any messages that get sent to this Handler
        @Override
        public void handleMessage(Message msg) {

            if (outerClass.get() == null) {
                Log.e(LOG_TAG, "FollowingManager's Weak reference lost, impossible to notify UI");
                return;
            }

            Bundle bundle = msg.getData();
            bundle.setClassLoader(Following.class.getClassLoader());

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
            int responseStatusCode = bundle.getInt(RESPONSE_CODE_KEY);

            outerClass.get().setChanged();
            switch (responseStatusCode) {
                case RESPONSE_STATUS_OK:
                    outerClass.get().notifyObservers(ApplicationState.FOLLOWING_SAVED);

                    break;
                case RESPONSE_STATUS_CANNOT_FOLLOW:
                    outerClass.get().notifyObservers(ApplicationState.FOLLOWING_CANNOT_FOLLOW);

                    break;
                case RESPONSE_STATUS_NOT_FOUND:
                    outerClass.get().notifyObservers(ApplicationState.FOLLOWING_NOT_FOUND);

                    break;
                case RESPONSE_STATUS_NOT_SAVED:
                    outerClass.get().notifyObservers(ApplicationState.FOLLOWING_NOT_SAVED);

                    break;
                case RESPONSE_STATUS_UNAUTHORIZED:
                    outerClass.get().notifyObservers(ApplicationState.ACCESS_UNAUTHORIZED);

                    break;
                case RESPONSE_STATUS_BADREQUEST:
                    outerClass.get().notifyObservers(ApplicationState.BAD_REQUEST);

                    break;
            }
        }

        public void handleFindOneMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleFindOneMessage: Message obtained");
        }

        public void handleFindAllMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleFindAllMessage: Message obtained");
        }

        public void handleDeleteMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleDeleteMessage: Message obtained");
        }
    }
}