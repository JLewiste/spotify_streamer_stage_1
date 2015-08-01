package com.example.firdause.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by johanneslewiste on 6/24/15.
 */
public class ArtistParcelable implements Parcelable {
    //For debugging purpose
    private final String LOG_TAG = ArtistParcelable.class.getSimpleName();

    private String id;
    private String url;
    private String name;

    //Constructor for ArtistParcelable
    public ArtistParcelable(String id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    //Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Parcelling part
    public ArtistParcelable(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    //Write to parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(url);

    }

    public static final Parcelable.Creator<ArtistParcelable> CREATOR = new Parcelable.Creator<ArtistParcelable>() {

        @Override
        public ArtistParcelable createFromParcel(Parcel in) {
            return new ArtistParcelable(in);
        }

        @Override
        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };
}
