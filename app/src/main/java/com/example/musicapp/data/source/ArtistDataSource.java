package com.example.musicapp.data.source;

import com.example.musicapp.data.model.artist.ArtistList;

import retrofit2.Callback;

public interface ArtistDataSource {
    interface Local {

    }

    interface Remote {
        void loadArtists(Callback<ArtistList> callback);
    }
}
