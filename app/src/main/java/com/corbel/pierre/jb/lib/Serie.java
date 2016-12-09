package com.corbel.pierre.jb.lib;

public class Serie {

    private int id;
    private String name;
    private String url;
    private int high_score;
    private int progress;

    public Serie() {
    }

    public Serie(int id, String name, String url, int high_score, int progress) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.high_score = high_score;
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHighScore() {
        return high_score;
    }

    public void setHighScore(int high_score) {
        this.high_score = high_score;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}