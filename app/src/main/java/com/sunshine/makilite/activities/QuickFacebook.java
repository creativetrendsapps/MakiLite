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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sunshine.makilite.R;
import com.sunshine.makilite.services.Connectivity;
import com.sunshine.makilite.utils.PreferencesUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SuppressLint("SetJavaScriptEnabled")
public class QuickFacebook extends AppCompatActivity {
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    public Context context;
    private WebView webView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private boolean fbtheme;
    private boolean blacktheme;
    private boolean dracula;
    private boolean maki;
    private boolean refreshed;
    private static SharedPreferences preferences;
    final boolean defaultfont = PreferencesUtility.getInstance(this).getFont().equals("default_font");
    boolean mediumfont = PreferencesUtility.getInstance(this).getFont().equals("medium_font");
    boolean largefont = PreferencesUtility.getInstance(this).getFont().equals("large_font");
    boolean xlfont = PreferencesUtility.getInstance(this).getFont().equals("xl_font");
    boolean xxlfont = PreferencesUtility.getInstance(this).getFont().equals("xxl_font");
    boolean smallfont = PreferencesUtility.getInstance(this).getFont().equals("small_font");
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        fbtheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("facebooktheme");
        blacktheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("darktheme");
        dracula = PreferencesUtility.getInstance(this).getFreeTheme().equals("draculatheme");
        maki = PreferencesUtility.getInstance(this).getFreeTheme().equals("materialtheme");
        final boolean makiclassic = PreferencesUtility.getInstance(this).getFreeTheme().equals("makiclassic");
        final boolean isGooglePlusTheme = PreferencesUtility.getInstance(this).getTheme().equals("gplus");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        final boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
        final boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
        final boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        final boolean defaultfeed = PreferencesUtility.getInstance(this).getFeed().equals("default_news");
        final boolean mostrecent = PreferencesUtility.getInstance(this).getFeed().equals("most_recent");
        final boolean basicmode = PreferencesUtility.getInstance(this).getFeed().equals("basic_mode");
        super.onCreate(savedInstanceState);
        context = this;

        setUpWindow();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.floating);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_float);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        webView = (WebView) findViewById(R.id.text_box);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (BB10; Touch) AppleWebKit/537.1+ (KHTML, like Gecko) Version/10.0.0.1337 Mobile Safari/537.1+");

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

        String webViewUrl = "https://m.facebook.com/home.php?sk=h_chr";

        if (defaultfeed) {
            webViewUrl = "https://m.facebook.com/home.php?sk=h_chr";
        }
        if (mostrecent) {
            webViewUrl = "https://m.facebook.com/home.php?sk=h_chr&refid=8";
        }
        if (topnews) {
            webViewUrl = "https://m.facebook.com/home.php?sk=h_nor&refid=8";
        }
        if(basicmode) {
            webViewUrl = "https://mbasic.facebook.com";

        }
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
                    Intent photoViewer = new Intent(QuickFacebook.this, PhotoViewer.class);
                    photoViewer.putExtra("url", url);
                    photoViewer.putExtra("title", view.getTitle());
                    startActivity(photoViewer);
                    return true;

                } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                        || Uri.parse(url).getHost().endsWith("m.facebook.com")
                        || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                        || Uri.parse(url).getHost().endsWith("mobile.facebook.com/messages")
                        || Uri.parse(url).getHost().endsWith("m.facebook.com/messages")
                        || Uri.parse(url).getHost().endsWith("h.facebook.com")
                        || Uri.parse(url).getHost().endsWith("l.facebook.com")
                        || Uri.parse(url).getHost().endsWith("0.facebook.com")
                        || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                        || Uri.parse(url).getHost().endsWith("fbcdn.net")
                        || Uri.parse(url).getHost().endsWith("akamaihd.net")
                        || Uri.parse(url).getHost().endsWith("fb.me")
                        || Uri.parse(url).getHost().endsWith("googleusercontent.com")) {
                    return false;


                }if (preferences.getBoolean("allow_inside", false)) {
                    if (preferences.getBoolean("play_gifs", false)) {
                        if (url.contains("gif")) {
                            if (url.contains("l.php?u=")) {

                            }
                            Intent photoViewer = new Intent(QuickFacebook.this, PhotoViewer.class);
                            photoViewer.putExtra("url", url);
                            photoViewer.putExtra("title", view.getTitle());
                            startActivity(photoViewer);
                            return true;

                        } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                                || Uri.parse(url).getHost().endsWith("m.facebook.com")
                                || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                                || Uri.parse(url).getHost().endsWith("mobile.facebook.com/messages")
                                || Uri.parse(url).getHost().endsWith("m.facebook.com/messages")
                                || Uri.parse(url).getHost().endsWith("h.facebook.com")
                                || Uri.parse(url).getHost().endsWith("l.facebook.com")
                                || Uri.parse(url).getHost().endsWith("0.facebook.com")
                                || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                                || Uri.parse(url).getHost().endsWith("fbcdn.net")
                                || Uri.parse(url).getHost().endsWith("akamaihd.net")
                                || Uri.parse(url).getHost().endsWith("fb.me")
                                || Uri.parse(url).getHost().endsWith("googleusercontent.com")) {
                            return false;

                        }
                    }
                    Intent intent = new Intent((MainActivity.getMainActivity()), MakiBrowser.class);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;

                }
                if (preferences.getBoolean("play_gifs", false)) {
                    if (url.contains("gif")) {
                        if (url.contains("l.php?u=")) {
                            return false;
                        }
                        Intent photoViewer = new Intent(QuickFacebook.this, PhotoViewer.class);
                        photoViewer.putExtra("url", url);
                        photoViewer.putExtra("title", view.getTitle());
                        startActivity(photoViewer);
                        return true;

                    } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                            || Uri.parse(url).getHost().endsWith("m.facebook.com")
                            || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                            || Uri.parse(url).getHost().endsWith("mobile.facebook.com/messages")
                            || Uri.parse(url).getHost().endsWith("m.facebook.com/messages")
                            || Uri.parse(url).getHost().endsWith("h.facebook.com")
                            || Uri.parse(url).getHost().endsWith("l.facebook.com")
                            || Uri.parse(url).getHost().endsWith("0.facebook.com")
                            || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                            || Uri.parse(url).getHost().endsWith("fbcdn.net")
                            || Uri.parse(url).getHost().endsWith("akamaihd.net")
                            || Uri.parse(url).getHost().endsWith("fb.me")
                            || Uri.parse(url).getHost().endsWith("googleusercontent.com")) {
                        return false;


                    }
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
                ApplyCustomCss();
                if (fbtheme) {
                    if (basicmode) {
                    }
                    if (defaultfeed) {
                        injectDefaultCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("facebooktheme", "facebooktheme"));
                    }
                    if (mostrecent) {
                        injectDefaultCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("facebooktheme", "facebooktheme"));
                    }
                    if (topnews) {
                        injectDefaultCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("facebooktheme", "facebooktheme"));
                    }

                }
                if (maki) {
                    if (basicmode) {
                    }
                    if (defaultfeed) {
                        injectMaterialCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("materialtheme", "materialtheme"));
                    }
                    if (mostrecent) {
                        injectMaterialCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("materialtheme", "materialtheme"));
                    }
                    if (topnews) {
                        injectMaterialCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("materialtheme", "materialtheme"));
                    }

                }
                if (makiclassic) {
                    if (basicmode) {
                    }
                    if (defaultfeed) {
                        injectClassicCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("makiclassic", "makiclassic"));
                    }
                    if (mostrecent) {
                        injectClassicCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("makiclassic", "makiclassic"));
                    }
                    if (topnews) {
                        injectClassicCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("makiclassic", "makiclassic"));
                    }

                }
                if (blacktheme) {
                    if (basicmode) {
                        injectDarkBasicCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("darktheme", "darktheme"));
                    }
                    if (defaultfeed) {
                        injectDarkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("darktheme", "darktheme"));
                    }
                    if (mostrecent) {
                        injectDarkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("darktheme", "darktheme"));
                    }
                    if (topnews) {
                        injectDarkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("darktheme", "darktheme"));
                    }
                }
                if (dracula) {
                    if (basicmode) {
                        injectDraculaBasicCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("draculatheme", "draculatheme"));
                    }
                    if (defaultfeed) {
                        injectDraculaCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("draculatheme", "draculatheme"));
                    }
                    if (mostrecent) {
                        injectDraculaCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("draculatheme", "draculatheme"));
                    }
                    if (topnews) {
                        injectDraculaCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("draculatheme", "draculatheme"));
                    }

                }
                if (isPinkTheme)
                    injectPinkCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pinktheme", "pinktheme"));

                if (isFalconTheme)
                    injectFalconCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("falcontheme", "falcontheme"));

                if (isLightBlueTheme)
                    injectLightBlueCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("lightbluetheme", "lightbluetheme"));

                if (isGooglePlusTheme)
                    injectGPlusCSS(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("lightbluetheme", "lightbluetheme"));

                if (preferences.getBoolean("no_images", false))
                    view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('.img, ._5s61, ._5sgg{ display: none; }');");

                // hide news feed (a feature requested by drjedd)
                if (preferences.getBoolean("hide_news_feed", false))
                    view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " +
                            "node.innerHTML = str; document.body.appendChild(node); } " +
                            "addStyleString('#m_newsfeed_stream{ display: none; }');");

                // hide news feed (a feature requested by drjedd)
                if (preferences.getBoolean("hide_people", false))
                    view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " +
                            "node.innerHTML = str; document.body.appendChild(node); } " +
                            "addStyleString('#_55wo{ display: none; }');");
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {

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


    public void setUpWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.4f;
        getWindow().setAttributes(params);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .7));
        } else {
            getWindow().setLayout((int) (width * .7), (int) (height * .8));
        }
        {

        }
    }

    private void injectDefaultCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("fbdefault.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectMaterialCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("makitheme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectDarkCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("black.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception b) {
            b.printStackTrace();
        }
    }

    private void injectDraculaCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("dracula.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }


    private void injectPinkCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("pink_theme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectDraculaBasicCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("draculabasic.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectDarkBasicCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("basicblack.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception b) {
            b.printStackTrace();
        }
    }

    private void injectFalconCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("falcon_theme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }


    private void injectLightBlueCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("light_blue_theme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }


    private void injectClassicCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("makiclassic.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception fb) {
            fb.printStackTrace();
        }
    }

    private void injectGPlusCSS(String mode) {
        try {
            InputStream inputStream = getAssets().open("gplus_theme.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void ApplyCustomCss() {
        String css = "";
        if (preferences.getBoolean("pref_centerTextPosts", false)) {
            css += getString(R.string.centerTextPosts);
        }
        //apply the customizations
        webView.loadUrl(getString(R.string.editCss).replace("$css", css));
    }
}
