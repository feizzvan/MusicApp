package com.example.musicapp;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.musicapp.data.model.PlayingSong;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.data.repository.RecentSongRepository;
import com.example.musicapp.data.repository.SongRepositoryImpl;
import com.example.musicapp.databinding.ActivityMainBinding;
import com.example.musicapp.databinding.FragmentMoreAlbumBinding;
import com.example.musicapp.ui.viewmodel.PermissionViewModel;
import com.example.musicapp.ui.viewmodel.SharedViewModel;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    public static final String PREF_SONG_ID = "com.example.musicapp.PREF_SONG_ID";
    public static final String PREF_PLAYLIST_NAME = "com.example.musicapp.PREF_PLAYLIST_NAME";

    private ActivityMainBinding mBinding;
    private SharedViewModel mSharedViewModel;
    private SharedPreferences mSharedPreferences;

//    private final ActivityResultLauncher<String> mResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    PermissionViewModel.getInstance().setPermissionGranted(isGranted);
//                } else {
//                    Snackbar snackbar = Snackbar.make(mBinding.getRoot(),
//                            R.string.message_permission_denied,
//                            Snackbar.LENGTH_LONG);
//                    snackbar.setAnchorView(R.id.bottom_nav_view);
//                    snackbar.show();
//                }
//            }
//    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupToolbar();
        setupViewModel();
        setupSharedPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedViewModel.getPlayingSong().observe(this, playingSong -> {
            if (playingSong != null) {
                if(playingSong.getSong() != null){ //Nếu bài hát có cả ở local và ở remote
                    mBinding.fcvMiniPlayer.setVisibility(View.VISIBLE);
                } else {
                    mBinding.fcvMiniPlayer.setVisibility(View.GONE);
                }
            } else {
                mBinding.fcvMiniPlayer.setVisibility(View.GONE);
            }
        });
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
            NavigationUI.setupWithNavController(mBinding.bottomNavView, navController);
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

//        PermissionViewModel.getInstance().getPermissionAsked().observe(this, isAsked -> {
//            if (isAsked) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    checkPermission();
//                }
//            }
//        });
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

//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU) //Đảm bảo cho android 13 trở lên
//    private void checkPermission() {
//        final String permission = android.Manifest.permission.POST_NOTIFICATIONS;
//        boolean isGranted = ActivityCompat.checkSelfPermission(this, permission)
//                == PackageManager.PERMISSION_GRANTED;
//        if (isGranted) {
//            PermissionViewModel.getInstance().setPermissionGranted(true);
//        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//            // Kiểm tra xem hệ thống có nên hiển thị lời giải thích về quyền trước khi yêu cầu lại không
//            Snackbar snackbar = Snackbar.make(mBinding.getRoot(),
//                    R.string.permission_description,
//                    Snackbar.LENGTH_LONG);
//            snackbar.setAnchorView(R.id.bottom_nav_view);
//            snackbar.setAction(R.string.action_agree, v -> mResultLauncher.launch(permission));
//            snackbar.show();
//        } else {
//            mResultLauncher.launch(permission);
//        }
//    }
}
