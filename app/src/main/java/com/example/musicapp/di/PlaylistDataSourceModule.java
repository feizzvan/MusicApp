package com.example.musicapp.di;

import com.example.musicapp.data.source.PlaylistDataSource;
import com.example.musicapp.data.source.local.playlist.LocalPlaylistDataSource;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class PlaylistDataSourceModule {
    @Binds
    public abstract PlaylistDataSource.Local bindLocalPlaylistDataSource(LocalPlaylistDataSource localPlaylistDataSource);
}
