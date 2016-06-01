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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import co.carlosandresjimenez.android.gotit.notification.AlarmReceiver;

/**
 * Created by carlosjimenez on 10/3/15.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private final String LOG_TAG = SettingsFragment.class.getSimpleName();

    Listener mListener;

    AlarmReceiver mAlarmReceiver = new AlarmReceiver();

    public void setListener(Listener l) {
        mListener = l;
    }

    public interface Listener {
        void onSignOutClicked();
    }

    public SettingsFragment() {
    }

    @SuppressLint("ValidFragment")
    public SettingsFragment(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        Preference signOutPref = findPreference(getString(R.string.pref_signout_key));
        signOutPref.setOnPreferenceClickListener(this);

        Preference accountDetailsPref = findPreference(getString(R.string.pref_account_details_key));
        accountDetailsPref.setOnPreferenceClickListener(this);

        Preference accountPrivacyPref = findPreference(getString(R.string.pref_account_privacy_key));
        accountPrivacyPref.setOnPreferenceClickListener(this);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_frequency_key)));
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_notifications_key)));

    }

    public void reviewSettingsVisibility() {

        String userType = Utility.getUserType(getActivity());

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory notificationCategory = (PreferenceCategory) findPreference(getString(R.string.pref_notification_category_key));

        if (preferenceScreen == null || notificationCategory == null)
            return;

        if (userType.equals(getString(R.string.user_type_follower)))
            preferenceScreen.removePreference(notificationCategory);
        else
            preferenceScreen.addPreference(notificationCategory);
    }

    // Registers a shared preference change listener that gets notified when preferences change
    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);

        reviewSettingsVisibility();

        super.onResume();
    }

    // Unregisters a shared preference change listener
    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if (preference.getKey().equals(getString(R.string.pref_signout_key))) {
            if (mListener != null) {
                mListener.onSignOutClicked();
            }
        } else if (preference.getKey().equals(getString(R.string.pref_account_details_key)))
            onClickAccountDetails();
        else if (preference.getKey().equals(getString(R.string.pref_account_privacy_key)))
            onClickAccountPrivacy();

        return true;
    }

    public void onClickAccountDetails() {
        Intent intent = new Intent(getActivity(), AccountDetailsActivity.class);
        startActivity(intent);
    }

    public void onClickAccountPrivacy() {
        Intent intent = new Intent(getActivity(), AccountPrivacyActivity.class);
        startActivity(intent);
    }

    // This gets called after the preference is changed, which is important because we
    // start our synchronization here
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_frequency_key))) {
            mAlarmReceiver.setAlarm(getActivity());

        } else if (key.equals(getString(R.string.pref_notifications_key))) {

            if (Utility.isShowNotifications(getActivity()))
                mAlarmReceiver.setAlarm(getActivity());
            else
                mAlarmReceiver.cancelAlarm(getActivity());
        }
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

}
