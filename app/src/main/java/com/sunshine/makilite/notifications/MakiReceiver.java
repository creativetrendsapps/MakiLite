/**  Copyright (C) 2016-2017  Roman Savchyn/Sunshine Apps
 Code is taken from:
 - Folio for Facebook by creativetrendsapps. Thank you!
 - Simple for Facebook by creativetrendsapps. Thank you!
 - FaceSlim by indywidualny. Thank you!
 - Toffed by JakeLane. Thank you!
 - SlimSocial by  Leonardo Rignanese. Thank you!
 - MaterialFBook by ZeeRooo. Thank you!
 - Simplicity by creativetrendsapps. Thank you!
 Copyright notice must remain here if you're using any part of this code.
 **/
package com.sunshine.makilite.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import net.grandcentrix.tray.TrayAppPreferences;


public class MakiReceiver extends BroadcastReceiver {

    public static void scheduleAlarms(Context ctxt, boolean cancel) {
        AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, MakiNotifications.class);
        PendingIntent pi = PendingIntent.getService(ctxt, 0, i, 0);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        TrayAppPreferences trayPreferences = new TrayAppPreferences(ctxt);


        trayPreferences.put("interval_pref", Integer.parseInt(preferences.getString("interval_pref", "1800000")));
        trayPreferences.put("ringtone", preferences.getString("ringtone", "content://settings/system/notification_sound"));
        trayPreferences.put("vibrate", preferences.getBoolean("vibrate", false));
        trayPreferences.put("led_light", preferences.getBoolean("led_light", false));
        trayPreferences.put("notifications_everywhere", preferences.getBoolean("notifications_everywhere", true));
        trayPreferences.put("notifications_activated", preferences.getBoolean("notifications_activated", false));



        if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false)&& !cancel) {
            int interval = Integer.parseInt(preferences.getString("interval_pref", "1800000"));
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 1800000, interval, pi);
        } else {
            mgr.cancel(pi);

        }
    }


    @Override
    public void onReceive(Context ctxt, Intent i) {
        scheduleAlarms(ctxt, false);
    }
}
