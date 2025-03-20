package com.example.musicapp.ui.home.recommended;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.RECOMMENDED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MusicApplication;
import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentRecommendedBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.home.album.detail.DetailAlbumViewModel;
import com.example.musicapp.ui.home.recommended.more.MoreRecommendedFragment;
import com.example.musicapp.ui.home.recommended.more.MoreRecommendedViewModel;
import com.example.musicapp.ui.viewmodel.NowPlayingViewModel;

public class RecommendedFragment extends AppBaseFragment {
    private FragmentRecommendedBinding mBinding;
    private SongListAdapter mSongListAdapter;
    private RecommendedSongViewModel mRecommendedSongViewModel;
    private MoreRecommendedViewModel mMoreRecommendedViewModel;
    private DetailAlbumViewModel mDetailAlbumViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRecommendedBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        setupViewModel();
    }

    private void setupView() {
        mBinding.progressRecommendedSong.setVisibility(View.VISIBLE);
        mSongListAdapter = new SongListAdapter(
                (song, index) -> showAndPlay(song, index, RECOMMENDED.getValue()),
                this::showMenuOption);
        mBinding.includeRecommendedSongs.recyclerSongList.setAdapter(mSongListAdapter);
        mDetailAlbumViewModel = new ViewModelProvider(requireActivity()).get(DetailAlbumViewModel.class);
        mBinding.btnMoreRecommendedSongs.setOnClickListener(view -> navigateToMoreRecommended());
        mBinding.textTitleRecommendedSongs.setOnClickListener(view -> navigateToMoreRecommended());
    }

    private void setupViewModel() {
        MusicApplication musicApplication = (MusicApplication) requireActivity().getApplication();
        RecommendedSongViewModel.Factory factory = new RecommendedSongViewModel.Factory(musicApplication.getSongRepository());
        mRecommendedSongViewModel = new ViewModelProvider(this, factory).get(RecommendedSongViewModel.class);
        mMoreRecommendedViewModel = new ViewModelProvider(requireActivity()).get(MoreRecommendedViewModel.class);

//        mDetailAlbumViewModel =
//                new ViewModelProvider(requireActivity()).get(DetailAlbumViewModel.class);
//        mRecommendedSongViewModel =
//                new ViewModelProvider(requireActivity()).get(RecommendedSongViewModel.class);
//        mMoreRecommendedViewModel =
//                new ViewModelProvider(requireActivity()).get(MoreRecommendedViewModel.class);

        mRecommendedSongViewModel.getSongList().observe(getViewLifecycleOwner(), songs -> {
            mDetailAlbumViewModel.setSongs(songs);
            mSongListAdapter.updateSongs(songs.subList(0, 15));
            mMoreRecommendedViewModel.setSongs(songs);
            NowPlayingViewModel.getInstance().setupPlaylist(songs, RECOMMENDED.getValue());
            mBinding.progressRecommendedSong.setVisibility(View.GONE);
        });
    }

    private void navigateToMoreRecommended() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MoreRecommendedFragment.class, null)
                .addToBackStack(null)
                .commit();
    }
}