/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.sunshine.makilite.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.sunshine.makilite.activities.MakiApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public final class PreferencesUtility {

    private static final String THEME_PREFERNCE="theme_preference";
    private static final String FONT_SIZE="font_pref";
    private static final String NAVIGATION="pref_navigation";
    private static final String MESSAGES="messages_pref";
    private static final String FACEBOOK_THEMES="theme_preference_fb";
    private static final String NEWS_FEED="news_feed";
    


    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }



    public String getTheme(){ return mPreferences.getString(THEME_PREFERNCE, ""); }
    
    public String getFont(){
        return mPreferences.getString(FONT_SIZE, "");
    }

    public String getNavigation(){
        return mPreferences.getString(NAVIGATION, "r");
    }

    public String getMessages(){
        return mPreferences.getString(MESSAGES, "e");
    }

    public String getFeed(){
        return mPreferences.getString(NEWS_FEED, "");
    }
    
    public String getFreeTheme(){ return mPreferences.getString(FACEBOOK_THEMES, ""); }

   public static boolean getBoolean(String key, boolean defValue){
	return PreferenceManager.getDefaultSharedPreferences(MakiApplication.getContextOfApplication()).getBoolean(key, defValue);
   }

   	   public static String getString(String key, String defValue){
	   return PreferenceManager.getDefaultSharedPreferences(MakiApplication.getContextOfApplication()).getString(key, defValue);
	   }
   	   
   	public static void putString(String key, String value) {
 	   Editor editor = PreferenceManager.getDefaultSharedPreferences(MakiApplication.getContextOfApplication()).edit();
 	   editor.putString(key, value);
 	   editor.apply();
    }
   	
   	public static void remove(String key) {
  	   Editor editor = PreferenceManager.getDefaultSharedPreferences(MakiApplication.getContextOfApplication()).edit();
  	   editor.remove(key);
  	   editor.apply();
     }
   	


        public static String getAppVersionName(Context context) {
            String res = "0.0.0.0";
            try {
                res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return res;
        }
    public static ArrayList<Bookmarks> getBookmarks() {
        String bookmarks = getString("maki_bookmarks", "[]");
        ArrayList<Bookmarks> listBookmarks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(bookmarks);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                Bookmarks bookmark = new Bookmarks();
                bookmark.setTitle(ob.getString("title"));
                bookmark.setUrl(ob.getString("url"));
                listBookmarks.add(bookmark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBookmarks;
    }

    public static void saveBookmarks(ArrayList<Bookmarks> listBookmarks) {
        JSONArray array = new JSONArray();
        Iterator it = listBookmarks.iterator();
        if (it.hasNext()) {
            do {
                Bookmarks bookmark = (Bookmarks) it.next();
                JSONObject ob = new JSONObject();
                try {
                    ob.put("title", bookmark.getTitle());
                    ob.put("url", bookmark.getUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(ob);
            } while (it.hasNext());
        }
        putString("maki_bookmarks", array.toString());
    }

    }
