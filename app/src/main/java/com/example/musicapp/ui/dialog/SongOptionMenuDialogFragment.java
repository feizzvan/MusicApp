package com.example.musicapp.ui.dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.databinding.FragmentSongOptionMenuDialogBinding;
import com.example.musicapp.ui.dialog.information.SongInfoDialogFragment;
import com.example.musicapp.ui.dialog.information.SongInfoDialogViewModel;
import com.example.musicapp.ui.library.playlist.PlaylistViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class SongOptionMenuDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "SongOptionMenuDialogFragment";
    private FragmentSongOptionMenuDialogBinding mBinding;
    private OptionMenuAdapter mOptionMenuAdapter;
    private OptionMenuViewModel mOptionMenuViewModel;
    private PlaylistViewModel mPlaylistViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public PlaylistViewModel.Factory factory;

    public static SongOptionMenuDialogFragment newInstance() {
        return new SongOptionMenuDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSongOptionMenuDialogBinding.inflate(inflater, container, false);
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
        mDisposable.clear();
    }

    private void setupView() {
        mOptionMenuAdapter = new OptionMenuAdapter(this::handleMenuItemClick);
        mBinding.recyclerMenuOption.setAdapter(mOptionMenuAdapter);
    }

    private void setupViewModel() {
        mPlaylistViewModel =
                new ViewModelProvider(requireActivity(), factory).get(PlaylistViewModel.class);
        mOptionMenuViewModel =
                new ViewModelProvider(requireActivity()).get(OptionMenuViewModel.class);
        SongInfoDialogViewModel songInfoDialogViewModel =
                new ViewModelProvider(requireActivity()).get(SongInfoDialogViewModel.class);
        mOptionMenuViewModel.getOptionMenuItem()
                .observe(getViewLifecycleOwner(), mOptionMenuAdapter::updateMenuOptionItems);
        mOptionMenuViewModel.getSong().observe(getViewLifecycleOwner(), song -> {
            showSongInfo(song);
            songInfoDialogViewModel.setSong(song);
        });
    }

    private void handleMenuItemClick(OptionMenuItem optionMenuItem) {
        switch (optionMenuItem.getMenuOption()) {
            case BLOCK:
                break;
            case VIEW_DETAILS:
                SongInfoDialogFragment.newInstance().show(requireActivity()
                        .getSupportFragmentManager(), SongInfoDialogFragment.TAG);
                break;
            case ADD_TO_PLAYLIST:
                showAddToPlaylistDialog();
                break;
            default:
                Toast.makeText(requireActivity(), "Not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSongInfo(Song song) {
        mBinding.includeSongMenuOption.textItemMenuOptionTitle.setText(song.getTitle());
        mBinding.includeSongMenuOption.textItemSongArtist.setText(song.getArtist());
        Glide.with(this)
                .load(song.getImage())
                .error(R.drawable.ic_music_note)
                .into(mBinding.includeSongMenuOption.imgItemMenuOptionAvatar);
    }

    private void showAddToPlaylistDialog() {
        AddToPlaylistDialog dialog = new AddToPlaylistDialog(playlist -> {
            Song song = mOptionMenuViewModel.getSong().getValue();
            if (song != null) {
                mDisposable.add(mPlaylistViewModel.createPlaylistSongCrossRef(playlist, song)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            String message = getString(R.string.add_to_playlist_success, playlist.getName());
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }, error -> {
                            String message = getString(R.string.error_add_to_playlist);
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        })
                );
            }
        });
        dialog.show(requireActivity().getSupportFragmentManager(), AddToPlaylistDialog.TAG);
    }
}