package com.example.booklisting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = BookActivity.class.getName();

    /** URL for book data from the Google books dataset */
    private String google_books_request_api="";

    boolean start = true;

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /**Stores true if internet connectivity is present else stores false */
    boolean isConnected;

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** ListView containing the search results, the list of books */
    private ListView bookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find a reference to the searchInput TextInputEditText
        TextInputEditText searchInput = (TextInputEditText) findViewById(R.id.search_input);
        searchInput.setHint("Enter something about the book...");

        //Find a reference to the loading spinner
        View loadingIndicator = findViewById(R.id.loading_spinner);

        //Find a reference to the empty_view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        checkConnection(connMgr);

        // If there is a network connection, fetch data
        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Set an OnEditorClickListener on the searchInput so that when the user searches a book,
        // a new list is displayed.
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchInput.clearFocus();
                    // Hide the keyboard after the user enters the query
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                    // Get text from searchText field
                    String searchText = searchInput.getText().toString().replace(" ", "");

                    // Check if internet connection is present
                    if(isConnected){
                        // Set mEmptyStateTextView gone and loadingIndicator visible
                        mEmptyStateTextView.setVisibility(View.GONE);
                        loadingIndicator.setVisibility(View.VISIBLE);
                        // Set the url for the Google Books api
                        google_books_request_api =
                                "https://www.googleapis.com/books/v1/volumes?q=" + searchText +
                                        "&maxResults=10";
                        // Restart the LoaderManager
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null,
                                BookActivity.this);
                    }
                    else{
                        // Clear the adapter of previous book data
                        mAdapter.clear();
                        // Set mEmptyStateTextView visible
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                        // ...and display message: "No internet connection."
                        mEmptyStateTextView.setText(R.string.no_internet_connection);
                    }
                    handled = true;
                }
                return handled;
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String info URL into a URI object (to pass into the Intent constructor)
                Uri infoLink = Uri.parse(currentBook.getInfoLink());

                // Create a new intent to view the book previewLink
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, infoLink);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }


    @Override
    public android.content.Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, google_books_request_api);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        if (start){
            mEmptyStateTextView.setText("Explore the world of books...");
            start = false;
        }
        else{
            // Set empty state text to display "No books found."
            mEmptyStateTextView.setText(R.string.no_books);
        }

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    public void checkConnection(ConnectivityManager connectivityManager) {
        // Status of internet connection
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {
            isConnected = true;

        } else {
            isConnected = false;
        }
    }
}