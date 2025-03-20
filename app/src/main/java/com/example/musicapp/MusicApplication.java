package com.example.musicapp;

import android.app.Application;
import android.content.ComponentName;
import android.telephony.ims.RcsUceAdapter;

import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.example.musicapp.data.repository.RecentSongRepository;
import com.example.musicapp.data.repository.SongRepository;
import com.example.musicapp.data.repository.SongRepositoryImpl;
import com.example.musicapp.data.source.RecentSongDataSource;
import com.example.musicapp.data.source.local.LocalSongDataSource;
import com.example.musicapp.ui.playing.PlaybackService;
import com.example.musicapp.ui.viewmodel.MediaPlayerViewModel;
import com.example.musicapp.utils.InjectionUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class MusicApplication extends Application {
    // Biến lưu trữ một Future đại diện cho MediaController
    private ListenableFuture<MediaController> mControllerFuture;
    // Biến lưu trữ MediaController sau khi khởi tạo thành công
    private MediaController mMediaController;

    private RecentSongRepository mRecentSongRepository;

    private SongRepositoryImpl mSongRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        // Gọi phương thức để khởi tạo MediaController khi ứng dụng bắt đầu
        setupMediaController();

        setupComponents();
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

    private void setupComponents(){
        RecentSongDataSource recentSongDataSource = InjectionUtils.provideRecentSongDataSource(getApplicationContext());
        mRecentSongRepository = InjectionUtils.provideRecentSongRepository(recentSongDataSource);
        LocalSongDataSource localSongDataSource = InjectionUtils.provideLocalSongDataSource(getApplicationContext());
        mSongRepository = InjectionUtils.provideSongRepository(localSongDataSource);
    }

    public RecentSongRepository getRecentSongRepository() {
        return mRecentSongRepository;
    }

    public SongRepositoryImpl getSongRepository(){
        return mSongRepository;
    }
}
