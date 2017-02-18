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

import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.luseen.verticalintrolibrary.VerticalIntro;
import com.luseen.verticalintrolibrary.VerticalIntroItem;
import com.sunshine.makilite.R;

public class IntroActivity extends VerticalIntro {

    @Override
    protected void init() {

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorPrimary)
                .image(R.drawable.ic_slide1)
                .title(getResources().getString(R.string.welcome_intro))
                .text(getResources().getString(R.string.slide1))
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorPrimary)
                .image(R.drawable.four)
                .title(getResources().getString(R.string.name2))
                .text(getResources().getString(R.string.slide2))
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorPrimary)
                .image(R.drawable.android)
                .title(getResources().getString(R.string.name3))
                .text(getResources().getString(R.string.slide3))
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.md_pink_300)
                .image(R.drawable.ic_slide6)
                .title(getResources().getString(R.string.name4))
                .text(getResources().getString(R.string.slide4))
                .build());

        setSkipEnabled(false);
        setVibrateEnabled(true);
        setNextText(getResources().getString(R.string.next_tab));
        setDoneText(getResources().getString(R.string.lets));
        setVibrateIntensity(20);
        setCustomTypeFace(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));
    }

    @Override
    protected Integer setLastItemBottomViewColor() {
        return R.color.md_pink_300;
    }

    @Override
    protected void onSkipPressed(View view) {
        Log.e("onSkipPressed ", "onSkipPressed");
    }

    @Override
    protected void onFragmentChanged(int position) {
        Log.e("onFragmentChanged ", "" + position);
    }

    @Override
    protected void onDonePressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.turn_on),
                Toast.LENGTH_LONG).show();
    }
}
