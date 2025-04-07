package com.example.musicapp.ui.library.favorite;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentFavoriteBinding;
import com.example.musicapp.ui.home.recommended.SongListAdapter;
import com.example.musicapp.ui.library.favorite.more.MoreFavoriteFragment;
import com.example.musicapp.ui.library.favorite.more.MoreFavoriteViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding mBinding;
    private FavoriteViewModel mFavoriteViewModel;
    private MoreFavoriteViewModel mMoreFavoriteViewModel;
    private SongListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFavoriteBinding.inflate(inflater, container, false);
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

    private void setupView() {
        mAdapter = new SongListAdapter(
                (song, index) -> {

                },
                (song) -> {

                }
        );
        mBinding.includeFavorite.rvSongList.setAdapter(mAdapter);
        mBinding.textTitleFavorite.setOnClickListener(view -> navigateToMoreFavorite());
        mBinding.btnMoreFavorite.setOnClickListener(view -> navigateToMoreFavorite());
    }

    private void navigateToMoreFavorite() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MoreFavoriteFragment.class, null)
                .addToBackStack(null)
                .commit();
    }

    private void setupViewModel() {
        mFavoriteViewModel = new ViewModelProvider(requireActivity()).get(FavoriteViewModel.class);
        mMoreFavoriteViewModel = new ViewModelProvider(requireActivity()).get(MoreFavoriteViewModel.class);

        mFavoriteViewModel.getFavoriteSongs().observe(getViewLifecycleOwner(), songs -> {
            mMoreFavoriteViewModel.setFavoriteSongs(songs);
            List<Song> subList = new ArrayList<>();
            if (songs != null)
                if (songs.size() < 10) {
                    subList.addAll(songs);
                } else {
                    subList.addAll(songs.subList(0, 10));
                }

//            List<Song> subList = songs;
//            if (songs.size() > 10) {
//                subList = songs.subList(0, 10);
//            }
            mAdapter.updateSongs(subList);
        });

    }
}