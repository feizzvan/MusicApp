package com.example.musicapp.ui.library.favorite.more;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Song;

import java.util.List;

public class MoreFavoriteViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> mFavoriteSongs = new MutableLiveData<>();

    public LiveData<List<Song>> getFavoriteSongs() {
        return mFavoriteSongs;
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        mFavoriteSongs.setValue(favoriteSongs);
    }
}