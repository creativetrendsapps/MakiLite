package com.sunshine.makilite.pin.interfaces;

import com.sunshine.makilite.pin.enums.KeyboardButtonEnum;
import com.sunshine.makilite.pin.managers.AppLockActivity;
import com.sunshine.makilite.pin.KeyboardButtonView;
import com.sunshine.makilite.ui.KeyboardView;

/**
 * Created by stoyan and oliviergoutay on 1/13/15.
 * The {@link AppLockActivity} will implement
 * this in order to receive events from {@link KeyboardButtonView}
 * and {@link KeyboardView}
 */
public interface KeyboardButtonClickedListener {

    /**
     * Receive the click of a button, just after a {@link android.view.View.OnClickListener} has fired.
     * Called before {@link #onRippleAnimationEnd()}.
     * @param keyboardButtonEnum The organized enum of the clicked button
     */
    public void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum);

    /**
     * Receive the end of a {@link com.andexert.library.RippleView} animation using a
     * {@link com.andexert.library.RippleAnimationListener} to determine the end.
     * Called after {@link #onKeyboardClick(KeyboardButtonEnum)}.
     */
    public void onRippleAnimationEnd();

}
