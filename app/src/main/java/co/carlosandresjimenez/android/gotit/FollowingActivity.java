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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.carlosandresjimenez.android.gotit.beans.Following;
import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.UserManager;

/**
 * Created by carlosjimenez on 10/4/15.
 */
public class FollowingActivity extends BaseActivity implements
        FollowFragment.Listener, FollowingListFragment.Listener, FollowRequestFragment.Listener {

    private static final String LOG_TAG = FollowingActivity.class.getSimpleName();

    private final int FOLLOWING_FRAGMENT_ID = 0;
    private final int REQUESTS_FRAGMENT_ID = 1;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_toolbar);

        mCurrentActivity = ACTIVITY_FOLLOWING_KEY;

        mViewPager = (ViewPager) findViewById(R.id.fragment_viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.toolbar_tablayout);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mViewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case FOLLOWING_FRAGMENT_ID:
                        break;
                    case REQUESTS_FRAGMENT_ID:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFrag(new FollowingListFragment(this), "FOLLOWING");
        mViewPagerAdapter.addFrag(new FollowRequestFragment(this), "REQUESTS");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    public void onFabClicked() {
        super.onFabClicked();

        openFollowDialog();
    }

    public void openFollowDialog() {
        FollowFragment fragment = new FollowFragment();
        fragment.setListener(this);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onFollowRequested(String userEmail) {
        Log.i(LOG_TAG, "Follow requested to user " + userEmail);
    }

    @Override
    public void onApprovedUsersFound(UserManager userManager) {
        super.onApprovedUsersFound(userManager);

        FollowingListFragment followingListFragment = (FollowingListFragment) mViewPagerAdapter.getItem(FOLLOWING_FRAGMENT_ID);

        if (followingListFragment != null)
            followingListFragment.refreshUserList(userManager.getUsers());
    }

    @Override
    public void onApprovedUsersNotFound() {
        super.onApprovedUsersNotFound();
    }

    @Override
    public void onPendingUsersFound(UserManager userManager) {
        super.onPendingUsersFound(userManager);

        FollowRequestFragment followRequestFragment = (FollowRequestFragment) mViewPagerAdapter.getItem(REQUESTS_FRAGMENT_ID);

        if (followRequestFragment != null)
            followRequestFragment.refreshRequestList(userManager.getUsers());
    }

    @Override
    public void onPendingUsersNotFound() {
        super.onPendingUsersNotFound();
    }

    @Override
    public void onFollowingNotSaved() {
        super.onFollowingNotSaved();

        FollowRequestFragment followRequestFragment = (FollowRequestFragment) mViewPagerAdapter.getItem(REQUESTS_FRAGMENT_ID);

        if (followRequestFragment != null)
            followRequestFragment.dismissProgressDialog();
    }

    @Override
    public void onFollowingSaved() {
        super.onFollowingSaved();

        FollowRequestFragment followRequestFragment = (FollowRequestFragment) mViewPagerAdapter.getItem(REQUESTS_FRAGMENT_ID);

        if (followRequestFragment != null) {

            Log.i(LOG_TAG, "Following saved, dismissing dialog, clearing adapter and getting a new list");

            followRequestFragment.dismissProgressDialog();

            followRequestFragment.clearFollowRequestAdapter();
            onFollowingUsersRequested(Following.PENDING);
        }
    }

    @Override
    public void onFollowingCannotFollow() {
        super.onFollowingCannotFollow();

        FollowRequestFragment followRequestFragment = (FollowRequestFragment) mViewPagerAdapter.getItem(REQUESTS_FRAGMENT_ID);

        if (followRequestFragment != null) {
            followRequestFragment.dismissProgressDialog();

            Log.i(LOG_TAG, "Following saved, dismissin dialog, clearing adapter and getting a new list");

            followRequestFragment.clearFollowRequestAdapter();
            onFollowingUsersRequested(Following.PENDING);
        }
    }

    @Override
    public void onFollowingUsersRequested(int approvedStatus) {
        mCloudManager = CloudFactory.getManager(this, new User(approvedStatus));
        mCloudManager.addObserver(this);
        mCloudManager.findAll();
    }

    @Override
    public void onAcceptRequested(String userEmail, boolean followBack) {
        mCloudManager = CloudFactory.getManager(this, new Following(userEmail, null, Following.APPROVED, true));
        mCloudManager.addObserver(this);
        mCloudManager.save();
    }

    @Override
    public void onRejectRequested(String userEmail) {
        mCloudManager = CloudFactory.getManager(this, new Following(userEmail, null, Following.REJECTED, false));
        mCloudManager.addObserver(this);
        mCloudManager.save();
    }
}
