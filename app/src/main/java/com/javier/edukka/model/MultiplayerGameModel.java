package com.javier.edukka.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MultiplayerGameModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user1")
    @Expose
    private String user1;
    @SerializedName("id_user1")
    @Expose
    private String id_user1;
    @SerializedName("user2")
    @Expose
    private String user2;
    @SerializedName("id_user2")
    @Expose
    private String id_user2;
    @SerializedName("quizzes")
    @Expose
    private String quizzes;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("class_id")
    @Expose
    private String class_id;
    @SerializedName("extra")
    @Expose
    private String extra;
    @SerializedName("data")
    @Expose
    private String data;

    public MultiplayerGameModel() {}

    public MultiplayerGameModel (String id, String user1, String id_user1, String user2, String id_user2, String quizzes, String status, String class_id, String extra, String data) {
        super();
        this.id = id;
        this.user1 = user1;
        this.id_user1 = id_user1;
        this.user2 = user2;
        this.id_user2 = id_user2;
        this.quizzes = quizzes;
        this.status = status;
        this.class_id = class_id;
        this.extra = extra;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getIdUser1() {
        return id_user1;
    }

    public void setIdUser1(String id_user1) {
        this.id_user1 = id_user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getIdUser2() {
        return id_user2;
    }

    public void setIdUser2(String id_user2) {
        this.id_user2 = id_user2;
    }

    public String getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(String quizzes) {
        this.quizzes = quizzes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassId() {
        return class_id;
    }

    public void setClassId(String class_id) { this.class_id = class_id; }

    public String getExtra() { return extra; }

    public void setExtra(String extra) { this.extra = extra; }

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }

}
