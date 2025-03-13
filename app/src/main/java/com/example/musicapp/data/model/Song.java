package com.example.musicapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "songs")
public class Song {
    @SerializedName("id")
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    private String mId = " ";

    @SerializedName("title")
    @ColumnInfo(name = "title")
    private String mTitle;

    @SerializedName("album")
    @ColumnInfo(name = "album")
    private String mAlbum;

    @SerializedName("artist")
    @ColumnInfo(name = "artist")
    private String mArtist;

    @SerializedName("source")
    @ColumnInfo(name = "source")
    private String mSource;

    @SerializedName("image")
    @ColumnInfo(name = "image")
    private String mImage;

    @SerializedName("duration")
    @ColumnInfo(name = "duration")
    private int mDuration;

    @SerializedName("favorite")
    @ColumnInfo(name = "favorite")
    private boolean mFavorite;

    @SerializedName("counter")
    @ColumnInfo(name = "counter")
    private int mCounter;

    @SerializedName("replay")
    @ColumnInfo(name = "replay")
    private int mReplay;

    public Song() {
    }

    public Song(@NonNull String Id, String Title, String Album, String Artist, String Source,
                String Image, int Duration, boolean Favorite, int Counter, int Replay) {
        this.mId = Id;
        this.mTitle = Title;
        this.mAlbum = Album;
        this.mArtist = Artist;
        this.mSource = Source;
        this.mImage = Image;
        this.mDuration = Duration;
        this.mFavorite = Favorite;
        this.mCounter = Counter;
        this.mReplay = Replay;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        this.mSource = source;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.mFavorite = favorite;
    }

    public int getCounter() {
        return mCounter;
    }

    public void setCounter(int counter) {
        this.mCounter = counter;
    }

    public int getReplay() {
        return mReplay;
    }

    public void setReplay(int replay) {
        this.mReplay = replay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return Objects.equals(mId, song.mId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId);
    }
}
