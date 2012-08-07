package com.twocity.bookworm;

import com.twocity.bookworm.utils.CustomHttpClient;
import com.twocity.bookworm.utils.PreferenceUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BookWorm extends DashboardActivity {
    private static final String TAG = "BookWorm";
    private Handler handler = new Handler();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //handler.post(checkNetworkRunnable);
    }
    
    final Runnable checkNetworkRunnable = new Runnable(){
        public void run(){
            boolean firstCheck = isConnected("http://www.douban.com");
            boolean secondCheck = isConnected("http://www.baidu.com");
            if(!firstCheck || !secondCheck){
                Toast.makeText(BookWorm.this, R.string.connection_wrong_msg, Toast.LENGTH_LONG).show();
            }
        }
    };
    
    private boolean isConnected(String url){
        try {
                HttpClient httpClient = CustomHttpClient.getHttpClient();
                HttpGet request = new HttpGet(url);
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setSoTimeout(params, 10000);
                request.setParams(params);
                HttpResponse response = httpClient.execute(request);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    return true;
                }else{
                    return false;
                }
            }catch (Exception e) {
              Log.d(TAG,"=== check network wrong ===");
              return false;
            }
    }
    
    public void doClick(View v){
        Intent intent;
        switch(v.getId()) {
            case R.id.home_btn_newbook:
                intent = new Intent(this,NewBookActivity.class);
                intent.putExtra(PreferenceUtils.BOOK_TYPE, true);
                this.startActivity(intent);
                break;
            case R.id.home_btn_topbook:
                intent = new Intent(this,NewBookActivity.class);
                intent.putExtra(PreferenceUtils.BOOK_TYPE, false);
                this.startActivity(intent);
                break;
            case R.id.home_btn_comment:
                intent = new Intent(this,BookCommentActivity.class);
                this.startActivity(intent);
                finish();
                //Toast.makeText(this,R.string.comment_tip,Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_btn_search:
                startActivity(new Intent(this,SearchActivity.class));
                break;
        }
    }
}