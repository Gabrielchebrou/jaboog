package com.corbel.pierre.jb.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.lib.SampleSlide;
import com.github.paolorotolo.appintro.AppIntro2;

public class TutorialActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.tuto_welcome));
        addSlide(SampleSlide.newInstance(R.layout.tuto_question));
        addSlide(SampleSlide.newInstance(R.layout.tuto_joker));
        addSlide(SampleSlide.newInstance(R.layout.tuto_update));
        addSlide(SampleSlide.newInstance(R.layout.tuto_ready));
        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Helper.switchActivity(this, HomeActivity.class, R.anim.fake_anim, R.anim.fake_anim);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Helper.switchActivity(this, HomeActivity.class, R.anim.fake_anim, R.anim.fake_anim);
    }
}