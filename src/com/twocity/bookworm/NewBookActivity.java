package com.twocity.bookworm;


import com.twocity.bookworm.service.UpdateIntentService;
import com.twocity.bookworm.utils.DataBaseHandler;
import com.twocity.bookworm.utils.PreferenceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;




public class NewBookActivity extends DashboardActivity 
        /*implements LoaderManager.LoaderCallbacks<Cursor>*/{
    
    private static final String TAG="BookWorm";
    
    private BookCursorAdapter mAdapter;
    private Cursor mBookCursor = null;
    private ListView listview;
    private boolean isNewestBookType = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        
        isNewestBookType = this.getIntent().getBooleanExtra(PreferenceUtils.BOOK_TYPE, true);
        //new Thread(fetchCursorRunnable).start();
        
        Intent intent = new Intent(this,UpdateIntentService.class);
        if(isNewestBookType){
            intent.setAction(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK);
            
        }else{
            intent.setAction(PreferenceUtils.ACTION_UPDATE_TOP_BOOK);
        }
        this.startService(intent);

        listview = (ListView)findViewById(R.id.newest_book_list);
        
    }
    
    private BroadcastReceiver broadreceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK_COMPLETE)){
                Log.d(TAG,"=== newest book broadcast recerved ===");
                new Thread(fetchCursorRunnable).start();
            }else if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_TOP_BOOK_COMPLETE)){
            	Log.d(TAG,"=== ACTION_UPDATE_TOP_BOOK_COMPLETE received! ===");
            	new Thread(fetchCursorRunnable).start();
            }
        }
    };
    
    final Runnable fetchCursorRunnable = new Runnable(){
    	public void run(){
    		DataBaseHandler dbHandler = new DataBaseHandler(NewBookActivity.this);
            if(mBookCursor != null){
                mBookCursor.close();
            }
    		if(isNewestBookType){
                mBookCursor = dbHandler.queryNewestBook();
                mHandler.sendEmptyMessage(1);
    		}else{
    			mBookCursor = dbHandler.queryTopBook();
    			mHandler.sendEmptyMessage(1);
    		}
    		
    	}
    };
    
     final Handler mHandler = new Handler()  {
        @Override
        public void handleMessage(Message msg){
            int[] toViews = {android.R.id.text1}; 
            String[] from = {"title"};
            switch(msg.what){
                case 1:
                    Log.d(TAG,"update newest book list");
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
    
    
    @Override
    protected void onStart(){
         super.onStart();
         IntentFilter filter = new IntentFilter();
         filter.addAction(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK_COMPLETE);
         filter.addAction(PreferenceUtils.ACTION_UPDATE_TOP_BOOK_COMPLETE);
         registerReceiver(broadreceiver,filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(broadreceiver);
    }
    
}