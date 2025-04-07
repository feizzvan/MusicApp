package com.example.musicapp.ui.library.recent.more;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Song;

import java.util.List;

public class MoreRecentViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> mRecentSongs = new MutableLiveData<>();

    public void setRecentSongs(List<Song> recentSongs) {
        mRecentSongs.setValue(recentSongs);
    }

    public LiveData<List<Song>> getRecentSongs() {
        return mRecentSongs;
    }
}