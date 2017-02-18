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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.sunshine.makilite.R;
import com.sunshine.makilite.pin.managers.AppLockActivity;
import com.sunshine.makilite.utils.PreferencesUtility;


public class CustomPinActivity extends AppLockActivity {
    private static SharedPreferences preferences;
    boolean isMakiTheme = PreferencesUtility.getInstance(this).getTheme().equals("maki");
    boolean isMakiDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makidark");
    boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
    boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
    boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
    boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
    boolean isMakiMaterialDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makimaterialdark");
    boolean isGooglePlusTheme = PreferencesUtility.getInstance(this).getTheme().equals("gplus");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            if (isMakiTheme)
                setTheme(R.style.MakiTheme);

            if (isDarkTheme)
                setTheme(R.style.MakiDark);

            if (isMakiDarkTheme)
                setTheme(R.style.MakiThemeDark);

            if (isPinkTheme)
                setTheme(R.style.MakiPink);

            if (isFalconTheme)
                setTheme(R.style.MakiFalcon);

            if (isLightBlueTheme)
                setTheme(R.style.MakiLightBlue);

            if (isMakiMaterialDarkTheme)
                setTheme(R.style.MakiMaterialDark);

            if (isGooglePlusTheme)
                setTheme(R.style.GooglePlus);

        super.onCreate(savedInstanceState);
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean("lock_portrait", false)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }

    @Override
    public void onPinFailure(int attempts) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(250);
    }

    @Override
    public void onPinSuccess(int attempts) {
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }

}
