package com.example.musicapp.data.model;

import androidx.media3.common.MediaItem;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "playlists")
public class Playlist {
    @Ignore
    private static int sNextId = 1001;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    private int mId = 1001;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "artwork")
    private String mArtwork;

    @ColumnInfo(name = "create_at")
    private Date mCreateAt;

    @Ignore
    private List<Song> mSongs = new ArrayList<>();

    @Ignore
    private final List<MediaItem> mMediaItems = new ArrayList<>();

    public Playlist() {

    }

    @Ignore
    public Playlist(int id, String name) {
        this(id, name, null, null, null);
    }

    @Ignore
    public Playlist(int id, String name, String artwork, Date createAt, List<Song> songs) {
        setId(id);
        setName(name);
        setArtwork(artwork);
        setCreateAt(createAt);
        updateSongs(songs);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        if (id > 0) {
            mId = id;
        } else {
            mId = sNextId++;
        }
    }

    public String getArtwork() {
        return mArtwork;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setArtwork(String artwork) {
        mArtwork = artwork;
    }

    public Date getCreateAt() {
        return mCreateAt;
    }

    public void setCreateAt(Date createAt) {
        mCreateAt = createAt;
    }

    public List<Song> getSongs() {
        return mSongs;
    }

    public void updateSongs(List<Song> songs) {
        if (songs != null && !songs.isEmpty()) {
            mSongs = songs;
            updateMediaItems();
        }
    }

    public List<MediaItem> getMediaItems() {
        return mMediaItems;
    }

    private void updateMediaItems() {
        mMediaItems.clear();
        for (Song song : mSongs) {
            mMediaItems.add(MediaItem.fromUri(song.getSource()));
        }
    }
}
