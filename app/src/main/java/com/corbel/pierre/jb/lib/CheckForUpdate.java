package com.corbel.pierre.jb.lib;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.downloader.ArchiveDownloader;
import com.corbel.pierre.jb.downloader.NameDownloader;
import com.corbel.pierre.jb.downloader.SerieDownloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.corbel.pierre.jb.lib.Helper.noInternet;

public class CheckForUpdate extends AsyncTask<String, Void, String> {

    private Activity activity;

    public CheckForUpdate(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... URL) {

        String url = URL[0];
        String line;
        String version = "0";

        // Download version from URL
        try {
            InputStream input = new java.net.URL(url).openStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));
            while ((line = r.readLine()) != null) {
                version = line;
            }
        } catch (Exception e) {
            // NO-OP
        }

        return version;
    }

    @Override
    protected void onPostExecute(String version) {

        int newVersion = Integer.parseInt(version);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        if (newVersion == 0) {
            noInternet(activity);
        } else {

            int oldVersion = preferences.getInt("VERSION_PREF", 0);

            // New version available
            if (newVersion > oldVersion) {
                SharedPreferences.Editor mEditor = preferences.edit();
                mEditor.putInt("VERSION_PREF", newVersion);
                mEditor.putBoolean("SHOULD_UPDATE", true);
                mEditor.apply();

                new ArchiveDownloader(activity).execute(activity.getResources().getString(R.string.server_archive));
            }
        }
    }
}