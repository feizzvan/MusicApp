package com.example.musicapp.ui.library.favorite;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.song.Song;

import java.util.List;

public class FavoriteViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> mFavoriteSongs = new MutableLiveData<>();

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        mFavoriteSongs.setValue(favoriteSongs);
    }

    public LiveData<List<Song>> getFavoriteSongs() {
        return mFavoriteSongs;
    }
}