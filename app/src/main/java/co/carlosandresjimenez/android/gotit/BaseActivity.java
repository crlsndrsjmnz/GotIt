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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.plus.model.people.Person;

import java.util.Observable;
import java.util.Observer;

import co.carlosandresjimenez.android.gotit.beans.Answer;
import co.carlosandresjimenez.android.gotit.beans.Checkin;
import co.carlosandresjimenez.android.gotit.beans.Question;
import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.cloud.AnswerManager;
import co.carlosandresjimenez.android.gotit.cloud.ApplicationState;
import co.carlosandresjimenez.android.gotit.cloud.CheckinManager;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.CloudManager;
import co.carlosandresjimenez.android.gotit.cloud.QuestionManager;
import co.carlosandresjimenez.android.gotit.cloud.UserManager;
import co.carlosandresjimenez.android.gotit.connection.ConnectionState;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by carlosjimenez on 10/28/15.
 */
public class BaseActivity extends AppCompatActivity implements Observer {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    protected static final String CHECKIN_ID_KEY = "CHECKIN_ID";
    protected static final String CHECKIN_ALLOW_SHARING = "CHECKIN_ALLOW_SHARING";
    protected static final String CHECKIN_FRAGMENT_TAG = "CHECKIN_FRAGMENT";
    protected static final String FOLLOWING_FRAGMENT_TAG = "FOLLOWING_FRAGMENT";
    protected static final String REQUESTS_FRAGMENT_TAG = "REQUESTS_FRAGMENT";

    protected static final int ACTIVITY_FEED_KEY = 0;
    protected static final int ACTIVITY_CHECKIN_KEY = 1;
    protected static final int ACTIVITY_FOLLOWING_KEY = 2;
    protected static final int ACTIVITY_GRAPH_KEY = 3;
    protected static final int ACTIVITY_SETTINGS_KEY = 4;
    protected static final int ACTIVITY_ABOUT_KEY = 5;

    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    private static final int RC_PERM_GET_ACCOUNTS = 2;

    String mUserName;
    String mUserEmail;
    String mUserAvatar;
    String mUserCover;
    int mCurrentActivity;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /* Connection used to interact with Google APIs. */
    private GoogleConnection mGoogleConnection;

    CloudManager mCloudManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleConnection = GoogleConnection.getInstance(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleConnection != null)
            mGoogleConnection.deleteObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        reviewDrawerConfiguration();

        if (mGoogleConnection != null)
            mGoogleConnection.addObserver(this);
    }

    public void setupUI() {
        setUserDetails();

        setNavigationUi();
        setupFab();
    }

    public void setUserDetails() {

        mUserEmail = "";
        mUserName = "";
        mUserAvatar = "";
        mUserCover = "";

        // Stop if we don't have permission to get users' email address (which requires GET_ACCOUNTS permission)
        if (!checkAccountsPermission()) {
            return;
        }

        if (mGoogleConnection != null && mGoogleConnection.isSignedIn() &&
                mGoogleConnection.getCurrentPerson() != null) {

            mUserEmail = mGoogleConnection.getAccountName();

            Person person = mGoogleConnection.getCurrentPerson();
            if (person.hasDisplayName())
                mUserName = person.getDisplayName();

            if (person.hasImage())
                mUserAvatar = person.getImage().getUrl();

            if (person.hasCover())
                mUserCover = person.getCover().getCoverPhoto().getUrl();
        }

    }

    public void reviewDrawerConfiguration() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (navigationView != null && navigationView.getMenu() != null) {
            String userType = Utility.getUserType(this);

            MenuItem menuItemCheckin = navigationView.getMenu().findItem(R.id.navigation_item_checkin);
            MenuItem menuItemGraph = navigationView.getMenu().findItem(R.id.navigation_item_graph);

            if (userType.equals(getString(R.string.user_type_follower))) {
                menuItemCheckin.setVisible(false);
                menuItemGraph.setVisible(false);
            } else {
                menuItemCheckin.setVisible(true);
                menuItemGraph.setVisible(true);
            }

            setDrawerItemChecked(navigationView);
        }
    }

    public void setNavigationUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if (mDrawerLayout != null) {

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

            // TODO: fix on the way? http://stackoverflow.com/a/33163288/3346625
            View headerLayout = navigationView.inflateHeaderView(R.layout.drawer_header);
            setDrawerHeaderInfo(headerLayout);

            //setDrawerItemChecked(navigationView);
            //setDrawerHeaderInfo(navigationView);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    mDrawerLayout.closeDrawers();
                    //menuItem.setChecked(true);

                    switch (menuItem.getItemId()) {
                        case R.id.navigation_item_feed:
                            openFeedActivity();
                            break;
                        case R.id.navigation_item_following:
                            openFollowingActivity();
                            break;
                        case R.id.navigation_item_checkin:
                            openCheckinListActivity();
                            break;
                        case R.id.navigation_item_graph:
                            openFeedbackGraphActivity();
                            break;
                        case R.id.navigation_item_settings:
                            openSettingsActivity();
                            break;
                        case R.id.navigation_item_about:
                            openAboutActivity();
                            break;
                        default:
                            Log.e(LOG_TAG, "Menu item not found");
                    }

                    return true;
                }
            });

            mDrawerToggle.syncState();
        }
    }

    public void setDrawerItemChecked(NavigationView navigationView) {

        switch (mCurrentActivity) {
            case ACTIVITY_FEED_KEY:
                navigationView.setCheckedItem(R.id.navigation_item_feed);
                break;
            case ACTIVITY_FOLLOWING_KEY:
                navigationView.setCheckedItem(R.id.navigation_item_following);
                break;
            case ACTIVITY_CHECKIN_KEY:
                navigationView.setCheckedItem(R.id.navigation_item_checkin);
                break;
            case ACTIVITY_SETTINGS_KEY:
                navigationView.setCheckedItem(R.id.navigation_item_settings);
                break;
            case ACTIVITY_GRAPH_KEY:
                navigationView.setCheckedItem(R.id.navigation_item_graph);
                break;
            case ACTIVITY_ABOUT_KEY:
                navigationView.setCheckedItem(R.id.navigation_item_about);
                break;
            default:
                Log.e(LOG_TAG, "Item not recognized.");
        }
    }

    public void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClicked();
                }
            });
        }
    }

    public void setDrawerHeaderInfo(View navigationView) {
        TextView tvHeaderName = (TextView) navigationView.findViewById(R.id.header_name);
        TextView tvHeaderEmail = (TextView) navigationView.findViewById(R.id.header_email);
        ImageView ivHeaderCover = (ImageView) navigationView.findViewById(R.id.header_cover);
        CircleImageView ivHeaderAvatar = (CircleImageView) navigationView.findViewById(R.id.header_avatar);

        if (mUserName != null && !mUserName.isEmpty() && tvHeaderName != null)
            tvHeaderName.setText(mUserName);

        if (mUserEmail != null && !mUserEmail.isEmpty() && tvHeaderEmail != null)
            tvHeaderEmail.setText(mUserEmail);

        if (mUserCover != null && !mUserCover.isEmpty() && ivHeaderCover != null)
            Glide.with(this)
                    .load(mUserCover)
                    .crossFade()
                    .centerCrop()
                    .into(ivHeaderCover);

        if (mUserAvatar != null && !mUserAvatar.isEmpty()) {
            int sizeParamIndex;
            // Remove the avatar size limitation
            if (mUserAvatar.contains("?sz=")) {
                sizeParamIndex = mUserAvatar.lastIndexOf("?sz=");
                mUserAvatar = mUserAvatar.substring(0, sizeParamIndex);
            }
        }

        if (ivHeaderAvatar != null)
            Glide.with(this)
                    .load(mUserAvatar)
                    .error(Utility.getDrawable(this, R.drawable.ic_account_circle_white_24dp, Color.LTGRAY))
                    .crossFade()
                    .fitCenter()
                    .into(ivHeaderAvatar);

    }

    public void onFabClicked() {

    }

    public void createSnackBar(int messageResId) {
        Snackbar.make(findViewById(R.id.main_layout),
                messageResId,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request the permission again.
                        v.setVisibility(View.GONE);
                    }
                }).show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerLayout != null)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean isSignedIn() {
        return mGoogleConnection.isSignedIn();
    }

    public void connectToGoogle() {
        mGoogleConnection.connect();
    }

    protected void resolveConnection(int resultCode) {
        mGoogleConnection.onActivityResult(resultCode);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof ConnectionState) {
            checkConnectionState((ConnectionState) data);
        } else if (data instanceof ApplicationState) {
            checkApplicationState((ApplicationState) data, observable);
        }
    }

    public void checkConnectionState(ConnectionState connectionState) {

        switch (connectionState) {
            case CREATED:

                onConnectionCreated();
                break;
            case OPENING:

                onConnectionOpening();
                break;
            case OPENED:

                onConnectionOpened();
                break;
            case CLOSED:

                onConnectionClosed();
                break;
            case TOKEN_OK:
                onTokenOk();

                break;
            case TOKEN_ERROR:
                onTokenError();

                break;
            case TOKEN_EXPIRED:
                onTokenExpired();

                break;
            default:
                onNoConnectionState(connectionState.name());
        }
    }

    public void checkApplicationState(ApplicationState applicationState, Observable observable) {

        //observable.deleteObserver(this);

        switch (applicationState) {
            case USER_FOUND:
                onUserFound((UserManager) observable);

                break;
            case USER_NOT_FOUND:
                onUserNotFound();

                break;
            case QUESTIONS_FOUND:
                onQuestionsFound((QuestionManager) observable);

                break;
            case QUESTIONS_NOT_FOUND:
                onQuestionsNotFound();

                break;
            case CHECKINS_FOUND:
                onCheckinsFound((CheckinManager) observable);

                break;
            case CHECKINS_NOT_FOUND:
                onCheckinsNotFound();

                break;
            case ANSWERS_FOUND:
                onAnswersFound((AnswerManager) observable);

                break;
            case ANSWERS_NOT_FOUND:
                onAnswersNotFound();

                break;
            case APPROVED_USERS_FOUND:
                onApprovedUsersFound((UserManager) observable);

                break;
            case APPROVED_USERS_NOT_FOUND:
                onApprovedUsersNotFound();

                break;
            case PENDING_USERS_FOUND:
                onPendingUsersFound((UserManager) observable);

                break;
            case PENDING_USERS_NOT_FOUND:
                onPendingUsersNotFound();

                break;
            case FOLLOWING_SAVED:
                onFollowingSaved();

                break;
            case FOLLOWING_NOT_SAVED:
                onFollowingNotSaved();

                break;
            case FOLLOWING_CANNOT_FOLLOW:
                onFollowingCannotFollow();

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

    public void onConnectionCreated() {
        Log.i(LOG_TAG, "########## Google Connection State - Connection Created");
    }

    public void onConnectionOpening() {
        Log.i(LOG_TAG, "########## Google Connection State - Connection Opening");
    }

    public void onConnectionOpened() {
        Log.i(LOG_TAG, "########## Google Connection State - Connection Opened");
    }

    public void onConnectionClosed() {
        Log.i(LOG_TAG, "########## Google Connection State - Connection Closed");
    }

    public void onTokenOk() {
        Log.i(LOG_TAG, "########## Google Connection State - Token Ok");
    }

    public void onTokenError() {
        Log.i(LOG_TAG, "########## Google Connection State - Token Error");
    }

    public void onTokenExpired() {
        Log.i(LOG_TAG, "########## Google Connection State - Token Expired");
        mGoogleConnection.renewToken();
    }

    public void onNoConnectionState(String stateName) {
        Log.i(LOG_TAG, "########## Google Connection State - No Connection State " + stateName);
    }

    public void onUserFound(UserManager userManager) {
        Log.i(LOG_TAG, ":::::::::: User found");
    }

    public void onUserNotFound() {
        Log.e(LOG_TAG, ":::::::::: User not found");
    }

    public void onQuestionsFound(QuestionManager questionManager) {
        int size = questionManager.getQuestions() != null ? questionManager.getQuestions().size() : -1;
        Log.i(LOG_TAG, ":::::::::: Questions found, size: " + size);
    }

    public void onQuestionsNotFound() {
        Log.e(LOG_TAG, ":::::::::: Questions not found");
    }

    public void onCheckinsFound(CheckinManager checkinManager) {
        int size = checkinManager.getCheckins() != null ? checkinManager.getCheckins().size() : -1;
        Log.i(LOG_TAG, ":::::::::: Checkins found, size: " + size);
    }

    public void onCheckinsNotFound() {
        Log.e(LOG_TAG, ":::::::::: Checkins not found");
    }

    public void onAnswersFound(AnswerManager answerManager) {
        int size = answerManager.getAnswers() != null ? answerManager.getAnswers().size() : -1;
        Log.i(LOG_TAG, ":::::::::: Answers found, size: " + size);
    }

    public void onAnswersNotFound() {
        Log.e(LOG_TAG, ":::::::::: Answers not found");
    }

    public void onApprovedUsersFound(UserManager userManager) {
        int size = userManager.getUsers() != null ? userManager.getUsers().size() : -1;
        Log.i(LOG_TAG, ":::::::::: Approved Users found, size: " + size);
    }

    public void onApprovedUsersNotFound() {
        Log.e(LOG_TAG, ":::::::::: Approved users not found");
    }

    public void onPendingUsersFound(UserManager userManager) {
        int size = userManager.getUsers() != null ? userManager.getUsers().size() : -1;
        Log.i(LOG_TAG, ":::::::::: Pending users found, size: " + size);
    }

    public void onPendingUsersNotFound() {
        Log.e(LOG_TAG, ":::::::::: Pending user not found");
    }

    public void onFollowingSaved() {
        Log.i(LOG_TAG, ":::::::::: Following request saved");
    }

    public void onFollowingNotSaved() {
        Log.e(LOG_TAG, ":::::::::: Following request not saved");
    }

    public void onFollowingCannotFollow() {
        Log.e(LOG_TAG, ":::::::::: Following cannot follow");
    }

    public void onAccessUnauthorized() {
        Log.e(LOG_TAG, ":::::::::: Access unauthorized");

        createSnackBar(R.string.error_service_unavailable);
    }

    public void onNoInternet() {
        Log.e(LOG_TAG, ":::::::::: No internet");

        createSnackBar(R.string.error_network_unavailable);
    }

    public void onNoApplicationState(String stateName) {
        Log.e(LOG_TAG, ":::::::::: No Application State " + stateName);
    }

    public void getCheckinList() {
        mCloudManager = CloudFactory.getManager(this, new Checkin(true));
        mCloudManager.addObserver(this);
        mCloudManager.findAll();
    }

    public void getCheckinFeedList() {
        mCloudManager = CloudFactory.getManager(this, new Checkin(false));
        mCloudManager.addObserver(this);
        mCloudManager.findAll();
    }

    public void getCheckinDetails() {
        String checkinId = getIntent().getStringExtra(CheckInListActivity.CHECKIN_ID_KEY);

        mCloudManager = CloudFactory.getManager(this, new Question(checkinId));
        mCloudManager.addObserver(this);
        mCloudManager.findAll();
    }

    public void getQuestionList() {
        mCloudManager = CloudFactory.getManager(this, new Question());
        mCloudManager.addObserver(this);
        mCloudManager.findAll();
    }

    public void getGraphData(String email) {
        mCloudManager = CloudFactory.getManager(this, new Answer(email));
        mCloudManager.addObserver(this);
        mCloudManager.findAll();
    }

    public void searchUsername() {
        String emailAddress;

        // We are signed in!
        // Retrieve some profile information to personalize our app for the user.
        try {
            if (checkAccountsPermission()) {
                emailAddress = mGoogleConnection.getAccountName();

                mCloudManager = CloudFactory.getManager(this, new User(emailAddress));
                mCloudManager.addObserver(this);
                mCloudManager.findOne();

            } else {
                Log.e(LOG_TAG, "User has denied permission to access email address.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Check if we have the GET_ACCOUNTS permission and request it if we do not.
     *
     * @return true if we have the permission, false if we do not.
     */
    private boolean checkAccountsPermission() {
        final String perm = Manifest.permission.GET_ACCOUNTS;
        int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // We have the permission
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
            // Need to show permission rationale, display a snackbar and then request
            // the permission again when the snackbar is dismissed.
            Snackbar.make(findViewById(R.id.main_layout),
                    R.string.contacts_permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Request the permission again.
                            ActivityCompat.requestPermissions(BaseActivity.this,
                                    new String[]{perm},
                                    RC_PERM_GET_ACCOUNTS);
                        }
                    }).show();
            return false;
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{perm},
                    RC_PERM_GET_ACCOUNTS);
            return false;
        }
    }

    public void openCheckinDetails(String checkinId, boolean isAllowSharing) {
        Intent intent = new Intent(this, CheckinDetailsActivity.class);
        intent.putExtra(CHECKIN_ID_KEY, checkinId);
        intent.putExtra(CHECKIN_ALLOW_SHARING, isAllowSharing);
        startActivity(intent);
    }

    public void openSettingsActivity() {
        if (mCurrentActivity != ACTIVITY_SETTINGS_KEY) {
            startActivity(new Intent(BaseActivity.this, SettingsActivity.class));
        }
    }

    public void openFollowingActivity() {
        if (mCurrentActivity != ACTIVITY_FOLLOWING_KEY) {
            Intent intent = new Intent(BaseActivity.this, FollowingActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void openCheckinListActivity() {
        if (mCurrentActivity != ACTIVITY_CHECKIN_KEY) {
            Intent intent = new Intent(BaseActivity.this, CheckInListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void openFeedActivity() {
        if (mCurrentActivity != ACTIVITY_FEED_KEY) {
            Intent intent = new Intent(BaseActivity.this, DataFeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void openAboutActivity() {
        if (mCurrentActivity != ACTIVITY_ABOUT_KEY) {
            Intent intent = new Intent(BaseActivity.this, AboutActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void openFeedbackGraphActivity() {
        if (mCurrentActivity != ACTIVITY_GRAPH_KEY) {
            Intent intent = new Intent(BaseActivity.this, FeedbackGraphActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void openRegistrationScreen() {
        Intent intent = new Intent(BaseActivity.this, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void openMainScreen() {
        String userType = Utility.getUserType(this);

        Intent intent;
        if (userType.equals(getString(R.string.user_type_follower)))
            intent = new Intent(BaseActivity.this, DataFeedActivity.class);
        else
            intent = new Intent(BaseActivity.this, CheckInListActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

}
