package com.corbel.pierre.jb.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.leolin.shortcutbadger.ShortcutBadger;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ShortcutBadger.applyCount(context, 1);
    }
}