package com.example.musicapp.data.source.remote;

import com.example.musicapp.data.model.album.Album;
import com.example.musicapp.data.model.album.AlbumById;
import com.example.musicapp.data.model.album.AlbumList;
import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.model.song.SongList;
import com.example.musicapp.data.network.request.LoginRequest;
import com.example.musicapp.data.network.request.RegisterRequest;
import com.example.musicapp.data.network.response.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

// `AppService` là một interface dùng để định nghĩa các yêu cầu API tới server từ xa bằng cách sử dụng Retrofit.
// Ở đây, nó định nghĩa một phương thức để lấy danh sách album từ một URL cụ thể.

public interface AppService {
//    https://thantrieu.com/resources/braniumapis/playlist.json
    @Headers("Content-Type: application/json")
    @POST("auth/sign-in")
    Call<AuthenticationResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<AuthenticationResponse> register(@Body RegisterRequest registerRequest);

    @GET("admin/songs/get-all")
    Call<SongList> getSongs();

    @GET("albums/get-all")
    Call<AlbumList> getAlbums();

    @GET("albums/get-by-id/{id}")
    Call<AlbumById> getAlbumById(@Path("id") int id);

//    @GET("/resources/braniumapis/songs.json")
//    Call<SongList> getSongs();

    @GET("resources/braniumapis/artists.json")
    Call<ArtistList> getArtists();
}
