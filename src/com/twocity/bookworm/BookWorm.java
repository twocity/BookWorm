package com.twocity.bookworm;

import com.twocity.bookworm.utils.PreferenceUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BookWorm extends DashboardActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    
    
    
    public void doClick(View v){
        Intent intent;
        switch(v.getId()) {
            case R.id.newBook:
                intent = new Intent(this,NewBookActivity.class);
                intent.putExtra(PreferenceUtils.BOOK_TYPE, true);
                this.startActivity(intent);
                break;
            case R.id.topBook:
                intent = new Intent(this,NewBookActivity.class);
                intent.putExtra(PreferenceUtils.BOOK_TYPE, false);
                this.startActivity(intent);
                break;
        }
    }
}