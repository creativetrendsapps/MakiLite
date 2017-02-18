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
package com.sunshine.makilite.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import net.grandcentrix.tray.TrayAppPreferences;



import java.io.File;
import java.lang.ref.WeakReference;

import com.sunshine.makilite.R;
import com.sunshine.makilite.activities.MakiApplication;

/** Singleton pattern */
public final class Logger {

    private static volatile Logger instance;
    private static final int MSG_SHOW_TOAST = 1;
    private static final Context context = MakiApplication.getContextOfApplication();
    private final TrayAppPreferences trayPreferences;
    private final String logFilePath;
    private final MyHandler messageHandler;

    private Logger() {
        messageHandler = new MyHandler(this);
        trayPreferences = new TrayAppPreferences(context);
        logFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + context.getString(R.string.app_name).replace(" ", "") + ".log";
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null)
                    instance = new Logger();
            }
        }
        return instance;
    }

    private boolean checkStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(context, storagePermission);
        return hasPermission == PackageManager.PERMISSION_GRANTED;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Logger> mLogger;

        public MyHandler(Logger logger) {
            mLogger = new WeakReference<>(logger);
        }

        @Override
        public void handleMessage(Message msg) {
            Logger logger = mLogger.get();
            if (logger != null) {
                if (msg.what == MSG_SHOW_TOAST) {
                    String message = (String) msg.obj;
                    Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void displayStoragePermissionRefused() {
        Message msg = new Message();
        msg.what = MSG_SHOW_TOAST;
        messageHandler.sendMessage(msg);
    }

    public synchronized void i(String tag, String msg) {
        final boolean fileLoggingEnabled = trayPreferences.getBoolean("notify", false);
        final boolean mounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        final boolean storageReady = mounted && checkStoragePermission();

        if (fileLoggingEnabled && storageReady) {
            FileLogger.open(logFilePath, Log.VERBOSE, 1000000);  // 1 megabyte
            FileLogger.i(tag, msg);
            FileLogger.close();
        } else if (fileLoggingEnabled) {
            displayStoragePermissionRefused();
            Log.i(tag, msg);
        } else {
            // use a standard logger instead
            Log.i(tag, msg);
        }
    }

}