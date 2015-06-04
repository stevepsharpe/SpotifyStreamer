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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevepsharpe.spotifystreamer.R;

import java.util.ArrayList;
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
    private ListView mListView;
    private SearchArtistsTask mSearchArtistsTask;
    private ArtistArrayAdapter mArtistArrayAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.artistsListView);
        mArtistArrayAdapter = new ArtistArrayAdapter(getActivity());
        mListView.setAdapter(mArtistArrayAdapter);

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

//            spotifyService.searchArtists(strings[0], new SpotifyCallback<ArtistsPager>() {
//
//                @Override
//                public void failure(SpotifyError spotifyError) {
//                    Log.e(LOG_TAG, "SpotifyError: " + spotifyError);
//                }
//
//                @Override
//                public void success(ArtistsPager artistsPager, Response response) {
//
//                    // TODO - Remove logs and change return type of AsyncTask
//                    for (Artist artist : artistsPager.artists.items) {
//                        Log.v(LOG_TAG, "Artist: " + artist.name + " - ID: " + artist.id);
//                    }
//
//                    artists = artistsPager.artists.items;
//                }
//            });

            return artists;
        }

        @Override
        protected void onPostExecute(List artists) {
            // TODO - update the list view
            mArtistArrayAdapter.clear();
            mArtistArrayAdapter.addAll(artists);
        }
    }

    private class ArtistArrayAdapter extends ArrayAdapter<Artist> {

        Context context;

        public ArtistArrayAdapter(Context context) {
            super(context, R.layout.list_item_artist, new ArrayList<Artist>());
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ArtistViewHolder holder =  null;
            Artist artist = this.getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item_artist, parent, false);
                holder = new ArtistViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ArtistViewHolder) convertView.getTag();
            }

            holder.artistName.setText(artist.name);

            if (artist.images.size() > 0) {
                Picasso.with(this.getContext()).load(artist.images.get(0).url).into(holder.artistThumb);
            } else {
                holder.artistThumb.setImageResource(R.drawable.placeholder);
            }

            return convertView;
        }
    }

    private static class ArtistViewHolder {
        ImageView artistThumb;
        TextView artistName;

        public ArtistViewHolder(View view) {
            artistThumb = (ImageView) view.findViewById(R.id.artistThumb);
            artistName = (TextView) view.findViewById(R.id.artistName);
        }
    }
}
