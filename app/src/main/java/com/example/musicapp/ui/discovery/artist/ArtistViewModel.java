package com.example.musicapp.ui.discovery.artist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.repository.artist.ArtistRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistViewModel extends ViewModel {
    private final ArtistRepository mArtistRepository;
    private final MutableLiveData<List<Artist>> mArtists = new MutableLiveData<>();
    private final MutableLiveData<List<Artist>> mLocalArtists = new MutableLiveData<>();

    public ArtistViewModel(ArtistRepository artistRepository) {
        mArtistRepository = artistRepository;
        loadArtists();
    }

    public void setArtist(List<Artist> artists) {
        mArtists.postValue(artists);
    }

    public LiveData<List<Artist>> getArtist() {
        return mArtists;
    }

    public LiveData<List<Artist>> getLocalArtists() {
        return mLocalArtists;
    }

    public void setLocalArtists(List<Artist> artists) {
        mLocalArtists.postValue(artists);
    }

    //load từ trên Internet
    public void loadArtists() {
        mArtistRepository.loadArtists(new Callback<ArtistList>() {
            @Override
            public void onResponse(@NonNull Call<ArtistList> call, @NonNull Response<ArtistList> response) {
                if (response.isSuccessful()) {
                    ArtistList artistList = response.body();
                    if (artistList != null) {
                        setArtist(artistList.artists);
                    }
                } else {
                    setArtist(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArtistList> call, @NonNull Throwable throwable) {
                mArtists.postValue(new ArrayList<>());
            }
        });
    }

    public Completable saveArtistToLocalDB(List<Artist> artists) {
        return mArtistRepository.insertArtist(artists);
    }

    public Flowable<List<Artist>> loadLocalArtists() {
        return mArtistRepository.getTopNArtists(15);
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final ArtistRepository mRepository;

        public Factory(ArtistRepository repository) {
            mRepository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ArtistViewModel.class)) {
                return (T) new ArtistViewModel(mRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
        }
    }
}