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

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import com.sunshine.makilite.R;
import com.sunshine.makilite.pin.PinCompatActivity;
import com.sunshine.makilite.services.Connectivity;
import com.sunshine.makilite.utils.ImageGrabber;
import com.sunshine.makilite.utils.Sharer;
import com.sunshine.makilite.utils.SlideFinishLayout;

import net.grandcentrix.tray.TrayAppPreferences;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewer extends PinCompatActivity {
    private static final int REQUEST_STORAGE = 1;
    private static final String TAG = PhotoViewer.class.getSimpleName();
    private static String appDirectoryName;
    ImageView fullImage;
    PhotoViewAttacher Image;

    String url;
    private GlideDrawableImageViewTarget gif;
    private String mPendingImageUrlToSave;
    private static SharedPreferences preferences;
    private TrayAppPreferences trayPreferences;

    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoviewer);
        setTheme(R.style.MakiDark);
        setContentView(R.layout.photoviewer);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        url = getIntent().getStringExtra("url");
        if (url == null) {
            onBackPressed();
            onDestroy();
        }

        fullImage = (ImageView) findViewById(R.id.pictureholder);
        appDirectoryName = getString(R.string.app_name).replace(" ", " ");
        loadImage();
        ((SlideFinishLayout)findViewById(R.id.root_layout)).setFinishListener(new SlideFinishLayout.onSlideFinishListener() {
            @Override
            public void onSlideFinish() {
                PhotoViewer.this.finish();
                overridePendingTransition(0, 0);
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        PreferenceManager.setDefaultValues(this, R.xml.customize_preferences, true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        trayPreferences = new TrayAppPreferences(getApplicationContext());

        if (preferences.getBoolean("lock_portrait", false)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }


    private void loadImage() {
        if (url.contains("gif")) {
            gif = new GlideDrawableImageViewTarget(fullImage);
            Glide.with(this).load(url).listener(new RequestListener<String, GlideDrawable>() {
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @SuppressWarnings("ConstantConditions")
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    fullImage.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
                    findViewById(R.id.photoprogress).setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(gif);
        } else {
            Glide.with(this).load(url).listener(new RequestListener<String, GlideDrawable>() {
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    fullImage.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
                    findViewById(R.id.photoprogress).setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(fullImage);
            Image = new PhotoViewAttacher(fullImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);
        return true;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] permissions;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.photo_save:
                if (!Connectivity.isConnected(getApplicationContext())) {
                    return true;
                }
                if (!hasStoragePermission()) {
                    permissions = new String[REQUEST_STORAGE];
                    permissions[0] = "android.permission.WRITE_EXTERNAL_STORAGE";
                    Log.e(TAG, "FaceSlim Code, Mixed with Toffeed");
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
                    return true;
                } else {
                    if (Sharer.resolve(this)) {
                        try {
                            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
                            if (!imageStorageDir.exists()) {
                                imageStorageDir.mkdirs();
                            }
                            String imgExtension = ".jpg";


                            if (url.contains(".gif")) {
                                imgExtension = ".gif";

                            } else if (url.contains(".png")) {
                                imgExtension = ".png";

                            } else if (url.contains(".jpeg")) {
                                imgExtension = ".jpeg";
                            }
                            String file = "IMG_" + System.currentTimeMillis() + imgExtension;
                            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, file)
                                    .setTitle(file).setDescription(getString(R.string.save_img))
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            dm.enqueue(request);
                            Toast.makeText(this, getString(R.string.downloading_img), Toast.LENGTH_SHORT).show();
                        } catch (IllegalStateException ex) {
                            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(this, getString(R.string.error_general), Toast.LENGTH_SHORT).show();

                        }

                        return true;
                    }

                }
                break;

            case R.id.share_image:
                if (!Connectivity.isConnected(this)) {
                    return true;
                }
                if (hasStoragePermission()) {

                    Toast.makeText(this, getString(R.string.context_share_image_progress), Toast.LENGTH_SHORT).show();
                    new ImageGrabber(new ImageGrabber.OnImageLoaderListener() {
                        public void onError(ImageGrabber.ImageError error) {

                            Toast.makeText(PhotoViewer.this, PhotoViewer.this.getResources().getString(R.string.error_general), Toast.LENGTH_SHORT).show();
                        }

                        public void onProgressChange(int percent) {
                        }

                        public void onComplete(Bitmap result) {
                            try {
                                FileOutputStream stream = PhotoViewer.this.openFileOutput("bitmap.png", 0);
                                result.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(PhotoViewer.this.getContentResolver(), result, "Maki", null));
                                stream.close();
                                result.recycle();
                                Intent share = new Intent("android.intent.action.SEND");
                                share.setType("image/*");
                                share.putExtra("android.intent.extra.STREAM", uri);
                                PhotoViewer.this.startActivity(Intent.createChooser(share, PhotoViewer.this.getResources().getString(R.string.context_share_image)));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(PhotoViewer.this, PhotoViewer.this.getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).download(this.url, false);
                    return true;
                }
                permissions = new String[REQUEST_STORAGE];
                permissions[0] = "android.permission.WRITE_EXTERNAL_STORAGE";
                Log.e(TAG, "FaceSlim Code, Mixed with Toffeed");
                ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
                return true;



            case R.id.photo_copy:
                ClipboardManager clipboard = (ClipboardManager) PhotoViewer.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(this.getContentResolver(), "URI", Uri.parse(url));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getString(R.string.content_copy_link_done), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.share_link:
                mPendingImageUrlToSave = url;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, mPendingImageUrlToSave);
                startActivity(Intent.createChooser(share, getString(R.string.share_link)));
                return true;


            case R.id.photo_open:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    finish();
                } catch (ActivityNotFoundException e) {
                    Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                    e.printStackTrace();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }return true;
    }

    public void onBackPressed() {
        Glide.clear(fullImage);
        fullImage.setImageDrawable(null);
        if (url.contains(".gif")){
            Glide.clear(gif);
            gif.setDrawable(null);
        }
        finish();

    }
    public void onDestroy(){
        Glide.get(this).clearMemory();
        System.gc();
        super.onDestroy();
    }
    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }
}
