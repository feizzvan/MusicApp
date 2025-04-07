package com.example.musicapp.ui.library.recent.more;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentMoreRecentBinding;
import com.example.musicapp.ui.home.recommended.SongListAdapter;

public class MoreRecentFragment extends Fragment {
    private FragmentMoreRecentBinding mBinding;
    private MoreRecentViewModel mMoreRecentViewModel;
    private SongListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMoreRecentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    private void setupView() {
        mBinding.toolbarMoreRecent.setNavigationOnClickListener(view ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        mAdapter = new SongListAdapter(
                (song, index) -> {

                },
                song -> {

                }
        );
        mBinding.includeMoreRecentSongList.rvSongList.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        mMoreRecentViewModel = new ViewModelProvider(requireActivity()).get(MoreRecentViewModel.class);
        mMoreRecentViewModel.getRecentSongs().observe(getViewLifecycleOwner(), songs ->
                mAdapter.updateSongs(songs));
    }
}