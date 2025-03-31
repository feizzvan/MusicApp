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
import com.example.musicapp.databinding.FragmentSongOptionMenuDialogBinding;
import com.example.musicapp.ui.dialog.information.SongInfoDialogFragment;
import com.example.musicapp.ui.dialog.information.SongInfoDialogViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SongOptionMenuDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "SongOptionMenuDialogFragment";
    private FragmentSongOptionMenuDialogBinding mBinding;
    private OptionMenuAdapter mOptionMenuAdapter;

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

    private void setupView() {
        mOptionMenuAdapter = new OptionMenuAdapter(optionMenuItem -> {
            handleMenuItemClick(optionMenuItem);
            dismiss();
        });
        mBinding.recyclerMenuOption.setAdapter(mOptionMenuAdapter);
    }

    private void setupViewModel() {
        OptionMenuViewModel optionMenuViewModel =
                new ViewModelProvider(requireActivity()).get(OptionMenuViewModel.class);
        SongInfoDialogViewModel songInfoDialogViewModel =
                new ViewModelProvider(requireActivity()).get(SongInfoDialogViewModel.class);
        optionMenuViewModel.getOptionMenuItem()
                .observe(getViewLifecycleOwner(), mOptionMenuAdapter::updateMenuOptionItems);
        optionMenuViewModel.getSong().observe(getViewLifecycleOwner(), song -> {
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