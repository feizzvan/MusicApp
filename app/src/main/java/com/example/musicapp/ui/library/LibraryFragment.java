package com.example.musicapp.ui.library;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.FAVOURITE;
import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.RECENT;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MusicApplication;
import com.example.musicapp.databinding.FragmentLibraryBinding;
import com.example.musicapp.ui.library.favorite.FavoriteViewModel;
import com.example.musicapp.ui.library.playlist.PlaylistViewModel;
import com.example.musicapp.ui.library.recent.RecentSongViewModel;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LibraryFragment extends Fragment {
    private FragmentLibraryBinding mBinding;
    private LibraryViewModel mLibraryViewModel;
    private RecentSongViewModel mRecentSongViewModel;
    private FavoriteViewModel mFavoriteViewModel;
    private PlaylistViewModel mPlaylistViewModel;
    private SharedViewModel mSharedViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    private void setupViewModel() {
        MusicApplication musicApplication = (MusicApplication) requireActivity().getApplication();
        LibraryViewModel.Factory libraryFactory = new LibraryViewModel.Factory(
                musicApplication.getSongRepository(),
                musicApplication.getRecentSongRepository(),
                musicApplication.getPlaylistRepository()
        );
        PlaylistViewModel.Factory playlistFactory = new PlaylistViewModel.Factory(
                musicApplication.getPlaylistRepository()
        );

        mLibraryViewModel = new ViewModelProvider(requireActivity(), libraryFactory).get(LibraryViewModel.class);
        mSharedViewModel = SharedViewModel.getInstance();
        mRecentSongViewModel = new ViewModelProvider(requireActivity()).get(RecentSongViewModel.class);
        mFavoriteViewModel = new ViewModelProvider(requireActivity()).get(FavoriteViewModel.class);
        mPlaylistViewModel = new ViewModelProvider(requireActivity(), playlistFactory).get(PlaylistViewModel.class);

        mLibraryViewModel.getFavoriteSongs().observe(getViewLifecycleOwner(), songs -> {
            mFavoriteViewModel.setFavoriteSongs(songs);
            mSharedViewModel.setupPlaylist(songs, FAVOURITE.getValue());
        });
        mLibraryViewModel.getRecentSongs().observe(getViewLifecycleOwner(), songs -> {
            mRecentSongViewModel.setRecentSongs(songs);
            mSharedViewModel.setupPlaylist(songs, RECENT.getValue());
        });
    }

    private void setupObserver() {
        mDisposable.add(mLibraryViewModel.loadRecentSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> mLibraryViewModel.setRecentSongs(songs),
                        throwable -> mLibraryViewModel.setRecentSongs(null))
        );

        mDisposable.add(mLibraryViewModel.loadFavoriteSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> mLibraryViewModel.setFavoriteSongs(songs),
                        throwable -> mLibraryViewModel.setFavoriteSongs(null))
        );

        mDisposable.add(mLibraryViewModel.loadPlaylists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playlists -> mPlaylistViewModel.setPlaylists(playlists),
                        throwable -> mPlaylistViewModel.setPlaylists(null))
        );
    }
}