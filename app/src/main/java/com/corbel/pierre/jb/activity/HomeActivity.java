package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.ArchiveDownloader;
import com.corbel.pierre.jb.downloader.PictureDownloader;
import com.corbel.pierre.jb.lib.AchievementHelper;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.CheckForUpdate;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.GameHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.lib.LeaderBoardHelper;
import com.corbel.pierre.jb.lib.MediaPlayerHelper;
import com.corbel.pierre.jb.lib.Serie;
import com.corbel.pierre.jb.view.BeautifulButtonWithImage;
import com.corbel.pierre.jb.view.FloatingActionButton;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.corbel.pierre.jb.lib.Helper.noInternet;
import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class HomeActivity extends Activity
        implements GameHelper.GameHelperListener, NavigationView.OnNavigationItemSelectedListener {

    public GameHelper mGameHelper;
    @BindView(R.id.play_button)
    BeautifulButtonWithImage playButton;
    @BindView(R.id.achievement_button)
    BeautifulButtonWithImage achievementButton;
    @BindView(R.id.leaderboard_button)
    BeautifulButtonWithImage leaderboardButton;
    @BindView(R.id.current_button)
    BeautifulButtonWithImage currentButton;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.ad_view)
    AdView adView;
    AutoResizeTextView profileTextView;
    CircleImageView profileImageView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Animation playButtonAnimation;
    private Animation achievementButtonAnimation;
    private Animation leaderboardButtonAnimation;
    private Animation currentButtonAnimation;
    private Animation fabAnimation;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private DbHelper db;
    private boolean isTryingToConnectAchievement = false;
    private boolean isTryingToConnectLeaderBoard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setStatusBarColor(this);
        ButterKnife.bind(this);
        prepareNavigationDrawer();
        mediaPlayer = MediaPlayerHelper.initializeMusicPlayer(this, R.raw.elevator);
        Helper.setAlarm(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mGameHelper.setup(this);
        if (preferences.getBoolean("IS_GOOGLE_CONN", false)) {
            mGameHelper.beginUserInitiatedSignIn();
        }

        // Disabled for first launch
        /*if (preferences.getBoolean("AD_ENABLED", true)) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.ad_nexus_5_id))
                    .addTestDevice(getString(R.string.ad_nexus_5_X_id))
                    .build();
            adView.loadAd(adRequest);
        }*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileTextView = (AutoResizeTextView) headerView.findViewById(R.id.profile_text_view);
        profileImageView = (CircleImageView) headerView.findViewById(R.id.profile_image_view);
        profileTextView.setText(preferences.getString("NAME_PREF", "Joueur"));
        profileImageView.setImageBitmap(Helper.getUserPicture(this, "white"));

        db = DbHelper.getInstance(this);
        int id = preferences.getInt("CURRENT_SERIE_ID_PREF", 0);
        if (id == 0) {
            db.createTablesIfNotExists(db.getWritableDatabase());
        } else {
            Serie serie = db.getSerie(id);
            currentButton.setText(serie.getName());
        }

        new CheckForUpdate(this).execute(getResources().getString(R.string.server_version));

        // Init Animations
        playButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        achievementButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        leaderboardButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        currentButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        playButtonAnimation.setStartOffset(0);
        playButton.setVisibility(View.VISIBLE);
        playButton.startAnimation(playButtonAnimation);

        achievementButtonAnimation.setStartOffset(100);
        achievementButton.setVisibility(View.VISIBLE);
        achievementButton.startAnimation(achievementButtonAnimation);

        leaderboardButtonAnimation.setStartOffset(200);
        leaderboardButton.setVisibility(View.VISIBLE);
        leaderboardButton.startAnimation(leaderboardButtonAnimation);

        currentButtonAnimation.setStartOffset(300);
        currentButton.setVisibility(View.VISIBLE);
        currentButton.startAnimation(currentButtonAnimation);

        fabAnimation.setStartOffset(400);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);
    }

    @OnClick(R.id.play_button)
    public void play() {
        if (preferences.getBoolean("IS_DB_READY", false) && preferences.getInt("CURRENT_SERIE_ID_PREF", 0) != 0) {
            animateOutTo(CountDownActivity.class);
        } else {
            animateOutTo(ArchiveActivity.class);
        }
    }

    @OnClick(R.id.achievement_button)
    public void startAchievement() {
        try {
            AchievementHelper.displayAchievement(this);
        } catch (Exception e) {
            e.printStackTrace();
            isTryingToConnectAchievement = true;
            mGameHelper.beginUserInitiatedSignIn();
        }
    }

    @OnClick(R.id.leaderboard_button)
    public void startLeaderBoard() {
        try {
            LeaderBoardHelper.displayLeaderBoard(this);
        } catch (Exception e) {
            e.printStackTrace();
            isTryingToConnectLeaderBoard = true;
            mGameHelper.beginUserInitiatedSignIn();
        }
    }

    @OnClick(R.id.current_button)
    public void browseHistory() {
        if (preferences.getBoolean("IS_ARCHIVE_READY", false)) {
            animateOutTo(ArchiveActivity.class);
        } else {
            new ArchiveDownloader(this).execute(getResources().getString(R.string.server_archive));
        }
    }

    @OnClick(R.id.fab)
    public void buy() {
        animateOutTo(SkeletonActivity.class);
        //animateOutTo(IAPActivity.class);
    }

    public void prepareNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            animateOutTo(ProfileActivity.class);
        } else if (id == R.id.nav_settings) {
            animateOutTo(SettingsActivity.class);
        } else if (id == R.id.nav_update) {
            new ArchiveDownloader(this).execute(getResources().getString(R.string.server_archive));
        } else if (id == R.id.nav_rate) {
            String appId = getResources().getString(R.string.package_name);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appId));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                noInternet(this);
            }
        } else if (id == R.id.nav_about) {
            animateOutTo(AboutActivity.class);
        } else if (id == R.id.nav_help) {
            animateOutTo(TutorialActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void animateOutTo(final Class toActivity) {

        playButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        achievementButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        leaderboardButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        currentButtonAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        playButtonAnimation.setStartOffset(0);
        playButton.setVisibility(View.INVISIBLE);
        playButton.startAnimation(playButtonAnimation);

        achievementButtonAnimation.setStartOffset(100);
        achievementButton.setVisibility(View.INVISIBLE);
        achievementButton.startAnimation(achievementButtonAnimation);

        leaderboardButtonAnimation.setStartOffset(200);
        leaderboardButton.setVisibility(View.INVISIBLE);
        leaderboardButton.startAnimation(leaderboardButtonAnimation);

        currentButtonAnimation.setStartOffset(300);
        currentButton.setVisibility(View.INVISIBLE);
        currentButton.startAnimation(currentButtonAnimation);

        fabAnimation.setStartOffset(400);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(HomeActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 600);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerHelper.pausePlayer(mediaPlayer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayerHelper.resumePlayer(mediaPlayer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaPlayerHelper.closePlayer(mediaPlayer);
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
                new PictureDownloader(this).execute(photo.toString());
            } else {
                new PictureDownloader(this).execute(getString(R.string.server_photo));
            }
        }

        if (isTryingToConnectAchievement) {
            isTryingToConnectAchievement = false;
            startAchievement();
        }

        if (isTryingToConnectLeaderBoard) {
            isTryingToConnectLeaderBoard = false;
            startLeaderBoard();
        }
    }
}