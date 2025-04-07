package com.example.musicapp.data.repository.playlist;

import com.example.musicapp.data.model.Playlist;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

// Định nghĩa API cho ViewModel gọi đến, giúp ViewModel không cần quan tâm dữ liệu lấy từ đâu (Quản lý nguồn dữ liệu)
public interface PlaylistRepository {
    interface Local {
        Flowable<List<Playlist>> getAll();

        Single<Playlist> findByName(String playlistName);

        Completable insert(Playlist playlist);

        Completable update(Playlist playlist);

        Completable delete(Playlist playlist);
    }

    interface Remote {

    }
}
