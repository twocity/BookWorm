package com.twocity.bookworm;


import com.twocity.bookworm.service.UpdateIntentService;
import com.twocity.bookworm.utils.DataBaseHandler;
import com.twocity.bookworm.utils.PreferenceUtils;
import com.twocity.bookworm.widget.PullToRefreshListView;
import com.twocity.bookworm.widget.PullToRefreshListView.OnRefreshListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;




public class NewBookActivity extends DashboardActivity {
    
    private static final String TAG="BookWorm";
    
    private BookCursorAdapter mAdapter;
    private Cursor mBookCursor = null;
    private PullToRefreshListView listview;
    private boolean isNewestBookType = true;
    private Intent updateIntent;
    TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        
        activityTitle = (TextView)findViewById(R.id.activity_title);
        
        isNewestBookType = this.getIntent().getBooleanExtra(PreferenceUtils.BOOK_TYPE, true);
        new Thread(fetchCursorRunnable).start();
        
        updateIntent = new Intent(this,UpdateIntentService.class);
        if(isNewestBookType){
            activityTitle.setText(R.string.new_book_title);
            updateIntent.setAction(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK);
            
        }else{
            activityTitle.setText(R.string.top_ten_title);
            updateIntent.setAction(PreferenceUtils.ACTION_UPDATE_TOP_BOOK);
        }

        listview = (PullToRefreshListView)findViewById(R.id.newest_book_list);
        listview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                NewBookActivity.this.startService(updateIntent);
            }
        });
        listview.setOnItemClickListener(myClickListener);
    }
    
    private BroadcastReceiver broadreceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK_COMPLETE)){
                //Log.d(TAG,"=== newest book broadcast recerved ===");
                new Thread(fetchCursorRunnable).start();
                listview.onRefreshComplete();
            }else if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_TOP_BOOK_COMPLETE)){
            	//Log.d(TAG,"=== ACTION_UPDATE_TOP_BOOK_COMPLETE received! ===");
            	new Thread(fetchCursorRunnable).start();
            	listview.onRefreshComplete();
            }else if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_FAILED)){
                listview.onRefreshComplete();
                Toast.makeText(NewBookActivity.this,R.string.update_book_failed,Toast.LENGTH_SHORT).show();
            }
        }
    };
    
    private final Runnable fetchCursorRunnable = new Runnable(){
    	public void run(){
    		DataBaseHandler dbHandler = new DataBaseHandler(NewBookActivity.this);
//            if(mBookCursor != null){
//                mBookCursor.close();
//            }
            if(isNewestBookType){
                mBookCursor = dbHandler.queryNewestBook();
            }else{
                mBookCursor = dbHandler.queryTopBook();
            }
            mHandler.sendEmptyMessage(1);
    	}
    };
    
     final Handler mHandler = new Handler()  {
        @Override
        public void handleMessage(Message msg){
            int[] toViews = {android.R.id.text1}; 
            String[] from = {"title"};
            switch(msg.what){
                case 1:
                    Log.d(TAG,"=== update listview ===");
                    mAdapter = new BookCursorAdapter(NewBookActivity.this, 
                          R.layout.bookitem, mBookCursor,
                          from, toViews);
                    listview.setAdapter(mAdapter);
                    
                    break;
//                case 2:
//                	Log.d(TAG,"update top book list");
//                    mAdapter = new BookCursorAdapter(NewBookActivity.this, 
//                          R.layout.bookitem, mBookCursor,
//                          from, toViews);
//                    listview.setAdapter(mAdapter);
                default:
                    break;
            }
        }
    };
    
    final OnItemClickListener myClickListener = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> a, View view, int position, long id){
            Intent i = new Intent(NewBookActivity.this,BookDetail.class);
            Cursor c = (Cursor)listview.getAdapter().getItem(position);
            int titleIndex = c.getColumnIndex("title");
            int linkIndex = c.getColumnIndex("link");
            String title = c.getString(titleIndex);
            String url = c.getString(linkIndex);
            i.putExtra(PreferenceUtils.BOOK_DETAIL_LINK, url);
            i.putExtra(PreferenceUtils.BOOK_DETAIL_TITLE, title);
            startActivity(i);
        }
    };
    
    @Override
    protected void onStart(){
         super.onStart();
         IntentFilter filter = new IntentFilter();
         filter.addAction(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK_COMPLETE);
         filter.addAction(PreferenceUtils.ACTION_UPDATE_TOP_BOOK_COMPLETE);
         filter.addAction(PreferenceUtils.ACTION_UPDATE_FAILED);
         registerReceiver(broadreceiver,filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(broadreceiver);
    }
    
}