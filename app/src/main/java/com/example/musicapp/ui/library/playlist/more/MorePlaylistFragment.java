package com.example.musicapp.ui.library.playlist.more;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.databinding.FragmentMorePlaylistBinding;
import com.example.musicapp.ui.library.playlist.PlaylistAdapter;

public class MorePlaylistFragment extends Fragment {
    private FragmentMorePlaylistBinding mBinding;
    private PlaylistAdapter mAdapter;
    private MorePlaylistViewModel mMorePlaylistViewModel;

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

    private void setupView() {
        mBinding.toolbarMorePlaylist.setNavigationOnClickListener(view ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        mAdapter = new PlaylistAdapter(
                playlist -> {

                },
                playlist -> {

                });
        mBinding.rvMorePlaylist.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        mMorePlaylistViewModel = new ViewModelProvider(requireActivity()).get(MorePlaylistViewModel.class);
        mMorePlaylistViewModel.getPlaylistLiveData().observe(getViewLifecycleOwner(),
                playlists -> mAdapter.updatePlaylists(playlists));
    }

}