package com.example.musicapp.data.model.album;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumList {
    @SerializedName("playlists")
    private List<Album> albums;

    public List<Album> getAlbums() {
        return albums;
    }
}
