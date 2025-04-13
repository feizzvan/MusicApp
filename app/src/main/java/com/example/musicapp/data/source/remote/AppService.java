package com.example.musicapp.data.source.remote;

import com.example.musicapp.data.model.album.AlbumList;
import com.example.musicapp.data.model.song.SongList;
import com.example.musicapp.data.model.artist.ArtistList;

import retrofit2.Call;
import retrofit2.http.GET;

// `AppService` là một interface dùng để định nghĩa các yêu cầu API tới server từ xa bằng cách sử dụng Retrofit.
// Ở đây, nó định nghĩa một phương thức để lấy danh sách album từ một URL cụ thể.

public interface AppService {
//    https://thantrieu.com/resources/braniumapis/playlist.json
    @GET("/resources/braniumapis/playlist.json")
    Call<AlbumList> getAlbums();

    @GET("/resources/braniumapis/songs.json")
    Call<SongList> getSongs();

    @GET("resources/braniumapis/artists.json")
    Call<ArtistList> getArtists();
}
