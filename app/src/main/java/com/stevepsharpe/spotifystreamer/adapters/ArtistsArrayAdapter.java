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
import com.stevepsharpe.spotifystreamer.ui.activities.TracksActivity;
import com.stevepsharpe.spotifystreamer.ui.viewholders.ArtistViewHolder;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class ArtistsArrayAdapter extends ArrayAdapter<Artist> implements AdapterView.OnItemClickListener {

    Context mContext;

    public ArtistsArrayAdapter(Context context) {
        super(context, R.layout.list_item_artist, new ArrayList<Artist>());
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArtistViewHolder holder = null;
        Artist artist = this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_artist, parent, false);
            holder = new ArtistViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ArtistViewHolder) convertView.getTag();
        }

        holder.artistName.setText(artist.name);

        if (!artist.images.isEmpty()) {
            // I wanted to grab 300x300 image size. They seem to all be random sizes depending on
            // which artist you search so *for now* I'm just grabbing the largest one
            // and then using Picasso to resize as it caches the image
            Picasso.with(this.getContext()).load(artist.images.get(0).url)
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
        Artist artist = this.getItem(i);

        // I checked the models provided by spotify-web-api-android
        // and they do not implement Parcelable so I can't pass the whole object
        // for now we just need the artist id and name
        Intent intent = new Intent(mContext, TracksActivity.class);
        intent.putExtra("artistID", artist.id);
        intent.putExtra("artistName", artist.name);
        mContext.startActivity(intent);
    }
}
