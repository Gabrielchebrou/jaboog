package com.corbel.pierre.jb.lib;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class MediaPlayerHelper {

    public static MediaPlayer initializeMusicPlayer(Activity activity, int music) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean MUSIC_PREF = preferences.getBoolean("MUSIC_PREF", true);
        MediaPlayer mediaPlayer = null;
        if (MUSIC_PREF) {
            mediaPlayer = MediaPlayer.create(activity, music);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        return mediaPlayer;
    }

    public static void closePlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pausePlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    public static void resumePlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }
}