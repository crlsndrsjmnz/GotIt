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

package co.carlosandresjimenez.android.gotit.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import co.carlosandresjimenez.android.gotit.MainActivity;
import co.carlosandresjimenez.android.gotit.R;
import co.carlosandresjimenez.android.gotit.Utility;

/**
 * Created by carlosjimenez on 11/13/15.
 * <p/>
 * When the alarm fires, this BroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SchedulingService} to do some work.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private final String LOG_TAG = AlarmReceiver.class.getSimpleName();

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;

    public static final long ONE_HOUR_MILLISECONDS = 1000 * 60 * 60;

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context);
    }

    private void createNotification(Context context) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            color = context.getColor(R.color.colorPrimary);
        else
            color = context.getResources().getColor(R.color.colorPrimary);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_stat_got_it)
                        .setColor(color)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.notification_text)))
                        .setContentText(context.getString(R.string.notification_text));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context
     */
    public void setAlarm(Context context) {

        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmIntent.cancel();
            alarmMgr.cancel(alarmIntent);
        }

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        int userFrequencySetting = Utility.getNotificationFrequency(context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR, userFrequencySetting);

        // Set the alarm to fire in X hours according to the device's
        // clock and user settings, and to repeat according to user settings
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), userFrequencySetting * ONE_HOUR_MILLISECONDS, alarmIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the alarm.
     *
     * @param context
     */
    public void cancelAlarm(Context context) {

        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmIntent.cancel();
            alarmMgr.cancel(alarmIntent);
        }

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
