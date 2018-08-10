package com.example.android.bookfind;

public class Book {
    private String mTitle;
    private String mAuthor;
    private String mPublisher;
    private String mPublishedDate;

    public Book(String title, String author, String publisher, String publishedDate){
        mTitle = title;
        mAuthor = author;
        mPublisher = publisher;
        mPublishedDate = publishedDate;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getPublishedDate(){
        return mPublishedDate;
    }
}