package com.twocity.bookworm.utils;

public class Books{
    private int _id;
    private String book_title;
    private String book_author;
    private String book_summary;
    private String book_link;
    private String book_image_link;
    
    public Books(){
        
    }
    
    public Books(int id,String title,String author,String summary,String link,String image_link){
        this._id = id;
        this.book_title = title;
        this.book_author = author;
        this.book_summary = summary;
        this.book_link = link;
        this.book_image_link = image_link;
    }
    
    public int getBookID(){
        return this._id;
    }
    
    public String getBookTitle(){
        return this.book_title;
    }
    
    public String getBookAuthor(){
        return this.book_author;
    }
    
    public String getBookSummary(){
        return this.book_summary;
    }
    
    public String getBookLink(){
        return this.book_link;
    }
    
    public String getBookImgaeLink(){
        return this.book_image_link;
    }
    
    public boolean isEmpty(){
    	if(book_title.equals("") || book_author.equals("")
    			|| book_link.equals("") || book_image_link.equals("")){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public String toString(){
        return "id: " + this._id +
                "\ntitle: " + this.book_title +
                "\nauthor: " + this.book_author +
                "\nsummary: " + this.book_summary +
                "\nlink: " + this.book_link +
                "\nimage: " + this.book_image_link;
    }
}