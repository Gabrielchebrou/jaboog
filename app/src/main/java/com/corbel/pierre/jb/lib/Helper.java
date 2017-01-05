package com.corbel.pierre.jb.lib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.corbel.pierre.jb.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.leolin.shortcutbadger.ShortcutBadger;

public class Helper {

    // Set status bar color
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.material_color_teal_500));
        }
    }

    // Switch between activity with anim
    public static void switchActivity(Activity fromActivity, Class toActivity, int exitingActivity, int appearingActivity) {
        Intent intent = new Intent(fromActivity, toActivity);
        fromActivity.startActivity(intent);
        fromActivity.finish();
        fromActivity.overridePendingTransition(appearingActivity, exitingActivity);
    }

    // Set badge counter on launcher
    public static void setBadge(Activity activity, int count) {
        ShortcutBadger.applyCount(activity.getApplicationContext(), count);
    }

    // Convert second to human readable format
    public static String convertTime(int seconds) {
        seconds /= 1000;
        return String.format(Locale.FRANCE, "%d:%02d",
                TimeUnit.SECONDS.toMinutes(seconds),
                TimeUnit.SECONDS.toSeconds(seconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        );
    }

    // Display no internet snackbar
    public static void noInternet(Activity activity) {
        Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.internet_failed), Snackbar.LENGTH_LONG)
                .show();
    }

    // Decode User Picture or load default one instead
    public static Bitmap getUserPicture(Context context, String color) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String picture = mPreferences.getString("PICTURE_PREF", "");
        if (!picture.equals("")) {
            byte[] b = Base64.decode(picture, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            if (color.equals("black")) {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.account_circle);
            } else if (color.equals("white")) {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.account_circle);
            } else {
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.account_circle);
            }

        }
    }
}