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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sunshine.makilite.R;
import com.sunshine.makilite.pin.PinCompatActivity;
import com.sunshine.makilite.services.Connectivity;


import com.sunshine.makilite.utils.DownloadManagerResolver;
import com.sunshine.makilite.utils.PreferencesUtility;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


public class TwitterActivity extends PinCompatActivity {
    private boolean refreshed;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Context context;
    private WebView webView;
    private static final String MESSENGER  ="Mozilla/5.0 (Linux; Linux x86_64; LG-H815 Build/MRA58K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/49.0.2623.105 Safari/537.36\"";
    // save images
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2562617;
    private static final int ID_CONTEXT_MENU_SHARE_IMAGE = 2562618;
    private String mPendingImageUrlToSave;
    private static String appDirectoryName;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences preferences;
    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_LOCATION = 2;
    NavigationView navigationView;
    private DrawerLayout drawerLayout;
    public Toolbar toolbar;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean isMakiTheme = PreferencesUtility.getInstance(this).getTheme().equals("maki");
        boolean isMakiDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makidark");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        final boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
        final boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
        boolean isGooglePlusTheme = PreferencesUtility.getInstance(this).getTheme().equals("gplus");
        boolean isMakiMaterialDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makimaterialdark");
        final boolean defaultfont = PreferencesUtility.getInstance(this).getFont().equals("default_font");
        boolean mediumfont = PreferencesUtility.getInstance(this).getFont().equals("medium_font");
        boolean largefont = PreferencesUtility.getInstance(this).getFont().equals("large_font");
        boolean xlfont = PreferencesUtility.getInstance(this).getFont().equals("xl_font");
        boolean xxlfont = PreferencesUtility.getInstance(this).getFont().equals("xxl_font");
        boolean smallfont = PreferencesUtility.getInstance(this).getFont().equals("small_font");

        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isMakiTheme)
                setTheme(R.style.MakiTwitter);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.twitter_activity);

        } else {

            if (isMakiDarkTheme)
                setTheme(R.style.MakiTwitterDark);

            if (isDarkTheme)
                setTheme(R.style.MakiDark);

            if (isPinkTheme)
                setTheme(R.style.MakiPink);

            if (isFalconTheme)
                setTheme(R.style.MakiFalcon);


            if (isLightBlueTheme)
                setTheme(R.style.MakiLightBlue);

            if (isGooglePlusTheme)
                setTheme(R.style.GooglePlus);

            if (isMakiMaterialDarkTheme)
                setTheme(R.style.MakiMaterialDark);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.twitter_activity);


            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean("lock_portrait", false)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        webView = (WebView) findViewById(R.id.twitter_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setUserAgentString(MESSENGER);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        String webViewUrl = "https://twitter.com";
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();

            }
        });

        if (defaultfont)
            webView.getSettings().setTextZoom(100);

        if (smallfont)
            webView.getSettings().setTextZoom(90);

        if (mediumfont)
            webView.getSettings().setTextZoom(105);

        if (largefont)
            webView.getSettings().setTextZoom(110);

        if (xlfont)
            webView.getSettings().setTextZoom(120);

        if (xxlfont)
            webView.getSettings().setTextZoom(150);
        webView.loadUrl(webViewUrl);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ((url.contains("market://") || url.contains("mailto:")
                        || url.contains("play.google") || url.contains("youtube")
                        || url.contains("tel:")
                        || url.contains("vid:")) == true) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if (url.contains("scontent") && url.contains("jpg")) {
                    if (url.contains("l.php?u=")) {
                        return false;
                    }
                    Intent photoViewer = new Intent(TwitterActivity.this, PhotoViewer.class);
                    photoViewer.putExtra("url", url);
                    photoViewer.putExtra("title", view.getTitle());
                    startActivity(photoViewer);
                    return true;

                } else if (Uri.parse(url).getHost().endsWith("twitter.com")
                        || Uri.parse(url).getHost().endsWith("m.twitter.com")
                        || Uri.parse(url).getHost().endsWith("mobile.twitter.com")
                        || Uri.parse(url).getHost().endsWith("mobile.twitter.com/messages")
                        || Uri.parse(url).getHost().endsWith("m.twitter.com/messages")
                        || Uri.parse(url).getHost().endsWith("h.twitter.com")
                        || Uri.parse(url).getHost().endsWith("l.twitter.com")
                        || Uri.parse(url).getHost().endsWith("0.twitter.com")
                        || Uri.parse(url).getHost().endsWith("zero.twitter.com")
                        || Uri.parse(url).getHost().endsWith("googleusercontent.com")) {
                    return false;


                }if (preferences.getBoolean("allow_inside", false)) {
                    Intent intent = new Intent(TwitterActivity.this, MakiBrowser.class);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                    e.printStackTrace();
                }
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                swipeRefreshLayout.setRefreshing(true);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (Connectivity.isConnected(getApplicationContext()) && !refreshed) {
                    webView.loadUrl(failingUrl);
                    refreshed = true;
                } else {
                    webView.loadUrl("file:///android_asset/error.html");
                    Snackbar snackbar = Snackbar.make(webView, R.string.descriptionNoConnection, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (webView.canGoBack()) {
                                webView.stopLoading();
                                webView.goBack();
                            }
                        }
                    });
                    snackbar.show();

                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);

                try {
                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }

            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                TwitterActivity.this.setTitle(title);
            }
            // for Lollipop, all in one
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {

                    }


                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Maki");

                if (!imageStorageDir.exists()) {

                    imageStorageDir.mkdirs();
                }

                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Maki");

                    if (!imageStorageDir.exists()) {

                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception e) {

                }

            }

            // not needed but let's make it overloaded just in case
            // openFileChooser for Android < 3.0
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions

            /** may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
             *  https://code.google.com/p/android/issues/detail?id=62220
             *  however newer versions of KitKat fixed it on some devices */
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.reload:
                        webView.reload();
                        break;

                    case R.id.close:
                        Intent facebook = new Intent(TwitterActivity.this, MainActivity.class);
                        startActivity(facebook);
                        finish();
                        break;

                    case R.id.forward:
                        webView.goForward();
                        break;

                    case R.id.jumpToTop:
                        webView.loadUrl("javascript:scroll(0,0)");
                        break;

                    case R.id.home:
                        webView.loadUrl("https://mobile.twitter.com/home");
                        break;

                    case R.id.notifications:
                        webView.loadUrl("https://mobile.twitter.com/notifications");
                        break;

                    case R.id.messages:
                        webView.loadUrl("https://mobile.twitter.com/messages");
                        break;

                    case R.id.search:
                        webView.loadUrl("https://mobile.twitter.com/search");
                        break;

                    case R.id.myprofile:
                        webView.loadUrl("https://mobile.twitter.com/account");
                        break;

                    case R.id.share:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, R.string.share_action_subject);
                        i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                        startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                        break;
                }
                return true;
            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {


            @Override
            public void onDrawerClosed(View drawerViewMain) {
                super.onDrawerClosed(drawerViewMain);
            }

            @Override
            public void onDrawerOpened(View drawerViewMain) {


                super.onDrawerOpened(drawerViewMain);

                super.onDrawerSlide(drawerViewMain, 0);

            }
        };


        actionBarDrawerToggle.syncState();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {

                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {

                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        WebView.HitTestResult result = webView.getHitTestResult();
        if (result != null) {
            int type = result.getType();

            if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                showLongPressedImageMenu(menu, result.getExtra());
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_CONTEXT_MENU_SAVE_IMAGE:
                /** In order to save anything we need storage permission.
                 *  onRequestPermissionsResult will save an image.
                 */
                requestStoragePermission();
                break;
            case ID_CONTEXT_MENU_SHARE_IMAGE:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, mPendingImageUrlToSave);
                startActivity(Intent.createChooser(share, getString(R.string.share_link)));
                break;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Storage permission granted");
                    // new image is about to be saved
                    if (mPendingImageUrlToSave != null)
                        saveImageToDisk(mPendingImageUrlToSave);
                } else {
                    Log.e(TAG, "Storage permission denied");
                    Toast.makeText(getApplicationContext(), getString(R.string.no_storage_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Location permission granted");
                    webView.reload();
                } else {
                    Log.e(TAG, "Location permission denied");
                    Toast.makeText(getApplicationContext(), getString(R.string.no_location_permission), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // request storage permission
    private void requestStoragePermission() {
        String[] permissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (!hasStoragePermission()) {
            Log.e(TAG, "No storage permission at the moment. Requesting...");
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            Log.e(TAG, "We already have storage permission. Yay!");
            // new image is about to be saved
            if (mPendingImageUrlToSave != null)
                saveImageToDisk(mPendingImageUrlToSave);
        }
    }
    // check is storage permission granted
    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void showLongPressedImageMenu(ContextMenu menu, String imageUrl) {
        mPendingImageUrlToSave = imageUrl;
        menu.add(0, ID_CONTEXT_MENU_SAVE_IMAGE, 0, getString(R.string.save_img));
        menu.add(0, ID_CONTEXT_MENU_SHARE_IMAGE, 1, getString(R.string.share_link));
    }

    private void saveImageToDisk(String imageUrl) {
        if (!DownloadManagerResolver.resolve(this)) {
            mPendingImageUrlToSave = null;
            return;
        }

        try {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);

            if (!imageStorageDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
            }

            // default image extension
            String imgExtension = ".jpg";

            if (imageUrl.contains(".gif"))
                imgExtension = ".gif";
            else if (imageUrl.contains(".png"))
                imgExtension = ".png";

            String date = DateFormat.getDateTimeInstance().format(new Date());
            String file = "faceslim-saved-image-" + date.replace(" ", "").replace(":", "").replace(".", "") + imgExtension;

            DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(imageUrl);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, file)
                    .setTitle(file).setDescription(getString(R.string.save_img))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            dm.enqueue(request);

            Toast.makeText(this, getString(R.string.downloading_img), Toast.LENGTH_LONG).show();
        } catch (IllegalStateException ex) {
            Toast.makeText(this, getString(R.string.cannot_access_storage), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            // just in case, it should never be called anyway
            Toast.makeText(this, getString(R.string.file_cannot_be_saved), Toast.LENGTH_LONG).show();
        } finally {
            mPendingImageUrlToSave = null;
        }
    }
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }


}
