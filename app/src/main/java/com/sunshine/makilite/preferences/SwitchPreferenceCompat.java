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
package com.sunshine.makilite.preferences;

import com.sunshine.makilite.R;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

public class SwitchPreferenceCompat extends CheckBoxPreference {

	public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SwitchPreferenceCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwitchPreferenceCompat(Context context) {
		super(context);
		init();
	}

	private void init() {
		setWidgetLayoutResource(R.layout.pref_widget_switch);
	}

}