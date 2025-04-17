package com.example.musicapp.data.source.remote;

import androidx.annotation.NonNull;

import com.example.musicapp.data.model.album.AlbumList;
import com.example.musicapp.data.source.AlbumDataSource;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteAlbumDataSourceImpl implements AlbumDataSource.Remote {
    @Inject
    public RemoteAlbumDataSourceImpl() {
    }

    @Override
    public void loadAlbums(Callback<AlbumList> callback) {
        // Lấy một thể hiện của `AppService` từ `RetrofitHelper` để gọi API.
        AppService appService = RetrofitHelper.getInstance();
        Call<AlbumList> call = appService.getAlbums();

        // Gọi API và sử dụng enqueue để thực hiện yêu cầu bất đồng bộ.
        call.enqueue(new Callback<AlbumList>() {
            @Override
            public void onResponse(@NonNull Call<AlbumList> call, @NonNull Response<AlbumList> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<AlbumList> call, @NonNull Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });
    }
}
