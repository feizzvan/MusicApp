package com.example.musicapp.ui.library.playlist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.repository.playlist.PlaylistRepositoryImpl;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


public class PlaylistViewModel extends ViewModel {
    private final PlaylistRepositoryImpl mPlaylistRepository;
    private final MutableLiveData<List<Playlist>> mPlaylists = new MutableLiveData<>();

    // Tạo lập đối tượng cho PlaylistRepositoryImpl
    public PlaylistViewModel(PlaylistRepositoryImpl playlistRepository) {
        mPlaylistRepository = playlistRepository;
    }

    public LiveData<List<Playlist>> getPlaylists() {
        return mPlaylists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        mPlaylists.postValue(playlists);
    }

    public Completable createPlaylist(String playlistName) {
        Playlist playlist = new Playlist(0, playlistName);
        return mPlaylistRepository.insert(playlist);
    }

    public Single<Playlist> getPlaylistByName(String playlistName) {
        return mPlaylistRepository.findByName(playlistName);
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final PlaylistRepositoryImpl mPlaylistRepository;

        public Factory(PlaylistRepositoryImpl playlistRepository) {
            mPlaylistRepository = playlistRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if(modelClass.isAssignableFrom(PlaylistViewModel.class)) {
                return (T) new PlaylistViewModel(mPlaylistRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

    }
}