package com.example.musicapp.data.model.song;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongList {
    @SerializedName("songs")
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }
}
