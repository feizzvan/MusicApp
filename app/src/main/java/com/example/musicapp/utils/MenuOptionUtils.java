package com.example.musicapp.utils;

import com.example.musicapp.R;
import com.example.musicapp.ui.dialog.MenuOptionItem;

import java.util.ArrayList;
import java.util.List;

//abstract để không thể tạo đối tượng trực tiếp khi sử dụng
public abstract class MenuOptionUtils {
    // Danh sách static chứa tất cả các tùy chọn menu, dùng chung trong toàn bộ ứng dụng.
    private static final List<MenuOptionItem> sMenuItems = new ArrayList<>();

    // Khối static được gọi khi lớp được tải lần đầu tiên.
    static {
        createMenuItems();
    }

    private static void createMenuItems() {
        sMenuItems.add(new MenuOptionItem(MenuOption.DOWNLOAD, R.drawable.ic_download, R.string.download));
        sMenuItems.add(new MenuOptionItem(MenuOption.ADD_TO_FAVOURITE, R.drawable.ic_favourite, R.string.favourite));
        sMenuItems.add(new MenuOptionItem(MenuOption.ADD_TO_PLAYLIST, R.drawable.ic_playlist_add, R.string.add_to_playlist));
        sMenuItems.add(new MenuOptionItem(MenuOption.PLAY_NEXT, R.drawable.ic_play_next, R.string.play_next));
        sMenuItems.add(new MenuOptionItem(MenuOption.VIEW_ALBUM, R.drawable.ic_album, R.string.view_album));
        sMenuItems.add(new MenuOptionItem(MenuOption.VIEW_ARTIST, R.drawable.ic_view_artist, R.string.view_artist));
        sMenuItems.add(new MenuOptionItem(MenuOption.BLOCK, R.drawable.ic_block, R.string.block));
        sMenuItems.add(new MenuOptionItem(MenuOption.REPORT_ERROR, R.drawable.ic_report_error, R.string.report_error));
        sMenuItems.add(new MenuOptionItem(MenuOption.VIEW_DETAILS, R.drawable.ic_info, R.string.view_details));
    }

    //Cung cấp quyền truy cập vào danh sách các tùy chọn menu, @return Danh sách các mục menu.
    public static List<MenuOptionItem> getSongMenuOptionItems() {
        return sMenuItems;
    }

    public enum MenuOption {
        DOWNLOAD("download"),
        ADD_TO_FAVOURITE("add_to_favourite"),
        ADD_TO_PLAYLIST("add_to_playlist"),
        PLAY_NEXT("play_next"),
        VIEW_ALBUM("view_album"),
        VIEW_ARTIST("view_artist"),
        BLOCK("block"),
        REPORT_ERROR("report_error"),
        VIEW_DETAILS("view_details");

        private final String mValue;

        MenuOption(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }
}