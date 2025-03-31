package com.example.musicapp.ui.playing;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.ActivityNowPlayingBinding;
import com.example.musicapp.ui.dialog.OptionMenuViewModel;
import com.example.musicapp.ui.dialog.SongOptionMenuDialogFragment;
import com.example.musicapp.ui.viewmodel.MediaPlayerViewModel;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

public class NowPlayingActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityNowPlayingBinding mBinding;
    private NowPlayingViewModel mNowPlayingViewModel;
    private SharedViewModel mSharedViewModel;
    private MediaController mMediaController;
    private Player.Listener mPlayerListener;
    private Handler mHandler;
    private Runnable mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNowPlayingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupView();
        setupToolbar();
        setupViewModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaController != null) {
            mMediaController.removeListener(mPlayerListener);
        }
        mHandler.removeCallbacks(mCallback);
        mMediaController = null;
        mPlayerListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_play_pause) {
            setupActionPlayPause();
        }
    }

    private void setupView() {
        mBinding.btnPlayPause.setOnClickListener(this);
        mBinding.seekBarNowPlaying.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String currentTimeLabel = mNowPlayingViewModel.getTimeLabel(progress);
                mBinding.textCurrentDuration.setText(currentTimeLabel);
                if (fromUser) {
                    mMediaController.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mBinding.toolbarNowPlaying);
        mBinding.toolbarNowPlaying
                .setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        mBinding.btnNowPlayingMoreOption.setOnClickListener(v -> showOptionMenuDialog());
    }

    private void showOptionMenuDialog() {
        OptionMenuViewModel optionMenuViewModel =
                new ViewModelProvider(this).get(OptionMenuViewModel.class);
        SongOptionMenuDialogFragment dialogFragment = SongOptionMenuDialogFragment.newInstance();
        Song song = null;
        if (mSharedViewModel.getPlayingSong().getValue() != null) {
            song = mSharedViewModel.getPlayingSong().getValue().getSong();
        }
        optionMenuViewModel.setSong(song);
        dialogFragment.show(getSupportFragmentManager(), SongOptionMenuDialogFragment.TAG);
    }

    private void setupViewModel() {
        mNowPlayingViewModel = new ViewModelProvider(this).get(NowPlayingViewModel.class);
        MediaPlayerViewModel mediaPlayerViewModel = MediaPlayerViewModel.getInstance();
        mediaPlayerViewModel.getMediaPlayerLiveData().observe(this, mediaPlayer -> {
            mMediaController = mediaPlayer;
            setupController();
            updateSeekBar();
        });
        mSharedViewModel = SharedViewModel.getInstance();
        mSharedViewModel.getPlayingSong().observe(this, playingSong -> {
            if (playingSong != null) {
                Song song = playingSong.getSong();
                showSongInfo(song);
            }
        });
        mNowPlayingViewModel.isPlaying().observe(this, isPlaying -> {
            if (isPlaying) {
                mBinding.btnPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                mBinding.btnPlayPause.setImageResource(R.drawable.ic_play);
            }
        });
    }

    private void setupController() {
        registerMediaController();
        if (mMediaController != null) {
            if (!mMediaController.isPlaying()) {
                mMediaController.prepare();
                mMediaController.play();
            }
            if (mMediaController.isPlaying()) {
                mNowPlayingViewModel.setIsPlaying(true);
            }
        }
    }

    //Lắng nghe sự kiện từ MediaController để cập nhật UI
    private void registerMediaController() {
        if (mMediaController != null) {
            mPlayerListener = new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    mNowPlayingViewModel.setIsPlaying(isPlaying);
                }

                @Override
                public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                    updateSeekBarMaxValue();
                    updateDuration();
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        updateSeekBarMaxValue();
                        updateDuration();
                    }
                }
            };
            mMediaController.addListener(mPlayerListener);
        }
    }

    private void showSongInfo(Song song) {
        updateSeekBarMaxValue();
        updateDuration();
        mBinding.textNowPlayingAlbum.setText(song.getAlbum());
        mBinding.textNowPlayingSongTitle.setText(song.getTitle());
        mBinding.textNowPlayingSongArtist.setText(song.getArtist());
        Glide.with(this)
                .load(song.getImage())
                .circleCrop()
                .error(R.drawable.ic_album)
                .into(mBinding.imageNowPlayingArtwork);
    }

    private void setupActionPlayPause() {
        if (mMediaController != null) {
            if (mMediaController.isPlaying()) {
                mMediaController.pause();
            } else {
                mMediaController.play();
            }
        }
    }

    //Cập nhật thanh SeekBar theo vị trí hiện tại của bài hát
    private void updateSeekBar() {
        mHandler = new Handler();
        mCallback = new Runnable() {
            @Override
            public void run() {
                if (mMediaController != null) {
                    int currentPos = (int) mMediaController.getCurrentPosition();
                    mBinding.seekBarNowPlaying.setProgress(currentPos);
                }
                mHandler.postDelayed(this, 1000);
            }
        };

        mHandler.post(mCallback);
    }

    //Đặt giá trị tối đa của SeekBar dựa trên thời lượng của bài hát
    private void updateSeekBarMaxValue() {
        long duration = mMediaController.getDuration();
        int seekBarMaxValue = 0;
        if (duration <= Integer.MAX_VALUE) {
            seekBarMaxValue = (int) duration;
        }
        mBinding.seekBarNowPlaying.setMax(seekBarMaxValue);
    }

    private void updateDuration() {
        String timeLabel = mNowPlayingViewModel.getTimeLabel(mMediaController.getDuration());
        mBinding.textTotalDuration.setText(timeLabel);
    }
}