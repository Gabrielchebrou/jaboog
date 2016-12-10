package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.SerieDownloader;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.lib.Serie;
import com.corbel.pierre.jb.view.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class ArchiveActivity extends Activity {

    @BindView(R.id.header_card_view)
    CardView headerCardView;
    @BindView(R.id.scroll_view)
    LinearLayout scrollView;
    @BindView(R.id.archive_text_view)
    AutoResizeTextView archiveTextView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private String archive;
    private SharedPreferences preferences;

    private Animation headerCardViewAnimation;
    private Animation scrollViewAnimation;
    private Animation fabAnimation;

    private Handler handler = new Handler();
    private DbHelper db;
    private boolean isFirstCard = true;
    private int cardNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Init Animations
        headerCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        scrollViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        headerCardViewAnimation.setStartOffset(0);
        headerCardView.setVisibility(View.VISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        scrollViewAnimation.setStartOffset(100);
        scrollView.setVisibility(View.VISIBLE);
        scrollView.startAnimation(scrollViewAnimation);

        fabAnimation.setStartOffset(200);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);

        try {
            archive = readFromRaw(this);
        } catch (Exception e) {
            // NO-OP
        }

        db = DbHelper.getInstance(this);

        if (archive != null) {
            String[] archiveList = archive.split("\n");

            for (String archive : archiveList) {
                String[] elements = archive.split(";");
                db.createSerieIfNotExists(db.getWritableDatabase(), elements[0], elements[1], elements[2]);
                createRow(Integer.parseInt(elements[0]));
            }
        }
    }

    @OnClick(R.id.fab)
    public void home() {
        animateOutTo(HomeActivity.class);
    }

    @Override
    public void onBackPressed() {
        animateOutTo(HomeActivity.class);
    }

    public void createRow(final int id) {

        final Serie serie = db.getSerie(id);
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView serieCardView = (CardView) inflater.inflate(R.layout.serie_card, null, false);

        AutoResizeTextView serieName = (AutoResizeTextView) serieCardView.findViewById(R.id.serie_name);
        serieName.setText(serie.getName());

        AutoResizeTextView serieHighScore = (AutoResizeTextView) serieCardView.findViewById(R.id.serie_high_score);
        serieHighScore.setText(getString(R.string.arch_high_score, serie.getHighScore()));

        AutoResizeTextView serieProgress = (AutoResizeTextView) serieCardView.findViewById(R.id.serie_progress);
        serieProgress.setText(getString(R.string.arch_progress, serie.getProgress()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int dp = getResources().getDimensionPixelSize(R.dimen.dp_24_margin);
        int dp_vertical = getResources().getDimensionPixelSize(R.dimen.dp_8_margin);
        params.setMargins(dp, dp_vertical, dp, dp_vertical);
        serieCardView.setLayoutParams(params);
        Animation cardAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        if (isFirstCard || preferences.getBoolean("PREMIUM_ENABLED", false)) {
            serieCardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new SerieDownloader(ArchiveActivity.this, true).execute(serie.getUrl());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SERIE_NAME_PREF", serie.getName());
                    editor.putInt("CURRENT_SERIE_ID_PREF", serie.getId());
                    editor.apply();
                }
            });
        }

        if (!isFirstCard && !preferences.getBoolean("PREMIUM_ENABLED", false)) {
            serieCardView.setBackgroundColor(getResources().getColor(R.color.material_color_grey_200));
            serieCardView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new MaterialDialog.Builder(ArchiveActivity.this)
                            .iconRes(R.mipmap.ic_launcher)
                            .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                            .title("Devenir Premium")
                            .content("Voulez-vous devenir Premium afin de pouvoir jouer avec les anciennes séries et ne plus avoir de publicités?")
                            .positiveText("Oui")
                            .negativeText("Non")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    animateOutTo(IAPActivity.class);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                }
                            })
                            .canceledOnTouchOutside(false)
                            .show();
                }
            });
        }

        cardAnimation.setStartOffset(cardNumber * 100);
        serieCardView.startAnimation(cardAnimation);
        cardNumber++;
        isFirstCard = false;
        scrollView.addView(serieCardView);
    }

    private String readFromRaw(Context ctx) throws Exception {

        Writer writer = new StringWriter();
        char[] buffer = new char[10240];
        InputStream stream;

        try {
            stream = ctx.getApplicationContext().openFileInput("archive.txt");
            Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            Log.d("ArchiveActivity : ", "Cannot read archive from raw");
            return null;
        }

        return writer.toString();
    }

    public void animateOutTo(final Class toActivity) {

        // Init Animations
        headerCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        scrollViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        // Anim Out
        headerCardViewAnimation.setStartOffset(0);
        headerCardView.setVisibility(View.INVISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        scrollViewAnimation.setStartOffset(100);
        scrollView.setVisibility(View.INVISIBLE);
        scrollView.startAnimation(scrollViewAnimation);

        fabAnimation.setStartOffset(200);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(ArchiveActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 400);
    }

    @OnLongClick(R.id.fab)
    public boolean setAdminMode() {
        try {
            String version = String.valueOf(getPackageManager().getPackageInfo(getString(R.string.package_name), 0).versionName);
            if (version.contains("alpha")) {
                archiveTextView.setText("Admin Mode");
                LayoutInflater inflater = LayoutInflater.from(this);

                // Alpha
                CardView alphaCardView = (CardView) inflater.inflate(R.layout.serie_card, null, false);

                AutoResizeTextView alphaName = (AutoResizeTextView) alphaCardView.findViewById(R.id.serie_name);
                alphaName.setText("Alpha");

                AutoResizeTextView alphaHighScore = (AutoResizeTextView) alphaCardView.findViewById(R.id.serie_high_score);
                alphaHighScore.setText(getString(R.string.arch_high_score, -1));

                AutoResizeTextView alphaProgress = (AutoResizeTextView) alphaCardView.findViewById(R.id.serie_progress);
                alphaProgress.setText(getString(R.string.arch_progress, -1));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                int dp = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                int dp_vertical = getResources().getDimensionPixelSize(R.dimen.dp_8_margin);
                params.setMargins(dp, dp_vertical, dp, dp_vertical);
                alphaCardView.setLayoutParams(params);

                alphaCardView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new SerieDownloader(ArchiveActivity.this, true).execute(getString(R.string.server_alpha));
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("SERIE_NAME_PREF", "Alpha");
                        editor.putInt("CURRENT_SERIE_ID_PREF", -1);
                        editor.apply();
                    }
                });

                scrollView.addView(alphaCardView);

                // Premium
                CardView premiumCardView = (CardView) inflater.inflate(R.layout.serie_card, null, false);

                AutoResizeTextView serieName = (AutoResizeTextView) premiumCardView.findViewById(R.id.serie_name);
                serieName.setText("Premium");

                AutoResizeTextView serieHighScore = (AutoResizeTextView) premiumCardView.findViewById(R.id.serie_high_score);
                serieHighScore.setText("Become Premium");

                AutoResizeTextView serieProgress = (AutoResizeTextView) premiumCardView.findViewById(R.id.serie_progress);
                serieProgress.setText("Mon gars!");

                premiumCardView.setLayoutParams(params);

                premiumCardView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("PREMIUM_ENABLED", true);
                        editor.apply();
                        Snackbar.make(findViewById(android.R.id.content), "Premium Enabled", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });

                scrollView.addView(premiumCardView);

                // noAd
                CardView noAdCardView = (CardView) inflater.inflate(R.layout.serie_card, null, false);

                AutoResizeTextView noAdName = (AutoResizeTextView) noAdCardView.findViewById(R.id.serie_name);
                noAdName.setText("No Ads");

                AutoResizeTextView noAdHighScore = (AutoResizeTextView) noAdCardView.findViewById(R.id.serie_high_score);
                noAdHighScore.setText("Buy No Ads");

                AutoResizeTextView noAdProgress = (AutoResizeTextView) noAdCardView.findViewById(R.id.serie_progress);
                noAdProgress.setText("Bastoss!");

                noAdCardView.setLayoutParams(params);

                noAdCardView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("AD_ENABLED", false);
                        editor.apply();
                        Snackbar.make(findViewById(android.R.id.content), "Ad Disabled", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });

                scrollView.addView(noAdCardView);

                // Joker
                CardView jokerCardView = (CardView) inflater.inflate(R.layout.serie_card, null, false);

                AutoResizeTextView jokerName = (AutoResizeTextView) jokerCardView.findViewById(R.id.serie_name);
                jokerName.setText("Jokers");

                AutoResizeTextView jokerHighScore = (AutoResizeTextView) jokerCardView.findViewById(R.id.serie_high_score);
                jokerHighScore.setText("Buy 3 Jokers");

                AutoResizeTextView jokerProgress = (AutoResizeTextView) jokerCardView.findViewById(R.id.serie_progress);
                jokerProgress.setText("Mouhahaha!");

                jokerCardView.setLayoutParams(params);

                jokerCardView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        int joker = preferences.getInt("JOKER_IN_STOCK", 0) + 3;
                        editor.putInt("JOKER_IN_STOCK", joker);
                        editor.apply();
                        Snackbar.make(findViewById(android.R.id.content), "3 Jokers added", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });

                scrollView.addView(jokerCardView);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // NO-OP
        }

        return false;
    }
}
