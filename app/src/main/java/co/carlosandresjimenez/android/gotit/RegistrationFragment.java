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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.model.people.Person;

import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.beans.UserPrivacy;
import co.carlosandresjimenez.android.gotit.cloud.ApplicationState;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.CloudManager;
import co.carlosandresjimenez.android.gotit.cloud.UserManager;
import co.carlosandresjimenez.android.gotit.connection.ConnectionState;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;
import co.carlosandresjimenez.android.gotit.notification.AlarmReceiver;

/**
 * Created by carlosjimenez on 10/16/15.
 */
public class RegistrationFragment extends DialogFragment implements Observer, DatePickerFragment.Listener {

    private static final String LOG_TAG = RegistrationFragment.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.user_type_selector)
    Spinner mSpUserType;
    @Bind(R.id.email)
    TextView mTvEmail;
    @Bind(R.id.user_name)
    TextView mTvUserName;
    @Bind(R.id.user_lastname)
    TextView mTvUserLastname;
    @Bind(R.id.user_birthdate)
    TextView mTvUserBirthdate;
    @Bind(R.id.user_medical_record_num)
    TextView mTvUserMedicalRecNum;
    CloudManager mCloudManager;
    /* Connection used to interact with Google APIs. */
    private GoogleConnection mGoogleConnection;
    private String userTypeSelected = "";

    private String mUserAvatarUrl = "";

    public RegistrationFragment() {
        setHasOptionsMenu(true);
    }

    @OnItemSelected(R.id.user_type_selector)
    void onSelectUserType(int position) {
        userTypeSelected = getResources().getStringArray(R.array.user_types_values)[position];// (String) mSpUserType.getItemAtPosition(position);
    }

    @OnClick(R.id.user_birthdate)
    void onSelectBirthdate() {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleConnection.addObserver(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, rootView);

        mGoogleConnection = GoogleConnection.getInstance(getActivity());
        String userEmail = mGoogleConnection.getAccountName();
        if (userEmail != null && !userEmail.isEmpty())
            mTvEmail.setText(userEmail);
        else
            Log.e(LOG_TAG, "Cannot get user's email address");

        Person person = mGoogleConnection.getCurrentPerson();
        if (person != null) {
            if (person.hasName()) {
                mTvUserName.setText(person.getName().getGivenName());
                mTvUserLastname.setText(person.getName().getFamilyName());
            }

            if (person.hasBirthday())
                mTvUserBirthdate.setText(person.getBirthday());

            if (person.hasImage())
                mUserAvatarUrl = person.getImage().getUrl();
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != mToolbar) {
            activity.setSupportActionBar(mToolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            //activity.getSupportActionBar().setIcon(R.drawable.ic_clear_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.user_types_labels, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpUserType.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleConnection.deleteObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data instanceof ConnectionState) {
            // checkConnectionState((ConnectionState) data);
        } else if (data instanceof ApplicationState) {
            checkApplicationState((UserManager) observable, (ApplicationState) data);
        }

    }

    public void checkApplicationState(UserManager userManager, ApplicationState applicationState) {

        switch (applicationState) {
            case USER_SAVED:
                Toast.makeText(getActivity(), "User saved...", Toast.LENGTH_SHORT).show();

                mCloudManager.deleteObserver(this);

                Utility.setUserPreferenceValues(getActivity(), userManager.getUser());

                AlarmReceiver alarmReceiver = new AlarmReceiver();
                alarmReceiver.setAlarm(getActivity());

                openMainScreen();

                break;
            case USER_NOT_SAVED:
                Toast.makeText(getActivity(), "Error saving the user", Toast.LENGTH_SHORT).show();

                break;
            case ACCESS_UNAUTHORIZED:
                Toast.makeText(getActivity(), "Access not available", Toast.LENGTH_SHORT).show();

                break;
            case NO_INTERNET:
                Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_SHORT).show();

                break;
            default:
                Toast.makeText(getActivity(), "Unknown error " + applicationState.name(), Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.registration, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveUserDetails();
            item.setEnabled(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveUserDetails() {

        if (mTvEmail.getText().toString().isEmpty() ||
                mTvUserName.getText().toString().isEmpty() ||
                mTvUserLastname.getText().toString().isEmpty() ||
                mTvUserBirthdate.getText().toString().isEmpty() ||
                mTvUserMedicalRecNum.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User("",
                mTvEmail.getText().toString(),
                mUserAvatarUrl,
                mTvUserName.getText().toString(),
                mTvUserLastname.getText().toString(),
                mTvUserBirthdate.getText().toString(),
                "", // TODO: Save telephone number
                mTvUserMedicalRecNum.getText().toString(),
                userTypeSelected,
                "",
                new UserPrivacy(mTvEmail.getText().toString(),
                        UserPrivacy.DEFAULT_SHARE_AVATAR,
                        UserPrivacy.DEFAULT_SHARE_BIRTH_DATE,
                        UserPrivacy.DEFAULT_SHARE_LOCATION,
                        UserPrivacy.DEFAULT_SHARE_FEEDBACK,
                        UserPrivacy.DEFAULT_SHARE_MEDICAL,
                        UserPrivacy.DEFAULT_SHARE_TELEPHONE)
        );

        mCloudManager = CloudFactory.getManager(getActivity(), user);
        mCloudManager.addObserver(this);
        mCloudManager.save();
    }

    public void openMainScreen() {

        Intent intent;
        if (userTypeSelected.equals(getString(R.string.user_type_follower)))
            intent = new Intent(getActivity(), DataFeedActivity.class);
        else
            intent = new Intent(getActivity(), CheckInListActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.dismiss();
        getActivity().finish();
    }

    @Override
    public void onDateSet(String date) {
        mTvUserBirthdate.setText(date);
    }

}
