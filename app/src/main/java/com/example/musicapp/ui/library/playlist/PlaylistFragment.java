package com.example.musicapp.ui.library.playlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.playlist.PlaylistWithSongs;
import com.example.musicapp.databinding.FragmentPlaylistBinding;
import com.example.musicapp.ui.dialog.PlaylistCreationDialog;
import com.example.musicapp.ui.library.LibraryFragmentDirections;
import com.example.musicapp.ui.library.playlist.detail.PlaylistDetailViewModel;
import com.example.musicapp.ui.library.playlist.more.MorePlaylistViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class PlaylistFragment extends Fragment {
    private FragmentPlaylistBinding mBinding;
    private PlaylistAdapter mAdapter;
    private PlaylistViewModel mPlaylistViewModel;
    private MorePlaylistViewModel mMorePlaylistViewModel;
    private PlaylistDetailViewModel mPlaylistDetailViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public PlaylistViewModel.Factory factory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPlaylistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    private void setupView() {
        mAdapter = new PlaylistAdapter(
                this::loadPlaylist,
                playlist -> {

                }
        );
        mBinding.rvPlaylist.setAdapter(mAdapter);
        mBinding.includeBtnAddPlaylist.btnAddPlaylist.setOnClickListener(view -> createPlaylist());
        mBinding.includeBtnAddPlaylist.textAddPlaylist.setOnClickListener(view -> createPlaylist());
        mBinding.btnMorePlaylist.setOnClickListener(view -> navigateToMorePlaylist());
        mBinding.textTitlePlaylist.setOnClickListener(view -> navigateToMorePlaylist());
    }

    private void setupViewModel() {
        mPlaylistViewModel = new ViewModelProvider(requireActivity(), factory).get(PlaylistViewModel.class);
        mMorePlaylistViewModel = new ViewModelProvider(requireActivity()).get(MorePlaylistViewModel.class);
        mPlaylistDetailViewModel = new ViewModelProvider(requireActivity()).get(PlaylistDetailViewModel.class);

        mPlaylistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithSongs -> {
            List<PlaylistWithSongs> subList = new ArrayList<>();
            if (playlistWithSongs.size() > 10) {
                subList.subList(0, 10);
            } else {
                subList.addAll(playlistWithSongs);
            }
            mAdapter.updatePlaylists(subList);
            mMorePlaylistViewModel.setPlaylistLiveData(playlistWithSongs);
        });
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
        NavDirections directions = LibraryFragmentDirections.actionLibraryFrToPlaylistDetailFr();
        NavHostFragment.findNavController(this).navigate(directions);
    }

    private void createPlaylist() {
        PlaylistCreationDialog.PlaylistDialogListener listener = playlistName ->
                mDisposable.add(mPlaylistViewModel.getPlaylistByName(playlistName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> checkAndCreatePlaylist(result, playlistName),
                                error -> checkAndCreatePlaylist(null, playlistName))
                );

        PlaylistCreationDialog dialog = new PlaylistCreationDialog(listener);
        dialog.show(requireActivity().getSupportFragmentManager(), PlaylistCreationDialog.TAG);
    }

    private void checkAndCreatePlaylist(Playlist playlist, String playlistName) {
        if (playlist == null) {
            mDisposable.add(mPlaylistViewModel.createPlaylist(playlistName)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            );
        } else {
            String errorMessage = getString(R.string.error_playlist_already_exists);
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMorePlaylist() {
        NavDirections directions = LibraryFragmentDirections.actionLibraryFrToMorePlaylistFr();
        NavHostFragment.findNavController(this).navigate(directions);
    }
}