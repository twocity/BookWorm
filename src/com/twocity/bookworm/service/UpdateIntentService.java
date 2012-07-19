package com.twocity.bookworm.service;

import com.twocity.bookworm.utils.Books;
import com.twocity.bookworm.utils.DataBaseHandler;
import com.twocity.bookworm.utils.PreferenceUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

public class UpdateIntentService extends IntentService{
    private static final String TAG = "UpdateIntentService";
    private static final String URL_NEWEST_BOOK = "http://book.douban.com/latest";
    public UpdateIntentService(){
        super(TAG);
    }
    
    @Override
    protected void onHandleIntent(Intent intent){
        Log.d(TAG,"=== receive intent: "+intent.getAction());
        if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK)){
            Log.d(TAG,"=== start to update newest book info ===");
            updateNewestBook();
        }else if(intent.getAction().equals(PreferenceUtils.ACTION_UPDATE_TOP_BOOK)){
            updateTopBook();
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
                    Log.d(TAG,book.toString());
                    dbHandler.insertNewestBook(book);
                    i++;
                }
                Intent intent = new Intent();
                intent.setAction(PreferenceUtils.ACTION_UPDATE_NEWEST_BOOK_COMPLETE);
                sendBroadcast(intent);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void updateTopBook(){
        DataBaseHandler dbHandler = new DataBaseHandler(this);
        try {
            Document doc = Jsoup.connect(URL_NEWEST_BOOK).get();
            Element article = doc.select("ul.chart-dashed-list").first();
            Elements booklist = article.select("li.clearfix");
            
            String author = "";
            String title = "";
            String link = "";
            String imagelink = "";
            
            for(Element item:booklist){
                Log.d(TAG,item.toString());
//                Element linksinfo = item.select("p.");
//                author = item.select("p.color-gray").text();
            }
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}