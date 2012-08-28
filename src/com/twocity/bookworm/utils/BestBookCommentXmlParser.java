package com.twocity.bookworm.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class BestBookCommentXmlParser {
    private static String TAG = "BestBookCommentXmlParser";
    private static String ns = "utf-8";
    
    public List<BookComment> parse(InputStream in) throws XmlPullParserException,IOException {
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in,ns);
            //parser.nextTag();
            return parserItem(parser);
        }finally{
            in.close();
        }
    }
    
    private List<BookComment> parserItem(XmlPullParser parser) throws XmlPullParserException,IOException {
        List<BookComment> list = new ArrayList<BookComment>();
        
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            if(parser.getName().equals("item")){

                list.add(readBookComment(parser));
            }else {
                skip(parser);
            }
        }
        return list;
    }
    
    private BookComment readBookComment(XmlPullParser parser) throws XmlPullParserException,IOException {
        Log.d(TAG,"=== start read BookComment Object ===");
        String commentTitle = null;
        String commentLink = null;
        String commentDecription = null;
        String bookTile = null;
        String bookImageLink = null;
        String bookLink = null;
        parser.require(XmlPullParser.START_TAG, ns, "item");
        while(parser.getEventType() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if(name.equals("title")){
                commentTitle = readTitle(parser);
            }else if(name.equals("link")){
                commentLink = readLink(parser);
            }else if(name.equals("description")){
                commentDecription = readDecription(parser);
            }else if(name.equals("content")){
                String content = readContent(parser);
                Log.d(TAG,content);
            }else{
                skip(parser);
            }
        }
        return new BookComment(commentTitle,commentLink,commentDecription,bookTile,bookImageLink,bookLink);
    }
    
    private String readTitle(XmlPullParser parser) throws XmlPullParserException,IOException {
        Log.d(TAG," === read title ====");
        String title = "";
        parser.require(XmlPullParser.START_TAG, ns, "title");
        title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }
    
    private String readLink(XmlPullParser parser) throws XmlPullParserException,IOException {
        Log.d(TAG,"=== read link ====");
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }
    
    private String readDecription(XmlPullParser parser) throws XmlPullParserException,IOException {
        Log.d(TAG,"=== read decription ===");
        String decription = "";
        parser.require(XmlPullParser.START_TAG, ns, "decription");
        if(parser.nextToken() == XmlPullParser.CDSECT){
            decription = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "decription");
        return decription;
    }
    
    private String readContent(XmlPullParser parser) throws XmlPullParserException,IOException {
        Log.d(TAG,"=== read content ===");
        String content = "";
        parser.require(XmlPullParser.START_TAG, ns, "content");
        if(parser.nextToken() == XmlPullParser.CDSECT){
            content = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "content");
        return content;
    }
    
    private String readText(XmlPullParser parser) throws XmlPullParserException,IOException {
        Log.d(TAG,"=== read text ===");
        String result = "";
        if(parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d(TAG,"=== skip ===");
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    
    
    public static class BookComment{
        public final String commentTitle;
        public final String commentLink;
        public final String commentDecription;
        public final String bookTitle;
        public final String bookImageLink;
        public final String bookLink;
        
        private BookComment(String commenttitle,String commentlink,
                String commentd,String bt,String bimagelink,String bl){
            this.commentTitle = commenttitle;
            this.commentLink = commentlink;
            this.commentDecription = commentd;
            this.bookTitle = bt;
            this.bookImageLink = bimagelink;
            this.bookLink = bl;
        }
        
        public String toString(){
            return "commentTitle: " + commentTitle +
                   "commentLink: " + commentLink +
                   "commentDecription: " + commentDecription +
                   "bookTitle: " + bookTitle +
                   "bookImageLink: " + bookImageLink +
                   "bookLink: " + bookLink;
        }
    }
}