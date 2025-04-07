package com.example.musicapp.data.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.musicapp.data.model.Album;
import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.model.RecentSong;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.source.local.playlist.PlaylistDAO;
import com.example.musicapp.data.source.local.recent.RecentSongDAO;
import com.example.musicapp.data.source.local.song.SongDAO;

@Database(entities = {Album.class, Playlist.class, Song.class, RecentSong.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "music.db").build();
                }
            }
        }

        return sInstance;
    }

    public abstract SongDAO songDAO();

    public abstract RecentSongDAO recentSongDAO();

    public abstract PlaylistDAO playlistDAO();

    public abstract AlbumDAO albumDAO();

}
