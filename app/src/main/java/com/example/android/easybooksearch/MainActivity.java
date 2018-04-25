package com.example.android.easybooksearch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String BOOK_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes";
    private ImageButton searchButton = null;
    private EditText searchField = null;
    private String userQuery;
    private ProgressBar progressLoader;

    /** Adapter for the list of earthquakes */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        //Load a circle progress bar as loading bar
        progressLoader = (ProgressBar) findViewById(R.id.loading_circle);
        progressLoader.setVisibility(View.INVISIBLE);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // TODO: Implementar on clicklistener na lista pra redirecionar para site do livro (ver referencia no projeto quakereport)

        searchButton = (ImageButton) findViewById(R.id.imagebutton_search);
        searchField = (EditText) findViewById(R.id.edittext_query);

        searchButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
                // hides the soft keyboard when search button is activated
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public void search(){
        if (isConnected()){
            TextView instructions = (TextView) findViewById(R.id.instructions);
            progressLoader.setVisibility(View.VISIBLE);

            userQuery = searchField.getText().toString();
            if (!userQuery.isEmpty()){
                instructions.setVisibility(TextView.INVISIBLE);
                booksAsyncTask task = new booksAsyncTask();
                task.execute(BOOK_REQUEST_URL);
            }else{
                progressLoader.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.search_field_empty), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private class booksAsyncTask extends AsyncTask<String, Void, List<Book>>{
        @Override
        protected List<Book> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            //this call the connection on server in base of preference
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            //get the URL of the google server
            Uri baseUri = Uri.parse(urls[0]);
            //this is used to build the URL with the query parameters
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", userQuery); //this adds the query in the search box
            uriBuilder.appendQueryParameter("maxResults", "30"); //this adds the max books listed
            // Perform the HTTP request for data and process the response.
            return QueryUtils.fetchData(uriBuilder.toString());
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (isConnected()) {
                // Clear the adapter of previous data
                mAdapter.clear();
                // Set empty state text to display "No books found."
                String message = getString(R.string.no_books, userQuery);
                mEmptyStateTextView.setText(message);

                progressLoader.setVisibility(View.INVISIBLE);

                // If there is a valid list of books, it add them to the adapter's data set.
                if (books != null && !books.isEmpty()) {
                    mAdapter.addAll(books);
                }
            } else {
                //emptyStateTextView.setText(R.string.no_internet);
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        }


    }


    // TODO: e depois fazer QueryUtils

    private Boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }



}
