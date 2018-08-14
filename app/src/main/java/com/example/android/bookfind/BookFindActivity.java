package com.example.android.bookfind;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookFindActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>{
    private TextView empty_text;
    private BookAdapter bookAdapter;
    private ListView listView;
    private EditText search_text;
    private ProgressBar progressBar;


    private static final String BOOKS_LIST_URL = "https://www.googleapis.com/books/v1/volumes?minResults=10&maxResults=20&q=";
    private String searchUrl;

    private static final String LOG_TAG = BookFindActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    private boolean isConnect;

    List<Book>books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_find);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        isConnect = networkInfo != null && networkInfo.isConnected();

        listView = findViewById(R.id.books_list);

        books = new ArrayList<>();
        bookAdapter = new BookAdapter(this, books);

        listView.setAdapter(bookAdapter);
        empty_text = findViewById(R.id.empty_text);
        listView.setEmptyView(empty_text);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);


        ImageButton search = findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect){
                    bookAdapter.clear();

                    search_text = findViewById(R.id.book_search);

                    String query = search_text.getText().toString().replace(" ", "+");

                    searchUrl = BOOKS_LIST_URL + query;

                    new getAllData().execute(searchUrl);

                    progressBar.setVisibility(View.VISIBLE);
                    empty_text.setVisibility(View.GONE);
                    isConnect = true;
                }else {
                    empty_text.setText(R.string.no_internet);
                    empty_text.setVisibility(View.VISIBLE);
                    isConnect = false;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = bookAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });

    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, BOOKS_LIST_URL);
    }

    @Override
    public void onLoadFinished( Loader<List<Book>> loader, List<Book> books) {
        View progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        empty_text.setText(R.string.no_books);
        bookAdapter.clear();
        if (books != null && books.isEmpty()){
            bookAdapter.addAll(books);
        }
    }
    @Override
    public void onLoaderReset( Loader<List<Book>> loader) {
        bookAdapter.clear();
    }

    private class getAllData extends AsyncTask<String, Void, List<Book>>{
        @Override
        protected List<Book> doInBackground(String... urls) {
            if (urls[0] == null) {
                return null;
            }
            List<Book>books = null;
            try {
                books = QueryUtils.fetchDataFromInternet(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            bookAdapter.clear();

            progressBar.setVisibility(View.GONE);

            if (listView == null || bookAdapter.isEmpty()){
                empty_text.setVisibility(View.VISIBLE);
                empty_text.setText(R.string.no_books);
            }else {
                empty_text.setVisibility(View.GONE);
            }
            if (books != null && !books.isEmpty()){
                bookAdapter.addAll(books);
            }
        }
    }
}