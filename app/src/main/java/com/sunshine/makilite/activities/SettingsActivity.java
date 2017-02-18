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

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sunshine.makilite.R;
import com.sunshine.makilite.fragments.Settings;
import com.sunshine.makilite.ui.SnackBar;
import com.sunshine.makilite.utils.PreferencesUtility;

import net.grandcentrix.tray.TrayAppPreferences;

@TargetApi(23)
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int REQUEST_STORAGE = 1;
    public static final String KEY_PREF_HIDE_EDITOR = "hide_editor_newsfeed";
    private static SharedPreferences preferences;
    private TrayAppPreferences trayPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean isMakiTheme = PreferencesUtility.getInstance(this).getTheme().equals("maki");
        boolean isMakiDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makidark");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
        boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
        boolean isGooglePlusTheme = PreferencesUtility.getInstance(this).getTheme().equals("gplus");
        boolean isMakiMaterialDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makimaterialdark");
        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isMakiTheme)
                setTheme(R.style.MakiTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
        } else {

            if (isDarkTheme)
                setTheme(R.style.MakiDark);

            if (isMakiDarkTheme)
                setTheme(R.style.MakiThemeDark);

            if (isPinkTheme)
                setTheme(R.style.MakiPink);

            if (isFalconTheme)
                setTheme(R.style.MakiFalcon);

            if (isMakiMaterialDarkTheme)
                setTheme(R.style.MakiMaterialDark);

            if (isGooglePlusTheme)
                setTheme(R.style.GooglePlus);

            if (isLightBlueTheme)
                setTheme(R.style.MakiLightBlue);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            trayPreferences = new TrayAppPreferences(getApplicationContext());

            if (preferences.getBoolean("lock_portrait", false)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            getFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new Settings()).commit();
        }

    }
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            changes();
        } else
            getFragmentManager().popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.rate_maki:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_app_store))));
                return true;

            case R.id.invite:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.downloadThisApp));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.thanks),
                        Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Storage permission granted");
                    // It's awesome, dude!
                } else {
                    Log.e(TAG, "Storage permission denied");
                    new SnackBar(this, getString(R.string.permission_not_granted), Snackbar.LENGTH_LONG)
                            .setTextColor(Color.parseColor("#FFFFFF"))
                            .setBackgroundColor(Color.parseColor("#354f88"))
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void changes() {

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra("apply_changes_to_app", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}