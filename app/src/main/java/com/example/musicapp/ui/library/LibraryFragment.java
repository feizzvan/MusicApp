package com.example.musicapp.ui.library;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentLibraryBinding;
import com.example.musicapp.ui.viewmodel.SharedViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LibraryFragment extends Fragment {
    private FragmentLibraryBinding mBinding;
    private LibraryViewModel mLibraryViewModel;
    private SharedViewModel mSharedViewModel;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    private void setupViewModel() {
        mLibraryViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        mSharedViewModel = SharedViewModel.getInstance();

    }

    private void setupObserver() {
        mDisposable.add(mSharedViewModel.loadRecentSongs()
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(songs -> mLibraryViewModel.setRecentSongs(songs),
                        throwable -> mLibraryViewModel.setRecentSongs(null)));
    }
}