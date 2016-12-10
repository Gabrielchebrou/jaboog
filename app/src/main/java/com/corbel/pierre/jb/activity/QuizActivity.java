package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.CountDownTimerWithPause;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.lib.LeaderBoardHelper;
import com.corbel.pierre.jb.lib.MediaPlayerHelper;
import com.corbel.pierre.jb.lib.Question;
import com.corbel.pierre.jb.view.BeautifulButton;
import com.corbel.pierre.jb.view.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class QuizActivity extends Activity {

    @BindView(R.id.question_card_view)
    CardView questionCardView;
    @BindView(R.id.question_text_view)
    AutoResizeTextView questionTextView;

    @BindView(R.id.button_1)
    BeautifulButton button_1;
    @BindView(R.id.button_2)
    BeautifulButton button_2;
    @BindView(R.id.button_3)
    BeautifulButton button_3;
    @BindView(R.id.button_4)
    BeautifulButton button_4;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.count_down_text_view)
    AutoResizeTextView countDownTextView;
    @BindView(R.id.score_text_view)
    AutoResizeTextView scoreTextView;
    @BindView(R.id.id_text_view)
    AutoResizeTextView idTextView;
    @BindView(R.id.bonus_text_view)
    TextView bonusTextView;

    private Vibrator vibrator;
    private int score = 0;
    private int questionId = 0;
    private int consecutiveGoodAnswer = 0;
    private int joker = 0;
    private boolean hasNotConsumedJoker = true;
    private Animation questionCardViewAnimation;
    private Animation jokerAnimation;
    private Animation bonusAnimation;
    private Animation fabAnimation;
    private Animation button_1_animation;
    private Animation button_2_animation;
    private Animation button_3_animation;
    private Animation button_4_animation;
    private DbHelper db;
    private Question currentQuestion;
    private Handler handler = new Handler();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer;

    public CountDownTimerWithPause countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        setStatusBarColor(this);
        mediaPlayer = MediaPlayerHelper.initializeMusicPlayer(this, R.raw.funky);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Prepare FAB and footer info
        fab.setProgress(0, true);
        fab.setIndeterminate(false);
        scoreTextView.setText(getString(R.string.quiz_score, score));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Init Animations
        questionCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        button_1_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        button_2_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        button_3_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        button_4_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        jokerAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        bonusAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Init DB and Shuffle
        db = DbHelper.getInstance(this);
        db.onUpgrade(db.getWritableDatabase(), 0, 1);

        // Start Quiz
        startCountDown();
        startQuestion(++questionId);

        // Anim In
        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    private void startQuestion(int questionId) {

        idTextView.setText(getString(R.string.quiz_simple_question_id, questionId));
        currentQuestion = db.getQuestion(questionId);

        questionCardView.setVisibility(View.INVISIBLE);
        button_1.setVisibility(View.INVISIBLE);
        button_2.setVisibility(View.INVISIBLE);
        button_3.setVisibility(View.INVISIBLE);
        button_4.setVisibility(View.INVISIBLE);

        List<String> answers = new ArrayList<>();
        answers.add(currentQuestion.getAnswer_1());
        answers.add(currentQuestion.getAnswer_2());
        answers.add(currentQuestion.getAnswer_3());
        answers.add(currentQuestion.getAnswer_4());

        int optionsNumber = 0;
        for (Object answer : answers) {
            if (!String.valueOf(answer).isEmpty()) {
                optionsNumber++;
            }
        }

        // In case of only two answers
        if (optionsNumber == 2) {

            answers.remove(3);
            answers.remove(2);

            Collections.shuffle(answers);

            questionTextView.setText(currentQuestion.getQuestion());
            button_2.setText(answers.get(0));
            button_3.setText(answers.get(1));

            questionCardViewAnimation.setStartOffset(0);
            questionCardView.setVisibility(View.VISIBLE);
            questionCardView.startAnimation(questionCardViewAnimation);

            button_2_animation.setStartOffset(200);
            button_2.setVisibility(View.VISIBLE);
            button_2.startAnimation(button_2_animation);

            button_3_animation.setStartOffset(400);
            button_3.setVisibility(View.VISIBLE);
            button_3.startAnimation(button_3_animation);

        } else if (optionsNumber == 4) {

            Collections.shuffle(answers);

            questionTextView.setText(currentQuestion.getQuestion());
            button_1.setText(answers.get(0));
            button_2.setText(answers.get(1));
            button_3.setText(answers.get(2));
            button_4.setText(answers.get(3));

            questionCardViewAnimation.setStartOffset(0);
            questionCardView.setVisibility(View.VISIBLE);
            questionCardView.startAnimation(questionCardViewAnimation);

            button_1_animation.setStartOffset(100);
            button_1.setVisibility(View.VISIBLE);
            button_1.startAnimation(button_1_animation);

            button_2_animation.setStartOffset(200);
            button_2.setVisibility(View.VISIBLE);
            button_2.startAnimation(button_2_animation);

            button_3_animation.setStartOffset(300);
            button_3.setVisibility(View.VISIBLE);
            button_3.startAnimation(button_3_animation);

            button_4_animation.setStartOffset(400);
            button_4.setVisibility(View.VISIBLE);
            button_4.startAnimation(button_4_animation);
        }
    }

    @OnClick({R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4})
    public void checkAnswer(BeautifulButton button) {
        String answer = button.getText();
        if (answer.equals(currentQuestion.getGoodAnswer())) {
            onWin();
        } else {
            onFail(button);
        }
    }

    private void onWin() {
        consecutiveGoodAnswer++;
        AchievementHelper.checkConsecutiveAchievement(this, consecutiveGoodAnswer);

        score += 10;
        checkBonus(consecutiveGoodAnswer);

        // FAB
        if (consecutiveGoodAnswer < 4) {
            fab.setProgress(consecutiveGoodAnswer * 25, true);
        } else if (consecutiveGoodAnswer == 4) {
            fab.setProgress(consecutiveGoodAnswer * 25, true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.setIndeterminate(true);
                    fab.setImageResource(R.drawable.heart);
                    fab.startAnimation(jokerAnimation);
                }
            }, 500);
            joker = 1;
        }

        scoreTextView.setText(getString(R.string.quiz_score, score));

        //questionId = 30;
        if (questionId < 30) {
            startQuestion(++questionId);
        } else {
            AchievementHelper.checkFastAchievement(this, countDown.timePassed());
            LeaderBoardHelper.incrementFinishedGames(this);
            animateOutTo(FinishActivity.class);
        }
    }

    private void onFail(BeautifulButton button) {
        // Vibrate
        if (preferences.getBoolean("VIBRATOR_PREF", true)) {
            vibrator.vibrate(300);
        }

        // FAB
        fab.setIndeterminate(false);
        fab.setProgress(0, true);
        fab.setImageResource(R.drawable.heart_broken);

        if (joker == 1) {
            joker = 0;
            consecutiveGoodAnswer = 0;
            fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
            button.setVisibility(View.INVISIBLE);
            button.startAnimation(fabAnimation);
            fab.startAnimation(fabAnimation);
            AchievementHelper.checkJokerAchievement(this);
            LeaderBoardHelper.incrementUsedJokers(this);
        } else {
            int jokerInStock = preferences.getInt("JOKER_IN_STOCK", 5);
            if (jokerInStock > 0 && hasNotConsumedJoker && questionId >= 10) {
                createJokerDialog(jokerInStock);
                button.setVisibility(View.INVISIBLE);
                button.startAnimation(fabAnimation);
            } else {
                button_1.setClickable(false);
                button_2.setClickable(false);
                button_3.setClickable(false);
                button_4.setClickable(false);
                animateOutTo(ResultActivity.class);
            }
        }
    }

    private void startCountDown() {
        countDown = new CountDownTimerWithPause(240000, 100, true) {

            int progress = 0;
            long timeLeft = 0;
            String globalTime = null;
            boolean blinking = false;

            @Override
            public void onTick(long millisUntilFinished) {
                progress++;
                timeLeft = millisUntilFinished;
                globalTime = Helper.convertTime(Math.round(timeLeft()));
                countDownTextView.setText(globalTime);
                if (timeLeft <= 30000 && !blinking) {
                    Animation animation = AnimationUtils.loadAnimation(countDownTextView.getContext(), R.anim.blink);
                    countDownTextView.startAnimation(animation);
                    blinking = true;
                }
            }

            @Override
            public void onFinish() {
                animateOutTo(ResultActivity.class);
            }
        };

        countDown.create();
    }

    private void checkBonus(int bonus) {

        boolean shouldAnimate = false;

        switch (bonus) {
            case 5:
                bonusTextView.setText("Série de 5\n+50");
                score += 50;
                shouldAnimate = true;
                break;
            case 10:
                bonusTextView.setText("Série de 10\n+150");
                score += 150;
                shouldAnimate = true;
                break;
            case 15:
                bonusTextView.setText("Série de 15\n+300");
                score += 300;
                shouldAnimate = true;
                break;
            case 20:
                bonusTextView.setText("Série de 20\n+500");
                score += 500;
                shouldAnimate = true;
                break;
            case 25:
                bonusTextView.setText("Série de 25\n+1000");
                score += 1000;
                shouldAnimate = true;
                break;
            case 30:
                bonusTextView.setText("Série de 30\n+2000");
                score += 2000;
                shouldAnimate = true;
                break;
        }

        if (shouldAnimate) {
            bonusTextView.setVisibility(View.VISIBLE);
            bonusTextView.startAnimation(bonusAnimation);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bonusTextView.setVisibility(View.GONE);
                }
            }, 700);
        }
    }

    public void createJokerDialog(int jokersLeft) {
        countDown.pause();
        new MaterialDialog.Builder(this)
                .iconRes(R.mipmap.ic_launcher)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.quiz_continue)
                .content(getString(R.string.quiz_jokers_left, jokersLeft))
                .positiveText("Oui")
                .negativeText("Non")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferences.Editor editor = preferences.edit();
                        int jokerInStock = preferences.getInt("JOKER_IN_STOCK", 5);
                        editor.putInt("JOKER_IN_STOCK", --jokerInStock);
                        editor.apply();
                        countDown.resume();
                        hasNotConsumedJoker = false;
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        animateOutTo(ResultActivity.class);
                    }
                })
                .canceledOnTouchOutside(false)
                .show();
    }

    public void animateOutTo(final Class toActivity) {

        if (questionCardView.getVisibility() == View.VISIBLE) {
            questionCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
            questionCardViewAnimation.setStartOffset(0);
            questionCardView.setVisibility(View.INVISIBLE);
            questionCardView.startAnimation(questionCardViewAnimation);
        }

        if (button_1.getVisibility() == View.VISIBLE) {
            button_1_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
            button_1_animation.setStartOffset(100);
            button_1.setVisibility(View.INVISIBLE);
            button_1.startAnimation(button_1_animation);
        }

        if (button_2.getVisibility() == View.VISIBLE) {
            button_2_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
            button_2_animation.setStartOffset(200);
            button_2.setVisibility(View.INVISIBLE);
            button_2.startAnimation(button_2_animation);
        }

        if (button_3.getVisibility() == View.VISIBLE) {
            button_3_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
            button_3_animation.setStartOffset(300);
            button_3.setVisibility(View.INVISIBLE);
            button_3.startAnimation(button_3_animation);
        }

        if (button_4.getVisibility() == View.VISIBLE) {
            button_4_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
            button_4_animation.setStartOffset(400);
            button_4.setVisibility(View.INVISIBLE);
            button_4.startAnimation(button_4_animation);
        }

        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        AchievementHelper.checkQuestionsAchievement(this, questionId);
        LeaderBoardHelper.incrementAnsweredQuestions(this, questionId);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countDown.pause();
                Intent intent = new Intent(QuizActivity.this, toActivity);
                intent.putExtra("questionId", questionId);
                intent.putExtra("timeLeft", Helper.convertTime(Math.round(countDown.timeLeft())));
                intent.putExtra("timePassed", countDown.timePassed());
                intent.putExtra("score", score);

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 700);
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDown.pause();
        MediaPlayerHelper.pausePlayer(mediaPlayer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDown.resume();
        MediaPlayerHelper.resumePlayer(mediaPlayer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDown.cancel();
        MediaPlayerHelper.closePlayer(mediaPlayer);
    }

    @Override
    public void onBackPressed() {
        animateOutTo(HomeActivity.class);
    }
}