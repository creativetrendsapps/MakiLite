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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunshine.makilite.R;
import com.sunshine.makilite.services.Connectivity;

@SuppressWarnings("ALL")
public class MakiBrowser extends AppCompatActivity {
    public View mCoordinatorLayoutView;

    private WebView mWebView;
    View mCustomView;
    FrameLayout customViewContainer;

    private ImageView secure;
    private android.webkit.WebChromeClient.CustomViewCallback mCustomViewCallback;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maki_browser);

        customViewContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
        secure = (ImageView) findViewById(R.id.lockButton);
        Toolbar toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }


        ImageView closeButton = (ImageView) findViewById(R.id.upButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
                finish();
            }
        });

        Uri url = getIntent().getData();

        mCoordinatorLayoutView = findViewById(R.id.coordinatorLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
                if (!Connectivity.isConnected(getApplicationContext()))
                    swipeRefreshLayout.setRefreshing(false);
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);

                        }

                    }, 3000);
                }
            }});


        mWebView = (WebView) findViewById(R.id.webview_maki);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebViewClient(new Callback());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(url.toString());
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if ((url.contains("market://")
                    || url.contains("mailto:")
                    || url.contains("play.google")
                    || url.contains("tel:")
                    || url.contains("vid:")
                    || url.contains("youtube")) == true) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            if ((url.contains("http://") || url.contains("https://"))) {
                return false;
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
            super.onPageStarted(view, url, favicon);
            swipeRefreshLayout.setRefreshing(true);
            ((TextView) findViewById(R.id.toolbarSub)).setText(url);
            if ((url.contains("https://"))){
                secure.setVisibility(View.VISIBLE);
            }else{
                secure.setVisibility(View.GONE);
            }

        }
        @SuppressLint("ResourceAsColor")
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
        }
    }




    private class WebChromeClient extends android.webkit.WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {

        }

        @Override
        public void
        onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            ((TextView) findViewById(R.id.toolbarTitle)).setText(title);
        }


        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            customViewContainer.setVisibility(View.VISIBLE);
            Toolbar toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
            toolbar.setVisibility(View.GONE);
            customViewContainer.addView(view);
            mCustomViewCallback = callback;
        }


        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;
            mCustomView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.GONE);
            Toolbar toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            customViewContainer.removeView(mCustomView);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browser_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.maki_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.share_action_subject);
                i.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                return true;

            case R.id.open_link:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse(mWebView.getUrl()));
                    startActivity(intent);
                    finish();
                } catch (ActivityNotFoundException e) {
                    Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }



    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }

}