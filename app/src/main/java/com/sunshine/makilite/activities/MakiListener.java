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

import android.Manifest;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.github.clans.fab.FloatingActionMenu;
import com.greysonparrelli.permiso.Permiso;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sunshine.makilite.R;
import com.sunshine.makilite.ui.MakiHelpers;

import java.io.File;

import im.delight.android.webview.AdvancedWebView;


public class MakiListener implements AdvancedWebView.Listener {
    private static final int ID_SAVE_IMAGE = 0;
    private static final int ID_SHARE_IMAGE = 1;
    private static final int ID_COPY_IMAGE_LINK = 2;
    private static final int ID_SHARE_LINK = 3;
    private static final int ID_COPY_LINK = 4;
    private static final int REQUEST_STORAGE = 5;
    private static SharedPreferences preferences;
    private final AdvancedWebView webView;
    private final FloatingActionMenu FAB;
    private final AHBottomNavigation bottomNavigation;
    private final MainActivity fActivity;
    private final int mScrollThreshold;
    private final DownloadManager mDownloadManager;
    private final View mCoordinatorLayoutView;
    private static Context context;
    private static final String HIDE_ORANGE_FOCUS = "*%7B-webkit-tap-highlight-color%3Atransparent%3Boutline%3A0%7D";
    private static final String HIDE_COMPOSER_CSS = "%23mbasic_inline_feed_composer%7Bdisplay%3Anone%7D";
    public MakiListener(MainActivity activity, WebView view) {
        fActivity = activity;
        mCoordinatorLayoutView = activity.mCoordinatorLayoutView;
        webView = (AdvancedWebView) view;
        FAB = (FloatingActionMenu) activity.findViewById(R.id.fab);
        bottomNavigation = (AHBottomNavigation) activity.findViewById(R.id.bottom_navigation);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mScrollThreshold = activity.getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
        mDownloadManager = (DownloadManager) fActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        context = MakiApplication.getContextOfApplication();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        fActivity.setLoading(true);
    }

    @Override
    public void onPageFinished(String url) {
        MakiHelpers.updateNumsService(webView);
        String css = HIDE_ORANGE_FOCUS;
        if (preferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_EDITOR, true)) {
            css += HIDE_COMPOSER_CSS;
        }
        JavaScriptHelpers.paramLoader(webView, url);
        JavaScriptHelpers.loadCSS(webView, css);
        JavaScriptHelpers.updateCurrentTab(webView);
        // Stop loading
        fActivity.setLoading(false);
    }


    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        fActivity.setLoading(false);
    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    @Override
    public void onExternalPageRequest(String url) {

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setShowTitle(true);
        intentBuilder.setToolbarColor(ContextCompat.getColor(fActivity, R.color.colorPrimary));

        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, url);

        PendingIntent menuItemPendingIntent = PendingIntent.getActivity(fActivity, 0, actionIntent, 0);
        intentBuilder.setActionButton(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share), (fActivity.getString(R.string.update_share)), menuItemPendingIntent);
        intentBuilder.addMenuItem(fActivity.getString(R.string.update_share), menuItemPendingIntent);

        intentBuilder.build().launchUrl(fActivity, Uri.parse(url));
    }




    @Override
    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if (preferences.getBoolean("show_fab", false) && Math.abs(oldScrollY - scrollY) > mScrollThreshold) {
            if (scrollY > oldScrollY) {

                FAB.hideMenuButton(true);
                bottomNavigation.hideBottomNavigation(true);
            } else if (scrollY < oldScrollY) {

                FAB.showMenuButton(true);
                bottomNavigation.restoreBottomNavigation(true);
            }

        }
    }


    private Resources getResources() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu) {
        final WebView.HitTestResult result = webView.getHitTestResult();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == ID_SAVE_IMAGE) {
                    Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                        @Override
                        public void onPermissionResult(Permiso.ResultSet resultSet) {
                            if (resultSet.areAllPermissionsGranted()) {
                                // Save the image
                                Uri uri = Uri.parse(result.getExtra());
                                DownloadManager.Request request = new DownloadManager.Request(uri);

                                // Set the download directory
                                File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                if (!downloads_dir.exists()) {
                                    if (!downloads_dir.mkdirs()) {
                                        return;
                                    }
                                }
                                File destinationFile = new File(downloads_dir, uri.getLastPathSegment());
                                request.setDestinationUri(Uri.fromFile(destinationFile));

                                // Make notification stay after download
                                request.setVisibleInDownloadsUi(true);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                // Start the download
                                mDownloadManager.enqueue(request);
                            } else {
                                Snackbar.make(mCoordinatorLayoutView, R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                            // TODO Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                            callback.onRationaleProvided();
                        }
                    }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return true;
                } else if (i == ID_SHARE_IMAGE) {
                    final Uri uri = Uri.parse(result.getExtra());
                    // Share image
                    Target target = new Target() {
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                            String path = MediaStore.Images.Media.insertImage(fActivity.getContentResolver(), bitmap, uri.getLastPathSegment(), null);

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                            fActivity.startActivity(Intent.createChooser(shareIntent, fActivity.getString(R.string.context_share_image)));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }
                    };

                    Picasso.with(fActivity).load(uri).into(target);
                    Snackbar.make(mCoordinatorLayoutView, R.string.context_share_image_progress, Snackbar.LENGTH_SHORT).show();
                    return true;
                } else if (i == ID_COPY_IMAGE_LINK || i == ID_COPY_LINK) {
                    // Copy the image link to the clipboard
                    ClipboardManager clipboard = (ClipboardManager) fActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newUri(fActivity.getContentResolver(), "URI", Uri.parse(result.getExtra()));
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(mCoordinatorLayoutView, R.string.content_copy_link_done, Snackbar.LENGTH_LONG).show();
                    return true;
                } else if (i == ID_SHARE_LINK) {
                    // Share the link
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                    fActivity.startActivity(Intent.createChooser(shareIntent, fActivity.getString(R.string.context_share_link)));
                    return true;
                } else {
                    return false;
                }
            }
        };

        // Long pressed image
        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextMenu.add(0, ID_SAVE_IMAGE, 0, R.string.context_save_image).setOnMenuItemClickListener(handler);
            contextMenu.add(0, ID_SHARE_IMAGE, 0, R.string.context_share_image).setOnMenuItemClickListener(handler);
            contextMenu.add(0, ID_COPY_IMAGE_LINK, 0, R.string.context_copy_image_link).setOnMenuItemClickListener(handler);
        }

        if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextMenu.add(0, ID_SHARE_LINK, 0, R.string.context_share_link).setOnMenuItemClickListener(handler);
            contextMenu.add(0, ID_COPY_LINK, 0, R.string.context_copy_link).setOnMenuItemClickListener(handler);
        }
    }

    private void requestStorage() {
        String locationPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[] { locationPermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fActivity, permissions, REQUEST_STORAGE);
        } else{

        }

    }


}