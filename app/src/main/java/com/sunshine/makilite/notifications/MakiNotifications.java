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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.TaskStackBuilder;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.sunshine.makilite.R;
import com.sunshine.makilite.activities.MakiApplication;
import com.sunshine.makilite.activities.MainActivity;
import com.sunshine.makilite.services.Connectivity;
import com.sunshine.makilite.utils.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MakiNotifications extends Service {

    // Facebook URL constants
    private static final String BASE_URL = "https://mobile.facebook.com";
    private static final String NOTIFICATIONS_URL = "https://m.facebook.com/notifications.php";
    private static final String MESSAGES_URL = "https://m.facebook.com/messages";
    private static final String MESSAGES_URL_BACKUP = "https://mobile.facebook.com/messages";
    private static final String NOTIFICATION_OLD_MESSAGE_URL = "https://m.facebook.com/messages#";


    private static final int MAX_RETRY = 3;
    private static final int JSOUP_TIMEOUT = 10000;
    private static final String TAG;


    private final HandlerThread handlerThread;
    private final Handler handler;
    private static Runnable runnable;


    private volatile boolean shouldContinue = true;
    private static String userAgent;
    private SharedPreferences preferences;


    private final Logger Log;


    static {
        TAG = MakiNotifications.class.getSimpleName();
    }


    public MakiNotifications() {
        handlerThread = new HandlerThread("Handler Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        Log = Logger.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "********** Service created! **********");
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        runnable = new HandlerRunnable();


        handler.postDelayed(runnable, 3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: Service stopping...");
        super.onDestroy();

        synchronized (handler) {
            shouldContinue = false;
            handler.notify();
        }

        handler.removeCallbacksAndMessages(null);
        handlerThread.quit();
    }


    private class HandlerRunnable implements Runnable {

        public void run() {
            try {

                final int timeInterval = Integer.parseInt(preferences.getString("interval_pref", "1800000"));
                Log.i(TAG, "Time interval: " + (timeInterval / 1000) + " seconds");


                final long now = System.currentTimeMillis();
                final long sinceLastCheck = now - preferences.getLong("last_check", now);
                final boolean ntfLastStatus = preferences.getBoolean("ntf_last_status", false);
                final boolean msgLastStatus = preferences.getBoolean("msg_last_status", false);

                if ((sinceLastCheck < timeInterval) && ntfLastStatus && msgLastStatus) {
                    final long waitTime = timeInterval - sinceLastCheck;
                    if (waitTime >= 1000) {
                        Log.i(TAG, "I'm going to wait. Resuming in: " + (waitTime / 1000) + " seconds");

                        synchronized (handler) {
                            try {
                                handler.wait(waitTime);
                            } catch (InterruptedException ex) {
                                Log.i(TAG, "Thread interrupted");
                            } finally {
                                Log.i(TAG, "Lock is now released");
                            }
                        }

                    }
                }


                if (shouldContinue) {

                    if (Connectivity.isConnected(getApplicationContext())) {
                        Log.i(TAG, "Internet connection active. Starting AsyncTask...");
                        String connectionType = "Wi-Fi";
                        if (Connectivity.isConnectedMobile(getApplicationContext()))
                            connectionType = "Mobile";
                        Log.i(TAG, "Connection Type: " + connectionType);
                        userAgent = preferences.getString("webview_user_agent", System.getProperty("http.agent"));
                        Log.i(TAG, "User Agent: " + userAgent);

                        if (preferences.getBoolean("notifications_activated", false))
                            new CheckNotificationsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                        if (preferences.getBoolean("messages_activated", false))
                            new CheckMessagesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);


                        preferences.edit().putLong("last_check", System.currentTimeMillis()).apply();
                    } else
                        Log.i(TAG, "No internet connection. Skip checking.");


                    handler.postDelayed(runnable, timeInterval);
                } else
                    Log.i(TAG, "Notified to stop running. Exiting...");

            } catch (RuntimeException re) {
                Log.i(TAG, "RuntimeException caught");
                restartItself();
            }
        }

    }

    /** Notifications checker task: it checks Facebook notifications only. */
    private class CheckNotificationsTask extends AsyncTask<Void, Void, Element> {

        boolean syncProblemOccurred = false;

        private Element getElement(String connectUrl) {
            try {
                return Jsoup.connect(connectUrl).userAgent(userAgent).timeout(JSOUP_TIMEOUT)
                        .cookie("https://mobile.facebook.com", CookieManager.getInstance().getCookie("https://mobile.facebook.com")).get()
                        .select("a.touchable").not("a._19no").not("a.button").first();
            } catch (IllegalArgumentException ex) {
                Log.i("CheckNotificationsTask", "Cookie sync problem occurred");
                if (!syncProblemOccurred) {

                    syncProblemOccurred = true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected Element doInBackground(Void... params) {
            Element result = null;
            int tries = 0;

            syncCookies();

            while (tries++ < MAX_RETRY && result == null) {
                Log.i("CheckNotificationsTask", "doInBackground: Processing... Trial: " + tries);
                Log.i("CheckNotificationsTask", "Trying: " + NOTIFICATIONS_URL);
                Element notification = getElement(NOTIFICATIONS_URL);
                if (notification != null)
                    result = notification;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Element result) {
            try {
                if (result == null)
                    return;
                if (result.text() == null)
                    return;

                String time = result.select("span.mfss.fcg").text();
                String text = result.text().replace(time, "");

                if (!preferences.getBoolean("activity_visible", false) || preferences.getBoolean("notifications_everywhere", true)) {
                    if (!preferences.getString("last_notification_text", "").equals(text))
                        notifier(text, BASE_URL + result.attr("href"), false);
                    preferences.edit().putString("last_notification_text", text).apply();
                }

                // save this check status
                preferences.edit().putBoolean("ntf_last_status", true).apply();
                Log.i("CheckNotificationsTask", "onPostExecute: Aight biatch ;)");
            } catch (NumberFormatException ex) {
                // save this check status
                preferences.edit().putBoolean("ntf_last_status", false).apply();
                Log.i("CheckNotificationsTask", "onPostExecute: Failure");
            }
        }

    }


    /** Messages checker task: it checks new messages only. */
    private class CheckMessagesTask extends AsyncTask<Void, Void, String> {

        boolean syncProblemOccurred = false;

        private String getNumber(String connectUrl) {
            try {
                Elements message = Jsoup.connect(connectUrl).userAgent(userAgent).timeout(JSOUP_TIMEOUT)
                        .cookie("https://m.facebook.com", CookieManager.getInstance().getCookie("https://m.facebook.com")).get()
                        .select("div#viewport").select("div#page").select("div._129-")
                        .select("#messages_jewel").select("span._59tg");

                return message.html();
            } catch (IllegalArgumentException ex) {
                Log.i("CheckMessagesTask", "Cookie sync problem occurred");
                if (!syncProblemOccurred) {

                    syncProblemOccurred = true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return "failure";
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            int tries = 0;

            syncCookies();

            while (tries++ < MAX_RETRY && result == null) {
                Log.i("CheckMessagesTask", "doInBackground: Processing... Trial: " + tries);


                Log.i("CheckMsgTask:getNumber", "Trying: " + MESSAGES_URL);
                String number = getNumber(MESSAGES_URL);
                if (!number.matches("^[+-]?\\d+$")) {
                    Log.i("CheckMsgTask:getNumber", "Trying: " + MESSAGES_URL_BACKUP);
                    number = getNumber(MESSAGES_URL_BACKUP);
                }
                if (number.matches("^[+-]?\\d+$"))
                    result = number;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                int newMessages = Integer.parseInt(result);
                if (!preferences.getBoolean("activity_visible", false) || preferences.getBoolean("notifications_everywhere", true)) {
                    if(newMessages == 1)
                        notifier(getString(R.string.you_have_one_message), NOTIFICATION_OLD_MESSAGE_URL, true);
                } else if (newMessages > 1)
                    notifier(String.format(getString(R.string.you_have_n_messages), newMessages), NOTIFICATION_OLD_MESSAGE_URL, true);


                preferences.edit().putBoolean("msg_last_status", true).apply();
                Log.i("CheckMessagesTask", "onPostExecute: Aight biatch ;)");
            } catch (NumberFormatException ex) {

                preferences.edit().putBoolean("msg_last_status", false).apply();
                Log.i("CheckMessagesTask", "onPostExecute: Failure");
            }
        }

    }


    @SuppressWarnings("deprecation")
    private void syncCookies() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(getApplicationContext());
            CookieSyncManager.getInstance().sync();
        }
    }


    private void restartItself() {
        final Context context = MakiApplication.getContextOfApplication();
        final Intent intent = new Intent(context, MakiNotifications.class);
        context.stopService(intent);
        context.startService(intent);
    }


    @SuppressWarnings("deprecation")
    private void notifier(String title, String url, boolean isMessage) {


        final String contentTitle;
        if (isMessage)
            contentTitle = getString(R.string.app_name);
        else
            contentTitle = getString(R.string.app_name);


        Log.i(TAG, "Start notification - isMessage: " + isMessage);

        Intent actionIntent = new Intent(this, MainActivity.class);
        actionIntent.putExtra("start_url", "https://m.facebook.com/notifications");
        actionIntent.setAction("NOTIFICATION_URL_ACTION");
        PendingIntent actionPendingIntent =  PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent messageIntent = new Intent(this, MainActivity.class);
        messageIntent.putExtra("start_url", "https://m.facebook.com/messages");
        messageIntent.setAction("NOTIFICATION_URL_ACTION");
        PendingIntent messagePendingIntent = PendingIntent.getActivity(this, 1, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.forum, getString(R.string.app_name), actionPendingIntent)
                .build();

        NotificationCompat.Action message = new NotificationCompat.Action.Builder(R.drawable.notify, getString(R.string.app_name), messagePendingIntent)
                .build();


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setColor(getResources().getColor(R.color.colorPrimaryDark))
                        .setContentTitle(contentTitle)
                        .setContentText(title)
                        .setTicker(title)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true);


        String ringtoneKey = "ringtone";
        if (isMessage)
            ringtoneKey = "ringtone_msg";

        Uri ringtoneUri = Uri.parse(preferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);


        if (preferences.getBoolean("vibrate", false))
            mBuilder.setVibrate(new long[] {500, 500});
        else
            mBuilder.setVibrate(new long[] {0L});


        if (preferences.getBoolean("led_light", false)) {
            Resources resources = getResources(), systemResources = Resources.getSystem();
            mBuilder.setLights(Color.CYAN,
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOff", "integer", "android")));

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (isMessage) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("start_url", url);
            intent.setAction("NOTIFICATION_URL_ACTION");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.extend(new WearableExtender().addAction(message));
            mBuilder.setOngoing(false);
            mBuilder.setSmallIcon(R.drawable.forum);
            mBuilder.setOnlyAlertOnce(true);
            Notification note = mBuilder.build();
            mNotificationManager.notify(1, note);

        } else {
            mBuilder.setSmallIcon(R.drawable.notify);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("start_url", url);
            intent.setAction("NOTIFICATION_URL_ACTION");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.extend(new WearableExtender().addAction(action));
            mBuilder.setOngoing(false);
            Notification note = mBuilder.build();
            mNotificationManager.notify(0, note);


            if (preferences.getBoolean("led_light", false))
                note.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

    }


    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager)
                MakiApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    public static void clearMessages() {
        NotificationManager notificationManager = (NotificationManager)
                MakiApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

}
