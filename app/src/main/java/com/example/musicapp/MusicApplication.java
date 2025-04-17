package com.example.musicapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.SharedPreferences;

import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.preference.PreferenceManager;

import com.example.musicapp.ui.playing.PlaybackService;
import com.example.musicapp.ui.settings.SettingsFragment;
import com.example.musicapp.ui.viewmodel.MediaPlayerViewModel;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MusicApplication extends Application {
    // Biến lưu trữ một Future đại diện cho MediaController
    private ListenableFuture<MediaController> mControllerFuture;
    // Biến lưu trữ MediaController sau khi khởi tạo thành công
    private MediaController mMediaController;

    @Override
    public void onCreate() {
        super.onCreate();

        setupLanguage();
        // Gọi phương thức để khởi tạo MediaController khi ứng dụng bắt đầu
        setupMediaController();
    }

    private void setupLanguage() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String defaultLanguage = Locale.getDefault().getLanguage();
        String language = sharedPreferences.getString(SettingsFragment.KEY_PREF_LANGUAGE, defaultLanguage);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SettingsFragment.KEY_PREF_LANGUAGE, language);
        editor.apply();
    }

    private void setupMediaController() {
        // Tạo một SessionToken để kết nối với PlaybackService
        // ComponentName chỉ định rõ dịch vụ (PlaybackService) sẽ được điều khiển
        SessionToken sessionToken = new SessionToken(getApplicationContext(),
                new ComponentName(getApplicationContext(), PlaybackService.class));

        // Sử dụng Builder để tạo MediaController bất đồng bộ (asynchronous)
        mControllerFuture = new MediaController
                .Builder(getApplicationContext(), sessionToken)
                .buildAsync();

        // Đăng ký một listener để xử lý kết quả khi mControllerFuture hoàn thành
        Runnable listener = () -> {
            if (mControllerFuture.isDone() && !mControllerFuture.isCancelled()) {
                try {
                    // Lấy MediaController từ Future
                    mMediaController = mControllerFuture.get();
                    // Cập nhật MediaPlayerViewModel với MediaController vừa tạo
                    MediaPlayerViewModel.getInstance().setMediaPlayer(mMediaController);
                } catch (ExecutionException | InterruptedException ignored) {

                }
            } else {
                // Nếu Future bị hủy hoặc không thành công, đặt MediaController là null
                mMediaController = null;
            }
        };
        mControllerFuture.addListener(listener, MoreExecutors.directExecutor());
        // Thực thi trực tiếp trên thread hiện tại
    }
}
