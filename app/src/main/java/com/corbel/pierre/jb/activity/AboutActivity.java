package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.noInternet;
import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class AboutActivity extends Activity {

    @BindView(R.id.author_card_view)
    CardView authorCardView;
    @BindView(R.id.developer_card_view)
    CardView developerCardView;
    @BindView(R.id.music_card_view)
    CardView musicCardView;
    @BindView(R.id.lib_card_view)
    CardView libCardView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Animation authorButtonAnimation;
    private Animation developerButtonAnimation;
    private Animation musicButtonAnimation;
    private Animation libButtonAnimation;
    private Animation fabAnimation;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        // Init Animations
        authorButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        developerButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        musicButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        libButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        authorButtonAnimation.setStartOffset(0);
        authorCardView.setVisibility(View.VISIBLE);
        authorCardView.startAnimation(authorButtonAnimation);

        developerButtonAnimation.setStartOffset(100);
        developerCardView.setVisibility(View.VISIBLE);
        developerCardView.startAnimation(developerButtonAnimation);

        musicButtonAnimation.setStartOffset(200);
        musicCardView.setVisibility(View.VISIBLE);
        musicCardView.startAnimation(musicButtonAnimation);

        libButtonAnimation.setStartOffset(300);
        libCardView.setVisibility(View.VISIBLE);
        libCardView.startAnimation(libButtonAnimation);

        fabAnimation.setStartOffset(400);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    @OnClick(R.id.music_card_view)
    public void openBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.about_bensoud)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            noInternet(this);
        }
    }

    @OnClick(R.id.lib_card_view)
    public void openLibActivity() {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .start(this);
    }

    @OnClick(R.id.fab)
    public void home() {
        animateOutTo(HomeActivity.class);
    }

    @Override
    public void onBackPressed() {
        animateOutTo(HomeActivity.class);
    }

    public void animateOutTo(final Class toActivity) {

        authorButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        developerButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        musicButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        libButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        authorButtonAnimation.setStartOffset(0);
        authorCardView.setVisibility(View.INVISIBLE);
        authorCardView.startAnimation(authorButtonAnimation);

        developerButtonAnimation.setStartOffset(100);
        developerCardView.setVisibility(View.INVISIBLE);
        developerCardView.startAnimation(developerButtonAnimation);

        musicButtonAnimation.setStartOffset(200);
        musicCardView.setVisibility(View.INVISIBLE);
        musicCardView.startAnimation(musicButtonAnimation);

        libButtonAnimation.setStartOffset(300);
        libCardView.setVisibility(View.INVISIBLE);
        libCardView.startAnimation(libButtonAnimation);

        fabAnimation.setStartOffset(400);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(AboutActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 600);
    }
}