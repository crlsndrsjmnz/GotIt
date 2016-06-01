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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.carlosandresjimenez.android.gotit.beans.User;
import co.carlosandresjimenez.android.gotit.beans.UserPrivacy;

/**
 * Created by carlosjimenez on 10/22/15.
 */
public class Utility {

    public static String getDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
                Locale.US);
        return sdf.format(new Date());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isAutoLogin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_connection_autologin_key), false);
    }

    public static void changeAutoLogin(Context context, boolean autoLogin) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_connection_autologin_key), autoLogin);
        editor.apply();
    }

    public static void changeUserSettingsEditFlag(Context context, boolean modified) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_account_modified_key), modified);
        editor.apply();
    }

    public static String getUserType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.user_type_key), context.getString(R.string.user_type_follower));
    }

    public static int getNotificationFrequency(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(prefs.getString(context.getString(R.string.pref_frequency_key), context.getString(R.string.pref_frequency_8)));
    }

    public static boolean isShowNotifications(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_notifications_key),
                Boolean.valueOf(context.getString(R.string.pref_notifications_default)));
    }

    public static boolean isAccountStored(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_account_stored_key), false);
    }

    public static void setUserPreferenceValues(Context context, User user) {

        if (user == null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean accountStored = prefs.getBoolean(context.getString(R.string.pref_account_stored_key), false);

        //if (!accountStored) {
        SharedPreferences.Editor editor = prefs.edit();

        if (!accountStored)
            editor.putBoolean(context.getString(R.string.pref_account_stored_key), true);

        editor.putString(context.getString(R.string.pref_account_userid_key), user.getUserId());
        editor.putString(context.getString(R.string.pref_account_email_key), user.getEmail());
        editor.putString(context.getString(R.string.pref_account_avatar_key), user.getAvatarUrl());
        editor.putString(context.getString(R.string.pref_account_firstname_key), user.getName());
        editor.putString(context.getString(R.string.pref_account_lastname_key), user.getLastName());
        editor.putString(context.getString(R.string.pref_account_birthdate_key), user.getBirthDate());
        editor.putString(context.getString(R.string.pref_account_telephone_key), user.getTelephoneNumber());
        editor.putString(context.getString(R.string.pref_account_medical_key), user.getMedicalRecNum());
        editor.putString(context.getString(R.string.user_type_key), user.getUserType());

        UserPrivacy userPrivacy = user.getUserPrivacy();

        if (userPrivacy != null) {
            editor.putBoolean(context.getString(R.string.pref_privacy_medical_key), userPrivacy.isShareMedical()); // Share Medical
            editor.putBoolean(context.getString(R.string.pref_privacy_birthdate_key), userPrivacy.isShareBirthDate()); // Share BirthDate
            editor.putBoolean(context.getString(R.string.pref_privacy_avatar_key), userPrivacy.isShareAvatar()); // Share Avatar
            // editor.putBoolean(getString(R.string.pref_privacy_), false); // Share CheckIn Location
            // editor.putBoolean(getString(R.string.pref_privacy_), false); // Share Telephone Number
            editor.putBoolean(context.getString(R.string.pref_privacy_feedback_key), userPrivacy.isShareFeedback()); // Share Feedback
        } else {
            editor.putBoolean(context.getString(R.string.pref_privacy_medical_key), false); // Share Medical
            editor.putBoolean(context.getString(R.string.pref_privacy_birthdate_key), false); // Share BirthDate
            editor.putBoolean(context.getString(R.string.pref_privacy_avatar_key), true); // Share Avatar
            // editor.putBoolean(getString(R.string.pref_privacy_), false); // Share CheckIn Location
            // editor.putBoolean(getString(R.string.pref_privacy_), false); // Share Telephone Number
            editor.putBoolean(context.getString(R.string.pref_privacy_feedback_key), true); // Share Feedback
        }

        editor.apply();
        /*
        } else {
            // TODO: validate that the data in preferences is the same as the cloud
        }
        */
    }

    public static User getUserPreferenceValues(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean accountStored = prefs.getBoolean(context.getString(R.string.pref_account_stored_key), false);

        User user = null;
        if (accountStored) {

            user = new User(prefs.getString(context.getString(R.string.pref_account_userid_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_email_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_avatar_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_firstname_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_lastname_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_birthdate_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_telephone_key), ""),
                    prefs.getString(context.getString(R.string.pref_account_medical_key), ""),
                    prefs.getString(context.getString(R.string.user_type_key), ""),
                    "",
                    new UserPrivacy(prefs.getString(context.getString(R.string.pref_account_email_key), ""),
                            prefs.getBoolean(context.getString(R.string.pref_privacy_avatar_key), true),
                            prefs.getBoolean(context.getString(R.string.pref_privacy_birthdate_key), false),
                            false,
                            prefs.getBoolean(context.getString(R.string.pref_privacy_feedback_key), true),
                            prefs.getBoolean(context.getString(R.string.pref_privacy_medical_key), false),
                            false)
            );

        }

        return user;
    }

    public static String getUserEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_account_email_key), null);
    }

    public static void clearPreferenceValues(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public static Drawable getDrawable(Context context, int drawableResId, int color) {
        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            drawable = context.getDrawable(drawableResId);
        else
            drawable = context.getResources().getDrawable(drawableResId);

        if (drawable != null) {
            Bitmap mNewBitmap = ((BitmapDrawable) drawable).getBitmap();

            Bitmap resultBitmap = Bitmap.createBitmap(mNewBitmap, 0, 0,
                    mNewBitmap.getWidth() - 1, mNewBitmap.getHeight() - 1);
            Paint p = new Paint();
            ColorFilter filter = new LightingColorFilter(color, 1);
            p.setColorFilter(filter);

            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(resultBitmap, 0, 0, p);

            return new BitmapDrawable(context.getResources(), resultBitmap);
        }

        return null;
    }
}
