package com.example.musicapp.ui.playing;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaSession;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.repository.song.SongRepository;
import com.example.musicapp.databinding.ActivityNowPlayingBinding;
import com.example.musicapp.service.MusicPlaybackService;
import com.example.musicapp.ui.dialog.OptionMenuViewModel;
import com.example.musicapp.ui.dialog.SongOptionMenuDialogFragment;
import com.example.musicapp.utils.AppUtils;
import com.example.musicapp.utils.SharedDataUtils;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class NowPlayingActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityNowPlayingBinding mBinding;
    private NowPlayingViewModel mNowPlayingViewModel;
    private MediaSession mMediaSession;
    private Player.Listener mPlayerListener;
    private Handler mHandler;
    private Runnable mCallback;
    private Animator mAnimator;
    private ObjectAnimator mRotationAnimator;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    SongRepository.Local localSongRepository;

    private final ServiceConnection mMusicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) iBinder;
                binder.getIsMediaControllerInitialized().observe(NowPlayingActivity.this, isInitialized -> {
                    if (isInitialized) {
                        mMediaSession = binder.getMediaSession();
                        setupController();
                        updateSeekBar();
                        updateSeekBarMaxValue();
                        updateDuration();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(NowPlayingActivity.this, "Service connection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMediaSession = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNowPlayingBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(mBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.nowPlaying, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        setupView();
        setupAnimator();
        setupToolbar();
        setupViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlaybackService.class);
        try {
            bindService(intent, mMusicServiceConnection, BIND_AUTO_CREATE);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot bind to MusicPlaybackService", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unbindService(mMusicServiceConnection);
        } catch (Exception e) {
            // Ignore if service is not bound
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaSession != null) {
            mMediaSession.getPlayer().removeListener(mPlayerListener);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mCallback);
        }
        mMediaSession = null;
        mPlayerListener = null;
        mDisposable.clear();
    }

    @Override
    public void onClick(View view) {
        mAnimator.setTarget(view);
        mAnimator.start();
        if (view.getId() == R.id.btn_play_pause) {
            setupActionPlayPause();
        } else if (view.getId() == R.id.btn_skip_previous) {
            setupActionSkipPrevious();
        } else if (view.getId() == R.id.btn_skip_next) {
            setupActionSkipNext();
        } else if (view.getId() == R.id.btn_repeat) {
            setupActionRepeat();
        } else if (view.getId() == R.id.btn_shuffle) {
            setupActionShuffle();
        } else if (view.getId() == R.id.btn_now_playing_favorite) {
            //setupActionFavorite();
        }
    }

    private void setupView() {
        mBinding.btnPlayPause.setOnClickListener(this);
        mBinding.btnSkipPrevious.setOnClickListener(this);
        mBinding.btnSkipNext.setOnClickListener(this);
        mBinding.btnRepeat.setOnClickListener(this);
        mBinding.btnShuffle.setOnClickListener(this);
        mBinding.btnNowPlayingFavorite.setOnClickListener(this);

        mBinding.seekBarNowPlaying.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String currentTimeLabel = mNowPlayingViewModel.getTimeLabel(progress);
                mBinding.textCurrentDuration.setText(currentTimeLabel);
                if (fromUser && mMediaSession != null) {
                    mMediaSession.getPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupAnimator() {
        mAnimator = AnimatorInflater.loadAnimator(this, R.animator.button_pressed);
        mRotationAnimator = ObjectAnimator.ofFloat(mBinding.imageNowPlayingArtwork, "rotation", 0f, 360f);
        mRotationAnimator.setDuration(16000);
        mRotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotationAnimator.setInterpolator(new LinearInterpolator());

        float currentFraction = getIntent().getFloatExtra(AppUtils.EXTRA_CURRENT_FRACTION, 0f);
        mRotationAnimator.setCurrentFraction(currentFraction);
    }

    private void setupToolbar() {
        setSupportActionBar(mBinding.toolbarNowPlaying);
        mBinding.toolbarNowPlaying.setNavigationOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(AppUtils.EXTRA_CURRENT_FRACTION, mRotationAnimator.getAnimatedFraction());
            setResult(RESULT_OK, intent);
            getOnBackPressedDispatcher().onBackPressed();
        });
        mBinding.btnNowPlayingMoreOption.setOnClickListener(v -> showOptionMenuDialog());
    }

    private void showOptionMenuDialog() {
        OptionMenuViewModel optionMenuViewModel =
                new ViewModelProvider(this).get(OptionMenuViewModel.class);
        Song song = null;
        if (SharedDataUtils.getPlayingSong().getValue() != null) {
            song = SharedDataUtils.getPlayingSong().getValue().getSong();
        }
        optionMenuViewModel.setSong(song);
        SongOptionMenuDialogFragment dialogFragment = SongOptionMenuDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), SongOptionMenuDialogFragment.TAG);
    }

    private void setupViewModel() {
        mNowPlayingViewModel = new ViewModelProvider(this).get(NowPlayingViewModel.class);

        SharedDataUtils.getPlayingSong().observe(this, playingSong -> {
            if (playingSong != null) {
                Song song = playingSong.getSong();
                showSongInfo(song);
            } else {
                mBinding.textNowPlayingSongTitle.setText("No Song");
                mBinding.textNowPlayingSongArtist.setText("Unknown");
                mBinding.imageNowPlayingArtwork.setImageResource(R.drawable.ic_album);
            }
        });
        mNowPlayingViewModel.isPlaying().observe(this, isPlaying -> {
            if (isPlaying != null && isPlaying) {
                if (mRotationAnimator.isPaused()) {
                    mRotationAnimator.resume();
                } else if (!mRotationAnimator.isRunning()) {
                    mRotationAnimator.start();
                }
                mBinding.btnPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                mBinding.btnPlayPause.setImageResource(R.drawable.ic_play);
                mRotationAnimator.pause();
            }
        });
    }

    private void setupController() {
        if (mMediaSession == null) {
            Toast.makeText(this, "MediaSession not initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        registerMediaController();
        try {
            String playlistName = SharedDataUtils.getCurrentPlaylistName();
            int index = SharedDataUtils.getIndexToPlay().getValue() != null ? SharedDataUtils.getIndexToPlay().getValue() : 0;
            if (playlistName != null && !mMediaSession.getPlayer().isPlaying()) {
                MusicPlaybackService service = MusicPlaybackService.getInstance();
                if (service != null) {
                    service.setPlaylistAndPlay(index, playlistName);
                }
            }
            if (mMediaSession.getPlayer().isPlaying()) {
                mNowPlayingViewModel.setIsPlaying(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Cannot play song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerMediaController() {
        if (mMediaSession != null) {
            mPlayerListener = new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    mNowPlayingViewModel.setIsPlaying(isPlaying);
                }

                @Override
                public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                    updateSeekBarMaxValue();
                    updateDuration();
                    if (mMediaSession.getPlayer().isPlaying()) {
                        mRotationAnimator.start();
                    }
                    // Cập nhật chỉ số bài hát trong SharedDataUtils
                    int currentIndex = mMediaSession.getPlayer().getCurrentMediaItemIndex();
                    SharedDataUtils.setIndexToPlay(currentIndex);
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        updateSeekBarMaxValue();
                        updateDuration();
                    }
                }
            };
            mMediaSession.getPlayer().addListener(mPlayerListener);
        }
    }

    private void showSongInfo(Song song) {
        if (song != null) {
            updateSeekBarMaxValue();
            updateDuration();
            showRepeatMode();
            showShuffleMode();

            mBinding.textNowPlayingSongTitle.setText(song.getTitle());
            mBinding.textNowPlayingSongArtist.setText(song.getMArtistName() != null ? String.valueOf(song.getMArtistName()) : "Unknown");
            Glide.with(this)
                    .load(song.getImageUrl())
                    .circleCrop()
                    .error(R.drawable.ic_album)
                    .into(mBinding.imageNowPlayingArtwork);
        } else {
            mBinding.textNowPlayingSongTitle.setText("No Song");
            mBinding.textNowPlayingSongArtist.setText("Unknown");
            mBinding.imageNowPlayingArtwork.setImageResource(R.drawable.ic_album);
        }
    }

    private void setupActionPlayPause() {
        if (mMediaSession != null) {
            if (mMediaSession.getPlayer().isPlaying()) {
                mMediaSession.getPlayer().pause();
            } else {
                mMediaSession.getPlayer().play();
            }
        }
    }

    private void setupActionSkipPrevious() {
        if (mMediaSession != null && mMediaSession.getPlayer().hasPreviousMediaItem()) {
            mMediaSession.getPlayer().seekToPreviousMediaItem();
            mRotationAnimator.end();
        }
    }

    private void setupActionSkipNext() {
        if (mMediaSession != null && mMediaSession.getPlayer().hasNextMediaItem()) {
            mMediaSession.getPlayer().seekToNextMediaItem();
            mRotationAnimator.end();
        }
    }

    private void setupActionRepeat() {
        if (mMediaSession != null) {
            int repeatMode = mMediaSession.getPlayer().getRepeatMode();
            switch (repeatMode) {
                case Player.REPEAT_MODE_OFF:
                    mBinding.btnRepeat.setImageResource(R.drawable.ic_repeat_one);
                    mMediaSession.getPlayer().setRepeatMode(Player.REPEAT_MODE_ONE);
                    break;
                case Player.REPEAT_MODE_ONE:
                    mBinding.btnRepeat.setImageResource(R.drawable.ic_repeat_all);
                    mMediaSession.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
                    break;
                case Player.REPEAT_MODE_ALL:
                    mBinding.btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                    mMediaSession.getPlayer().setRepeatMode(Player.REPEAT_MODE_OFF);
                    break;
            }
        }
    }

    private void showRepeatMode() {
        if (mMediaSession != null) {
            int repeatMode = mMediaSession.getPlayer().getRepeatMode();
            switch (repeatMode) {
                case Player.REPEAT_MODE_OFF:
                    mBinding.btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                    break;
                case Player.REPEAT_MODE_ONE:
                    mBinding.btnRepeat.setImageResource(R.drawable.ic_repeat_one);
                    break;
                case Player.REPEAT_MODE_ALL:
                    mBinding.btnRepeat.setImageResource(R.drawable.ic_repeat_all);
                    break;
            }
        }
    }

    private void setupActionShuffle() {
        if (mMediaSession != null) {
            boolean isShuffle = mMediaSession.getPlayer().getShuffleModeEnabled();
            mMediaSession.getPlayer().setShuffleModeEnabled(!isShuffle);
            showShuffleMode();
        }
    }

    private void showShuffleMode() {
        if (mMediaSession != null) {
            boolean isShuffle = mMediaSession.getPlayer().getShuffleModeEnabled();
            if (isShuffle) {
                mBinding.btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
            } else {
                mBinding.btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
            }
        }
    }

    private void updateSeekBar() {
        mHandler = new Handler();
        mCallback = new Runnable() {
            @Override
            public void run() {
                if (mMediaSession != null) {
                    int currentPos = (int) mMediaSession.getPlayer().getCurrentPosition();
                    mBinding.seekBarNowPlaying.setProgress(currentPos);
                }
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.post(mCallback);
    }

    private void updateSeekBarMaxValue() {
        if (mMediaSession != null) {
            long duration = mMediaSession.getPlayer().getDuration();
            int seekBarMaxValue = duration <= Integer.MAX_VALUE ? (int) duration : 0;
            int progress = (int) mMediaSession.getPlayer().getCurrentPosition();
            mBinding.seekBarNowPlaying.setProgress(progress);
            mBinding.seekBarNowPlaying.setMax(seekBarMaxValue);
        }
    }

    private void updateDuration() {
        if (mMediaSession != null) {
            String timeLabel = mNowPlayingViewModel.getTimeLabel(mMediaSession.getPlayer().getDuration());
            mBinding.textTotalDuration.setText(timeLabel);
        }
    }
}