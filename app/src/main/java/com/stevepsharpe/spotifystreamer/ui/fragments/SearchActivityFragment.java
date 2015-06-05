package com.stevepsharpe.spotifystreamer.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.adapters.ArtistsArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private static final String LOG_TAG = SearchActivityFragment.class.getSimpleName();

    private static final int TRIGGER_SEARCH = 100;
    private static final int SEARCH_TRIGGER_DELAY_IN_MS = 500;

    private EditText mSearchField;
    private ListView mListView;
    private Toast mToast;

    private SearchArtistsTask mSearchArtistsTask;
    private ArtistsArrayAdapter mArtistArrayAdapter;
    private SpotifyService mSpotifyService;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mArtistArrayAdapter = new ArtistsArrayAdapter(getActivity());

        SpotifyApi api = new SpotifyApi();
        mSpotifyService = api.getService();

//        TextView emptyText = (TextView) rootView.findViewById(android.R.id.empty);

        mListView = (ListView) rootView.findViewById(R.id.artistsListView);
        mListView.setAdapter(mArtistArrayAdapter);
//        mListView.setEmptyView(emptyText);
        mListView.setOnItemClickListener(mArtistArrayAdapter);

        // http://stackoverflow.com/questions/10317716/android-remove-soft-keyboard-when-touching-the-listview
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                hideKeyboard();
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        });

        // hide the emptyView by default until a search is performed
//        emptyText.setVisibility(View.INVISIBLE);

        mSearchField = (EditText) rootView.findViewById(R.id.searchEditText);

        // I've added this to do a live search - however this generates a lot of network requests
        // maybe I just need to use setOnEditorActionListener?
        // http://developer.android.com/reference/android/widget/TextView.html#addTextChangedListener(android.text.TextWatcher)
        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(final Editable editable) {
                handler.removeMessages(TRIGGER_SEARCH);
                handler.sendEmptyMessageDelayed(TRIGGER_SEARCH, SEARCH_TRIGGER_DELAY_IN_MS);
            }
        });

        // http://developer.android.com/reference/android/widget/TextView.html#setOnEditorActionListener(android.widget.TextView.OnEditorActionListener)
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    searchArtists();
                    hideKeyboard();
                    handled = true;
                }
                return handled;
            }
        });

        return rootView;
    }

    // http://stackoverflow.com/questions/10217051/how-to-avoid-multiple-triggers-on-edittext-while-user-is-typing
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TRIGGER_SEARCH) {
                searchArtists();
            }
        }
    };

    private void searchArtists() {

        if (mSearchField.length() > 0) {

            // do we have a task already?
            // this raises a java.io.InterruptedIOException from Retrofit
            // when cancelling the task
            // leaving in for reference
//            if (mSearchArtistsTask != null) {
//                mSearchArtistsTask.cancel(true);
//            }

            String query = mSearchField.getText().toString();

            mSearchArtistsTask = new SearchArtistsTask();
            mSearchArtistsTask.execute(query);

        } else {
            if (mToast != null) {
                // Close the toast if it's showing
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), R.string.no_search_results, Toast.LENGTH_SHORT);
            mToast.show();
            mArtistArrayAdapter.clear();
        }

    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class SearchArtistsTask extends AsyncTask<String, Void, List<Artist>> {

        @Override
        protected List doInBackground(String... strings) {

            try {
                // search with wildcard to match how Spotify works with partial match
                return mSpotifyService.searchArtists(strings[0] + "*").artists.items;
            } catch (RetrofitError error) {
                Log.e(LOG_TAG, "SpotifyError: " + SpotifyError.fromRetrofitError(error));
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {

            if (mToast != null) {
                // Close the toast if it's showing
                mToast.cancel();
            }

            if (artists == null) {
                mToast = Toast.makeText(getActivity(), R.string.error_spotify, Toast.LENGTH_SHORT);
                mToast.show();
            } else if (artists.isEmpty()) {
                mToast = Toast.makeText(getActivity(), R.string.no_search_results, Toast.LENGTH_SHORT);
                mToast.show();
            }

            mArtistArrayAdapter.clear();
            mArtistArrayAdapter.addAll(artists);
        }
    }
}