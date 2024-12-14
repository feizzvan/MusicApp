package com.example.musicapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.utils.AppUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Trung tâm quản lý trạng thái phát nhạc
public class NowPlayingViewModel extends ViewModel {
    //Tạo singleton để tránh truy cập không cần thiết
    private static volatile NowPlayingViewModel sInstance;

    //Lưu trữ danh sách playlist
    private final Map<String, Playlist> mPlaylistMap = new HashMap<>();

    //LiveData để lưu trữ dữ liệu Playlist hiện tại đang phát
    private final MutableLiveData<Playlist> mPlaylistLiveData = new MutableLiveData<>();

    //LiveData để lưu trữ dữ liệu bài hát đang phát
    private final MutableLiveData<PlayingSong> mPlayingSongLiveData = new MutableLiveData<>();

    //Tạo đối tượng rỗng, bài hát nào phát thì cập nhật vào đây
    private final PlayingSong mPlayingSong = new PlayingSong();
    
    //LiveData để lưu trữ index bài hát cần phát
    private final MutableLiveData<Integer> mIndexToPlay = new MutableLiveData<>();

    public static NowPlayingViewModel getInstance() {
        return sInstance;
    }

    private NowPlayingViewModel() {
        //Đảm bảo an toàn trong môi trường đa luồng
        synchronized (this) {
            if (sInstance == null) {
                sInstance = this;
            }
        }

        initPlaylists();
    }

    //Tạo danh sách playlist mặc định từ các giá trị có trong AppUtils.DefaultPlaylistName
    private void initPlaylists() {
        for (AppUtils.DefaultPlaylistName playlistName : AppUtils.DefaultPlaylistName.values()) {
            //Tạo playlist vơ ID mặc định là -1 để tránh xung đột với các playlist khác
            Playlist playlist = new Playlist(-1, playlistName.getValue());
            mPlaylistMap.put(playlistName.getValue(), playlist);
        }
    }

    public LiveData<Playlist> getCurrentPlaylist() {
        return mPlaylistLiveData;
    }

    public void setCurrentPlaylist(String playlistName) {
        Playlist playlist = getPlaylist(playlistName);
        if (playlist != null) {
            mPlaylistLiveData.setValue(playlist);
            mPlayingSong.setPlaylist(playlist);
        }
    }

    public Playlist getPlaylist(String playlistName) {
        // Trả về playlist dựa trên tên, nếu không tồn tại trả về null
        return mPlaylistMap.getOrDefault(playlistName, null);
    }

    public LiveData<PlayingSong> getPlayingSong() {
        return mPlayingSongLiveData;
    }

    // Cập nhật bài hát đang phát
    public void setPlayingSong(PlayingSong playingSong) {
        mPlayingSongLiveData.setValue(playingSong);
    }

    // Cập nhật danh sách bài hát cho playlist
    public void setupPlaylist(List<Song> songs, String playlistName) {
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
    public boolean addPlaylist(Playlist playlist) {
        // Kiểm tra xem playlist có tồn tại trong danh sách chưa
        if (!mPlaylistMap.containsKey(playlist.getName())) {
            //Thêm playlist, khi playlist tồn tại thì != null, != null cho thấy một playlist cũ được thay thế thành công
            return mPlaylistMap.put(playlist.getName(), playlist) != null;
        }

        // Nếu playlist đã tồn tại, cập nhật playlist vào danh sách
        return updatePlaylist(playlist);
    }

    // Cập nhật thông tin của một playlist đã tồn tại
    public boolean updatePlaylist(Playlist playlist) {
        return mPlaylistMap.put(playlist.getName(), playlist) != null;
    }

    // Cập nhật bài hát đang phát dựa trên chỉ số (index) của bài hát trong playlist hiện tại và lưu trữ vào mPlayingSong
    public void setPlayingSong(int index) {
        if (index > -1 && mPlayingSong.getPlaylist().getSongs().size() > index) {
            Song song = mPlayingSong.getPlaylist().getSongs().get(index);
            mPlayingSong.setSong(song);
            mPlayingSong.setCurrentSongIndex(index);
            mPlayingSongLiveData.setValue(mPlayingSong);
        }
    }

    // Lưu trữ chỉ số của bài hát đang phát trong playlist
    public LiveData<Integer> getIndexToPlay() {
        return mIndexToPlay;
    }

    // Cập nhật chỉ số của bài hát cần phát
    public void setIndexToPlay(int index) {
        mIndexToPlay.setValue(index);
    }
}
