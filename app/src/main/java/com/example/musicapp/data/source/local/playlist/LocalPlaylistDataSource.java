package com.example.musicapp.data.source.local.playlist;

import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.source.PlaylistDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

// Triển khai tất cả các phương thức trong PlaylistDataSource.Local bằng cách sử dụng PlaylistDAO
public class LocalPlaylistDataSource implements PlaylistDataSource.Local {
    private final PlaylistDAO mPlaylistDAO;

    public LocalPlaylistDataSource(PlaylistDAO playlistDAO) {
        mPlaylistDAO = playlistDAO;
    }

    @Override
    public Flowable<List<Playlist>> getAll() {
        return mPlaylistDAO.getAll();
    }

    @Override
    public Single<Playlist> findByName(String playlistName) {
        return mPlaylistDAO.findByName(playlistName);
    }

    @Override
    public Completable insert(Playlist playlist) {
        return mPlaylistDAO.insert(playlist.getName(), playlist.getArtwork(), playlist.getCreatedAt());
    }

    @Override
    public Completable update(Playlist playlist) {
        return mPlaylistDAO.update(playlist);
    }

    @Override
    public Completable delete(Playlist playlist) {
        return mPlaylistDAO.update(playlist);
    }
}
