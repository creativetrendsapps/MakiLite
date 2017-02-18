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

import com.sunshine.makilite.R;
import com.sunshine.makilite.utils.PreferencesUtility;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;


@SuppressWarnings("ALL")
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isMakiTheme = PreferencesUtility.getInstance(this).getTheme().equals("maki");
        boolean isMakiDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makidark");
        boolean isMakiMaterialDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makimaterialdark");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
        boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
        boolean isGooglePlusTheme = PreferencesUtility.getInstance(this).getTheme().equals("gplus");
        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isMakiTheme)
                setTheme(R.style.MakiTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);

        } else {

            if (isDarkTheme)
                setTheme(R.style.MakiDark);

            if (isMakiDarkTheme)
                setTheme(R.style.MakiThemeDark);

            if (isMakiMaterialDarkTheme)
                setTheme(R.style.MakiMaterialDark);

            if (isPinkTheme)
                setTheme(R.style.MakiPink);

            if (isFalconTheme)
                setTheme(R.style.MakiFalcon);

            if (isLightBlueTheme)
                setTheme(R.style.MakiLightBlue);

            if (isGooglePlusTheme)
                setTheme(R.style.GooglePlus);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);
        }

        CardView facebook = (CardView) findViewById(R.id.onFacebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://m.facebook.com/sunshineappsst/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        CardView twitter = (CardView) findViewById(R.id.onTwitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://twitter.com/sunshine_dev";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        CardView telegram = (CardView) findViewById(R.id.onTelegram);
        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://telegram.me/st_sunshine";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        CardView vk = (CardView) findViewById(R.id.onVK);
        vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://vk.com/club135547239";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        CardView google = (CardView) findViewById(R.id.onG);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://plus.google.com/communities/104475212201947642753";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        TextView site = (TextView) findViewById(R.id.app_description);
        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://sunshineapps.com.ua/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

}
