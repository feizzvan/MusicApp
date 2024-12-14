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
import com.example.musicapp.data.model.Song;
import com.example.musicapp.databinding.FragmentSongMenuOptionDialogBinding;
import com.example.musicapp.ui.dialog.information.SongInfoDialogFragment;
import com.example.musicapp.ui.dialog.information.SongInfoDialogViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SongMenuOptionDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "SongMenuOptionDialogFragment";
    private FragmentSongMenuOptionDialogBinding mBinding;
    private MenuOptionAdapter mMenuOptionAdapter;

    public static SongMenuOptionDialogFragment newInstance() {
        return new SongMenuOptionDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSongMenuOptionDialogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        setupViewModel();
    }

    private void setupView() {
        mMenuOptionAdapter = new MenuOptionAdapter(menuOptionItem -> {
            handleMenuItemClick(menuOptionItem);
            dismiss();
        });
        mBinding.recyclerMenuOption.setAdapter(mMenuOptionAdapter);
    }

    private void setupViewModel() {
        MenuOptionViewModel menuOptionViewModel =
                new ViewModelProvider(requireActivity()).get(MenuOptionViewModel.class);
        SongInfoDialogViewModel songInfoDialogViewModel =
                new ViewModelProvider(requireActivity()).get(SongInfoDialogViewModel.class);
        menuOptionViewModel.getMenuOptionItem()
                .observe(getViewLifecycleOwner(), mMenuOptionAdapter::updateMenuOptionItems);
        menuOptionViewModel.getSong().observe(getViewLifecycleOwner(), song -> {
            showSongInfo(song);
            songInfoDialogViewModel.setSong(song);
        });
    }

    private void handleMenuItemClick(MenuOptionItem menuOptionItem) {
        switch (menuOptionItem.getMenuOption()) {
            case BLOCK:
                break;
            case VIEW_DETAILS:
                SongInfoDialogFragment.newInstance().show(requireActivity()
                        .getSupportFragmentManager(), SongInfoDialogFragment.TAG);
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
}