package com.corbel.pierre.jb.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.corbel.pierre.jb.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DbHelper extends SQLiteOpenHelper {

    private static final int dbVersion = 1;
    private static final String dbName = "jb.db";
    private static final String dbTable = "quiz";
    private static final String dbSerieTable = "serie";
    private static final String id = "id";
    private static final String question = "question";
    private static final String answer_1 = "answer_1";
    private static final String answer_2 = "answer_2";
    private static final String answer_3 = "answer_3";
    private static final String answer_4 = "answer_4";
    private static final String goodAnswer = "goodAnswer";
    private static final String theme = "theme";
    private static final String url = "url";
    private static final String TAG = "com.corbel.pierre.jb";
    private static DbHelper mInstance = null;
    private SQLiteDatabase db;
    private Context context;

    private DbHelper(Context context) {
        super(context, dbName, null, dbVersion);
        this.context = context;
    }

    public static DbHelper getInstance(Context ctx) {
        return mInstance == null ? new DbHelper(ctx.getApplicationContext()) : mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.db = db;

        String createSql = "CREATE TABLE IF NOT EXISTS " + dbTable + " ( "
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + question + " TEXT, "
                + answer_1 + " TEXT, "
                + answer_2 + " TEXT, "
                + answer_3 + " TEXT, "
                + answer_4 + " TEXT, "
                + goodAnswer + " TEXT, "
                + theme + " TEXT, "
                + url + " TEXT)";

        db.execSQL(createSql);

        String createSql2 = "CREATE TABLE IF NOT EXISTS " + dbSerieTable + " ( "
                + "id INTEGER, "
                + "url TEXT, "
                + "name TEXT, "
                + "highScore INTEGER, "
                + "progress INTEGER)";

        db.execSQL(createSql2);

        setAllQuestions();
    }

    public void createSerieIfNotExists(SQLiteDatabase db, String id, String url, String name) {

        this.db = db;
        int highScore = 0;
        int progress = 0;

        String sql = "INSERT INTO "+ dbSerieTable + "(id, url, name, highScore, progress) "
        + "SELECT " + id + ",\"" + url + "\",\"" + name + "\"," + highScore + "," + progress
        + " WHERE NOT EXISTS(SELECT 1 FROM "+ dbSerieTable + " WHERE "
        + "id = " + id + ");";

        db.execSQL(sql);
    }

    public void updateSerie(SQLiteDatabase db, int id, String url, String name, int highScore, int progress) {

        this.db = db;

        String sql = "UPDATE " + dbSerieTable + " SET "
                + "id = " + id + ", "
                + "url = \"" + url + "\", "
                + "name = \"" + name + "\", "
                + "highScore = " + highScore + ", "
                + "progress = " + progress + " "
                + "WHERE id = " + id + ";";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        this.db = db;
        String dropSql = "DROP TABLE IF EXISTS " + dbTable;
        db.execSQL(dropSql);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        this.db = db;
    }

    private void addQuestion(Question questionToAdd) {
        ContentValues values = new ContentValues();

        values.put(question, questionToAdd.getQuestion());
        values.put(answer_1, questionToAdd.getAnswer_1());
        values.put(answer_2, questionToAdd.getAnswer_2());
        values.put(answer_3, questionToAdd.getAnswer_3());
        values.put(answer_4, questionToAdd.getAnswer_4());
        values.put(goodAnswer, questionToAdd.getGoodAnswer());
        values.put(theme, questionToAdd.getTheme());
        values.put(url, questionToAdd.getUrl());

        db.insert(dbTable, null, values);
    }

    private void setAllQuestions() {

        String series = null;

        try {
            series = readFromRaw(context);
        } catch (Exception e) {
            // NO-OP
        }

        if (series != null) {
            String[] questionList = series.split("\n");

            // Shuffle order of questions
            Collections.shuffle(Arrays.asList(questionList));

            // Create list of parsed lines
            List<String[]> list = new ArrayList<String[]>();
            for (String fullQuestion : questionList) {
                String[] elements = fullQuestion.split(";");
                list.add(elements);
            }

            // Add theme as key into HashMap
            Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();
            for (String[] line : list) {
                String key = line[6];
                if (map.get(key) == null) {
                    map.put(key, new ArrayList<String[]>());
                }
                map.get(key).add(line);
            }

            // Split in list by theme
            Set<String> themes = map.keySet();

            // Shuffle order of theme
            List<String> shuffleMe = new ArrayList<>(themes);
            Collections.shuffle(shuffleMe);
            LinkedHashSet<String> shuffledSet = new LinkedHashSet<>();
            shuffledSet.addAll(shuffleMe);

            // Add to final theme by theme
            List<String[]> listFinal = new ArrayList<String[]>();
            for (String theme : shuffledSet) {
                listFinal.addAll(map.get(theme));
            }

            // Add
            for (String[] elements : listFinal) {
                Question q = new Question(
                        elements[0],
                        elements[1],
                        elements[2],
                        elements[3],
                        elements[4],
                        elements[5],
                        elements[6],
                        elements[7]);

                addQuestion(q);
            }
        }
    }

    public Question getQuestion(int questionId) {

        String sql = "SELECT * FROM " + dbTable + " WHERE " + id + "=" + questionId;

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        Question questionToGet = new Question();
        while (cursor.moveToNext()) {
            questionToGet.setId(cursor.getInt(0));
            questionToGet.setQuestion(cursor.getString(1));
            questionToGet.setAnswer_1(cursor.getString(2));
            questionToGet.setAnswer_2(cursor.getString(3));
            questionToGet.setAnswer_3(cursor.getString(4));
            questionToGet.setAnswer_4(cursor.getString(5));
            questionToGet.setGoodAnswer(cursor.getString(6));
            questionToGet.setTheme(cursor.getString(7));
            questionToGet.setUrl(cursor.getString(8));
        }

        cursor.close();

        return questionToGet;
    }

    public Serie getSerie(int serieId) {

        String sql = "SELECT * FROM " + dbSerieTable + " WHERE id =" + serieId;

        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        Serie serieToGet = new Serie();
        while (cursor.moveToNext()) {
            serieToGet.setId(cursor.getInt(0));
            serieToGet.setUrl(cursor.getString(1));
            serieToGet.setName(cursor.getString(2));
            serieToGet.setHighScore(cursor.getInt(3));
            serieToGet.setProgress(cursor.getInt(4));
        }

        cursor.close();

        return serieToGet;
    }

    private String readFromRaw(Context ctx) throws Exception {

        Writer writer = new StringWriter();
        char[] buffer = new char[10240];
        InputStream stream;

        try {
            stream = ctx.getApplicationContext().openFileInput("serie.txt");
            Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            Log.d("DbHelper : ", "Cannot read from raw");
            return null;
        }

        return writer.toString();
    }
}