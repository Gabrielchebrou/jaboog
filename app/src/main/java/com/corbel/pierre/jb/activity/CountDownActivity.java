package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.GameHelper;
import com.corbel.pierre.jb.lib.LeaderBoardHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;
import static com.corbel.pierre.jb.lib.Helper.switchActivity;

public class CountDownActivity extends Activity
        implements GameHelper.GameHelperListener {

    public GameHelper mGameHelper;
    @BindView(R.id.one_text_view)
    TextView oneTextView;
    @BindView(R.id.two_text_view)
    TextView twoTextView;
    @BindView(R.id.three_text_view)
    TextView threeTextView;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private Handler handler1 = new Handler();
    private Runnable runnable;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mGameHelper.setup(this);
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            mGameHelper.beginUserInitiatedSignIn();
        }

        AchievementHelper.checkPushPlayedAchievement(this);
        LeaderBoardHelper.incrementLocalPushedPlayed(this);

        final Animation animation3 = AnimationUtils.loadAnimation(threeTextView.getContext(), R.anim.slide_out);
        final Animation animation2 = AnimationUtils.loadAnimation(twoTextView.getContext(), R.anim.slide_out);
        final Animation animation1 = AnimationUtils.loadAnimation(oneTextView.getContext(), R.anim.slide_out);

        animation3.setDuration(1000);
        threeTextView.setVisibility(View.VISIBLE);
        threeTextView.startAnimation(animation3);

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                threeTextView.setVisibility(View.GONE);
                animation2.setDuration(750);
                twoTextView.setVisibility(View.VISIBLE);
                twoTextView.startAnimation(animation2);
            }
        }, 1000);

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                twoTextView.setVisibility(View.GONE);
                animation1.setDuration(500);
                oneTextView.setVisibility(View.VISIBLE);
                oneTextView.startAnimation(animation1);
            }
        }, 1750);


        runnable = new Runnable() {
            @Override
            public void run() {
                oneTextView.setVisibility(View.GONE);
                switchActivity(CountDownActivity.this, QuizActivity.class, R.anim.fake_anim, R.anim.fake_anim);
            }
        };

        handler.postDelayed(runnable, 2250);
    }

    @Override
    public void onBackPressed() {
        switchActivity(this, HomeActivity.class, R.anim.fake_anim, R.anim.fake_anim);
        handler.removeCallbacks(runnable);
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
        LeaderBoardHelper.incrementGamesPushedPlayed(this);
    }
}