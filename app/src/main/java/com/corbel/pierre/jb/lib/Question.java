package com.corbel.pierre.jb.lib;

public class Question {

    private int id;
    private String question;
    private String answer_1;
    private String answer_2;
    private String answer_3;
    private String answer_4;
    private String goodAnswer;
    private String theme;
    private String url;


    public Question() {
    }

    public Question(String question, String answer_1, String answer_2,
                    String answer_3, String answer_4, String goodAnswer,
                    String theme, String url) {

        this.question = question;
        this.answer_1 = answer_1;
        this.answer_2 = answer_2;
        this.answer_3 = answer_3;
        this.answer_4 = answer_4;
        this.goodAnswer = goodAnswer;
        this.theme = theme;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer_1() {
        return answer_1;
    }

    public void setAnswer_1(String answer_1) {
        this.answer_1 = answer_1;
    }

    public String getAnswer_2() {
        return answer_2;
    }

    public void setAnswer_2(String answer_2) {
        this.answer_2 = answer_2;
    }

    public String getAnswer_3() {
        return answer_3;
    }

    public void setAnswer_3(String answer_3) {
        this.answer_3 = answer_3;
    }

    public String getAnswer_4() {
        return answer_4;
    }

    public void setAnswer_4(String answer_4) {
        this.answer_4 = answer_4;
    }

    public String getGoodAnswer() {
        return goodAnswer;
    }

    public void setGoodAnswer(String goodAnswer) {
        this.goodAnswer = goodAnswer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String toString() {
        return "Question: '" + this.question + "', Answer 1: '" + this.answer_1 + "', Answer 2: '" + this.answer_2 + "'"
                + "', Answer 3: '" + this.answer_3 + "', Answer 4: '" + this.answer_4 + "', Good Answer: '" + this.goodAnswer + "'"
                + "', Theme: '" + this.theme + "', Url: '" + this.url + "'";
    }
}