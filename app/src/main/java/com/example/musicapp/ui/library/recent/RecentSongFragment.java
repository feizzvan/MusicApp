package com.example.musicapp.ui.library.recent;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.RECENT;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.databinding.FragmentRecentSongBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.library.LibraryFragmentDirections;
import com.example.musicapp.ui.library.recent.more.MoreRecentViewModel;
import com.example.musicapp.utils.AppUtils;

import java.util.List;

public class RecentSongFragment extends AppBaseFragment {
    private FragmentRecentSongBinding mBinding;
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
                    String playlistName = RECENT.getValue();
                    showAndPlay(song, index, playlistName);
                },
                this::showOptionMenu
        );
        mBinding.rvRecentSong.setAdapter(mRecentSongAdapter);
        MyLayoutManager layoutManager = new MyLayoutManager(
                requireContext(), 3, RecyclerView.HORIZONTAL, false
        );
        mBinding.rvRecentSong.setLayoutManager(layoutManager);
        mBinding.textTitleRecentSongs.setOnClickListener(view -> navigateToMoreRecentScreen());
        mBinding.btnMoreRecentSongs.setOnClickListener(view -> navigateToMoreRecentScreen());
    }

    private void setupViewModel() {
        RecentSongViewModel recentSongViewModel =
                new ViewModelProvider(requireActivity()).get(RecentSongViewModel.class);
        mMoreRecentViewModel =
                new ViewModelProvider(requireActivity()).get(MoreRecentViewModel.class);

        recentSongViewModel.getRecentSongs().observe(getViewLifecycleOwner(), recentSongs -> {
            mMoreRecentViewModel.setRecentSongs(recentSongs);
            List<Song> subList = recentSongs;
            if (recentSongs.size() >= 21) {
                subList = recentSongs.subList(0, 21);
            }
            mRecentSongAdapter.updateSongs(subList);
            mBinding.progressBarRecentSong.setVisibility(View.GONE);
        });
    }

    private void navigateToMoreRecentScreen() {
        NavDirections directions = LibraryFragmentDirections.actionLibraryFrToMoreRecentFr();
        NavHostFragment.findNavController(this).navigate(directions);
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