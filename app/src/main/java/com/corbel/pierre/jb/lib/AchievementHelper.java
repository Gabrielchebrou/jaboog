package com.corbel.pierre.jb.lib;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.activity.CountDownActivity;
import com.corbel.pierre.jb.activity.ProfileActivity;
import com.corbel.pierre.jb.activity.QuizActivity;
import com.corbel.pierre.jb.app.Jaboog;
import com.google.android.gms.games.Games;

public class AchievementHelper {

    public static void checkConsecutiveAchievement(QuizActivity activity, int consecutive) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (Jaboog.getGoogleApiHelper().mGoogleApiClient.isConnected()) {
                switch (consecutive) {
                    case 5:
                        Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_etudiant));
                        break;
                    case 10:
                        Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_diplm));
                        break;
                    case 15:
                        Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_chercheur));
                        break;
                    case 20:
                        Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_sage));
                        break;
                    case 25:
                        Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_savant));
                        break;
                    case 30:
                        Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_erudit));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static void checkFastAchievement(QuizActivity activity, long timePassed) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (Jaboog.getGoogleApiHelper().mGoogleApiClient.isConnected()) {
                if (timePassed < 150000) {
                    Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_escargot));
                }
                if (timePassed < 120000) {
                    Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_tortue));
                }
                if (timePassed < 90000) {
                    Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_hrisson));
                }
                if (timePassed < 60000) {
                    Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_livre));
                }
                if (timePassed < 45000) {
                    Games.Achievements.unlock(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_springbok));
                }
            }
        }
    }

    public static void checkQuestionsAchievement(QuizActivity activity, int questionsAnswered) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (Jaboog.getGoogleApiHelper().mGoogleApiClient.isConnected()) {
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_novice), questionsAnswered);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_apprenti), questionsAnswered);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_confirm), questionsAnswered);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_important), questionsAnswered);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_influent), questionsAnswered);
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        int questionsAnsweredPref = preferences.getInt("QUESTIONS_ANSWERED", 0);
        editor.putInt("QUESTIONS_ANSWERED", questionsAnsweredPref + questionsAnswered);
        editor.apply();
    }

    public static void checkJokerAchievement(QuizActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (Jaboog.getGoogleApiHelper().mGoogleApiClient.isConnected()) {
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_un_peu___), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_beaucoup___), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_passionnment___), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_a_la_folie), 1);
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        int jokerUsed = preferences.getInt("JOKER_USED", 0);
        editor.putInt("JOKER_USED", ++jokerUsed);
        editor.apply();
    }

    public static void checkPushPlayedAchievement(CountDownActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (Jaboog.getGoogleApiHelper().mGoogleApiClient.isConnected()) {
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_influ), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_accoutum), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_dpendant), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_toxicomane), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_accroc), 1);
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        int jokerUsed = preferences.getInt("JOKER_USED", 0);
        editor.putInt("JOKER_USED", ++jokerUsed);
        editor.apply();
    }

    public static void checkStatsAchievement(ProfileActivity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("IS_GOOGLE_CONN", true)) {
            if (Jaboog.getGoogleApiHelper().mGoogleApiClient.isConnected()) {
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_data_analyst), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_data_architect), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_data_scientist), 1);
                Games.Achievements.increment(Jaboog.getGoogleApiHelper().mGoogleApiClient, activity.getResources().getString(R.string.achievement_data_engineer), 1);
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        int viewedStats = preferences.getInt("VIEWED_STATS", 0);
        editor.putInt("VIEWED_STATS", ++viewedStats);
        editor.apply();
    }
}