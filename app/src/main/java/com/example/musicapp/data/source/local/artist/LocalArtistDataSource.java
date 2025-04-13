package com.example.musicapp.data.source.local.artist;

import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.source.ArtistDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class LocalArtistDataSource implements ArtistDataSource.Local {
    private final ArtistDAO mArtistDAO;

    public LocalArtistDataSource(ArtistDAO artistDAO) {
        mArtistDAO = artistDAO;
    }

    @Override
    public Flowable<List<Artist>> getAllArtists() {
        return mArtistDAO.getAllArtists();
    }

    @Override
    public Flowable<List<Artist>> getTopNArtists(int limit) {
        return mArtistDAO.getTopNArtists(limit);
    }

    @Override
    public Completable insertArtist(List<Artist> artists) {
        return mArtistDAO.insertArtist(artists);
    }

    @Override
    public Completable updateArtist(Artist artist) {
        return mArtistDAO.updateArtist(artist);
    }

    @Override
    public Completable deleteArtist(Artist artist) {
        return mArtistDAO.deleteArtist(artist);
    }
}
