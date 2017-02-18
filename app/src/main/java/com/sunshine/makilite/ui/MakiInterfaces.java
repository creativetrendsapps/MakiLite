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
package com.sunshine.makilite.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;


import com.sunshine.makilite.activities.MainActivity;


public class MakiInterfaces {
    private final MainActivity mContext;
    private final SharedPreferences mPreferences;

    public MakiInterfaces(MainActivity c) {
        mContext = c;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }



    @JavascriptInterface
    public void loadingCompleted() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setLoading(false);
            }
        });
    }

    @JavascriptInterface
    public void getCurrent(final String value) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value) {

                    default:

                        break;
                }
            }
        });
    }


    @JavascriptInterface
    public void getNums(final String notifications, final String messages, final String requests, final String feed) {
        final int notifications_int = MakiHelpers.isInteger(notifications) ? Integer.parseInt(notifications) : 0;
        final int messages_int = MakiHelpers.isInteger(messages) ? Integer.parseInt(messages) : 0;
        final int requests_int = MakiHelpers.isInteger(requests) ? Integer.parseInt(requests): 0;
        final int feed_int = MakiHelpers.isInteger(feed) ? Integer.parseInt(feed): 0;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setNotificationNum(notifications_int);
                mContext.setMessagesNum(messages_int);

            }
        });
    }
}
