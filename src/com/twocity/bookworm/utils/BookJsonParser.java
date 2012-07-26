package com.twocity.bookworm.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.util.ArrayList;


public class BookJsonParser{
	private final static String TAG = "BookXmlParser";
	private final static String SEARCH_PREFIX = "http://api.douban.com/book/subjects?q=%s";
	private final static String SEARCH_URL = SEARCH_PREFIX +
			"&" + RequestArgs.MAX_RESULTS +
			"&" + RequestArgs.DATA_TYPE +
			"&" + RequestArgs.API_KEY;;
	
	
	public BookJsonParser(){
	}
	
	public static String getStringfromHtml(String arg,String start_index){
	    String url = String.format(SEARCH_URL, arg) +
	                 "&" +
	                 RequestArgs.START_INDEX +
	                 start_index;
	    Log.d(TAG,"search_url: "+url);
	    
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
	
	
	public static ArrayList<Books> Parser(String json){
	    ArrayList<Books> BookList = new ArrayList<Books>();
	    
	    try{
	        JSONObject root = new JSONObject(json);
	        JSONArray entries = root.getJSONArray("entry");
	        
	        
	        for(int i=0;i<entries.length();i++){
	            Books bookitem = new Books();
	            JSONObject item = entries.getJSONObject(i);

	            if(!item.isNull("author")){
	                JSONArray authorarray = item.getJSONArray("author");
	                String author = "";
	                for(int k=0;k<authorarray.length();k++){
	                    JSONObject authorObject = authorarray.getJSONObject(k);
	                    
	                    author += authorObject.getJSONObject("name").getString("$t");
	                    if((k+1) < authorarray.length()){
	                        author += "/";
	                    }
	                }
	                bookitem.setBookAuthor(author);
	            }else{
	                //continue;
	            }
	            
	            if(!item.isNull("link")){
	                JSONArray linkArray = item.getJSONArray("link");
	                for(int j=0;j<linkArray.length();j++){
	                    JSONObject linkObject = linkArray.getJSONObject(j);
	                    if(linkObject.getString("@rel").equals("alternate")){
	                        bookitem.setBookLink(linkObject.getString("@href"));
	                    }else if(linkObject.getString("@rel").equals("image")){
	                        bookitem.setBookImageLink(linkObject.getString("@href"));
	                    }
	                }
	            }

	            if(!item.isNull("title")){
	                JSONObject titleObject = item.getJSONObject("title");
	                String title = titleObject.getString("$t");
	                bookitem.setBookTitle(title);
	            }

	            Log.d(TAG,bookitem.toString());
	            BookList.add(bookitem);

	        }
	    }catch(JSONException e){
	        e.printStackTrace();
	    }
	    return BookList;
	}
	
	static final public class RequestArgs{
		public final static String QUERY = "q";
		public final static String TAG = "tag";
		public final static String START_INDEX = "start-index=";
		public final static String MAX_RESULTS = "max-results=15";
		public final static String API_KEY = "apikey=0a34108c46d8b09d1e9c31a4d48a11d7";
		public final static String DATA_TYPE = "alt=json";
	}
}