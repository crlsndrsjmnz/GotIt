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
import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;

/**
 * Created by carlosjimenez on 10/15/15.
 */
public class UserManager extends Observable implements CloudManager {

    private final static String LOG_TAG = UserManager.class.getCanonicalName();
    final Activity mActivity;
    /**
     * Instantiate the MessengerHandler, passing in the CloudManager to be
     * stored as a WeakReference
     */
    MessengerHandler handler = new MessengerHandler(this);
    private User mUser;
    private ArrayList<User> mUsers;

    /**
     * Class initializes the fields.
     *
     * @param context The enclosing Context
     */
    UserManager(Activity activity, User user) {
        mActivity = activity;
        mUser = user;
    }

    /**
     * Class initializes the fields.
     *
     * @param context The enclosing Context
     */
    UserManager(Activity activity, ArrayList<User> users) {
        mActivity = activity;
        mUsers = users;
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
        Intent intent = new Intent(mActivity, UserIntentService.class);
        intent.putExtra(AUTH_TOKEN_KEY, googleConnection.getToken());
        intent.putExtra(REQUEST_CODE_KEY, REQUEST_TYPE_FIND_ALL);
        intent.putExtra(MESSENGER_KEY, messenger);
        intent.putExtra(USER_APPROVED_STATUS_KEY, mUser.getApprovedStatus());
        mActivity.startService(intent);
    }

    @Override
    public void findOne() {

        if (!Utility.isNetworkAvailable(mActivity)) {
            setChanged();
            notifyObservers(ApplicationState.NO_INTERNET);
            return;
        }

        if (mUser == null) {
            return;
        }

        GoogleConnection googleConnection = GoogleConnection.getInstance(mActivity);

        Messenger messenger = new Messenger(handler);
        Intent intent = new Intent(mActivity, UserIntentService.class);
        intent.putExtra(AUTH_TOKEN_KEY, googleConnection.getToken());
        intent.putExtra(REQUEST_CODE_KEY, REQUEST_TYPE_FIND_ONE);
        intent.putExtra(MESSENGER_KEY, messenger);
        intent.putExtra(USER_EMAIL_KEY, mUser.getEmail());
        mActivity.startService(intent);
    }

    @Override
    public void save() {

        if (!Utility.isNetworkAvailable(mActivity)) {
            setChanged();
            notifyObservers(ApplicationState.NO_INTERNET);
            return;
        }

        GoogleConnection googleConnection = GoogleConnection.getInstance(mActivity);

        Messenger messenger = new Messenger(handler);
        Intent intent = new Intent(mActivity, UserIntentService.class);
        intent.putExtra(AUTH_TOKEN_KEY, googleConnection.getToken());
        intent.putExtra(REQUEST_CODE_KEY, REQUEST_TYPE_SAVE);
        intent.putExtra(MESSENGER_KEY, messenger);
        intent.putExtra(USER_ENTITY_KEY, mUser);
        mActivity.startService(intent);
    }

    @Override
    public void delete() {

    }

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<User> users) {
        this.mUsers = users;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    static class MessengerHandler extends Handler {

        // A weak reference to the enclosing class
        private final WeakReference<UserManager> outerClass;

        /**
         * A constructor that gets a weak reference to the enclosing class. We
         * do this to avoid memory leaks during Java Garbage Collection.
         *
         * @see https
         * ://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk
         * /lIYDavGYn5UJ
         */
        public MessengerHandler(UserManager outer) {
            outerClass = new WeakReference<UserManager>(outer);
        }

        // Handle any messages that get sent to this Handler
        @Override
        public void handleMessage(Message msg) {

            if (outerClass.get() == null) {
                Log.e(LOG_TAG, "UserManager's Weak reference lost, impossible to notify UI");
                return;
            }

            Bundle bundle = msg.getData();
            bundle.setClassLoader(User.class.getClassLoader());

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

            User user = bundle.getParcelable(USER_ENTITY_KEY);

            int responseStatusCode = bundle.getInt(RESPONSE_CODE_KEY);

            outerClass.get().setChanged();
            switch (responseStatusCode) {
                case RESPONSE_STATUS_OK:
                    outerClass.get().notifyObservers(ApplicationState.USER_SAVED);

                    break;
                case RESPONSE_STATUS_NOT_SAVED:
                    outerClass.get().notifyObservers(ApplicationState.USER_NOT_SAVED);

                    break;
                case RESPONSE_STATUS_UNAUTHORIZED:
                    outerClass.get().notifyObservers(ApplicationState.ACCESS_UNAUTHORIZED);

                    break;
            }
        }

        public void handleFindOneMessage(Bundle bundle) {

            User user = bundle.getParcelable(USER_ENTITY_KEY);

            int responseStatusCode = bundle.getInt(RESPONSE_CODE_KEY);

            outerClass.get().setChanged();
            switch (responseStatusCode) {
                case RESPONSE_STATUS_OK:
                    outerClass.get().setUser(user);
                    outerClass.get().notifyObservers(ApplicationState.USER_FOUND);

                    break;
                case RESPONSE_STATUS_NO_CONTENT:
                    outerClass.get().notifyObservers(ApplicationState.USER_NOT_FOUND);

                    break;
                case RESPONSE_STATUS_UNAUTHORIZED:
                    outerClass.get().notifyObservers(ApplicationState.ACCESS_UNAUTHORIZED);

                    break;
                default:
                    outerClass.get().notifyObservers(ApplicationState.ERROR);

                    break;
            }
        }

        public void handleFindAllMessage(Bundle bundle) {
            ArrayList<User> users = bundle.getParcelableArrayList(USER_ENTITIES_KEY);

            int responseStatusCode = bundle.getInt(RESPONSE_CODE_KEY);
            int approvedStatus = bundle.getInt(USER_APPROVED_STATUS_KEY);

            outerClass.get().setChanged();
            switch (responseStatusCode) {
                case RESPONSE_STATUS_OK:
                    outerClass.get().setUsers(users);

                    switch (approvedStatus) {
                        case Following.PENDING:
                            outerClass.get().notifyObservers(ApplicationState.PENDING_USERS_FOUND);

                            break;
                        case Following.APPROVED:
                            outerClass.get().notifyObservers(ApplicationState.APPROVED_USERS_FOUND);

                            break;
                    }
                    break;
                case RESPONSE_STATUS_NO_CONTENT:

                    switch (approvedStatus) {
                        case Following.PENDING:
                            outerClass.get().notifyObservers(ApplicationState.PENDING_USERS_NOT_FOUND);

                            break;
                        case Following.APPROVED:
                            outerClass.get().notifyObservers(ApplicationState.APPROVED_USERS_NOT_FOUND);

                            break;
                    }

                    break;
                case RESPONSE_STATUS_UNAUTHORIZED:
                    outerClass.get().notifyObservers(ApplicationState.ACCESS_UNAUTHORIZED);

                    break;
            }
        }

        public void handleDeleteMessage(Bundle bundle) {
            Log.e(LOG_TAG, "handleDeleteMessage: Message obtained");
        }
    }
}
