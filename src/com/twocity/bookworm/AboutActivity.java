package com.twocity.bookworm;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;



public class AboutActivity extends DashboardActivity {
	private final static String TAG = "AboutActivity";
	private TextView mAboutText = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        mAboutText = (TextView)findViewById(R.id.about_activity_text);
        mAboutText.setText(Html.fromHtml(
                "<div>" +
                	"<p>" + "BookWorm信息获取自douban.com "+ "</p>" +
                	"<p>" + "源码地址:" + "</p>" +
                    "<a href=\"http://www.google.com\">https://github.com/twocity/BookWorm</a> " +
                	"<p>" + "Author:" + "</p>" +
                    "<a href=\"mailto:dvy.zhang@gmail.com\">dvy.zhang@gmail.com</a> " +
                 "</div>"));
        mAboutText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}