package com.example.musicapp.data.repository.playlist;

import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.source.PlaylistDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

// Kết nối với LocalPlaylistDataSource hoặc RemotePlaylistDataSource để lấy dữ liệu (Cài đặt repository)
public class PlaylistRepositoryImpl implements PlaylistRepository.Local, PlaylistRepository.Remote {
    private final PlaylistDataSource.Local mLocalDataSource;

    public PlaylistRepositoryImpl(PlaylistDataSource.Local localDataSource) {
        mLocalDataSource = localDataSource;
    }

    @Override
    public Flowable<List<Playlist>> getAll() {
        return mLocalDataSource.getAll();
    }

    @Override
    public Single<Playlist> findByName(String playlistName) {
        return mLocalDataSource.findByName(playlistName);
    }

    @Override
    public Completable insert(Playlist playlist) {
        return mLocalDataSource.insert(playlist);
    }

    @Override
    public Completable update(Playlist playlist) {
        return mLocalDataSource.update(playlist);
    }

    @Override
    public Completable delete(Playlist playlist) {
        return mLocalDataSource.delete(playlist);
    }
}
