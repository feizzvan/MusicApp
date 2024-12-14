package com.example.musicapp.data.repository;

import com.example.musicapp.data.model.SongList;
import com.example.musicapp.data.source.remote.SongRemoteDataSourceImpl;

import retrofit2.Callback;

public class SongRepositoryImpl implements SongRepository {
    @Override
    public void loadSongs(Callback<SongList> callback) {
        SongRemoteDataSourceImpl songRemoteDataSource = new SongRemoteDataSourceImpl();
        songRemoteDataSource.loadSongs(callback);
    }
}
