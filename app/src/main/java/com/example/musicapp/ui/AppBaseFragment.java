package com.example.musicapp.ui;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.data.model.Song;
import com.example.musicapp.ui.dialog.MenuOptionViewModel;
import com.example.musicapp.ui.dialog.SongMenuOptionDialogFragment;
import com.example.musicapp.ui.playing.MiniPlayerViewModel;
import com.example.musicapp.ui.viewmodel.NowPlayingViewModel;

public class AppBaseFragment extends Fragment {

    protected void showAndPlay(Song song, int index, String playlistName) {
        NowPlayingViewModel.getInstance().setCurrentPlaylist(playlistName);
        NowPlayingViewModel.getInstance().setIndexToPlay(index);
    }

    protected void showMenuOption(Song song) {
        MenuOptionViewModel menuOptionViewModel =
                new ViewModelProvider(requireActivity()).get(MenuOptionViewModel.class);
        menuOptionViewModel.setSong(song);
        SongMenuOptionDialogFragment dialog = SongMenuOptionDialogFragment.newInstance();
        dialog.show(requireActivity().getSupportFragmentManager(), SongMenuOptionDialogFragment.TAG);
    }
}
