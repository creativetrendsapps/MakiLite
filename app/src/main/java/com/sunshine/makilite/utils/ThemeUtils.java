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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;

import static android.graphics.Color.parseColor;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

import com.sunshine.makilite.R;

final class ThemeUtils {

  // material_deep_teal_500
  static final int FALLBACK_COLOR = parseColor("#3b5999");

  private ThemeUtils() {
    // no instances
  }

  static boolean isAtLeastL() {
    return SDK_INT >= LOLLIPOP;
  }

  @TargetApi(LOLLIPOP)
  static int resolveAccentColor(Context context) {
    Theme theme = context.getTheme();

    // on Lollipop, grab system colorAccent attribute
    // pre-Lollipop, grab AppCompat colorAccent attribute
    // finally, check for custom mp_colorAccent attribute
    int attr = isAtLeastL() ? android.R.attr.colorAccent : R.attr.colorAccent;
    TypedArray typedArray = theme.obtainStyledAttributes(new int[] { attr, R.attr.mp_colorAccent });

    int accentColor = typedArray.getColor(0, FALLBACK_COLOR);
    accentColor = typedArray.getColor(1, accentColor);
    typedArray.recycle();

    return accentColor;
  }

}
