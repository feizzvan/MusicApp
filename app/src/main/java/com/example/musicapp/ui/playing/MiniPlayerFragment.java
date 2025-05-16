package com.example.musicapp.ui.playing;

import static android.app.Activity.RESULT_OK;
import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.SEARCHED;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;
import androidx.media3.session.MediaSession;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.repository.song.SongRepository;
import com.example.musicapp.databinding.FragmentMiniPlayerBinding;
import com.example.musicapp.service.MusicPlaybackService;
import com.example.musicapp.utils.AppUtils;
import com.example.musicapp.utils.SharedDataUtils;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MiniPlayerFragment extends Fragment implements View.OnClickListener {
    private FragmentMiniPlayerBinding mBinding;
    private MiniPlayerViewModel mMiniPlayerViewModel;
    private MediaSession mMediaSession;
    private Player.Listener mPlayerListener;
    private Animator mAnimator;
    private ObjectAnimator mRotationAnimator;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private float currentFraction = 0f;

    @Inject
    SongRepository.Local localSongRepository;

    private final ActivityResultLauncher<Intent> nowPlayingActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        float fraction = data.getFloatExtra(AppUtils.EXTRA_CURRENT_FRACTION, 0f);
                        mRotationAnimator.setCurrentFraction(currentFraction);
                        currentFraction = fraction;
                    } else {
                        currentFraction = 0f;
                    }
                }
            }
    );

    private final ServiceConnection mMusicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) iBinder;
            binder.getIsMediaControllerInitialized().observe(MiniPlayerFragment.this, isInitialized -> {
                if (isInitialized) {
                    if (mMediaSession == null) {
                        mMediaSession = binder.getMediaSession();
                        setupViewModel();
                        setMediaSession(mMediaSession);
                        setupObserveControllerData();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMediaSession = null;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMiniPlayerBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAnimator();
        setupListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(requireContext(), MusicPlaybackService.class);
        requireActivity().bindService(intent, mMusicServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            requireActivity().unbindService(mMusicServiceConnection);
        } catch (Exception e) {
            // Ignore if service is not bound
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaSession != null) {
            mMediaSession.getPlayer().removeListener(mPlayerListener);
        }
        mDisposable.dispose();
    }

    private void setupViewModel() {
        mMiniPlayerViewModel = new ViewModelProvider(this).get(MiniPlayerViewModel.class);

        SharedDataUtils.getCurrentPlaylist().observe(getViewLifecycleOwner(), playlist -> {
            PlayingSong playingSong = SharedDataUtils.getPlayingSong().getValue();
            Playlist currentPlaylist = null;
            if (playingSong != null) {
                currentPlaylist = playingSong.getPlaylist();
            }
            if (mMediaSession != null && (mMediaSession.getPlayer().getMediaItemCount() == 0
                    || playlist != null && playlist.getMediaItems() != null
                    && !playlist.getMediaItems().isEmpty()
                    && (currentPlaylist == null || currentPlaylist.getId() != playlist.getId()
                    || playlist.getMediaItems().size() != mMediaSession.getPlayer().getMediaItemCount()))
                    || (playlist != null && playlist.getName().compareTo(SEARCHED.getValue()) == 0)) {
                mMiniPlayerViewModel.setMediaItems(playlist.getMediaItems());
            }
        });

        SharedDataUtils.getPlayingSong().observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
                Song song = playingSong.getSong();
                showSongInfo(song);
            }
        });

        mMiniPlayerViewModel.isPlaying().observe(getViewLifecycleOwner(), this::updatePlayingState);
    }

    private void setupAnimator() {
        mAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.button_pressed);
        mRotationAnimator = ObjectAnimator
                .ofFloat(mBinding.imgMiniPlayerAvatar, "rotation", 0f, 360f);
        mRotationAnimator.setInterpolator(new LinearInterpolator());
        mRotationAnimator.setDuration(12000);
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
        intent.putExtra(AppUtils.EXTRA_CURRENT_FRACTION, mRotationAnimator.getAnimatedFraction());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeCustomAnimation(requireContext(), R.anim.slide_up, R.anim.fade_out);
        nowPlayingActivityLauncher.launch(intent, options);
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
            // setupFavorite();
        }
    }

    private void setupPlayPauseAction() {
        if (mMediaSession != null) {
            if (mMediaSession.getPlayer().isPlaying()) {
                mMediaSession.getPlayer().pause();
            } else {
                mMediaSession.getPlayer().play();
            }
        }
    }

    private void setupNextAction() {
        if (mMediaSession != null && mMediaSession.getPlayer().hasNextMediaItem()) {
            mMediaSession.getPlayer().seekToNext();
            mRotationAnimator.end();
        }
    }

    /*
    private void setupFavorite() {
        PlayingSong playingSong = SharedDataUtils.getPlayingSong().getValue();
        if (playingSong != null) {
            Song song = playingSong.getSong();
            song.setFavorite(!song.isFavorite());
            mDisposable.add(SharedDataUtils.updateSongFavoriteStatus(song, localSongRepository)
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
    */

    private void updatePlayingState(Boolean isPlaying) {
        if (isPlaying != null && isPlaying) {
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

    private void setMediaSession(MediaSession mediaSession) {
        mMediaSession = mediaSession;
        mPlayerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                mMiniPlayerViewModel.setIsPlaying(isPlaying);
            }
        };
        mMediaSession.getPlayer().addListener(mPlayerListener);
    }

    private void showSongInfo(Song song) {
        if (song != null) {
            Glide.with(this)
                    .load(song.getImageUrl())
                    .circleCrop()
                    .error(R.drawable.ic_music_note)
                    .into(mBinding.imgMiniPlayerAvatar);
            mBinding.textMiniPlayerTitle.setText(song.getTitle());
            mBinding.textMiniPlayerArtist.setText(song.getArtistId() != 0 ? String.valueOf(song.getArtistId()) : "Unknown");
            // updateFavoriteStatus(song);
        }
    }

    private void setupObserveControllerData() {
        if (mMediaSession != null && mMediaSession.getPlayer().isPlaying() && AppUtils.sIsConfigChanged) {
            AppUtils.sIsConfigChanged = false;
            return;
        }

        mMiniPlayerViewModel.getMediaItems().observe(getViewLifecycleOwner(), mediaItems -> {
            if (mMediaSession != null) {
                mMediaSession.getPlayer().setMediaItems(mediaItems);
            }
        });

        SharedDataUtils.getIndexToPlay().observe(getViewLifecycleOwner(), index -> {
            PlayingSong playingSong = SharedDataUtils.getPlayingSong().getValue();
            Playlist currentPlaylist = null;
            if (playingSong != null) {
                currentPlaylist = playingSong.getPlaylist();
            }
            Playlist playlist = SharedDataUtils.getCurrentPlaylist().getValue();
            if (mMediaSession != null && index > -1) {
                Boolean condition1 = mMediaSession.getPlayer().getMediaItemCount() > index
                        && mMediaSession.getPlayer().getCurrentMediaItemIndex() != index;
                Boolean condition2 = playlist != null && currentPlaylist != null
                        && mMediaSession.getPlayer().getCurrentMediaItemIndex() == index
                        && playlist.getId() != currentPlaylist.getId();
                Boolean condition3 = playlist != null
                        && playlist.getName().compareTo(SEARCHED.getValue()) == 0;
                if (condition1 || condition2 || condition3) {
                    mMediaSession.getPlayer().seekTo(index, 0);
                    mMediaSession.getPlayer().play();
                }
            }
        });
    }
}