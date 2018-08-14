package com.example.android.bookfind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book>{
    public BookAdapter( Context context, List<Book>books) {
        super(context, 0, books);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }
        Book currentBook = getItem(position);

        TextView bookTitleView = (TextView)listItemView.findViewById(R.id.book_title);
        bookTitleView.setText(currentBook.getTitle());

        TextView authorsView = (TextView)listItemView.findViewById(R.id.author);
        authorsView.setText(currentBook.getAuthor());

        TextView publisherView = (TextView)listItemView.findViewById(R.id.publisher);
        publisherView.setText(currentBook.getPublisher());

        TextView publishedDateView = (TextView)listItemView.findViewById(R.id.publishedDate);
        publishedDateView.setText(currentBook.getPublishedDate());

        RatingBar ratingBarView = (RatingBar)listItemView.findViewById(R.id.rating);
        ratingBarView.setRating(currentBook.getRatingBar());

        ImageView thumbnailView = (ImageView)listItemView.findViewById(R.id.thumbnail);
        thumbnailView.setImageBitmap(currentBook.getImage());

        TextView descriptionView = (TextView)listItemView.findViewById(R.id.book_description);
        descriptionView.setText(currentBook.getDescription());

        return listItemView;

    }
}
