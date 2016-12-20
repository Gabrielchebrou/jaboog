package com.corbel.pierre.jb.downloader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Base64;

import com.corbel.pierre.jb.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PictureDownloader extends AsyncTask<String, Void, Bitmap> {

    private Activity activity;

    public PictureDownloader(Activity activity) {
        this.activity = activity;
    }

    public Bitmap doInBackground(String... urls) {
        String pictureUrl = urls[0];
        Bitmap result = null;

        try {
            InputStream inputStream = new java.net.URL(pictureUrl).openStream();
            result = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.internet_failed), Snackbar.LENGTH_LONG)
                    .show();
        }

        return result;
    }

    public void onPostExecute(Bitmap result) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (result != null) {
            result.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        } else {
            InputStream is = activity.getResources().openRawResource(R.raw.photo);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String personPicture = Base64.encodeToString(byteArray, Base64.DEFAULT);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("PICTURE_PREF", personPicture);
        mEditor.apply();
    }
}