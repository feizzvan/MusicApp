package com.example.musicapp.ui.discovery.artist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MusicApplication;
import com.example.musicapp.data.repository.artist.ArtistRepository;
import com.example.musicapp.databinding.FragmentArtistBinding;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistFragment extends Fragment {
    private FragmentArtistBinding mBinding;
    private ArtistAdapter mArtistAdapter;
    private ArtistViewModel mArtistViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentArtistBinding.inflate(inflater, container, false);
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
        mArtistAdapter = new ArtistAdapter(
                artist -> {
                    // Xử lý khi người dùng nhấp vào artist
                }
        );
        mBinding.includeArtist.rvArtist.setAdapter(mArtistAdapter);
    }

    private void setupViewModel() {
        MusicApplication application = (MusicApplication) requireActivity().getApplication();

        ArtistRepository repository = application.getArtistRepository();
        ArtistViewModel.Factory factory = new ArtistViewModel.Factory(repository);
        mArtistViewModel =
                new ViewModelProvider(requireActivity(), factory).get(ArtistViewModel.class);

        mArtistViewModel.getArtist().observe(getViewLifecycleOwner(), artists -> {
            mDisposable.add(mArtistViewModel
                    .saveArtistToLocalDB(artists)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            );
        });

        mDisposable.add(mArtistViewModel
                .loadLocalArtists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(artists -> mArtistViewModel.setLocalArtists(artists),
                        throwable -> mArtistViewModel.setLocalArtists(new ArrayList<>())));

        mArtistViewModel.getLocalArtists()
                .observe(getViewLifecycleOwner(), mArtistAdapter::updateArtist);

    }
}