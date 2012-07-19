package com.twocity.bookworm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHandler extends SQLiteOpenHelper {
    
    private static final String TAG = "DataBaseHandler";
    private static final String DB_NAME = "bookworm_db";
    private static final int DB_VERSION = 1;
    private static final String NEWEST_BOOK_TABLENAME = "newest_book_table";
    private static final String TOP_BOOK_TABLENAME = "top_book_table";
    private static final String _ID = "_id";
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String SUMMARY = "summary";
    private static final String LINK = "link";
    private static final String IMAGE_LINK = "image_link";
    
    private static final String CREATE_NEWEST_BOOK_DB = 
         "CREATE TABLE " + NEWEST_BOOK_TABLENAME + "(" +
         _ID + " INTEGER PRIMARY KEY," +
         TITLE + " TEXT," +
         AUTHOR + " TEXT," +
         SUMMARY + " TEXT," +
         LINK + " TEXT," +
         IMAGE_LINK + " TEXT" + ")" ;
    
    private static final String CREATE_TOP_BOOK_DB = 
            "CREATE TABLE " + TOP_BOOK_TABLENAME + "(" +
             _ID + " INTEGER PRIMARY KEY," +
             TITLE + " TEXT," +
             AUTHOR + " TEXT," +
             LINK + " TEXT," +
             IMAGE_LINK + " TEXT" + ")" ;
    
    public DataBaseHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(TAG,"=== create table: "+NEWEST_BOOK_TABLENAME + "===");
        db.execSQL(CREATE_NEWEST_BOOK_DB);
        db.execSQL(CREATE_TOP_BOOK_DB);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        Log.d(TAG,"=== onUpgrage ===");
        db.execSQL("DROP TABLE IF EXISTS" + NEWEST_BOOK_TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS" + TOP_BOOK_TABLENAME);
        onCreate(db);  
    }                
    
    public void insertNewestBook(Books book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(_ID, book.getBookID());
        values.put(TITLE, book.getBookTitle());
        values.put(AUTHOR, book.getBookAuthor());
        values.put(SUMMARY, book.getBookSummary());
        values.put(LINK, book.getBookLink());
        values.put(IMAGE_LINK, book.getBookImgaeLink());
        
        Log.d(TAG,"insert book :"+book.getBookTitle());

        db.delete(NEWEST_BOOK_TABLENAME, _ID + " = ?", new String[]{ String.valueOf(book.getBookID()) });
        db.insert(NEWEST_BOOK_TABLENAME,null,values);
        db.close();
    }
    
    public void insertTopBook(Books book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(_ID, book.getBookID());
        values.put(TITLE, book.getBookTitle());
        values.put(AUTHOR, book.getBookAuthor());
        values.put(LINK, book.getBookLink());
        values.put(IMAGE_LINK, book.getBookImgaeLink());
        
        Log.d(TAG,"insert book :"+book.getBookTitle());

        db.delete(NEWEST_BOOK_TABLENAME, _ID + " = ?", new String[]{ String.valueOf(book.getBookID()) });
        db.insert(NEWEST_BOOK_TABLENAME,null,values);
        db.close();
    }
    
    public Cursor queryNewestBook(){
        Log.d(TAG,"queryNewestBook()");
        SQLiteDatabase db = this.getReadableDatabase();
        //String selection[] ={TITLE};
        Cursor c = db.query(NEWEST_BOOK_TABLENAME,null,null , null, null, null, null);
        c.moveToFirst();
        return c;
    }
    
    public Cursor queryTopBook(){
        Log.d(TAG,"queryTopBook()");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TOP_BOOK_TABLENAME,null,null , null, null, null, null);
        c.moveToFirst();
        return c;
    }
    
    
    public void updateNewestBook(ContentValues cv){
        //SQLiteDatabase db = this.getWritableDatabase();
        
    }
}