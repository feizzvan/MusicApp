package com.example.musicapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.repository.RecentSongRepository;
import com.example.musicapp.data.repository.SongRepositoryImpl;
import com.example.musicapp.databinding.ActivityMainBinding;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

public class MainActivity extends AppCompatActivity {
    public static final String PREF_SONG_ID = "com.example.musicapp.PREF_SONG_ID";
    public static final String PREF_PLAYLIST_NAME = "com.example.musicapp.PREF_PLAYLIST_NAME";

    private ActivityMainBinding binding;
    private SharedViewModel mSharedViewModel;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViewModel();
        setupSharedPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCurrentSong();
    }

    private void setupToolbar() {
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration
        // .Builder(R.id.nav_home, R.id.nav_library, R.id.nav_discovery, R.id.nav_settings).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        }
    }

    private void setupViewModel() {
        MusicApplication musicApplication = (MusicApplication) getApplication();
        RecentSongRepository recentSongRepository = musicApplication.getRecentSongRepository();
        SongRepositoryImpl songRepository = musicApplication.getSongRepository();
        SharedViewModel.Factory factory = new SharedViewModel.Factory(songRepository, recentSongRepository);
        mSharedViewModel = new ViewModelProvider(this, factory).get(SharedViewModel.class);
        mSharedViewModel.isSongLoaded().observe(this, isLoaded -> {
            if (isLoaded) {
                readPrefPlayingSong();
            }
        });
    }

    private void setupSharedPreferences() {
        mSharedPreferences = getApplicationContext()
                .getSharedPreferences("MUSIC_APP_PREFERENCES", MODE_PRIVATE);
    }

    private void saveCurrentSong() {
        PlayingSong playingSong = mSharedViewModel.getPlayingSong().getValue();
        if (playingSong != null) {
            Song song = playingSong.getSong();
            if (song != null) {
                mSharedPreferences.edit()
                        .putString("PREF_SONG_ID", song.getId())
                        .putString("PREF_PLAYLIST_NAME", playingSong.getPlaylist().getName())
                        .apply();
            }
        }
    }

    private void readPrefPlayingSong() {
        String songId = mSharedPreferences.getString(PREF_SONG_ID, null);
        String playlistName = mSharedPreferences.getString(PREF_PLAYLIST_NAME, null);
        if (songId != null && playlistName != null) {
            mSharedViewModel.setupPrefPlayingSong(songId, playlistName);
        }
    }
}
