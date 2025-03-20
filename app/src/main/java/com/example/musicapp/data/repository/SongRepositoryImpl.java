package com.example.musicapp.data.repository;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.model.SongList;
import com.example.musicapp.data.source.local.LocalSongDataSource;
import com.example.musicapp.data.source.remote.SongRemoteDataSourceImpl;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Callback;

public class SongRepositoryImpl implements SongRepository.Local, SongRepository.Remote {
    private final LocalSongDataSource mLocalSongDataSource;

    public SongRepositoryImpl(LocalSongDataSource localSongDataSource) {
        this.mLocalSongDataSource = localSongDataSource;
    }

    @Override
    public void loadSongs(Callback<SongList> callback) {
        SongRemoteDataSourceImpl songRemoteDataSource = new SongRemoteDataSourceImpl();
        songRemoteDataSource.loadSongs(callback);
    }

    @Override
    public Single<List<Song>> getSongs() {
        return mLocalSongDataSource.getSongs();
    }

    @Override
    public Completable saveSongs(Song... songs) {
        return mLocalSongDataSource.saveSongs(songs);
    }

    @Override
    public Completable updateSong(Song song) {
        return mLocalSongDataSource.updateSong(song);
    }
}
