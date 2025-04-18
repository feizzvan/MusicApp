package com.example.musicapp.ui.home.album.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.album.Album;
import com.example.musicapp.databinding.FragmentDetailAlbumBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.home.recommended.SongListAdapter;
import com.example.musicapp.utils.SharedDataUtils;

public class DetailAlbumFragment extends AppBaseFragment {
    private FragmentDetailAlbumBinding mBinding;
    private DetailAlbumViewModel mDetailAlbumViewModel;
    private SongListAdapter mSongListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentDetailAlbumBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    private void setupView() {
        mBinding.includeFragmentDetailPlaylist
                .toolbarPlaylistDetail
                .setNavigationOnClickListener(
                        view -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        mSongListAdapter = new SongListAdapter(
                (song, index) -> {
                    Album album = mDetailAlbumViewModel.getAlbum().getValue();
                    String playlistName = album == null ? " " : album.getName();
                    SharedDataUtils.addPlaylist(mDetailAlbumViewModel.getPlaylist());
                    showAndPlay(song, index, playlistName);
                }, this::showOptionMenu);

        mBinding.includeFragmentDetailPlaylist
                .includeSongList
                .rvSongList
                .setAdapter(mSongListAdapter);
    }

    private void setupViewModel() {
        mDetailAlbumViewModel =
                new ViewModelProvider(requireActivity()).get(DetailAlbumViewModel.class);

        mDetailAlbumViewModel.getAlbum()
                .observe(getViewLifecycleOwner(), this::showAlbumInfo);

        mDetailAlbumViewModel.getAlbumSongs()
                .observe(getViewLifecycleOwner(), mSongListAdapter::updateSongs);
    }

    private void showAlbumInfo(Album album) {
        mBinding.includeFragmentDetailPlaylist.textTitlePlaylistDetail.setText(album.getName());
        String numberSong = getString(R.string.text_number_song, album.getSize());
        mBinding.includeFragmentDetailPlaylist.textNumberSongPlaylist.setText(numberSong);
        Glide.with(this)
                .load(album.getArtwork())
                .error(R.drawable.ic_album)
                .into(mBinding.includeFragmentDetailPlaylist.imgPlaylistAvatar);
    }
}