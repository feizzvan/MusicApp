package com.example.musicapp.ui.library.playlist.more;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MusicApplication;
import com.example.musicapp.R;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.databinding.FragmentMorePlaylistBinding;
import com.example.musicapp.ui.library.playlist.PlaylistAdapter;
import com.example.musicapp.ui.library.playlist.PlaylistViewModel;
import com.example.musicapp.ui.library.playlist.detail.PlaylistDetailFragment;
import com.example.musicapp.ui.library.playlist.detail.PlaylistDetailViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MorePlaylistFragment extends Fragment {
    private FragmentMorePlaylistBinding mBinding;
    private PlaylistAdapter mAdapter;
    private MorePlaylistViewModel mMorePlaylistViewModel;
    private PlaylistViewModel mPlaylistViewModel;
    private PlaylistDetailViewModel mPlaylistDetailViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMorePlaylistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    private void setupView() {
        mBinding.toolbarMorePlaylist.setNavigationOnClickListener(view ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        mAdapter = new PlaylistAdapter(
                playlist -> {
                    loadPlaylist(playlist);
                },
                playlist -> {

                });
        mBinding.rvMorePlaylist.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        MusicApplication application = (MusicApplication) requireActivity().getApplication();
        PlaylistViewModel.Factory factory = new PlaylistViewModel.Factory(application.getPlaylistRepository());
        mPlaylistViewModel = new ViewModelProvider(requireActivity(), factory).get(PlaylistViewModel.class);
        mPlaylistDetailViewModel = new ViewModelProvider(requireActivity()).get(PlaylistDetailViewModel.class);
        mMorePlaylistViewModel = new ViewModelProvider(requireActivity()).get(MorePlaylistViewModel.class);
        mMorePlaylistViewModel.getPlaylistLiveData().observe(getViewLifecycleOwner(),
                playlists -> mAdapter.updatePlaylists(playlists));
    }

    private void loadPlaylist(Playlist playlist) {
        mDisposable.add(mPlaylistViewModel.getPlaylistWithSongs(playlist.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mPlaylistDetailViewModel.setPlaylistWithSongs(result);
                    navigateToPlaylistDetail();
                }, error -> {
                })
        );
    }

    private void navigateToPlaylistDetail() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, PlaylistDetailFragment.class, null)
                .addToBackStack(null)
                .commit();
    }

}