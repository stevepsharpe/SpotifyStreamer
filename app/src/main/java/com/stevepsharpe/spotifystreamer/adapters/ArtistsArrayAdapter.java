package com.stevepsharpe.spotifystreamer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;
import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.model.SpotifyArtist;
import com.stevepsharpe.spotifystreamer.ui.activities.TracksActivity;
import com.stevepsharpe.spotifystreamer.ui.fragments.TracksActivityFragment;
import com.stevepsharpe.spotifystreamer.ui.viewholders.ArtistViewHolder;

import java.util.ArrayList;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class ArtistsArrayAdapter extends ArrayAdapter<SpotifyArtist> implements AdapterView.OnItemClickListener {

    Context mContext;
    ArrayList<SpotifyArtist> mArtists;

    public ArtistsArrayAdapter(Context context, ArrayList<SpotifyArtist> artists) {
        super(context, R.layout.list_item_artist, artists);
        this.mContext = context;
        this.mArtists =  artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArtistViewHolder holder = null;
        SpotifyArtist artist = this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_artist, parent, false);
            holder = new ArtistViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ArtistViewHolder) convertView.getTag();
        }

        holder.artistName.setText(artist.getName());

        if (!artist.getImages().isEmpty()) {
            // I wanted to grab 300x300 image size. They seem to all be random sizes depending on
            // which artist you search so *for now* I'm just grabbing the largest one
            // and then using Picasso to resize as it caches the image
            Picasso.with(this.getContext()).load(artist.getImages().get(0))
                    .placeholder(R.drawable.placeholder)
                    .resize(300, 300)
                    .centerCrop()
                    .into(holder.artistThumb);
        } else {
            holder.artistThumb.setImageResource(R.drawable.placeholder);
        }

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        SpotifyArtist artist = this.getItem(i);

        Intent intent = new Intent(mContext, TracksActivity.class);
        intent.putExtra(TracksActivityFragment.SPOTIFY_ARTIST, artist);
        mContext.startActivity(intent);
    }
}
