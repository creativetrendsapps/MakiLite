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
package com.sunshine.makilite.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.sunshine.makilite.R;
import com.sunshine.makilite.activities.MainActivity;

import java.util.ArrayList;

public class BookmarksAdapter extends Adapter<BookmarksAdapter.ViewHolderBookmark> {
    @SuppressLint("StaticFieldLeak")
    private static BookmarksAdapter adapter;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Bookmarks> listBookmarks = new ArrayList<>();
    private onBookmarkSelected onBookmarkSelected;

    class ViewHolderBookmark extends ViewHolder implements View.OnClickListener {
        private Bookmarks bookmark;
        private RelativeLayout bookmarkHolder;
        private ImageView delete;
        private TextView title;

        ViewHolderBookmark(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.bookmark_title);
            delete = (ImageView) itemView.findViewById(R.id.bookmark_delete);
            bookmarkHolder = (RelativeLayout) itemView.findViewById(R.id.bookmark_holder);
        }

        void bind(Bookmarks bookmark) {
            this.bookmark = bookmark;
            title.setText(bookmark.getTitle());
            bookmarkHolder.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bookmark_holder:
                    onBookmarkSelected.loadBookmark(bookmark.getTitle(), bookmark.getUrl());
                    MainActivity.bookmarksDrawer.closeDrawers();
                    break;
                case R.id.bookmark_delete:
                    AlertDialog.Builder removeFavorite = new AlertDialog.Builder(context);
                    removeFavorite.setTitle( context.getResources().getString(R.string.removePage) + " " +
                            "" + bookmark.getTitle() + "?");
                    removeFavorite.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            listBookmarks.remove(bookmark);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context, context.getResources().getString(R.string.removed), Toast.LENGTH_LONG).show();

                        }
                    });
                    removeFavorite.setNegativeButton(R.string.cancel, null);
                    removeFavorite.show();
                    break;
                default:
                    break;
            }
        }
    }

    public interface onBookmarkSelected {
        void loadBookmark(String str, String str2);

        void onCreateHomeScreenShortcutCancel(DialogFragment dialog);

        void onCreateHomeScreenShortcutCreate(DialogFragment dialog);
    }

    public BookmarksAdapter(Context context, ArrayList<Bookmarks> listBookmarks, onBookmarkSelected onBookmarkSelected) {
        this.context = context;
        this.listBookmarks = listBookmarks;
        this.onBookmarkSelected = onBookmarkSelected;
        layoutInflater = LayoutInflater.from(context);
        adapter = this;
    }

    public void addItem(Bookmarks bookmark) {
        listBookmarks.add(bookmark);
        notifyDataSetChanged();
    }

    public ArrayList<Bookmarks> getListBookmarks() {
        return listBookmarks;

    }

    public ViewHolderBookmark onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBookmark(layoutInflater.inflate(R.layout.maki_favs, parent, false));
    }

    public void onBindViewHolder(ViewHolderBookmark holder, int position) {
        holder.bind(listBookmarks.get(position));
    }

    public int getItemCount() {
        return this.listBookmarks.size();

    }
}