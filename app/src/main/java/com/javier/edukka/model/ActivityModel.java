package com.javier.edukka.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityModel {

    @SerializedName("student_id")
    @Expose
    private String studentId;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("date")
    @Expose
    private String date;

    public ActivityModel() {}

    public ActivityModel(String studentId, String gameId, String subject, String result, String date) {
        super();
        this.studentId = studentId;
        this.gameId = gameId;
        this.subject = subject;
        this.result = result;
        this.date = date;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}