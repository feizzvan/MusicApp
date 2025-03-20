package com.example.musicapp.utils;

import android.content.Context;

import com.example.musicapp.data.repository.RecentSongRepository;
import com.example.musicapp.data.repository.RecentSongRepositoryImpl;
import com.example.musicapp.data.repository.SongRepositoryImpl;
import com.example.musicapp.data.source.RecentSongDataSource;
import com.example.musicapp.data.source.local.AppDatabase;
import com.example.musicapp.data.source.local.LocalRecentSongDataSource;
import com.example.musicapp.data.source.local.LocalSongDataSource;

// là lớp chứa các phương thức để cung cấp các đối tượng cần thiết cho ứng dụng,
public abstract class InjectionUtils {
    // Cung cấp RecentSongDataSource nguồn dữ liệu cho bài hát gần đây
    public static RecentSongDataSource provideRecentSongDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new LocalRecentSongDataSource(database.recentSongDAO());
    }

    // Cung cấp RecentSongRepositoryImpl để truy cập dữ liệu cho bài hát gần đây
    public static RecentSongRepository provideRecentSongRepository(RecentSongDataSource dataSource) {
        return new RecentSongRepositoryImpl(dataSource);
    }

    // Cung cấp LocalSongDataSource để truy cập dữ liệu cho bài hát trong máy
    public static LocalSongDataSource provideLocalSongDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new LocalSongDataSource(database.songDAO());
    }

    // Cung cấp SongRepositoryImpl để truy cập dữ liệu cho bài hát trong ứng dụng
    public static SongRepositoryImpl provideSongRepository(LocalSongDataSource dataSource) {
        return new SongRepositoryImpl(dataSource);
    }
}
