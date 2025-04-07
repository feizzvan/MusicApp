package com.example.musicapp.data.source;

import com.example.musicapp.data.model.Playlist;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

// Định nghĩa các nguồn dữ liệu cho Playlist
public interface PlaylistDataSource {
    interface Local{
        Flowable<List<Playlist>> getAll();

        Single<Playlist> findByName(String playlistName);

        Completable insert(Playlist playlist);

        Completable update(Playlist playlist);

        Completable delete(Playlist playlist);
    }

    interface Remote{

    }
}
