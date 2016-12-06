package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.LeaderBoardHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;
import static com.corbel.pierre.jb.lib.Helper.switchActivity;

public class CountDownActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        ButterKnife.bind(this);
        setStatusBarColor(this);

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
        AchievementHelper.checkPushPlayedAchievement(this);
        LeaderBoardHelper.incrementPushedPlayed(this);
    }

    @Override
    public void onBackPressed() {
        switchActivity(this, HomeActivity.class, R.anim.fake_anim, R.anim.fake_anim);
        handler.removeCallbacks(runnable);
    }
}