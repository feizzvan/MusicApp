package com.example.musicapp.ui.home.album.more;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentMoreAlbumBinding;
import com.example.musicapp.ui.home.album.AlbumViewModel;

public class MoreAlbumFragment extends Fragment {

    private FragmentMoreAlbumBinding mBinding;
    private AlbumViewModel mAlbumViewModel;

    public static MoreAlbumFragment newInstance() {
        return new MoreAlbumFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMoreAlbumBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupComponents();
    }

    private void setupComponents() {
        mBinding.toolbarMoreAlbum.setNavigationOnClickListener(
                view -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        MoreAlbumAdapter adapter = new MoreAlbumAdapter(album -> {
        });
        mBinding.recyclerMoreAlbum.setAdapter(adapter);
        mAlbumViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);
        mAlbumViewModel.getAlbumList().observe(getViewLifecycleOwner(), adapter::updateAlbums);
    }
}