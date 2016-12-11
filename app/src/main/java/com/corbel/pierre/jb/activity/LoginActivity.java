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
import android.util.Log;
import android.widget.Button;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.PictureDownloader;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.noInternet;
import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class LoginActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.edit_text)
    TextInputEditText editText;
    @BindView(R.id.button)
    Button ok;
    @BindView(R.id.fab)
    FloatingActionButton playFab;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;

    private String TAG = "com.corbel/pierre.jb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarColor(this);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
    }


    @OnClick(R.id.button)
    public void login() {
        if (!editText.getText().toString().trim().equals("")) {
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
        if (mGoogleApiClient.isConnected()) {
            editor.putBoolean("IS_GOOGLE_CONN", true);
            editor.apply();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, 9001);
        } else {
            noInternet(this);
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        noInternet(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Get account information
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {
                    String personName = acct.getDisplayName();
                    editor.putString("NAME_PREF", personName);
                    editor.putBoolean("IS_INITIALIZED", true);
                    editor.apply();
                    Uri personPhoto = acct.getPhotoUrl();
                    if (personPhoto != null) {
                        new PictureDownloader(this).execute(personPhoto.toString());
                    } else {
                        new PictureDownloader(this).execute(getString(R.string.server_photo));
                    }
                    Helper.switchActivity(this, TutorialActivity.class, R.anim.fake_anim, R.anim.fake_anim);
                } else {
                    noInternet(this);
                }
            } else {
                noInternet(this);
            }
        } else {
            noInternet(this);
        }
    }
}