package com.stevepsharpe.spotifystreamer.ui.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stevepsharpe.spotifystreamer.R;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class TrackViewHolder {
    public ImageView albumThumb;
    public TextView trackName;
    public TextView albumName;

    public TrackViewHolder(View view) {
        trackName = (TextView) view.findViewById(R.id.trackName);
        albumThumb = (ImageView) view.findViewById(R.id.albumThumb);
        albumName = (TextView) view.findViewById(R.id.albumName);
    }
}