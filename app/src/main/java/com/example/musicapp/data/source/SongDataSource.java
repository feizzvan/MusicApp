package com.example.musicapp.data.source;

import com.example.musicapp.data.model.SongList;

import retrofit2.Callback;

public interface SongDataSource {
    interface Remote {
        void loadSongs(Callback<SongList> callback); // Hàm để tải danh sách bài hát từ nguồn dữ liệu xa (remote)
    }

    interface Local {

    }
}
