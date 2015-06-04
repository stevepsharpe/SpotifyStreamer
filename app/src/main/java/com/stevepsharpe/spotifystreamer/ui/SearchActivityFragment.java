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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.ui.adapters.ArtistsArrayAdapter;

import java.util.ArrayList;
import java.util.List;

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

    private EditText mSearchField;
    private ListView mListView;

    private SearchArtistsTask mSearchArtistsTask;
    private ArtistsArrayAdapter mArtistArrayAdapter;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistArrayAdapter = new ArtistsArrayAdapter(getActivity());

        mListView = (ListView) rootView.findViewById(R.id.artistsListView);
        mListView.setAdapter(mArtistArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // not sure if I should have used mArtistArrayAdapter like so...
                // Artist artist = mArtistArrayAdapter.getItem(i);
                // or like I ended up doing getting the adapter from the adapterView?
                Artist artist = (Artist) adapterView.getAdapter().getItem(i);

                Toast.makeText(getActivity(), "Artist: " + artist.name, Toast.LENGTH_LONG).show();
            }
        });

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

    private class SearchArtistsTask extends AsyncTask<String, Void, List> {

        @Override
        protected List doInBackground(String... strings) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            List<Artist> artists = new ArrayList<Artist>();

            try {
                artists = spotifyService.searchArtists(strings[0]).artists.items;
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // handle error
                Log.e(LOG_TAG, "SpotifyError: " + spotifyError);
            }

            return artists;
        }

        @Override
        protected void onPostExecute(List artists) {
            // TODO - update the list view
            mArtistArrayAdapter.clear();
            mArtistArrayAdapter.addAll(artists);
        }
    }
}