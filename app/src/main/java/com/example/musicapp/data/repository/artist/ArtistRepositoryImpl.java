package com.example.musicapp.data.repository.artist;

import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.source.ArtistDataSource;
import com.example.musicapp.data.source.remote.RemoteArtistDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import retrofit2.Callback;

public class ArtistRepositoryImpl implements ArtistRepository {
    private final ArtistDataSource.Remote remoteArtistDataSource = new RemoteArtistDataSource();
    private final ArtistDataSource.Local mLocalArtistDataSource;

    public ArtistRepositoryImpl(ArtistDataSource.Local localArtistDataSource) {
        mLocalArtistDataSource = localArtistDataSource;
    }

    @Override
    public void loadArtists(Callback<ArtistList> callback) {
        remoteArtistDataSource.loadArtists(callback);
    }

    @Override
    public Flowable<List<Artist>> getAllArtists() {
        return mLocalArtistDataSource.getAllArtists();
    }

    @Override
    public Flowable<List<Artist>> getTopNArtists(int limit) {
        return mLocalArtistDataSource.getTopNArtists(limit);
    }

    @Override
    public Completable insertArtist(List<Artist> artists) {
        return mLocalArtistDataSource.insertArtist(artists);
    }

    @Override
    public Completable updateArtist(Artist artist) {
        return mLocalArtistDataSource.updateArtist(artist);
    }

    @Override
    public Completable deleteArtist(Artist artist) {
        return mLocalArtistDataSource.deleteArtist(artist);
    }
}
