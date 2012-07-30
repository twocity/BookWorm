package com.twocity.bookworm;

import com.twocity.bookworm.utils.PreferenceUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class BookDetail extends DashboardActivity{
    private final static String TAG = "BookDetail";
    private WebView webview;
    private TextView titleText;
    private ProgressBar mProgressBar;
    
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.book_detail);
        
        String booklink = this.getIntent().getStringExtra(PreferenceUtils.BOOK_DETAIL_LINK);
        String booktitle = this.getIntent().getStringExtra(PreferenceUtils.BOOK_DETAIL_TITLE);
        
        mProgressBar = (ProgressBar)findViewById(R.id.bookdetail_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);
        
        titleText = (TextView)findViewById(R.id.book_detail_title);
        titleText.setText(booktitle);
        
        setWebView(booklink);
    }
    
    private void setWebView(String url){
        webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);

        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
            activity.setProgress(progress * 100);
          }
        });
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                view.loadUrl(url);  
                return true;  
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                mProgressBar.setVisibility(View.VISIBLE);
            }
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.loadUrl(url);
    }
    
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
        }
    };
}