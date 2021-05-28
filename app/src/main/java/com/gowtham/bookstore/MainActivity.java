package com.gowtham.bookstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private TextView emptyStateTextView;
    private ProgressBar loaderProgressBar;
    private SearchView searchView;
    private BookAdapter adapter;
    private String searchQuery = "Learning"; // initial search
    private  ConnectivityManager connectivityManager;
    private static final String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyStateTextView = findViewById(R.id.empty_view);
        loaderProgressBar = findViewById(R.id.loading_spinner);
        searchView = findViewById(R.id.search_query);


        adapter = new BookAdapter(MainActivity.this, new ArrayList<Book>());
        ListView bookListView = findViewById(R.id.list);
        bookListView.setAdapter(adapter);

        bookListView.setEmptyView(emptyStateTextView);

        bookListView.setOnItemClickListener((parent, view, position, id) -> {
            Book currentBook = adapter.getItem(position);
            openPlayStore(currentBook.getUrl());
        });

        if (checkConnectivity()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }
        else {
            connectionError();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                adapter.clear();
                emptyStateTextView.setText("");
                loaderProgressBar.setVisibility(View.VISIBLE);

                if (checkConnectivity()) {
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                }
                else {
                    connectionError();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private Boolean checkConnectivity() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void connectionError() {
        loaderProgressBar.setVisibility(View.GONE);
        emptyStateTextView.setText(getString(R.string.no_internet_connection));
    }

    private void openPlayStore(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri webPage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(this,"Not Available",Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("filter", "paid-ebooks");
        uriBuilder.appendQueryParameter("maxResults", "40");

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> books) {
        loaderProgressBar.setVisibility(View.GONE);
        emptyStateTextView.setText(R.string.no_books);
        adapter.clear();
        if (books != null && !books.isEmpty()) {
            adapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        adapter.clear();
    }
}