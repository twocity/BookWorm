package com.twocity.bookworm.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;


public class BookXmlParser{
	private final static String TAG = "BookXmlParser";
	private final static String SEARCH_PREFIX = "http://api.douban.com/book/subjects?q=%s";
	private final static String SEARCH_URL = SEARCH_PREFIX +
			"&" + RequestArgs.MAX_RESULTS +
			"&" + RequestArgs.DATA_TYPE +
			"&" + RequestArgs.API_KEY;
	private String queryArg;
	
	
	public BookXmlParser(String arg){
		queryArg = arg;
		String url = String.format(SEARCH_URL, queryArg);
		Log.d(TAG,"url: "+url);
		Log.d(TAG,getStringfromHtml(url));
	}
	
	private String getStringfromHtml(String url){
        HttpClient httpClient = CustomHttpClient.getHttpClient();
        try {
          
          HttpGet request = new HttpGet(url);
          HttpParams params = new BasicHttpParams();
          HttpConnectionParams.setSoTimeout(params,5000);   // 5s
          request.setParams(params);
          String result = httpClient.execute(request,new BasicResponseHandler());
          
          return result;
          
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
	}
	
	static final public class RequestArgs{
		public final static String QUERY = "q";
		public final static String TAG = "tag";
		public final static String START_INDEX = "start-index=1";
		public final static String MAX_RESULTS = "max-results=20";
		public final static String API_KEY = "apikey=0a34108c46d8b09d1e9c31a4d48a11d7";
		public final static String DATA_TYPE = "alt=json";
	}
	
	
	
	
	
	
	
	
}