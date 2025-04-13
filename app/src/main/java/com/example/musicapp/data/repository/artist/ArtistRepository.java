package com.example.musicapp.data.repository.artist;

import com.example.musicapp.data.model.artist.ArtistList;

import retrofit2.Callback;

public interface ArtistRepository {
    void loadArtists(Callback<ArtistList> callback);
}
