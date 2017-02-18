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
package com.sunshine.makilite.fragments;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.util.Log;
import android.widget.RemoteViews;


import com.sunshine.makilite.R;
import com.sunshine.makilite.activities.MakiApplication;
import com.sunshine.makilite.activities.QuickFacebook;
import com.sunshine.makilite.activities.QuickInstagram;
import com.sunshine.makilite.activities.QuickTwitter;
import com.sunshine.makilite.activities.QuickVK;
import com.sunshine.makilite.notifications.MakiNotifications;
import com.sunshine.makilite.preferences.SwitchPreferenceCompat;

import net.grandcentrix.tray.TrayAppPreferences;

@SuppressLint({ "NewApi", "ServiceCast" })
public class Notify extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private static Context context;
    private SharedPreferences preferences;
    public static final String FACEBOOK = "https://m.facebook.com/";
    public static final String INSTAGRAM = "https://instagram.com/";
    public static final String TWITTER = "https://twitter.com/";
    public static final String VK = "https://m.vk.com/";
    private TrayAppPreferences trayPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.notifications_preferences);


        context = MakiApplication.getContextOfApplication();


        trayPreferences = new TrayAppPreferences(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ListPreference lp = (ListPreference) findPreference("interval_pref");
        String temp1 = getString(R.string.interval_pref_description).replace("%s", "");
        String temp2 = lp.getSummary().toString();
        if (temp1.equals(temp2))
            lp.setValueIndex(2);


        myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                final Intent intent = new Intent(context, MakiNotifications.class);

                switch (key) {
                    case "interval_pref":
                        // update Tray Preference before restarting the service
                        trayPreferences.put("interval_pref", Integer.parseInt(preferences.getString("interval_pref", "1800000")));
                        // restart the service after time interval change
                        if (prefs.getBoolean("notifications_activated", false)) {
                            context.stopService(intent);
                            context.startService(intent);
                        }
                        break;
                    case "ringtone":
                        trayPreferences.put("ringtone", preferences.getString("ringtone", "content://settings/system/notification_sound"));
                        break;
                    case "ringtone_msg":
                        trayPreferences.put("ringtone_msg", preferences.getString("ringtone_msg", "content://settings/system/notification_sound"));
                        break;
                    case "vibrate":
                        trayPreferences.put("vibrate", preferences.getBoolean("vibrate", false));
                        break;
                    case "led_light":
                        trayPreferences.put("led_light", preferences.getBoolean("led_light", false));
                        break;
                    case "notifications_everywhere":
                        trayPreferences.put("notifications_everywhere", preferences.getBoolean("notifications_everywhere", true));
                        break;

                    case "notifications_activated":
                        trayPreferences.put("notifications_activated", preferences.getBoolean("notifications_activated", false));
                        if (prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("messages_activated", false)) {
                            context.stopService(intent);
                            context.startService(intent);
                        } else //noinspection StatementWithEmptyBody
                            if (!prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("messages_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("notifications_activated", false) && !preferences.getBoolean("messages_activated", false)) {
                                context.startService(intent);
                            } else
                                context.stopService(intent);
                        break;
                    case "messages_activated":
                        trayPreferences.put("messages_activated", preferences.getBoolean("messages_activated", false));
                        if (prefs.getBoolean("messages_activated", false) && preferences.getBoolean("notifications_activated", false)) {
                            context.stopService(intent);
                            context.startService(intent);
                        } else //noinspection StatementWithEmptyBody
                            if (!prefs.getBoolean("messages_activated", false) && preferences.getBoolean("notifications_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("messages_activated", false) && !preferences.getBoolean("notifications_activated", false)) {
                                context.startService(intent);
                            } else
                                context.stopService(intent);
                        break;
                }

                // what's going on, dude?
                Log.v("SharedPreferenceChange", key + " changed in NotificationsSettingsFragment");
            }
        };


        SwitchPreferenceCompat quickbar = (SwitchPreferenceCompat) findPreference("quickbar_pref");
        quickbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                NotificationManager notificationmanager = (NotificationManager) MakiApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                if ((Boolean) newValue) {


                    RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.quickbar);

                    remoteView.setTextViewText(R.id.quick, getString(R.string.app_name));
                    remoteView.setTextViewText(R.id.quick_bar, getString(R.string.quick_bar));
                    Builder builder = new Builder(context);
                    builder.setSmallIcon(R.drawable.ic_stat_f)
                            .setTicker(getString(R.string.quick_bar_on))
                            .setOngoing(true)
                            .setContent(remoteView)
                            .setPriority(Notification.PRIORITY_MIN);


                    Intent quickFacebook = new Intent(getActivity().getApplicationContext(), QuickFacebook.class);
                    quickFacebook.setData(Uri.parse(FACEBOOK));
                    quickFacebook.setAction(Intent.ACTION_VIEW);
                    PendingIntent facebookIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickFacebook,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_facebook, facebookIntent);


                    Intent quickTwitter = new Intent(getActivity().getApplicationContext(), QuickTwitter.class);
                    quickTwitter.setData(Uri.parse(TWITTER));
                    quickTwitter.setAction(Intent.ACTION_VIEW);
                    PendingIntent twitterIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickTwitter,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_twitter, twitterIntent);


                    Intent quickInstagram = new Intent(getActivity().getApplicationContext(), QuickInstagram.class);
                    quickInstagram.setData(Uri.parse(INSTAGRAM));
                    quickInstagram.setAction(Intent.ACTION_VIEW);
                    PendingIntent instagramIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickInstagram,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_instagram, instagramIntent);

                    Intent quickVK = new Intent(getActivity().getApplicationContext(), QuickVK.class);
                    quickVK.setData(Uri.parse(VK));
                    quickVK.setAction(Intent.ACTION_VIEW);
                    PendingIntent vkIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, quickVK,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    remoteView.setOnClickPendingIntent(R.id.quick_vk, vkIntent);


                    notificationmanager.notify(22, builder.build());


                } else {
                    notificationmanager.cancel(22);

                }
                return true;
            }
        });


    }




    @Override
    public void onStart() {
        super.onStart();

        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
    }

    @Override
    public void onStop() {
        super.onStop();

        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }



    @Override
    public void onResume() {
        super.onResume();


        String ringtoneString = preferences.getString("ringtone", "content://settings/system/notification_sound");
        Uri ringtoneUri = Uri.parse(ringtoneString);
        String name;

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            name = ringtone.getTitle(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpn = (RingtonePreference) findPreference("ringtone");
        rpn.setSummary(getString(R.string.notification_sound_description) + name);


        ringtoneString = preferences.getString("ringtone_msg", "content://settings/system/notification_sound");
        ringtoneUri = Uri.parse(ringtoneString);

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            name = ringtone.getTitle(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpm = (RingtonePreference) findPreference("ringtone_msg");
        rpm.setSummary(getString(R.string.message_sound_description) + name);
    }


}
