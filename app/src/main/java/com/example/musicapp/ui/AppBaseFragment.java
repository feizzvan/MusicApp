package com.example.musicapp.ui;

import android.content.Intent;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.R;
import com.example.musicapp.data.model.song.Song;
import com.example.musicapp.ui.dialog.OptionMenuViewModel;
import com.example.musicapp.ui.dialog.SongOptionMenuDialogFragment;
import com.example.musicapp.ui.playing.NowPlayingActivity;
import com.example.musicapp.utils.PermissionUtils;
import com.example.musicapp.utils.SharedDataUtils;

public class AppBaseFragment extends Fragment {

    protected void showAndPlay(Song song, int index, String playlistName) {
        Boolean isPermissionGranted = PermissionUtils.getPermissionGranted().getValue();
        if (isPermissionGranted != null && isPermissionGranted) {
            doNavigate(index, playlistName);
        } else if (!PermissionUtils.isRegistered) {
            PermissionUtils.getPermissionGranted().observe(requireActivity(), isGranted -> {
                        if (isGranted) {
                            doNavigate(index, playlistName);
                        }
                    });
            PermissionUtils.isRegistered = true;
        }

        doNavigate(index, playlistName); //Bỏ comment phía trên thì xoá dòng này
    }

    protected void showOptionMenu(Song song) {
        OptionMenuViewModel optionMenuViewModel =
                new ViewModelProvider(requireActivity()).get(OptionMenuViewModel.class);
        optionMenuViewModel.setSong(song);
        SongOptionMenuDialogFragment dialog = SongOptionMenuDialogFragment.newInstance();
        dialog.show(requireActivity().getSupportFragmentManager(), SongOptionMenuDialogFragment.TAG);
    }

    private void doNavigate(int index, String playlistName) {
        SharedDataUtils.setCurrentPlaylist(playlistName);
        SharedDataUtils.setIndexToPlay(index);

        Intent intent = new Intent(requireContext(), NowPlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeCustomAnimation(requireContext(), R.anim.slide_up, R.anim.fade_out);
        requireContext().startActivity(intent, options.toBundle());
    }
}
