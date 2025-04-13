package com.example.musicapp.data.repository.artist;

import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.source.ArtistDataSource;
import com.example.musicapp.data.source.remote.RemoteArtistDataSource;

import retrofit2.Callback;

public class ArtistRepositoryImpl implements ArtistRepository{
    private final ArtistDataSource.Remote remoteArtistDataSource = new RemoteArtistDataSource();

    @Override
    public void loadArtists(Callback<ArtistList> callback) {
        remoteArtistDataSource.loadArtists(callback);
    }
}
