package com.stevepsharpe.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by stevepsharpe on 08/06/15.
 */
public class SpotifyTrack implements Parcelable {
    private String name;
    private String albumName;
    private String artworkThumb;
    private String artworkLarge;

    public SpotifyTrack(Track track) {
        this.name = track.name;
        this.albumName = track.album.name;

        if (!track.album.images.isEmpty()) {
            if (track.album.images.get(0) != null) {
                this.artworkLarge = track.album.images.get(0).url;
            }

            if (track.album.images.get(1) != null) {
                this.artworkThumb = track.album.images.get(1).url;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtworkThumb() {
        return artworkThumb;
    }

    public void setArtworkThumb(String artworkThumb) {
        this.artworkThumb = artworkThumb;
    }

    public String getArtworkLarge() {
        return artworkLarge;
    }

    public void setArtworkLarge(String artworkLarge) {
        this.artworkLarge = artworkLarge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.albumName);
        dest.writeString(this.artworkThumb);
        dest.writeString(this.artworkLarge);
    }

    protected SpotifyTrack(Parcel in) {
        this.name = in.readString();
        this.albumName = in.readString();
        this.artworkThumb = in.readString();
        this.artworkLarge = in.readString();
    }

    public static final Parcelable.Creator<SpotifyTrack> CREATOR = new Parcelable.Creator<SpotifyTrack>() {
        public SpotifyTrack createFromParcel(Parcel source) {
            return new SpotifyTrack(source);
        }

        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };

    public static List<SpotifyTrack> spotifyTracksArrayList(List<Track> tracks) {

        ArrayList<SpotifyTrack> spotifyTracks = new ArrayList<>();

        for (Track track : tracks) {
            spotifyTracks.add(new SpotifyTrack(track));
        }

        return spotifyTracks;
    }
}
