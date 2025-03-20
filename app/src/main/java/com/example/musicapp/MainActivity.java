package com.example.musicapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.musicapp.data.repository.RecentSongRepository;
import com.example.musicapp.data.repository.SongRepositoryImpl;
import com.example.musicapp.databinding.ActivityMainBinding;
import com.example.musicapp.ui.viewmodel.NowPlayingViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NowPlayingViewModel mNowPlayingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        setupViewModel();
    }

    private void setupToolbar(){
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration
        // .Builder(R.id.nav_home, R.id.nav_library, R.id.nav_discovery, R.id.nav_settings).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if(navHostFragment != null){
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        }
    }

    private void setupViewModel(){
        MusicApplication musicApplication = (MusicApplication) getApplication();
        RecentSongRepository recentSongRepository = musicApplication.getRecentSongRepository();
        SongRepositoryImpl songRepository = musicApplication.getSongRepository();
        NowPlayingViewModel.Factory factory = new NowPlayingViewModel.Factory(songRepository, recentSongRepository);
        mNowPlayingViewModel = new ViewModelProvider(this, factory).get(NowPlayingViewModel.class);
    }
}