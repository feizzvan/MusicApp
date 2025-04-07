package com.example.musicapp.ui.library.favorite.more;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentMoreFavoriteBinding;
import com.example.musicapp.ui.home.recommended.SongListAdapter;

public class MoreFavoriteFragment extends Fragment {
    private FragmentMoreFavoriteBinding mBinding;
    private SongListAdapter mSongListAdapter;
    private MoreFavoriteViewModel mMoreFavoriteViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMoreFavoriteBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    private void setupView() {
        mSongListAdapter = new SongListAdapter(
                (song, index) -> {

                },
                song -> {

                }
        );
        mBinding.includeMoreFavoriteSongList.rvSongList.setAdapter(mSongListAdapter);
        mBinding.toolbarMoreFavorite.setNavigationOnClickListener(view ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupViewModel() {
        mMoreFavoriteViewModel = new ViewModelProvider(requireActivity()).get(MoreFavoriteViewModel.class);
        mMoreFavoriteViewModel.getFavoriteSongs().observe(getViewLifecycleOwner(), songs -> {
            mSongListAdapter.updateSongs(songs);
        });
    }
}