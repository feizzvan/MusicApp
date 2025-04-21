package com.example.musicapp.ui.library.playlist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.playlist.PlaylistSongCrossRef;
import com.example.musicapp.data.model.playlist.PlaylistWithSongs;
import com.example.musicapp.data.repository.playlist.PlaylistRepositoryImpl;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@HiltViewModel
public class PlaylistViewModel extends ViewModel {
    private final PlaylistRepositoryImpl mPlaylistRepository;
    private final MutableLiveData<List<PlaylistWithSongs>> mPlaylists = new MutableLiveData<>();

    // Tạo lập đối tượng cho PlaylistRepositoryImpl
    @Inject
    public PlaylistViewModel(PlaylistRepositoryImpl playlistRepository) {
        mPlaylistRepository = playlistRepository;
    }

    public LiveData<List<PlaylistWithSongs>> getPlaylists() {
        return mPlaylists;
    }

    public void setPlaylists(List<PlaylistWithSongs> playlists) {
        mPlaylists.postValue(playlists);
    }

    public Flowable<List<Playlist>> loadPlaylistInDB() {
        return mPlaylistRepository.getAll();
    }

    public Completable createPlaylist(String playlistName) {
        Playlist playlist = new Playlist(0, playlistName);
        return mPlaylistRepository.insert(playlist);
    }

    public Single<Playlist> getPlaylistByName(String playlistName) {
        return mPlaylistRepository.findByName(playlistName);
    }

    public Completable createPlaylistSongCrossRef(Playlist playlist, Song song){
        PlaylistSongCrossRef playlistSongCrossRef = new PlaylistSongCrossRef();
        playlistSongCrossRef.playlistId = playlist.getId();
        playlistSongCrossRef.songId = song.getId();
        return mPlaylistRepository.insertPlaylistSongCrossRef(playlistSongCrossRef);
    }

    public Flowable<List<PlaylistWithSongs>> getAllPlaylistWithSongs() {
        return mPlaylistRepository.getAllPlaylistWithSongs();
    }

    public Single<PlaylistWithSongs> getPlaylistWithSongs(int playlistId) {
        return mPlaylistRepository.findPlaylistWithSongByPlaylistId(playlistId);
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final PlaylistRepositoryImpl mPlaylistRepository;

        @Inject
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