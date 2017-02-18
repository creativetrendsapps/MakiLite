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

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar.SnackbarLayout;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.github.clans.fab.FloatingActionMenu;


public class TabLayoutBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {

  public TabLayoutBehavior(Context context, AttributeSet attrs) {
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
    return dependency instanceof AHBottomNavigation;
  }



  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
    float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
    child.setTranslationY(translationY);
    return true;
  }
}