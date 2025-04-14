package com.example.musicapp.ui.discovery.artist.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.artist.ArtistWithSongs;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.repository.artist.ArtistRepository;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

import io.reactivex.rxjava3.core.Single;

public class DetailArtistViewModel extends ViewModel {
    private final MutableLiveData<ArtistWithSongs> mArtistWithSongs = new MutableLiveData<>();
    private final ArtistRepository mArtistRepository;

    public DetailArtistViewModel(ArtistRepository artistRepository) {
        mArtistRepository = artistRepository;
    }

    public LiveData<ArtistWithSongs> getArtistWithSongs() {
        return mArtistWithSongs;
    }

    public void setArtistWithSongs(ArtistWithSongs artistWithSongs) {
        mArtistWithSongs.postValue(artistWithSongs);
    }

    public Single<ArtistWithSongs> getArtistWithSongs(int artistId) {
        return mArtistRepository.getArtistWithSongs(artistId);
    }

    public Playlist createPlaylist() {
        ArtistWithSongs artistWithSongs = mArtistWithSongs.getValue();
        if (artistWithSongs != null) {
            Playlist playlist = new Playlist(-1, artistWithSongs.artist.getName());
            playlist.updateSongs(artistWithSongs.songs);
            SharedViewModel.getInstance().addPlaylist(playlist);
            return playlist;
        }
        return null;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final ArtistRepository mRepository;

        public Factory(ArtistRepository repository) {
            mRepository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DetailArtistViewModel.class)) {
                return (T) new DetailArtistViewModel(mRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
        }
    }
}