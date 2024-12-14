package com.example.musicapp.ui.home.recommended;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.model.SongList;
import com.example.musicapp.data.repository.SongRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedSongViewModel extends ViewModel {
    private final SongRepositoryImpl mSongRepository = new SongRepositoryImpl();
    private final MutableLiveData<List<Song>> mSongList = new MutableLiveData<>();

    public RecommendedSongViewModel() {
        loadSongs();
    }

    private void loadSongs(){
        mSongRepository.loadSongs(new Callback<SongList>() {
            @Override
            public void onResponse(@NonNull Call<SongList> call, @NonNull Response<SongList> response) {
                if(response.isSuccessful() && response.body() != null){
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

    public void setSong(List<Song> songs){
        if(songs != null){
            mSongList.postValue(songs);
        }
    }

    public LiveData<List<Song>> getSongList(){
        return mSongList;
    }
}
