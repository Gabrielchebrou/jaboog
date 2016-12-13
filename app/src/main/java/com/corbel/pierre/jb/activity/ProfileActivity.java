package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TableRow;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.GameHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.view.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.corbel.pierre.jb.lib.Helper.noInternet;
import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class ProfileActivity extends Activity
        implements GameHelper.GameHelperListener {

    public GameHelper mGameHelper;
    @BindView(R.id.profile_image_view)
    CircleImageView profileImageView;
    @BindView(R.id.profile_text_view)
    AutoResizeTextView profileTextView;
    @BindView(R.id.game_played_text_view)
    AutoResizeTextView gamePlayedTextView;
    @BindView(R.id.game_finished_text_view)
    AutoResizeTextView gameFinishedTextView;
    @BindView(R.id.question_answered_text_view)
    AutoResizeTextView questionAnsweredTextView;
    @BindView(R.id.joker_used_text_view)
    AutoResizeTextView jokerUsedTextView;
    @BindView(R.id.joker_available_text_view)
    AutoResizeTextView jokerAvailableTextView;
    @BindView(R.id.best_score_text_view)
    AutoResizeTextView bestScoreTextView;
    @BindView(R.id.stats_viewed_text_view)
    AutoResizeTextView statsViewedTextView;
    @BindView(R.id.profile_card_view)
    CardView profileCardView;
    @BindView(R.id.game_played_table_row)
    TableRow gamePlayedTableRow;
    @BindView(R.id.game_finished_table_row)
    TableRow gameFinishedTableRow;
    @BindView(R.id.question_answered_table_row)
    TableRow questionAnsweredTableRow;
    @BindView(R.id.joker_used_table_row)
    TableRow jokerUsedTableRow;
    @BindView(R.id.joker_available_table_row)
    TableRow jokerAvailableTableRow;
    @BindView(R.id.best_score_table_row)
    TableRow bestScoreTableRow;
    @BindView(R.id.stats_viewed_table_row)
    TableRow statsViewedTableRow;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Animation profileCardViewAnimation;
    private Animation gamePlayedTableRowAnimation;
    private Animation gameFinishedTableRowAnimation;
    private Animation questionAnsweredTableRowAnimation;
    private Animation jokerUsedTableRowAnimation;
    private Animation jokerAvailableTableRowAnimation;
    private Animation bestScoreTableRowAnimation;
    private Animation statsViewedTableRowAnimation;
    private Animation fabAnimation;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mGameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mGameHelper.setup(this);
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            mGameHelper.beginUserInitiatedSignIn();
        }

        AchievementHelper.checkStatsAchievement(this);

        profileTextView.setText(preferences.getString("NAME_PREF", "Joueur"));
        profileImageView.setImageBitmap(Helper.getUserPicture(this, "black"));
        gamePlayedTextView.setText(getString(R.string.profile_number_info, preferences.getInt("PLAYED_GAMES", 0)));
        gameFinishedTextView.setText(getString(R.string.profile_number_info, preferences.getInt("FINISHED_GAMES", 0)));
        questionAnsweredTextView.setText(getString(R.string.profile_number_info, preferences.getInt("ANSWERED_QUESTIONS", 0)));
        jokerUsedTextView.setText(getString(R.string.profile_number_info, preferences.getInt("USED_JOKERS", 0)));
        jokerAvailableTextView.setText(getString(R.string.profile_number_info, preferences.getInt("JOKER_IN_STOCK", 0)));
        bestScoreTextView.setText(getString(R.string.profile_number_info, preferences.getInt("BEST_SCORE", 0)));
        statsViewedTextView.setText(getString(R.string.profile_number_info, preferences.getInt("VIEWED_STATS", 0)));

        // Init Animations
        profileCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        gamePlayedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        gameFinishedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        questionAnsweredTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        jokerUsedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        jokerAvailableTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        bestScoreTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        statsViewedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        profileCardViewAnimation.setStartOffset(0);
        profileCardView.setVisibility(View.VISIBLE);
        profileCardView.startAnimation(profileCardViewAnimation);

        gamePlayedTableRowAnimation.setStartOffset(200);
        gamePlayedTableRow.setVisibility(View.VISIBLE);
        gamePlayedTableRow.startAnimation(gamePlayedTableRowAnimation);

        gameFinishedTableRowAnimation.setStartOffset(300);
        gameFinishedTableRow.setVisibility(View.VISIBLE);
        gameFinishedTableRow.startAnimation(gameFinishedTableRowAnimation);

        questionAnsweredTableRowAnimation.setStartOffset(400);
        questionAnsweredTableRow.setVisibility(View.VISIBLE);
        questionAnsweredTableRow.startAnimation(questionAnsweredTableRowAnimation);

        jokerUsedTableRowAnimation.setStartOffset(500);
        jokerUsedTableRow.setVisibility(View.VISIBLE);
        jokerUsedTableRow.startAnimation(jokerUsedTableRowAnimation);

        jokerAvailableTableRowAnimation.setStartOffset(600);
        jokerAvailableTableRow.setVisibility(View.VISIBLE);
        jokerAvailableTableRow.startAnimation(jokerAvailableTableRowAnimation);

        bestScoreTableRowAnimation.setStartOffset(700);
        bestScoreTableRow.setVisibility(View.VISIBLE);
        bestScoreTableRow.startAnimation(bestScoreTableRowAnimation);

        statsViewedTableRowAnimation.setStartOffset(800);
        statsViewedTableRow.setVisibility(View.VISIBLE);
        statsViewedTableRow.startAnimation(statsViewedTableRowAnimation);

        fabAnimation.setStartOffset(900);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
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

        profileCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        gamePlayedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        gameFinishedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        questionAnsweredTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        jokerUsedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        jokerAvailableTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        bestScoreTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        statsViewedTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        profileCardViewAnimation.setStartOffset(0);
        profileCardView.setVisibility(View.INVISIBLE);
        profileCardView.startAnimation(profileCardViewAnimation);

        gamePlayedTableRowAnimation.setStartOffset(200);
        gamePlayedTableRow.setVisibility(View.INVISIBLE);
        gamePlayedTableRow.startAnimation(gamePlayedTableRowAnimation);

        gameFinishedTableRowAnimation.setStartOffset(300);
        gameFinishedTableRow.setVisibility(View.INVISIBLE);
        gameFinishedTableRow.startAnimation(gameFinishedTableRowAnimation);

        questionAnsweredTableRowAnimation.setStartOffset(400);
        questionAnsweredTableRow.setVisibility(View.INVISIBLE);
        questionAnsweredTableRow.startAnimation(questionAnsweredTableRowAnimation);

        jokerUsedTableRowAnimation.setStartOffset(500);
        jokerUsedTableRow.setVisibility(View.INVISIBLE);
        jokerUsedTableRow.startAnimation(jokerUsedTableRowAnimation);

        jokerAvailableTableRowAnimation.setStartOffset(600);
        jokerAvailableTableRow.setVisibility(View.INVISIBLE);
        jokerAvailableTableRow.startAnimation(jokerAvailableTableRowAnimation);

        bestScoreTableRowAnimation.setStartOffset(700);
        bestScoreTableRow.setVisibility(View.INVISIBLE);
        bestScoreTableRow.startAnimation(bestScoreTableRowAnimation);

        statsViewedTableRowAnimation.setStartOffset(800);
        statsViewedTableRow.setVisibility(View.INVISIBLE);
        statsViewedTableRow.startAnimation(statsViewedTableRowAnimation);

        fabAnimation.setStartOffset(900);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(ProfileActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 1000);
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
        // NO-OP
    }
}
