package com.example.musicapp.ui.library.recent;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentRecentSongBinding;
import com.example.musicapp.ui.library.LibraryViewModel;
import com.example.musicapp.ui.viewmodel.SharedViewModel;
import com.example.musicapp.utils.AppUtils;

public class RecentSongFragment extends Fragment {
    private FragmentRecentSongBinding mBinding;
    private LibraryViewModel mLibraryViewModel;
    private RecentSongViewModel mRecentSongViewModel;
    private RecentSongAdapter mRecentSongAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentRecentSongBinding.inflate(inflater, container, false);
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
        mBinding.progressBarRecentSong.setVisibility(View.VISIBLE);
        mRecentSongAdapter = new RecentSongAdapter(
                (song, index) -> {

                },
                (song) -> {

                }
        );
        mBinding.rvRecentSong.setAdapter(mRecentSongAdapter);
        MyLayoutManager layoutManager = new MyLayoutManager(
                requireContext(), 3, RecyclerView.HORIZONTAL, false
        );
        mBinding.rvRecentSong.setLayoutManager(layoutManager);
    }

    private void setupViewModel() {
        mLibraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        mRecentSongViewModel = new ViewModelProvider(requireActivity()).get(RecentSongViewModel.class);
        mLibraryViewModel.getRecentSongs().observe(getViewLifecycleOwner(), recentSongs -> {
            mRecentSongViewModel.setRecentSongs(recentSongs);
        });
        mRecentSongViewModel.getRecentSongs().observe(getViewLifecycleOwner(), recentSongs -> {
            mRecentSongAdapter.updateSongs(recentSongs);
            mBinding.progressBarRecentSong.setVisibility(View.GONE);
        });
    }

    static class MyLayoutManager extends GridLayoutManager {
        public MyLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
            int dx = (int) (48 * AppUtils.X_DPI / 160);
            lp.width = getWidth() - dx;
            return true;
        }
    }
}