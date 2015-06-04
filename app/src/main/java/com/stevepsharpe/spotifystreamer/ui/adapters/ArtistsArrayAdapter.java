package com.stevepsharpe.spotifystreamer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;
import com.stevepsharpe.spotifystreamer.R;
import com.stevepsharpe.spotifystreamer.ui.ArtistViewHolder;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class ArtistsArrayAdapter extends ArrayAdapter<Artist> {

    Context context;

    public ArtistsArrayAdapter(Context context) {
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
