package com.corbel.pierre.jb.downloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.activity.ArchiveActivity;
import com.corbel.pierre.jb.activity.HomeActivity;
import com.corbel.pierre.jb.lib.DbHelper;
import com.corbel.pierre.jb.lib.Serie;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import me.leolin.shortcutbadger.ShortcutBadger;

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
        StringBuilder archive = new StringBuilder();
        String line;
        FileOutputStream output;
        boolean hasFailed = false;

        // Download questions from URL
        try {
            InputStream input = new java.net.URL(url).openStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));
            while ((line = r.readLine()) != null) {
                archive.append(line).append("\n");
            }
        } catch (Exception e) {
            hasFailed = true;
        }

        // Storing Questions
        try {
            output = activity.openFileOutput("archive.txt", Context.MODE_PRIVATE);
            output.write(archive.toString().getBytes());
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

            forceUpgrade();
        }
    }

    @Override
    protected void onCancelled() {
        mProgressDialog.dismiss();
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

    private void forceUpgrade() {

        String archive = null;

        try {
            archive = readFromRaw(activity);
        } catch (Exception e) {
            // NO-OP
        }

        DbHelper db = DbHelper.getInstance(activity);
        Integer lastIdAvailable = 0;

        if (archive != null) {
            String[] archiveList = archive.split("\n");

            for (String arch : archiveList) {
                String[] elements = arch.split(";");
                db.createSerieIfNotExists(db.getWritableDatabase(), elements[0], elements[1], elements[2]);
                lastIdAvailable = Integer.parseInt(elements[0]) > lastIdAvailable ? Integer.parseInt(elements[0]) : lastIdAvailable;
            }
        }

        final Serie lastSerie = db.getSerie(lastIdAvailable);

        new SerieDownloader(activity, false).execute(lastSerie.getUrl());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("SERIE_NAME_PREF", lastSerie.getName());
        editor.putInt("CURRENT_SERIE_ID_PREF", lastSerie.getId());
        editor.apply();

        ShortcutBadger.removeCount(activity.getApplicationContext());
    }

    private class SnackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((HomeActivity) activity).animateOutTo(ArchiveActivity.class);
        }
    }
}