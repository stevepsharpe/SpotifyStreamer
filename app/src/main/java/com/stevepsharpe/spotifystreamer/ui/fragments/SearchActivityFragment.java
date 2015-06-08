package com.stevepsharpe.spotifystreamer.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.adapters.ArtistsArrayAdapter;
import com.stevepsharpe.spotifystreamer.model.SpotifyArtist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
public class SearchActivityFragment extends Fragment {

    private static final String LOG_TAG = SearchActivityFragment.class.getSimpleName();
    private static final String STATE_QUERY = "state_query";
    private static final String STATE_ARTISTS = "state_artists";

    private static final int TRIGGER_SEARCH = 100;
    private static final int SEARCH_TRIGGER_DELAY_IN_MS = 500;


    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<SpotifyArtist> mSpotifyArtists;
    private Toast mToast;

    private ArtistsArrayAdapter mArtistArrayAdapter;
    private SpotifyService mSpotifyService;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mArtistArrayAdapter = new ArtistsArrayAdapter(getActivity(), new ArrayList<SpotifyArtist>());

        SpotifyApi api = new SpotifyApi();
        mSpotifyService = api.getService();

        mListView = (ListView) rootView.findViewById(R.id.artistsListView);
        mListView.setAdapter(mArtistArrayAdapter);
        mListView.setOnItemClickListener(mArtistArrayAdapter);

        // http://stackoverflow.com/questions/10317716/android-remove-soft-keyboard-when-touching-the-listview
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                hideKeyboard();
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        mSearchView = (SearchView) rootView.findViewById(R.id.searchArtist);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handler.removeMessages(TRIGGER_SEARCH);
                handler.sendEmptyMessage(TRIGGER_SEARCH);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeMessages(TRIGGER_SEARCH);
                handler.sendEmptyMessageDelayed(TRIGGER_SEARCH, SEARCH_TRIGGER_DELAY_IN_MS);
                return false;
            }
        });

        if (savedInstanceState != null) {
            mSpotifyArtists = savedInstanceState.getParcelableArrayList(STATE_ARTISTS);
            mArtistArrayAdapter.clear();
            mArtistArrayAdapter.addAll(mSpotifyArtists);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        String query = mSearchView.getQuery().toString();
        outState.putString(STATE_QUERY, query);
        outState.putParcelableArrayList(STATE_ARTISTS, mSpotifyArtists);

        super.onSaveInstanceState(outState);
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

        if (mSearchView.getQuery().toString().length() > 0) {

            String query = mSearchView.getQuery().toString();
            mSpotifyService.searchArtists(query, new SpotifyCallback<ArtistsPager>() {
                @Override
                public void failure(SpotifyError spotifyError) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mToast = Toast.makeText(getActivity(), R.string.error_spotify, Toast.LENGTH_SHORT);
                            mToast.show();
                        }
                    });

                }

                @Override
                public void success(ArtistsPager artistsPager, Response response) {

                    mSpotifyArtists = (ArrayList<SpotifyArtist>) SpotifyArtist.spotifyArtistsArrayList(artistsPager.artists.items);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mArtistArrayAdapter.clear();

                            if (mSpotifyArtists.isEmpty()) {
                                mToast = Toast.makeText(getActivity(), R.string.no_search_results, Toast.LENGTH_SHORT);
                                mToast.show();
                            } else {
                                mArtistArrayAdapter.addAll(mSpotifyArtists);
                            }

                        }
                    });

                }
            });

        } else {
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

}