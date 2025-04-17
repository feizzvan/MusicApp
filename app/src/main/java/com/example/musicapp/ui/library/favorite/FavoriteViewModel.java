package com.example.musicapp.ui.library.favorite;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.song.Song;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> mFavoriteSongs = new MutableLiveData<>();

    @Inject
    public FavoriteViewModel() {
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        mFavoriteSongs.setValue(favoriteSongs);
    }

    public LiveData<List<Song>> getFavoriteSongs() {
        return mFavoriteSongs;
    }
}