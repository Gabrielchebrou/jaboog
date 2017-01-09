package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.DuelTurn;
import com.corbel.pierre.jb.lib.GameHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.view.BeautifulButtonWithImage;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class DuelActivity extends Activity
        implements GameHelper.GameHelperListener {

    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;
    public GameHelper mGameHelper;
    public TurnBasedMatch mMatch;
    public DuelTurn mTurnData;
    @BindView(R.id.check_match_button)
    BeautifulButtonWithImage checkButton;
    @BindView(R.id.start_match_button)
    BeautifulButtonWithImage startButton;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Animation checkButtonAnimation;
    private Animation startButtonAnimation;
    private Animation fabAnimation;
    private SharedPreferences preferences;
    private Handler handler = new Handler();
    private boolean isTryingToStartMatch = false;
    private boolean isTryingToCheckMatch = false;
    private String TAG = "DuelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mGameHelper.setup(this);
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            mGameHelper.beginUserInitiatedSignIn();
        }

        // Init Animations
        checkButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        startButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        startButtonAnimation.setStartOffset(0);
        startButton.setVisibility(View.VISIBLE);
        startButton.startAnimation(startButtonAnimation);

        checkButtonAnimation.setStartOffset(100);
        checkButton.setVisibility(View.VISIBLE);
        checkButton.startAnimation(checkButtonAnimation);

        fabAnimation.setStartOffset(200);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    @OnClick(R.id.start_match_button)
    public void initializeMatch() {
        try {
            Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGameHelper.getApiClient(), 1, 7, true);
            startActivityForResult(intent, RC_SELECT_PLAYERS);
        } catch (Exception e) {
            e.printStackTrace();
            isTryingToStartMatch = true;
            mGameHelper.beginUserInitiatedSignIn();
        }
    }

    @OnClick(R.id.check_match_button)
    public void checkMatch() {

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

        checkButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        startButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        startButtonAnimation.setStartOffset(0);
        startButton.setVisibility(View.INVISIBLE);
        startButton.startAnimation(startButtonAnimation);

        checkButtonAnimation.setStartOffset(100);
        checkButton.setVisibility(View.INVISIBLE);
        checkButton.startAnimation(checkButtonAnimation);

        fabAnimation.setStartOffset(200);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(DuelActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
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
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
        if (isTryingToStartMatch) {
            isTryingToStartMatch = false;
            initializeMatch();
        }

        if (isTryingToCheckMatch) {
            isTryingToCheckMatch = false;
            checkMatch();
        }
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SELECT_PLAYERS && response == Activity.RESULT_OK) {

            // Get the invitee list
            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            autoMatchCriteria = minAutoMatchPlayers > 0 ? RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0) : null;

            TurnBasedMatchConfig turnBasedMatchConfig = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(mGameHelper.getApiClient(), turnBasedMatchConfig).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    });
        }
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (checkStatusCode(match, result.getStatus().getStatusCode())) {
            if (match.getData() != null) {
                //TODO
                //updateMatch(match);
            } else if (match.getData() == null) {
                startMatch(match);
            }
        }
    }

    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (checkStatusCode(match, result.getStatus().getStatusCode())) {
            if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                //TODO
                //updateMatch(match);
            }
        }

        // TODO : Implement rematch
        /*
        if (match.canRematch()) {
            Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        isDoingTurn = false;
        }*/
    }

    public void startMatch(TurnBasedMatch match) {
        mTurnData = new DuelTurn();
        mTurnData.questionId = 0;
        mTurnData.score = 0;
        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGameHelper.getApiClient());
        String myParticipantId = mMatch.getParticipantId(playerId);

        Games.TurnBasedMultiplayer.takeTurn(mGameHelper.getApiClient(), match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }

    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        boolean isOkay = false;

        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                isOkay = true;
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                Log.d(TAG, "Stored action for later.");
                isOkay = true;
                break;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                Log.d(TAG, getString(R.string.status_multiplayer_error_not_trusted_tester));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                Log.d(TAG, getString(R.string.match_error_already_rematched));
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                Log.d(TAG, getString(R.string.network_error_operation_failed));
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                Log.d(TAG, getString(R.string.client_reconnect_required));
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                Log.d(TAG, getString(R.string.internal_error));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                Log.d(TAG, getString(R.string.match_error_inactive_match));
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                Log.d(TAG, getString(R.string.match_error_locally_modified));
                break;
            default:
                Log.d(TAG, getString(R.string.unexpected_status));
                Log.d(TAG, "Did not have warning or string to deal with: " + statusCode);
        }

        return isOkay;
    }
}