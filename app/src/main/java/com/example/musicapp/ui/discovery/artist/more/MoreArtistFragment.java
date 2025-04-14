package com.example.musicapp.ui.discovery.artist.more;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.databinding.FragmentMoreArtistBinding;
import com.example.musicapp.ui.discovery.artist.ArtistAdapter;
import com.example.musicapp.ui.discovery.artist.detail.DetailArtistFragment;

public class MoreArtistFragment extends Fragment {
    private FragmentMoreArtistBinding mBinding;
    private ArtistAdapter mAdapter;
    private MoreArtistViewModel mMoreArtistViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMoreArtistBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
        setupViewModel();
    }

    private void setupView() {
        mAdapter = new ArtistAdapter(this::navigateToDetailArtist);
        mBinding.includeMoreArtist.rvArtist.setAdapter(mAdapter);
        mBinding.toolbarMoreArtist.setNavigationOnClickListener(
                view -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupViewModel() {
        mMoreArtistViewModel = new ViewModelProvider(requireActivity()).get(MoreArtistViewModel.class);
        mMoreArtistViewModel.getArtists().observe(getViewLifecycleOwner(), mAdapter::updateArtist);
    }

    private void navigateToDetailArtist(Artist artist) {
        Bundle bundle = new Bundle();
        bundle.putInt(DetailArtistFragment.EXTRA_ARTIST_ID, artist.getId());

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, DetailArtistFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }
}