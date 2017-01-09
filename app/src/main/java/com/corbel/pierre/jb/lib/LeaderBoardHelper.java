package com.corbel.pierre.jb.lib;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.activity.CountDownActivity;
import com.corbel.pierre.jb.activity.FinishActivity;
import com.corbel.pierre.jb.activity.HomeActivity;
import com.corbel.pierre.jb.activity.QuizActivity;
import com.corbel.pierre.jb.activity.ResultActivity;
import com.google.android.gms.games.Games;

public class LeaderBoardHelper {

    public static void displayLeaderBoard(HomeActivity activity) {
        Games.setViewForPopups(activity.mGameHelper.getApiClient(), activity.getWindow().getDecorView().findViewById(android.R.id.content));
        activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(activity.mGameHelper.getApiClient()), 9002);
    }

    public static void displayLeaderBoard(FinishActivity activity) {
        Games.setViewForPopups(activity.mGameHelper.getApiClient(), activity.getWindow().getDecorView().findViewById(android.R.id.content));
        activity.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(activity.mGameHelper.getApiClient()), 9002);
    }

    public static void updateLocalBestScore(Activity activity, int score) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        int bestScore = preferences.getInt("BEST_SCORE", 0);
        editor.putInt("BEST_SCORE", score > bestScore ? score : bestScore);
        editor.apply();
    }

    public static void updateGamesBestScore(FinishActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Games.Leaderboards.submitScore(activity.mGameHelper.getApiClient(), activity.getString(R.string.leaderboard_meilleur_score), preferences.getInt("BEST_SCORE", 0));
    }

    public static void updateGamesBestScore(ResultActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Games.Leaderboards.submitScore(activity.mGameHelper.getApiClient(), activity.getString(R.string.leaderboard_meilleur_score), preferences.getInt("BEST_SCORE", 0));
    }

    public static void incrementLocalPushedPlayed(CountDownActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        int playedGames = preferences.getInt("PLAYED_GAMES", 0);
        editor.putInt("PLAYED_GAMES", ++playedGames);
        editor.apply();
    }

    public static void incrementGamesPushedPlayed(CountDownActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Games.Leaderboards.submitScore(activity.mGameHelper.getApiClient(), activity.getString(R.string.leaderboard_parties_joues), preferences.getInt("PLAYED_GAMES", 0));
    }

    public static void incrementLocalFinishedGames(FinishActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        int finishedGames = preferences.getInt("FINISHED_GAMES", 0);
        editor.putInt("FINISHED_GAMES", ++finishedGames);
        editor.apply();
    }

    public static void incrementGamesFinishedGames(FinishActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Games.Leaderboards.submitScore(activity.mGameHelper.getApiClient(), activity.getString(R.string.leaderboard_parties_termines), preferences.getInt("FINISHED_GAMES", 0));
    }

    // Not local and games because called at the end of the activity
    public static void incrementAnsweredQuestions(QuizActivity activity, int questions) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        int answeredQuestions = preferences.getInt("ANSWERED_QUESTIONS", 0);
        editor.putInt("ANSWERED_QUESTIONS", answeredQuestions + questions);
        editor.apply();

        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (activity.mGameHelper.getApiClient().isConnected()) {
                Games.Leaderboards.submitScore(activity.mGameHelper.getApiClient(), activity.getString(R.string.leaderboard_questions_rpondues), preferences.getInt("ANSWERED_QUESTIONS", 0));
            }
        }
    }

    // Not local and games because called at the end of the activity
    public static void incrementUsedJokers(QuizActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        int usedJokers = preferences.getInt("USED_JOKERS", 0);
        editor.putInt("USED_JOKERS", ++usedJokers);
        editor.apply();

        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (activity.mGameHelper.getApiClient().isConnected()) {
                Games.Leaderboards.submitScore(activity.mGameHelper.getApiClient(), activity.getString(R.string.leaderboard_jokers_utiliss), preferences.getInt("USED_JOKERS", 0));
            }
        }
    }
}