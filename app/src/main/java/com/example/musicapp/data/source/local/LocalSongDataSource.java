package com.example.musicapp.data.source.local;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.source.SongDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class LocalSongDataSource implements SongDataSource.Local {
    private final SongDAO mSongDAO;

    public LocalSongDataSource(SongDAO songDAO) {
        this.mSongDAO = songDAO;
    }

    @Override
    public Single<List<Song>> getSongs() {
        return mSongDAO.getAllSongs();
    }

    @Override
    public Completable saveSongs(Song... songs) {
        return mSongDAO.insertSongs(songs);
    }

    @Override
    public Completable updateSong(Song song) {
        return mSongDAO.updateSong(song);
    }
}
