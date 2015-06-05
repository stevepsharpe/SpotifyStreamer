package com.stevepsharpe.spotifystreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;
import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.ui.viewholders.TrackViewHolder;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class TracksArrayAdapter extends ArrayAdapter<Track> {

    Context context;

    public TracksArrayAdapter(Context context) {
        super(context, R.layout.list_item_track, new ArrayList<Track>());
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TrackViewHolder holder = null;
        Track track = this.getItem(position);
        
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_track, parent, false);
            holder = new TrackViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TrackViewHolder) convertView.getTag();
        }

        holder.trackName.setText(track.name);
        holder.albumName.setText(track.album.name);

        if (!track.album.images.isEmpty()) {
            // get 300x300 artwork
            Picasso.with(this.getContext()).load(track.album.images.get(1).url)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.albumThumb);
        } else {
            holder.albumThumb.setImageResource(R.drawable.placeholder);
        }

        return convertView;
    }
}
