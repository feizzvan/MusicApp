package com.example.musicapp.ui.library.playlist.more;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.playlist.PlaylistWithSongs;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MorePlaylistViewModel extends ViewModel {
    private final MutableLiveData<List<PlaylistWithSongs>> mPlaylistLiveData = new MutableLiveData<>();

    @Inject
    public MorePlaylistViewModel() {
    }

    public void setPlaylistLiveData(List<PlaylistWithSongs> playlists) {
        mPlaylistLiveData.setValue(playlists);
    }

    public LiveData<List<PlaylistWithSongs>> getPlaylistLiveData() {
        return mPlaylistLiveData;
    }
}