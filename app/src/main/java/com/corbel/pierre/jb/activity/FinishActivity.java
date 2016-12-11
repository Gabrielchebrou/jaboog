package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.PictureDownloader;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.lib.LeaderBoardHelper;
import com.corbel.pierre.jb.lib.Serie;
import com.corbel.pierre.jb.view.BeautifulButtonWithImage;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

public class FinishActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.header_card_view)
    CardView headerCardView;

    @BindView(R.id.score_button)
    BeautifulButtonWithImage scoreButton;
    @BindView(R.id.clock_button)
    BeautifulButtonWithImage clockButton;
    @BindView(R.id.rate_button)
    BeautifulButtonWithImage rateButton;
    @BindView(R.id.archive_button)
    BeautifulButtonWithImage archiveButton;
    @BindView(R.id.jb_logo)
    ImageView jbLogo;
    @BindView(R.id.header_text_view)
    AutoResizeTextView headerTextView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.ad_view)
    AdView adView;

    private DbHelper db;
    private int questionId;
    private int score;
    private Handler handler = new Handler();

    private Animation headerCardViewAnimation;
    private Animation fabAnimation;
    private Animation scoreButtonAnimation;
    private Animation clockButtonAnimation;
    private Animation rateButtonAnimation;
    private Animation archiveButtonAnimation;
    private Animation logoAnimation;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        db = DbHelper.getInstance(this);
        questionId = getIntent().getExtras().getInt("questionId");
        String timeLeft = getIntent().getExtras().getString("timeLeft");
        score = getIntent().getExtras().getInt("score");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

        LeaderBoardHelper.incrementBestScore(this, score);

        int id = preferences.getInt("CURRENT_SERIE_ID_PREF", 0);
        Serie serie = db.getSerie(id);

        if (serie.getProgress() < questionId) {
            serie.setProgress(questionId);
        }

        if (serie.getHighScore() < score) {
            serie.setHighScore(score);
        }

        db.updateSerie(db.getWritableDatabase(), serie.getId(), serie.getUrl(), serie.getName(), serie.getHighScore(), serie.getProgress());

        scoreButton.setText(getString(R.string.quiz_score, score));
        clockButton.setText(getString(R.string.finish_time_left, timeLeft));
        headerTextView.setText(serie.getName());

        // Prepare Ad
        if (preferences.getBoolean("AD_ENABLED", true)) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.ad_nexus_5_id))
                    .addTestDevice(getString(R.string.ad_nexus_5_X_id))
                    .build();
            adView.loadAd(adRequest);
        }

        // Init Animations
        headerCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        scoreButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        clockButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        rateButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        archiveButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        logoAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in_your_face);

        // Anim In
        logoAnimation.setStartOffset(0);
        jbLogo.setVisibility(View.VISIBLE);
        jbLogo.startAnimation(logoAnimation);

        headerCardViewAnimation.setStartOffset(2000);
        headerCardView.setVisibility(View.VISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        scoreButtonAnimation.setStartOffset(2100);
        scoreButton.setVisibility(View.VISIBLE);
        scoreButton.startAnimation(scoreButtonAnimation);

        clockButtonAnimation.setStartOffset(2200);
        clockButton.setVisibility(View.VISIBLE);
        clockButton.startAnimation(clockButtonAnimation);

        rateButtonAnimation.setStartOffset(2300);
        rateButton.setVisibility(View.VISIBLE);
        rateButton.startAnimation(rateButtonAnimation);

        archiveButtonAnimation.setStartOffset(2400);
        archiveButton.setVisibility(View.VISIBLE);
        archiveButton.startAnimation(archiveButtonAnimation);

        fabAnimation.setStartOffset(2500);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    @OnClick(R.id.score_button)
    public void startAchievement() {
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            AchievementHelper.displayAchievement(this);
        } else {
            loginWithGoogle();
        }
    }

    @OnClick(R.id.clock_button)
    public void startLeaderBoard() {
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            LeaderBoardHelper.displayLeaderBoard(this);
        } else {
            loginWithGoogle();
        }
    }


    @OnClick(R.id.rate_button)
    public void rateApp() {
        String appId = getResources().getString(R.string.package_name);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + appId));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            noInternet(this);
        }
    }

    @OnClick(R.id.archive_button)
    public void openArchive() {
        animateOutTo(ArchiveActivity.class);
    }

    @OnClick(R.id.fab)
    public void replay() {
        animateOutTo(CountDownActivity.class);
    }

    @Override
    public void onBackPressed() {
        animateOutTo(HomeActivity.class);
    }

    public void animateOutTo(final Class toActivity) {

        // Init Animations
        headerCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        scoreButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        clockButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        rateButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        archiveButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        // Anim In
        headerCardViewAnimation.setStartOffset(0);
        headerCardView.setVisibility(View.INVISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        scoreButtonAnimation.setStartOffset(100);
        scoreButton.setVisibility(View.INVISIBLE);
        scoreButton.startAnimation(scoreButtonAnimation);

        clockButtonAnimation.setStartOffset(200);
        clockButton.setVisibility(View.INVISIBLE);
        clockButton.startAnimation(clockButtonAnimation);

        rateButtonAnimation.setStartOffset(300);
        rateButton.setVisibility(View.INVISIBLE);
        rateButton.startAnimation(rateButtonAnimation);

        archiveButtonAnimation.setStartOffset(400);
        archiveButton.setVisibility(View.INVISIBLE);
        archiveButton.startAnimation(archiveButtonAnimation);

        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(FinishActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 700);
    }

    public void loginWithGoogle() {
        if (mGoogleApiClient.isConnected()) {
            editor = preferences.edit();
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
                    editor = preferences.edit();
                    editor.putString("NAME_PREF", personName);
                    editor.putBoolean("IS_INITIALIZED", true);
                    editor.apply();
                    Uri personPhoto = acct.getPhotoUrl();
                    if (personPhoto != null) {
                        new PictureDownloader(this).execute(personPhoto.toString());
                    } else {
                        new PictureDownloader(this).execute(getString(R.string.server_photo));
                    }
                } else {
                    noInternet(this);
                }
            } else {
                noInternet(this);
            }
        }
    }
}
