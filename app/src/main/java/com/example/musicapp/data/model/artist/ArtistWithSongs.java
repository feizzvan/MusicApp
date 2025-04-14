package com.example.musicapp.data.model.artist;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.musicapp.data.model.song.Song;

import java.util.List;

public class ArtistWithSongs {
    @Embedded
    public Artist artist;

    @Relation(
            parentColumn = "artist_id",
            entityColumn = "song_id",
            associateBy = @Junction(ArtistSongCrossRef.class)
    )
    public List<Song> songs;
}
