package com.example.musicapp.data.source;

import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.playlist.PlaylistSongCrossRef;
import com.example.musicapp.data.model.playlist.PlaylistWithSongs;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

// Định nghĩa các nguồn dữ liệu cho Playlist
public interface PlaylistDataSource {
    interface Local{
        Flowable<List<Playlist>> getAll();

        Single<Playlist> findByName(String playlistName);

        Flowable<List<PlaylistWithSongs>> getAllPlaylistWithSongs();

        Single<PlaylistWithSongs> findPlaylistWithSongByPlaylistId(int playlistId);

        Completable insert(Playlist playlist);

        Completable insertPlaylistSongCrossRef(PlaylistSongCrossRef object);

        Completable update(Playlist playlist);

        Completable delete(Playlist playlist);
    }

    interface Remote{

    }
}
