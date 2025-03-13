package com.example.musicapp.data.source.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicapp.data.model.Song;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SongDAO {
    @Query("SELECT * FROM songs")
    Single<Song> getAllSongs();

    @Query("SELECT * FROM songs WHERE song_id = :songId")
    Flowable<Song> getSongById(int songId); // Dùng cho tập dữ liệu có thể phát sinh hoặc thay đổi theo thời gian

    @Insert
    Completable insertSong(Song... song);

    @Delete
    Completable deleteSong(Song song); // Không trả về giá trị khi hoàn thành

    @Update
    Completable updateSong(Song song);
}
