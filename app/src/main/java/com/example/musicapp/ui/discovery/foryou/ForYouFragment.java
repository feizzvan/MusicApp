package com.example.musicapp.ui.discovery.foryou;

import static com.example.musicapp.utils.AppUtils.DefaultPlaylistName.FOR_YOU;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.databinding.FragmentForYouBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.discovery.DiscoveryFragmentDirections;
import com.example.musicapp.ui.home.recommended.SongListAdapter;
import com.example.musicapp.utils.SharedDataUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class ForYouFragment extends AppBaseFragment {
    private FragmentForYouBinding mBinding;
    private ForYouViewModel mForYouViewModel;
    private SongListAdapter mAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public ForYouViewModel.Factory factory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentForYouBinding.inflate(inflater, container, false);
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
        mAdapter = new SongListAdapter(
                (song, index) -> {
                    String playlistName = FOR_YOU.getValue();
                    showAndPlay(song, index, playlistName);
                }, this::showOptionMenu
        );
        mBinding.includeForYou.rvSongList.setAdapter(mAdapter);
        mBinding.textTitleForYou.setOnClickListener(view -> navigateToMoreForYou());
        mBinding.btnMoreForYou.setOnClickListener(view -> navigateToMoreForYou());
    }

    private void setupViewModel() {
        mForYouViewModel =
                new ViewModelProvider(requireActivity(), factory).get(ForYouViewModel.class);

        mForYouViewModel.getSongs().observe(getViewLifecycleOwner(), mAdapter::updateSongs);
        mDisposable.add(mForYouViewModel.loadTop15ForYouSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> {
                    mAdapter.updateSongs(songs);
                    mForYouViewModel.setSongs(songs);
                    SharedDataUtils.setupPlaylist(songs, FOR_YOU.getValue());
                }, throwable -> mForYouViewModel.setSongs(new ArrayList<>()))
        );
    }

    private void navigateToMoreForYou() {
        NavDirections directions = DiscoveryFragmentDirections.actionDiscoveryFrToMoreForYouFr();
        NavHostFragment.findNavController(this).navigate(directions);
    }
}