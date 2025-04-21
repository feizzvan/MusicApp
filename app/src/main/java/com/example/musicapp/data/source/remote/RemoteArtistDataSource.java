package com.example.musicapp.data.source.remote;

import androidx.annotation.NonNull;

import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.source.ArtistDataSource;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//triển khai RemoteArtistDataSource từ interface ArtistDataSource.Remote
// dùng để gọi API lấy danh sách nghệ sĩ từ server thông qua Retrofit.
public class RemoteArtistDataSource implements ArtistDataSource.Remote {
    @Inject
    public RemoteArtistDataSource() {
    }

    @Override
    public void loadArtists(Callback<ArtistList> callback) {
        AppService appService = RetrofitHelper.getInstance();
        Call<ArtistList> call = appService.getArtists();
        call.enqueue(new Callback<ArtistList>() {
            @Override
            public void onResponse(@NonNull Call<ArtistList> call, @NonNull Response<ArtistList> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<ArtistList> call, @NonNull Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });
    }
}
