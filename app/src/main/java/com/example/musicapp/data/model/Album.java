package com.example.musicapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album {
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("size")
    private int mSize;
    @SerializedName("artwork")
    private String mArtwork;
    @SerializedName("songs")
    private final List<String> mSongs = new ArrayList<>();

    public Album() {
    }

    public Album(int id, int size, String name, String artwork) {
        this.mId = id;
        this.mSize = size;
        this.mName = name;
        this.mArtwork = artwork;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        this.mSize = size;
    }

    public String getArtwork() {
        return mArtwork;
    }

    public void setArtwork(String artwork) {
        this.mArtwork = artwork;
    }

    public void setSongs(List<String> songs) {
        if (songs != null && !songs.isEmpty()) {
            mSongs.clear();
            mSongs.addAll(songs);
        }
    }

    public List<String> getSongs() {
        return mSongs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return mId == album.mId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId);
    }
}
