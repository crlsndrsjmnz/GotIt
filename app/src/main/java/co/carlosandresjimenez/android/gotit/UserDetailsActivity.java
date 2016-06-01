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

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.cloud.ApplicationState;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.CloudManager;
import co.carlosandresjimenez.android.gotit.cloud.UserManager;

/**
 * Created by carlosjimenez on 11/8/15.
 */
public class UserDetailsActivity extends AppCompatActivity implements Observer {

    public static final String USER_EMAIL_KEY = "USER_EMAIL";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.user_name)
    TextView mTvUserName;
    @Bind(R.id.email)
    TextView mTvEmail;
    @Bind(R.id.user_birthdate)
    TextView mTvBirthdate;
    @Bind(R.id.user_medical_record_num)
    TextView mTvMedicalRecord;
    @Bind(R.id.user_type)
    TextView mTvUserType;
    @Bind(R.id.user_avatar)
    ImageView mIvAvatar;

    @Bind(R.id.ll_birthdate)
    LinearLayout mLlBirthDate;
    @Bind(R.id.ll_medical_record)
    LinearLayout mLlMedicalRecord;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        getUserDetails(getIntent().getStringExtra(USER_EMAIL_KEY));
    }

    public void getUserDetails(String email) {
        if (email != null && !email.isEmpty()) {
            showProgressDialog(getString(R.string.progressdialog_loading));

            CloudManager cloudManager = CloudFactory.getManager(this, new User(email));
            cloudManager.addObserver(this);
            cloudManager.findOne();
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        ApplicationState applicationState = (ApplicationState) data;
        observable.deleteObserver(this);

        switch (applicationState) {
            case USER_FOUND:
                onUserFound((UserManager) observable);

                break;
            case USER_NOT_FOUND:
                onUserNotFound();

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

    public void onUserFound(UserManager userManager) {
        User user = userManager.getUser();

        if (user != null) {
            mTvUserName.setText(user.getName() + " " + user.getLastName());
            mTvEmail.setText(user.getEmail());

            if (user.getBirthDate() != null && !user.getBirthDate().isEmpty()) {
                mLlBirthDate.setVisibility(View.VISIBLE);
                mTvBirthdate.setText(user.getBirthDate());
            }

            if (user.getBirthDate() != null && !user.getBirthDate().isEmpty()) {
                mLlMedicalRecord.setVisibility(View.VISIBLE);
                mTvMedicalRecord.setText(user.getMedicalRecNum());
            }

            String userType = "";
            if (user.getUserType() != null && !user.getUserType().isEmpty()) {
                userType = user.getUserType().equals(getString(R.string.user_type_follower)) ?
                        getString(R.string.user_type_follower_label) :
                        getString(R.string.user_type_patient_label);
            }

            mTvUserType.setText(userType);

            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                int sizeParamIndex = 0;
                // Remove the avatar size limitation
                if (avatarUrl.contains("?sz=")) {
                    sizeParamIndex = avatarUrl.lastIndexOf("?sz=");
                    avatarUrl = avatarUrl.substring(0, sizeParamIndex);
                }

            }

            Glide.with(this)
                    .load(avatarUrl)
                    .error(Utility.getDrawable(this, R.drawable.ic_account_circle_white_24dp, Color.LTGRAY))
                    .crossFade()
                    .fitCenter()
                    .into(mIvAvatar);

        }

        progressDialog.dismiss();
    }

    public void onUserNotFound() {
        progressDialog.dismiss();
    }

    public void onAccessUnauthorized() {

    }

    public void onNoInternet() {

    }

    public void onNoApplicationState(String stateName) {

    }

    public void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(this, null, message, true, true);
    }
}
