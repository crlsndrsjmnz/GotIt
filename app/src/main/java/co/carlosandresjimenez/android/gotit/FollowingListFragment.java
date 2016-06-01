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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Following;
import co.carlosandresjimenez.android.gotit.beans.User;

/**
 * Created by carlosjimenez on 10/30/15.
 */
public class FollowingListFragment extends Fragment {

    private static final String LOG_TAG = FollowingListFragment.class.getSimpleName();

    Listener mListener;

    ListView listView;
    ArrayList<User> mUsers;

    public interface Listener {
        void onFollowingUsersRequested(int approvedStatus);

        void onFabClicked();
    }

    public FollowingListFragment() {

    }

    @SuppressLint("ValidFragment")
    public FollowingListFragment(Listener l) {
        mListener = l;
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following_list, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.following_list);

        mListener.onFollowingUsersRequested(Following.APPROVED);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                User user = mUsers.get(position);
                openUserDetails(user.getEmail());
            }

        });

        setupFab(view);

        return view;
    }

    public void openUserDetails(String email) {
        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.USER_EMAIL_KEY, email);
        startActivity(intent);
    }

    public void refreshUserList(ArrayList<User> users) {
        this.mUsers = users;

        UserAdapter adapter = new UserAdapter(getActivity(), mUsers);

        if (listView != null)
            listView.setAdapter(adapter);
    }

    public void setupFab(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFabClicked();
                }
            });
        }
    }

}