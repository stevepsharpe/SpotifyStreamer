package com.stevepsharpe.spotifystreamer.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.adapters.TracksArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment {

    private static final String LOG_TAG = TracksActivityFragment.class.getSimpleName();
    private static final String COUNTRY_CODE = "GB";

    private ListView mListView;
    private Toast mToast;

    private GetTracksTask mGetTracksTask;
    private TracksArrayAdapter mTracksArrayAdapter;
    private SpotifyService mSpotifyService;

    public TracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        mTracksArrayAdapter = new TracksArrayAdapter(getActivity());

        SpotifyApi api = new SpotifyApi();
        mSpotifyService = api.getService();

        mListView = (ListView) rootView.findViewById(R.id.tracksListView);
        mListView.setAdapter(mTracksArrayAdapter);
        mListView.setOnItemClickListener(mTracksArrayAdapter);

        Intent intent = getActivity().getIntent();
        String artistID = intent.getStringExtra("artistID");
        String artistName = intent.getStringExtra("artistName");

        setSubTitle(artistName);
        getTopTracks(artistID);

        return rootView;
    }

    private void setSubTitle(String artistName) {
        if (artistName != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            actionBar.setSubtitle(artistName);
        }
    }

    private void getTopTracks(String artistID) {
        if (artistID != null) {
            mGetTracksTask = new GetTracksTask();
            mGetTracksTask.execute(artistID);
        }
    }

    private class GetTracksTask extends AsyncTask<String, Void, List> {

        @Override
        protected List doInBackground(String... strings) {

            // build the params
            Map<String, Object> options = new HashMap<>();
            options.put("country", COUNTRY_CODE);

            try {
                return mSpotifyService.getArtistTopTrack(strings[0], options).tracks;
            } catch (RetrofitError error) {
                Log.e(LOG_TAG, "SpotifyError: " + SpotifyError.fromRetrofitError(error));
                return null;
            }
        }

        @Override
        protected void onPostExecute(List tracks) {

            if (mToast != null) {
                // Close the toast if it's showing
                mToast.cancel();
            }

            if (tracks == null) {
                mToast = Toast.makeText(getActivity(), R.string.error_spotify, Toast.LENGTH_SHORT);
                mToast.show();
            } else if (tracks.isEmpty()) {
                mToast = Toast.makeText(getActivity(), R.string.no_top_tracks, Toast.LENGTH_SHORT);
                mToast.show();
            }

            mTracksArrayAdapter.clear();
            mTracksArrayAdapter.addAll(tracks);
        }
    }
}