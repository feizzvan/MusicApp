package com.example.musicapp.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.utils.SharedDataUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicPlaybackService extends MediaSessionService {
    private ExoPlayer mExoPlayer;
    private MediaSession mMediaSession;
    private final MutableLiveData<Boolean> mIsMediaControllerInitialized = new MutableLiveData<>();
    private LocalBinder mBinder;
    private static MusicPlaybackService sInstance;

    public static MusicPlaybackService getInstance() {
        return sInstance;
    }

    @OptIn(markerClass = UnstableApi.class)
    public void setPlaylistAndPlay(int index, String playlistName) {
        List<Song> songs = SharedDataUtils.getPlaylistSongs(playlistName);
        if (songs != null && !songs.isEmpty()) {
            List<MediaItem> mediaItems = new ArrayList<>();
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            ProgressiveMediaSource.Factory mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);

            for (Song song : songs) {
                String url = song.getFullFileUrl();
                MediaItem item = MediaItem.fromUri(url);
                mediaItems.add(item);
            }

            if (mExoPlayer != null) {
                mExoPlayer.setMediaItems(mediaItems, index, 0);
                mExoPlayer.prepare();
                mExoPlayer.play();
                mIsMediaControllerInitialized.postValue(true);
                SharedDataUtils.setPlayingSong(index);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mExoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
        mMediaSession = new MediaSession.Builder(this, mExoPlayer).build();
        mBinder = new LocalBinder();
        mIsMediaControllerInitialized.postValue(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent) != null ? super.onBind(intent) : mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaSession != null) {
            mMediaSession.release();
        }
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        sInstance = null;
    }

    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mMediaSession;
    }

    public class LocalBinder extends Binder {
        public MediaSession getMediaSession() {
            return mMediaSession;
        }

        public LiveData<Boolean> getIsMediaControllerInitialized() {
            return mIsMediaControllerInitialized;
        }
    }
}