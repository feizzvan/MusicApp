package com.example.musicapp.data.repository.song;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.model.SongList;
import com.example.musicapp.data.source.local.song.LocalSongDataSource;
import com.example.musicapp.data.source.remote.RemoteSongDataSourceImpl;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Callback;

public class SongRepositoryImpl implements SongRepository.Local, SongRepository.Remote {
    private final LocalSongDataSource mLocalSongDataSource;

    public SongRepositoryImpl(LocalSongDataSource localSongDataSource) {
        this.mLocalSongDataSource = localSongDataSource;
    }

    @Override
    public void loadSongs(Callback<SongList> callback) {
        RemoteSongDataSourceImpl songRemoteDataSource = new RemoteSongDataSourceImpl();
        songRemoteDataSource.loadSongs(callback);
    }

    @Override
    public Single<List<Song>> getSongs() {
        return mLocalSongDataSource.getSongs();
    }

    @Override
    public Flowable<List<Song>> getFavoriteSongs() {
        return mLocalSongDataSource.getFavoriteSongs();
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
