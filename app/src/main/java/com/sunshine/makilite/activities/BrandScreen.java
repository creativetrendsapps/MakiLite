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
package com.sunshine.makilite.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.sunshine.makilite.R;
import java.util.Timer;
import java.util.TimerTask;

public class BrandScreen extends Activity {

    private static final int TIME = 5;
    private static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (preferences.getBoolean("first_twitter", false)) {
                    Intent intent = new Intent(BrandScreen.this, TwitterActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(BrandScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, TIME);
    }
}