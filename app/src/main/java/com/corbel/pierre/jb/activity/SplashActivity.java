package com.corbel.pierre.jb.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.Helper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("IS_INITIALIZED", false)) {
            Helper.switchActivity(this, HomeActivity.class, R.anim.fake_anim, R.anim.fake_anim);
        } else {
            Helper.switchActivity(this, LoginActivity.class, R.anim.fake_anim, R.anim.fake_anim);
        }
    }
}