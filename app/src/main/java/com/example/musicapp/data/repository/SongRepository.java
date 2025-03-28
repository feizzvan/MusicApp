package com.example.musicapp.data.repository;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.model.SongList;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Callback;

public interface SongRepository {
    interface Local{
        Single<List<Song>> getSongs(); // Hàm để lấy danh sách bài hát từ nguồn dữ liệu cục bộ (local)
        Completable saveSongs(Song... songs); // Hàm để lưu danh sách bài hát vào nguồn dữ liệu cục bộ
        Completable updateSong(Song song);
    }

    interface Remote{
        void loadSongs(Callback<SongList> callback); // Hàm để tải danh sách bài hát từ nguồn dữ liệu (remote)
    }
}
