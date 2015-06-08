package com.stevepsharpe.spotifystreamer.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by stevepsharpe on 05/06/15.
 */
public class SpotifyArtist implements Parcelable {

    private String name;
    private String id;
    private ArrayList<String> images = new ArrayList<String>();

    public SpotifyArtist(Artist artist) {
        this.id = artist.id;
        this.name = artist.name;
        for (Image image : artist.images) {
            if (image.url != null) {
                this.images.add(image.url);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public static List<SpotifyArtist> spotifyArtistsArrayList(List<Artist> artists) {

        ArrayList<SpotifyArtist> spotifyArtists = new ArrayList<SpotifyArtist>();

        for (Artist artist : artists) {
            spotifyArtists.add(new SpotifyArtist(artist));
        }

        return spotifyArtists;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeStringList(this.images);
    }

    protected SpotifyArtist(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
        this.images = in.createStringArrayList();
    }

    public static final Creator<SpotifyArtist> CREATOR = new Creator<SpotifyArtist>() {
        public SpotifyArtist createFromParcel(Parcel source) {
            return new SpotifyArtist(source);
        }

        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };
}
