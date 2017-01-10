package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.widget.Button;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.app.Jaboog;
import com.corbel.pierre.jb.downloader.PictureDownloader;
import com.corbel.pierre.jb.lib.GameHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.games.Games;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class LoginActivity extends Activity implements GameHelper.GameHelperListener {

    protected GameHelper mGameHelper;
    @BindView(R.id.edit_text)
    TextInputEditText editText;
    @BindView(R.id.button)
    Button ok;
    @BindView(R.id.fab)
    FloatingActionButton playFab;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarColor(this);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set 3 Jokers on first launch
        int jokerInStock = preferences.getInt("JOKER_IN_STOCK", 3);
        editor = preferences.edit();
        editor.putInt("JOKER_IN_STOCK", jokerInStock);
        editor.apply();

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
    }

    @Override
    public void onSignInSucceeded() {
        String name = Games.Players.getCurrentPlayer(mGameHelper.getApiClient()).getDisplayName();
        String photo = Games.Players.getCurrentPlayer(mGameHelper.getApiClient()).getIconImageUrl();

        editor = preferences.edit();
        editor.putString("NAME_PREF", name);
        editor.putBoolean("IS_INITIALIZED", true);
        editor.putBoolean("IS_GOOGLE_CONN", true);
        editor.apply();

        if (photo != null) {
            new PictureDownloader(this).execute(photo);
        } else {
            new PictureDownloader(this).execute(getString(R.string.server_photo));
        }

        Jaboog.getInstance().trackEvent("Play Games", "Login", "Login from Login");

        Helper.switchActivity(this, TutorialActivity.class, R.anim.fake_anim, R.anim.fake_anim);
    }
}