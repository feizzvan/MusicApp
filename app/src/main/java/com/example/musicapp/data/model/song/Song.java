package com.example.musicapp.data.model.song;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "songs")
public class Song {
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "song_id")
    private int mId;

    @SerializedName("title")
    @ColumnInfo(name = "title")
    private String mTitle;

    @SerializedName("artistId")
    @ColumnInfo(name = "artist_id")
    private int mArtistId;

    @SerializedName("duration")
    @ColumnInfo(name = "duration")
    private int mDuration;

    @SerializedName("fileUrl")
    @ColumnInfo(name = "file_url")
    private String mFileUrl;

    @SerializedName("imageUrl")
    @ColumnInfo(name = "image_url")
    private String mImageUrl;

    @SerializedName("genreId")
    @Ignore
    private int genreId;

    public Song() {
    }

    @Ignore
    public Song(int Id) {
        setId(Id);
    }

    public Song(int Id, String Title, int artistId, int Duration, String fileUrl, String imageUrl) {
        setId(Id);
        setTitle(Title);
        setArtistId(artistId);
        setFileUrl(fileUrl);
        setImageUrl(imageUrl);
        setDuration(Duration);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getArtistId() {
        return mArtistId;
    }

    public void setArtistId(int artist) {
        this.mArtistId = artist;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public void setFileUrl(String source) {
        this.mFileUrl = source;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String image) {
        this.mImageUrl = image;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
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
