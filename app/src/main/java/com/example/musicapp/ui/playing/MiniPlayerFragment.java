package com.example.musicapp.ui.playing;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentMiniPlayerBinding;
import com.example.musicapp.ui.viewmodel.MediaPlayerViewModel;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MiniPlayerFragment extends Fragment implements View.OnClickListener {
    private FragmentMiniPlayerBinding mBinding;
    private MiniPlayerViewModel mMiniPlayerViewModel;
    // MediaController để điều khiển trình phát nhạc
    private MediaController mMediaController;
    // Listener để lắng nghe các thay đổi trạng thái của trình phát nhạc
    private Player.Listener mPlayerListener;
    private Animator mAnimator;
    private ObjectAnimator mRotationAnimator;
    private SharedViewModel mSharedViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

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
        setupAnimator();
        setupListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Khi Fragment bị hủy, xóa listener khỏi MediaController để tránh rò rỉ bộ nhớ
        if (mMediaController != null) {
            mMediaController.removeListener(mPlayerListener);
        }
        mDisposable.dispose();
    }

    private void setupViewModel() {
        mSharedViewModel = SharedViewModel.getInstance();
        mMiniPlayerViewModel = MiniPlayerViewModel.getInstance();

        // Lấy danh sách bài hát hiện tại từ NowPlayingViewModel
        mSharedViewModel.getCurrentPlaylist().observe(getViewLifecycleOwner(), playlist ->
                mMiniPlayerViewModel.setMediaItems(playlist.getMediaItems()));

        // Quan sát bài hát đang phát để hiển thị thông tin bài hát
        mSharedViewModel.getPlayingSong().observe(getViewLifecycleOwner(), playingSong -> {
            Song song = playingSong.getSong();
            showSongInfo(song);
        });

        // Quan sát MediaController từ MediaPlayerViewModel để liên kết Mini player với trình phát nhạc
        MediaPlayerViewModel.getInstance()
                .getMediaPlayerLiveData()
                .observe(getViewLifecycleOwner(), mediaPlayer -> {
                    if (mediaPlayer != null) {
                        setMediaController(mediaPlayer);
                        setupObserveControllerData();
                    }
                });
        mMiniPlayerViewModel.isPlaying().observe(getViewLifecycleOwner(), this::updatePlayingState);
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

    private void setupListener() {
        mBinding.getRoot().setOnClickListener(view -> navigateToNowPlaying());
        mBinding.btnMiniPlayerPlayPause.setOnClickListener(this);
        mBinding.btnMiniPlayerSkipNext.setOnClickListener(this);
        mBinding.btnMiniPlayerFavorite.setOnClickListener(this);
    }

    private void navigateToNowPlaying() {
        Intent intent = new Intent(requireContext(), NowPlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        requireContext().startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        mAnimator.setTarget(view);
        mAnimator.start();
        if (view.getId() == R.id.btn_mini_player_play_pause) {
            setupPlayPauseAction();
        } else if (view.getId() == R.id.btn_mini_player_skip_next) {
            setupNextAction();
        } else if (view.getId() == R.id.btn_mini_player_favorite) {
            setupFavorite();
        }
    }

    private void setupPlayPauseAction() {
        if (mMediaController.isPlaying()) {
            mMediaController.pause();
        } else {
            mMediaController.prepare();
            mMediaController.play();
        }
    }

    private void setupNextAction() {
        mRotationAnimator.end();
        if (mMediaController.hasNextMediaItem()) {
            mMediaController.seekToNext();
        }
    }

    private void setupFavorite() {
        PlayingSong playingSong = mSharedViewModel.getPlayingSong().getValue();
        if (playingSong != null) {
            Song song = playingSong.getSong();
            song.setFavorite(!song.isFavorite());
            mDisposable.add(mSharedViewModel.updateSongFavoriteStatus(song)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> updateFavoriteStatus(song)));
        }
    }

    private void updateFavoriteStatus(Song song) {
        if (song.isFavorite()) {
            mBinding.btnMiniPlayerFavorite.setImageResource(R.drawable.ic_favorite_on);
        } else {
            mBinding.btnMiniPlayerFavorite.setImageResource(R.drawable.ic_favorite_off);
        }
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
        mPlayerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Khi trạng thái phát thay đổi, cập nhật vào ViewModel
                mMiniPlayerViewModel.setIsPlaying(isPlaying);
            }
        };
        // Thêm listener vào MediaController
        mMediaController.addListener(mPlayerListener);
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
            updateFavoriteStatus(song);
        }
    }

    private void setupObserveControllerData() {
        // Quan sát MediaItem để cập nhật bài hát cho MediaController
        mMiniPlayerViewModel.getMediaItems().observe(getViewLifecycleOwner(), mediaItems -> {
            if (mMediaController != null) {
                mMediaController.setMediaItems(mediaItems);
            }
        });

        // Quan sát index bài hát để phát bài hát tương ứng trong danh sách bài hát hiện tại của MediaController
        mSharedViewModel.getIndexToPlay().observe(getViewLifecycleOwner(), index -> {
            if (index > -1 && mMediaController != null && mMediaController.getMediaItemCount() > index) {
                mMediaController.seekTo(index, 0);
                mMediaController.prepare();
            }
        });
    }
}