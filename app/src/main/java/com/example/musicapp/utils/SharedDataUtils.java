package com.example.musicapp.utils;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.DEFAULT;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.RecentSong;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.model.playlist.PlaylistWithSongs;
import com.example.musicapp.data.repository.recent.RecentSongRepository;
import com.example.musicapp.data.repository.song.SongRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

// Trung tâm quản lý trạng thái phát nhạc
public final class SharedDataUtils {
    //Lưu trữ danh sách playlist
    private static final Map<String, Playlist> mPlaylistMap = new HashMap<>();

    //LiveData để lưu trữ dữ liệu Playlist hiện tại đang phát
    private static final MutableLiveData<Playlist> mPlaylistLiveData = new MutableLiveData<>();

    //LiveData để lưu trữ dữ liệu bài hát đang phát
    private static final MutableLiveData<PlayingSong> mPlayingSongLiveData = new MutableLiveData<>();

    //Tạo đối tượng rỗng, bài hát nào phát thì cập nhật vào đây
    private static final PlayingSong mPlayingSong = new PlayingSong();

    private static String mPlaylistName;

    //LiveData để lưu trữ index bài hát cần phát
    private static final MutableLiveData<Integer> mIndexToPlay = new MutableLiveData<>();

    private static final MutableLiveData<Boolean> isSongLoaded = new MutableLiveData<>();

    static {
        initPlaylists();
    }

    //Tạo danh sách playlist mặc định từ các giá trị có trong AppUtils.DefaultPlaylistName
    private static void initPlaylists() {
        for (AppUtils.DefaultPlaylistName playlistName : AppUtils.DefaultPlaylistName.values()) {
            //Tạo playlist với ID mặc định là -1 để tránh xung đột với các playlist khác
            Playlist playlist = new Playlist(-1, playlistName.getValue());
            mPlaylistMap.put(playlistName.getValue(), playlist);
        }
    }

    // Tải danh sách bài hát gần đây từ một nguồn dữ liệu thông qua RecentSongRepository
    public static Flowable<List<Song>> loadRecentSongs(RecentSongRepository recentSongRepository) {
        return recentSongRepository.getAllRecentSongs().map(ArrayList::new); // Kết quả trả về là một ArrayList
    }

    // Lưu một bài hát (Song) vào danh sách các bài hát gần đây bằng cách chuyển đổi bài hát đó
    // sang kiểu dữ liệu RecentSong và sử dụng repository (mRecentSongRepository) để chèn vào cơ sở dữ liệu.
    public static Completable saveRecentSong(Song song, RecentSongRepository recentSongRepository) {
        if (song == null) {
            return null;
        }

        RecentSong recentSong = new RecentSong.Builder(song).build();
        return recentSongRepository.insertRecentSong(recentSong);
    }

    public static Completable updateSongFavoriteStatus(Song song, SongRepository.Local songRepository) {
        return songRepository.updateSong(song);
    }

    public static Completable updateSongInDB(Song song, SongRepository.Local songRepository) {
        song.setCounter(song.getCounter() + 1);
        song.setReplay(song.getReplay() + 1);
        return songRepository.updateSong(song);
    }

    public static void setupPreviousSessionPlayingSong(String songId, String playlistName) {
        mPlaylistName = playlistName;
        int index;
        Playlist playlist = getPlaylist(playlistName);
        if (playlist == null) {
            playlist = getPlaylist(DEFAULT.getValue());
        }
        if (songId != null && playlistName != null) {
            //Gọi thiết lập playlist trước khi thực hiện các hành động khác
            //Để đảm bảo playlist đã tồn tại khi gọi thiết lập index to play
            boolean buildInPlaylist = isBuildInPlaylist(playlistName);
            if (buildInPlaylist) {
                setCurrentPlaylist(playlistName);
            } else {
                setCurrentPlaylist(DEFAULT.getValue());
            }

            mPlayingSong.setPlaylist(playlist);
            List<Song> songList = playlist.getSongs();
            index = songList.indexOf(new Song(songId));
            if (index >= 0 && index < songList.size()) {
                Song song = songList.get(index);
                mPlayingSong.setSong(song);
                mPlayingSong.setCurrentSongIndex(index);
            }
            setCurrentPlaylist(playlistName);
            setPlayingSong(mPlayingSong);
            setIndexToPlay(index);
        }
    }

    private static boolean isBuildInPlaylist(String playlistName) {
        for (AppUtils.DefaultPlaylistName defaultPlaylistName : AppUtils.DefaultPlaylistName.values()) {
            if (defaultPlaylistName.getValue().equals(playlistName)) {
                return true;
            }
        }

        return false;
    }

    public static void setPlaylistSongs(List<PlaylistWithSongs> playlistWithSongs) {
        if (playlistWithSongs != null) {
            for (PlaylistWithSongs element : playlistWithSongs) {
                Playlist playlist = element.playlist;
                playlist.updateSongs(element.songs);
                addPlaylist(playlist);
            }
        }
    }

    public static LiveData<Playlist> getCurrentPlaylist() {
        return mPlaylistLiveData;
    }

    public static void setCurrentPlaylist(String playlistName) {
        Playlist playlist = getPlaylist(playlistName);
        if (playlist != null) {
            mPlaylistLiveData.setValue(playlist);
            mPlayingSong.setPlaylist(playlist);
        }
    }

    public static Playlist getPlaylist(String playlistName) {
        // Trả về playlist dựa trên tên, nếu không tồn tại trả về null
        return mPlaylistMap.getOrDefault(playlistName, null);
    }

    public static LiveData<PlayingSong> getPlayingSong() {
        return mPlayingSongLiveData;
    }

    // Cập nhật bài hát đang phát
    public static void setPlayingSong(PlayingSong playingSong) {
        mPlayingSongLiveData.setValue(playingSong);
    }

    // Cập nhật danh sách bài hát cho playlist
    public static void setupPlaylist(List<Song> songs, String playlistName) {
        if (songs == null || songs.isEmpty()) {
            return;
        }

        //Lấy playlist cần cập nhật từ mPlaylistMap
        Playlist playlist = getPlaylist(playlistName);
        if (playlist != null) {
            playlist.updateSongs(songs);
            mPlaylistMap.put(playlistName, playlist);
        }
    }

    // Thêm một playlist mới vào danh sách nếu playlist đó chưa tồn tại.
    public static void addPlaylist(Playlist playlist) {
        if (!mPlaylistMap.containsKey(playlist.getName())) {
            mPlaylistMap.put(playlist.getName(), playlist);
        }
    }

    // Cập nhật bài hát đang phát dựa trên chỉ số (index) của bài hát trong playlist hiện tại và lưu trữ vào mPlayingSong
    public static void setPlayingSong(int index) {
        if (index > -1 && mPlayingSong.getPlaylist().getSongs().size() > index) {
            Song song = mPlayingSong.getPlaylist().getSongs().get(index);
            mPlayingSong.setSong(song);
            mPlayingSong.setCurrentSongIndex(index);
            mPlayingSongLiveData.setValue(mPlayingSong);
        }
    }

    // Lưu trữ chỉ số của bài hát đang phát trong playlist
    public static LiveData<Integer> getIndexToPlay() {
        return mIndexToPlay;
    }

    // Cập nhật chỉ số của bài hát cần phát
    public static void setIndexToPlay(int index) {
        mIndexToPlay.setValue(index);
    }

    public static LiveData<Boolean> isSongLoaded() {
        return isSongLoaded;
    }

    public static void setSongLoaded(boolean isLoaded) {
        isSongLoaded.setValue(isLoaded);
    }
}
