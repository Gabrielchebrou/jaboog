package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.widget.Button;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.PictureDownloader;
import com.corbel.pierre.jb.lib.GameHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.noInternet;
import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class LoginActivity extends Activity implements GameHelper.GameHelperListener {

    @BindView(R.id.edit_text)
    TextInputEditText editText;
    @BindView(R.id.button)
    Button ok;
    @BindView(R.id.fab)
    FloatingActionButton playFab;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    protected GameHelper mGameHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarColor(this);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mGameHelper.setup(this);
    }


    @OnClick(R.id.button)
    public void login() {
        if (!editText.getText().toString().trim().equals("")) {
            editor = preferences.edit();
            editor.putString("NAME_PREF", editText.getText().toString());
            editor.putBoolean("IS_GOOGLE_CONN", false);
            editor.putBoolean("IS_INITIALIZED", true);
            editor.apply();
            new PictureDownloader(this).execute(getString(R.string.server_photo));
            Helper.switchActivity(this, TutorialActivity.class, R.anim.fake_anim, R.anim.fake_anim);
        }
    }

    @OnClick(R.id.fab)
    public void loginWithGoogle() {
        mGameHelper.beginUserInitiatedSignIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        mGameHelper.onActivityResult(request, response, data);
    }

    @Override
    public void onSignInFailed() {
        noInternet(this);
    }

    @Override
    public void onSignInSucceeded() {
        String name = Games.Players.getCurrentPlayer(mGameHelper.getApiClient()).getDisplayName();
        Uri photo = Games.Players.getCurrentPlayer(mGameHelper.getApiClient()).getIconImageUri();

        editor = preferences.edit();
        editor.putString("NAME_PREF", name);
        editor.putBoolean("IS_INITIALIZED", true);
        editor.apply();

        if (photo != null) {
            new PictureDownloader(this).execute(photo.toString());
        } else {
            new PictureDownloader(this).execute(getString(R.string.server_photo));
        }
    }
}