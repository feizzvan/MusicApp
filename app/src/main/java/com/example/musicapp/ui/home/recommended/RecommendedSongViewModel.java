package com.example.musicapp.ui.home.recommended;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.model.song.SongList;
import com.example.musicapp.data.repository.song.SongRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class RecommendedSongViewModel extends ViewModel {
    private final SongRepositoryImpl mSongRepository;
    private final MutableLiveData<List<Song>> mSongList = new MutableLiveData<>();

    @Inject
    public RecommendedSongViewModel(SongRepositoryImpl songRepository) {
        mSongRepository = songRepository;
        loadSongs();
    }

    private void loadSongs() {
        mSongRepository.loadSongs(new Callback<SongList>() {
            @Override
            public void onResponse(@NonNull Call<SongList> call, @NonNull Response<SongList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body().getSongs();
                    setSong(songs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SongList> call, @NonNull Throwable throwable) {
                mSongList.postValue(new ArrayList<>());
            }
        });
    }

    Completable saveSongToDB(List<Song> songs){
        if(songs == null){
            return Completable.complete();
        }
        Song[] songArray = songs.toArray(new Song[0]);
        return mSongRepository.saveSongs(songArray);
    }

    public void setSong(List<Song> songs) {
        if (songs != null) {
            mSongList.postValue(songs);
        }
    }

    public LiveData<List<Song>> getSongList() {
        return mSongList;
    }

    public static class Factory implements ViewModelProvider.Factory{
        private final SongRepositoryImpl mSongRepository;

        @Inject
        public Factory(SongRepositoryImpl songRepository) {
            mSongRepository = songRepository;
        }

        @NonNull
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(RecommendedSongViewModel.class)) {
                return (T) new RecommendedSongViewModel(mSongRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
