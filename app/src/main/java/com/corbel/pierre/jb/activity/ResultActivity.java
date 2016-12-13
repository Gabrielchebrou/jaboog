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
import com.corbel.pierre.jb.downloader.PictureDownloader;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.GameHelper;
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

public class ResultActivity extends Activity
        implements GameHelper.GameHelperListener {

    public GameHelper mGameHelper;
    @BindView(R.id.question_card_view)
    CardView questionCardView;
    @BindView(R.id.question_text_view)
    AutoResizeTextView questionTextView;
    @BindView(R.id.answer_button)
    BeautifulButtonWithImage answerButton;
    @BindView(R.id.wiki_button)
    BeautifulButtonWithImage wikiButton;
    @BindView(R.id.score_button)
    BeautifulButtonWithImage scoreButton;
    @BindView(R.id.current_button)
    BeautifulButtonWithImage currentButton;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.ad_view)
    AdView adView;
    private DbHelper db;
    private Question currentQuestion;
    private int questionId;
    private int score;
    private Handler handler = new Handler();
    private Animation questionCardViewAnimation;
    private Animation fabAnimation;
    private Animation answerButtonAnimation;
    private Animation wikiButtonAnimation;
    private Animation scoreButtonAnimation;
    private Animation currentButtonAnimation;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isTryingToConnectAchievement = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        db = DbHelper.getInstance(this);
        questionId = getIntent().getExtras().getInt("questionId");
        String timeLeft = getIntent().getExtras().getString("timeLeft");
        score = getIntent().getExtras().getInt("score");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mGameHelper.setup(this);
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            mGameHelper.beginUserInitiatedSignIn();
        }

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

        currentQuestion = db.getQuestion(questionId);
        String question = currentQuestion.getQuestion();
        String answer = currentQuestion.getGoodAnswer();

        questionTextView.setText(question);
        answerButton.setText(answer);
        scoreButton.setText(getString(R.string.quiz_score, score));
        currentButton.setText(getString(R.string.quiz_question_id, questionId));
        wikiButton.setWikiUnderline();

        // Prepare Ad
        if (preferences.getBoolean("AD_ENABLED", true)) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.ad_nexus_5_id))
                    .addTestDevice(getString(R.string.ad_nexus_5_X_id))
                    .build();
            adView.loadAd(adRequest);
        }

        // Init Animations
        questionCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        answerButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        wikiButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        scoreButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        currentButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        questionCardViewAnimation.setStartOffset(0);
        questionCardView.setVisibility(View.VISIBLE);
        questionCardView.startAnimation(questionCardViewAnimation);

        answerButtonAnimation.setStartOffset(100);
        answerButton.setVisibility(View.VISIBLE);
        answerButton.startAnimation(answerButtonAnimation);

        wikiButtonAnimation.setStartOffset(200);
        wikiButton.setVisibility(View.VISIBLE);
        wikiButton.startAnimation(wikiButtonAnimation);

        scoreButtonAnimation.setStartOffset(300);
        scoreButton.setVisibility(View.VISIBLE);
        scoreButton.startAnimation(scoreButtonAnimation);

        currentButtonAnimation.setStartOffset(400);
        currentButton.setVisibility(View.VISIBLE);
        currentButton.startAnimation(currentButtonAnimation);

        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    @OnClick(R.id.wiki_button)
    public void openBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(currentQuestion.getUrl()));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            noInternet(this);
        }
    }

    @OnClick(R.id.fab)
    public void replay() {
        animateOutTo(CountDownActivity.class);
    }

    @OnClick(R.id.current_button)
    public void openArchive() {
        animateOutTo(ArchiveActivity.class);
    }

    @OnClick(R.id.score_button)
    public void startAchievement() {
        try {
            AchievementHelper.displayAchievement(this);
        } catch (Exception e) {
            e.printStackTrace();
            isTryingToConnectAchievement = true;
            mGameHelper.beginUserInitiatedSignIn();
        }
    }

    @Override
    public void onBackPressed() {
        animateOutTo(HomeActivity.class);
    }

    public void animateOutTo(final Class toActivity) {

        questionCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        answerButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        wikiButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        scoreButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        currentButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        questionCardViewAnimation.setStartOffset(0);
        questionCardView.setVisibility(View.INVISIBLE);
        questionCardView.startAnimation(questionCardViewAnimation);

        answerButtonAnimation.setStartOffset(100);
        answerButton.setVisibility(View.INVISIBLE);
        answerButton.startAnimation(answerButtonAnimation);

        wikiButtonAnimation.setStartOffset(200);
        wikiButton.setVisibility(View.INVISIBLE);
        wikiButton.startAnimation(wikiButtonAnimation);

        scoreButtonAnimation.setStartOffset(300);
        scoreButton.setVisibility(View.INVISIBLE);
        scoreButton.startAnimation(scoreButtonAnimation);

        currentButtonAnimation.setStartOffset(400);
        currentButton.setVisibility(View.INVISIBLE);
        currentButton.startAnimation(currentButtonAnimation);

        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(ResultActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 700);
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
        if (!preferences.getBoolean("IS_GOOGLE_CONN", false)) {
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
        }

        if (isTryingToConnectAchievement) {
            isTryingToConnectAchievement = false;
            startAchievement();
        }
    }
}
