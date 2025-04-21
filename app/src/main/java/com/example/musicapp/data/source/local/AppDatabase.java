package com.example.musicapp.data.source.local;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;

import com.example.musicapp.data.model.album.Album;
import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.artist.ArtistSongCrossRef;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.RecentSong;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.model.playlist.PlaylistSongCrossRef;
import com.example.musicapp.data.source.local.playlist.PlaylistDAO;
import com.example.musicapp.data.source.local.recent.RecentSongDAO;
import com.example.musicapp.data.source.local.song.SongDAO;
import com.example.musicapp.data.source.local.artist.ArtistDAO;


@Database(
        entities = {
                Album.class,
                Playlist.class,
                Song.class,
                RecentSong.class,
                PlaylistSongCrossRef.class,
                Artist.class,
                ArtistSongCrossRef.class
        },
        version = 4,
        exportSchema = true,
        autoMigrations = {
                @AutoMigration(
                        from = 3,
                        to = 4,
                        spec = AppDatabase.DbMigrationSpec.class)
        }
)
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

    public abstract ArtistDAO artistDAO();

    static class DbMigrationSpec implements AutoMigrationSpec {

    }
}
