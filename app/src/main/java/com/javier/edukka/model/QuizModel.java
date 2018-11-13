package com.javier.edukka.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuizModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("options")
    @Expose
    private String options;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("edited")
    @Expose
    private String edited;

    public QuizModel() {}

    public QuizModel(String id, String question, String answer, String options, String type, String gameId, String edited) {
        super();
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.options = options;
        this.type = type;
        this.gameId = gameId;
        this.edited = edited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getEdited() { return edited; }

    public void setEdited(String edited) { this.edited = edited; }

}