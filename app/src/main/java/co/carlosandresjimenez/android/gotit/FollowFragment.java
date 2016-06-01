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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import co.carlosandresjimenez.android.gotit.beans.Following;
import co.carlosandresjimenez.android.gotit.cloud.ApplicationState;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.CloudManager;

/**
 * Created by carlosjimenez on 10/4/15.
 */
public class FollowFragment extends DialogFragment implements Observer {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    TextView mTvEmail;
    ProgressDialog progressDialog;

    Listener mListener;

    CloudManager mCloudManager;
    String mSessionEmail;

    public void setListener(Listener l) {
        mListener = l;
    }

    public interface Listener {
        void onFollowRequested(String userEmail);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_follow, null);

        mTvEmail = (TextView) dialogView.findViewById(R.id.email);
        mSessionEmail = Utility.getUserEmail(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(R.string.title_dialog_follow)
                .setView(dialogView)
                        // Add action buttons
                .setPositiveButton(R.string.action_request, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FollowFragment.this.getDialog().cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new RequestPermissionListener(alertDialog));

        return alertDialog;
    }

    private class RequestPermissionListener implements View.OnClickListener {
        private final Dialog dialog;

        public RequestPermissionListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            // put your code here
            Log.i(LOG_TAG, "Button id: " + v.getId());

            String mValue = mTvEmail.getText().toString();

            if (mValue.isEmpty()) {
                openResultDialog("Invalid email entered");
                return;
            }

            if (mValue.equals(mSessionEmail)) {
                openResultDialog("Invalid email entered, you cannot follow yourself.");
                return;
            }

            mListener.onFollowRequested(mTvEmail.getText().toString());
            Utility.hideSoftKeyboard(getActivity(), mTvEmail);
            requestPermission();
            openValidatingDialog();
        }
    }

    public void openValidatingDialog() {
        progressDialog = ProgressDialog.show(getActivity(), null, "Requesting permission...", true, false);
    }

    public void openResultDialog(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle("Request error");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.action_edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close current activity
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close the dialog box and do nothing
                        dialog.cancel();
                        FollowFragment.this.getDialog().dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        // show it
        alertDialog.show();

    }

    public void requestPermission() {
        mCloudManager = CloudFactory.getManager(getActivity(), new Following(mTvEmail.getText().toString()));
        mCloudManager.addObserver(this);
        mCloudManager.save();
    }

    @Override
    public void update(Observable observable, Object data) {

        switch ((ApplicationState) data) {
            case FOLLOWING_SAVED:
                progressDialog.dismiss();
                mCloudManager.deleteObserver(this);
                this.dismiss();

                break;
            case FOLLOWING_CANNOT_FOLLOW:
                progressDialog.dismiss();
                mCloudManager.deleteObserver(this);
                openResultDialog("Follow request not saved. User has a Follower profile and cannot be followed.");

                break;
            case FOLLOWING_NOT_FOUND:
                progressDialog.dismiss();
                mCloudManager.deleteObserver(this);
                openResultDialog("Follow request not saved. User not found");

                break;
            case FOLLOWING_NOT_SAVED:
                progressDialog.dismiss();
                mCloudManager.deleteObserver(this);
                openResultDialog("Follow request not saved");

                break;
            case ACCESS_UNAUTHORIZED:

                break;
            case NO_INTERNET:

                break;
            default:

                break;
        }
    }


}
