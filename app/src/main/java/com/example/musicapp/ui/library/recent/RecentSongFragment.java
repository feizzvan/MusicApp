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
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentRecentSongBinding;
import com.example.musicapp.ui.library.recent.more.MoreRecentFragment;
import com.example.musicapp.ui.library.recent.more.MoreRecentViewModel;
import com.example.musicapp.utils.AppUtils;

import java.util.List;

public class RecentSongFragment extends Fragment {
    private FragmentRecentSongBinding mBinding;
    private RecentSongViewModel mRecentSongViewModel;
    private RecentSongAdapter mRecentSongAdapter;
    private MoreRecentViewModel mMoreRecentViewModel;

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
        mBinding.textTitleRecentSongs.setOnClickListener(view -> navigateToMoreRecentScreen());
        mBinding.btnMoreRecentSongs.setOnClickListener(view -> navigateToMoreRecentScreen());
    }

    private void navigateToMoreRecentScreen() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MoreRecentFragment.class, null)
                .addToBackStack(null)
                .commit();
    }

    private void setupViewModel() {
        mRecentSongViewModel = new ViewModelProvider(requireActivity()).get(RecentSongViewModel.class);
        mMoreRecentViewModel = new ViewModelProvider(requireActivity()).get(MoreRecentViewModel.class);

        mRecentSongViewModel.getRecentSongs().observe(getViewLifecycleOwner(), recentSongs -> {
            mMoreRecentViewModel.setRecentSongs(recentSongs);
            List<Song> subList = recentSongs;
            if (recentSongs.size() >= 21) {
                subList = recentSongs.subList(0, 21);
            }
            mRecentSongAdapter.updateSongs(subList);
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