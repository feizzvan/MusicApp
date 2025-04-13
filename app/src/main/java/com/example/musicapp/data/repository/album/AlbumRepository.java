package com.example.musicapp.data.repository.album;

import com.example.musicapp.data.model.album.AlbumList;

import retrofit2.Callback;

// `AlbumRepository` là một interface dùng để định nghĩa phương thức load dữ liệu Album từ nguồn dữ liệu từ xa hoặc cục bộ.
// Mục tiêu là để tạo ra một lớp trừu tượng có thể dễ dàng thay đổi hoặc mở rộng khi cần thiết (theo nguyên tắc Dependency Inversion).

public interface AlbumRepository {
    // Phương thức loadAlbums sử dụng Retrofit để gọi API trả về danh sách album
    void loadAlbums(Callback<AlbumList> callback);
}
