package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tayyab on 6/30/2017.
 */

public class TrailerData implements Parcelable{
    private String id;
    private String key;
    private String name;

    public TrailerData() {
    }

    private TrailerData(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<TrailerData> CREATOR
            = new Parcelable.Creator<TrailerData>() {
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };
}
