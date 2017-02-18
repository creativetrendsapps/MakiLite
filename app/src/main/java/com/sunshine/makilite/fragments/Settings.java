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

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.sunshine.makilite.activities.IntroActivity;
import com.sunshine.makilite.pin.managers.AppLock;
import com.sunshine.makilite.R;
import com.sunshine.makilite.activities.AboutActivity;
import com.sunshine.makilite.activities.CustomPinActivity;
import com.sunshine.makilite.activities.MakiApplication;
import com.sunshine.makilite.utils.FileOperation;

import net.grandcentrix.tray.TrayAppPreferences;


public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    private static Context context;
    private TrayAppPreferences trayPreferences;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = Settings.class.getSimpleName();
    private static final int REQUEST_CODE_ENABLE = 11;
    private static final int REQUEST_CODE_DISABLE = 12;
    private static final int REQUEST_SFINGER = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = MakiApplication.getContextOfApplication();
        trayPreferences = new TrayAppPreferences(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                switch (key) {

                    case "maki_locker":
                        trayPreferences.put("maki_locker", preferences.getBoolean("maki_locker", false));
                        if (prefs.getBoolean("maki_locker", false)) {
                            Intent intent = new Intent(getActivity(), CustomPinActivity.class);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
                            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                            startActivityForResult(intent, REQUEST_CODE_ENABLE);
                        } else {
                            Intent intent = new Intent(getActivity(), CustomPinActivity.class);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
                            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK);
                            startActivityForResult(intent, REQUEST_CODE_DISABLE);
                        }
                        break;


                    case "allow_location":
                        trayPreferences.put("allow_location", preferences.getBoolean("allow_location", false));
                        if (prefs.getBoolean("allow_location", false)) {
                            requestLocationPermission();
                        }
                            break;
                }

            }
        };

        addPreferencesFromResource(R.xml.preferences);
        Preference notifications = findPreference("notifications_settings");
        Preference firsttwitter = findPreference("first_twitter");
        Preference customize = findPreference("custom_settings");
        Preference about = findPreference("about_settings");
        Preference location = findPreference ("allow_location");
        Preference credits = findPreference("credits_settings");
        Preference translate = findPreference("help_translate");
        Preference tabs = findPreference("allow_inside");
        Preference play = findPreference("play_intro");
        Preference news = findPreference("whats_new");
        Preference getkey = findPreference("help_development");
        Preference clearCachePref = findPreference("clear");
        Preference source = findPreference("maki_source");

        firsttwitter.setOnPreferenceClickListener(this);
        translate.setOnPreferenceClickListener(this);
        play.setOnPreferenceClickListener(this);
        tabs.setOnPreferenceClickListener(this);
        news.setOnPreferenceClickListener(this);
        notifications.setOnPreferenceClickListener(this);
        location.setOnPreferenceClickListener(this);
        customize.setOnPreferenceClickListener(this);
        about.setOnPreferenceClickListener(this);
        credits.setOnPreferenceClickListener(this);
        translate.setOnPreferenceClickListener(this);
        clearCachePref.setOnPreferenceClickListener(this);
        getkey.setOnPreferenceClickListener(this);
        source.setOnPreferenceClickListener(this);
    }


    @SuppressWarnings("ResourceType")
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {
            case "notifications_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Notify()).commit();
                break;

            case "custom_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Customize()).commit();
                break;

            case "first_twitter":
                Toast.makeText(getActivity(), getString(R.string.reload_app), Toast.LENGTH_LONG).show();
                break;

            case "play_intro":
                Intent intro = new Intent(getActivity(), IntroActivity.class);
                startActivity(intro);
                break;

            case "whats_new":
                AlertDialog.Builder whats_new = new AlertDialog.Builder(getActivity());
                whats_new.setTitle(getResources().getString(R.string.what_new));
                whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new)));
                whats_new.setPositiveButton(getResources().getString(R.string.great), null);
                whats_new.setNegativeButton(getString(R.string.like_on), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://m.facebook.com/sunshineappsst/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                whats_new.show();
                break;


            case "about_settings":
                Intent settings = new Intent(getActivity(), AboutActivity.class);
                startActivity(settings);
                break;

            case "credits_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new Credits()).commit();
                break;

            case "help_translate":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://sunshineapps.com.ua/#footer")));
                break;

            case "help_development":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                break;

            case "clear":
                AlertDialog.Builder clear =
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                clear.setTitle(getResources().getString(R.string.clear_cache_title));
                clear.setMessage(Html.fromHtml(getResources().getString(R.string.clear_cache_message)));
                clear.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        FileOperation.deleteCache(getActivity().getApplicationContext());
                        getActivity().finish();
                    }
                });
                clear.setNeutralButton(R.string.cancel, null);
                clear.show();
                break;

            case "maki_source":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sfilmak/Maki-for-Facebook")));
                break;
        }
        return false;
    }



    @Override
    public void onStart() {
        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
    }

    @Override
    public void onStop() {
        super.onStop();
        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }




    private void requestLocationPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[] { locationPermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION);
        } else
            Log.e(TAG, "We already have location permission.");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_ENABLE:
                Toast.makeText(getActivity(), R.string.code_enabled, Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_CODE_DISABLE:
                Toast.makeText(getActivity(), R.string.code_disabled, Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void requestFingerPermission() {
        String locationPermission = Manifest.permission.USE_FINGERPRINT;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[]{locationPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_SFINGER);
        } else {
            Log.i("", "");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasfingerPermission() {
        String storagePermission = Manifest.permission.USE_FINGERPRINT;
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }


}



	