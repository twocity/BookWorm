package com.twocity.bookworm.service;

import com.twocity.bookworm.utils.BestBookCommentXmlParser;
import com.twocity.bookworm.utils.BestBookCommentXmlParser.BookComment;
import com.twocity.bookworm.utils.BookJsonParser;
import com.twocity.bookworm.utils.Books;
import com.twocity.bookworm.utils.CustomHttpClient;
import com.twocity.bookworm.utils.DataBaseHandler;
import com.twocity.bookworm.utils.HttpApi;
import com.twocity.bookworm.utils.PreferenceUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.InputStream;
import java.util.List;

public class UpdateIntentService extends IntentService{
    private static final String TAG = "UpdateIntentService";
    private static final String URL_NEWEST_BOOK = "http://book.douban.com/latest";
    private static final String URL_TOP_BOOK_1 = "http://book.douban.com/chart?subcat=F";
    private static final String URL_TOP_BOOK_2 = "http://book.douban.com/chart?subcat=I";
    private static final String BEST_COMMENT_RSS = "http://book.douban.com/feed/review/book";
    private int top_book_index = 0;

    public UpdateIntentService(){
        super(TAG);
    }
    
    @Override
    protected void onHandleIntent(Intent intent){
        Log.d(TAG,"=== receive intent: "+intent.getAction());
        if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK)){
            updateNewestBook();
        }else if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_TOP_BOOK)){
            updateTopBook();
        }else if(intent.getAction().equals(PreferenceUtils.ACTION_SEARCH_BOOK)){
            String search_book_args = intent.getStringExtra(PreferenceUtils.SEARCH_ARG_Q);
            String start_index = intent.getStringExtra(PreferenceUtils.SEARCH_ARG_START_INDEX);
            Log.d(TAG,"search_q: "+search_book_args);
            Log.d(TAG,"search_start-index: "+start_index);
            SearchBook(search_book_args,start_index);
        }else if(intent.getAction().equals(PreferenceUtils.ACTION_BEST_COMMENT)){
            getBestComment();
        }
    }
    
    private void updateNewestBook(){
        DataBaseHandler dbHandler = new DataBaseHandler(this);
        try {
            Document doc = Jsoup.connect(URL_NEWEST_BOOK).get();
            Element article = doc.select("div.article").first();
            Elements itemlist  = article.select("li");
            
            String title = "";
            String author="";
            String info="";
            String link = "";
            String image="";
            int i = 0;
            for(Element item:itemlist){
                title = item.select("h2").text();
                Elements infolist = item.select("p");
                if(infolist!=null && infolist.size() == 2){
                    author = infolist.first().text();
                    info = infolist.get(1).text();
                }
                link = item.select("a[href]").attr("href");
                image = item.select("img[src$=.jpg]").attr("src");
                
                if(title.equals("")||link.equals("")||image.equals("")){

                }else{
                    
                    Books book = new Books(i,title,author,info,link,image);
                    //Log.d(TAG,book.toString());
                    dbHandler.insertNewestBook(book);
                    i++;
                }
            }
            Intent intent = new Intent();
            intent.setAction(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK_COMPLETE);
            sendBroadcast(intent);
            
        } catch(Exception e){
            e.printStackTrace();
            Intent intent = new Intent();
            intent.setAction(PreferenceUtils.ACTION_UPDATE_FAILED);
            sendBroadcast(intent);
        }
    }
    
    private void updateTopBook(){
        
        try {
            ParseTopBookHtml(URL_TOP_BOOK_1,URL_TOP_BOOK_2);
            Intent intent = new Intent();
            intent.setAction(PreferenceUtils.ACTION_UPDATE_TOP_BOOK_COMPLETE);
            sendBroadcast(intent);
            
        } catch(Exception e){
            e.printStackTrace();
            Intent intent = new Intent();
            intent.setAction(PreferenceUtils.ACTION_UPDATE_FAILED);
            sendBroadcast(intent);
        }
    }

    private void ParseTopBookHtml(String... urls) throws Exception{
        DataBaseHandler dbHandler = new DataBaseHandler(this);
        for(String url:urls){
            Document doc = Jsoup.parse(getHtml(url));//.connect(url).timeout(5000).get();
            Element article = doc.select("ul.chart-dashed-list").first();
            Elements booklist = article.select("li");
            
            String author = "";
            String title = "";
            String link = "";
            String imagelink = "";
            
            for(Element item:booklist){
                title = item.select("h2>a").text();
                author = item.select("p.color-gray").text();
                link = item.select("h2>a[href]").attr("href");
                imagelink = item.select("img[src$=.jpg]").attr("src");
                Books tmp = new Books(top_book_index,title,author,"",link,imagelink);
                insert2DB(dbHandler,tmp);
                top_book_index++;

            }
        }
    }
    
    private void SearchBook(String args_q,String start_index){
        String jsonString = BookJsonParser.getStringfromHtml(args_q, start_index);
        Intent intent = new Intent();
        intent.setAction(PreferenceUtils.ACTION_SEARCH_BOOK_COMPLETE);
        intent.putExtra(PreferenceUtils.SEARCH_BOOK_JSON, jsonString);
        sendBroadcast(intent);
    }
    
    private void getBestComment(){
        try{
            InputStream inputstream = HttpApi.getStream("http://book.douban.com/feed/review/book");
            if(inputstream != null){
                Log.d(TAG,"=== start to parser ===");
                BestBookCommentXmlParser xmlparser = new BestBookCommentXmlParser();
                List<BookComment> list = xmlparser.parse(inputstream);
                for(BookComment item:list){
                    Log.d(TAG,item.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private String getHtml(String url){
    	Log.d(TAG,"url: "+url);
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
    
    private void insert2DB(DataBaseHandler handler,Books book){
    	if(!book.isEmpty()){
            //Log.d(TAG,book.toString());
    		handler.insertTopBook(book);
    	}
    }
}