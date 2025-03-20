package com.example.musicapp.ui.home.recommended;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.RECOMMENDED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MusicApplication;
import com.example.musicapp.R;
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentRecommendedBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.home.album.detail.DetailAlbumViewModel;
import com.example.musicapp.ui.home.recommended.more.MoreRecommendedFragment;
import com.example.musicapp.ui.home.recommended.more.MoreRecommendedViewModel;
import com.example.musicapp.ui.viewmodel.NowPlayingViewModel;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecommendedFragment extends AppBaseFragment {
    private FragmentRecommendedBinding mBinding;
    private SongListAdapter mSongListAdapter;
    private RecommendedSongViewModel mRecommendedSongViewModel;
    private MoreRecommendedViewModel mMoreRecommendedViewModel;
    private DetailAlbumViewModel mDetailAlbumViewModel;
    CompositeDisposable mDisposable = new CompositeDisposable();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
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
        mRecommendedSongViewModel.getSongList().observe(getViewLifecycleOwner(), songs -> {
            saveSongToDB(songs);
            mDetailAlbumViewModel.setSongs(songs);
            mSongListAdapter.updateSongs(songs.subList(0, 15));
            mMoreRecommendedViewModel.setSongs(songs);
            NowPlayingViewModel.getInstance().setupPlaylist(songs, RECOMMENDED.getValue());
            mBinding.progressRecommendedSong.setVisibility(View.GONE);
        });
    }

    private void saveSongToDB(List<Song> songs) {
        mDisposable.add(mRecommendedSongViewModel.saveSongToDB(songs)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    private void navigateToMoreRecommended() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MoreRecommendedFragment.class, null)
                .addToBackStack(null)
                .commit();
    }
}