package com.corbel.pierre.jb.downloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.activity.ArchiveActivity;
import com.corbel.pierre.jb.activity.HomeActivity;
import com.corbel.pierre.jb.lib.Helper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.corbel.pierre.jb.lib.Helper.noInternet;

public class ArchiveDownloader extends AsyncTask<String, Void, Boolean> {

    private ProgressDialog mProgressDialog;
    private Activity activity;

    public ArchiveDownloader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Mise à jour des séries disponibles");
        mProgressDialog.setMessage("Chargement...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... URL) {

        String url = URL[0];
        StringBuilder questions = new StringBuilder();
        String line;
        FileOutputStream output;
        boolean hasFailed = false;
        String version;

        // Download questions from URL
        try {
            InputStream input = new java.net.URL(url).openStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));
            while ((line = r.readLine()) != null) {
                questions.append(line).append("\n");
            }
        } catch (Exception e) {
            hasFailed = true;
        }

        // Storing Questions
        try {
            output = activity.openFileOutput("archive.txt", Context.MODE_PRIVATE);
            output.write(questions.toString().getBytes());
            output.close();
        } catch (IOException e) {
            hasFailed = true;
        }

        return hasFailed;
    }

    @Override
    protected void onPostExecute(Boolean hasFailed) {

        mProgressDialog.dismiss();

        if (hasFailed) {
            noInternet(activity);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.archive_success), Snackbar.LENGTH_LONG)
                    .setAction("OK", new SnackListener())
                    .show();
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.putBoolean("IS_ARCHIVE_READY", true);
            mEditor.apply();
            Helper.setBadge(activity, 0);
        }
    }

    @Override
    protected void onCancelled() {
        mProgressDialog.dismiss();
    }

    private class SnackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((HomeActivity) activity).animateOutTo(ArchiveActivity.class);
        }
    }
}