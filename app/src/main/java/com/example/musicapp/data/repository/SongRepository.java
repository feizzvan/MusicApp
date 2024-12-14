package com.example.musicapp.data.repository;

import com.example.musicapp.data.model.SongList;

import retrofit2.Callback;

public interface SongRepository {
    void loadSongs(Callback<SongList> callback); // Hàm để tải danh sách bài hát từ nguồn dữ liệu xa (remote)
}
