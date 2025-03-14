package com.example.musicapp.ui.home.album;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentAlbumBinding;
import com.example.musicapp.ui.home.album.detail.DetailAlbumFragment;
import com.example.musicapp.ui.home.album.detail.DetailAlbumViewModel;
import com.example.musicapp.ui.home.album.more.MoreAlbumFragment;

public class AlbumFragment extends Fragment {
    private AlbumAdapter mAlbumAdapter;
    private FragmentAlbumBinding mBinding;
    private AlbumViewModel mAlbumViewModel;
    private DetailAlbumViewModel mDetailAlbumViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAlbumBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        setupViewModel();
    }

    private void setupView() {
        mBinding.progressAlbum.setVisibility(View.VISIBLE);
        mAlbumAdapter = new AlbumAdapter(album -> {
            mDetailAlbumViewModel.setAlbum(album);
            mDetailAlbumViewModel.extractSongList(album);
            navigateToDetailAlbum();
        });

        // Gắn adapter vào RecyclerView trong layout
        mBinding.recyclerAlbumHot.setAdapter(mAlbumAdapter);

        mBinding.btnMoreAlbumHot.setOnClickListener(view -> navigateToMoreAlbum());
        mBinding.textTitleAlbumHot.setOnClickListener(view -> navigateToMoreAlbum());
    }

    private void setupViewModel() {
        // Khởi tạo AlbumViewModel từ ViewModelProvider
        mAlbumViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);

        mDetailAlbumViewModel = new ViewModelProvider(requireActivity()).get(DetailAlbumViewModel.class);

        // Quan sát mAlbumList trong ViewModel
        mAlbumViewModel.getAlbumList().observe(getViewLifecycleOwner(), albumList -> {
            mAlbumAdapter.updateAlbums(albumList.subList(0, 10));
            mBinding.progressAlbum.setVisibility(View.GONE);
        });
    }

    private void navigateToDetailAlbum() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, DetailAlbumFragment.class, null)
                .addToBackStack(null)
                .commit();

    }

    private void navigateToMoreAlbum() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MoreAlbumFragment.class, null)
                .addToBackStack(null)
                .commit();
    }
}