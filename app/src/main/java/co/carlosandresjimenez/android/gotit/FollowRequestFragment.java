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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
public class FollowRequestFragment extends Fragment implements FollowRequestAdapter.Listener {

    private static final String LOG_TAG = FollowRequestFragment.class.getSimpleName();

    Listener mListener;

    ListView listView;
    ArrayList<User> mUsers;
    ProgressDialog progressDialog;
    FollowRequestAdapter mAdapter;

    public interface Listener {
        void onFollowingUsersRequested(int approvedStatus);

        void onAcceptRequested(String userEmail, boolean followBack);

        void onRejectRequested(String userEmail);
    }

    public FollowRequestFragment() {

    }

    @SuppressLint("ValidFragment")
    public FollowRequestFragment(Listener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_request, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.follow_request_list);

        mListener.onFollowingUsersRequested(Following.PENDING);
        mAdapter = new FollowRequestAdapter(getActivity(), this, null);

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

        return view;
    }

    public void openUserDetails(String email) {
        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.USER_EMAIL_KEY, email);
        startActivity(intent);
    }

    public void refreshRequestList(ArrayList<User> users) {
        this.mUsers = users;

        mAdapter = new FollowRequestAdapter(getActivity(), this, mUsers);

        if (listView != null)
            listView.setAdapter(mAdapter);
    }

    public void clearFollowRequestAdapter() {
        mAdapter.clear();
    }

    @Override
    public void onAcceptRequested(String userEmail) {
        openAcceptDialog(userEmail);
    }

    @Override
    public void onRejectRequested(String userEmail) {
        openRejectDialog(userEmail);
    }

    public void openAcceptDialog(final String userEmail) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
        alertDialogBuilder.setTitle("Accept request");

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.dialog_follow_accept, userEmail))
                .setCancelable(false)
                .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAcceptRequested(userEmail, true);
                        dialog.dismiss();
                        showProgressDialog(getString(R.string.progressdialog_follow_accept));
                    }
                })
                .setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAcceptRequested(userEmail, true);
                        dialog.dismiss();
                        showProgressDialog(getString(R.string.progressdialog_follow_reject));
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        // show it
        alertDialog.show();
    }

    public void openRejectDialog(final String userEmail) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle("Reject request");

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.dialog_follow_reject, userEmail))
                .setCancelable(false)
                .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onRejectRequested(userEmail);
                        dialog.dismiss();
                        showProgressDialog(getString(R.string.progressdialog_follow_reject));
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        // show it
        alertDialog.show();
    }

    public void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(getActivity(), null, message, true, false);
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }


}