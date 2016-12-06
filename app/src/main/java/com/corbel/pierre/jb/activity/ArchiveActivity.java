package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.SerieDownloader;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.Helper;
import com.github.clans.fab.FloatingActionButton;

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
    @BindView(R.id.archive_table)
    TableLayout archiveTable;
    @BindView(R.id.archive_text_view)
    AutoResizeTextView archiveTextView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private String archive;
    private SharedPreferences preferences;

    private Animation headerCardViewAnimation;
    private Animation archiveTableAnimation;
    private Animation fabAnimation;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        ButterKnife.bind(this);
        setStatusBarColor(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Init Animations
        headerCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        archiveTableAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        headerCardViewAnimation.setStartOffset(0);
        headerCardView.setVisibility(View.VISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        archiveTableAnimation.setStartOffset(100);
        archiveTable.setVisibility(View.VISIBLE);
        archiveTable.startAnimation(archiveTableAnimation);

        fabAnimation.setStartOffset(200);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);

        try {
            archive = readFromRaw(this);
        } catch (Exception e) {
            // NO-OP
        }

        if (archive != null) {
            String[] archiveList = archive.split("\n");

            for (String archive : archiveList) {
                String[] elements = archive.split(";");
                createRow(elements[1], elements[2]);
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

    public void createRow(final String url, final String name) {
        TableRow row = new TableRow(this);
        TableRow rowSeparator = new TableRow(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View separator = inflater.inflate(R.layout.table_separator, null, false);

        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        TextView artv = new TextView(this);
        artv.setText(name);
        artv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        artv.setPadding(16, 16, 16, 16);
        artv.setMaxLines(1);
        artv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        artv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SerieDownloader(ArchiveActivity.this, true).execute(url);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SERIE_NAME_PREF", name);
                editor.apply();
            }
        });

        row.addView(artv);
        rowSeparator.addView(separator);
        archiveTable.addView(row);
        archiveTable.addView(rowSeparator);
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
        }

        return writer.toString();
    }

    public void animateOutTo(final Class toActivity) {

        // Init Animations
        headerCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        archiveTableAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        // Anim Out
        headerCardViewAnimation.setStartOffset(0);
        headerCardView.setVisibility(View.INVISIBLE);
        headerCardView.startAnimation(headerCardViewAnimation);

        archiveTableAnimation.setStartOffset(100);
        archiveTable.setVisibility(View.INVISIBLE);
        archiveTable.startAnimation(archiveTableAnimation);

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
                createRow(getString(R.string.server_alpha), "Alpha");

                TableRow rowSeparator = new TableRow(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                View separator = inflater.inflate(R.layout.table_separator, null, false);
                rowSeparator.addView(separator);

                TableRow premiumRow = new TableRow(this);
                premiumRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
                TextView premiumTextView = new TextView(this);
                premiumTextView.setText("Become Premium");
                premiumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                premiumTextView.setPadding(16, 16, 16, 16);
                premiumTextView.setMaxLines(1);
                premiumTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                premiumTextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("PREMIUM_ENABLED", true);
                        editor.apply();
                        Snackbar.make(findViewById(android.R.id.content), "Premium Enabled", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                premiumRow.addView(premiumTextView);
                archiveTable.addView(premiumRow);
                archiveTable.addView(rowSeparator);

                TableRow noAdRow = new TableRow(this);
                noAdRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
                TextView noAdTextView = new TextView(this);
                noAdTextView.setText("Buy No Ads");
                noAdTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                noAdTextView.setPadding(16, 16, 16, 16);
                noAdTextView.setMaxLines(1);
                noAdTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                noAdTextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("AD_ENABLED", false);
                        editor.apply();
                        Snackbar.make(findViewById(android.R.id.content), "Ad Disabled", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                noAdRow.addView(noAdTextView);
                archiveTable.addView(noAdRow);

                TableRow joker3Row = new TableRow(this);
                joker3Row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
                TextView joker3TextView = new TextView(this);
                joker3TextView.setText("Buy 3 Jokers");
                joker3TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                joker3TextView.setPadding(16, 16, 16, 16);
                joker3TextView.setMaxLines(1);
                joker3TextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                joker3TextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = preferences.edit();
                        int joker = preferences.getInt("JOKER_IN_STOCK", 0) + 3;
                        editor.putInt("JOKER_IN_STOCK", joker);
                        editor.apply();
                        Snackbar.make(findViewById(android.R.id.content), "3 Jokers added", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                joker3Row.addView(joker3TextView);
                archiveTable.addView(joker3Row);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // NO-OP
        }

        return false;
    }
}
