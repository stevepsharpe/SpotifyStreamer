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

    private ListView mListView;

    private GetTracksTask mGetTracksTask;
    private TracksArrayAdapter mTracksArrayAdapter;

    public TracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        mTracksArrayAdapter = new TracksArrayAdapter(getActivity());

        mListView = (ListView) rootView.findViewById(R.id.tracksListView);
        mListView.setAdapter(mTracksArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Track track = (Track) adapterView.getAdapter().getItem(i);
                Toast.makeText(getActivity(), "Track: " + track.name, Toast.LENGTH_LONG);
            }
        });

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
            GetTracksTask getTracksTask = new GetTracksTask();
            getTracksTask.execute(artistID);
        }
    }

    private class GetTracksTask extends AsyncTask<String, Void, List> {

        @Override
        protected List doInBackground(String... strings) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();
            List<Track> tracks = new ArrayList<Track>();

            // build the params
            Map<String, Object> params = new HashMap<>();
            params.put("country", "GB");

            try {
                tracks = spotifyService.getArtistTopTrack(strings[0], params).tracks;
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // handle error
                Log.e(LOG_TAG, "SpotifyError: " + spotifyError);
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(List tracks) {
            mTracksArrayAdapter.clear();
            mTracksArrayAdapter.addAll(tracks);
        }
    }
}
