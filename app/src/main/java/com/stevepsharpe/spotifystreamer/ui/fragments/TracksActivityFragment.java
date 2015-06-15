package com.stevepsharpe.spotifystreamer.ui.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.adapters.TracksArrayAdapter;
import com.stevepsharpe.spotifystreamer.model.SpotifyArtist;
import com.stevepsharpe.spotifystreamer.model.SpotifyTrack;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;


import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment {

    private static final String LOG_TAG = TracksActivityFragment.class.getSimpleName();
    public static final String SPOTIFY_ARTIST = "spotify_artist";
    public static final String SPOTIFY_TRACKS = "spotify_tracks";

    private static final String COUNTRY_CODE = "GB";

    private Toast mToast;
    private SpotifyArtist mArtist;
    private ArrayList<SpotifyTrack> mSpotifyTracks;

    private TracksArrayAdapter mTracksArrayAdapter;
    private SpotifyService mSpotifyService;

    @InjectView(R.id.tracksListView) ListView mListView;

    public TracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);
        ButterKnife.inject(this, rootView);

        mTracksArrayAdapter = new TracksArrayAdapter(getActivity());

        SpotifyApi api = new SpotifyApi();
        mSpotifyService = api.getService();

        mListView.setAdapter(mTracksArrayAdapter);
        mListView.setOnItemClickListener(mTracksArrayAdapter);

        if (savedInstanceState != null) {
            mArtist = savedInstanceState.getParcelable(SPOTIFY_ARTIST);
            mSpotifyTracks = savedInstanceState.getParcelableArrayList(SPOTIFY_TRACKS);

            mTracksArrayAdapter.clear();
            mTracksArrayAdapter.addAll(mSpotifyTracks);
        } else {
            Intent intent = getActivity().getIntent();
            mArtist = intent.getParcelableExtra(SPOTIFY_ARTIST);
            getTopTracks();
        }

        setSubTitle(mArtist.getName());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SPOTIFY_ARTIST, mArtist);
        outState.putParcelableArrayList(SPOTIFY_TRACKS, mSpotifyTracks);
        super.onSaveInstanceState(outState);
    }

    private void setSubTitle(String artistName) {
        if (artistName != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            actionBar.setSubtitle(artistName);
        }
    }

    private void getTopTracks() {
        if (mArtist != null) {

            // build the params
            Map<String, Object> options = new HashMap<>();
            options.put("country", COUNTRY_CODE);

            mSpotifyService.getArtistTopTrack(mArtist.getId(), options, new SpotifyCallback<Tracks>() {
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
                public void success(Tracks tracks, Response response) {
                    mSpotifyTracks = (ArrayList<SpotifyTrack>) SpotifyTrack.spotifyTracksArrayList(tracks.tracks);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mTracksArrayAdapter.clear();

                            if (mSpotifyTracks.isEmpty()) {
                                mToast = Toast.makeText(getActivity(), R.string.no_top_tracks, Toast.LENGTH_SHORT);
                                mToast.show();
                            } else {
                                mTracksArrayAdapter.addAll(mSpotifyTracks);
                            }

                        }
                    });

                }
            });

        }
    }
}