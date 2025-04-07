package com.example.musicapp.ui.home.album;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.Album;
import com.example.musicapp.data.model.AlbumList;
import com.example.musicapp.data.repository.album.AlbumRepository;
import com.example.musicapp.data.repository.album.AlbumRepositoryImpl;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewModel extends ViewModel {
    // LiveData để quản lý danh sách album
    private final MutableLiveData<List<Album>> mAlbumList = new MutableLiveData<>();
    // Repository để truy cập dữ liệu album
    private final AlbumRepository mAlbumRepository = new AlbumRepositoryImpl();

    public AlbumViewModel() {
        loadAlbumList();
    }

    // Đặt giá trị mới cho danh sách album
    public void setAlbumList(List<Album> albumList) {
        mAlbumList.postValue(albumList);
    }

    // Lấy danh sách album từ LiveData
    public LiveData<List<Album>> getAlbumList() {
        return mAlbumList;
    }

    // Tải danh sách album từ nguồn dữ liệu và cập nhật giá trị cho LiveData
    private void loadAlbumList() {
        // Thực hiện logic để tải danh sách album từ nguồn dữ liệu
        // Sau đó cập nhật giá trị cho mAlbumList
        mAlbumRepository.loadAlbums(new Callback<AlbumList>() {
            @Override
            public void onResponse(@NonNull Call<AlbumList> call, @NonNull Response<AlbumList> response) {
                AlbumList albumList = response.body();
                if (albumList != null) {
                    List<Album> albums = albumList.getAlbums();
                    albums.sort(Comparator.comparing(Album::getSize));
                    setAlbumList(Lists.reverse(albums));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AlbumList> call, @NonNull Throwable throwable) {
                // Khi thất bại, cập nhật danh sách album thành một danh sách trống
                mAlbumList.postValue(new ArrayList<>());
            }
        });
    }

}
