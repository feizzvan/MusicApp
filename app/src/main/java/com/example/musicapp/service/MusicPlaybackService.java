package com.example.musicapp.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

//Tạo và quản lý một MediaController để điều khiển nhạc
public class MusicPlaybackService extends Service {
    // Biến lưu trữ một Future đại diện cho MediaController
    private ListenableFuture<MediaController> mControllerFuture;
    // Biến lưu trữ MediaController sau khi khởi tạo thành công
    private MediaController mMediaController;

    private final MutableLiveData<Boolean> mIsMediaControllerInitialized = new MutableLiveData<>();
    private LocalBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();

        setupMediaController();
        mBinder = new LocalBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaController != null) {
            mMediaController.release();
        }
        MediaController.releaseFuture(mControllerFuture);

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
                    if (mMediaController != null) {
                        mIsMediaControllerInitialized.postValue(true);
                    }
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

    public class LocalBinder extends Binder {
        public MediaController getMediaController() {
            return mMediaController;
        }

        public LiveData<Boolean> getIsMediaControllerInitialized() {
            return mIsMediaControllerInitialized;
        }
    }
}