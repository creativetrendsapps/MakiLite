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

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Html;
import com.sunshine.makilite.R;
import com.sunshine.makilite.activities.MakiApplication;

public class Credits extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about_preferences);
		Context context = MakiApplication.getContextOfApplication();
		PreferenceManager.getDefaultSharedPreferences(context);

		//open about dialog
		Preference preferenceabout = findPreference("about");
		preferenceabout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder =
						new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
				builder.setTitle(getResources().getString(R.string.about_header));
				builder.setMessage(Html.fromHtml(getResources().getString(R.string.about_text)));
				builder.setPositiveButton(getResources().getString(R.string.ok), null);
				builder.setNegativeButton(null, null);
				builder.show();

				return true;
			}
		});


	}
}