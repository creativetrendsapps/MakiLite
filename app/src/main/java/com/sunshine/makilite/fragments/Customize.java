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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.sunshine.makilite.R;

public class Customize extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.customize_preferences);
        Preference pro1 = findPreference("pro1");
        Preference pro2 = findPreference("pro2");
        pro1.setOnPreferenceClickListener(this);
        pro2.setOnPreferenceClickListener(this);
    }
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case "pro1":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                break;

            case "pro2":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                break;
        }

        return false;

    }

}


