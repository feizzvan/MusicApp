package com.example.musicapp.data.source;

import com.example.musicapp.data.model.album.AlbumList;

import retrofit2.Callback;

// `AlbumDataSource` là một interface để định nghĩa các nguồn dữ liệu cho Album.
// Được chia thành hai interface con: `Remote` và `Local` để hỗ trợ phân chia logic xử lý dữ liệu từ xa và cục bộ.


public interface AlbumDataSource {
    interface Remote {
        void loadAlbums(Callback<AlbumList> callback);
    }

    interface Local {

    }
}
