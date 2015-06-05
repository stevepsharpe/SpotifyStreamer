package com.stevepsharpe.spotifystreamer.ui.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stevepsharpe.spotifystreamer.R;

/**
 * Created by stevepsharpe on 04/06/15.
 */
public class ArtistViewHolder {
    public ImageView artistThumb;
    public TextView artistName;

    public ArtistViewHolder(View view) {
        artistThumb = (ImageView) view.findViewById(R.id.artistThumb);
        artistName = (TextView) view.findViewById(R.id.artistName);
    }
}