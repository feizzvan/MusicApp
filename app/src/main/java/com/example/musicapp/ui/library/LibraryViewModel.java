package com.example.musicapp.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Song;

import java.util.List;

public class LibraryViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> mRecentSongs = new MutableLiveData<>();

    public void setRecentSongs(List<Song> recentSongs) {
        mRecentSongs.postValue(recentSongs);
    }

    public LiveData<List<Song>> getRecentSongs() {
        return mRecentSongs;
    }
}