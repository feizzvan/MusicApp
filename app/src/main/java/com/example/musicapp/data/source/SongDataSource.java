package com.example.musicapp.data.source;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.model.SongList;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Callback;

public interface SongDataSource {
    interface Remote {
        void loadSongs(Callback<SongList> callback); // Hàm để tải danh sách bài hát từ nguồn dữ liệu xa (remote)
    }

    interface Local {
        Single<List<Song>> getSongs(); // Hàm để lấy danh sách bài hát từ nguồn dữ liệu cục bộ (local)

        Completable saveSongs(Song... songs); // Hàm để lưu danh sách bài hát vào nguồn dữ liệu cục bộ

        Completable updateSong(Song song); // Hàm để cập nhật thông tin của một bài hát
    }
}
