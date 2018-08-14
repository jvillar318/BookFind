package com.example.android.bookfind;

import android.graphics.Bitmap;

public class Book{
    private String mTitle;
    private String mAuthor;
    private String mPublisher;
    private String mPublishedDate;
    private int mRatingBar;
    private Bitmap mImage;
    private String mDescription;
    private String mUrl;

    public Book(String title, String author, String publisher, String publishedDate, int ratingBar, Bitmap image, String description,String url){
        mTitle = title;
        mAuthor = author;
        mPublisher = publisher;
        mPublishedDate = publishedDate;
        mRatingBar = ratingBar;
        mImage = image;
        mDescription = description;
        mUrl = url;
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

    public int getRatingBar(){
        return mRatingBar;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getDescription(){
        return mDescription;
    }
    public String getUrl(){
        return mUrl;
    }

}