package com.stevepsharpe.spotifystreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.model.SpotifyTrack;
import com.stevepsharpe.spotifystreamer.ui.viewholders.TrackViewHolder;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class TracksArrayAdapter extends ArrayAdapter<SpotifyTrack> implements AdapterView.OnItemClickListener {

    Context mContext;

    public TracksArrayAdapter(Context context) {
        super(context, R.layout.list_item_track, new ArrayList<SpotifyTrack>());
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TrackViewHolder holder = null;
        SpotifyTrack track = this.getItem(position);
        
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_track, parent, false);
            holder = new TrackViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TrackViewHolder) convertView.getTag();
        }

        holder.trackName.setText(track.getName());
        holder.albumName.setText(track.getAlbumName());

        if (track.getArtworkThumb() != null) {
            // get 300x300 artwork
            Picasso.with(this.getContext()).load(track.getArtworkThumb())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.albumThumb);
        } else {
            holder.albumThumb.setImageResource(R.drawable.placeholder);
        }

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SpotifyTrack track = this.getItem(i);
        Toast.makeText(mContext, "Track: " + track.getName(), Toast.LENGTH_LONG).show();
    }
}
