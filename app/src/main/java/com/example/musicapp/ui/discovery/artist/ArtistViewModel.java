package com.example.musicapp.ui.discovery.artist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.model.artist.ArtistSongCrossRef;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.repository.artist.ArtistRepository;
import com.example.musicapp.data.repository.song.SongRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ArtistViewModel extends ViewModel {
    private final ArtistRepository mArtistRepository;
    private final SongRepositoryImpl mSongRepository;
    private final MutableLiveData<List<Artist>> mArtists = new MutableLiveData<>();
    private final MutableLiveData<List<Artist>> mLocalArtists = new MutableLiveData<>();

    @Inject
    public ArtistViewModel(ArtistRepository artistRepository, SongRepositoryImpl songRepository) {
        mArtistRepository = artistRepository;
        mSongRepository = songRepository;
        loadArtists();
    }

    public void setArtist(List<Artist> artists) {
        mArtists.postValue(artists);
    }

    public LiveData<List<Artist>> getArtist() {
        return mArtists;
    }

    public void setLocalArtists(List<Artist> artists) {
        mLocalArtists.postValue(artists);
    }

    public LiveData<List<Artist>> getLocalArtists() {
        return mLocalArtists;
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

    public Single<List<Song>> loadAllSongs() {
        return mSongRepository.getSongs();
    }

    public Completable saveArtistSongCrossRef(List<Artist> artists, List<Song> songs) {
        if (artists != null) {
            List<ArtistSongCrossRef> artistSongCrossRefList = getArtistSongCrossRef(artists, songs);
            return mArtistRepository.insertArtistSongCrossRef(artistSongCrossRefList);
        }
        return Completable.complete();
    }

    public Flowable<List<Artist>> loadLocalNArtists() {
        return mArtistRepository.getTopNArtists(15);
    }

    public Flowable<List<Artist>> loadAllLocalArtists() {
        return mArtistRepository.getAllArtists();
    }

    private List<ArtistSongCrossRef> getArtistSongCrossRef(List<Artist> artists, List<Song> songs) {
        List<ArtistSongCrossRef> artistSongCrossRefList = new ArrayList<>();
        for (Artist artist : artists) {
            for (Song song : songs) {
                String key = ".*" + artist.getName().toLowerCase() + ".*";
//                if (song.getArtistId().toLowerCase().matches(key)) {
//                    artistSongCrossRefList.add(new ArtistSongCrossRef(artist.getId(), song.getId()));
//                }
            }
        }
        return artistSongCrossRefList;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final ArtistRepository mArtistRepository;
        private final SongRepositoryImpl mSongRepository;

        @Inject
        public Factory(ArtistRepository artistRepository, SongRepositoryImpl songRepository) {
            mArtistRepository = artistRepository;
            mSongRepository = songRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ArtistViewModel.class)) {
                return (T) new ArtistViewModel(mArtistRepository, mSongRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
        }
    }
}