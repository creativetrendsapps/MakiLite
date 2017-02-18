package com.sunshine.makilite.pin.interfaces;

import android.app.Activity;

import com.sunshine.makilite.pin.managers.AppLockActivity;
import com.sunshine.makilite.pin.managers.AppLockImpl;

/**
 * Created by stoyan on 1/12/15.
 * Allows to follow the LifeCycle of the {@link com.github.orangegangsters.lollipin.lib.PinActivity}
 * Implemented by {@link AppLockImpl} in order to
 * determine when the app was launched for the last time and when to launch the
 * {@link AppLockActivity}
 */
public interface LifeCycleInterface {

    /**
     * Called in {@link Activity#onResume()}
     */
    public void onActivityResumed(Activity activity);

    /**
     * Called in {@link Activity#onPause()}
     */
    public void onActivityPaused(Activity activity);
}
