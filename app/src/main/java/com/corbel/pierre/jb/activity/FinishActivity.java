package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.app.Jaboog;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.lib.LeaderBoardHelper;
import com.corbel.pierre.jb.lib.Question;
import com.corbel.pierre.jb.lib.Serie;
import com.corbel.pierre.jb.view.BeautifulButtonWithImage;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.noInternet;
import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;
import static com.corbel.pierre.jb.lib.Helper.setViewForPopup;

public class FinishActivity extends Activity {

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

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.ad_view)
    AdView adView;

    private DbHelper db;
    private Question currentQuestion;
    private int questionId;
    private int score;
    private Handler handler = new Handler();

    private Animation headerCardViewAnimation;
    private Animation fabAnimation;
    private Animation scoreButtonAnimation;
    private Animation clockButtonAnimation;
    private Animation rateButtonAnimation;
    private Animation archiveButtonAnimation;

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

        LeaderBoardHelper.incrementBestScore(this, score);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        // Anim In
        headerCardViewAnimation.setStartOffset(0);
        headerCardView.setVisibility(View.VISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        scoreButtonAnimation.setStartOffset(100);
        scoreButton.setVisibility(View.VISIBLE);
        scoreButton.startAnimation(scoreButtonAnimation);

        clockButtonAnimation.setStartOffset(200);
        clockButton.setVisibility(View.VISIBLE);
        clockButton.startAnimation(clockButtonAnimation);

        rateButtonAnimation.setStartOffset(300);
        rateButton.setVisibility(View.VISIBLE);
        rateButton.startAnimation(rateButtonAnimation);

        archiveButtonAnimation.setStartOffset(400);
        archiveButton.setVisibility(View.VISIBLE);
        archiveButton.startAnimation(archiveButtonAnimation);

        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    @OnClick(R.id.score_button)
    public void startAchievement() {
        try {
            setViewForPopup(this);
            startActivityForResult(Games.Achievements.getAchievementsIntent(Jaboog.getGoogleApiHelper().mGoogleApiClient), 2);
        } catch (IllegalStateException e) {
            Jaboog.getGoogleApiHelper().mGoogleApiClient.connect();
            noInternet(this);
        }
    }

    @OnClick(R.id.clock_button)
    public void startLeaderBoard() {
        try {
            setViewForPopup(this);
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(Jaboog.getGoogleApiHelper().mGoogleApiClient), 2);
        } catch (IllegalStateException e) {
            Jaboog.getGoogleApiHelper().mGoogleApiClient.connect();
            noInternet(this);
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
}
