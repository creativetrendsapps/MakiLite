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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

package com.sunshine.makilite.activities.MakiApplication;


public class MakiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context = MakiApplication.getContextOfApplication();
        Intent startIntent = new Intent(context, MakiNotifications.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false)) {
            context.startService(startIntent);
            Log.d("NotificationReceiver", "Notifications started");
        }else {
            context.stopService(startIntent);
            Log.d("PollReceiver", "Notifications canceled");
        }
    }
}
