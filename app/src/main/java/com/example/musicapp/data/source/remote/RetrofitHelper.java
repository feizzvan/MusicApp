package com.example.musicapp.data.source.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// `RetrofitHelper` là một lớp tiện ích để tạo một thể hiện của Retrofit với cấu hình sẵn.
// Mục tiêu là tách logic tạo và cấu hình Retrofit ra riêng để tái sử dụng và dễ dàng quản lý.

public abstract class RetrofitHelper {
    public static AppService getInstance() {
        String baseUrl = "https://thantrieu.com";
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AppService.class);
    }
}
