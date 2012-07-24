package com.twocity.bookworm;

import com.twocity.bookworm.service.UpdateIntentService;
import com.twocity.bookworm.utils.CustomHttpClient;
import com.twocity.bookworm.utils.PreferenceUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class SearchActivity extends DashboardActivity implements OnClickListener {
    private final static String TAG = "BookWorm";
    private final static String search_url = "http://api.douban.com/book/subjects?q=%s";
    
    
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
                    Toast.makeText(getApplicationContext(), mSearchEdit.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        mSearchButton = (ImageButton) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(this);
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Intent intent = new Intent(SearchActivity.this,UpdateIntentService.class);
                intent.setAction(PreferenceUtils.ACTION_SEARCH_BOOK);
                intent.putExtra(PreferenceUtils.SEARCH_ARG, String.format(search_url, mSearchEdit.getText()));
                SearchActivity.this.startService(intent);
                break;
            default:
                break;
        }
    }
}
