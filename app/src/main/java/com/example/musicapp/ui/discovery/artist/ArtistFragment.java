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
import com.example.musicapp.R;
import com.example.musicapp.data.model.artist.Artist;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.data.repository.artist.ArtistRepository;
import com.example.musicapp.data.repository.song.SongRepositoryImpl;
import com.example.musicapp.databinding.FragmentArtistBinding;
import com.example.musicapp.ui.discovery.artist.detail.DetailArtistFragment;
import com.example.musicapp.ui.discovery.artist.more.MoreArtistFragment;
import com.example.musicapp.ui.discovery.artist.more.MoreArtistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArtistFragment extends Fragment {
    private FragmentArtistBinding mBinding;
    private ArtistAdapter mArtistAdapter;
    private ArtistViewModel mArtistViewModel;
    private MoreArtistViewModel mMoreArtistViewModel;
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
        mArtistAdapter = new ArtistAdapter(this::navigateToDetailArtist);
        mBinding.includeArtist.rvArtist.setAdapter(mArtistAdapter);
        mBinding.textTitleArtist.setOnClickListener(view -> navigateToMoreArtist());
        mBinding.btnMoreArtist.setOnClickListener(view -> navigateToMoreArtist());
    }

    private void setupViewModel() {
        MusicApplication application = (MusicApplication) requireActivity().getApplication();
        ArtistRepository ArtistRepository = application.getArtistRepository();
        SongRepositoryImpl songRepository = application.getSongRepository();
        ArtistViewModel.Factory factory = new ArtistViewModel.Factory(ArtistRepository, songRepository);

        mArtistViewModel =
                new ViewModelProvider(requireActivity(), factory).get(ArtistViewModel.class);
        mMoreArtistViewModel =
                new ViewModelProvider(requireActivity()).get(MoreArtistViewModel.class);

        mArtistViewModel.getArtist().observe(getViewLifecycleOwner(), artists ->
                mDisposable.add(mArtistViewModel.saveArtistToLocalDB(artists)
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                ));

        mDisposable.add(mArtistViewModel.loadLocalNArtists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(artists -> mArtistViewModel.setLocalArtists(artists),
                        throwable -> mArtistViewModel.setLocalArtists(new ArrayList<>()))
        );

        mArtistViewModel.getLocalArtists()
                .observe(getViewLifecycleOwner(), mArtistAdapter::updateArtist);

        mDisposable.add(mArtistViewModel.loadAllLocalArtists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(artists -> {
                    mMoreArtistViewModel.setArtists(artists);
                    mDisposable.add(mArtistViewModel.loadAllSongs()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(songs -> saveArtistSongCrossRef(artists, songs),
                                    throwable -> {
                                    })
                    );
                }, throwable -> mMoreArtistViewModel.setArtists(new ArrayList<>()))
        );
    }

    private void saveArtistSongCrossRef(List<Artist> artists, List<Song> songs) {
        mDisposable.add(mArtistViewModel.saveArtistSongCrossRef(artists, songs)
                .subscribeOn(Schedulers.io())
                .subscribe()
        );
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

    private void navigateToMoreArtist() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MoreArtistFragment.class, null)
                .addToBackStack(null)
                .commit();
    }
}