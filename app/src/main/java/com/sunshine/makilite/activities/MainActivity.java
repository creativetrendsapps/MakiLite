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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.github.clans.fab.*;
import com.github.clans.fab.FloatingActionButton;
import com.sunshine.makilite.pin.PinCompatActivity;
import com.sunshine.makilite.pin.managers.AppLock;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.sunshine.makilite.R;
import com.sunshine.makilite.notifications.MakiNotifications;
import com.sunshine.makilite.notifications.MakiReceiver;
import com.sunshine.makilite.rate.FiveStarsDialog;
import com.sunshine.makilite.rate.NegativeReviewListener;
import com.sunshine.makilite.rate.ReviewListener;
import com.sunshine.makilite.services.Connectivity;
import com.sunshine.makilite.ui.MakiHelpers;
import com.sunshine.makilite.ui.MakiInterfaces;
import com.sunshine.makilite.utils.Bookmarks;
import com.sunshine.makilite.utils.BookmarksAdapter;
import com.sunshine.makilite.utils.DownloadManagerResolver;
import com.sunshine.makilite.utils.Miscellany;
import com.sunshine.makilite.utils.PreferencesUtility;
import com.greysonparrelli.permiso.Permiso;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import net.grandcentrix.tray.TrayAppPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;


@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
@SuppressWarnings("ALL")
public class MainActivity extends PinCompatActivity implements View.OnLongClickListener, BottomSheetListener, NegativeReviewListener, ReviewListener, BookmarksAdapter.onBookmarkSelected {
    public static final String PREFS_JELLY_BEAN_WARNING = "BetaNotification";
    private static Activity mainActivity;
    //bookmarks
    private RelativeLayout header;
    NavigationView bookmarkFavs;
    public static DrawerLayout bookmarksDrawer;
    RecyclerView recyclerBookmarks;
    private ArrayList<Bookmarks> listBookmarks = new ArrayList<>();
    private BookmarksAdapter adapterBookmarks;
    private boolean refreshed;
    //Files
    private static final int FILECHOOSER_RESULTCODE = 1;
    public static final String PREFS_NAME = "MyPrefsFile";
    //Bottom Navigation
    private AHBottomNavigationAdapter navigationAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private boolean useMenuResource = true;
    private int[] tabColors;
    private Handler handler = new Handler();
    private AHBottomNavigation bottomNavigation;
    //Bottom Navigation Ends
    public static  FloatingActionMenu FAB;
    private FloatingActionButton DownloadFab;
    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.textFAB:
                    webView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_ENCODED + "%3Fpageload%3Dcomposer%22%7D%7D)()");
                    break;
                case R.id.photoFAB:
                    webView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_ENCODED + "%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                    break;
                case R.id.checkinFAB:
                    webView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_ENCODED + "%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
                    break;

                default:
                    break;
            }
            FAB.close(true);
        }
    };
    private final static String LOG_TAG = "Maki";
    static final List<String> FB_PERMISSIONS = Arrays.asList("public_profile", "user_friends");
    public static final String FACEBOOK = "https://m.facebook.com/";
    public static final String BASICFB = "https://mbasic.facebook.com/";
    private static final String FACEBOOK_ENCODED = "https%3A%2F%2Fm.facebook.com%2F";
    private final BadgeStyle BADGE_GRAY_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, Color.parseColor("#595c68"), Color.parseColor("#595c68"), Color.WHITE);
    int request_Code = 1;
    private static Context context = MakiApplication.getContextOfApplication();
    private Snackbar snackbar = null;
    DrawerLayout drawerLayoutFavs;
    private static SharedPreferences preferences;
    private TrayAppPreferences trayPreferences;
    private static final int REQUEST_STORAGE = 1;
    private MenuItem mNotificationButton;
    private MenuItem mMessagesButton;
    public Toolbar BAR;
    private static final String HIDE_COMPOSER_CSS = "%23mbasic_inline_feed_composer%7Bdisplay%3Anone%7D";

    public static SwipeRefreshLayout swipeRefreshLayout;
    private String UrlCleaner;
    public static AdvancedWebView webView;
    public static TabLayout allTabs;
    public static Bitmap favoriteIcon;
    private static String appDirectoryName;
    private int previousUiVisibility;
    private String mPendingImageUrlToSave = null;
    //protected final static String URL_PAGE_SHARE_LINKS = "/sharer.php?u=%s&t=%s";
    private Context mContext = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    View mCustomView;
    FrameLayout customViewContainer;
    Window window;
    private CustomViewCallback mCustomViewCallback;
    public static CoordinatorLayout mCoordinatorLayoutView;
    // user agents
    //private static final String USER_AGENT_BASIC = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-gb; " +
            //"Nexus S Build/GRI20) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    //public static final String USER_AGENT_MESSENGER = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 " +
            //"(KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    private Handler mUiHandler = new Handler();
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView userNameView;
    List<String> bookmarkUrls;
    List<String> bookmarkTitles;


    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }


    @Override
    @SuppressLint({"setJavaScriptEnabled", "CutPasteId", "ClickableViewAccessibility", "SdCardPath"})
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;

        final boolean isMakiTheme = PreferencesUtility.getInstance(this).getTheme().equals("maki");
        final boolean isMakiDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makidark");
        final boolean isMakiMaterialDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("makimaterialdark");
        final boolean isPinkTheme = PreferencesUtility.getInstance(this).getTheme().equals("pink");
        final boolean isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("darktheme");
        final boolean isFalconTheme = PreferencesUtility.getInstance(this).getTheme().equals("falcon");
        final boolean isLightBlueTheme = PreferencesUtility.getInstance(this).getTheme().equals("lightblue");
        final boolean isGooglePlusTheme = PreferencesUtility.getInstance(this).getTheme().equals("gplus");
        final boolean defaultfont = PreferencesUtility.getInstance(this).getFont().equals("default_font");
        boolean mediumfont = PreferencesUtility.getInstance(this).getFont().equals("medium_font");
        boolean largefont = PreferencesUtility.getInstance(this).getFont().equals("large_font");
        boolean xlfont = PreferencesUtility.getInstance(this).getFont().equals("xl_font");
        boolean xxlfont = PreferencesUtility.getInstance(this).getFont().equals("xxl_font");
        boolean smallfont = PreferencesUtility.getInstance(this).getFont().equals("small_font");
        final boolean defaultmessages = PreferencesUtility.getInstance(this).getMessages().equals("default_message");
        final boolean basicmessages = PreferencesUtility.getInstance(this).getMessages().equals("basic_message");
        final boolean touchmessages = PreferencesUtility.getInstance(this).getMessages().equals("touch_message");
        final boolean desktopmessages = PreferencesUtility.getInstance(this).getMessages().equals("desktop_message");
        final boolean messengerin = PreferencesUtility.getInstance(this).getMessages().equals("messenger_in");
        final boolean messengerapp = PreferencesUtility.getInstance(this).getMessages().equals("messenger_app");
        final boolean messengerlite = PreferencesUtility.getInstance(this).getMessages().equals("messenger_lite");
        final boolean disaapp = PreferencesUtility.getInstance(this).getMessages().equals("disa_app");
        final boolean classic = PreferencesUtility.getInstance(this).getNavigation().equals("classic");
        final boolean topfour = PreferencesUtility.getInstance(this).getNavigation().equals("top_four");
        final boolean bottomthree = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_three");
        final boolean bottomfour = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_four");
        final boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        final boolean defaultfeed = PreferencesUtility.getInstance(this).getFeed().equals("default_news");
        final boolean mostrecent = PreferencesUtility.getInstance(this).getFeed().equals("most_recent");
        final boolean basicmode = PreferencesUtility.getInstance(this).getFeed().equals("basic_mode");
        final boolean fbtheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("facebooktheme");
        final boolean blacktheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("darktheme");
        final boolean dracula = PreferencesUtility.getInstance(this).getFreeTheme().equals("draculatheme");
        final boolean maki = PreferencesUtility.getInstance(this).getFreeTheme().equals("materialtheme");
        final boolean makiclassic = PreferencesUtility.getInstance(this).getFreeTheme().equals("makiclassic");

        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (isMakiTheme)
                setTheme(R.style.MakiTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        } else {

            if (isDarkTheme)
                setTheme(R.style.MakiDark);

            if (isMakiMaterialDarkTheme)
                setTheme(R.style.MakiMaterialDark);

            if (isMakiDarkTheme)
                setTheme(R.style.MakiThemeDark);

            if (isPinkTheme)
                setTheme(R.style.MakiPink);

            if (isFalconTheme)
                setTheme(R.style.MakiFalcon);

            if (isLightBlueTheme)
                setTheme(R.style.MakiLightBlue);

            if (isGooglePlusTheme)
                setTheme(R.style.GooglePlus);

            super.onCreate(savedInstanceState);

            mCoordinatorLayoutView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            trayPreferences = new TrayAppPreferences(getApplicationContext());
            setContentView(R.layout.activity_main);
            //Bottom Navigation
            boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                    .getBoolean("translucentNavigation", false);
            //Bottom Navigation ends
            initUI();
            initRate();
            header = (RelativeLayout) findViewById(R.id.bookmarks_header);
            bookmarksDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            bookmarkFavs = (NavigationView) findViewById(R.id.maki_bookmarks);
            listBookmarks = PreferencesUtility.getBookmarks();
            recyclerBookmarks = (RecyclerView) findViewById(R.id.recycler_bookmarks);
            recyclerBookmarks.setLayoutManager(new LinearLayoutManager(this));
            adapterBookmarks = new BookmarksAdapter(this, listBookmarks, this);
            recyclerBookmarks.setAdapter(adapterBookmarks);

            getAllWidgets();
            bindWidgetsWithAnEventFour();
            setupTabLayoutFour();
            Permiso.getInstance().setActivity(this);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            String lockState = (String) getIntent().getSerializableExtra("state");
            if (lockState != null && lockState.equals("unlocked")) {
            } else {
                if (preferences.getBoolean("maki_locker", false)) {
                    Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);
                    intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                    startActivity(intent);
                }
            }

            if (preferences.getBoolean("quickbar_pref", false)) {
                RemoteViews remoteView = new RemoteViews(MainActivity.this.getPackageName(), R.layout.quickbar);
                NotificationManager notificationmanager = (NotificationManager) MakiApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                remoteView.setTextViewText(R.id.quick, getString(R.string.app_name));
                remoteView.setTextViewText(R.id.quick_bar, getString(R.string.quick_bar));

                Builder builder = new Builder(MakiApplication.getContextOfApplication());
                builder.setSmallIcon(R.drawable.ic_stat_f)
                        .setOngoing(true)
                        .setContent(remoteView)
                        .setPriority(Notification.PRIORITY_MIN);

                //QuickBar for Facebook
                Intent quickFacebook = new Intent(this, QuickFacebook.class);
                quickFacebook.putExtra("start_url", "https://m.facebook.com");
                quickFacebook.setAction(Intent.ACTION_VIEW);
                PendingIntent facebookIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickFacebook,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_facebook, facebookIntent);
                //QuickBar for Twitter
                Intent quickTwitter = new Intent(this, QuickTwitter.class);
                quickTwitter.putExtra("start_url", "https://m.twitter.com/");
                quickTwitter.setAction(Intent.ACTION_VIEW);
                PendingIntent twitterIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickTwitter,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_twitter, twitterIntent);

                //QuickBar for Instagram
                Intent quickInstagram = new Intent(this, QuickInstagram.class);
                quickInstagram.putExtra("start_url", "https://www.instagram.com/");
                quickInstagram.setAction(Intent.ACTION_VIEW);
                PendingIntent instagramIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickInstagram,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_instagram, instagramIntent);

                //QuickBar for VK
                Intent quickVK = new Intent(this, QuickVK.class);
                quickVK.putExtra("start_url", "https://m.vk.com/");
                quickVK.setAction(Intent.ACTION_VIEW);
                PendingIntent vkIntent = PendingIntent.getActivity(getApplicationContext(), 0, quickVK,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteView.setOnClickPendingIntent(R.id.quick_vk, vkIntent);
                notificationmanager.notify(22, builder.build());
            }

            if (preferences.getBoolean("first_run", true)) {
                Intent intro = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(intro);
                requestStoragePermission();
                preferences.edit().putBoolean("first_run", false).apply();
            }
            if (preferences.getBoolean("lock_portrait", false)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
            if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false)) {
                MakiReceiver.scheduleAlarms(getApplicationContext(), false);
            }
            if (preferences.getBoolean("show_fab", false)) {
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                FAB.setVisibility(View.VISIBLE);
            } else {
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                FAB.setVisibility(View.GONE);
            }
            if (classic) {
                allTabs = (TabLayout) findViewById(R.id.tabs);
                allTabs.setVisibility(View.GONE);
                bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
                bottomNavigation.setVisibility(View.GONE);
            }
            if (topfour) {
                allTabs = (TabLayout) findViewById(R.id.tabs);
                allTabs.setVisibility(View.VISIBLE);
                bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
                bottomNavigation.setVisibility(View.GONE);
            }
            if (bottomthree) {
                allTabs = (TabLayout) findViewById(R.id.tabs);
                allTabs.setVisibility(View.GONE);
                FAB = (FloatingActionMenu) findViewById(R.id.fab);
                bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
                bottomNavigation.setVisibility(View.VISIBLE);
            }
            if (bottomfour) {
                allTabs = (TabLayout) findViewById(R.id.tabs);
                allTabs.setVisibility(View.GONE);
                bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
                bottomNavigation.setVisibility(View.VISIBLE);
            }

            BAR = (Toolbar) findViewById(R.id.toolbar_main);
            setSupportActionBar(BAR);
            BAR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.loadUrl("javascript:scroll(0,0)");
                }
            });

            drawerLayoutFavs = (DrawerLayout) findViewById(R.id.drawer_layout);
            customViewContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
            FAB = (FloatingActionMenu) findViewById(R.id.fab);

            String webViewUrl = "https://m.facebook.com";

            if (defaultfeed) {
                webViewUrl = "https://m.facebook.com";
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

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    webView.reload();

                }
            });
            // Inflate the FAB menu
            findViewById(R.id.textFAB).setOnClickListener(mFABClickListener);
            findViewById(R.id.photoFAB).setOnClickListener(mFABClickListener);
            findViewById(R.id.checkinFAB).setOnClickListener(mFABClickListener);

            webView = (AdvancedWebView) findViewById(R.id.webView1);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            if (preferences.getBoolean("allow_location", false)) {
                webView.getSettings().setGeolocationEnabled(true);
                webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
            } else {
                webView.getSettings().setGeolocationEnabled(false);
            }
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            if (Build.VERSION.SDK_INT < 18) {
                webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024);
            }
            webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.setListener(this, new MakiListener(this, webView));
            webView.addJavascriptInterface(new MakiInterfaces(this), "android");

            //webView.getSettings().setUserAgentString(USER_AGENT_BASIC);
            //Bookmarks
            ImageView add_button = (ImageView) findViewById(R.id.add_bookmark);
            add_button.setClickable(true);
            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bookmarks bookmark = new Bookmarks();
                    bookmark.setTitle(webView.getTitle());
                    bookmark.setUrl(webView.getUrl());
                    adapterBookmarks.addItem(bookmark);
                    bookmarksDrawer.closeDrawers();
                    Toast.makeText(getBaseContext(), getString(R.string.toast_added), Toast.LENGTH_LONG).show();
                }
            });

            if (preferences.getBoolean("no_images", false))
                webView.getSettings().setLoadsImagesAutomatically(false);

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

            String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
            String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);

            if (sharedUrl != null) {
                if (!sharedUrl.equals("")) {
                    // check if the URL being shared is a proper web URL
                    if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {
                        // if it's not, let's see if it includes an URL in it (prefixed with a message)
                        int startUrlIndex = sharedUrl.indexOf("http:");
                        if (startUrlIndex > 0) {
                            sharedUrl = sharedUrl.substring(startUrlIndex);
                        }
                    }
                    // final step, set the proper Sharer...
                    webViewUrl = String.format("https://m.facebook.com/sharer.php?u=%s&t=%s", sharedUrl, sharedSubject);
                    webViewUrl = Uri.parse(webViewUrl).toString();
                }
            }
            try {

                if (getIntent().getExtras().getString("start_url") != null) {
                    String temp = getIntent().getExtras().getString("start_url");

                    if (temp.equals("https://m.facebook.com/notifications"))
                        MakiNotifications.clearNotifications();
                    if (temp.equals("https://m.facebook.com/messages/"))
                        MakiNotifications.clearNotifications();

                }
            } catch (Exception ignored) {
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
                        Intent photoViewer = new Intent(MainActivity.this, PhotoViewer.class);
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
                                Intent photoViewer = new Intent(MainActivity.this, PhotoViewer.class);
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
                            Intent photoViewer = new Intent(MainActivity.this, PhotoViewer.class);
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
                    super.onPageStarted(view, url, favicon);
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
                    try {
                        swipeRefreshLayout.setRefreshing(false);
                        listBookmarks = PreferencesUtility.getBookmarks();
                        ApplyCustomCss();
                        if (preferences.getBoolean("hide_people", false)) {
                            injectHide(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("hidepeople", "hidepeople"));
                        } else {
                            injectShow(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                        }
                        if (fbtheme) {
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

                        if (bottomthree) {
                            injectFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                        }
                        if (bottomfour) {
                            injectFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                        }
                        if (topfour) {
                            injectFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
                        }
                        if (url.contains("sharer") || url.contains("/composer/") || url.contains("throwback_share_source")) {
                            showFbBar(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("showpeople", "showpeople"));
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
                            swipeRefreshLayout.setEnabled(false);
                        } else {
                            swipeRefreshLayout.setEnabled(true);
                        }
                    } catch (NullPointerException e) {
                        Log.e("onLoadResourceError", "" + e.getMessage());
                        e.printStackTrace();
                    }

                }
            });


            webView.setWebChromeClient(new WebChromeClient() {
                @SuppressWarnings("deprecation")
                @Override
                public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                    onShowCustomView(view, callback);
                }

                @Override
                public void onShowCustomView(View view,CustomViewCallback callback) {
                    try {
                        if (mCustomView != null) {
                            callback.onCustomViewHidden();
                            return;
                        }
                        mCustomView = view;
                        customViewContainer.setVisibility(View.VISIBLE);
                        BAR.setVisibility(View.GONE);
                        customViewContainer.addView(view);
                        mCustomViewCallback = callback;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



                @Override
                public void onHideCustomView() {
                    super.onHideCustomView();
                    try{
                        if (mCustomView == null)
                            return;
                        mCustomView.setVisibility(View.GONE);
                        customViewContainer.setVisibility(View.GONE);
                        BAR.setVisibility(View.VISIBLE);
                        customViewContainer.removeView(mCustomView);
                        mCustomViewCallback.onCustomViewHidden();
                        mCustomView = null;
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }


                @Override
                public void onGeolocationPermissionsShowPrompt(String origin,
                                                               Callback callback) {

                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                    callback.invoke(origin, true, false);
                }


                @Override
                public void
                onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    try { if (title != null && title.contains("Facebook") || title.contains("1")) {
                        MainActivity.this.setTitle(R.string.app_name);
                    } else {
                        MainActivity.this.setTitle(title);


                    }
                        if (title != null && title.contains("https://www.facebook.com/dialog/return")) {
                            webView.loadUrl(FACEBOOK);
                        }

                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                }

            });
//What's new
            final SharedPreferences makimain = getSharedPreferences(PREFS_JELLY_BEAN_WARNING, 0);
            boolean whats_new = makimain.getBoolean("whats_new_9", false);
            try {
                if (!whats_new) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar.make(webView, getResources().getString(R.string.app_name) + " " + PreferencesUtility.getAppVersionName(getApplicationContext()), Snackbar.LENGTH_INDEFINITE);
                            //noinspection deprecation
                            snackbar.setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    SharedPreferences.Editor editor = makimain.edit();
                                    editor.putBoolean("whats_new_9", true);
                                    editor.apply();
                                }

                                @Override
                                public void onShown(Snackbar snackbar) {
                                    SharedPreferences.Editor editor = makimain.edit();
                                    editor.putBoolean("whats_new_9", true);
                                    editor.apply();

                                }
                            });
                            snackbar.setAction(getResources().getString(R.string.what_new), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    newDialog();
                                }
                            });
                            snackbar.show();
                        }
                    }, 6000);
                }
            } catch (Exception ignored) {

            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final boolean classic = PreferencesUtility.getInstance(this).getNavigation().equals("classic");
        final boolean topfour = PreferencesUtility.getInstance(this).getNavigation().equals("top_four");
        final boolean bottomthree = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_three");
        final boolean bottomfour = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_four");
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (classic) {
            mMessagesButton = menu.findItem(R.id.action_messages);
            ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.forum, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
            MenuItem notifications = menu.findItem(R.id.action_notifications);
            notifications.setVisible(false);
            MenuItem search = menu.findItem(R.id.search);
            search.setVisible(false);
        }
        if (topfour) {
            mMessagesButton = menu.findItem(R.id.action_messages);
            ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.forum, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
            MenuItem notifications = menu.findItem(R.id.action_notifications);
            notifications.setVisible(false);
            MenuItem search = menu.findItem(R.id.search);
            search.setVisible(true);
        }
        if (bottomthree) {
            mMessagesButton = menu.findItem(R.id.action_messages);
            ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.forum, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
            mNotificationButton = menu.findItem(R.id.action_notifications);
            ActionItemBadge.update(this, mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
            MenuItem search = menu.findItem(R.id.search);
            search.setVisible(true);
        }
        if (bottomfour) {
            mMessagesButton = menu.findItem(R.id.action_messages);
            ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.forum, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
            MenuItem notifications = menu.findItem(R.id.action_notifications);
            notifications.setVisible(false);
            MenuItem search = menu.findItem(R.id.search);
            search.setVisible(true);
        }
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final boolean defaultmessages = PreferencesUtility.getInstance(this).getMessages().equals("default_message");
        final boolean basicmessages = PreferencesUtility.getInstance(this).getMessages().equals("basic_message");
        final boolean touchmessages = PreferencesUtility.getInstance(this).getMessages().equals("touch_message");
        final boolean desktopmessages = PreferencesUtility.getInstance(this).getMessages().equals("desktop_message");
        final boolean messengerin = PreferencesUtility.getInstance(this).getMessages().equals("messenger_in");
        final boolean messengerapp = PreferencesUtility.getInstance(this).getMessages().equals("messenger_app");
        final boolean messengerlite = PreferencesUtility.getInstance(this).getMessages().equals("messenger_lite");
        final boolean disaapp = PreferencesUtility.getInstance(this).getMessages().equals("disa_app");
        final boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        final boolean defaultfeed = PreferencesUtility.getInstance(this).getFeed().equals("default_news");
        final boolean mostrecent = PreferencesUtility.getInstance(this).getFeed().equals("most_recent");
        final boolean basicmode = PreferencesUtility.getInstance(this).getFeed().equals("basic_mode");
        int id = item.getItemId();

        switch (id) {

            case R.id.menu:
                new BottomSheet.Builder(this)
                        .setSheet(R.menu.list_sheet)
                        .setTitle(R.string.bottom_sheet_menu)
                        .setListener(this)
                        .show();
                return true;

            case R.id.search:
                if (defaultfeed) {
                    webView.loadUrl("https://m.facebook.com/search/?query=");
                    return true;
                }
                if (basicmode) {
                    webView.loadUrl("https://mbasic.facebook.com/search");
                    return true;
                }
                if(topnews) {
                    webView.loadUrl("https://m.facebook.com/search/?query=");
                    return true;
                }
                if(mostrecent){
                    webView.loadUrl("https://m.facebook.com/search/?query=");
                    return true;
                }


            case R.id.action_notifications:
                if (preferences.getBoolean("power_save", false)) {
                    if (defaultfeed) {
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "notifications.php?more';}");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                    if (basicmode) {
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + BASICFB + "notifications.php?more';}");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                    if(topnews) {
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "notifications.php?more';}");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                    if(mostrecent){
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "notifications.php?more';}");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                } else {
                    if (defaultfeed) {
                        webView.loadUrl("https://m.facebook.com/notifications");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);

                        return true;
                    }
                    if (basicmode) {
                        webView.loadUrl("https://mbasic.facebook.com/notifications");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                    if(topnews) {
                        webView.loadUrl("https://m.facebook.com/notifications");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                    if(mostrecent){
                        webView.loadUrl("https://m.facebook.com/notifications");
                        MakiNotifications.clearNotifications();
                        MakiHelpers.updateNums(webView);
                        return true;
                    }
                }


            case R.id.action_messages:
                if (preferences.getBoolean("power_save", false)) {
                    if (defaultmessages) {
                        Intent messages = new Intent(MainActivity.this, StandartMessages.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (basicmessages) {
                        Intent messages = new Intent(MainActivity.this, MessagesActivity.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (messengerin) {
                        Intent messages = new Intent(MainActivity.this, Messenger.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (messengerapp) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (messengerlite) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.mlite");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (disaapp) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.disa");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (touchmessages) {
                        Intent messages = new Intent(MainActivity.this, MessagesTouchActivity.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (desktopmessages) {
                        Intent messages = new Intent(MainActivity.this, MessagesDesktopActivity.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                } else {
                    if (defaultmessages) {
                        Intent messages = new Intent(MainActivity.this, StandartMessages.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (basicmessages) {
                        Intent messages = new Intent(MainActivity.this, MessagesActivity.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (messengerin) {
                        Intent messages = new Intent(MainActivity.this, Messenger.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (messengerapp) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (messengerlite) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.mlite");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (disaapp) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.disa");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (touchmessages) {
                        Intent messages = new Intent(MainActivity.this, MessagesTouchActivity.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                    if (desktopmessages) {
                        Intent messages = new Intent(MainActivity.this, MessagesDesktopActivity.class);
                        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(messages);
                        MakiNotifications.clearMessages();
                        MakiHelpers.updateNums(webView);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(data.getData().toString()));
                startActivity(i);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String webViewUrl = getIntent().getDataString();

        String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);


        if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {

                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {

                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }

                webViewUrl = String.format("https://m.facebook.com/sharer.php?u=%s&t=%s", sharedUrl, sharedSubject);
                webViewUrl = Uri.parse(webViewUrl).toString();

            }
        }

        try {
            if (getIntent().getExtras().getString("start_url") != null)
                webViewUrl = getIntent().getExtras().getString("start_url");

            if ("https://m.facebook.com/notifications".equals(webViewUrl))
                MakiNotifications.clearNotifications();
            if ("https://m.facebook.com/messages".equals(webViewUrl))
                MakiNotifications.clearMessages();
        } catch (Exception ignored) {
        }


    }

    public void setNotificationNum(int num) {
        final boolean classic = PreferencesUtility.getInstance(this).getNavigation().equals("classic");
        final boolean topfour = PreferencesUtility.getInstance(this).getNavigation().equals("top_four");
        final boolean bottomthree = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_three");
        final boolean bottomfour = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_four");

        if (bottomfour) {
            if (num > 0) {
                AHNotification notification = new AHNotification.Builder()
                        .setText("1")
                        .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.md_red_500))
                        .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white))
                        .build();
                bottomNavigation.setNotification(notification, 2);
            }
        }

        if (bottomthree) {
            if (num > 0) {
                ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), num);
                AHNotification notification = new AHNotification.Builder()
                        .setText(null)
                        .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.md_red_500))
                        .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white))
                        .build();
                bottomNavigation.setNotification(notification, 1);
            }
        }

        if (num > 0) {

            if (classic) {
                ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), num);
            }
            if (topfour) {
                ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), num);
            }


        } else {

            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_none, null), Integer.MIN_VALUE);
            if (bottomthree) {
                AHNotification notification = new AHNotification.Builder()
                        .setText(null)
                        .build();
                bottomNavigation.setNotification(notification, 1);
            }

            if (bottomfour) {
                AHNotification notification = new AHNotification.Builder()
                        .setText(null)
                        .build();
                bottomNavigation.setNotification(notification, 2);
            }

        }

    }

    public void setMessagesNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.forum, null), num);
        } else {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.forum, null), Integer.MIN_VALUE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }
    // handling back button
    @Override
    public void onBackPressed() {
        if (inCustomView());
        else if (mCustomView == null && webView.canGoBack()) {
            webView.stopLoading();
            webView.goBack();
        } else {
            if (preferences.getBoolean("confirm_exit", false))
                showExitDialog();
            else
                super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForContextMenu(webView);
        trayPreferences.put("activity_visible", true);
        String lockState = (String) getIntent().getSerializableExtra("state");
        registerForContextMenu(webView);
        listBookmarks = PreferencesUtility.getBookmarks();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterForContextMenu(webView);
        trayPreferences.put("activity_visible", false);
        PreferencesUtility.saveBookmarks(adapterBookmarks.getListBookmarks());
    }


    @Override
    public void onDestroy() {
        Log.i("MainActivity", "Destroying...");
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
        handler.removeCallbacksAndMessages(null);
    }



    @Override
    public boolean onLongClick(View v) {
        openContextMenu(v);
        return true;
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



    private void injectFbBar(String mode) {
        try {
            InputStream inputStream = getAssets().open("fb_bar.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void showFbBar(String mode) {
        try {
            InputStream inputStream = getAssets().open("showfbar.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectComposer(String mode) {
        try {
            InputStream inputStream = getAssets().open("composer.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void injectHide(String mode) {
        try {
            InputStream inputStream = getAssets().open("hidepeople.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }


    private void injectShow(String mode) {
        try {
            InputStream inputStream = getAssets().open("showpeople.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }


    private void injectSelect(String mode) {
        try {
            InputStream inputStream = getAssets().open("selectshit.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, 2) + "');" + "parent.appendChild(style)" + "})()");
        } catch (Exception d) {
            d.printStackTrace();
        }
    }


    public static Activity getMainActivity() {
        return mainActivity;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        try{
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }catch(Exception e){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // from stackoverflow http://stackoverflow.com/q/21667002
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        previousUiVisibility = decorView.getSystemUiVisibility();
        onConfigurationChanged(getResources().getConfiguration());

    }

    public void setLoading(boolean loading) {
        // Toggle the WebView and Spinner visibility
        if (preferences.getBoolean("show_webview", false)) {
            webView.setVisibility(loading ? View.GONE : View.VISIBLE);
        } else {
            webView.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(loading);

    }

    private boolean inCustomView() {
        return (mCustomView != null);
    }

    private AlertDialog createExitDialog() {
        AppCompatTextView messageTextView = new AppCompatTextView(this);
        messageTextView.setTextSize(16f);
        messageTextView.setText(getString(R.string.really_quit_question));
        messageTextView.setPadding(50, 50, 50, 0);
        messageTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
        return new AlertDialog.Builder(this)
                .setView(messageTextView)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do here
                    }
                })
                .setCancelable(true)
                .create();
    }
    private void showExitDialog() {
        AlertDialog alertDialog = createExitDialog();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private void getAllWidgets() {
        allTabs = (TabLayout) findViewById(R.id.tabs);
    }
    private void setupTabLayoutFour() {
        allTabs.addTab(allTabs.newTab().setIcon(R.drawable.newspaper),true);
        allTabs.addTab(allTabs.newTab().setIcon(R.drawable.account));
        allTabs.addTab(allTabs.newTab().setIcon(R.drawable.earth));
        allTabs.addTab(allTabs.newTab().setIcon(R.drawable.menu));
    }



    private void bindWidgetsWithAnEventFour()
    {
        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }
        });
    }


    private void setCurrentTabFragment(int tabPosition)
    {
        final boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        final boolean defaultfeed = PreferencesUtility.getInstance(this).getFeed().equals("default_news");
        final boolean mostrecent = PreferencesUtility.getInstance(this).getFeed().equals("most_recent");
        final boolean basicmode = PreferencesUtility.getInstance(this).getFeed().equals("basic_mode");
        switch (tabPosition)
        {
            case 0:
                if (defaultfeed) {
                    webView = (AdvancedWebView) findViewById(R.id.webView1);
                    webView.loadUrl("https://m.facebook.com");
                    MakiHelpers.updateNums(webView);
                    break;
                }
                if (basicmode) {
                    webView = (AdvancedWebView) findViewById(R.id.webView1);
                    webView.loadUrl("https://mbasic.facebook.com/");
                    MakiHelpers.updateNums(webView);
                    break;
                }
                if(topnews) {
                    webView = (AdvancedWebView) findViewById(R.id.webView1);
                    webView.loadUrl("https://m.facebook.com/home.php?sk=h_nor&refid=8");
                    MakiHelpers.updateNums(webView);
                    break;
                }
                if(mostrecent){
                    webView = (AdvancedWebView) findViewById(R.id.webView1);
                    webView.loadUrl("https://m.facebook.com/home.php?sk=h_chr&refid=8");
                    MakiHelpers.updateNums(webView);
                    break;
                }
            case 1:
                if (preferences.getBoolean("power_save", false)) {

                    if (defaultfeed) {
                        webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                        MakiHelpers.updateNums(webView);
                        break;
                    }
                    if (basicmode) {
                        webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + BASICFB + "friends/center/friends/';}");
                        MakiHelpers.updateNums(webView);
                        break;
                    }
                    if(topnews) {
                        webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                        MakiHelpers.updateNums(webView);
                        break;
                    }
                    if(mostrecent){
                        webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                        MakiHelpers.updateNums(webView);
                        break;
                    }


                } else {

                    if (defaultfeed) {
                        webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                        MakiHelpers.updateNums(webView);
                        break;
                    }
                    if (basicmode) {
                        webView.loadUrl("https://mbasic.facebook.com/friends/center/friends/");
                        MakiHelpers.updateNums(webView);
                        break;
                    }
                    if(topnews) {
                        webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                        MakiHelpers.updateNums(webView);
                        break;
                    }
                    if(mostrecent){
                        webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                        MakiHelpers.updateNums(webView);
                        break;
                    }

                }

            case 2:
                if (preferences.getBoolean("power_save", false)) {
                    if (defaultfeed) {
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                        break;
                    }
                    if (basicmode) {
                        webView.loadUrl("https://mbasic.facebook.com/notifications.php");
                        break;
                    }
                    if(topnews) {
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                        break;
                    }
                    if(mostrecent){
                        webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                        break;
                    }


                } else {


                    if (defaultfeed) {
                        webView.loadUrl("https://m.facebook.com/notifications");
                        break;
                    }
                    if (basicmode) {
                        webView.loadUrl("https://mbasic.facebook.com/notifications.php");
                        break;
                    }
                    if(topnews) {
                        webView.loadUrl("https://m.facebook.com/notifications");
                        break;
                    }
                    if(mostrecent){
                        webView.loadUrl("https://m.facebook.com/notifications");
                        break;
                    }

                }

            case 3:
                if (defaultfeed) {
                    webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                    break;
                }
                if (basicmode) {
                    webView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/");
                    break;
                }
                if(topnews) {
                    webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                    break;
                }
                if(mostrecent){
                    webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                    break;
                }

            default:
                if (defaultfeed) {
                    webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                    MakiHelpers.updateNums(webView);
                    break;
                }
                if (basicmode) {
                    webView.loadUrl("https://mbasic.facebook.com/home.php';}");
                    MakiHelpers.updateNums(webView);
                    break;
                }
                if(topnews) {
                    webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                    MakiHelpers.updateNums(webView);
                    break;
                }
                if(mostrecent){
                    webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                    MakiHelpers.updateNums(webView);
                    break;
                }
        }
    }

    @Override
    public void onSheetShown(@NonNull BottomSheet bottomSheet) {
        Log.v(TAG, "onSheetShown");
    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item) {
        int menuid = item.getItemId();

        switch (menuid) {

            case R.id.favorites:
                bookmarksDrawer.openDrawer(GravityCompat.END);
                break;
            case R.id.reload:
                webView.reload();
                break;

            case R.id.share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.share_action_subject);
                i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                break;

            case R.id.getpro:
                AlertDialog.Builder proalert =
                        new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                proalert.setTitle(getResources().getString(R.string.preference_donate_category));


                proalert.setMessage(Html.fromHtml(getResources().getString(R.string.pro_hint)));
                proalert.setPositiveButton(getResources().getString(R.string.go_pros), new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.thanks),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                proalert.setNeutralButton(getResources().getString(R.string.go_pro_hint), null);

                proalert.show();

                break;
            case R.id.forward:
                webView.goForward();

            case R.id.jumpToTop:
                webView.loadUrl("javascript:scroll(0,0)");
                break;

            case R.id.twitter:
                Intent twitter = new Intent(MainActivity.this, TwitterActivity.class);
                startActivity(twitter);
                break;

            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
        }

    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int dismissEvent) {
        Log.v(TAG, "onSheetDismissed " + dismissEvent);

        switch (dismissEvent) {
            case R.id.favorites:
                bookmarksDrawer.openDrawer(GravityCompat.END);
                break;
            case R.id.forward:
                webView.goForward();

            case R.id.getpro:
                AlertDialog.Builder proalert =
                        new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                proalert.setTitle(getResources().getString(R.string.preference_donate_category));


                proalert.setMessage(Html.fromHtml(getResources().getString(R.string.pro_hint)));
                proalert.setPositiveButton(getResources().getString(R.string.go_pros), new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.get_key))));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.thanks),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                proalert.setNeutralButton(getResources().getString(R.string.go_pro_hint), null);

                proalert.show();

                break;

            case R.id.jumpToTop:
                webView.loadUrl("javascript:scroll(0,0)");
                break;


        }
    }


    // handle long clicks on links, an awesome way to avoid memory leaks
    private static class MyHandler extends Handler {

        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {

                // get url to share
                String url = (String) msg.getData().get("url");

                if (url != null) {
                    /* "clean" an url to remove Facebook tracking redirection while sharing
                    and recreate all the special characters */
                    url = Miscellany.cleanAndDecodeUrl(url);

                    Log.v("Link long clicked", url);
                    // create share intent for long clicked url
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_link)));
                }
            }
        }
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


            dm.enqueue(request);


        } catch (IllegalStateException ex) {
            Toast.makeText(this,getResources().getString(R.string.cannot_access_storage),Toast.LENGTH_LONG);
        } catch (Exception ex) {
            // just in case, it should never be called anyway
            Toast.makeText(this,getResources().getString(R.string.file_cannot_be_saved),Toast.LENGTH_LONG);
        } finally {
            mPendingImageUrlToSave = null;
        }
    }
    // check is storage permission granted
    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }



    private void ApplyCustomCss() {
        String css = "";
        if (preferences.getBoolean("pref_centerTextPosts", false)) {
            css += getString(R.string.centerTextPosts);
        }
        // Hide the status editor on the News Feed if setting is enabled
        if (preferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_EDITOR, true)) {
            css += HIDE_COMPOSER_CSS;
        }
        //apply the customizations
        webView.loadUrl(getString(R.string.editCss).replace("$css", css));
    }

    private void loadUrlFromTextBox() throws UnsupportedEncodingException {
        String unUrlCleaner = webView.toString();
        URL unformattedUrl = null;
        Uri.Builder formattedUri = new Uri.Builder();
        if (Patterns.WEB_URL.matcher(unUrlCleaner).matches()) {
            if (!unUrlCleaner.startsWith("http")) {
                unUrlCleaner = "http://" + unUrlCleaner;
            }
            try {
                unformattedUrl = new URL(unUrlCleaner);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            final String scheme = unformattedUrl != null ? unformattedUrl.getProtocol() : null;
            final String authority = unformattedUrl != null ? unformattedUrl.getAuthority() : null;
            final String path = unformattedUrl != null ? unformattedUrl.getPath() : null;
            final String query = unformattedUrl != null ? unformattedUrl.getQuery() : null;
            final String fragment = unformattedUrl != null ? unformattedUrl.getRef() : null;

            formattedUri.scheme(scheme).authority(authority).path(path).query(query).fragment(fragment);
            UrlCleaner = formattedUri.build().toString();
        } else {

            final String encodedUrlString = URLEncoder.encode(unUrlCleaner, "UTF-8");

        }

        webView.loadUrl(UrlCleaner);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(webView.getWindowToken(), 0);
    }
    //Bottom Navigation

    private void initUI() {
        final boolean bottomthree = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_three");
        final boolean bottomfour = PreferencesUtility.getInstance(this).getNavigation().equals("bottom_four");
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        if (bottomthree) {
            if (useMenuResource) {
                tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
                navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
                navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
            }
        }
        if (bottomfour) {
            if (useMenuResource) {
                tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
                navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_4);
                navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
            }
        }

        bottomNavigation.setTranslucentNavigationEnabled(false);
        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#616161"));
        bottomNavigation.setInactiveColor(Color.parseColor("#adadad"));

        if (bottomfour) {
            bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
                @Override
                public boolean onTabSelected(int position, boolean wasSelected) {
                    final boolean topnews = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("top_news");
                    final boolean defaultfeed = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("default_news");
                    final boolean mostrecent = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("most_recent");
                    final boolean basicmode = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("basic_mode");
                    switch (position) {
                        case 0:
                            if (defaultfeed) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://m.facebook.com");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (basicmode) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://mbasic.facebook.com/");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (topnews) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://m.facebook.com/home.php?sk=h_nor&refid=8");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (mostrecent) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://m.facebook.com/home.php?sk=h_chr&refid=8");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                        case 1:
                            if (preferences.getBoolean("power_save", false)) {

                                if (defaultfeed) {
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if (basicmode) {
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + BASICFB + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(topnews) {
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(mostrecent){
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }


                            } else {

                                if (defaultfeed) {
                                    webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if (basicmode) {
                                    webView.loadUrl("https://mbasic.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(topnews) {
                                    webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(mostrecent){
                                    webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }

                            }

                        case 2:
                            if (preferences.getBoolean("power_save", false)) {
                                if (defaultfeed) {
                                    webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                    break;
                                }
                                if (basicmode) {
                                    webView.loadUrl("https://mbasic.facebook.com/notifications.php");
                                    break;
                                }
                                if (topnews) {
                                    webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                    break;
                                }
                                if (mostrecent) {
                                    webView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                    break;
                                }


                            } else {


                                if (defaultfeed) {
                                    webView.loadUrl("https://m.facebook.com/notifications");
                                    break;
                                }
                                if (basicmode) {
                                    webView.loadUrl("https://mbasic.facebook.com/notifications.php");
                                    break;
                                }
                                if (topnews) {
                                    webView.loadUrl("https://m.facebook.com/notifications");
                                    break;
                                }
                                if (mostrecent) {
                                    webView.loadUrl("https://m.facebook.com/notifications");
                                    break;
                                }

                            }

                        case 3:
                            if (defaultfeed) {
                                webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                break;
                            }
                            if (basicmode) {
                                webView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/");
                                break;
                            }
                            if (topnews) {
                                webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                break;
                            }
                            if (mostrecent) {
                                webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                break;
                            }

                        default:
                            if (defaultfeed) {
                                webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (basicmode) {
                                webView.loadUrl("https://mbasic.facebook.com/home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (topnews) {
                                webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (mostrecent) {
                                webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                    }

                    return true;
                }
            });
        }




        if (bottomthree) {
            bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
                @Override
                public boolean onTabSelected(int position, boolean wasSelected) {
                    final boolean topnews = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("top_news");
                    final boolean defaultfeed = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("default_news");
                    final boolean mostrecent = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("most_recent");
                    final boolean basicmode = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("basic_mode");
                    switch (position) {
                        case 0:
                            if (defaultfeed) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://m.facebook.com/");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (basicmode) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://mbasic.facebook.com/");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if(topnews) {
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://m.facebook.com/home.php?sk=h_nor&refid=8");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if(mostrecent){
                                webView = (AdvancedWebView) findViewById(R.id.webView1);
                                webView.loadUrl("https://m.facebook.com/home.php?sk=h_chr&refid=8");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                        case 1:
                            if (preferences.getBoolean("power_save", false)) {

                                if (defaultfeed) {
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if (basicmode) {
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + BASICFB + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(topnews) {
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(mostrecent){
                                    webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "friends/center/friends/';}");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }


                            } else {

                                if (defaultfeed) {
                                    webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if (basicmode) {
                                    webView.loadUrl("https://mbasic.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(topnews) {
                                    webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }
                                if(mostrecent){
                                    webView.loadUrl("https://m.facebook.com/friends/center/friends/");
                                    MakiHelpers.updateNums(webView);
                                    break;
                                }

                            }


                        case 2:
                            if (defaultfeed) {
                                webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                break;
                            }
                            if (basicmode) {
                                webView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/");
                                break;
                            }
                            if (topnews) {
                                webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                break;
                            }
                            if (mostrecent) {
                                webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                break;
                            }

                        default:
                            if (defaultfeed) {
                                webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (basicmode) {
                                webView.loadUrl("https://mbasic.facebook.com/home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (topnews) {
                                webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                            if (mostrecent) {
                                webView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "home.php';}");
                                MakiHelpers.updateNums(webView);
                                break;
                            }
                    }

                    return true;
                }
            });
        }


        if (bottomthree) {
            bottomNavigation.setColored(false);

        }
        if (bottomfour) {
            bottomNavigation.setColored(false);
            bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        }


    }

    public int getBottomNavigationNbItems() {
        return bottomNavigation.getItemsCount();
    }

    //Bottom navigation ends

    private void initRate() {
        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this,"sunshineappsst@gmail.com");
        fiveStarsDialog.setRateText(getResources().getString(R.string.rate_more))
                .setTitle(getResources().getString(R.string.rate_hello))
                .setForceMode(true)
                .setUpperBound(2)
                .setNegativeReviewListener(this)
                .setReviewListener(this)
                .showAfter(4);
    }
    @Override
    public void onNegativeReview(int stars) {
        Log.d(TAG, getResources().getString(R.string.negative_review) + stars);
        Toast.makeText(this,getResources().getString(R.string.contact_us),Toast.LENGTH_LONG);
    }

    @Override
    public void onReview(int stars) {
        Log.d(TAG, getResources().getString(R.string.review) + stars);
    }
    @Override
    public void loadBookmark(String title, String url) {
        loadPage(url);
    }
    public void loadPage(String htmlLink) {
        webView.loadUrl(htmlLink);

    }
    @Override
    public void onCreateHomeScreenShortcutCancel(DialogFragment dialog) {

    }

    @Override
    public void onCreateHomeScreenShortcutCreate(DialogFragment dialog) {

        Intent bookmarkShortcut = new Intent();
        bookmarkShortcut.setAction(Intent.ACTION_VIEW);
        bookmarkShortcut.setData(Uri.parse(UrlCleaner));
        Intent shortcutMaker = new Intent();
        shortcutMaker.putExtra("duplicate", false);
        shortcutMaker.putExtra("android.intent.extra.shortcut.INTENT", bookmarkShortcut);
        shortcutMaker.putExtra("android.intent.extra.shortcut.ICON", favoriteIcon);
        shortcutMaker.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(shortcutMaker);
    }
    private void newDialog() {
        try {
            AlertDialog.Builder whats_new = new AlertDialog.Builder(MainActivity.this);
            whats_new.setTitle(getResources().getString(R.string.what_new));

            //noinspection deprecation
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
        } catch (Exception ignored) {


        }
    }
}