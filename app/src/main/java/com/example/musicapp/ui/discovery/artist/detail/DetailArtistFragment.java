package com.example.musicapp.ui.discovery.artist.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.databinding.FragmentDetailArtistBinding;
import com.example.musicapp.ui.AppBaseFragment;
import com.example.musicapp.ui.home.recommended.SongListAdapter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class DetailArtistFragment extends AppBaseFragment {
    private FragmentDetailArtistBinding mBinding;
    private DetailArtistViewModel mDetailArtistViewModel;
    private SongListAdapter mSongAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public DetailArtistViewModel.Factory factory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDetailArtistBinding.inflate(inflater, container, false);
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
        mBinding.toolbarDetailArtist.setNavigationOnClickListener(
                view -> requireActivity().getSupportFragmentManager().popBackStack());
        mSongAdapter = new SongListAdapter(this::playSong, this::showOptionMenu);
        mBinding.includeArtistSong.rvSongList.setAdapter(mSongAdapter);
    }

    private void setupViewModel() {
        mDetailArtistViewModel =
                new ViewModelProvider(requireActivity(), factory).get(DetailArtistViewModel.class);

        if (getArguments() != null) {
            int artistId = DetailArtistFragmentArgs.fromBundle(getArguments()).getArtistId();
            mDisposable.add(mDetailArtistViewModel.getArtistWithSongs(artistId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(artistWithSongs -> mDetailArtistViewModel.setArtistWithSongs(artistWithSongs),
                            t -> mDetailArtistViewModel.setArtistWithSongs(null)
                    )
            );
        }

        mDetailArtistViewModel.getArtistWithSongs().observe(getViewLifecycleOwner(),
                artistWithSongs -> {
                    if (artistWithSongs != null) {
                        mSongAdapter.updateSongs(artistWithSongs.songs);
                        showArtistInfo(artistWithSongs.artist);
                    }
                }
        );
    }

    private void playSong(Song song, int index) {
        Playlist playlist = mDetailArtistViewModel.createPlaylist();
        String playlistName = playlist.getName();
        showAndPlay(song, index, playlistName);
    }

    private void showArtistInfo(Artist artist) {
        Glide.with(requireContext())
                .load(artist.getAvatar())
                .error(R.drawable.ic_view_artist)
                .circleCrop()
                .into(mBinding.imgDetailArtistAvatar);
        mBinding.textDetailArtistName
                .setText(getString(R.string.text_detail_artist_name, artist.getName()));
        mBinding.textDetailArtistInterest
                .setText(getString(R.string.text_detail_number_of_interest, artist.getInterested()));
        String yourInterest = "No";
        mBinding.textDetailArtistYourInterest
                .setText(getString(R.string.text_detail_your_interested_artist, yourInterest));
    }
}