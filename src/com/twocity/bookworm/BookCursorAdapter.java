package com.twocity.bookworm;

import com.twocity.bookworm.utils.ImageDownloader;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

 public class BookCursorAdapter extends SimpleCursorAdapter {
        //private final static String TAG = "BookCursorAdapter";
        private LayoutInflater mInflater;
        private ImageView bookImageView;
        private TextView titleTextView;
        private TextView authorTextView;
        
        private final ImageDownloader imageDownloader = new ImageDownloader();

        public BookCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            LinearLayout ll = null;
            if (view == null) {
                ll = (LinearLayout) mInflater.inflate(R.layout.bookitem,null);
            } else {
                ll = (LinearLayout)view;
            }

            int imageIndex = cursor.getColumnIndex("image_link");
            int titleIndex = cursor.getColumnIndex("title");
            int authorIndex = cursor.getColumnIndex("author");

            bookImageView = (ImageView)ll.findViewById(R.id.book_image);
            titleTextView = (TextView)ll.findViewById(R.id.book_title);
            authorTextView = (TextView)ll.findViewById(R.id.book_author);
            
            String imagelink = cursor.getString(imageIndex);
            imageDownloader.download(imagelink, (ImageView) bookImageView);

            titleTextView.setText(cursor.getString(titleIndex));
            authorTextView.setText(cursor.getString(authorIndex));
        }
        
    }