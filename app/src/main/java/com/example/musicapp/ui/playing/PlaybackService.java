package com.example.musicapp.ui.playing;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.example.musicapp.ui.viewmodel.NowPlayingViewModel;

// Lớp PlaybackService để cung cấp và quản lý phát nhạc cho ứng dụng
public class PlaybackService extends MediaSessionService {
    // ID của thông báo phát nhạc (dùng để cập nhật hoặc hủy thông báo khi cần thiết)
    public static final int NOTIFICATION_ID = 1;

    public static final String CHANNEL_ID = "music_app_notification_channel_id";

    // MediaSession quản lý các sự kiện điều khiển phát nhạc (play, pause, skip)
    private MediaSession mMediaSession;

    // Listener để theo dõi các thay đổi từ ExoPlayer
    private Player.Listener mPlayerListener = new Player.Listener() {

    };


    public void onCreate() {
        super.onCreate();
        // Khởi tạo MediaSession và ExoPlayer khi dịch vụ được tạo
        initSessionAndPlayer();
        // Thiết lập listener để theo dõi các sự kiện từ ExoPlayer
        setupListener();
    }

    @Nullable
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        // Cung cấp MediaSession cho hệ thống, cho phép điều khiển nhạc từ UI khác (thông báo, màn hình khóa)
        return mMediaSession;
    }

    @Override
    public void onDestroy() {
        mMediaSession.getPlayer().removeListener(mPlayerListener); // Xoá listener khỏi ExoPlayer
        mMediaSession.getPlayer().release(); // Giải phóng tài nguyên của ExoPlayer
        mMediaSession.release(); // Giải phóng tài nguyên của MediaSession
        mMediaSession = null;
        super.onDestroy();
    }

    private void initSessionAndPlayer() {
        // Tạo ExoPlayer với cấu hình mặc định
        ExoPlayer player = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true) // Thiết lập thuộc tính âm thanh mặc định,
                // cho phép nhạc tạm dừng khi có cuộc gọi hặc thông báo ưu tiên
                .build();

        // Tạo MediaSession và liên kết với ExoPlayer
        MediaSession.Builder mediaSessionBuilder = new MediaSession.Builder(this, player);
        //todo
        // Gán MediaSession đã được xây dựng vào biến mMediaSession
        mMediaSession = mediaSessionBuilder.build();
    }

    private void setupListener() {
        Player player = mMediaSession.getPlayer();
        mPlayerListener = new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                // Cập nhật chỉ số bài hát đang phát trong NowPlayingViewModel
                NowPlayingViewModel.getInstance().setPlayingSong(player.getCurrentMediaItemIndex());
            }

            // Gọi khi trạng thái phát nhạc thay đổi (bắt đầu hoặc tạm dừng).
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }
        };

        player.addListener(mPlayerListener);
    }
}