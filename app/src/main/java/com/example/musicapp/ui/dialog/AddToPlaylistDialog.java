package com.example.musicapp.ui.dialog;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.data.model.playlist.Playlist;
import com.example.musicapp.ui.library.playlist.PlaylistAdapter;
import com.example.musicapp.ui.library.playlist.PlaylistViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class AddToPlaylistDialog extends DialogFragment {
    public static final String TAG = "AddToPlaylistDialog";
    private final onPlaylistSelectedListener mListener;
    private PlaylistAdapter mAdapter;
    private PlaylistViewModel mPlaylistViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public PlaylistViewModel.Factory factory;

    public AddToPlaylistDialog(onPlaylistSelectedListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setupComponents();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_to_playlist, null);

        //Khai báo các biến liên kết tới các view
        RecyclerView recyclerViewPlaylist = rootView.findViewById(R.id.rv_existed_playlist);
        recyclerViewPlaylist.setAdapter(mAdapter);

        MaterialButton btnCancel = rootView.findViewById(R.id.btn_cancel_playlist);
        MaterialButton btnCreate = rootView.findViewById(R.id.btn_create_playlist);
        TextInputEditText edtPlaylistName = rootView.findViewById(R.id.edit_playlist_name);

        //Xử lý action cho từng biên
        btnCancel.setOnClickListener(view -> dismiss());
        btnCreate.setOnClickListener(view -> {
            String playlistName;
            if (edtPlaylistName.getText() != null) {
                playlistName = edtPlaylistName.getText().toString().trim();
                if (playlistName.isEmpty()) {
                    edtPlaylistName.setError(getString(R.string.error_empty_playlist_name));
                } else {
                    checkAndCreatePlaylist(playlistName);
                    closeKeyboard(edtPlaylistName);
                    edtPlaylistName.getText().clear();
                }
            }
        });

        builder.setView(rootView);
        loadPlaylist();

        //Người dùng click vào dialog sẽ không thay đổi vị trí hoặc kích thước của cửa sổ
        edtPlaylistName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                requireActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
            }
        });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    private void setupComponents() {
        mPlaylistViewModel =
                new ViewModelProvider(requireActivity(), factory).get(PlaylistViewModel.class);
        mAdapter = new PlaylistAdapter(
                playlist -> {
                    mListener.onPlaylistSelected(playlist);
                    dismiss();
                },
                playlist -> {

                }
        );

    }

    private void loadPlaylist() {
        mDisposable.add(mPlaylistViewModel.getAllPlaylistWithSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playlists -> mPlaylistViewModel.setPlaylists(playlists),
                        error -> {
                        })
        );
        mPlaylistViewModel.getPlaylists().observe(requireActivity(), playlists ->
                mAdapter.updatePlaylists(playlists));
    }

    private void checkAndCreatePlaylist(String playlistName) {
        mDisposable.add(mPlaylistViewModel.getPlaylistByName(playlistName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> doCreatePlaylist(result, playlistName),
                        error -> doCreatePlaylist(null, playlistName))
        );
    }

    private void doCreatePlaylist(Playlist playlist, String playlistName) {
        if (playlist == null) {
            mDisposable.add(mPlaylistViewModel.createPlaylist(playlistName)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            );
        } else {
            String errorMessage = getString(R.string.error_playlist_already_exists);
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard(TextInputEditText view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface onPlaylistSelectedListener {
        void onPlaylistSelected(Playlist playlist);
    }
}
