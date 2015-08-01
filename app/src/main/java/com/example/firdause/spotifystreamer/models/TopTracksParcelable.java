package com.example.firdause.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johanneslewiste on 6/24/15.
 */
public class TopTracksParcelable implements Parcelable{
    //For debugging purpose
    private final String LOG_TAG = TopTracksParcelable.class.getSimpleName();

    private String smallUrl;
    private String bigUrl;

    //This is meant for Spotify Stage 2, ignore it at the moment
    private String previewUrl;

    private String name;
    private String album;
    private String artist;

    //Constructor for TopTracksParcelable
    public TopTracksParcelable(String smallUrl, String bigUrl, String previewUrl,
                               String name, String album, String artist) {
        this.smallUrl = smallUrl;
        this.bigUrl = bigUrl;
        this.previewUrl = previewUrl;
        this.name = name;
        this.album = album;
        this.artist = artist;
    }

    //Getters and Setters
    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String getBigUrl() {
        return bigUrl;
    }

    public void setBigUrl(String bigUrl) {
        this.bigUrl = bigUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    //Parcelling part
    public TopTracksParcelable(Parcel in) {
        smallUrl = in.readString();
        bigUrl = in.readString();
        previewUrl = in.readString();
        name = in.readString();
        album = in.readString();
        artist = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }


    //Write to parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(smallUrl);
        dest.writeString(bigUrl);
        dest.writeString(previewUrl);
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(artist);
    }


    public static final Parcelable.Creator<TopTracksParcelable> CREATOR = new Parcelable.Creator<TopTracksParcelable>() {

        @Override
        public TopTracksParcelable createFromParcel(Parcel in) {
            return new TopTracksParcelable(in);
        }

        @Override
        public TopTracksParcelable[] newArray(int size) {
            return new TopTracksParcelable[size];
        }
    };
}
