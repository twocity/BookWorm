package com.twocity.bookworm;

import com.twocity.bookworm.service.UpdateIntentService;
import com.twocity.bookworm.utils.BookJsonParser;
import com.twocity.bookworm.utils.PreferenceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SearchActivity extends DashboardActivity implements OnClickListener {
    private final static String TAG = "BookWorm";
    
    
    private EditText mSearchEdit = null;
    private ImageButton mSearchButton = null;
    
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.activity_search);
        
        initViews();
    }
    
    private void initViews() {
        mSearchEdit = (EditText) findViewById(R.id.book_search);
        mSearchEdit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v,int keyCode,KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && 
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    Toast.makeText(SearchActivity.this, mSearchEdit.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        mSearchButton = (ImageButton) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(this);
    }
    
    private BroadcastReceiver broadreceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PreferenceUtils.ACTION_SEARCH_BOOK_COMPLETE)){
                String json = intent.getStringExtra(PreferenceUtils.SEARCH_BOOK_JSON);
                BookJsonParser.Parser(json);
            }
        }
    };
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Intent intent = new Intent(SearchActivity.this,UpdateIntentService.class);
                intent.setAction(PreferenceUtils.ACTION_SEARCH_BOOK);
                intent.putExtra(PreferenceUtils.SEARCH_ARG, mSearchEdit.getText().toString().trim());
                SearchActivity.this.startService(intent);
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void onStart(){
         super.onStart();
         IntentFilter filter = new IntentFilter();
         filter.addAction(PreferenceUtils.ACTION_SEARCH_BOOK_COMPLETE);
         registerReceiver(broadreceiver,filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(broadreceiver);
    }
}
