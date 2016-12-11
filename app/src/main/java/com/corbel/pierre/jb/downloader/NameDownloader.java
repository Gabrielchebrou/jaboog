package com.corbel.pierre.jb.downloader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.view.BeautifulButtonWithImage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.corbel.pierre.jb.lib.Helper.noInternet;

public class NameDownloader extends AsyncTask<String, Void, String> {

    private Activity activity;

    public NameDownloader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... URL) {

        String url = URL[0];
        String line;
        String name = null;

        // Download name from URL
        try {
            InputStream input = new java.net.URL(url).openStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));
            while ((line = r.readLine()) != null) {
                name = line;
            }
        } catch (Exception e) {
            // NO-OP
        }

        return name;
    }

    @Override
    protected void onPostExecute(String name) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        if (name == null) {
            noInternet(activity);
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("SERIE_NAME_PREF", name);
            editor.apply();

            BeautifulButtonWithImage currentTextView = (BeautifulButtonWithImage) activity.findViewById(R.id.current_button);
            currentTextView.setText(activity.getString(R.string.home_serie, preferences.getString("SERIE_NAME_PREF", ""), preferences.getInt("BEST_ACTUAL_PROGRESS", 0)));
        }
    }
}