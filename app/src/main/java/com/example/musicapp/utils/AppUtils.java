package com.example.musicapp.utils;

public abstract class AppUtils {
    public enum DefaultPlaylistName {
        DEFAULT("default"),
        FAVOURITE("favourite"),
        RECOMMENDED("recommended"),
        RECENT("recent"),
        SEARCHED("searched"),
        MOST_HEARD("most_heard"),
        FOR_YOU("for_you"),
        CUSTOM("custom");

        private final String value;

        DefaultPlaylistName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
