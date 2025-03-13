package com.example.musicapp.ui.playing;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentMiniPlayerBinding;
import com.example.musicapp.ui.viewmodel.MediaPlayerViewModel;
import com.example.musicapp.ui.viewmodel.NowPlayingViewModel;

public class MiniPlayerFragment extends Fragment {
    private FragmentMiniPlayerBinding mBinding;
    private MiniPlayerViewModel mMiniPlayerViewModel;
    // MediaController để điều khiển trình phát nhạc
    private MediaController mMediaController;
    // Listener để lắng nghe các thay đổi trạng thái của trình phát nhạc
    private Player.Listener mPlayerListener;
    private Animator mAnimator;
    private ObjectAnimator mRotationAnimator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMiniPlayerBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Thiết lập ViewModel và lắng nghe dữ liệu thay đổi
        setupViewModel();
        setupView();
        setupAnimator();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Khi Fragment bị hủy, xóa listener khỏi MediaController để tránh rò rỉ bộ nhớ
        if (mMediaController != null) {
            mMediaController.removeListener(mPlayerListener);
        }
    }

    private void setupViewModel() {
        mMiniPlayerViewModel = MiniPlayerViewModel.getInstance();
        // Quan sát MediaItem để cập nhật bài hát cho MediaController
        mMiniPlayerViewModel.getMediaItems().observe(getViewLifecycleOwner(), mediaItems -> {
            if (mMediaController != null) {
                mMediaController.setMediaItems(mediaItems);
            }
        });

        // Lấy danh sách bài hát hiện tại từ NowPlayingViewModel
        NowPlayingViewModel.getInstance()
                .getCurrentPlaylist()
                .observe(getViewLifecycleOwner(), playlist -> {
                    mMiniPlayerViewModel.setMediaItems(playlist.getMediaItems());
                });

        // Quan sát bài hát đang phát để hiển thị thông tin bài hát
        NowPlayingViewModel.getInstance()
                .getPlayingSong()
                .observe(getViewLifecycleOwner(), playingSong -> {
                    Song song = playingSong.getSong();
                    showSongInfo(song);
                });

        // Quan sát index bài hát để phát bài hát tương ứng trong danh sách bài hát hiện tại của MediaController
        NowPlayingViewModel.getInstance()
                .getIndexToPlay()
                .observe(getViewLifecycleOwner(), index -> {
                    if (index > -1 && mMediaController.getMediaItemCount() > index) {
                        mMediaController.seekTo(index, 0);
                        mMediaController.prepare();
                        mMediaController.play();
                    }
                });

        // Quan sát MediaController từ MediaPlayerViewModel để liên kết mini player với trình phát nhạc
        MediaPlayerViewModel.getInstance()
                .getMediaPlayerLiveData()
                .observe(getViewLifecycleOwner(), this::setMediaController);
        mMiniPlayerViewModel.isPlaying().observe(getViewLifecycleOwner(), this::updatePlayingState);
    }

    private void setupView() {
        mBinding.btnMiniPlayerPlayPause.setOnClickListener(view -> {
            if (mMediaController.isPlaying()) {
                mAnimator.setTarget(view);
                mAnimator.start();
                mMediaController.pause();
            } else {
                mMediaController.play();
            }
        });
        mBinding.btnMiniPlayerSkipNext.setOnClickListener(view -> {
            mAnimator.setTarget(view);
            mAnimator.start();
            mRotationAnimator.end();
            if (mMediaController.hasNextMediaItem()) {
                mMediaController.seekToNext();
            }
        });
    }

    private void setupAnimator() {
        mAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.button_pressed);
        mRotationAnimator = ObjectAnimator
                .ofFloat(mBinding.imgMiniPlayerAvatar, "rotation", 0f, 360f);
        mRotationAnimator.setInterpolator(new LinearInterpolator());
        mRotationAnimator.setDuration(10000);
        mRotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    private void updatePlayingState(Boolean isPlaying) {
        if (isPlaying) {
            mBinding.btnMiniPlayerPlayPause.setImageResource(R.drawable.ic_pause);
            if (mRotationAnimator.isPaused()) {
                mRotationAnimator.resume();
            } else if (!mRotationAnimator.isRunning()) {
                mRotationAnimator.start();
            }
        } else {
            mBinding.btnMiniPlayerPlayPause.setImageResource(R.drawable.ic_play);
            mRotationAnimator.pause();
        }
    }

    // Thiết lập MediaController và lắng nghe các thay đổi trạng thái phát nhạc
    private void setMediaController(MediaController mediaController) {
        // Lưu lại MediaController
        mMediaController = mediaController;
        // Tạo Listener để lắng nghe thay đổi trạng thái phát nhạc
        mPlayerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Khi trạng thái phát thay đổi, cập nhật vào ViewModel
                mMiniPlayerViewModel.setIsPlaying(isPlaying);
            }
        };
        // Nếu MediaController không null, thêm listener vào MediaController
        if (mMediaController != null) {
            mediaController.addListener(mPlayerListener);
        }
    }

    // Thiết lập MediaItem để MediaController phát bài hát
    private void setupMediaItem(MediaItem mediaItem) {
        if (mediaItem != null) {
            // Gửi bài hát đến MediaController
            mMediaController.setMediaItem(mediaItem);
            mMediaController.prepare();
            mMediaController.play();
        }
    }

    private void showSongInfo(Song song) {
        if (song != null) {
            Glide.with(this)
                    .load(song.getImage())
                    .circleCrop()
                    .error(R.drawable.ic_music_note)
                    .into(mBinding.imgMiniPlayerAvatar);
            mBinding.textMiniPlayerTitle.setText(song.getTitle());
            mBinding.textMiniPlayerArtist.setText(song.getArtist());
        }
    }
}