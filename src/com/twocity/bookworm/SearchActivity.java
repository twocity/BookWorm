package com.twocity.bookworm;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SearchActivity extends DashboardActivity implements OnClickListener {
    private final static String TAG = "BOOKWORM";
    private final static String ApiKey = "0516527552aeabd50f42f75ff80c037f";
    private final static String Secret = "438ad54fabc47fea";
    
    
    
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
    
    private void connect2Douban() {
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                Toast.makeText(getApplicationContext(), "Now searching "+mSearchEdit.getText(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
