package com.stevepsharpe.spotifystreamer.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.stevepsharpe.spotifystreamer.R;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private EditText mSearchField;
    private SearchArtistsTask mSearchArtistsTask;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mSearchField = (EditText) rootView.findViewById(R.id.searchEditText);
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (TextUtils.isEmpty(mSearchField.getText().toString())) {
                        mSearchField.setError(getString(R.string.error_blank_search));
                    } else {
                        searchArtists();
                    }

                    handled = true;
                }
                return handled;
            }
        });

        return rootView;
    }

    private void searchArtists() {
        String query = mSearchField.getText().toString();
        hideKeyboard();

        mSearchArtistsTask = new SearchArtistsTask();
        mSearchArtistsTask.execute(query);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class SearchArtistsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            spotifyService.searchArtists(strings[0], new SpotifyCallback<ArtistsPager>() {

                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.e(LOG_TAG, "SpotifyError: " + spotifyError);
                }

                @Override
                public void success(ArtistsPager artistsPager, Response response) {

                    // TODO - Remove logs and change return type of AsyncTask
                    for (Artist artist : artistsPager.artists.items) {
                        Log.v(LOG_TAG, "Artist: " + artist.name + " - ID: " + artist.id);
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // TODO - update the list view
        }
    }
}
