package com.example.musicapp.data.source.local.artist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.musicapp.data.model.artist.Artist;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

//Định nghĩa hành động tương tác với database
@Dao
public interface ArtistDAO {
    @Query("SELECT * FROM artists")
    Flowable<List<Artist>> getAllArtists();

    @Query("SELECT * FROM artists ORDER BY interested DESC LIMIT :limit")
    Flowable<List<Artist>> getTopNArtists(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertArtist(List<Artist> artists);

    @Update
    Completable updateArtist(Artist artist);

    @Delete
    Completable deleteArtist(Artist artist);
}
