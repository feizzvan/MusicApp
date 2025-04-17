package com.example.musicapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.session.MediaController;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MediaPlayerViewModel extends ViewModel {
    private static final MediaPlayerViewModel sInstance = new MediaPlayerViewModel();
    private final MutableLiveData<MediaController> mMediaPlayerLiveData = new MutableLiveData<>();

    @Inject
    public MediaPlayerViewModel() {

    }

    public void setMediaPlayer(MediaController mediaController) {
        mMediaPlayerLiveData.setValue(mediaController);
    }

    public LiveData<MediaController> getMediaPlayerLiveData() {
        return mMediaPlayerLiveData;
    }

    public static MediaPlayerViewModel getInstance() {
        return sInstance;
    }
}
