package com.example.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tayyab on 6/5/2017.
 */

public class MovieData implements Parcelable {

    private int movieId;
    private String movieTitle;
    private String posterPath;
    private String movieOverview;
    private String movieRating;
    private String releaseDate;

    public MovieData () {

    }

    private MovieData(Parcel in) {
        movieId = in.readInt();
        movieTitle = in.readString();
        posterPath = in.readString();
        movieOverview = in.readString();
        movieRating = in.readString();
        releaseDate = in.readString();
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setPath(String path) {
        this.posterPath = path;
    }

    public String getPath() {
        return this.posterPath;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeString(posterPath);
        dest.writeString(movieOverview);
        dest.writeString(movieRating);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
