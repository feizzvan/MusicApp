package com.example.musicapp.ui.home.album.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Album;
import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.model.Song;

import java.util.ArrayList;
import java.util.List;

public class DetailAlbumViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> mSongs = new MutableLiveData<>();
    private final MutableLiveData<Album> mAlbum = new MutableLiveData<>();
    private final MutableLiveData<List<Song>> mAlbumSongs = new MutableLiveData<>();
    private Playlist mPlaylist;

    public void extractSongList(Album album) {
        List<Song> songList = new ArrayList<>();
        mPlaylist = new Playlist();
        mPlaylist.setName(album.getName());
        List<Song> songs = mSongs.getValue();

        if (songs != null) {
            for (Song song : songs) {
                for (String id : album.getSongs()) {
                    if (song.getId().compareTo(id) == 0) {
                        songList.add(song);
                    }
                }
            }
        }

        mPlaylist.updateSongs(songList);
        mAlbumSongs.setValue(songList);
    }

    public void setSongs(List<Song> songs) {
        mSongs.setValue(songs);
    }

    public LiveData<List<Song>> getSongs() {
        return mSongs;
    }

    public void setAlbum(Album album) {
        mAlbum.setValue(album);
    }

    public LiveData<Album> getAlbum() {
        return mAlbum;
    }

    public LiveData<List<Song>> getAlbumSongs() {
        return mAlbumSongs;
    }

    public Playlist getPlaylist() {
        return mPlaylist;
    }

}
