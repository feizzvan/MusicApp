package com.example.musicapp.data.repository.artist;

import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.artist.ArtistList;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import retrofit2.Callback;

//định nghĩa các phương thức liên quan đến việc truy xuất dữ liệu nghệ sĩ (Artist).
public interface ArtistRepository {
    void loadArtists(Callback<ArtistList> callback);

    Flowable<List<Artist>> getAllArtists();

    Flowable<List<Artist>> getTopNArtists(int limit);

    Completable insertArtist(List<Artist> artists);

    Completable updateArtist(Artist artist);

    Completable deleteArtist(Artist artist);
}
