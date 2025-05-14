package com.example.musicapp.data.source.remote;

import com.example.musicapp.data.model.auth.AuthResponse;
import com.example.musicapp.data.model.album.AlbumList;
import com.example.musicapp.data.model.auth.User;
import com.example.musicapp.data.model.song.SongList;
import com.example.musicapp.data.model.artist.ArtistList;
import com.example.musicapp.data.network.request.LoginRequest;
import com.example.musicapp.data.network.request.RegisterRequest;
import com.example.musicapp.data.network.response.AuthenticationResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// `AppService` là một interface dùng để định nghĩa các yêu cầu API tới server từ xa bằng cách sử dụng Retrofit.
// Ở đây, nó định nghĩa một phương thức để lấy danh sách album từ một URL cụ thể.

public interface AppService {
//    https://thantrieu.com/resources/braniumapis/playlist.json


    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    AppService apiService = new Retrofit.Builder()
            .baseUrl("localhost:8080/api/v1")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AppService.class);

    @POST("/auth/sign-in")
    Call<AuthenticationResponse> login(@Body LoginRequest loginRequest);

    @POST("/auth/register")
    Call<AuthenticationResponse> register(@Body RegisterRequest registerRequest);

    @GET("/resources/braniumapis/playlist.json")
    Call<AlbumList> getAlbums();

    @GET("/resources/braniumapis/songs.json")
    Call<SongList> getSongs();

    @GET("resources/braniumapis/artists.json")
    Call<ArtistList> getArtists();


}
