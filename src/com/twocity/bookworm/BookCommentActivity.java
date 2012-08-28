package com.twocity.bookworm;


import com.twocity.bookworm.service.UpdateIntentService;
import com.twocity.bookworm.utils.BookJsonParser;
import com.twocity.bookworm.utils.Books;
import com.twocity.bookworm.utils.ImageDownloader;
import com.twocity.bookworm.utils.PreferenceUtils;
import com.twocity.bookworm.widget.PullToRefreshListView;
import com.twocity.bookworm.widget.PullToRefreshListView.OnRefreshListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;




public class BookCommentActivity extends DashboardActivity {
    
    private static final String TAG="BookCommentActivity";
    private PullToRefreshListView listview;
    private CommentBookAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        
        update();
        setUpListView();
    }
    
    private void setUpListView(){
        
        listview = (PullToRefreshListView)findViewById(R.id.newest_book_list);
        listview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
        listview.setOnItemClickListener(myClickListener);
        
        mAdapter = new CommentBookAdapter(this, null);
        //listview.setAdapter(mAdapter);
    }
    
    private void update(){
        Intent updateIntent = new Intent(BookCommentActivity.this,UpdateIntentService.class);
        updateIntent.setAction(PreferenceUtils.ACTION_BEST_COMMENT);
        BookCommentActivity.this.startService(updateIntent);
    }
    
    private BroadcastReceiver broadreceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PreferenceUtils.ACTION_BEST_COMMENT_COMPLETE)){
                String comments = intent.getStringExtra(PreferenceUtils.BEST_COMMENT);
                Log.d(TAG,"comment: "+comments);
                BookJsonParser.BookCommentParser(comments);
            }
        }
    };
    
    final OnItemClickListener myClickListener = new OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> a, View view, int position, long id){
            Intent i = new Intent(BookCommentActivity.this,BookDetail.class);
            Books book = (Books)listview.getAdapter().getItem(position);
            if(book != null){
                String url = book.getBookLink();
                i.putExtra(PreferenceUtils.BOOK_DETAIL_LINK, url);
                i.putExtra(PreferenceUtils.BOOK_DETAIL_TITLE, book.getBookTitle());
                startActivity(i);
                Log.d(TAG,"=== BookCommentActivity to BookDetail ===");
            }
        }
    };
    
    
    
    final private class CommentBookAdapter extends BaseAdapter{
        
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Books> mBookList = new ArrayList<Books>();
        private final ImageDownloader imageDownloader = new ImageDownloader();
        
        public CommentBookAdapter(Context context,ArrayList<Books> books){
            mContext = context;
            mBookList = books;
        }
        
        public int getCount(){
            return mBookList.size();
        }
        
        public Object getItem(int position){
            return mBookList.get(position);
        }
        
        public long getItemId(int position){
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                mInflater = LayoutInflater.from(mContext);  
                convertView = mInflater.inflate(R.layout.bookitem, null);
                
                holder.coverImage = (ImageView)convertView.findViewById(R.id.book_image);
                holder.titleText = (TextView)convertView.findViewById(R.id.book_title);
                holder.authorText = (TextView)convertView.findViewById(R.id.book_author);
      
                convertView.setTag(holder);  
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            
            
            Books item = mBookList.get(position);

            String imagelink = item.getBookImgaeLink();
            imageDownloader.download(imagelink, (ImageView) holder.coverImage);
            holder.titleText.setText(item.getBookTitle());
            holder.authorText.setText(item.getBookAuthor());
            
            return convertView;
        }
        
        private class ViewHolder{
            TextView titleText;
            TextView authorText;
            ImageView coverImage;
        }
    }
    
    
    @Override
    protected void onStart(){
         super.onStart();
         IntentFilter filter = new IntentFilter();
         filter.addAction(PreferenceUtils.ACTION_BEST_COMMENT_COMPLETE);
         registerReceiver(broadreceiver,filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(broadreceiver);
    }
    
}