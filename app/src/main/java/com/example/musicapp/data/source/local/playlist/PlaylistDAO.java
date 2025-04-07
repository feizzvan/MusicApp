package com.example.musicapp.data.source.local.playlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicapp.data.model.Playlist;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PlaylistDAO {
    @Query("SELECT * FROM playlists")
    Flowable<List<Playlist>> getAll();

    @Query("SELECT * FROM playlists WHERE name = :name")
    Single<Playlist> findByName(String name);

    @Query("INSERT INTO playlists(name, artwork, created_at) VALUES(:name, :artwork, :createdAt)")
    Completable insert(String name, String artwork, Date createdAt);

    @Update
    Completable update(Playlist playlist);

    @Delete
    Completable delete(Playlist playlist);
}
