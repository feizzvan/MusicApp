package com.example.musicapp.ui.viewmodel;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.DEFAULT;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.Playlist;
import com.example.musicapp.data.model.RecentSong;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.repository.RecentSongRepository;
import com.example.musicapp.data.repository.SongRepositoryImpl;
import com.example.musicapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

// Trung tâm quản lý trạng thái phát nhạc
public class SharedViewModel extends ViewModel {
    private final SongRepositoryImpl mSongRepository;
    private final RecentSongRepository mRecentSongRepository;

    //Tạo singleton để tránh truy cập không cần thiết
    private static volatile SharedViewModel sInstance;

    //Lưu trữ danh sách playlist
    private final Map<String, Playlist> mPlaylistMap = new HashMap<>();

    //LiveData để lưu trữ dữ liệu Playlist hiện tại đang phát
    private final MutableLiveData<Playlist> mPlaylistLiveData = new MutableLiveData<>();

    //LiveData để lưu trữ dữ liệu bài hát đang phát
    private final MutableLiveData<PlayingSong> mPlayingSongLiveData = new MutableLiveData<>();

    //Tạo đối tượng rỗng, bài hát nào phát thì cập nhật vào đây
    private final PlayingSong mPlayingSong = new PlayingSong();

    private String mPlaylistName;

    //LiveData để lưu trữ index bài hát cần phát
    private final MutableLiveData<Integer> mIndexToPlay = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isSongLoaded = new MutableLiveData<>();

    public static SharedViewModel getInstance() {
        return sInstance;
    }

    // Constructor này được gọi khi không có đối tượng RecentSongRepository được cung cấp.
    private SharedViewModel() {
        this(null, null);
    }

    private SharedViewModel(SongRepositoryImpl songRepository, RecentSongRepository recentSongRepository) {
        mSongRepository = songRepository;
        // Liên kết NowPlayingViewModel với repository để truy cập dữ liệu bài hát gần đây.
        mRecentSongRepository = recentSongRepository;
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

    // Tải danh sách bài hát gần đây từ một nguồn dữ liệu thông qua RecentSongRepository
    public Flowable<List<Song>> loadRecentSongs() {
        return mRecentSongRepository.getAllRecentSongs().map(ArrayList::new); // Kết quả trả về là một ArrayList
    }

    // Lưu một bài hát (Song) vào danh sách các bài hát gần đây bằng cách chuyển đổi bài hát đó
    // sang kiểu dữ liệu RecentSong và sử dụng repository (mRecentSongRepository) để chèn vào cơ sở dữ liệu.
    public Completable saveRecentSong(Song song) {
        if (song == null) {
            return null;
        }

        RecentSong recentSong = new RecentSong.Builder(song).build();
        return mRecentSongRepository.insertRecentSong(recentSong);
    }

    public Completable updateSongFavoriteStatus(Song song) {
        return mSongRepository.updateSong(song);
    }

    public Completable updateSongInDB(Song song) {
        song.setCounter(song.getCounter() + 1);
        song.setReplay(song.getReplay() + 1);
        return mSongRepository.updateSong(song);
    }

    public void setupPrefPlayingSong(String songId, String playlistName) {
        mPlaylistName = playlistName;
        int index;
        Playlist playlist = getPlaylist(playlistName);
        if (playlist == null) {
            playlist = getPlaylist(DEFAULT.getValue());
        }
        if (songId != null && playlistName != null) {
            mPlayingSong.setPlaylist(playlist);
            List<Song> songList = playlist.getSongs();
            index = songList.indexOf(new Song(songId));
            if(index >= 0 && index < songList.size()){
                Song song = songList.get(index);
                mPlayingSong.setSong(song);
                mPlayingSong.setCurrentSongIndex(index);
            }
            setCurrentPlaylist(playlistName);
            setPlayingSong(mPlayingSong);
            setIndexToPlay(index);
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

    public LiveData<Boolean> isSongLoaded() {
        return isSongLoaded;
    }

    public void setSongLoaded(boolean isSongLoaded) {
        this.isSongLoaded.setValue(isSongLoaded);
    }

    // Factory để tạo ra một instance của NowPlayingViewModel
    public static class Factory implements ViewModelProvider.Factory {
        private final SongRepositoryImpl mSongRepository;
        private final RecentSongRepository mRecentSongRepository;

        // Factory sẽ nhận tham số cần thiết để khởi tạo NowPlayingViewModel.
        // Sau đó, đối tượng NowPlayingViewModel sẽ có thể sử dụng mRecentSongRepository để tương tác với cơ sở dữ liệu hoặc bộ nhớ.
        public Factory(SongRepositoryImpl songRepository, RecentSongRepository recentSongRepository) {
            mSongRepository = songRepository;
            mRecentSongRepository = recentSongRepository;
        }

        // tạo ra một đối tượng ViewModel (NowPlayingViewModel)
        @NonNull
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            // Kiểm tra xem loại lớp modelClass có phải là NowPlayingViewModel không
            // Phương thức này đảm bảo rằng Factory chỉ tạo ra đối tượng của đúng loại ViewModel mà nó hỗ trợ (NowPlayingViewModel
            if (modelClass.isAssignableFrom(SharedViewModel.class)) {
                // Nếu kiểm tra kiểu lớp thành công, Factory sẽ khởi tạo và trả về một đối tượng NowPlayingViewModel
                // với tham số mRecentSongRepository đã được truyền vào. Phương thức này sẽ trả về đối tượng NowPlayingViewModel với kiểu động (T).
                return (T) new SharedViewModel(mSongRepository, mRecentSongRepository);
            }

            // Nếu modelClass không phải là NowPlayingViewModel,
            // phương thức sẽ ném ra một ngoại lệ IllegalArgumentException với thông báo rằng lớp ViewModel không xác định.
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
