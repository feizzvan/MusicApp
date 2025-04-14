package com.example.musicapp.ui.discovery.foryou.more;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.FOR_YOU;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MusicApplication;
import com.example.musicapp.data.repository.song.SongRepositoryImpl;
import com.example.musicapp.databinding.FragmentMoreForYouBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.home.recommended.SongListAdapter;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoreForYouFragment extends AppBaseFragment {
    private FragmentMoreForYouBinding mBinding;
    private MoreForYouViewModel mMoreForYouViewModel;
    private SongListAdapter mAdapter;
    private SongRepositoryImpl mSongRepository;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMoreForYouBinding.inflate(inflater, container, false);
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
        mBinding.toolbarMoreForYou.setNavigationOnClickListener(
                view -> requireActivity().getSupportFragmentManager().popBackStack());
        mAdapter = new SongListAdapter(
                (song, index) -> {
                    String playlistName = FOR_YOU.getValue();
                    showAndPlay(song, index, playlistName);
                }, this::showOptionMenu
        );
        mBinding.includeMoreForYouSongList.rvSongList.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        MusicApplication application = (MusicApplication) requireActivity().getApplication();
        mSongRepository = application.getSongRepository();
        MoreForYouViewModel.Factory factory = new MoreForYouViewModel.Factory(mSongRepository);
        mMoreForYouViewModel =
                new ViewModelProvider(requireActivity(), factory).get(MoreForYouViewModel.class);

        mMoreForYouViewModel.getSongs().observe(getViewLifecycleOwner(), mAdapter::updateSongs);

        mDisposable.add(mMoreForYouViewModel.loadTop40ForYouSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> {
                    mAdapter.updateSongs(songs);
                    mMoreForYouViewModel.setSongs(songs);
                    SharedViewModel.getInstance().setupPlaylist(songs, FOR_YOU.getValue());
                }, throwable -> mMoreForYouViewModel.setSongs(new ArrayList<>()))
        );
    }
}