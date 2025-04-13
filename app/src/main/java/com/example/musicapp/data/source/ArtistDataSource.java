package com.example.musicapp.data.source;

import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.artist.ArtistList;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import retrofit2.Callback;

//Định nghĩa các nguồn cho Artist
public interface ArtistDataSource {
    interface Local {
        Flowable<List<Artist>> getAllArtists();

        Flowable<List<Artist>> getTopNArtists(int limit);

        Completable insertArtist(List<Artist> artists);

        Completable updateArtist(Artist artist);

        Completable deleteArtist(Artist artist);
    }

    interface Remote {
        void loadArtists(Callback<ArtistList> callback);
    }
}
