package com.example.musicapp.ui.library.playlist.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.playlist.PlaylistWithSongs;
import com.example.musicapp.databinding.FragmentPlaylistDetailBinding;
import com.example.musicapp.ui.home.recommended.SongListAdapter;

public class PlaylistDetailFragment extends Fragment {
    private FragmentPlaylistDetailBinding mBinding;
    private SongListAdapter mAdaper;
    private PlaylistDetailViewModel mPlaylistDetailViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPlaylistDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    private void setupView() {
        mBinding.includePlaylistDetail.toolbarPlaylistDetail.setNavigationOnClickListener(
                view -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        mAdaper = new SongListAdapter(
                (song, index) -> {

                },
                song -> {

                }
        );
        mBinding.includePlaylistDetail.includeMoreRecentSongList.rvSongList.setAdapter(mAdaper);
    }

    private void setupViewModel() {
        mPlaylistDetailViewModel = new ViewModelProvider(requireActivity()).get(PlaylistDetailViewModel.class);
        mPlaylistDetailViewModel.getPlaylistWithSongs().observe(getViewLifecycleOwner(), playlistWithSongs -> {
            mAdaper.updateSongs(playlistWithSongs.songs);
            showPlaylistInfo(playlistWithSongs);
        });
    }

    private void showPlaylistInfo(PlaylistWithSongs playlistWithSongs) {
        String artworkUrl = null;
        if (!playlistWithSongs.songs.isEmpty()) {
            artworkUrl = playlistWithSongs.songs.get(0).getImage();
        }
        Glide.with(this)
                .load(artworkUrl)
                .error(R.drawable.ic_album)
                .into(mBinding.includePlaylistDetail.imgPlaylistAvatar);
        String playlistName = playlistWithSongs.playlist.getName();
        mBinding.includePlaylistDetail.textTitlePlaylistDetail.setText(playlistName);
        String numOfSong = getString(R.string.text_number_song, playlistWithSongs.songs.size());
        mBinding.includePlaylistDetail.textNumberSongPlaylist.setText(numOfSong);
    }
}