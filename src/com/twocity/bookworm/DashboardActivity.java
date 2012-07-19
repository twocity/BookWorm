package com.twocity.bookworm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public abstract class DashboardActivity extends Activity {
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    protected void onResume() {
        super.onResume();
    }
    
    protected void onStart() {
        super.onStart();
    }
    
    protected void onStop() {
        super.onStop();
    }
    
    protected void onPause() {
        super.onPause();
    }
    
    protected void onRestart() {
        super.onRestart();
    }
    
    public void onClickHome(View v) {
        final Intent intent = new Intent(this,BookWorm.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    public void onClickAbout(View v) {
        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
    }
    
    public void onClickSearch(View v) {
        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
    }
    
    public void onClickFeaturs(View v) {
        
    }
    
    public void setTitleFromActivityLabel(int textviewId) {
        TextView tv = (TextView) findViewById(textviewId);
        if(tv != null)
            tv.setText(getTitle());
    }
}